# Signing AA ecosystem API Request/Response

The REBIT AA ecosystem APIs (https://api.rebit.org.in/) have certain APIs that require signing the request/response before sending, and the receiver needs to validate the signature before honoring such request/responses.

Here we present two methods to sign content that is to be sent in the http body of a message.

## Usage

Pull git and import project in your favourite java IDE, like Eclipse.

Run the JWSSignatureTest.java

## Sample Message

Sample used for both methods:

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

As described in the RFC, a JWS contains 3 sections, i.e. A header, the content and the signature. All sections are base64 encoded. JWS is typically transmitted in JSON Serialization format, which is represented as 3 base64 encodes strings as a continuous string, each string separated by a dot.

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

For both the methods explained below, Detached Content signatures are used.

### Method 1 - Embed signature within the content before sending the message:

In this method, following steps are performed:

(It is assumed that the content to be signed is already in object form.)
* A serialized copy of the content is generated and a Detached Content JWS signature (See Appendix F of RFC7515) is generated. 
* The signature is then embedded inside the content object.
* The object is then serialized again and sent in the http body to receiver. 

On the receiving side, following steps are performed:
* First the content is parsed into object form
* The signature is then extracted
* The signature is set to null in the parsed object, and serialized for verification
* The serialized content and the extracted signature is then used to validate the signature.

This method has many disadvantages, as we can see from the above steps, there are significant processing overheads for redundant serialization and deserialization operations. Also, this is error prone, as the content has to be modified to embed and remove signature as needed.

This method generates the content as follows:

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
    "Nonce": 0,
    "Signature":"eyJhbGciOiJSUzUxMiIsImtpZCI6IjQyNzE5MTNlLTdiOTMtNDlkZC05OTQ5LTFjNzZmZjVmYzVjZiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..bWfMy_4OhMVT02yd5H3VDwZBVFC2l0eoUCOgzHOwwoo6_vGvD8WuwS9yZLAkZopx-WPJ8t4_BbOVXKb5YHv9WMTcgbmZt1126ScPIEalsldOS2sSFtZyUCyBtG5XklHTv-ZYZYQNaLkDHCJvBAcd6YpJSLfVaTOrG1hUDw_u0OrU28jg1dizvHFenB5Ibsn_Y9g9-7SGrPXTKfLTqgkxFy8tzR8rw4oQP7D-E6cHLMxn5FdJAxl0emOsPmV9Sb3MLqHa0Gx-0SBM0K6MRYnfqjEAE32Diw94DRmFjguFaUQMOcG08piPy9Nvv9vfkumEWpx7Yd19H7PnTd79UStUHQ"
  }
}
```

### Method 2 - Send content in the http body, send signature in http header:

In this method, the content is sent as is in the body of the http message, and the signature is sent separately in customized http header of the same message.

Open banking uses x-jws-signature as the http header to set the signature.

This method has several advantages:

* request/responses that that have badly formatted x-jws-signature header can be rejected before ever reading the full HTTP body
* The http body need not be parsed to validate signature
* Gives more options to keep the message signing away from business logic as the HTTP body needs no manipulation
* No unnecessary serialization/deserialization of objects for generating and embeddeding signature when compared to option 1

In this method, following steps are performed:

* The content to sign if is in object form, is serialized to text
* A detached content signature is then generated for the text
* The signature is set in the http header, and the text is sent as http body.
* The request is then sent to the receiver.
 
* On the receiving side:
* The receiver extracts the signature from header
* The receiver extract the text from body of the http post request
* The receiver uses the detached signature and text to validate the signature.

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

 As we can see the second method is much simpler and has significant performance advantage over method 1. Hence, Method 2 is recommended.
