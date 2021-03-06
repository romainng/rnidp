package com.renault.rnet.idp.bean;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.renault.rn.config.RNConfig;
import com.renault.rnet.idp.controller.ServiceProvidersParser;

/**
 * INITIALISATION Browse properties files and create a map with all the service
 * providers informed in the xml file
 * 
 * @author rng
 *
 */
public class ServiceProvidersList {

	private final String SP_LIST_QUERY = "serviceProvider.list";

	private final org.slf4j.Logger log = LoggerFactory.getLogger(ServiceProvidersList.class);

	private Map<String, ServiceProviderProperties> samlHandlers = new HashMap<String, ServiceProviderProperties>();

	public ServiceProvidersList(String contextPath) {
		
		
		log.info("Trying fetch service providers xml file at "+contextPath);
		if (contextPath.endsWith(".xml")) {
			
			ServiceProvidersParser spParser = new ServiceProvidersParser();
			this.samlHandlers = spParser.parseXML(contextPath);
			log.info("SP xml Parsed succesfully");
		}else{
			log.info("SP xml Parsing failed path="+ contextPath);

		}/*else {
			try {
				propertiesFile = new RNConfig(prop_path);
				log.info("file properties loaded path: " + prop_path);
				listingSAMLHandlerProcess(propertiesFile);
			} catch (RNConfException e) {
				log.error("Constructor : RNconfig object instanciation (properties file)");
				e.printStackTrace();
			}
		}*/

	}

	// static protected OpenSAMLSigner signer = null;
	public Map<String, ServiceProviderProperties> getSamlHandlers() {
		return samlHandlers;
	}

	public void setSamlHandlers(Map<String, ServiceProviderProperties> samlHandlers) {
		this.samlHandlers = samlHandlers;
	}

	public void putSamlHandler(String key, ServiceProviderProperties value) {
		log.debug("Handler put key=" + key + " value sp name=" + value.getSpName());
		this.samlHandlers.put(key, value);
	}

	/**
	 * Put in a map all the services providers set in the properties SP file (by
	 * splitting the string sp list)
	 * 
	 * @param propertiesFile
	 */
	/*
	private void listingSAMLHandlerProcess(RNConfig propertiesFile) {

		String handlersToString = propertiesFile.getString(SP_LIST_QUERY, null);
		try {
			validHandlers(handlersToString);
			String[] listSAMLHandlers = handlersToString.split("\\|");

			// TODO
			for (String handlerName : listSAMLHandlers) {
				ServiceProviderProperties spProperties = new ServiceProviderProperties(handlerName, propertiesFile);
				putSamlHandler(handlerName, spProperties);
				log.debug("SAMLHANDLERS put handler=" + handlerName);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		log.debug("All handlers have been put. Number of entries" + this.samlHandlers.size());

	}*/

	/**
	 * Verifying handlers
	 * 
	 * @param handlers
	 * @throws Exception
	 */
//	private void validHandlers(String handlers/* ,Logger logger */) throws Exception {
//		if (handlers != null && handlers.trim().length() < 2) {
//			// logger.error("sp error.");
//			throw new Exception("Invalid handlers.");
//		}
//	}
	/*
	public ServiceProviderProperties getSpecificSP(){
		ServiceProviderProperties spp = new ServiceProviderProperties(spName)
		
		return null;
	}*/

}
