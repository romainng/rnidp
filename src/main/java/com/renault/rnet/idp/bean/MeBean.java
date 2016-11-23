package com.renault.rnet.idp.bean;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.renault.rnet.idp.ldap.LdapConnector;
import com.renault.rnet.idp.ldap.LdapException;

/**
 * Represent all the infomations fetchable in LDAP with the uid (ipn) and all the information we are able to send to the receiver
 * @author rng
 *
 */
public class MeBean {

	/**
	 * ARRAY OF AVAILABLE ATTRIBUTE WITH UID (USER AND DEALER)
	 */
	private final String[] myAttributesArray = { "USER_CN", "USER_NAME", "USER_GIVENNAME", "USER_PREFERREDLANGUAGE",
			"USER_BRANDS", "USER_CITY", "USER_PHONE", "USER_MAIL", "USER_MAILFORWARD", "USER_MAILDELIVERYOPTION",
			"DEALER_NUMBER", "DEALER_NAME", "DEALER_COUNTRY", "DEALER_STREET", "DEALER_POSTALCODE",
			"DEALER_POSTALADDRESS", "DEALER_CITY", "DEALER_DCSKIND", "DEALER_DEPDEALER", "DEALER_SUBSIDIARY" };

	
	private String myUID;
	private LdapConnector myLDAP;
	private Map<String, List<String>> myLDAPAttributes;

	public MeBean(String myUID, LdapConnector myLDAP) {
		this.myUID = myUID;
		this.myLDAP = myLDAP;
		try {
			if (this.myLDAP != null && myUID != null && !myUID.equals("")) {
				this.myLDAPAttributes = this.myLDAP.getAttributes(this.myUID, Arrays.asList(myAttributesArray));
			}
		} catch (LdapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public String getMyUID() {
		return myUID;
	}

	public void setMyUID(String myUID) {
		this.myUID = myUID;
	}

	public LdapConnector getMyLDAP() {
		return myLDAP;
	}

	public void setMyLDAP(LdapConnector myLDAP) {
		this.myLDAP = myLDAP;
	}

	public String getMyLDAPAttributes(ServletContext servletC) {
		StringBuilder strb = new StringBuilder();

		if (this.myLDAPAttributes != null) {
			for (String att : myAttributesArray) {
				List<String> list = this.myLDAPAttributes.get(att);

				strb.append(att);
				strb.append(" : ");
				if (list != null) {
					Iterator<String> iterator = list.iterator();
					while (iterator.hasNext()) {
						String next = iterator.next();
						strb.append(next);
					}
				}
				strb.append(System.lineSeparator());
			}
			return strb.toString();
		} else {
			if (servletC.getAttribute("lang") != null) {
				if (servletC.getAttribute("lang").equals("fr"))
					return "Utilisateur non enregistré dans LDAP! Veulliez contacter un administrateur, merci.";
			}
			return "User not registered in LDAP! Please contact an administrator.";

		}

	}

}
