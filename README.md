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
  "consent" : {
    "consentJWS" : "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQyNzE5MTNlLTdiOTMtNDlkZC05OTQ5LTFjNzZmZjVmYzVjZiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19.eyJjb25zZW50U3RhcnQiOiIyMDE5LTEyLTA2VDExOjM5OjU3LjE1M1oiLCJjb25zZW50RXhwaXJ5IjoiMjAxOS0xMi0wNlQxMTozOTo1Ny4xNTNaIiwiY29uc2VudE1vZGUiOiJWSUVXIiwiZmV0Y2hUeXBlIjoiT05FVElNRSIsInB1cnBvc2UiOnsiY29kZSI6IjEwMSIsInJlZlVyaSI6Imh0dHBzOi8vYXBpLnJlYml0Lm9yZy5pbi9hYS9wdXJwb3NlLzEwMS54bWwiLCJ0ZXh0IjoiV2VhbHRoIG1hbmFnZW1lbnQgc2VydmljZSIsImNhdGVnb3J5Ijp7InR5cGUiOiJjYXRlZ29yeSB0eXBlIn19LCJkYXRhTGlmZSI6eyJ1bml0IjoiREFZIiwidmFsdWUiOjAuMH0sImZyZXF1ZW5jeSI6eyJ1bml0IjoiSE9VUiIsInZhbHVlIjoxLjB9LCJkYXRhUHJvdmlkZXIiOnsiaWQiOiJEUDEiLCJ0eXBlIjoiRklQIn0sImRhdGFDb25zdW1lciI6eyJpZCI6IkRDMSIsInR5cGUiOiJBQSJ9LCJjdXN0b21lciI6eyJpZCI6ImN1c3RvbWVyQGZpbnZ1LmluIn0sImZpZGF0YVJhbmdlIjp7ImZyb20iOiIyMDE3LTA3LTEzVDExOjMzOjM0LjUwOVoiLCJ0byI6IjIwMTctMDctMTNUMTE6MzM6MzQuNTA5WiJ9fQ.NdjwMjLortTb10dxcJezkvdOxPVvEdZvIqwqVWOHnE8pS_YDswcRPLTRmds2xO-Tvm_A2cFv1qKYpaZnv8Bl6xmZoOsG_F8_40gRZqIhz5hF9puzp8lEEhMlZ6NaX2Y2OYkwHcOBBpCIXpLXB4CMWiefUEWjO9zKDd5JJwZ4vLWeT4qgwvPfRqE60B33tzVlF5E6OA2mKK17sGRXsfrI9obEjL52RMdGo_9bv1HnHfvPlbj5ihj6d_5iTtoh7HUC_X8CrJGvkgkCjP_7of1jPb5QgJ9nx_Yfsxj3vf8zEseZIVISjF3MwBzW7Di4CpfQl4wlnpkQO9MKu78F69Z9Ig"
  },
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
	"consentStart": "2019-12-06T11:39:57.153Z",
	"consentExpiry": "2019-12-06T11:39:57.153Z",
	"consentMode": "VIEW",
	"fetchType": "ONETIME",
	"fidataRange": {
		"from": "2017-07-13T11:33:34.509Z",
		"to": "2017-07-13T11:33:34.509Z"
	},
	"frequency": {
		"unit": "HOUR",
		"value": 1.0
	},
	"purpose": {
		"code": "101",
		"refUri": "https://api.rebit.org.in/aa/purpose/101.xml",
		"text": "Wealth management service",
		"category": {
			"type": "category type"
		}
	},
	"dataConsumer": {
		"id": "DC1",
		"type": "AA"
	},
	"dataLife": {
		"unit": "DAY",
		"value": 0.0
	},
	"customer": {
		"id": "customer@finvu.in"
	},
	"dataProvider": {
		"id": "DP1",
		"type": "FIP"
	}
}
```
## JOSE Header requirements
When signing API requests and consents, following claims must be included by the signing entity in the JWS header section. The receiving entity must use these fields to correctly identify the certificate for verifying the signature.

| claim | Required | Description |
| --- | --- | --- |
| alg | Yes | The algorithm that will be used for signing the JWS. This must be RS256
| kid | Yes | Key id. This parameter indicates which key was used to sign the JWS. The verifying entity must use this to identify the certificate for verifying the signature. The kid must be a globally unique identifier. UUID may be used when generating the certificate by the signing entity. When submitting the certificate to the central registry, this kid must be used against the certificate.

### Example:
```
{
  "alg": "RS256",
  "kid": "4271913e-7b93-49dd-9949-1c76ff5fc5cf"
}
```


