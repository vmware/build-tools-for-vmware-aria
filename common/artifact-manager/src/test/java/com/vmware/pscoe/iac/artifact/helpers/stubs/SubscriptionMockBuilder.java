package com.vmware.pscoe.iac.artifact.helpers.stubs;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSubscription;

import org.apache.commons.io.IOUtils;

public class SubscriptionMockBuilder {
	
	/**
	 * Subscription mock.
	 */
	private JsonElement mockData;

	/**
	 * Subscription identity.
	 */
	private String id;

	/**
	 * Subscription name.
	 */
	private	String name;

	/**
	 * SubscriptionMockBuilder constructor.
	 * @throws IOException
	 */
	public SubscriptionMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString(cl.getResourceAsStream("test/fixtures/subscription.json"),
					StandardCharsets.UTF_8);			
			this.mockData = JsonParser.parseString(read);
		} catch (IOException ex) {
			throw ex;
		}
	}

	/**
	 * Set subscription Name.
	 * @param value new name.
	 * @return subscription with updated name.
	 */
	public SubscriptionMockBuilder setName(String value) {
		this.name = value;
		return this;
	}

	/**
	 * Set subscription Identity.
	 * @param value new id.
	 * @return subscription with updated identity.
	 */
	public SubscriptionMockBuilder setId(String value) {
		this.id = value;
		return this;
	}
	/**
	 * Set property value.
	 * @param key
	 * @param value
	 * @return subscription with updated (created) property.
	 */
	public SubscriptionMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject subscription = this.mockData.getAsJsonObject();
		if (subscription.has(key)) {
			subscription.remove(key);
		}
		subscription.addProperty(key, value);
		this.mockData = subscription.getAsJsonObject();
		return this;
	}

	/**
	 * Build subscription based on mock.
	 * @return new subscription based on mock.
	 */
	public VraNgSubscription build() {
		JsonObject subscription = this.mockData.getAsJsonObject();

		return new VraNgSubscription(this.id, this.name, subscription.toString());
	}
}
