package com.ctpl.rebit.aa.consent;

import java.util.ArrayList;

public class ConsentDetail {
	private String consentStart;
	private String consentExpiry;
	private String consentMode;
	private String fetchType;
	ArrayList<Object> consentTypes = new ArrayList<Object>();
	ArrayList<Object> fiTypes = new ArrayList<Object>();
	DataConsumer DataConsumerObject;
	DataProvider DataProviderObject;
	Customer CustomerObject;
	ArrayList<Account> Accounts = new ArrayList<>();
	Purpose PurposeObject;
	FIDataRange FIDataRangeObject;
	DataLife DataLifeObject;
	Frequency FrequencyObject;
	ArrayList<DataFilter> DataFilter = new ArrayList<>();

	// Getter Methods

	public String getConsentStart() {
		return consentStart;
	}

	public String getConsentExpiry() {
		return consentExpiry;
	}

	public String getConsentMode() {
		return consentMode;
	}

	public String getFetchType() {
		return fetchType;
	}

	public DataConsumer getDataConsumer() {
		return DataConsumerObject;
	}

	public DataProvider getDataProvider() {
		return DataProviderObject;
	}

	public Customer getCustomer() {
		return CustomerObject;
	}

	public Purpose getPurpose() {
		return PurposeObject;
	}

	public FIDataRange getFIDataRange() {
		return FIDataRangeObject;
	}

	public DataLife getDataLife() {
		return DataLifeObject;
	}

	public Frequency getFrequency() {
		return FrequencyObject;
	}

	// Setter Methods

	public void setConsentStart(String consentStart) {
		this.consentStart = consentStart;
	}

	public void setConsentExpiry(String consentExpiry) {
		this.consentExpiry = consentExpiry;
	}

	public void setConsentMode(String consentMode) {
		this.consentMode = consentMode;
	}

	public void setFetchType(String fetchType) {
		this.fetchType = fetchType;
	}

	public void setDataConsumer(DataConsumer DataConsumerObject) {
		this.DataConsumerObject = DataConsumerObject;
	}

	public void setDataProvider(DataProvider DataProviderObject) {
		this.DataProviderObject = DataProviderObject;
	}

	public void setCustomer(Customer CustomerObject) {
		this.CustomerObject = CustomerObject;
	}

	public void setPurpose(Purpose PurposeObject) {
		this.PurposeObject = PurposeObject;
	}

	public void setFIDataRange(FIDataRange FIDataRangeObject) {
		this.FIDataRangeObject = FIDataRangeObject;
	}

	public void setDataLife(DataLife DataLifeObject) {
		this.DataLifeObject = DataLifeObject;
	}

	public void setFrequency(Frequency FrequencyObject) {
		this.FrequencyObject = FrequencyObject;
	}
	
	public void addConsentType(String consentType) {
		consentTypes.add(consentType);
	}
	
	public void addFitype(String fiType) {
		fiTypes.add(fiType);
	}
	
	public void addAccount(Account account) {
		Accounts.add(account);
	}
	
	public void addDataFilter(DataFilter dataFilter) {
		DataFilter.add(dataFilter);
	}
}