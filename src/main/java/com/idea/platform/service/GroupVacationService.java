package com.idea.platform.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.idea.platform.domain.ActivitiProc;

/**
 * 分组任务
 * @author idea
 *
 */
@Service
public class GroupVacationService {
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
    private static final String PROCESS_DEFINE_KEY = "vacationGroupProcess";
  //启动一个请假流程
    public void startWork(String userName,ActivitiProc task) {
    	identityService.setAuthenticatedUserId(userName);
    	//初始化一个流程，这个key为工作流程图的id
    	ProcessInstance vacationInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINE_KEY);
    	//初始化任务
    	Task currentTask = taskService.createTaskQuery().processInstanceId(vacationInstance.getId()).singleResult();
    	//Task currentTask = taskService.createTaskQuery().taskAssignee("lyj").singleResult();
    	// 任务信息
        //taskService.claim(currentTask.getId(), userName);
        //创建任务组
        Group group = identityService.newGroup("tec");
        group.setName("技术部");
        identityService.saveGroup(group);
        User user1 = identityService.newUser("fwh");
        User user2 = identityService.newUser("lyj");
        identityService.saveUser(user1);
        identityService.saveUser(user2);
        identityService.createMembership("fwh", "tec");
        identityService.createMembership("lyj", "tec");
        Map<String, Object> vars = new HashMap<>(10);
        vars.put("applyUser", userName);
        vars.put("days", task.getDays());
        vars.put("reason", task.getReason());
        vars.put("bmjl", "tec");
        vars.put("zjl", "myb");
        //taskService.complete(currentTask.getId(), vars);
        System.out.println("流程和任务初始化成功");
    }
    /**
     * 查询待认领组任务
     * @param userId
     * @return
     */
    public List<Task> myGroupTask(String userId){
    	List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(userId).orderByTaskCreateTime().asc().list();
    	return taskList;
    }
    /**
     * 个人认领任务
     * @param userId
     * @param taskId
     */
    public void signMyTask(String userId,String taskId) {
    	taskService.claim(taskId, userId);    	
    }
    /**
     * 取消认领
     * @param userId
     * @param taskId
     */
    public void unSignTask(String taskId) {
    	taskService.setAssignee(taskId, null);
    }
    /**
     * 我的任务
     * @param userId
     * @return
     */
    public Object getMyTask(String userId) {
    	List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().asc().list();
    	return taskList;
    }

}
