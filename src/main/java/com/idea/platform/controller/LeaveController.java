package com.idea.platform.controller;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idea.platform.domain.ActivitiProc;
import com.idea.platform.domain.ActivitiTask;
import com.idea.platform.service.GroupVacationService;
import com.idea.platform.service.LeaveService;

/**
 * 第一个请假流程
 * @author idea
 *
 */
@RestController
public class LeaveController {
	@Autowired
	private LeaveService activitiService;
	@Autowired
	private GroupVacationService groupVacationService;
	/**
	 * 创建流程
	 * 单人审批
	 */
	@RequestMapping("myb")
	public void startAct(HttpSession session) {
		//获取申请人
		//String userName = (String) session.getAttribute("username");
		String userName = "fwh";
		//生成请假单
		ActivitiProc activitiTask = new ActivitiProc();
		activitiTask.setApplyUserName(userName);
		activitiTask.setDays(5);
		activitiTask.setReason("生病");
		//创建工作流
		activitiService.startWork(userName, activitiTask);		
	}
	@RequestMapping("myhis")
	public Object getMyHis() {
		return activitiService.myHisPro("fwh");
	}
	
	@RequestMapping("myapply")
	public Object listMyAooly(){
		return activitiService.myVac("fwh");
	}
	@RequestMapping("zsaudit")
	public List<ActivitiTask> listZSTask(){
		return activitiService.myAudit("zs");
	}
	@RequestMapping("mybaudit")
	public List<ActivitiTask> listMYBTask(){
		return activitiService.myAudit("myb");
	}
	@RequestMapping("passaudit")
	public void passAudit(){
		activitiService.passAudit("lyj", "16");
	}
	@RequestMapping("zsrefuseaudit")
	public void zsrefuseAudit(){
		activitiService.refuseAudit("zs", "18", true);
	}
	@RequestMapping("mybrefuseaudit")
	public void mybrefuseAudit(){
		activitiService.refuseAudit("myb", "7519", true);
	}
	
	/**
	 * 创建分组审批流程
	 * @param session
	 */
	@RequestMapping("lff")
	public void startAct2(HttpSession session) {
		//获取申请人
		//String userName = (String) session.getAttribute("username");
		String userName = "lff";
		//生成请假单
		ActivitiProc activitiTask = new ActivitiProc();
		activitiTask.setApplyUserName(userName);
		activitiTask.setDays(3);
		activitiTask.setReason("dfsadfsad");
		//创建工作流
		groupVacationService.startWork(userName, activitiTask);		
	}
	/**
	 * 查询组任务
	 * @return
	 */
	@RequestMapping("mygroup")
	public Object getMyGroup() {
		return groupVacationService.myGroupTask("lyj");
	}
	/**
	 * 认领任务
	 */
	@RequestMapping("sign")
	public void signMyTask() {
		groupVacationService.signMyTask("fwh", "2506");
	}
	/**
	 * 放弃认领的任务
	 */
	@RequestMapping("unsign")
	public void unSignMyTask() {
		groupVacationService.unSignTask("2506");
	}
	/**
	 * 查看我的任务
	 * @return
	 */
	@RequestMapping("mytask")
	public Object getMyTasks(){
		return groupVacationService.getMyTask("fwh");
	}
}
