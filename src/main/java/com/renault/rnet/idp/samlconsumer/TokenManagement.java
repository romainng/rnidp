package com.renault.rnet.idp.samlconsumer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TokenManagement {

	public TokenManagement() {

	}

	public Node getElementNode(String tagName, Element element) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();

			if (subList != null && subList.getLength() > 0) {
				return subList.item(0);
			}
		}

		return null;
	}

	/**
	 * return xml element (tagname) as a string
	 * 
	 * @param tagName
	 *            tag name
	 * @param element
	 *            root
	 * @return
	 */
	public String getElementString(String tagName, Element element) {
		Node elementNode = getElementNode(tagName, element);

		if (elementNode != null) {
			return elementNode.getNodeValue();
		}
		return null;
	}

	/**
	 * return attribute value (attribute name) as a string of an xml element
	 * (tagname)
	 * 
	 * @param tagName
	 * @param attribute
	 *            attribute name
	 * @param element
	 *            root
	 * @return
	 */
	public String getElementAttString(String tagName, String attribute, Element element) {

		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {

			return list.item(0).getAttributes().getNamedItem(attribute).getNodeValue();

		}

		return null;
	}

	
	public void modifyElementText(String tagName, Element element, String mod){
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			System.out.println("KIKOI");
			System.out.println("MOD ASK="+tagName);
			list.item(0).setTextContent(mod);
		}
		
	}
	public void modifyElementAtt(String tagName, String attribute, Element element, String mod) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {

			list.item(0).getAttributes().getNamedItem(attribute).setNodeValue(mod);

		}
	}

	public NodeList getElementAttListNode(String tagName, Element element) {

		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			return list;
		}
		return null;
	}

	public Node getElementAttNode(String tagName, String namedItem, String attValue, Element element) {

		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {

			for (int i = 0; i < list.getLength(); i++) {
				Node item = list.item(i);

				if (item.getAttributes().getNamedItem(namedItem).getNodeValue().equals(attValue)) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * return attribute (value name) value of a child node as a string
	 * 
	 * @param valueName
	 *            attribute we want to get
	 * @param element
	 * @return
	 */
	public String getElementValueString(String valueName, Element element) {
		String tagName = "saml2:Attribute";
		String namedItem = "Name";
		Node node = getElementAttNode(tagName, namedItem, valueName, element);
		if (node != null) {
			// System.out.println("TEXT
			// CONT="+node.getChildNodes().getLength());
			// NodeList childNodes = node.getChildNodes();
			// for(int i=0; i < childNodes.getLength();i++){
			// if(node.getChildNodes().item(i).getTextContent()!=null &&
			// node.getChildNodes().item(i).getTextContent().length()>1){
			// System.out.println("oula
			// "+node.getChildNodes().item(i).getTextContent());
			//
			if( node.getChildNodes().getLength()>0){
				return node.getChildNodes().item(0).getTextContent();
			}
			
		}

		return null;

	}

}
