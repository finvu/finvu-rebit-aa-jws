package com.ctpl.rebit.aa.firequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class KeyMaterials {

	String cryptoAlg;
	String curve;
	String params;
	
	@JsonProperty("DHPublicKey")
	DHPublicKey dhPublicKey;
	
	@JsonProperty("Nonce")
	String Nonce;
	
	@JsonProperty("Signature")
	String signature;

	public String getCryptoAlg() {
		return cryptoAlg;
	}

	public void setCryptoAlg(String cryptoAlg) {
		this.cryptoAlg = cryptoAlg;
	}

	public String getCurve() {
		return curve;
	}

	public void setCurve(String curve) {
		this.curve = curve;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public DHPublicKey getDhPublicKey() {
		return dhPublicKey;
	}

	public void setDhPublicKey(DHPublicKey dhPublicKey) {
		this.dhPublicKey = dhPublicKey;
	}

	public String getNonce() {
		return Nonce;
	}

	public void setNonce(String nonce) {
		Nonce = nonce;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}
