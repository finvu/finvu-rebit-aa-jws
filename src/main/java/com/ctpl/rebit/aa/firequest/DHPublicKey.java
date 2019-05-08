package com.ctpl.rebit.aa.firequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DHPublicKey {

	String expiry;
	
	@JsonProperty("Parameters")
	String Parameters;
	
	@JsonProperty("KeyValue")
	String KeyValue;
	
	public String getExpiry() {
		return expiry;
	}
	
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	
	public String getParameters() {
		return Parameters;
	}
	
	public void setParameters(String parameters) {
		Parameters = parameters;
	}
	
	public String getKeyValue() {
		return KeyValue;
	}
	
	public void setKeyValue(String keyValue) {
		KeyValue = keyValue;
	}
}
