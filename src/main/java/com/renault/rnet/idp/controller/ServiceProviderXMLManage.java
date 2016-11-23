package com.renault.rnet.idp.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.renault.rnet.idp.bean.ServiceProviderProperties;

/**
 * Manage (read write modify) Service provider xml file
 * @author rng
 *
 */
public class ServiceProviderXMLManage {

	private final String Issuer_URL = "IssuerURL";
	private final String Confirmation_Data_Recipient = "ConfirmationDataRecipient";
	private final String Audience_URI = "AudienceURI";
	private final String Relay_State = "RelayState";
	private final String Confirmation_Data_Not_On_Or_After = "ConfirmationDataNotOnOrAfter";
	private final String Conditions_Not_Before = "ConditionsNotBefore";
	private final String Conditions_Not_On_Or_After = "ConditionsNotOnOrAfter";
	private final String Attributes_ = "Attributes";
	private final String Attribute_ = "Attribute";
	private final String Profiles_ = "Profiles";
	private final String Profile_ = "Profile";
	private final String Administrators_ = "Administrators";
	private final String Administrator_ = "Administrator";
	private final String SP_NAME_XML = "name";
	private final String SERVICE_PROVIDER_XML = "Serviceprovider";
	private Document doc;
	private DocumentBuilder builder;
	private String pathXML;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(ServiceProviderXMLManage.class);

	public ServiceProviderXMLManage(String pathXML) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			this.pathXML = pathXML;
			this.doc = builder.parse(new File(pathXML));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void modifySPXML(ServiceProviderProperties spp) {
		 delSpXML(spp.getSpName(),false);
		 addSPXML(spp);
	}
	
	
	public void delSpXML(String spToDel){
		delSpXML(spToDel,true);
	}

	private void delSpXML(String spToDel,boolean reWriteXML) {
		boolean isDel = false;
		Element root = this.doc.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) childNodes.item(i);
				if (elem.getAttribute(SP_NAME_XML).equals(spToDel)) {
					elem.getParentNode().removeChild(elem);
					isDel = true;
					break;
				}
			}
		}
		
		if(isDel && reWriteXML){
			log.debug("Service Provider "+spToDel+" deleted succesfully and write process OK");
			writeXML();
		}else if(isDel && !reWriteXML){
			log.info("Service Provider "+spToDel+" deleted succesfully but write process postponed");
		}else if(!isDel){
			log.error("Service Provider "+spToDel+" failed to be deleted");
		}
		
		
	}

	private boolean isExistSP(Element root, String nameSp) {
		boolean exist = false;

		NodeList childNodes = root.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) childNodes.item(i);

				if (elem.getAttribute(SP_NAME_XML).equals(nameSp)) {
					exist = true;
					break;
				}
			}

		}

		return exist;
	}

	public void addSPXML(ServiceProviderProperties spProperties) {
		Element root = this.doc.getDocumentElement();

		if (!isExistSP(root, spProperties.getSpName())) {
			//try {

				Element elementSP = this.doc.createElement(SERVICE_PROVIDER_XML);
				Attr attr = doc.createAttribute(SP_NAME_XML);
				attr.setValue(spProperties.getSpName());
				elementSP.setAttributeNode(attr);

				Element recipient = this.doc.createElement(Confirmation_Data_Recipient);
				recipient.appendChild(this.doc.createTextNode(spProperties.getConfirmationDataRecipient()));
				elementSP.appendChild(recipient);

				Element audience = this.doc.createElement(Audience_URI);
				audience.appendChild(doc.createTextNode(spProperties.getAudienceURI()));
				elementSP.appendChild(audience);

				Element elementIssuer = this.doc.createElement(Issuer_URL);
				elementIssuer.appendChild(doc.createTextNode(spProperties.getIssuerURL()));
				elementSP.appendChild(elementIssuer);

				Element relay = this.doc.createElement(Relay_State);
				relay.appendChild(doc.createTextNode(spProperties.getRelaystate()));
				elementSP.appendChild(relay);

				Element confirmation = this.doc.createElement(Confirmation_Data_Not_On_Or_After);
				confirmation.appendChild(
						this.doc.createTextNode(String.valueOf(spProperties.getConfirmationDataNotOnOrAfter())));
				elementSP.appendChild(confirmation);

				Element before = this.doc.createElement(Conditions_Not_Before);
				before.appendChild(this.doc.createTextNode(String.valueOf(spProperties.getConditionsNotBefore())));
				elementSP.appendChild(before);

				Element after = this.doc.createElement(Conditions_Not_On_Or_After);
				after.appendChild(this.doc.createTextNode(String.valueOf(spProperties.getConditionsNotOnOrAfter())));
				elementSP.appendChild(after);

				Element attributes = this.doc.createElement(Attributes_);
				List<String> attList = spProperties.getAttributes();
				if(attList !=null && attList.size()>0){
				Iterator<String> attIterator = attList.iterator();
				while (attIterator.hasNext()) {
					String attNext = attIterator.next();
					Element attribute = this.doc.createElement(Attribute_);
					attribute.appendChild(this.doc.createTextNode(attNext));
					attributes.appendChild(attribute);
				}
				}
				elementSP.appendChild(attributes);

				Element profiles = this.doc.createElement(Profiles_);
				List<String> profileList = spProperties.getProfiles();
				Iterator<String> profileIterator = profileList.iterator();
				while (profileIterator.hasNext()) {
					String profileNext = profileIterator.next();
					Element profile = this.doc.createElement(Profile_);
					profile.appendChild(this.doc.createTextNode(profileNext));
					profiles.appendChild(profile);
				}
				elementSP.appendChild(profiles);

				Element admins = this.doc.createElement(Administrators_);
				List<String> adminList = spProperties.getAdministrators();
				Iterator<String> adminIterator = adminList.iterator();
				while (adminIterator.hasNext()) {
					String adminNext = adminIterator.next();
					Element admin = this.doc.createElement(Administrator_);
					admin.appendChild(this.doc.createTextNode(adminNext));
					admins.appendChild(admin);
				}
				elementSP.appendChild(admins);

				root.appendChild(elementSP);
				
				writeXML();
/*
				TransformerFactory tranFactory = TransformerFactory.newInstance();
				Transformer aTransformer;

				aTransformer = tranFactory.newTransformer();
				aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
				aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				Source src = new DOMSource(doc);
				StreamResult result = new StreamResult(this.pathXML);
				aTransformer.transform(src, result);

			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}*/
				log.debug("Sp " + spProperties.getSpName() + " added succesfully");
		} else {
			log.error("Sp " + spProperties.getSpName() + " not added because already exist");
		}

	}

	private void writeXML() {
		try {

			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer;

			aTransformer = tranFactory.newTransformer();

			aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			Source src = new DOMSource(doc);
			StreamResult result = new StreamResult(this.pathXML);
			aTransformer.transform(src, result);
			log.debug("write xml file OK");
		} catch (TransformerConfigurationException e) {
			log.debug("Transformer config ERROR");
			e.printStackTrace();
		} catch (TransformerException e) {
			log.debug("Transformer exception ERROR");
			e.printStackTrace();
		}
	}

}
