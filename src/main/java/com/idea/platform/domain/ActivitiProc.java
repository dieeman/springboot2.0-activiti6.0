package com.idea.platform.domain;

import java.util.Date;
/**
 * 流程单
 * @author idea
 *
 */
public class ActivitiProc {
	/**
	 * 请假申请信息
	 */
	//请假人
	private String applyUserId;
	private String applyUserName;
	//请假理由
	private String reason;
	//申请日期
	private Date applyTime;
	//请假天数
	private int days; 
	//状态
	private String applyStatus;
	
	/**
	 * 审核信息
	 */
	//审核人
	private String auditId;
	//审核时间
	private Date auditTime;
	//审核结果
	private String result;
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getApplyStatus() {
		return applyStatus;
	}
	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}
	
}
