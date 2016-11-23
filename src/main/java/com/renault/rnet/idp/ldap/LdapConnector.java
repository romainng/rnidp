package com.renault.rnet.idp.ldap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

	/**
	 * LDAP access with cache
	 * 
	 * @author nfriand
	 */

	public class LdapConnector {	
	
	/**
	 * logger
	 */
	final static Logger log = LoggerFactory.getLogger(LdapConnector.class);
	
	/**
	 * tags allowed for searching in LDAP ; at each TAG, there is a unique LDAP attribute mapped
	 */

	public static final Map<String, String> TAG_ATTRIBUTE_MAP;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("USER_CN", "cn");
        aMap.put("USER_NAME", "sn");
        aMap.put("USER_GIVENNAME", "givenname");
        aMap.put("USER_PREFERREDLANGUAGE", "preferredlanguage");
        aMap.put("USER_BRANDS", "dcsbrand");
        aMap.put("USER_CITY", "dcscity");
        aMap.put("USER_PHONE", "telephonenumber");
        aMap.put("USER_MAIL", "mail");
        aMap.put("USER_MAILFORWARD", "mailforwardingaddress");
        aMap.put("USER_MAILDELIVERYOPTION", "maildeliveryoption");
        aMap.put("DEALER_NUMBER", "dcsnumber");
        aMap.put("DEALER_NAME", "dcsname");
        aMap.put("DEALER_COUNTRY", "dcscountry");
        aMap.put("DEALER_STREET", "street");
        aMap.put("DEALER_POSTALCODE", "postalcode");
        aMap.put("DEALER_POSTALADDRESS", "postaladdress");
        aMap.put("DEALER_CITY", "l");
        aMap.put("DEALER_DCSKIND", "dcskind");
        aMap.put("DEALER_DEPDEALER", "dcsdepdealer");
        //No mapping for DEALER_SUBSIDIARY, it is extracted from DN if requested
        aMap.put("DEALER_SUBSIDIARY", "c");
        TAG_ATTRIBUTE_MAP = Collections.unmodifiableMap(aMap);
    }

	/**
	 * environment to build LDAP context, must be static
	 */
	private static Hashtable<String, String> environment;

	/**
	 * to manage cache
	 */
	//private CacheManager cacheManager;
	
	/**
	 * user cache uid<=>TDFUser
	 * Avoid access to ldap because no session managed
	 */
	//private Cache userCache;

	/**
	 * LDAP search base
	 */
	private LdapName LDAPSearchBaseDN;

	/**
	 * LDAP context
	 */
	private InitialLdapContext LDAPcontext;
	
	/**
	 * LDAP context boolean to indicate if context valid
	 */
	private boolean LDAPcontextValid;
	
	/**
	 * @return the lDAPcontextValid
	 */
	public boolean isLDAPcontextValid() {
		return LDAPcontextValid;
	}

	/**
	 * counter to know how many times LDAP context has been reloaded; if all is fine should be always 0
	 */
	private long LDAPcontextReloadCounter;
	
	/**
	 * counter for communication Exception to LDAP
	 */
	private long communicationExceptionCoutner;

	
	/**
	 * Constructor. 
	 */
	public LdapConnector()  {

	}

	/**
	 * Constructor. No Ctls
	 * @param cacheManager 
	 * @param userLdapCache 
	 * @throws NamingException 
	 */
	//public LdapConnector(Hashtable<?, ?> environment, String LDAPSearchBase, CacheManager cacheManager, Cache userCache) throws Exception {
		public LdapConnector(Hashtable<?, ?> environment, String LDAPSearchBase) throws Exception {
				
		this.LDAPcontextReloadCounter = 0; 
		this.communicationExceptionCoutner = 0;

		this.LDAPSearchBaseDN = new LdapName(LDAPSearchBase);
		//this.cacheManager = cacheManager;
		
		//this.userCache = userCache;

		//Handle Communication exception, might occur if LDAP unavailable 
		try {
			LDAPcontext = new InitialLdapContext(environment, null);
			this.LDAPcontextValid = true;
		} catch (Exception e) {
			if(e instanceof CommunicationException){
				this.LDAPcontextValid = false;
			}else{
				throw e;
			}
		}
	}

	/**
	 * Static instance generator with properties
	 */
	public static LdapConnector getInstance(Properties properties) throws LdapException 
	{

		//check parameters
		if (properties == null){
			log.error("invalid parameter; properties is Null");
			throw new LdapException("invalid parameter; properties is Null");
		}
		
		LdapConnector instance = null;
		
		try {
			// LDAP configuration
			environment = new Hashtable<String, String>();
			
			//static value Required
			environment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			
			//list of LDAP available Required
			environment.put(Context.PROVIDER_URL,properties.getProperty("ldap.url"));
			
			//static value Required
			environment.put(Context.SECURITY_AUTHENTICATION, "simple");
			
			//Required
			environment.put(Context.SECURITY_PRINCIPAL, properties.getProperty("ldap.authdn"));
			environment.put(Context.SECURITY_CREDENTIALS, properties.getProperty("ldap.authpwd"));

			//Tuning parameters
			// pooling allowed
			environment.put("com.sun.jndi.ldap.connect.pool", "true");
			
			//en ms timeout temps attente maximum pour obtention connexion dans pool (2mn)
			environment.put("com.sun.jndi.ldap.connect.timeout", properties.getProperty("ldap.connect.timeout"));
			
			//en ms temps maximum pour une operation sur l'annuaire (2mn)
			environment.put("com.sun.jndi.ldap.read.timeout", properties.getProperty("ldap.read.timeout"));
			
			//System Properties
			
			//nombre de connexions initiales ouvertes pour un context ldap specifique
			System.setProperty("com.sun.jndi.ldap.connect.pool.initsize", properties.getProperty("ldap.pool.initsize"));
			
			//nombre maximum de connexions ouvertes pour un context ldap specifique 
			System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", properties.getProperty("ldap.pool.maxsize"));
			
			//nombre prefere de connexions ouvertes pour un context ldap specifique
			System.setProperty("com.sun.jndi.ldap.connect.pool.prefsize", properties.getProperty("ldap.pool.prefsize"));
			
			//en ms timeout a partir du quel les connexion idle sont supprimee du pool (1mn)
			System.setProperty("com.sun.jndi.ldap.connect.pool.timeout", properties.getProperty("ldap.pool.timeout"));
			
			System.setProperty("com.sun.jndi.ldap.connect.pool.authentication","simple");
			System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain");

			
			
			//create cache Manager
			
			//System property to avoid check update art startup
			//System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
			
			//CacheManager singletonManager = CacheManager.create();
		
			//build user cache
			
			//user size cache
			//int usercacheSize = Integer.valueOf(properties.getProperty("usercache.size"));
			
			//user ttl cache
			//int usercacheTtl = Integer.valueOf(properties.getProperty("usercache.ttl"));
			
			//create cache then add to manager
			//Cache cache = new Cache("userCache", usercacheSize, false, false, usercacheTtl, usercacheTtl);
			//singletonManager.addCache(cache);
			
			//then retrieve managed cache
			//Cache userCache = singletonManager.getCache("userCache");
	
			//no ctrl
			//instance = new LdapConnector(environment, properties.getProperty("ldap.root"),singletonManager,userCache);
			instance = new LdapConnector(environment, properties.getProperty("ldap.root"));
		} catch (Exception e) {

			log.error("cannot generate instance with properties : {}",e);
			throw new LdapException(e);
		}
		
		return instance;
	}

	
	/**
	 * Generic method for search in LDAP
	 * if LDAP Context is invalid regenerate automatically 
	 */
	private NamingEnumeration<SearchResult> search(String LdapSearchBase,String filter, SearchControls sc) throws LdapException {

		NamingEnumeration<SearchResult> res = null;

			try {
				//check if context valid
				if(!LDAPcontextValid){
					
					//check if synchronize required
					//regenerate context
					LDAPcontext = new InitialLdapContext(environment, null);
					
					log.debug("LDAP context reloaded");
					
					//context is OK 
					this.LDAPcontextValid = true;
					
					//counter to know if reload occurs; dump at closing
					LDAPcontextReloadCounter++;
				}
				
				//search...
				//if base not set use root
				LdapName base = LDAPSearchBaseDN;
				if(LdapSearchBase != null){
					base = new LdapName(LdapSearchBase);
				}

				log.debug("base {}",base);
				log.debug("filter {}",filter.toString());
				log.debug("sc {}",sc.getReturningAttributes().length);
				
				res = LDAPcontext.search(base, filter, sc);
				log.debug("res {}",res);
			} catch (Exception e) {
				log.warn("cannot search LDAP : {}",e.getMessage());
				if(e instanceof CommunicationException){
					communicationExceptionCoutner++;
					this.LDAPcontextValid = false;
				}else{
					throw new LdapException(e);
				}
			}
		return res;
	}


	/**
	 * Get all attributes from LDAP : unique method with 2 LDAp Requests
	 */
	public Map<String, List<String>> getAttributes(String uid, List<String> attributes) throws LdapException
	{

		//result of LDAP search for user
		NamingEnumeration<SearchResult> namingEnumeration = null;
		//result of LDAP search for dealer
		NamingEnumeration<SearchResult> namingEnumeration2 = null;
		
		//DN of user, should looks like  : cn=nicolas friant,ou=90399000,c=zz, o=renault
		LdapName ldapName = null;

		try {

			//check parameter uid Null
			if (uid == null) {
				log.warn("uid is Null");
				return null;
			}

			//check parameter attributes Null or size 0
			if ((attributes == null) || (attributes.size() == 0)){
				log.warn("attributes is Null or size 0");
				return null;
			}

			//check parameter attributes must be in list of TAG_ATTRIBUTE_MAP
			List<String> cleanAttributes = new ArrayList<String>();
			for (String attribute : attributes) 
			{ 
			    attribute = attribute.trim().toUpperCase();
			    if (!TAG_ATTRIBUTE_MAP.containsKey(attribute)){
			    	log.warn("attribute unknown : {}", attribute);
			    }else{
			    	cleanAttributes.add(attribute);
			    }
			} 
	
			//clean attributes must not be empty
			if (cleanAttributes.size() == 0){
				log.warn("clean attributes size 0");
				return null;
			}
	
			//get USER attributes
			List<String> cleanUserAttributes = new ArrayList<String>();
			for (String cleanAttribute : cleanAttributes) 
			{ 
			    if (cleanAttribute.startsWith("USER_")){
			    	cleanUserAttributes.add(cleanAttribute);
			    }
			} 
			log.debug("clean user attributes size : {}",cleanUserAttributes.size());
	
			//get DEALER attributes
			List<String> cleanDealerAttributes = new ArrayList<String>();
			for (String cleanAttribute : cleanAttributes) 
			{ 
			    if (cleanAttribute.startsWith("DEALER_")){
			    	cleanDealerAttributes.add(cleanAttribute);
			    }
			} 
			log.debug("clean dealer attributes size : {}",cleanDealerAttributes.size());
			
			//STEP 1 : look for user with uid
			String ldapFilterAsString = new String("(uid="+ uid +")");
			log.debug("LdapFilterAsString : {}",ldapFilterAsString);
			
			//create specific control 
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

			//set user attributes requested
			sc.setReturningAttributes(getLdapAttributes(cleanUserAttributes));
		
			//Execute method and get Enumeration of searchResult  
			namingEnumeration = search(null, ldapFilterAsString, sc);
			
			// result
			HashMap<String,List<String>> res = null;

			//check how many responses, should have only one...
			if(namingEnumeration == null){
				
				//should never happen
				log.warn("No user found for {}",uid);
				return null;
			}else{
				
				res  =  new HashMap<String,List<String>>();
				
				//counter on namingEnumeration,should be 1 no more
				int namingEnumerationIdx = 0;
				
				//iterate on naming Enumeration
				while (namingEnumeration.hasMoreElements()) {
					
					namingEnumerationIdx++;
					
					SearchResult searchResult = namingEnumeration.nextElement();
					
					//get DN
					ldapName = new LdapName(searchResult.getNameInNamespace());
					
					//deal with User attributes
					log.debug("getNameInNamespace : {}", searchResult.getNameInNamespace());
					Attributes atts = searchResult.getAttributes();
					log.debug("atts.size() : {}",atts.size());
					
					for(String cleanUserAttribute : cleanUserAttributes){
						res.put(cleanUserAttribute,getLowerStringValues(atts,TAG_ATTRIBUTE_MAP.get(cleanUserAttribute)));
					}
					
				}//while
				
				log.debug("namingEnumerationIdx : {}",namingEnumerationIdx);
				if(namingEnumerationIdx ==0){
					log.warn("No user found for {}",uid);
					return null;
				}else if (namingEnumerationIdx > 1){
					log.warn("too much users found for {} : {}",uid, namingEnumerationIdx);
				}
			}//userSearchResultNamingEnumeration == null
			
			
			//STEP 2 : look for dealer attributes
			String ldapFilterAsString2 = new String("(ou=*)");
			log.debug("LdapFilterAsString2 : {}",ldapFilterAsString);
			
			//create specific control 
			SearchControls sc2 = new SearchControls();
			sc2.setSearchScope(SearchControls.SUBTREE_SCOPE);

			//set dealer atrributes requested
			sc2.setReturningAttributes(getLdapAttributes(cleanDealerAttributes));
		
			//Execute method and get Enumeration of searchResult
			//set base at Dealer scope
			namingEnumeration2 = search(getParentDn(ldapName), ldapFilterAsString2, sc2);

			//iterate on naming Enumeration
			while (namingEnumeration2.hasMoreElements()) {
				
				
				SearchResult searchResult = namingEnumeration2.nextElement();
				
				//deal with Dealer attributes
				log.debug("getNameInNamespace : {}", searchResult.getNameInNamespace());
				Attributes atts = searchResult.getAttributes();
				log.debug("atts.size() : {}",atts.size());
				
				for(String cleanDealerAttribute : cleanDealerAttributes){
					res.put(cleanDealerAttribute,getLowerStringValues(atts,TAG_ATTRIBUTE_MAP.get(cleanDealerAttribute)));
				}
				
				//parse DN
				Map<String, String> ldapNameElements = getElements(ldapName);
				
				//extract Subsidiary from DN only if asked
				if(cleanDealerAttributes.contains("DEALER_SUBSIDIARY")){
					res.put("DEALER_SUBSIDIARY", Arrays.asList(ldapNameElements.get("c")));
				}
			}//while
			
			return res;
		
		} catch (Exception e) {
			log.warn("cannot get Attributes for uid {} : {}",uid, e.getMessage());	
			throw new LdapException(e);
		}finally{
			//free resource
			closeQuietly(namingEnumeration);
			closeQuietly(namingEnumeration2);
		}
	}

	/**
	 * get parent of DN
	 */
	protected String getParentDn(LdapName ldapName) {

		String res = null;

	    if (ldapName != null) {
            try {
				ldapName.remove(ldapName.size() - 1);
	            res = ldapName.toString();
			} catch (Exception e) {
				log.warn("Cannot get parent for {} : {} ",ldapName, e.getMessage());
			}
	    }
	    
	    return res;
	}

	/**
	 * get list of LDAP attributes regarding tags
	 * Keep only clean values (trim)
	 */
	private String[] getLdapAttributes(List<String> tags) {
		
		ArrayList<String> res = new ArrayList<String>(); 
		for(String tag : tags){
			if(TAG_ATTRIBUTE_MAP.containsKey(tag)){
				log.debug("tag {} : {} ",tag, TAG_ATTRIBUTE_MAP.get(tag));
				res.add(TAG_ATTRIBUTE_MAP.get(tag));
			}
		}		

		return res.toArray(new String[res.size()]);
	}

	/**
	 * retrieves list values removing null String and length = 0 String 
	 * Keep only clean values (trim)
	 */
	public static List<String> getStringValues(Attributes attributes,String attributeName) {

		List<String> res = null;
		
		try{
			if((attributes != null) && (attributes.size() > 0)){
				
				res = new ArrayList<String>();
				
				Attribute attribute = attributes.get(attributeName);
				if((attribute != null) && (attributes.size() > 0)){
					NamingEnumeration<?> attributeValuesNamingEnumeration = attribute.getAll();
					while (attributeValuesNamingEnumeration.hasMore()){
						String runningAttributeValue = (String) attributeValuesNamingEnumeration.next();
						//add trim value
						if (!isEmpty(runningAttributeValue)) res.add(runningAttributeValue.trim());
					}
				}
			}
			
		}catch(Exception e){
			log.warn("cannot getStringValues attributes({}) attributeName({}) : {}",attributes.toString(),attributeName, e.getMessage());
			res = null;
		}
		
		return res;
	}

	
	/**
	 * retrieves list values removing null String and length = 0 String 
	 * Keep only clean values (trim)
	 * 
	 * set into upper case
	 */
	public static List<String> getUpperStringValues(Attributes attributes,String attributeName) {

		List<String> res = null;
		
		try{
			if((attributes != null) && (attributes.size() > 0)){
				
				res = new ArrayList<String>();
				
				Attribute attribute = attributes.get(attributeName);
				if((attribute != null) && (attributes.size() > 0)){
					NamingEnumeration<?> attributeValuesNamingEnumeration = attribute.getAll();
					while (attributeValuesNamingEnumeration.hasMore()){
						String runningAttributeValue = (String) attributeValuesNamingEnumeration.next();
						//add trim value
						if (!isEmpty(runningAttributeValue)) res.add(runningAttributeValue.trim().toUpperCase(Locale.ENGLISH));
					}
				}
			}
			
		}catch(Exception e){
			log.warn("cannot getUpperStringValues attributes({}) attributeName({}) : {}",attributes.toString(),attributeName, e.getMessage());
			res = null;
		}
		
		return res;
	}

	/**
	 * retrieves list values removing null String and length = 0 String 
	 * Keep only clean values (trim)
	 * 
	 * set into upper case
	 */
	public static List<String> getLowerStringValues(Attributes attributes,String attributeName) {

		List<String> res = null;
		
		try{
			if((attributes != null) && (attributes.size() > 0)){
				
				res = new ArrayList<String>();
				
				Attribute attribute = attributes.get(attributeName);
				if((attribute != null) && (attributes.size() > 0)){
					NamingEnumeration<?> attributeValuesNamingEnumeration = attribute.getAll();
					while (attributeValuesNamingEnumeration.hasMore()){
						String runningAttributeValue = (String) attributeValuesNamingEnumeration.next();
						//add trim value
						if (!isEmpty(runningAttributeValue)) res.add(runningAttributeValue.trim().toLowerCase(Locale.ENGLISH));
					}
				}
			}
			
		}catch(Exception e){
			log.warn("cannot getLowerStringValues attributes({}) attributeName({}) : {}",attributes.toString(),attributeName, e.getMessage());
			res = null;
		}
		
		return res;
	}

	/**
	 * retrieves value removing null String and length = 0 String 
	 * Keep only clean value (trim)
	 * if more than 1 value, log and send 1st one 
	 */
	public static String getStringValue(Attributes attributes,String attributeName) {
		
		String res = null;
		
		List<String> attributeValues = LdapConnector.getStringValues(attributes, attributeName);
		
		if ((attributeValues != null) && (attributeValues.size() != 0)){
			if (attributeValues.size() > 1){
				log.warn("more than one value({}) found in LDAP for attributeName({}) : {};choose the first one : {} ",attributeValues.size(),attributeName,attributeValues,attributeValues.get(0));
				res = attributeValues.get(0);
			}else{
				res = attributeValues.get(0);
			}
		}

		return res;
	}


	/**
	 * retrieves value removing null String and length = 0 String 
	 * Keep only clean value (trim)
	 * if more than 1 value, log and send 1st one 
	 * 
	 * set into lower case
	 */
	public static String getLowerStringValue(Attributes attributes,String attributeName) {
		
		String res = null;
		
		List<String> attributeValues = LdapConnector.getStringValues(attributes, attributeName);
		
		if ((attributeValues != null) && (attributeValues.size() != 0)){
			if (attributeValues.size() > 1){
				log.warn("more than one value({}) found in LDAP for attributeName({}) : {};choose the first one : {} ",attributeValues.size(),attributeName,attributeValues,attributeValues.get(0));
				res = attributeValues.get(0);
			}else{
				res = attributeValues.get(0);
			}
		}

		if (res != null) res = res.toLowerCase(Locale.ENGLISH);
		return res;
	}

	/**
	 * retrieves value removing null String and length = 0 String 
	 * Keep only clean value (trim)
	 * if more than 1 value, log and send 1st one 
	 * 
	 * set into upper case
	 */
	public static String getUpperStringValue(Attributes attributes,String attributeName) {
		
		String res = null;
		
		List<String> attributeValues = LdapConnector.getStringValues(attributes, attributeName);
		
		if ((attributeValues != null) && (attributeValues.size() != 0)){
			if (attributeValues.size() > 1){
				log.warn("more than one value({}) found in LDAP for attributeName({}) : {};choose the first one : {} ",attributeValues.size(),attributeName,attributeValues,attributeValues.get(0));
				res = attributeValues.get(0);
			}else{
				res = attributeValues.get(0);
			}
		}

		if (res != null) res = res.toUpperCase(Locale.ENGLISH);
		return res;
	}

	/**
	 * parse ldapName to extract all elements as Map (o, c, ou, cn....) 
	 */
	public static Map<String,String> getElements(LdapName ldapName) {
		
		Map<String,String> res = null;

		//ldapName must exist
		if (ldapName != null) {
			
			res = new HashMap<String, String>();

			List<Rdn> rdnList = ldapName.getRdns();

			if(rdnList != null){
				//get size on iterate on it
				for (int idx = 0; idx <rdnList.size(); idx++) {
					Rdn rdn = rdnList.get(idx);
					log.debug("{} : {}",idx,rdnList.get(idx));
					//ldapName.get(idx);
					res.put(rdn.getType().toLowerCase(Locale.ENGLISH), rdn.getValue().toString());
				}// for
			}//rdnList != null
		}//ldapName != null
		return res;
	}

	
	
	/**
	 * check if string is empty or not
	 * useful to extract clean data from LDAP
	 */
	private static boolean isEmpty(String string) {
		
		if ((string != null) && (string.trim().length() != 0))
			return false;
		else
			return true;
	}

	/**
	 * Static method to clean up resources
	 */
	public static void closeQuietly(final LdapConnector instance) {

		try {
			if (instance != null) {
				instance.close();
			}
		} catch (Exception e) {
			log.debug("cannot close quietly : {}",e.getMessage());
		}
	}

	/**
	 * Static method to clean up resources
	 */
	public static void closeQuietly(final NamingEnumeration<SearchResult> instance) {

		try {
			if (instance != null) {
				instance.close();
			}
		} catch (Exception e) {
			log.debug("cannot close quietly : {}",e.getMessage());
		}
	}

	private void close() {

		
		//LDAPContext
		log.info("LDAP context has been reloaded {} times",LDAPcontextReloadCounter);
		log.info("LDAP connection has been broken {} times",communicationExceptionCoutner);
		
		if(LDAPcontext != null){
			try {
				LDAPcontext.close();
			} catch (Exception e) {
				log.debug("cannot close {}",e.getMessage());
			}	
		}

		//user cache
//		if(userCache != null){
//			try {
//				log.debug("{}",userCache.getStatistics());
//				LiveCacheStatistics liveCacheStatistics = userCache.getLiveCacheStatistics();
//				log.debug("{}",liveCacheStatistics);
//			} catch (Exception e) {
//				log.debug("cannot get statistics from TDFUserCache {}",e.getMessage());
//			}	
//		}

		//cacheManager
//		if(cacheManager != null){
//			try {
//				cacheManager.shutdown();
//			} catch (Exception e) {
//				log.debug("cannot shutdown cacheManager {}",e.getMessage());
//			}	
//		}

	}

}
