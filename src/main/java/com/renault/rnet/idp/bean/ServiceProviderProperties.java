package com.renault.rnet.idp.bean;

import java.util.List;
import org.slf4j.LoggerFactory;

import com.renault.rn.config.RNConfig;

/**
 * Properties of a specific (named by spName) Service Provider
 * those properties are : issuerURL, confirmationDataRecipient, audienceURI, relaystate, confirmationDataNotOnOrAfter, conditionsNotBefore, conditionsNotOnOrAfter, attributes, profiles, administrators.
 * 
 * @author rng
 *
 */
public class ServiceProviderProperties {
	private final static org.slf4j.Logger log = LoggerFactory.getLogger(ServiceProviderProperties.class);

	// Name of the invoked Service Provider
	private String spName = null;

	// RN PROPERTIES FILE
	private RNConfig propertiesFile = null;

	public RNConfig getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(RNConfig propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	
	
	
	/**
	 * 
	 * BEGIN : SERVICE PROVIDER ATTRIBUTE LIST
	 * 
	 */

	private String issuerURL;

	private String confirmationDataRecipient;

	private String audienceURI;

	private String relaystate;

	private int confirmationDataNotOnOrAfter;

	private int conditionsNotBefore;

	private int conditionsNotOnOrAfter;

	private List<String> attributes;

	private List<String> profiles;

	private List<String> administrators;

	/**
	 * 
	 * END : SERVICE PROVIDER ATTRIBUTE LIST
	 * 
	 */

	/**
	 * string for querying in sp property file
	 */
	private final String CONSTANT_SP = "sp";
	private final String ISSUER_URL = "IssuerURL";
	private final String CONFIRMATION_DATA_RECIPIENT = "ConfirmationDataRecipient";
	private final String AUDIENCE_URI = "AudienceURI";
	private final String RELAY_STATE = "RelayState";
	private final String CONFIRMATION_DATA_NOT_ON_OR_AFTER = "ConfirmationDataNotOnOrAfter";
	private final String CONDITION_NOT_BEFORE = "ConditionsNotBefore";
	private final String CONDITION_NOT_AFTER = "ConditionsNotOnOrAfter";
	private final String ATTRIBUTES = "Attributes";
	private final String PROFILES = "Profiles";
	private final String ADMINISTRATORS = "Administrators";

	/**
	 * Constructor 
	 * Instantiate object with this constructor if you don't have config file and 
	 * you have to set manually configuration with setters methods
	 * @param spName
	 *            The name of the invoked Service Provider
	 */
	public ServiceProviderProperties(String spName) {
		this.spName = spName;
	}


	/**
	 * Overload constructor 
	 * Standard constructor : when invoked set the service
	 * provider properties with the RNConf object set in param
	 * 
	 * @param spName
	 * @param propertiesFile
	 */
	
	public ServiceProviderProperties(String spName, RNConfig propertiesFile) {
		this.spName = spName;
		this.propertiesFile = propertiesFile;

		if (this.spName != null && this.propertiesFile != null) {
			setPropertiesProcess();
		}

	}
	
	public void setPropertiesProcess(){
		setIssuerURL();
		setConfirmationDataRecipient();
		setAudienceURI();
		setRelaystate();
		setConfirmationDataNotOnOrAfter();
		setConditionsNotBefore();
		setConditionsNotOnOrAfter();
		setAttributes();
		setProfiles();
		setAdministrators();
	}
	

	/**
	 * 
	 * BEGIN : Getters
	 * 
	 */

	public String getSpName() {
		return this.spName;
	}

	public String getIssuerURL() {
		return issuerURL;
	}

	public String getConfirmationDataRecipient() {
		return confirmationDataRecipient;
	}

	public String getAudienceURI() {
		return audienceURI;
	}

	public String getRelaystate() {
		return relaystate;
	}

	public int getConfirmationDataNotOnOrAfter() {
		return confirmationDataNotOnOrAfter;
	}

	public int getConditionsNotOnOrAfter() {
		return conditionsNotOnOrAfter;
	}

	public List<String> getAdministrators() {
		return administrators;
	}

	public List<String> getProfiles() {
		return profiles;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public int getConditionsNotBefore() {
		return conditionsNotBefore;
	}

	/**
	 * 
	 * END : Getters
	 * 
	 */

	/**
	 * 
	 * BEGIN : Setters
	 * 
	 */
	public void setSpName(String spName) {
		this.spName = spName;
	}

	public void setIssuerURL(String issuerURL) {
		this.issuerURL = issuerURL;
	}

	public void setIssuerURL() {
		this.issuerURL = this.propertiesFile.getString(querying(ISSUER_URL), null, null);
		log.info("Result query issuer URL for "+this.spName+" ="+this.issuerURL);
	}

	public void setConfirmationDataRecipient(String confirmationDataRecipient) {
		this.confirmationDataRecipient = confirmationDataRecipient;
	}

	public void setConfirmationDataRecipient() {
		this.confirmationDataRecipient = this.propertiesFile.getString(querying(CONFIRMATION_DATA_RECIPIENT), null,
				null);
		log.info("Result query Confirmation Data Recipient for "+this.spName+" ="+this.confirmationDataRecipient);
	}

	public void setAudienceURI(String audienceURI) {
		this.audienceURI = audienceURI;
	}

	public void setAudienceURI() {
		this.audienceURI = this.propertiesFile.getString(querying(AUDIENCE_URI), null, null);
		log.info("Result query audience URI for "+this.spName+" ="+this.audienceURI);
	}

	public void setRelaystate(String relaystate) {
		this.relaystate = relaystate;
	}

	public void setRelaystate() {
		this.relaystate = this.propertiesFile.getString(querying(RELAY_STATE), null, null);
		log.info("Result query relaystate for "+this.spName+" ="+this.relaystate);
	}

	public void setConfirmationDataNotOnOrAfter(int confirmationDataNotOnOrAfter) {
		this.confirmationDataNotOnOrAfter = confirmationDataNotOnOrAfter;
	}

	public void setConfirmationDataNotOnOrAfter() {
		this.confirmationDataNotOnOrAfter = this.propertiesFile.getInt(querying(CONFIRMATION_DATA_NOT_ON_OR_AFTER), 30,
				null);
		log.info("Result query confirmation Data Not On Or After for "+this.spName+" ="+this.confirmationDataNotOnOrAfter);

	}

	public void setConditionsNotBefore(int conditionsNotBefore) {
		this.conditionsNotBefore = conditionsNotBefore;
	}

	public void setConditionsNotBefore() {
		this.conditionsNotBefore = this.propertiesFile.getInt(querying(CONDITION_NOT_BEFORE), 1, null);
		log.info("Result query conditions Not Before for "+this.spName+" ="+this.conditionsNotBefore);

	}

	public void setConditionsNotOnOrAfter(int conditionsNotOnOrAfter) {
		this.conditionsNotOnOrAfter = conditionsNotOnOrAfter;
	}

	public void setConditionsNotOnOrAfter() {
		this.conditionsNotOnOrAfter = this.propertiesFile.getInt(querying(CONDITION_NOT_AFTER), 30, null);
		log.info("Result query conditions Not On Or After for "+this.spName+" ="+this.conditionsNotOnOrAfter);

	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public void setAttributes() {
		this.attributes = this.propertiesFile.getList(querying(ATTRIBUTES), null, null);
		log.info("Result query attributes for "+this.spName+" ="+this.attributes);

	}

	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}

	public void setProfiles() {
		this.profiles = this.propertiesFile.getList(querying(PROFILES), null, null);
		log.info("Result query profiles for "+this.spName+" ="+this.profiles);

	}

	public void setAdministrators(List<String> administrators) {
		this.administrators = administrators;
	}

	public void setAdministrators() {
		this.administrators = this.propertiesFile.getList(querying(ADMINISTRATORS), null, null);
		log.info("Result query administrators for "+this.spName+" ="+this.administrators);

	}

	/**
	 * 
	 * END : Setters
	 * 
	 */

	/**
	 * String generator for querying properties in service provider properties
	 * 
	 * @param handlerName
	 * @param propertyName
	 * @return
	 */
	private String querying(String propertyName) {
		StringBuilder strb = new StringBuilder();
		strb.append(CONSTANT_SP);
		strb.append(".");
		strb.append(this.spName);
		strb.append(".");
		strb.append(propertyName);
		log.debug("Query in properties file for "+this.spName+" query="+strb.toString());
		return strb.toString();
	}
		
	
}
