package com.renault.rnet.idp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.Instant;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.renault.rnet.idp.bean.MeBean;
import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.bean.ServiceProvidersList;
import com.renault.rnet.idp.controller.OpenSAMLSigner;
import com.renault.rnet.idp.controller.SAMLHandler;
import com.renault.rnet.idp.controller.SamlAssertionResponseGen;
import com.renault.rnet.idp.ldap.LdapConnector;
import com.renault.rnet.idp.ldap.LdapException;
import com.renault.rnet.idp.samlconsumer.TokenManagement;

public class AuditTriggerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(AuditTriggerServlet.class);

	private final String IDENTIFY_LDAP = "USER_CN";
	private String uid = null;
	private Instant instantDebut = null;
	private Instant instantFin = null;
	private ServletContext servletC;
	private String serviceProvider = null;
	private OpenSAMLSigner signer = null;

	private LdapConnector ldap = null;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		this.servletC = getServletContext();
		String dateD = (String) request.getParameter("dateDebut");
		String timeD = (String) request.getParameter("timeDebut");
		String dateF = (String) request.getParameter("dateFin");
		String timeF = (String) request.getParameter("timeFin");
		String signerPath = (String) request.getParameter("signerFile");
		if (checkAuditString(request.getParameter("spChoose"))) {
			this.serviceProvider = request.getParameter("spChoose");
		}

		if (checkAuditString(request.getParameter("uid"))) {
			this.uid = (String) request.getParameter("uid");
		} else {
			Principal _userPrincipal = request.getUserPrincipal();
			if (_userPrincipal != null && !_userPrincipal.equals("")) {
				this.uid = _userPrincipal.getName();
			}
		}

		
		if (checkAuditString(dateD) && checkAuditString(timeD)) {

			if (checkInstant(dateD, timeD) != null) {
				this.instantDebut = checkInstant(dateD, timeD);

			} else {
				this.instantDebut = Instant.now();
			}
		} else {
			this.instantDebut = Instant.now();
		}

		if (checkAuditString(dateF) && checkAuditString(timeF)) {

			if (checkInstant(dateF, timeF) != null) {
				this.instantFin = checkInstant(dateF, timeF);
			} else {
				this.instantFin = Instant.now().plus(5000);

			}
		} else {
			this.instantFin = Instant.now().plus(5000);
		}

		if (checkAuditString(signerPath)) {
			this.signer = signerGen(signerPath);
		} else {
			this.signer = (OpenSAMLSigner) servletC.getAttribute("signer");
		}

		ServiceProvidersList spList = (ServiceProvidersList) this.servletC.getAttribute("handlers");

		ServiceProviderProperties spProperties = spList.getSamlHandlers().get(serviceProvider);

		ldap = (LdapConnector) this.servletC.getAttribute("ldapctx");

		SAMLHandler samlHandler = (SAMLHandler) this.servletC.getAttribute("samlHandler");

		if (checkAuditString(this.uid) && checkAuditString(this.serviceProvider) && this.instantDebut != null
				&& this.instantFin != null) {

			List<String> SPattributes = spProperties.getAttributes();
			
			SamlAssertionResponseGen samlProcess = new SamlAssertionResponseGen(uid, serviceProvider, spProperties,
					ldap, samlHandler, signer, response);

			List<String> uidAtt = new ArrayList<String>();
			uidAtt.add(IDENTIFY_LDAP);

			try {

				if (ldap.getAttributes(this.uid, uidAtt) != null) {

					Response responseGen = samlProcess.ResponseGen();
					SAMLHandler mySAMLHandler = new SAMLHandler("issuerURL");
					try {

						MeBean ldapAtt = new MeBean(uid, ldap);
						String[] SPattributesArray = SPattributes.toArray(new String[SPattributes.size()]);
						Map<String, String> mySpecificLDAPAttrMap = ldapAtt.getMySpecificLDAPAttrMap(SPattributesArray);
						// TODO
						String generateNewTokenString = generateNewToken(
								mySAMLHandler.prettyPrintSigneObject(responseGen), uid, mySpecificLDAPAttrMap,
								this.instantDebut, this.instantFin);

						// System.out.println("NEW
						// TOKEN="+generateNewTokenString);
						samlProcess.sendResponse(generateNewTokenString);

					} catch (MarshallingException | TransformerException e) {
						log.error("ERROR GEN AUDIT RESPONSE ");
						e.printStackTrace();
					}
				}
			} catch (LdapException e1) {
				log.error("ERROR GEN AUDIT RESPONSE ");
				e1.printStackTrace();
			}

		} else {
			log.error("PARAM ERROR");
		}

	}

	private OpenSAMLSigner signerGen(String signerPath) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(signerPath)));

			this.signer = new OpenSAMLSigner(properties.getProperty("keystore.path"),
					properties.getProperty("keystore.password"), properties.getProperty("keystore.type"));
			return this.signer;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkAuditString(String string) {

		if (string != null && !string.equals("") && !string.equals("null") && !string.equals("-Select-")) {
			return true;
		} else {
			return false;
		}
	}

	private Instant checkInstant(String date, String time) {
		String dateTime = date.concat("T" + time);
		Instant instant = Instant.parse(dateTime);
		if (instant != null) {
			return instant;
		} else {
			return null;
		}
	}

	private String generateNewToken(String token, String newUID, Map<String, String> newAttributes, Instant modBefore,
			Instant modAfter) {

		TokenManagement tkManagement = new TokenManagement();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		InputSource is = null;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
			is = new InputSource();
			is.setCharacterStream(new StringReader(token));

			try {
				doc = db.parse(is);
				Element element = doc.getDocumentElement();
				// Node elementNode =
				// tkManagement.getElementNode("saml2:Conditions", element);
				tkManagement.modifyElementAtt("saml2:Conditions", "NotBefore", element, modBefore.toString());
				tkManagement.modifyElementAtt("saml2:Conditions", "NotOnOrAfter", element, modAfter.toString());

//				if (newAttributes != null && newAttributes.size() > 0) {
//
//					Set<String> keySet = newAttributes.keySet();
//					Iterator<String> ksIterator = keySet.iterator();
//					while (ksIterator.hasNext()) {
//						
//						String attKey = ksIterator.next();
//						
//						//tkManagement.modifyElementText(attKey, element, newAttributes.get(attKey));
//					}
//
//				}

			} catch (SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;

			transformer = tf.newTransformer();

			// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			// "no");
			// transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
