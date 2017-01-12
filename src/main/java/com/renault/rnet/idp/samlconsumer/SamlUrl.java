package com.renault.rnet.idp.samlconsumer;

import javax.servlet.http.HttpServletRequest;

public class SamlUrl {

	private final String SAML_RESPONSE = "SAMLResponse";
	private final String RELAYSTATE = "RelayState";
	
	private String saml64 =null;
	private String relay = null;
	
	public SamlUrl(HttpServletRequest request){
		this.setSaml64(request.getParameter(SAML_RESPONSE));
		this.setRelay(request.getParameter(RELAYSTATE));
	}

	public String getSaml64() {
		return saml64;
	}

	public void setSaml64(String saml64) {
		this.saml64 = saml64;
	}

	public String getRelay() {
		return relay;
	}

	public void setRelay(String relay) {
		this.relay = relay;
	}
}
