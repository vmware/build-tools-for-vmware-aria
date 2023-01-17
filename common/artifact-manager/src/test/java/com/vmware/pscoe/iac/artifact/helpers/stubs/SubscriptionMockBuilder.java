package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSubscription;

import org.apache.commons.io.IOUtils;

public class SubscriptionMockBuilder {
	private JsonElement mockData;
	private String		id;
	private	String		name;

	public SubscriptionMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString( cl.getResourceAsStream("test/fixtures/subscription.json"), StandardCharsets.UTF_8 );;
			this.mockData = JsonParser.parseString(read);
		}
		catch (IOException ex) {
			throw ex;
		}
	}

	public SubscriptionMockBuilder setName(String name){
		this.name = name;
		return this;
	}

	public SubscriptionMockBuilder setId(String id){
		this.id = id;
		return this;
	}

	public SubscriptionMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject subscription = this.mockData.getAsJsonObject();
		if(subscription.has(key)) {
			subscription.remove(key);
			subscription.addProperty(key, value);
		}
		this.mockData = subscription.getAsJsonObject();
		return this;
	}

	public VraNgSubscription build() {
		JsonObject subscription = this.mockData.getAsJsonObject();
		
		return new VraNgSubscription(this.id, this.name, subscription.toString());
	}
}
