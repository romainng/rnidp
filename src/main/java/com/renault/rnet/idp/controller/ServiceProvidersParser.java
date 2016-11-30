package com.renault.rnet.idp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.bean.ServiceProvidersList;

/**
 * Parse Service providers xml file 
 * @author rng
 *
 */
public class ServiceProvidersParser {

	private final String Issuer_URL = "IssuerURL";
	private final String Confirmation_Data_Recipient = "ConfirmationDataRecipient";
	private final String Audience_URI = "AudienceURI";
	private final String Relay_State = "RelayState";
	private final String Confirmation_Data_Not_On_Or_After = "ConfirmationDataNotOnOrAfter";
	private final String Conditions_Not_Before = "ConditionsNotBefore";
	private final String Conditions_Not_On_Or_After = "ConditionsNotOnOrAfter";
	private final String Attributes = "Attributes";
	private final String Profiles = "Profiles";
	private final String Administrators = "Administrators";
	private final String SP_NAME_XML = "name";

	private final org.slf4j.Logger log = LoggerFactory.getLogger(ServiceProvidersParser.class);

	
	public ServiceProvidersParser(){
		
	}
	
	public HashMap<String, ServiceProviderProperties> parseXML(String path) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		HashMap<String, ServiceProviderProperties> handlers = null;
		try {
			log.info("START PARSING SP XML");
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new File(path));

			final Element racine = document.getDocumentElement();

			final NodeList racineNoeuds = racine.getChildNodes();

			List<Element> serviceProviders = getChildren(racineNoeuds);

			Iterator<Element> spIterator = serviceProviders.iterator();
			handlers = new HashMap<String, ServiceProviderProperties>();

			while (spIterator.hasNext()) {
				Element spNode = spIterator.next();
				List<Element> spChildren = getChildren(spNode.getChildNodes());

				ServiceProviderProperties spProperties = createServiceProvider(spNode.getAttribute(SP_NAME_XML),
						spChildren);
				if (spProperties != null) {
					handlers.put(spProperties.getSpName(), spProperties);
				}

			}

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

		return handlers;
	}
	

	public ServiceProviderProperties createServiceProvider(String spName, List<Element> spChildren) {
		ServiceProviderProperties spProperties = null;
		if (spName != null && !spName.equals("")) {
			spProperties = new ServiceProviderProperties(spName);
			Iterator<Element> propertiesIterator = spChildren.iterator();
			while (propertiesIterator.hasNext()) {
				Element property = propertiesIterator.next();

				switch (property.getNodeName()) {
				case Issuer_URL:
					spProperties.setIssuerURL(property.getTextContent());
					break;

				case Confirmation_Data_Recipient:
					spProperties.setConfirmationDataRecipient(property.getTextContent());
					break;

				case Audience_URI:
					spProperties.setAudienceURI(property.getTextContent());
					break;

				case Relay_State:
					spProperties.setRelaystate(property.getTextContent());
					break;

				case Confirmation_Data_Not_On_Or_After:
					spProperties.setConfirmationDataNotOnOrAfter(Integer.valueOf(property.getTextContent()));
					break;

				case Conditions_Not_Before:
					spProperties.setConditionsNotBefore(Integer.valueOf(property.getTextContent()));
					break;

				case Conditions_Not_On_Or_After:
					spProperties.setConditionsNotOnOrAfter(Integer.valueOf(property.getTextContent()));
					break;

				case Attributes:

					List<Element> spAttChildren = getChildren(property.getChildNodes());
					List<String> listSpAttributes = new ArrayList<String>();
					Iterator<Element> spAttIterator = spAttChildren.iterator();
					while (spAttIterator.hasNext()) {
						Element att = spAttIterator.next();
						listSpAttributes.add(att.getTextContent());
					}
					spProperties.setAttributes(listSpAttributes);

					break;

				case Profiles:

					List<Element> spProfileChildren = getChildren(property.getChildNodes());
					List<String> listSpProfiles = new ArrayList<String>();
					Iterator<Element> spProfileIterator = spProfileChildren.iterator();
					while (spProfileIterator.hasNext()) {
						Element att = spProfileIterator.next();
						listSpProfiles.add(att.getTextContent());
					}
					spProperties.setProfiles(listSpProfiles);

					break;

				case Administrators:

					List<Element> spAdminChildren = getChildren(property.getChildNodes());
					List<String> listAdmin = new ArrayList<String>();
					Iterator<Element> spAdminIterator = spAdminChildren.iterator();
					while (spAdminIterator.hasNext()) {
						Element admin = spAdminIterator.next();
						listAdmin.add(admin.getTextContent());
					}
					spProperties.setAdministrators(listAdmin);
					break;

				default:
					break;
				}

			}

		}

		return spProperties;

	}

	public List<Element> getChildren(NodeList nodeList) {
		ArrayList<Element> nodes = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element node = (Element) nodeList.item(i);

				nodes.add(node);
			}
		}
		return nodes;
	}
	
	
	

}
