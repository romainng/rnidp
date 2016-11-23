package com.renault.rnet.idp.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.Parameter;

/**
 * Manage the URL given by the method POST
 * 
 * @author rng
 *
 */
public class URLParameters {

	private final static org.slf4j.Logger log = LoggerFactory.getLogger(URLParameters.class);

	// Object parameters
	private Parameter parameters;

	public Parameter getParameters() {
		return parameters;
	}

	public void setParameters(Parameter parameters) {
		this.parameters = parameters;
	}

	/**
	 * parameter Name in URL
	 */
	//private static final String IPN = "ipn";

	/**
	 * parameter Name in URL
	 */
	private static final String SP = "sp";

	// the list of errors when fetching parameters
	private Map<String, String> errors = new HashMap<String, String>();

	// If no errors true else false;
	private boolean succes;
	private String uid;
	/**
	 * Fetch parameters from URL(ig. uid + service provider)
	 * 
	 * @param request
	 * @return parameters
	 */
	public URLParameters(HttpServletRequest request, String uid) {
		parameters = new Parameter();
		
		// TODO TEST PURPOSE
		/*
		 * String uid = "a189564"; String sp ="salesforce";
		 */
/*
		if(uid == null || uid.equals("")){
			this.uid = request.getParameter(IPN);
		}else{
			this.uid = uid;
		}
		*/
		String sp = request.getParameter(SP);

		try {
			if (uid == null) {
				Principal userPrincipal = request.getUserPrincipal();

				if (userPrincipal != null) {
					this.uid = userPrincipal.getName();
				}
			}

			validUID(uid);
		} catch (Exception e) {
			//setErrors(IPN, e.getMessage());
		} finally {
			log.info("IPN set in URL parameter =" + uid);
		}
		parameters.setUid(uid);

		try {
			validSP(sp);
		} catch (Exception e) {
			setErrors(SP, e.getMessage());
		} finally {
			log.info("Service provider set in URL parameter =" + sp);
		}
		parameters.setSp(sp);
	}

	/**
	 * Check IPN
	 * 
	 * @param uid
	 * @throws Exception
	 */
	private void validUID(String uid/* ,Logger logger */) throws Exception {
		if (uid != null && uid.trim().length() < 2) {
			// logger.error("ipn error.");
			throw new Exception("L'ipn est incorrect.");
		}
	}

	/**
	 * Check Service provider
	 * 
	 * @param sp
	 * @param logger
	 * @throws Exception
	 */
	private void validSP(String sp/* ,Logger logger */) throws Exception {
		if (sp != null && sp.trim().length() < 2) {
			// logger.error("sp error.");
			throw new Exception("L'id de la sp est incorrect.");
		}
	}

	/**
	 * Create an error
	 * 
	 * @param field
	 * @param mess
	 */
	private void setErrors(String field, String mess) {
		errors.put(field, mess);
	}

	public Map<String, String> getErrors() {
		return this.errors;
	}

	/**
	 * get succes statut if true datas are ok else false
	 * 
	 * @return
	 */
	public boolean getSucces() {
		return this.succes;
	}

}
