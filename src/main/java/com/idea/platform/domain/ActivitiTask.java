package com.idea.platform.domain;
/**
 * 任务表
 * @author idea
 *
 */

import java.util.Date;

public class ActivitiTask {
	private String id;
	private String taskName;
	private String taskResult;
	private Date   createtime;
	private Date   endTime;
	private ActivitiProc activitiProc;
	public ActivitiProc getActivitiProc() {
		return activitiProc;
	}
	public void setActivitiProc(ActivitiProc activitiProc) {
		this.activitiProc = activitiProc;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	private String result;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskResult() {
		return taskResult;
	}
	public void setTaskResult(String taskResult) {
		this.taskResult = taskResult;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	

}
