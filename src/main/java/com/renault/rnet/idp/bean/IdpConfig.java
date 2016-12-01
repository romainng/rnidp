package com.renault.rnet.idp.bean;

import org.slf4j.LoggerFactory;

import com.renault.rn.config.RNConfException;
import com.renault.rn.config.RNConfig;

/**
 * Describe properties defined by the identity provider in
 * the configuration file
 * 
 * @author rng
 *
 */
public class IdpConfig {
//	private final org.slf4j.Logger log = LoggerFactory.getLogger(IdpConfig.class);
//
//	// RN CONFIG FILE
//	private RNConfig configFile = null;
//
//	/**
//	 * 
//	 * BEGIN : CONFIFIGURATION ATTRIBUTES LIST
//	 * 
//	 */
//
//	private String keyStoreLocation;
//
//	private String keyStorePassword;
//
//	private String keyStoreType;
//
//	/**
//	 * 
//	 * END : CONFIFIGURATION ATTRIBUTES LIST
//	 * 
//	 */
//	
//	/**
//	 * string (constant) for querying in property file
//	 */
//	private final String KEYSTORE = "keystore";
//
//	private final String PASSWORD = "password";
//	private final String TYPE = "type";
//	private final String PATH = "path";
//
//	/**
//	 * Constructor 
//	 * Instantiate object with this constructor if you don't have config file and 
//	 * you have to set manually configuration with setters methods
//	 */
//	public IdpConfig(String path){
//		String conf_path;
//		try {
//
//			/**
//			 * String prop_path = System.getProperty("app.properties");
//			 * 
//			 * // FOR TESTS PURPOSES if ((prop_path == null) ||
//			 * (prop_path.length() == 0)) { prop_path =
//			 * Resources.getPropertiesPath(); }
//			 */
//			ResourcesPaths paths = new ResourcesPaths(path);
//			conf_path = System.getProperty("app.configuration");
//			
//			if ((conf_path == null) || (conf_path.length() == 0)) {
//				
//				conf_path = paths.getConfigPath();
//			}
//			
//			this.configFile = new RNConfig(conf_path);
//			log.info("file config loaded path: " + paths.getConfigPath());
//			
//			if (this.configFile != null) {
//				setKeyStoreLocation();
//				setKeyStorePassword();
//				setKeyStoreType();
//			}
//
//		} catch (RNConfException e) {
//			log.error("Constructor : RNconfig object instanciation (config file)");
//			e.printStackTrace();
//		}
//
//	}
//	
//	/**
//	 * Overload constructor 
//	 * Standard constructor : when invoked set configuration 
//	 *  with the RNConf object set in param
//	 * @param configFile
//	 */
//	public IdpConfig(RNConfig configFile) {
//		this.configFile = configFile;
//
//		if (this.configFile != null) {
//			setKeyStoreLocation();
//			setKeyStorePassword();
//			setKeyStoreType();
//		}
//
//	}
//
//	/**
//	 * String generator for querying properties in service provider properties
//	 * 
//	 * @param handlerName
//	 * @param propertyName
//	 * @return
//	 */
//	private String querying(String configName, String configValue) {
//		StringBuilder strb = new StringBuilder();
//
//		strb.append(configName);
//		strb.append(".");
//		strb.append(configValue);
//		return strb.toString();
//	}
//
//	/**
//	 * 
//	 * BEGIN : Setters
//	 * 
//	 */
//
//	public void setKeyStoreLocation(String keyStoreLocation) {
//		this.keyStoreLocation = keyStoreLocation;
//	}
//
//	public void setKeyStoreLocation() {
//		this.keyStoreLocation = this.configFile.getString(querying(KEYSTORE, PATH), null, null);
//		log.info("Configuration key Store Location ="+this.keyStoreLocation);
//
//	}
//
//	public void setKeyStorePassword(String keyStorePassword) {
//		this.keyStorePassword = keyStorePassword;
//	}
//
//	public void setKeyStorePassword() {
//		this.keyStorePassword = this.configFile.getString(querying(KEYSTORE, PASSWORD), null, null);
//		log.info("Configuration key Store Password Location ="+this.keyStorePassword);
//	}
//
//	public void setKeyStoreType(String keyStoreType) {
//		this.keyStoreType = keyStoreType;
//	}
//
//	public void setKeyStoreType() {
//		this.keyStoreType = this.configFile.getString(querying(KEYSTORE, TYPE), null, null);
//		log.info("Configuration set Key Store Type Location ="+this.keyStoreType);
//	}
//
//	/**
//	 * 
//	 * END : Setters
//	 * 
//	 */
//
//	/**
//	 * 
//	 * BEGIN : Getters
//	 * 
//	 */
//
//	public String getKeyStoreLocation() {
//		return keyStoreLocation;
//	}
//
//	public String getKeyStorePassword() {
//		return keyStorePassword;
//	}
//
//	public String getKeyStoreType() {
//		return keyStoreType;
//	}
//
//	/**
//	 * 
//	 * END : Getters
//	 * 
//	 */

}
