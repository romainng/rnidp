package com.renault.rnet.idp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.Parameter;
import com.renault.rnet.idp.bean.ResourcesPaths;
import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.bean.ServiceProvidersList;
import com.renault.rnet.idp.controller.OpenSAMLSigner;
import com.renault.rnet.idp.controller.SAMLHandler;
import com.renault.rnet.idp.controller.URLParameters;
import com.renault.rnet.idp.ldap.LdapConnector;
import com.renault.rnet.idp.ldap.LdapException;
import com.renault.rnet.idp.log.LogManagement;

/**
 * Renault Identity Provider main Servlet
 * 
 * @author rng
 *
 */
@WebServlet(name = "SAMLProvider", loadOnStartup = 1, urlPatterns = { "/SAMLProvider" })
public class SAMLProvider extends HttpServlet {

	private String serviceProviderXMLPath = null;
	private LogManagement logManagement;

	private String realPath;
	private static final String SAML_RESPONSE = "SAMLResponse";
	private SAMLHandler mySAMLHandler = null;
	private static final String RELAY_STATE = "RelayState";
	private LdapConnector myLdapConnector = null;

	private String userUid = "";

	private String[] allRoles = { "admin", "logviewer", "audit" };

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Parameters set in the url
	 */
	private Parameter parameter = null;

	/**
	 * Map of all the service providers
	 */
	private ServiceProvidersList serviceProvidersList = null;

	/**
	 * ldap context
	 */
	// private LDAPContext ldapContex = null;

	/**
	 * Idp Configuration
	 */
	// IdpConfig idpConfig = null;

	private static int req_idx;
	private OpenSAMLSigner signer = null;
	private ServletContext servletC;
	private boolean postMethod = false;
	private String headerSP = null;
	/**
	 * Log
	 */
	private final org.slf4j.Logger log = LoggerFactory.getLogger(SAMLProvider.class);

	static protected AtomicInteger reqCounter = new AtomicInteger(1);

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession session = request.getSession(true);

		if ((String) request.getParameter("action") != null && !request.getParameter("action").equals("")) {
			switch ((String) request.getParameter("action")) {
			case "lang":
				if (session != null) {
					log.debug("HttpSession OK");
				} else {
					log.debug("HttpSession KO");
				}

				Locale locale = (Locale) Config.get(session, Config.FMT_LOCALE);

				if (locale == null) {
					log.debug("Failed get locale via session");
					locale = request.getLocale();
					if (locale == null) {
						log.debug("locale via request KO");
					} else {
						log.debug("locale via request OK");
					}
				}
				if (request.getParameter("language") != null) {
					log.debug("Locale language : " + request.getParameter("language"));
					locale = new Locale(request.getParameter("language"));
					this.servletC.setAttribute("lang", request.getParameter("language"));
				} else {
					log.debug("Locale language : unknow");
				}
				Config.set(session, Config.FMT_LOCALE, locale);
				response.setLocale(locale);
				return;
			case "userRole":
				JSONObject jsObj = new JSONObject();
				System.out.println("CHECK USER ROLE");
				List<String> userRoles = new ArrayList<String>(allRoles.length);
				for (String role : allRoles) {
					if (request.isUserInRole(role)) {
						userRoles.add(role);
					}
				}
				try {
					jsObj.put("roles", userRoles);
					response.setContentType("application/json");
					response.getWriter().write(jsObj.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			default:
				System.out.println("DEFAULT");
				break;
			}

			System.out.println("ENDING");
		}

		headerSP = request.getHeader("X-Vectury-sp");
		this.postMethod = true;
		doGet(request, response);

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Principal _userPrincipal = request.getUserPrincipal();
		String uid = null;
		if (_userPrincipal != null && !_userPrincipal.equals("")) {
			uid = _userPrincipal.getName();
			log.info("User principal =" + uid);
			this.userUid = uid;
		}

		req_idx = reqCounter.getAndIncrement();

		// TODO renanme handlers context
		Object attribute = servletC.getAttribute("handlers");

		if (attribute != null && attribute instanceof ServiceProvidersList) {
			this.serviceProvidersList = (ServiceProvidersList) attribute;
			log.debug("USER=" + this.userUid + " Servlet context handlers loaded : "
					+ this.serviceProvidersList.getSamlHandlers().size() + " item(s)");
		} else {
			log.error("USER=" + this.userUid + " Failed to load servlet context handlers");
		}

		log.debug("USER=" + this.userUid + " check the parameters in url");

		URLParameters urlParameters = new URLParameters(request, uid);

		this.parameter = urlParameters.getParameters();

		if (this.postMethod) {
			if (headerSP != null && !headerSP.equals(""))
				this.parameter.setSp(headerSP);
		}

		log.info(
				"USER=" + this.userUid + " Parameters sp=" + this.parameter.getSp() + " ipn" + this.parameter.getUid());
		try {

			Map<String, List<String>> attributes = this.myLdapConnector.getAttributes(this.parameter.getUid(),
					this.serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()).getAttributes());

			if (attributes != null) {
				// Create assertion
				Assertion assertion = mySAMLHandler.createAuthnAssertion(this.parameter.getUid(), attributes,
						this.serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()));
				log.info("USER=" + this.userUid + " generate assertion for " + this.parameter.getSp());
				log.info("USER=" + this.userUid + " assertion : {} ", mySAMLHandler.prettyPrint(assertion));

				// create response
				Response responseSAML;

				responseSAML = mySAMLHandler.createResponse(assertion);
				log.info("USER=" + this.userUid + " generate response for " + this.parameter.getSp());
				log.info("USER=" + this.userUid + " response : {} ", mySAMLHandler.prettyPrint(responseSAML));
				signer.sign(responseSAML);

				String SignedSamlRequestAsString = mySAMLHandler.prettyPrintSigneObject(responseSAML);
				// encode 64 using common apache codec
				String EncodedSignedSamlRequestAsString = Base64
						.encodeBase64String(SignedSamlRequestAsString.getBytes("UTF-8"));
				// log.info(logBegin + "response signed as string
				// encoded:"+EncodedSignedSamlRequestAsString);

				response.setStatus(HttpServletResponse.SC_OK);

				PrintWriter out = null;
				out = response.getWriter();

				out.print("<HTML><BODY onload=VECTURYFORM.submit();>");
				out.print("<FORM NAME=\"VECTURYFORM\" METHOD=\"POST\" ACTION=" + serviceProvidersList.getSamlHandlers()
						.get(this.parameter.getSp()).getConfirmationDataRecipient() + ">");
				out.print("<INPUT TYPE=hidden NAME=\"" + SAML_RESPONSE + "\" VALUE=\""
						+ EncodedSignedSamlRequestAsString + "\">");
				if (serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()).getRelaystate() != null) {
					out.print("<INPUT TYPE=hidden NAME=\"" + RELAY_STATE + "\" VALUE=\""
							+ serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()).getRelaystate()
							+ "\">");
				}
				out.print("</FORM>");
				out.print("</BODY></HTML>");
				out.close();

				// TODO URL TOKEN
				StringBuilder strb = new StringBuilder();
				strb.append("<HTML><BODY onload=VECTURYFORM.submit();>");
				strb.append("<FORM NAME=\"VECTURYFORM\" METHOD=\"POST\" ACTION=" + serviceProvidersList
						.getSamlHandlers().get(this.parameter.getSp()).getConfirmationDataRecipient() + ">");
				strb.append("<INPUT TYPE=hidden NAME=\"" + SAML_RESPONSE + "\" VALUE=\""
						+ EncodedSignedSamlRequestAsString + "\">");
				if (serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()).getRelaystate() != null) {
					strb.append("<INPUT TYPE=hidden NAME=\"" + RELAY_STATE + "\" VALUE=\""
							+ serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()).getRelaystate()
							+ "\">");
				}
				System.out.println();
				System.out.println(strb.toString());

			} else {
				log.error("User " + this.parameter.getUid() + " not registered");
			}

		} catch (LdapException e) {
			log.error("USER=" + this.userUid + " My LDAPConnector error");
			e.printStackTrace();
		} catch (MarshallingException e1) {
			log.error("USER=" + this.userUid + " My Marshalling error");
			e1.printStackTrace();
		} catch (TransformerException e1) {
			log.error("USER=" + this.userUid + " Transformer error");
			e1.printStackTrace();
		} finally {
			this.postMethod = false;
		}

		// --------------------------------------
		request.setAttribute("handlers", this.serviceProvidersList);
		request.setAttribute("param", this.parameter);

	}

	public void init() throws ServletException {
		System.out.println("initialisation");

		Properties properties = new Properties();

		String prop_path = System.getProperty("app.configurationFile");
		String logFilePath = System.getProperty("app.logFile");

		logManagement = new LogManagement(logFilePath);

		log.debug("---------- Begin Initialisation ---------------");

		servletC = getServletContext();

		if (servletC == null) {
			log.error("Failed to instanciate servlet context in initialisation process");
		}

		if ((prop_path == null) || (prop_path.length() == 0)) {

			realPath = servletC.getRealPath(File.separator);
			log.info("Get servlet context real path=", realPath);
			ResourcesPaths paths = new ResourcesPaths(realPath);
			log.debug("System property \"app.configurationFile\" not exist");

			try {
				properties.load(new FileInputStream(new File(paths.getConfigPath())));
				log.info("Properties file raw loaded at " + paths.getConfigPath());
			} catch (FileNotFoundException e) {
				log.error("File properties raw not found at " + paths.getConfigPath());
			} catch (IOException e) {
				log.error("Error loading properties");
			}

		} else {
			try {
				properties.load(new FileInputStream(prop_path));
				log.info("Properties file loaded at " + prop_path);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		reqCounter = new AtomicInteger(1);

		// mySAMLHandler = null;
		try {
			mySAMLHandler = new SAMLHandler("issuerURL");
			servletC.setAttribute("samlHandler", mySAMLHandler);
		} catch (Exception e) {
			log.error("cannot create SAMLHandler :  {}", e.getMessage());
		}

		// this.serviceProvidersList = new ServiceProvidersList(realPath);
		this.serviceProviderXMLPath = properties.getProperty("serviceProviders.path");
		log.debug("Path to service provider xml file set to " + this.serviceProviderXMLPath);

		servletC.setAttribute("serviceProviderXmlPath", this.serviceProviderXMLPath);
		servletC.setAttribute("logPath", logFilePath);
		this.serviceProvidersList = new ServiceProvidersList(this.serviceProviderXMLPath);

		if (this.serviceProvidersList == null) {
			log.error("Failed to get Service providers path file at " + this.serviceProviderXMLPath);
		} else {
			log.info("Service provider XML path =" + this.serviceProviderXMLPath);
		}

		int handlersCount = 1;
		for (Entry<String, ServiceProviderProperties> h : this.serviceProvidersList.getSamlHandlers().entrySet()) {
			log.debug("Handler number " + handlersCount + " service provider name=" + h.getKey());
			handlersCount++;
		}

		servletC.setAttribute("handlers", this.serviceProvidersList);
		// TODO free memory
		servletC.setAttribute("spitem", this.serviceProvidersList.getSamlHandlers().keySet());
		servletC.setAttribute("spmap", this.serviceProvidersList.getSamlHandlers());
		// TODO a revoir
		servletC.setAttribute("path", this.realPath);
		// log.debug("fetch identity provider configuration ");
		// this.idpConfig = new IdpConfig(realPath);

		try {
			log.info("Try get signer at " + properties.getProperty("keystore.path"));
			this.signer = new OpenSAMLSigner(properties.getProperty("keystore.path"),
					properties.getProperty("keystore.password"), properties.getProperty("keystore.type"));

			servletC.setAttribute("signer", this.signer);
		} catch (Exception e1) {
			log.info("Failed to get signer at " + properties.getProperty("keystore.path"));
			e1.printStackTrace();
		}

		try {
			myLdapConnector = LdapConnector.getInstance(properties);
			servletC.setAttribute("ldapctx", myLdapConnector);

		} catch (Exception e) {
			log.error("cannot instanciate LDAP via Porpoerties :  {}", e.getMessage());
		}

		log.debug("---------- End Initialisation ---------------");
	}

}
