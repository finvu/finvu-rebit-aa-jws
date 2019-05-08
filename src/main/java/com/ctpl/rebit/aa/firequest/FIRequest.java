package com.ctpl.rebit.aa.firequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class FIRequest {

	String ver;
	String timestamp;
	String txnid;
	
	@JsonProperty("Consent")
	Consent consent;
	
	@JsonProperty("FIDataRange")
	FIDataRange fiDataRange;
	
	@JsonProperty("KeyMaterials")
	KeyMaterials keyMaterials;

	/**
	 * @return the ver
	 */
	public String getVer() {
		return ver;
	}

	/**
	 * @param ver the ver to set
	 */
	public void setVer(String ver) {
		this.ver = ver;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the txnid
	 */
	public String getTxnid() {
		return txnid;
	}

	/**
	 * @param txnid the txnid to set
	 */
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	/**
	 * @return the consent
	 */
	public Consent getConsent() {
		return consent;
	}

	/**
	 * @param consent the consent to set
	 */
	public void setConsent(Consent consent) {
		this.consent = consent;
	}

	/**
	 * @return the fiDataRange
	 */
	public FIDataRange getFiDataRange() {
		return fiDataRange;
	}

	/**
	 * @param fiDataRange the fiDataRange to set
	 */
	public void setFiDataRange(FIDataRange fiDataRange) {
		this.fiDataRange = fiDataRange;
	}

	/**
	 * @return the keyMaterials
	 */
	public KeyMaterials getKeyMaterials() {
		return keyMaterials;
	}

	/**
	 * @param keyMaterials the keyMaterials to set
	 */
	public void setKeyMaterials(KeyMaterials keyMaterials) {
		this.keyMaterials = keyMaterials;
	}
}