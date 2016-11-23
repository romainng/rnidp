package com.renault.rnet.idp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import com.renault.rn.config.RNConfException;
import com.renault.rn.config.RNConfig;
import com.renault.rnet.idp.bean.ResourcesPaths;
import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.controller.ServiceProviderXMLManage;
import com.renault.rnet.idp.controller.ServiceProvidersParser;

public class Tests {

	/**
	 * TEST ONLY ON LOCAL MACHINE
	 */

	private static final String pathex = "D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\test\\resources\\config\\spTest.properties";
	private static final String pathXML = "D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\test\\resources\\config\\serviceproviders.xml";

	/**
	 * if fail, check properties file paths in ResourcesPaths class
	 */

	
	//@Test
	public void deleteElementXML(){
		String del = "RitoKun";
		ServiceProviderXMLManage xmlManage = new ServiceProviderXMLManage(pathXML);
		xmlManage.delSpXML(del);
	}
	
	//@Test
	public void writeXMLProperties() {

		ServiceProviderProperties spp = new ServiceProviderProperties("RitoKun");

		
		spp.setAudienceURI("URI.RITOKUN");
		spp.setConfirmationDataRecipient("RECIPIENT.RITOKUN");
		spp.setRelaystate("RELAY.RITOKUN");
		spp.setIssuerURL("ISSUER.RITO");
		spp.setConditionsNotOnOrAfter(30);
		spp.setConditionsNotBefore(2);
		List<String> attList = new ArrayList<String>();
		attList.add("USER_NAME");
		attList.add("USER_GIVENNAME");
		spp.setAttributes(attList);
		List<String> proList = new ArrayList<String>();
		//proList.add("profil1");
		//proList.add("profilRITO");
		spp.setProfiles(proList);
		List<String> adminList = new ArrayList<String>();
		adminList.add("a189564");
		adminList.add("ji16274");
		spp.setAdministrators(adminList);

		ServiceProviderXMLManage spWriter = new ServiceProviderXMLManage(pathXML);
		spWriter.addSPXML(spp);
		//spWriter.write();

	}
	
	
	//@Test
	public void modifXML() {

		ServiceProviderProperties spp = new ServiceProviderProperties("RitoKun");

		
		spp.setAudienceURI("URI.RITOKUN.modif");
		spp.setConfirmationDataRecipient("RECIPIENT.RITOKUN.modif");
		spp.setRelaystate("RELAY.RITOKUN.modif");
		spp.setIssuerURL("ISSUER.RITO.modif");
		spp.setConditionsNotOnOrAfter(30);
		spp.setConditionsNotBefore(2);
		List<String> attList = new ArrayList<String>();
		attList.add("USER_NAME");
		attList.add("USER_GIVENNAME");
		spp.setAttributes(attList);
		List<String> proList = new ArrayList<String>();
		//proList.add("profil1");
		//proList.add("profilRITO");
		spp.setProfiles(proList);
		List<String> adminList = new ArrayList<String>();
		adminList.add("a189564");
		adminList.add("ji16274");
		spp.setAdministrators(adminList);

		ServiceProviderXMLManage spWriter = new ServiceProviderXMLManage(pathXML);
		spWriter.modifySPXML(spp);
		//spWriter.write();

	}

	/*
	 * @Test public void readPropertiesFileShouldNotNull() { String prop_path =
	 * ResourcesPaths.getPropertiesPath(); RNConfig properties = null; try {
	 * properties = new RNConfig(prop_path); } catch (RNConfException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * assertNotNull(properties);
	 * 
	 * }
	 */

	/**
	 * if fail, check config file paths in ResourcesPaths class
	 */
	//@Test
	public void readConfigFileShouldNotNull() {
		ResourcesPaths paths = new ResourcesPaths("D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\main");
		String config_path = paths.getConfigPath();
		RNConfig config = null;
		try {
			config = new RNConfig(config_path);
		} catch (RNConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull(config);
	}

	//@Test
	public void testNumberSpShouldEquals2() {
		final String SP_LIST_QUERY = "serviceProvider.list";
		String prop_path = pathex;
		RNConfig prop = null;
		try {
			prop = new RNConfig(prop_path);
		} catch (RNConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String testNbSp = prop.getString(SP_LIST_QUERY, null);

		String[] listSAMLHandlers = testNbSp.split("\\|");

		assertEquals("Number of sp should be = 2 (eg. salesforce and test)", 2, listSAMLHandlers.length);
		assertEquals("First sp should be = salesforce", "salesforce", listSAMLHandlers[0]);
		assertEquals("Second sp should be = test", "test", listSAMLHandlers[1]);
	}

	//@Test
	public void fetchSpTestPropertiesShouldReturnTestProp() {
		ResourcesPaths paths = new ResourcesPaths("D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\main");
		String prop_path = paths.getPropertiesPath();

		RNConfig properties = null;
		String spName = "test";
		ServiceProviderProperties spProperties = null;
		try {
			properties = new RNConfig(prop_path);
			spProperties = new ServiceProviderProperties(spName, properties);
		} catch (RNConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals("issuerURL for sp test should be = TestDealerCommunity", "TestDealerCommunity",
				spProperties.getIssuerURL());
		assertEquals("ConfirmationDataRecipient for sp test should be = https://login.test.com?so=00Dw0000000Cxsv",
				"https://login.test.com?so=00Dw0000000Cxsv", spProperties.getConfirmationDataRecipient());
		assertEquals("AudienceURI for sp test should be = https://myEntityId.test.com", "https://myEntityId.test.com",
				spProperties.getAudienceURI());
		assertEquals("RelayState for sp test should be = http://myrelaystate.test.com", "http://myrelaystate.test.com",
				spProperties.getRelaystate());
		assertEquals("ConfirmationDataNotOnOrAfter for sp test should be = 30", 30,
				spProperties.getConfirmationDataNotOnOrAfter());
		assertEquals("ConditionsNotBefore for sp test should be = 1", 1, spProperties.getConditionsNotBefore());
		assertEquals("ConditionsNotOnOrAfter for sp test should be = 30", 30, spProperties.getConditionsNotOnOrAfter());
		// assertEquals("Attributes for sp test should be =
		// [USER_NAME|USER_GIVENNAME]",
		// "[USER_NAME|USER_GIVENNAME]",spProperties.getAttributes());
		// assertNotEquals("Profiles for sp test should be null", "null",
		// spProperties.getProfiles());
		// assertEquals("administrators for sp test should be = a189564",
		// "a189564",spProperties.getAdministrators());

	}

//	@Test
	public void testParser() {
		ServiceProvidersParser spp = new ServiceProvidersParser();
		ResourcesPaths paths = new ResourcesPaths("D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\main");
		String prop_path = paths.getPropertiesXmlPath();
		spp.parseXML(prop_path);
	}
	/*
	 * @Test public void testWriteFile() { String propertiesPath =
	 * "D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\test\\resources\\config\\spTest.properties";
	 * 
	 * 
	 * ServiceProviderProperties properties = new
	 * ServiceProviderProperties("junitSP");
	 * 
	 * properties.setIssuerURL("JunitDealerCommunity");
	 * properties.setConfirmationDataRecipient(
	 * "https://Junit.test.com?so=00Dw0000000Cxsv");
	 * properties.setAudienceURI("https://Junit.test.com");
	 * properties.setRelaystate("http://Junit.test.com");
	 * properties.setConfirmationDataNotOnOrAfter(99);
	 * properties.setConditionsNotBefore(7);
	 * properties.setConditionsNotOnOrAfter(100); List<String> listAtt = new
	 * ArrayList<String>(); listAtt.add("USER_NAME");
	 * properties.setAttributes(listAtt); List<String> listAdmin = new
	 * ArrayList<String>(); listAdmin.add("a189564");
	 * properties.setAdministrators(listAdmin);
	 * 
	 * ServiceProviderAdder.addServiceProvider(propertiesPath, properties);
	 * 
	 * }
	 */
	/*
	 * @Test public void testRNFile(){ String propertiesPath =
	 * "D:\\LocalData\\p083925\\Desktop\\renaultMaven\\renaultIdp\\src\\test\\resources\\config\\spTest.properties";
	 * 
	 * ServiceProviderAdder.refreshList(propertiesPath,"JunitTest");
	 * 
	 * }
	 */

}