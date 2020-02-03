package com.ctpl.rebit.aa.consent;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "ver", "txnid", "consentId", "status", "createTimestamp", "consentDetail", "consentDetailDigitalSignature", "ConsentUse" })
public class ConsentResponse {
	private String ver;
	private String txnid;
	private String consentId;
	private String status;
	private String createTimestamp;
	private ConsentJWS consent;
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

	public void setConsentUse(ConsentUse ConsentUseObject) {
		this.ConsentUseObject = ConsentUseObject;
	}

	/**
	 * @return the consent
	 */
	public ConsentJWS getConsent() {
		return consent;
	}

	/**
	 * @param consent the consent to set
	 */
	public void setConsent(ConsentJWS consent) {
		this.consent = consent;
	}
}
