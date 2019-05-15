package com.ctpl.rebit.aa.jws.test;

import com.ctpl.rebit.aa.consent.Account;
import com.ctpl.rebit.aa.consent.Category;
import com.ctpl.rebit.aa.consent.ConsentDetail;
import com.ctpl.rebit.aa.consent.ConsentResponse;
import com.ctpl.rebit.aa.consent.ConsentUse;
import com.ctpl.rebit.aa.consent.Customer;
import com.ctpl.rebit.aa.consent.DataConsumer;
import com.ctpl.rebit.aa.consent.DataFilter;
import com.ctpl.rebit.aa.consent.DataLife;
import com.ctpl.rebit.aa.consent.DataProvider;
import com.ctpl.rebit.aa.consent.Frequency;
import com.ctpl.rebit.aa.consent.Purpose;
import com.ctpl.rebit.aa.firequest.FIRequest;
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
		
		System.out.println("###### Testing Embedded, detached signature:");
		testEmbeddedDetached();
		
		System.out.println("\n\n###### Testing true detached signature:");
		testTrueDetached();
		
		System.out.println("\n\n###### Testing consent signature:");
		testConsentSignature();
	}
	
	/**
	 * In the embedded method following steps are performed:
	 * 
	 * 1. It is assumed that the content to be signed is already in object form.
	 * 2. A serialized copy of the content is generated and
	 *    a Detached JWS signature (See Appendix F of RFC7515) is generated. 
	 * 3. The signature is then embedded inside the content object.
	 * 4. The object is then serialized again and sent in the http body to receiver.
	 * 
	 * On the receiving side, following steps are performed:
	 * 1. First the content is parsed into object form
	 * 2. The detached signature is then extracted
	 * 3. The signature is set to null in the parsed object, and serialized for verification
	 * 4. The serialized content and the extracted signature is then used to validate the signature.
	 * 
	 * @throws Exception
	 */
	public static void testEmbeddedDetached() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FIRequest request = mapper.readValue(bodyToSign.getBytes(), FIRequest.class);
		
		String requestBeforeSign = mapper.writeValueAsString(request);
		System.out.println("Request before sign: " + requestBeforeSign);
		
		// On the sender side:
		
		JWSSignatureUtil util = new JWSSignatureUtil();
		String signature = util.sign(requestBeforeSign);
		
		request.getKeyMaterials().setSignature(signature);
		
		/*
		 * Signed request (note the added signature object at the end):
		 * 
			{
			  "ver":"1.0",
			  "timestamp":"2018-06-09T09:58:50.505Z",
			  "txnid":"c4a1450c-d08a-45b4-a475-0468bd10e380",
			  "Consent":{
			    "id":"654024c8-29c8-11e8-8868-0289437bf331",
			    "digitalSignature":"Digital signature of the consentDetail section in the consent Artefact"
			  },
			  "FIDataRange":{
			    "from":"2018-11-27T06:26:29.761Z",
			    "to":"2018-12-27T06:26:29.761Z"
			  },
			  "KeyMaterials":{
			    "cryptoAlg":"ECDHE",
			    "curve":"Curve25519",
			    "params":"string",
			    "nonce":"0",
			    "DHPublicKey":{
			      "expiry":"2019-06-01T09:58:50.505Z",
			      "parameters":"string",
			      "keyValue":"string",
			      "Parameters":"string",
			      "KeyValue":"string"
			    },
			    "Nonce":"0",
			    "Signature":"eyJhbGciOiJSUzUxMiIsImtpZCI6IjQyNzE5MTNlLTdiOTMtNDlkZC05OTQ5LTFjNzZmZjVmYzVjZiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..bWfMy_4OhMVT02yd5H3VDwZBVFC2l0eoUCOgzHOwwoo6_vGvD8WuwS9yZLAkZopx-WPJ8t4_BbOVXKb5YHv9WMTcgbmZt1126ScPIEalsldOS2sSFtZyUCyBtG5XklHTv-ZYZYQNaLkDHCJvBAcd6YpJSLfVaTOrG1hUDw_u0OrU28jg1dizvHFenB5Ibsn_Y9g9-7SGrPXTKfLTqgkxFy8tzR8rw4oQP7D-E6cHLMxn5FdJAxl0emOsPmV9Sb3MLqHa0Gx-0SBM0K6MRYnfqjEAE32Diw94DRmFjguFaUQMOcG08piPy9Nvv9vfkumEWpx7Yd19H7PnTd79UStUHQ"
			  }
			}
		 */
		String requestToValidate = mapper.writeValueAsString(request);
		System.out.print("Signed request is: ");
		System.out.println(requestToValidate);
		
		// On the receiver side:
		
		FIRequest requestObjToValidate = mapper.readValue(requestToValidate.getBytes(), FIRequest.class);
		
		String detachedSignature = requestObjToValidate.getKeyMaterials().getSignature();
		System.out.println("Detached Signature is: " + detachedSignature);
		
		// set the signature to null so that the generated string for signature validation does not contain signature.
		requestObjToValidate.getKeyMaterials().setSignature(null);
		requestToValidate = mapper.writeValueAsString(requestObjToValidate);
		
		System.out.println("Request to vaidate is: " + requestToValidate);
		System.out.println("Signature valid?: " + util.validateSign(detachedSignature, requestToValidate));
	}
	
	/**
	 * In the 'true' detached method, the content and signature are transmitted separately.
	 * This has significant advantage over the embedded method, as there is no need to
	 * parse and serialize the content several times and on both sides.
	 * 
	 * In this mothod, following steps are performed:
	 * 
	 * 1. The content to sign if is in object form, is serialized to text
	 * 2. A detached signature is then generated for the text
	 * 3. The detached signature is set in the http header, and the text is sent as http body.
	 * 4. The request is then sent to the receiver.
	 * 
	 * On the receiving side:
	 * 1. The receiver extracts the signature from header
	 * 2. The receiver extract the text from body of the http post request
	 * 3. The receiver uses the detached signature and text to validate the signature.
	 * 
	 * @throws Exception
	 */
	public static void testTrueDetached() throws Exception {
		
		// On the sender side:
		
		JWSSignatureUtil util = new JWSSignatureUtil();
		String signature = util.sign(bodyToSign);
		
		System.out.print("Signed request is: " + signature);
		
		// The signature is then sent separately as a custom HTTP Header
		// and the content (bodyToSign in this case) is sent in the body of the http post request.
		
		// On the receiver side:
		
		System.out.println("Request to vaidate is: " + bodyToSign);
		System.out.println("Signature valid?: " + util.validateSign(signature, bodyToSign));
	}
	
	/**
	 * The Detached content signature can be used for signing and validating consent artefacts as well.
	 * 
	 * The following illustrates typical steps.
	 * 
	 * 1. The sender prepares consent response object
	 * 2. The sender prepares consent artefact object.
	 * 3. The sender generates text of the consent artefact, and generates signature.
	 * 4. The sender sets the consent artefact and the signature to the response object.
	 * 5. The sender generates text of the response and sends it to receiver.
	 * 
	 * On the receiving side:
	 * 
	 * 1. The receiver parses the response received into object.
	 * 2. The receiver extract the consent artefact and the signature
	 * 3. The receiver generates text of the consent artefact object
	 * 4. The receiver validates the consent artefact text with the signature
	 * 
	 * 
	 * @throws Exception
	 */	
	public static void testConsentSignature() throws Exception {
		
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
		
		response.setConsentDetail(consentDetail);
		
		// Generate consent string for signing.
		String consentDetailToSign =  mapper.writeValueAsString(consentDetail);
		
		// Generate detached content signature.
		JWSSignatureUtil util = new JWSSignatureUtil();
		String signature = util.sign(consentDetailToSign);
		
		
		// set the signature to the response.
		response.setConsentDetailDigitalSignature(signature);
		
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
		
		String consentDetailString = mapper.writeValueAsString(receiverResponse.getConsentDetail());
		String signtoValidate = receiverResponse.getConsentDetailDigitalSignature();
		
		// now validate the signature.
		System.out.println("Signature valid?: " + util.validateSign(signtoValidate, consentDetailString));
	}

}
