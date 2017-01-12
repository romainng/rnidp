package com.renault.rnet.idp.samlconsumer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.Instant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SamlTokenValidator {

	private String before = null;
	private String after = null;
	private String signature = null;
	private Map<String, String> attributeMap;

	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

	private List<String> AttributesReq;
	private String saml;
	private boolean okToken = false;

	private TokenManagement tkManagement = new TokenManagement();

	public SamlTokenValidator(String saml) {
		this.saml = saml;
		processToken();
	}

	private void processToken() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		try {
			db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(this.saml));
			try {
				Document doc = db.parse(is);
				Element element = doc.getDocumentElement();
				this.signature = tkManagement.getElementString("ds:SignatureValue", element);
				this.before = tkManagement.getElementAttString("saml2:Conditions", "NotBefore", element);
				this.after = tkManagement.getElementAttString("saml2:Conditions", "NotOnOrAfter", element);

				if (ValidAuth.AUTH_SIGNATURE(signature)) {
					if (ValidAuth.AUTH_BEFORE(Instant.parse(before)) && ValidAuth.AUTH_AFTER(Instant.parse(after))) {
						this.setOkToken(true);

						getAttributesReq(element);

						Iterator<String> attIterator = AttributesReq.iterator();

						this.attributeMap = new HashMap<String, String>();

						while (attIterator.hasNext()) {
							String attValue = attIterator.next();
							String elementValueString = tkManagement.getElementValueString(attValue, element);
							attributeMap.put(attValue, elementValueString);

						}

						// tkManagement.getElementValueString(, element);
					} else {
						this.setOkToken(false);
						System.out.println("KO:instant");

					}
				} else {
					this.setOkToken(false);
					System.out.println("KO:signature");
				}

			} catch (SAXException e) {
				this.setOkToken(false);
			} catch (IOException e) {
				this.setOkToken(false);
			}
		} catch (ParserConfigurationException e1) {
			this.setOkToken(false);
		}
	}

	public void getAttributesReq(Element element) {
		NodeList elementAttListNode = tkManagement.getElementAttListNode("saml2:Attribute", element);
		this.AttributesReq = new ArrayList<>();
		if (elementAttListNode != null && elementAttListNode.getLength() > 0) {
			for (int i = 0; i < elementAttListNode.getLength(); i++) {
				Node item = elementAttListNode.item(i);
				String nodeValue = item.getAttributes().getNamedItem("Name").getNodeValue();
				this.AttributesReq.add(nodeValue);
			}
		}
	}

	public boolean isOkToken() {
		return okToken;
	}

	public void setOkToken(boolean okToken) {
		this.okToken = okToken;
	}

}
