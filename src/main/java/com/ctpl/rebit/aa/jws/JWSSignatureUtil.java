package com.ctpl.rebit.aa.jws;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.VerificationJwkSelector;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

public class JWSSignatureUtil {

	private JsonWebKeySet rsaJsonWebKeySet;

	public JWSSignatureUtil() throws Exception {
		rsaJsonWebKeySet = new JsonWebKeySet("{\r\n" + "  \"keys\": [\r\n" + "	{\r\n"
				+ "		\"p\":\"9Rhdmgn4tOQdkqwVs6bwGxNIquHcL4u3PJLm8cRUQirmZuiLFMDfltXPKYO0AUnjn3pM7_Y49-WZnIsiigst08eB5JpRjS1FP20152oKhIXuueQeyeJKNEEjT8lUvy4UWBGfTK-UijTlPSCJouGB7JK1GSqwH9jc_LQ_4hAxB60\",\r\n"
				+ "		\"kty\":\"RSA\",\r\n"
				+ "		\"q\":\"q1oboI0ZBEZmdy8t2KxQCUXHmOuvMwx1OSbmKnWoWvSQ5GWf3_KUOqamcaJQxwwW6pzN6b1mbrJHhntPkSaphp1ZgXXoIjqJR1dp31yD64R7DURYT0Nk4FsH_UixP_mD917tIiphxB-iAwD7EofX9WyEKHPrAIVRZqabZhQ5xuM\",\"d\":\"YoqYVNsE1q6vsZSyXqadJ5QtTWkMbnjIa4GxfRW3tfWRQfNASauXc74LekJGQ3qPA7qWOe6TIzFKpKkcp0DxwQ-Gdm8PGbWGXnD4jcz4hU_ocGkpymeIsYBK7zzVUIyV69Aexx76qi_sFr8C0nw5wsWGculeyXpXnkJOBdv-W5QZ6oftBhxIrniku0nmjED-nrGMrbXUx68BVC8eyJ7asv2GTHU59czC3aFPm7yZ8W7wa6vlEzR2RUVmOki3L_GfjjEIZ5ecP2o-2_beHVhidsqryw8_cEWsyGLzXhP5aDKtWcJxLCh1eKd_yaYQfG1TObymPs8HRGefLxCEBuZNEQ\",\r\n"
				+ "		\"e\":\"AQAB\",\r\n" + "		\"use\":\"sig\",\r\n"
				+ "		\"kid\":\"4271913e-7b93-49dd-9949-1c76ff5fc5cf\",\r\n"
				+ "		\"qi\":\"CMxTPbY1KAs4lVgy4zFeRqqljG8Z1jbfkamZ0Rme8a840gvqmxfx5XekPRH49kMjLFPVxFRXO-29OcEe5hasjZKqcaWYNmBkflvayk-QNiAdttUpHrIcKWuj59YjSOawSArlYUOCDpZkVC6b68G3-wHVYGhKzsoG2VKTHvEGoWU\",\r\n"
				+ "		\"dp\":\"55VSPA5X9XPljYUULF_8V1jtPKmUx0gYpx-XH7IGe07VzT7Ey6NCoN79k5hiKSWL8lA5CjN4uKM1dfjxdcYCUy0byskVbXPtDnU-jZ5gub9jRjoJ7W_n9V_m4ai2br43cs-T2X29uKkqQYvRARpbDRHCFDmFmVHr3Mrj4dKywTE\",\r\n"
				+ "		\"dq\":\"TsGptQ9lEivxaJJMtivs1XY3GTgqXII7VrtkStutN7D076Ut6FasfpCeK9h43CPYXJkZo3ckH3jdmxgOKB7nCDJGM1SoDyNseVfaF4mEMLnBfQWOuU-2i9ALSgUhQtBCMW51-6ATM3t-kXbf71J0lh78V0OAHcso8M9e2XouXBU\",\r\n"
				+ "		\"n\":\"pA2LihUAwJ4yO0IwHlY0F0N-oAr9OgCXUL4uryK39PP9f-s0aGL1Q_4sj6AR7xJDivSAlQeW6_yAEjunnNNUl45Su1m2n9LX2YyuU4-4UzCq2c-1oMcQ9ChmYZ9k2HyWxRUZjVlnIiHHXNGrVvdGRAR7Z7kMrGq1pdPv35oPKZO1YM9nmsjUOUlPgQSwOSGYokNjT96QCOWElid_C6gRwtMc6YyVLXEXYj16xR_OqDWSyEbUO7P1xrZh4b-ekvahL2so1aH6_aqyP70RG2y8zC-oREd1v3MnsMGSjlGjGSifo21hYYXW45OEp5-ygFhBOM0SMhP0O_5rM7tsgO6cZw\"\r\n"
				+ "	}\r\n" + "  ]\r\n" + "}");
	}
	
	public String signEmbedded(String payload) throws Exception {
		return doSign(payload, false);
	}
	
	private String doSign(String payload, boolean detached) throws Exception {
		// Create a new JsonWebSignature object for the signing
		JsonWebSignature signerJws = new JsonWebSignature();

		// The content is the payload of the JWS
		signerJws.setPayload(payload);

		// Set the signature algorithm on the JWS
		signerJws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		RsaJsonWebKey jwk = getJsonWebKey();

		// The private key is used to sign
		signerJws.setKey(jwk.getPrivateKey());

		// Set the Key ID (kid) header because it's just the polite thing to do.
		signerJws.setKeyIdHeaderValue(jwk.getKeyId());

		// Set the "b64" header to false, which indicates that the payload is not
		// encoded
		// when calculating the signature (per RFC 7797)
		signerJws.getHeaders().setObjectHeaderValue(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD, !detached);

		// Produce the compact serialization with an empty/detached payload,
		// which is the encoded header + ".." + the encoded signature
		if(detached) {
			return signerJws.getDetachedContentCompactSerialization();	
		} else {
			// RFC 7797 requires that the "b64" header be listed as critical
			signerJws.setCriticalHeaderNames(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD);
			return signerJws.getCompactSerialization();
		}
	}

	/**
	 * This method generates a detached json web signature,
	 * Using the RFC 7797 JWS Unencoded Payload Option
	 * 
	 * @param payload
	 * @return signature without the payload (i.e. detached signature)
	 * @throws Exception
	 */
	public String sign(String payload) throws Exception {
		return doSign(payload, true);
	}

	/**
	 * This method validates the detached json web signature with the supplied payload.
	 * 
	 * @param detachedSignature
	 * @param payload
	 * @throws Exception - if signature validation fails.
	 */
	public JsonWebSignature parseSign(String detachedSignature, String payload) throws Exception {

		// Use a JsonWebSignature object to verify the signature
		JsonWebSignature verifierJws = new JsonWebSignature();

		// Set the algorithm constraints based on what is agreed upon or expected from
		// the sender
		verifierJws.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
				AlgorithmIdentifiers.RSA_USING_SHA256));

		if(payload == null) {
			// The JWS with embedded content is the compact serialization
			verifierJws.setCompactSerialization(detachedSignature);
		} else {
			// The JWS with detached content is the compact serialization
			verifierJws.setCompactSerialization(detachedSignature);

			// The unencoded detached content is the payload
			verifierJws.setPayload(payload);
		}

		VerificationJwkSelector jwkSelector = new VerificationJwkSelector();
		RsaJsonWebKey jwk = (RsaJsonWebKey) jwkSelector.select(verifierJws, rsaJsonWebKeySet.getJsonWebKeys());

		// The public key is used to verify the signature
		// This should be the public key of the sender.
		verifierJws.setKey(jwk.getPublicKey());
		
		if(!verifierJws.verifySignature()) {
			throw new JoseException("Signature verification failed.");
		}
		
		// return the jws
		return verifierJws;
	}

	private RsaJsonWebKey getJsonWebKey() {
		return (RsaJsonWebKey) rsaJsonWebKeySet.getJsonWebKeys().get(0);
	}
}
