# Signing AA ecosystem API Request/Response

The REBIT AA ecosystem APIs (https://api.rebit.org.in/) have certain APIs that require signing the request/response before sending, and the receiver needs to validate the signature before honoring such request/responses.

Here we present two methods to sign content that is to be sent in the http body of a message.

## JSON Web Signatures (JWS)

We use Json Web Signature (JWS) method to sign the content, as descirbed in RFC7515 here: https://www.rfc-editor.org/rfc/rfc7515.txt

As described in the RFC, a JWS contains 3 sections, i.e. A header, the content and the signature. All sections are base64 encoded.

Since the content is already being sent in the http body of the message, there will be duplication to include the content in the JWS.

Even though, the JWS itself can be sent as the body of the http message, it obfuscates the message and renders debugging quite difficult.

The JWS specification also provides a method to produce signature without the content. This is described in Appendix F of RFC7515.

This method is called 'Detached Content' signature method.

In this method, the generated JWS signature does not contain the content part, only the header and signature are returned.

Examples can be seen in section 4.5 of rfc7520 here: https://tools.ietf.org/html/rfc7520#page-24

For both the methods explained below, Detached Content signatures are used.

### Method 1 - Embed signature within the content before sending the message

In this method, following steps are performed:

(It is assumed that the content to be signed is already in object form.)
* A serialized copy of the content is generated and a Detached Content JWS signature (See Appendix F of RFC7515) is generated. 
* The signature is then embedded inside the content object.
* The object is then serialized again and sent in the http body to receiver. 

On the receiving side, following steps are performed:
* First the content is parsed into object form
* The detached signature is then extracted
* The signature is set to null in the parsed object, and serialized for verification
* The serialized content and the extracted signature is then used to validate the signature.

This method has many disadvantages, as we can see from the above steps, there are significant processing overheads for redundant serialization and deserialization operations. Also, this is error prone, as the content has to be modified to embed and remove signature as needed.

### Method 2 - Send content in the http body, send signature in http header:

In this method, the content is sent as is in the body of the http message, and the signature us sent separately in customized http header of the same message.

Open banking uses x-jws-signature as the http header to set the signature.

This method has several advantages:

* request/responses that that have badly formatted x-jws-signature header can be rejected before ever reading the full HTTP body
* Gives more options to keep the message signing away from business logic as the HTTP body needs no manipulation
* No unnecessary serialization/deserialization of objects for generating and embeddeding signature when compared to option 1

In this method, following steps are performed:

* The content to sign if is in object form, is serialized to text
* A detached signature is then generated for the text
* The detached signature is set in the http header, and the text is sent as http body.
* The request is then sent to the receiver.
 
* On the receiving side:
* The receiver extracts the signature from header
* The receiver extract the text from body of the http post request
* The receiver uses the detached signature and text to validate the signature.
   
 As we can see the second method is much simpler and has significant performance advantage over method 1. Hence, Method 2 is recommended.
