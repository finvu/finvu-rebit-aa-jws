# Signing AA ecosystem API Request/Response and Consent Artefacts

The REBIT AA ecosystem APIs (https://api.rebit.org.in/) have certain APIs that require signing the request/response before sending, and the receiver needs to verify the signature before honoring such request/responses.

Here we present platform/language neutral methods to sign content so that the AA ecosystem partners can inter-operate.

## Usage

Pull git and import project in your favourite java IDE, like Eclipse.

Run the JWSSignatureTest.java

## Sample Message

Sample used:

```
{
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
```

## JSON Web Signatures (JWS)

We use Json Web Signature (JWS) method to sign the content, as descirbed in RFC7515 here: https://www.rfc-editor.org/rfc/rfc7515.txt

As described in the RFC, a JWS contains 3 sections, i.e. A header, the content and the signature. All sections are base64 encoded. JWS is typically transmitted in JSON Serialization format, which is represented as 3 base64 encoded strings as a continuous string, each string separated by a dot.

As per the REBIT specifications, the content is already being sent in the http body of the message, hence including content in the JWS will be  duplication of data and inefficient.

Another option would be to send the JWS itself as the body of the http message, however it obfuscates the message and renders debugging quite difficult.

The JWS specification also provides a method to produce signature without the content. This is described in Appendix F of RFC7515.

This method is called 'Detached Content' signature method.

In this method, the generated JWS signature does not contain the content part, only the header and signature are returned.

A typical Detached Content signature looks like this:

```
eyJhbGciOiJSUzUxMiIsImtpZCI6IjQyNzE5MTNlLTdiOTMtNDlkZC05OTQ5LTFjNzZmZjVmYzVjZiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..bWfMy_4OhMVT02yd5H3VDwZBVFC2l0eoUCOgzHOwwoo6_vGvD8WuwS9yZLAkZopx-WPJ8t4_BbOVXKb5YHv9WMTcgbmZt1126ScPIEalsldOS2sSFtZyUCyBtG5XklHTv-ZYZYQNaLkDHCJvBAcd6YpJSLfVaTOrG1hUDw_u0OrU28jg1dizvHFenB5Ibsn_Y9g9-7SGrPXTKfLTqgkxFy8tzR8rw4oQP7D-E6cHLMxn5FdJAxl0emOsPmV9Sb3MLqHa0Gx-0SBM0K6MRYnfqjEAE32Diw94DRmFjguFaUQMOcG08piPy9Nvv9vfkumEWpx7Yd19H7PnTd79UStUHQ
```
Note the two 'dots' in the signature. It shows that the content which is usually attached between these two dots is absent.

Examples can be seen in section 4.5 of rfc7520 here: https://tools.ietf.org/html/rfc7520#page-24

In the method explained below, Detached Content signature is used.

## Sending API Request content in the http body, send signature in http header:

In this method, the content can be sent as plain text in the body of the http message, and the signature can be sent separately in customized http header of the same http request/response.

Open banking uses x-jws-signature as the http header to set the signature.

This method has several advantages:

* request/responses that that have badly formatted x-jws-signature header can be rejected before ever reading the full HTTP body
* The http body need not be parsed to verify signature
* Gives more options to keep the message signing away from business logic as the HTTP body needs no manipulation
* No unnecessary serialization/deserialization of objects for generating and embeddeding signature

In this method, following steps are performed:

* The content to sign if is in object form, is serialized to plain text
* A detached content signature is then generated for the text
* The signature is set in the http header, and the plain text is sent as http body.
* The request is then sent to the receiver.
 
* On the receiving side:
* The receiver extracts the signature from header
* The receiver extract the plain text from body of the http post request
* The receiver uses the detached signature and plain text to verify the signature.

A typical HTTP request using this method should look like the following):

```
POST /FI/request HTTP/1.1
Host: aaapi.finvu.in
Content-Type: application/json
Content-Length: 643
aa_api_key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ0b3B0YWwuY29tIiwiZXhwIjoxNDI2NDIwODAwLCJodHRwOi8vdG9wdGFsLmNvbS9qd3RfY2xhaW1zL2lzX2FkbWluIjp0cnVlLCJjb21wYW55IjoiVG9wdGFsIiwiYXdlc29tZSI6dHJ1ZX0.yRQYnWzskCZUxPwaQupWkiUzKELZ49eM7oWxAQK_ZXw
x-jws-signature: eyJhbGciOiJSUzUxMiIsImtpZCI6IjQyNzE5MTNlLTdiOTMtNDlkZC05OTQ5LTFjNzZmZjVmYzVjZiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..bWfMy_4OhMVT02yd5H3VDwZBVFC2l0eoUCOgzHOwwoo6_vGvD8WuwS9yZLAkZopx-WPJ8t4_BbOVXKb5YHv9WMTcgbmZt1126ScPIEalsldOS2sSFtZyUCyBtG5XklHTv-ZYZYQNaLkDHCJvBAcd6YpJSLfVaTOrG1hUDw_u0OrU28jg1dizvHFenB5Ibsn_Y9g9-7SGrPXTKfLTqgkxFy8tzR8rw4oQP7D-E6cHLMxn5FdJAxl0emOsPmV9Sb3MLqHa0Gx-0SBM0K6MRYnfqjEAE32Diw94DRmFjguFaUQMOcG08piPy9Nvv9vfkumEWpx7Yd19H7PnTd79UStUHQ


{
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
  "KeyMaterial": {
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
```

 As we can see this method is much simpler and has significant performance advantages, so it is recommended to use this method for all API request/responses.
 
### Points to note when using this method:
 * Signature must be generated AFTER serializing the JSON object, in other words, the signature should be generated on the plain text representation of the JSON which will be sent in the http request.
 * Signature must be verified BEFORE de-serializing request/response into JSON object. In other words, the signature must be verified against the plain text representation of the JSON as received in the http request.
 
## Signing and verifying signature in consents
 
 In the AA ecosystem, Consents are signed artefacts authorizing a FIU to request for data with an FIP.
 
 Consents must be transmitted with every FI Request to the Account Aggregator by FIU. Hence it is important for an AA to generate a consent artefact with signature that can be verified by the FIP before honoring the request.
 
 Since the consents are embedded within the FI Request, it is recommended to use JWS compact serilization (i.e. a JWS with embedded content) and embed the JWS itself in the FI Request.
 
 Since the JWS already has the consent embedded, there is no need to include the plain text consent artefact.
 
 Since the JWS is already is base64 encoded and is in the 'compact serialized' format, there is no need to extract, prase and convert consent artefact to plain text to verify signature as that would certainly lead to incorrect signature verification.
 
 The entity verifying the consent artefact signature can extract the consent artefact JWS and simply verify it using any standard json JWS processing libraries.
 
 Once signature is verified, the requester can extract the base64 encoded content payload from the JWS and convert to object if required for further processing and validation.
 
 The JWSSignatureTest.java class illustrates signing and verifying signature of consents. The body of a typical consent response looks like this:
 
 ```
 {
  "ver" : "1.0",
  "txnid" : "0b811819-9044-4856-b0ee-8c88035f8858",
  "consentId" : "XXXX-XXXX-XXXX-XXXX",
  "status" : "ACTIVE",
  "createTimestamp" : "2018-12-06T11:39:57.153Z",
  "signedConsent" : "eyJhbGciOiJSUzI1NiIsImtpZCI6ImY2NzZiNjg4LTA3YTItNDUwYi1hYmI1LTU3ZmJiOThlY2FiZSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19.eyJjb25zZW50U3RhcnQiOjE1ODYxNjYxMjQ4NjcsImNvbnNlbnRFeHBpcnkiOjE2MTc2MTU3MjIzMTQsImNvbnNlbnRNb2RlIjoiVklFVyIsImZldGNoVHlwZSI6IlBFUklPRElDIiwiY29uc2VudFR5cGVzIjpbIlNVTU1BUlkiLCJUUkFOU0FDVElPTlMiLCJQUk9GSUxFIl0sImZpVHlwZXMiOlsiREVQT1NJVCIsIlRFUk0tREVQT1NJVCJdLCJEYXRhQ29uc3VtZXIiOnsiaWQiOiJjb29raWVqYXItYWFAZmludnUuaW4iLCJ0eXBlIjoiQUEifSwiRGF0YVByb3ZpZGVyIjp7ImlkIjoiQkFSQjBLSU1YWFgiLCJ0eXBlIjoiRklQIn0sIkN1c3RvbWVyIjp7ImlkIjoiamF5QGZpbnZ1In0sIkFjY291bnRzIjpbeyJmaVR5cGUiOiJERVBPU0lUIiwiZmlwSWQiOiJCQVJCMEtJTVhYWCIsImFjY1R5cGUiOiJDVVJSRU5UIiwibGlua1JlZk51bWJlciI6IjAzOTQ4ODU2NjY4ODAwIiwibWFza2VkQWNjTnVtYmVyIjoiWFhYWFhYWFhYODAwMCJ9XSwiUHVycG9zZSI6eyJjb2RlIjoiMTAyIiwicmVmVXJpIjoiaHR0cHM6Ly9hcGkucmViaXQub3JnLmluL2FhL3B1cnBvc2UvMTAyLnhtbCIsInRleHQiOiJQZXJzb25hbCBGaW5hbmNlIE1hbmFnZW1lbnQiLCJDYXRlZ29yeSI6eyJ0eXBlIjoicHVycG9zZUNhdGVnb3J5VHlwZSJ9fSwiRklEYXRhUmFuZ2UiOnsiZnJvbSI6MTU3MDQ0MTMyMjMxNCwidG8iOjE1ODYxNjYxMjQ4Njd9LCJEYXRhTGlmZSI6eyJ1bml0IjoiWUVBUiIsInZhbHVlIjoxfSwiRnJlcXVlbmN5Ijp7InVuaXQiOiJEQVkiLCJ2YWx1ZSI6MX0sIkRhdGFGaWx0ZXIiOlt7InR5cGUiOiJUUkFOU0FDVElPTkFNT1VOVCIsIm9wZXJhdG9yIjoiPiIsInZhbHVlIjoiMTAifV19.UnthBM3KTz8S0K9jk6UfOOCYsSwQ4BZgc5c7P1HmL_h1czqpx91xXG94BgMlOpMdDHnmmpPNdxP5GMVrHiqjwwMaRZ_5UG_ySA5UoeBllOFrX2z_dyRMOrg4EUMyq3YT3KWjHR7RtOe4zrQmKzbbaPgpPca-6aSINag51Isbof4crDK-gj1S2yMyHj6JMAIvJLi9ypuOVFk4uO6Wld-XZ0BR_XNLBVccCEzT5TFB7lnzRRuuRPzMluhQgv_t2IWbSOBTvtWYm4ktllYoTiWa1ab6xOX-l3Z_OF8rhVHq5eDxhVPxYyXdJMBEWejw6G9G02ez0pIKJ0Sc2O-_wIKgmg",
  "consentUse" : {
    "logUri" : "loguri string",
    "count" : 1.0,
    "lastUseDateTime" : "2018-12-06T11:39:57.153Z"
  }
}
 ```
 Once extracted, the consent content of the JWS looks like this (formatted for readability):
 
```
{
	"consentStart": 1586166124867,
	"consentExpiry": 1617615722314,
	"consentMode": "VIEW",
	"fetchType": "PERIODIC",
	"consentTypes": [
		"SUMMARY",
		"TRANSACTIONS",
		"PROFILE"
	],
	"fiTypes": [
		"DEPOSIT",
		"TERM-DEPOSIT"
	],
	"DataConsumer": {
		"id": "cookiejar-aa@finvu.in",
		"type": "AA"
	},
	"DataProvider": {
		"id": "BARB0KIMXXX",
		"type": "FIP"
	},
	"Customer": {
		"id": "jay@finvu"
	},
	"Accounts": [
		{
			"fiType": "DEPOSIT",
			"fipId": "BARB0KIMXXX",
			"accType": "CURRENT",
			"linkRefNumber": "03948856668800",
			"maskedAccNumber": "XXXXXXXXX8000"
		}
	],
	"Purpose": {
		"code": "102",
		"refUri": "https://api.rebit.org.in/aa/purpose/102.xml",
		"text": "Personal Finance Management",
		"Category": {
			"type": "purposeCategoryType"
		}
	},
	"FIDataRange": {
		"from": 1570441322314,
		"to": 1586166124867
	},
	"DataLife": {
		"unit": "YEAR",
		"value": 1
	},
	"Frequency": {
		"unit": "DAY",
		"value": 1
	},
	"DataFilter": [
		{
			"type": "TRANSACTIONAMOUNT",
			"operator": ">",
			"value": "10"
		}
	]
}
```
## JOSE Header requirements
When signing API requests and consents, following claims must be included by the signing entity in the JWS header section. The receiving entity must use these fields to correctly identify the certificate for verifying the signature.

| claim | Required | Description |
| --- | --- | --- |
| alg | Yes | The algorithm that will be used for signing the JWS. This must be RS256
| kid | Yes | Key id. This parameter indicates which key was used to sign the JWS. The verifying entity must use this to identify the certificate for verifying the signature. The kid must be a globally unique identifier. UUID may be used when generating the certificate by the signing entity. When submitting the certificate to the central registry, this kid must be specified for the certificate.

### JOSE Header Example:
```
{
	"alg": "RS256",
	"kid": "f676b688-07a2-450b-abb5-57fbb98ecabe",
	"b64": false,
	"crit": [
		"b64"
	]
}
```


