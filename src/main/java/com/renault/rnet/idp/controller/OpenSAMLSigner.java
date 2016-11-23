package com.renault.rnet.idp.controller;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.opensaml.saml2.core.Response;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author nfriand
 *
 */

public class OpenSAMLSigner {

	
	
	private final org.slf4j.Logger log = LoggerFactory.getLogger(OpenSAMLSigner.class);

	private Credential credential;

	private X509Certificate x509cert;

	private PrivateKey privateKey;

	private PublicKey publicKey;	

	// sign parameter
	private String canonicalizationAlgorithm = SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;

	// sign parameter : TRUE mean signedInfo added
	private boolean includeKeyInfoInSignature = true;

	/**
	 * @param keystorePath
	 * @param keystorePassword
	 * @param keystoreType
	 */
	public OpenSAMLSigner(String keystorePath, String keystorePassword, String keystoreType) throws Exception {

		// load
		FileInputStream fis = null;
		KeyStore keyStore;

		// keyStore = KeyStore.getInstance(keystoreType, "BC");
		keyStore = KeyStore.getInstance(keystoreType);

		log.debug("init-ks Provider : " + keyStore.getProvider().toString());

		fis = new FileInputStream(keystorePath);
		keyStore.load(fis, keystorePassword.toCharArray());

		// Get PrivateKey, useful to sign

		String alias = null;
		Enumeration<?> enumAlias = keyStore.aliases();

		// read KeyStore : and get unique Private Key, no need to know alias
		int i = 1;

		while (enumAlias.hasMoreElements() && (privateKey == null)) {

			alias = (String) enumAlias.nextElement();
			boolean isKeyEntry = keyStore.isKeyEntry(alias);
			log.debug("init-ks alias " + i++ + " isKeyEntry: " + isKeyEntry + " alias : " + alias);

			if (isKeyEntry) {
				privateKey = (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
				publicKey = keyStore.getCertificate(alias).getPublicKey();
			}
		} // while

		if (privateKey == null) {
			log.error("init-No privateKey found...");
		}

		x509cert = (X509Certificate) keyStore.getCertificate(alias);

		log.debug("Loading credential data from keystore");

		BasicX509Credential basicX509Credential = new BasicX509Credential();
		basicX509Credential.setEntityCertificate(x509cert);
		basicX509Credential.setPrivateKey((PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray()));
		basicX509Credential.setPublicKey(keyStore.getCertificate(alias).getPublicKey());

		// set credential
		credential = basicX509Credential;
	}

	public SignableXMLObject sign(SignableXMLObject signableXmlObject) {

		Signature signature = (Signature) Configuration.getBuilderFactory().getBuilder(Signature.DEFAULT_ELEMENT_NAME)
				.buildObject(Signature.DEFAULT_ELEMENT_NAME);

		// set credential
		signature.setSigningCredential(credential);

		// set cannon algo
		signature.setCanonicalizationAlgorithm(canonicalizationAlgorithm);

		// add signedInfo
		if (includeKeyInfoInSignature) {
			KeyInfoGeneratorManager keyInfoGeneratorManager = Configuration.getGlobalSecurityConfiguration()
					.getKeyInfoGeneratorManager().getDefaultManager();
			KeyInfoGeneratorFactory keyInfoGeneratorFactory = keyInfoGeneratorManager.getFactory(credential);
			KeyInfo keyInfo = null;
			try {
				keyInfo = keyInfoGeneratorFactory.newInstance().generate(credential);
			} catch (SecurityException | org.opensaml.xml.security.SecurityException e) {
				throw new RuntimeException(e);
			}
			signature.setKeyInfo(keyInfo);
		}

		// signature algo selected depending on public Key Type
		if (credential.getPublicKey().getAlgorithm().equalsIgnoreCase("DSA")) {
			signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_DSA);
		} else if (credential.getPublicKey().getAlgorithm().equalsIgnoreCase("RSA")) {
			signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA);
		} else {
			throw new RuntimeException(
					new SignatureException("Unknown public key algorithm. Signature algorithm not set."));
		}

		signableXmlObject.setSignature(signature);

		log.debug("Marshalling signableXmlObject");
		MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(signableXmlObject);
		try {
			marshaller.marshall(signableXmlObject);
		} catch (MarshallingException e) {
			throw new RuntimeException(e);
		}

		log.debug("Signing signableXmlObject");
		try {
			Signer.signObject(signature);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}

		log.debug("Returning signed signableXmlObject");
		return signableXmlObject;
	}

	public void verify(Response response) {
		// Response response = getResponse();

		// StaticKeyInfoCredentialResolver skicr = new
		// StaticKeyInfoCredentialResolver(credential);
		//
		// org.opensaml.saml2.encryption.Decrypter dec = new Decrypter(null,
		// skicr, new InlineEncryptedKeyResolver());
		// dec.setRootInNewDocument(true);
		//
		//
		// try {
		// Assertion assertion =
		// dec.decrypt(response.getEncryptedAssertions().get(0));}
		// catch (DecryptionException e) { e.printStackTrace();}

		SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();

		try {
			Signature res = response.getSignature();
			profileValidator.validate(res);

		} catch (ValidationException e) {
			// Indicates signature did not conform to SAML Signature profile
			e.printStackTrace();
		}

		// Credential verificationCredential =
		// getVerificationCredential(response);
		Credential verificationCredential = credential;
		SignatureValidator sigValidator = new SignatureValidator(verificationCredential);

		try {
			sigValidator.validate(response.getSignature());
		} catch (ValidationException e) {
			// Indicates signature was not cryptographically valid, or possibly
			// a processing error
			e.printStackTrace();
		}

	}
	
	
}
