package com.ctpl.rebit.aa.consent;

public class ConsentUse {
	private String logUri;
	private float count;
	private String lastUseDateTime;

	// Getter Methods

	public String getLogUri() {
		return logUri;
	}

	public float getCount() {
		return count;
	}

	public String getLastUseDateTime() {
		return lastUseDateTime;
	}

	// Setter Methods

	public void setLogUri(String logUri) {
		this.logUri = logUri;
	}

	public void setCount(float count) {
		this.count = count;
	}

	public void setLastUseDateTime(String lastUseDateTime) {
		this.lastUseDateTime = lastUseDateTime;
	}
}