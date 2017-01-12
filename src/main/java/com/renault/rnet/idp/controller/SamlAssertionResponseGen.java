package com.renault.rnet.idp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.ldap.LdapConnector;
import com.renault.rnet.idp.ldap.LdapException;

public class SamlAssertionResponseGen {

	private static final String SAML_RESPONSE = "SAMLResponse";
	private static final String RELAY_STATE = "RelayState";

	private String userUid;
	private String serviceProvider;
	private ServiceProviderProperties spProperties;
	private LdapConnector ldap;
	private SAMLHandler samlHandler;
	private OpenSAMLSigner signer;
	private final org.slf4j.Logger log = LoggerFactory.getLogger(SamlAssertionResponseGen.class);

	private HttpServletResponse response;

	public SamlAssertionResponseGen(String uid, String serviceProvider, ServiceProviderProperties spProperties,
			LdapConnector ldap, SAMLHandler samlHandler, OpenSAMLSigner signer, HttpServletResponse response) {

		this.userUid = uid;
		this.serviceProvider = serviceProvider;
		this.spProperties = spProperties;
		this.ldap = ldap;
		this.samlHandler = samlHandler;
		this.signer = signer;
		this.response = response;

	}

	public Response ResponseGen() {

		try {

			Map<String, List<String>> attributes = this.ldap.getAttributes(this.userUid,
					this.spProperties.getAttributes());

			if (attributes != null) {
				// Create assertion
				Assertion assertion = this.samlHandler.createAuthnAssertion(this.userUid, attributes,
						this.spProperties);
				log.info("USER=" + this.userUid + " generate assertion for " + this.serviceProvider);
				log.info("USER=" + this.userUid + " assertion : {} ", this.samlHandler.prettyPrint(assertion));

				// create response

				Response responseSAML;

				responseSAML = this.samlHandler.createResponse(assertion);
				log.info("USER=" + this.userUid + " generate response for " + this.serviceProvider);
				// log.info("USER=" + this.userUid + " response : {} ",
				// mySAMLHandler.prettyPrint(responseSAML));
				signer.sign(responseSAML);
				return responseSAML;

			} else {
				log.error("User " + this.userUid + " not registered");
			}

		} catch (LdapException e) {
			log.error("USER=" + this.userUid + " My LDAPConnector error");
			e.printStackTrace();
		} catch (MarshallingException e1) {
			log.error("USER=" + this.userUid + " My Marshalling error");
			e1.printStackTrace();
		} catch (TransformerException e1) {
			log.error("USER=" + this.userUid + " Transformer error");
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		// --------------------------------------
		// request.setAttribute("handlers", this.serviceProvidersList);
		// request.setAttribute("param", this.parameter);

	}

	public void sendResponse(String responseSAML) {



		try {

			// encode 64 using common apache codec
			String EncodedSignedSamlRequestAsString = Base64.encodeBase64String(responseSAML.getBytes("UTF-8"));

			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = null;
			out = response.getWriter();

			out.print("<HTML><BODY onload=VECTURYFORM.submit();>");
			out.print("<FORM NAME=\"VECTURYFORM\" METHOD=\"POST\" ACTION="
					+ this.spProperties.getConfirmationDataRecipient() + ">");
			out.print("<INPUT TYPE=hidden NAME=\"" + SAML_RESPONSE + "\" VALUE=\"" + EncodedSignedSamlRequestAsString
					+ "\">");
			if (this.spProperties.getRelaystate() != null) {
				out.print("<INPUT TYPE=hidden NAME=\"" + RELAY_STATE + "\" VALUE=\"" + this.spProperties.getRelaystate()
						+ "\">");
			}

			out.print("</FORM>");
			out.print("</BODY></HTML>");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
