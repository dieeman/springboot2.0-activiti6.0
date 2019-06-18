package com.idea.platform.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.idea.platform.domain.ActivitiProc;
import com.idea.platform.domain.ActivitiTask;
/**
 * 请假流程
 */
@Service
public class LeaveService {
	@Resource
    private RuntimeService runtimeService;
    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ManagementService managementService;
    //工作流类型，和流程图的id相对应
    private static final String PROCESS_DEFINE_KEY = "vacationProcess";
    //启动一个请假流程
    public void startWork(String userName,ActivitiProc task) {
    	identityService.setAuthenticatedUserId(userName);
    	//初始化一个流程，这个key为工作流程图的id
    	ProcessInstance vacationInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINE_KEY);
    	//初始化任务
    	Task currentTask = taskService.createTaskQuery().processInstanceId(vacationInstance.getId()).singleResult();
    	//Task currentTask = taskService.createTaskQuery().taskAssignee("lyj").singleResult();
    	// 指定任务受理人
        taskService.claim(currentTask.getId(), userName);
        Map<String, Object> vars = new HashMap<>(10);
        vars.put("applyUser", userName);
        vars.put("days", task.getDays());
        vars.put("reason", task.getReason());
        vars.put("bmjl", "zs");
        vars.put("zjl", "myb");
        //完成任务
        taskService.complete(currentTask.getId(), vars);
        System.out.println("流程和任务初始化成功");
    }
    /**
     * 查询我的申请
     * @param userName
     * @return
     */
    public Object myVac(String userName) {
    	List<Task> task = taskService.createTaskQuery().taskAssignee(userName)
                .orderByTaskCreateTime().desc().list();
        List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery().startedBy(userName).list();
        List<ActivitiProc> taskList = new ArrayList<>();
        for (ProcessInstance instance : instanceList) {
        	ActivitiProc vac = getVac(instance);
        	taskList.add(vac);
        }
        return taskList;
    }
    /**
     * 查询已完成的历史记录
     */
    public Object myHisPro(String userName){
    	List<HistoricProcessInstance> hsHistoricProcessInstances = historyService.createHistoricProcessInstanceQuery()
    			.startedBy(userName).orderByProcessInstanceStartTime().desc().finished().list();
        return hsHistoricProcessInstances;
    }
    /**
     * 查询申请的详细信息
     * @param instance
     * @return
     */
    private ActivitiProc getVac(ProcessInstance instance) {
        Integer days = runtimeService.getVariable(instance.getId(), "days", Integer.class);
        String reason = runtimeService.getVariable(instance.getId(), "reason", String.class);
        ActivitiProc pro = new ActivitiProc();
        pro.setApplyUserName(instance.getStartUserId());
        pro.setDays(days);
        pro.setReason(reason);
        Date startTime = instance.getStartTime();
        pro.setApplyTime(startTime);      
        pro.setResult(instance.isEnded() ? "申请结束" : "等待审批");       
        return pro;
    }
    /**
     * 查询我的任务
     * @param userName
     * @return
     */
    public List<ActivitiTask> myAudit(String assignee) {
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(assignee)
                .orderByTaskCreateTime().desc().list();
        List<ActivitiTask> activitiTaskList = new ArrayList<>();
        for (Task task : taskList) {
        	ActivitiTask activitiTask = new ActivitiTask();
        	activitiTask.setId(task.getId());
        	activitiTask.setTaskName(task.getName());
        	activitiTask.setCreatetime(task.getCreateTime());
            String instanceId = task.getProcessInstanceId();
            ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            ActivitiProc activitiProc = getVac(instance);
            activitiTask.setActivitiProc(activitiProc);
            activitiTaskList.add(activitiTask);
        }
        return activitiTaskList;
    }
    /**
     * 通过审核
     * @param userName
     * @param activitiTask
     * @return
     */
    public Object passAudit(String userName, String taskId) {
    	Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //String taskId = activitiTask.getId();
       // String result = activitiTask.getVac().getResult();
        Map<String, Object> vars = new HashMap<>();
        //vars.put("result", result);
        vars.put("auditor", userName);
        vars.put("auditTime", new Date());
        taskService.claim(taskId, userName);
        taskService.complete(taskId, vars);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        		.processInstanceId(task.getProcessInstanceId())
        		.singleResult();
        if(processInstance.isEnded()) {
        	//设置为已结束
        	
        }
        return "success";
    }
    /**
     * 驳回请求
     * @param userName currentUser
     * @param taskId
     * @param returnStart    return to start or lasttask
     * @return
     */
    public Object refuseAudit(String userName,String taskId,Boolean returnStart) {
    	//获取当前任务
    	Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    	//获取流程定义
    	BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
    	//获取历史操作流程
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
        		.processInstanceId(task.getProcessInstanceId()).activityType("userTask")
        		.orderByHistoricActivityInstanceEndTime()
        		.finished()
        		.desc()
        		.list();
    	if(list==null || list.size()==0) {
    		throw new ActivitiException("操作历史流程不存在");
    	}
    	//找到上一活动
    	String lastActivityId = list.get(0).getActivityId();
    	String lastAssigne = list.get(0).getAssignee();
		//上一活动所在节点
		FlowNode lastFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(lastActivityId);
		
		
		//获得当前任务执行流程
		Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
		String activityId = execution.getActivityId();
		FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
		//记录原活动方向
		List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
		oriSequenceFlows.addAll(myFlowNode.getOutgoingFlows());
		
		//建立新方向
		List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
		SequenceFlow newSequenceFlow = new SequenceFlow();
		newSequenceFlow.setId("newSequenceFlowId");
		newSequenceFlow.setSourceFlowElement(myFlowNode);
		newSequenceFlow.setTargetFlowElement(lastFlowNode);
		newSequenceFlowList.add(newSequenceFlow);
		myFlowNode.setOutgoingFlows(newSequenceFlowList);
		
		Authentication.setAuthenticatedUserId("myb");
		taskService.addComment(task.getId(), task.getProcessInstanceId(), "驳回");
		Map<String,Object> currentVariables = new HashMap<String,Object>();
		currentVariables.put("applier", "myb");
		//完成任务
		task.setAssignee(lastAssigne);
		taskService.complete(task.getId(),currentVariables);
		//恢复原方向
		myFlowNode.setOutgoingFlows(oriSequenceFlows);
    	//在当前任务下建立一个节点为目标节点的新任务
    	//然后完成任务
    	//ExecutionEntity executionEntity = task.getExecutionId();
    	//删除当前运行任务
    	//DeleteTaskCmd eCmd = new DeleteTaskCmd(task.getId());
    	//eCmd.reason = "测试";
    	//删除当前任务，返回当前任务的来源任务id
        //String executionEntityId = managementService.executeCommand(eCmd);
        //Task task2 = taskService.createTaskQuery().taskId(executionEntityId).singleResult();
        //将流程执行到来源节点
        //managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    	return "{status:success}";
    }
    /**
     * 查询流程的第一个节点
     * @param processDefinitionId
     * @return
     */
    private FlowNode getFirstActivity(String processDefinitionId) {
    	Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
		FlowElement flowElement = process.getInitialFlowElement();
		FlowNode startActivity = (FlowNode) flowElement;
        if (startActivity.getOutgoingFlows().size() != 1) {
            throw new IllegalStateException(
                    "start activity outgoing transitions cannot more than 1, now is : "
                            + startActivity.getOutgoingFlows().size());
        }
 
        SequenceFlow sequenceFlow = startActivity.getOutgoingFlows()
                .get(0);
        FlowNode targetActivity = (FlowNode) sequenceFlow.getTargetFlowElement();
 
        if (!(targetActivity instanceof UserTask)) {
 
            return null;
        }
 
        return targetActivity;
    }
    
}
