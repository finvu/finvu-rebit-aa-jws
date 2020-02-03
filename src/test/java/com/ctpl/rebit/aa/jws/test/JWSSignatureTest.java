package com.ctpl.rebit.aa.jws.test;

import java.util.Base64;

import org.jose4j.jws.JsonWebSignature;

import com.ctpl.rebit.aa.consent.Account;
import com.ctpl.rebit.aa.consent.Category;
import com.ctpl.rebit.aa.consent.ConsentDetail;
import com.ctpl.rebit.aa.consent.ConsentJWS;
import com.ctpl.rebit.aa.consent.ConsentResponse;
import com.ctpl.rebit.aa.consent.ConsentUse;
import com.ctpl.rebit.aa.consent.Customer;
import com.ctpl.rebit.aa.consent.DataConsumer;
import com.ctpl.rebit.aa.consent.DataFilter;
import com.ctpl.rebit.aa.consent.DataLife;
import com.ctpl.rebit.aa.consent.DataProvider;
import com.ctpl.rebit.aa.consent.Frequency;
import com.ctpl.rebit.aa.consent.Purpose;
import com.ctpl.rebit.aa.jws.JWSSignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWSSignatureTest {
	
	/*
	 * Content to sign:
	 * {
		  "ver": "1.0",
		  "timestamp": "2018-06-09T09:58:50.505Z",
		  "txnid": "c4a1450c-d08a-45b4-a475-0468bd10e380",
		  "Consent": {
		    "id": "654024c8-29c8-11e8-8868-0289437bf331",
		    "digitalSignature": "Digital signature of the consentDetail section in the consent Artefact"
		  },
		  "FIDataRange": {
		    "from": "2018-11-27T06:26:29.761Z",
		    "to": "2018-12-27T06:26:29.761Z"
		  },
		  "KeyMaterials": {
		    "cryptoAlg": "ECDHE",
		    "curve": "Curve25519",
		    "params": "string",
		    "DHPublicKey": {
		      "expiry": "2019-06-01T09:58:50.505Z",
		      "Parameters": "string",
		      "KeyValue": "string"
		    },
		    "Nonce": 0
		  }
		}
	 */
	static final String bodyToSign = "{\r\n" + 
			"  \"ver\": \"1.0\",\r\n" + 
			"  \"timestamp\": \"2018-06-09T09:58:50.505Z\",\r\n" + 
			"  \"txnid\": \"c4a1450c-d08a-45b4-a475-0468bd10e380\",\r\n" + 
			"  \"Consent\": {\r\n" + 
			"    \"id\": \"654024c8-29c8-11e8-8868-0289437bf331\",\r\n" + 
			"    \"digitalSignature\": \"Digital signature of the consentDetail section in the consent Artefact\"\r\n" + 
			"  },\r\n" + 
			"  \"FIDataRange\": {\r\n" + 
			"    \"from\": \"2018-11-27T06:26:29.761Z\",\r\n" + 
			"    \"to\": \"2018-12-27T06:26:29.761Z\"\r\n" + 
			"  },\r\n" + 
			"  \"KeyMaterials\": {\r\n" + 
			"    \"cryptoAlg\": \"ECDHE\",\r\n" + 
			"    \"curve\": \"Curve25519\",\r\n" + 
			"    \"params\": \"string\",\r\n" + 
			"    \"DHPublicKey\": {\r\n" + 
			"      \"expiry\": \"2019-06-01T09:58:50.505Z\",\r\n" + 
			"      \"Parameters\": \"string\",\r\n" + 
			"      \"KeyValue\": \"string\"\r\n" + 
			"    },\r\n" + 
			"    \"Nonce\": 0\r\n" + 
			"  }\r\n" + 
			"}";

	public static void main(String[] args) throws Exception {
		
		System.out.println("\n\n###### Running API Request example with detached content JWS:");
		testDetachedApiRequestExample();
		
		System.out.println("\n\n###### Running API Request with compact serialization of consent (i.e. JWS with embedded content):");
		testCompactSerializedConsentExample();
	}
	
	/**
	 * In the detached content method, the content and signature are transmitted separately.
	 * 
	 * In this method, following steps are performed:
	 * 
	 * 1. If the content to sign is in object form, is serialized to text
	 * 2. A detached signature is then generated for the text
	 * 3. The detached signature is set in the http header, and the text is sent as http body.
	 * 4. The request is then sent to the receiver.
	 * 
	 * On the receiving side:
	 * 1. The receiver extracts the signature from header
	 * 2. The receiver extract the text from body of the http post request
	 * 3. The receiver uses the detached signature and text to validate the signature.
	 * 
	 * Note: In order for this to work, the sender must always generate
	 * signature after serializing the content and the receiver must
	 * always validate the signature before de-serializing the content.
	 * 
	 * @throws Exception
	 */
	public static void testDetachedApiRequestExample() throws Exception {
		
		// On the sender side:
		System.out.println("The content to sign is: \n" + bodyToSign);
		JWSSignatureUtil util = new JWSSignatureUtil();
		String signature = util.sign(bodyToSign);
		
		System.out.print("JWS with detached content: " + signature);
		System.out.println();
		
		// The signature is then sent separately as a custom HTTP Header
		// and the content (bodyToSign in this case) is sent in the body of the http post request.
		
		// On the receiver side:
		System.out.println();
		System.out.println("Request to vaidate is: " + bodyToSign);
		System.out.println("Signature is: " + signature);
		System.out.println();
		System.out.println("#######Signature valid?: " + util.parseSign(signature, bodyToSign));
	}
	
	/**
	 * For signing consent a JWS with the content represented in compact serialized format can be used.
	 * 
	 * The following illustrates typical steps.
	 * 
	 * 1. The sender prepares consent response object
	 * 2. The sender prepares consent artefact object.
	 * 3. The sender generates text of the consent artefact, and generates signature.
	 * 4. The sender sets the consent to the response object.
	 * 5. The sender generates text of the response and sends it to receiver.
	 * 
	 * On the receiving side:
	 * 
	 * 1. The receiver parses the response received into object.
	 * 2. The receiver extracts the consent artefact JWS
	 * 3. The receiver validates the JWS
	 * 4. The receiver can then proceed to extract the body of the JWS and parse it a consent artefact object.
	 * 
	 * 
	 * @throws Exception
	 */	
	public static void testCompactSerializedConsentExample() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		
		// Create the response object (for demo purpose)
		
		ConsentResponse response = new ConsentResponse();
		
		response.setVer("1.0");
		response.setTxnid("0b811819-9044-4856-b0ee-8c88035f8858");
		response.setConsentId("XXXX-XXXX-XXXX-XXXX");
		response.setStatus("ACTIVE");
		response.setCreateTimestamp("2018-12-06T11:39:57.153Z");
		
		// Create the consent artefact for signing.
		ConsentDetail consentDetail = new ConsentDetail();
		
		consentDetail.setConsentStart("2019-12-06T11:39:57.153Z");
		consentDetail.setConsentExpiry("2019-12-06T11:39:57.153Z");
		consentDetail.setConsentMode("VIEW");
		consentDetail.setFetchType("ONETIME");
		consentDetail.addConsentType("BALANCE");
		consentDetail.addFitype("DEPOSIT");
		
		DataConsumer dataConsumer = new DataConsumer();
		dataConsumer.setId("DC1");
		dataConsumer.setType("AA");
		
		consentDetail.setDataConsumer(dataConsumer);
		
		DataProvider dataProvider = new DataProvider();
		dataProvider.setId("DP1");
		dataProvider.setType("FIP");
		
		consentDetail.setDataProvider(dataProvider);
		
		Customer customer = new Customer();
		customer.setId("customer@finvu.in");
		
		consentDetail.setCustomer(customer);
		
		Account acc = new Account();
		acc.setFiType("DEPOSIT");
		acc.setFipId("FIP1");
		acc.setAccType("SAVINGS");
		acc.setLinkRefNumber("XXXX-XXXX-XXXX");
		acc.setMaskedAccNumber("XXXXXXXX4020");
		
		consentDetail.addAccount(acc);
		
		Purpose purpose = new Purpose();
		
		purpose.setCode("101");
		purpose.setRefUri("https://api.rebit.org.in/aa/purpose/101.xml");
		purpose.setText("Wealth management service");
		
		Category cat = new Category();
		cat.setType("category type");
		
		purpose.setCategory(cat);
		
		consentDetail.setPurpose(purpose);
		
		com.ctpl.rebit.aa.consent.FIDataRange fiDataRange = new com.ctpl.rebit.aa.consent.FIDataRange();
		
		fiDataRange.setFrom("2017-07-13T11:33:34.509Z");
		fiDataRange.setTo("2017-07-13T11:33:34.509Z");
		
		consentDetail.setFIDataRange(fiDataRange);
		
		DataLife dataLife = new DataLife();
		
		dataLife.setUnit("DAY");
		dataLife.setValue(0);
		
		consentDetail.setDataLife(dataLife);
	
		Frequency frequency = new Frequency();
		
		frequency.setUnit("HOUR");
		frequency.setValue(1);
		
		consentDetail.setFrequency(frequency);
		
		DataFilter dataFilter = new DataFilter();
		
		dataFilter.setType("TRANSACTIONAMOUNT");
		dataFilter.setOperator(">=");
		dataFilter.setValue(20000);
		
		consentDetail.addDataFilter(dataFilter);
		
		// Generate consent string for signing.
		String consentDetailToSign =  mapper.writeValueAsString(consentDetail);
		
		// Generate content signature in the compact serialization format.
		JWSSignatureUtil util = new JWSSignatureUtil();
		String signature = util.signEmbedded(consentDetailToSign);
		
		
		ConsentJWS consent = new ConsentJWS();
		consent.setConsentJWS(signature);
		
		// set the consent JWS to the response.
		response.setConsent(consent);
		
		ConsentUse consentUse = new ConsentUse();
		
		consentUse.setLogUri("loguri string");
		consentUse.setCount(1);
		consentUse.setLastUseDateTime("2018-12-06T11:39:57.153Z");
		
		response.setConsentUse(consentUse);
		
		String consentResponse = mapper.writeValueAsString(response);
		
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
		
		
		// Now on the receiving side, lets validate signature.
		
		// first parse and extract the consent detail object.
		
		ConsentResponse receiverResponse = mapper.readValue(consentResponse, ConsentResponse.class);
		
		// Then get the consent JWS.
		ConsentJWS receivedConsent = receiverResponse.getConsent();
		String consentJWS = receivedConsent.getConsentJWS();
		
		JsonWebSignature verifierJws = util.parseSign(consentJWS, null);
		
		// now validate the signature.
		System.out.println("Signature valid?: " + verifierJws.verifySignature());
		
		// now extract the body and decode it:
		
		
		System.out.println("consent artefact: " + new String(Base64.getDecoder().decode(verifierJws.getEncodedPayload())));
		
		
	}

}
