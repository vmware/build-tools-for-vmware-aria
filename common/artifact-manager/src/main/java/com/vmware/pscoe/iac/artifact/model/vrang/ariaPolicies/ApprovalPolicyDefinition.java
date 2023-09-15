package com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies;

import java.util.List;

public class ApprovalPolicyDefinition {
	public ApprovalPolicyDefinition() {

	}

	private int level;

	private List<String> actions;

	private List<String> approvers;

	private String approvalMode;

	private String approverType;

	private int autoApprovalExpiry;

	private String autoApprovalDecision;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public List<String> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<String> approvers) {
		this.approvers = approvers;
	}

	public String getApprovalMode() {
		return approvalMode;
	}

	public void setApprovalMode(String approvalMode) {
		this.approvalMode = approvalMode;
	}

	public String getApproverType() {
		return approverType;
	}

	public void setApproverType(String approverType) {
		this.approverType = approverType;
	}

	public int getAutoApprovalExpiry() {
		return autoApprovalExpiry;
	}

	public void setAutoApprovalExpiry(int autoApprovalExpiry) {
		this.autoApprovalExpiry = autoApprovalExpiry;
	}

	public String getAutoApprovalDecision() {
		return autoApprovalDecision;
	}

	public void setAutoApprovalDecision(String autoApprovalDecision) {
		this.autoApprovalDecision = autoApprovalDecision;
	}
}
