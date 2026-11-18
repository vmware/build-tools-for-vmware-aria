/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.aria.operations.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Status of the last content management operation, as returned by
 * {@code GET /suite-api/api/content/operations/import}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentOperationDTO implements Serializable {
	private static final long serialVersionUID = 2961578843306284547L;

	@JsonProperty("state")
	private String state;

	@JsonProperty("startTime")
	private Long startTime;

	@JsonProperty("errorCode")
	private String errorCode;

	@JsonProperty("errorMessages")
	private List<String> errorMessages = new ArrayList<>();

	@JsonProperty("operationSummaries")
	private List<OperationSummary> operationSummaries = new ArrayList<>();

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public List<OperationSummary> getOperationSummaries() {
		return operationSummaries;
	}

	public void setOperationSummaries(List<OperationSummary> operationSummaries) {
		this.operationSummaries = operationSummaries;
	}

	/**
	 * Result of the operation for a single content type.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class OperationSummary implements Serializable {
		private static final long serialVersionUID = -1726488380226574339L;

		@JsonProperty("contentType")
		private String contentType;

		@JsonProperty("state")
		private String state;

		@JsonProperty("imported")
		private int imported;

		@JsonProperty("skipped")
		private int skipped;

		@JsonProperty("failed")
		private int failed;

		@JsonProperty("total")
		private int total;

		@JsonProperty("errorMessages")
		private List<String> errorMessages = new ArrayList<>();

		@JsonProperty("infoMessages")
		private List<String> infoMessages = new ArrayList<>();

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public int getImported() {
			return imported;
		}

		public void setImported(int imported) {
			this.imported = imported;
		}

		public int getSkipped() {
			return skipped;
		}

		public void setSkipped(int skipped) {
			this.skipped = skipped;
		}

		public int getFailed() {
			return failed;
		}

		public void setFailed(int failed) {
			this.failed = failed;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public List<String> getErrorMessages() {
			return errorMessages;
		}

		public void setErrorMessages(List<String> errorMessages) {
			this.errorMessages = errorMessages;
		}

		public List<String> getInfoMessages() {
			return infoMessages;
		}

		public void setInfoMessages(List<String> infoMessages) {
			this.infoMessages = infoMessages;
		}
	}
}
