package com.renault.rnet.idp.bean;

/**
 * Static class for getting files paths (eg. properties and config)
 * 
 * @author rng
 *
 */
public class ResourcesPaths {

	public ResourcesPaths(String contextPath) {
		super();
		this.contextPath = contextPath.replace("\\webapp\\","");	
	}

	private String contextPath = null;
	// private static final String CONFIG_PATH =
	// "E:\\workspaceUsb\\renaultIdp\\WebContent\\config\\sp.config";
	private String CONFIG_PATH = "\\resources\\config\\sp.config";

	// private static final String PROPERTIES_PATH =
	// "E:\\workspaceUsb\\renaultIdp\\WebContent\\config\\sp.properties";
	/**
	 * .properties file
	 */
	// private static final String PROPERTIES_PATH =
	// "d:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\main\\resources\\config\\sp.properties";

	/**
	 * XML file
	 */
	// private static final String PROPERTIES_PATH =
	// "d:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\main\\resources\\config\\serviceproviders.xml";
	private String PROPERTIES_PATH ="\\resources\\config\\serviceproviders.xml";

	// private static final String CERTIFICATES_prodExtVecturySignature01_PATH =
	// "E:\\workspaceUsb\\renaultIdp\\WebContent\\certificates\\prodExtVecturySignature01.p12";
	private String CERTIFICATES_prodExtVecturySignature01_PATH = "\\resources\\config\\prodExtVecturySignature01.p12";

	private String PROPERTIES_XML_PATH = "\\main\\resources\\config\\serviceproviders.xml";

	
	
	public String getCertificatesProdextvecturysignature01Path() {
		return this.contextPath+CERTIFICATES_prodExtVecturySignature01_PATH;
	}

	public String getCertificatesPreprodextvecturysignature01Ext01Path() {
		return this.contextPath+CERTIFICATES_preprodExtVecturySignature01_ext01_PATH;
	}

	private final String CERTIFICATES_preprodExtVecturySignature01_ext01_PATH = "E:\\workspaceUsb\\renaultIdp\\WebContent\\certificates\\preprodExtVecturySignature01-ext01.p12";

	public String getConfigPath() {
		return this.contextPath+CONFIG_PATH;
	}

	public String getPropertiesPath() {
		return this.contextPath+PROPERTIES_PATH;
	}

	public String getPropertiesXmlPath() {
		return this.contextPath+PROPERTIES_XML_PATH;
	}

}
