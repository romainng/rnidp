package com.renault.rnet.idp.ldap;

/**
 * 
 * @author nfriand
 *
 */
public class LdapException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LdapException(String e) {
		super(e);
	}

	public LdapException(Exception e) {
		super(e);
	}


}
