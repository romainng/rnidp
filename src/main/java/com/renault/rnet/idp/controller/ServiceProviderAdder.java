package com.renault.rnet.idp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.ServiceProviderProperties;

/**
 * 
 * Update xml file with a new service provider 
 * @author rng
 *
 */
public class ServiceProviderAdder {

	private final static String SP_PROP = "sp.";
	private final static String ISSUER_URL = ".IssuerURL=";
	private final static String CONFIRMATION_DATA_RECIPIENT = ".ConfirmationDataRecipient=";
	private final static String AUDIENCE_URI = ".AudienceURI=";
	private final static String RELAY_STATE = ".RelayState=";
	private final static String CONFIRMATION_DATA_NOT_ON_OR_AFTER = ".ConfirmationDataNotOnOrAfter=";
	private final static String CONDITION_NOT_BEFORE = ".ConditionsNotBefore=";
	private final static String CONDITION_NOT_AFTER = ".ConditionsNotOnOrAfter=";
	private final static String ATTRIBUTES = ".Attributes=";
	private final static String PROFILES = ".Profiles=";
	private final static String ADMINISTRATORS = ".Administrators=";

	private static final String TEMPFILEPATH = "..\\myTempFile.txt";
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(ServiceProviderAdder.class);

	/**
	 * Be carefull to not add an already exsiting SP
	 * This method add a service provider in the file sp.properties
	 * @param propertiesPath
	 * @param spProp
	 */
	public static void addServiceProvider(String propertiesPath, ServiceProviderProperties spProp) {
		refreshList(propertiesPath, spProp.getSpName());
		
		try (FileWriter fw = new FileWriter(propertiesPath, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {

			StringBuilder propertiesString = new StringBuilder();
			outString(propertiesString, spProp.getSpName(), ISSUER_URL, spProp.getIssuerURL());
			outString(propertiesString, spProp.getSpName(), CONFIRMATION_DATA_RECIPIENT,
					spProp.getConfirmationDataRecipient());
			outString(propertiesString, spProp.getSpName(), AUDIENCE_URI, spProp.getAudienceURI());
			outString(propertiesString, spProp.getSpName(), RELAY_STATE, spProp.getRelaystate());
			outString(propertiesString, spProp.getSpName(), CONFIRMATION_DATA_NOT_ON_OR_AFTER,
					String.valueOf(spProp.getConfirmationDataNotOnOrAfter()));
			outString(propertiesString, spProp.getSpName(), CONDITION_NOT_BEFORE,
					String.valueOf(spProp.getConditionsNotBefore()));
			outString(propertiesString, spProp.getSpName(), CONDITION_NOT_AFTER,
					String.valueOf(spProp.getConditionsNotOnOrAfter()));
			outString(propertiesString, spProp.getSpName(), ATTRIBUTES, listToString(spProp.getAttributes()));
			outString(propertiesString, spProp.getSpName(), PROFILES, listToString(spProp.getProfiles()));
			outString(propertiesString, spProp.getSpName(), ADMINISTRATORS, listToString(spProp.getAdministrators()));

			out.println(propertiesString.toString());

		} catch (FileNotFoundException e1) {
			log.error("File not found at path: "+propertiesPath);
			e1.printStackTrace();
		} catch (IOException e) {
			log.error("IO error");
			e.printStackTrace();
		}

	}

	//TODO
	/**
	private static void deleteServiceProvider(String propertiesPath, String spName){
		
	}*/
	
	/**
	 * This method return a string that fit the sp.properties file syntax
	 * e.g : sp.ServiceProviderName.PropertyName=Value 
	 * @param strb
	 * @param spName
	 * @param field
	 * @param value
	 */
	private static void outString(StringBuilder strb, String spName, String field, String value) {

		strb.append(System.getProperty("line.separator"));
		strb.append(SP_PROP);
		strb.append(spName);
		strb.append(field);
		strb.append(value);
	}

	/**
	 * This method change a list to a string that fit the sp.properties file syntax
	 * @param list
	 * @return
	 */
	private static String listToString(List<String> list) {
		StringBuffer strb = new StringBuffer();
		if (list != null && list.size() > 0) {
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next();
				strb.append(next);
				strb.append("|");
			}
		} else {
			return "";
		}
		strb.deleteCharAt(strb.length() - 1);
		return strb.toString();
	}

	/**
	 * Udpate the line serviceProvider.list= when a new service provider is added
	 * @param path
	 * @param spName
	 */
	public static void refreshList(String path, String spName) {
		StringBuilder lineToChange = null;
		
		try {
			File inputFile = new File(path);
			File tempFile = new File(TEMPFILEPATH);

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String lineToRemove = "serviceProvider.list";
			String currentLine;
			
			while ((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();
				
				if (trimmedLine.split("=")[0].equals(lineToRemove)){
					if(trimmedLine.split("=")[1] != null || trimmedLine.split("=")[1].equals("")){
						
						lineToChange = new StringBuilder();
						lineToChange.append(trimmedLine.split("=")[0]);
						lineToChange.append("=");
						lineToChange.append(trimmedLine.split("=")[1]);
						lineToChange.append("|");
						lineToChange.append(spName);
						writer.write(lineToChange.toString() + System.getProperty("line.separator"));
						log.debug("Writing new Service providers list :"+lineToChange.toString());
					}
					continue;
				}
					
				writer.write(currentLine + System.getProperty("line.separator"));
			}
			writer.close();
			reader.close();
			inputFile.delete();
			if(tempFile.renameTo(inputFile)){
				log.info("file renamed"+inputFile.getName());
			}else{
				log.error("Failed to rename");
			}

			
		} catch (Exception e) {

		}

	}

}
