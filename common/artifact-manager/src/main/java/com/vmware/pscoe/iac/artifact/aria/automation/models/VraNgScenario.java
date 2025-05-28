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
package com.vmware.pscoe.iac.artifact.aria.automation.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a VRA NG Scenario.
 */
public class VraNgScenario implements Identifiable {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;
	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;

	/**
	 * @param enabled The enabled flag of the scenario.
	 */
	private final Boolean enabled;

	/**
	 * @param scenarioCategory The category of the scenario.
	 */
	private final String scenarioCategory;

	/**
	 * @param scenarioName The display name of the scenario.
	 */
	private final String scenarioName;

	/**
	 * @param scenarioId The ID of the scenario.
	 */
	private final String scenarioId;

	/**
	 * @param subject The email subject of the scenario.
	 */
	private final String subject;

	/**
	 * @param body The email body of the scenario.
	 */
	private final String body;

	/**
	 * @param enabled The enabled flag of the scenario.
	 * @param scenarioCategory The category of the scenario.
	 * @param scenarioName The display name of the scenario.
	 * @param scenarioId The ID of the scenario.
	 * @param subject The email subject of the scenario.
	 * @param body The email body of the scenario.
	 */
	public VraNgScenario(Boolean enabled, String scenarioCategory, String scenarioName, String scenarioId, String subject, String body) {
		this.enabled = enabled;
		this.scenarioCategory = scenarioCategory;
		this.scenarioName = scenarioName;
		this.scenarioId = scenarioId;
		this.subject = subject;
		this.body = body;
	}

	/**
	 * @return The ID of the scenario.
	 */
	public String getId() {
		return this.getScenarioId();
	}

	/**
	 * @return The name of the scenario.
	 */
	public String getName() {
		return this.getScenarioName();
	}

	/**
	 * @return The enabled flag of the scenario.
	 */
	public Boolean getEnabled() {
		return this.enabled;
	}

	/**
	 * @return The category of the scenario.
	 */
	public String getScenarioCategory() {
		return this.scenarioCategory;
	}

	/**
	 * @return The name of the scenario.
	 */
	public String getScenarioName() {
		return this.scenarioName;
	}

	/**
	 * @return The ID of the scenario
	 */
	public String getScenarioId() {
		return this.scenarioId;
	}

	/**
	 * @return The email subject of the scenario.
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @return The email body of the scenario.
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * Checks if the scenario is equal to another object.
	 *
	 * @param obj The object to compare.
	 * @return True if the scenario is equal to the object, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgScenario other = (VraNgScenario) obj;
		return this.scenarioId.equals(other.getScenarioId());
	}

	/**
	 * @return the hashCode representation of the object
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
				.append(enabled)
				.append(scenarioCategory)
				.append(scenarioName)
				.append(scenarioId)
				.append(subject)
				.append(body)
				.toHashCode();
	}

}
