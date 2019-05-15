package com.ctpl.rebit.aa.consent;

public class Purpose {
	private String code;
	private String refUri;
	private String text;
	Category CategoryObject;

	// Getter Methods

	public String getCode() {
		return code;
	}

	public String getRefUri() {
		return refUri;
	}

	public String getText() {
		return text;
	}

	public Category getCategory() {
		return CategoryObject;
	}

	// Setter Methods

	public void setCode(String code) {
		this.code = code;
	}

	public void setRefUri(String refUri) {
		this.refUri = refUri;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCategory(Category CategoryObject) {
		this.CategoryObject = CategoryObject;
	}
}