package com.ctpl.rebit.aa.consent;

public class DataFilter {
	private String type;
	private String operator;
	private float value;

	// Getter Methods

	public String getType() {
		return type;
	}

	public String getOperator() {
		return operator;
	}

	public float getValue() {
		return value;
	}

	// Setter Methods

	public void setType(String type) {
		this.type = type;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
