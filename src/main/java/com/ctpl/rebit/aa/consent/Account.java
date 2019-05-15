package com.ctpl.rebit.aa.consent;

public class Account {
	private String fiType;
	private String fipId;
	private String accType;
	private String linkRefNumber;
	private String maskedAccNumber;

	// Getter Methods

	public String getFiType() {
		return fiType;
	}

	public String getFipId() {
		return fipId;
	}

	public String getAccType() {
		return accType;
	}

	public String getLinkRefNumber() {
		return linkRefNumber;
	}

	public String getMaskedAccNumber() {
		return maskedAccNumber;
	}

	// Setter Methods

	public void setFiType(String fiType) {
		this.fiType = fiType;
	}

	public void setFipId(String fipId) {
		this.fipId = fipId;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public void setLinkRefNumber(String linkRefNumber) {
		this.linkRefNumber = linkRefNumber;
	}

	public void setMaskedAccNumber(String maskedAccNumber) {
		this.maskedAccNumber = maskedAccNumber;
	}
}