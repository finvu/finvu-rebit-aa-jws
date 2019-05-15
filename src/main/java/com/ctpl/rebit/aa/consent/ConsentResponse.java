package com.ctpl.rebit.aa.consent;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "ver", "txnid", "consentId", "status", "createTimestamp", "consentDetail", "consentDetailDigitalSignature", "ConsentUse" })
public class ConsentResponse {
	private String ver;
	private String txnid;
	private String consentId;
	private String status;
	private String createTimestamp;
	private ConsentDetail ConsentDetailObject;
	private String consentDetailDigitalSignature;
	private ConsentUse ConsentUseObject;

	// Getter Methods

	public String getVer() {
		return ver;
	}

	public String getTxnid() {
		return txnid;
	}

	public String getConsentId() {
		return consentId;
	}

	public String getStatus() {
		return status;
	}

	public String getCreateTimestamp() {
		return createTimestamp;
	}

	public ConsentDetail getConsentDetail() {
		return ConsentDetailObject;
	}

	public String getConsentDetailDigitalSignature() {
		return consentDetailDigitalSignature;
	}

	public ConsentUse getConsentUse() {
		return ConsentUseObject;
	}

	// Setter Methods

	public void setVer(String ver) {
		this.ver = ver;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreateTimestamp(String createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public void setConsentDetail(ConsentDetail ConsentDetailObject) {
		this.ConsentDetailObject = ConsentDetailObject;
	}

	public void setConsentDetailDigitalSignature(String consentDetailDigitalSignature) {
		this.consentDetailDigitalSignature = consentDetailDigitalSignature;
	}

	public void setConsentUse(ConsentUse ConsentUseObject) {
		this.ConsentUseObject = ConsentUseObject;
	}
}
