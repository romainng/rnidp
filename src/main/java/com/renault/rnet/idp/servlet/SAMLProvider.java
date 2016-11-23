package com.renault.rnet.idp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
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

/**
 * Renault Identity Provider main Servlet
 * 
 * @author rng
 *
 */
@WebServlet(name = "SAMLProvider", loadOnStartup = 1, urlPatterns = { "/SAMLProvider" })
public class SAMLProvider extends HttpServlet {

	String realPath;
	private static final String SAML_RESPONSE = "SAMLResponse";
	SAMLHandler mySAMLHandler = null;
	private static final String RELAY_STATE = "RelayState";
	LdapConnector myLdapConnector = null;

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
	/**
	 * Log
	 */
	private final org.slf4j.Logger log = LoggerFactory.getLogger(SAMLProvider.class);

	static protected AtomicInteger reqCounter = new AtomicInteger(1);

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		 HttpSession session = request.getSession(true);
	     Locale locale = (Locale) Config.get(session, Config.FMT_LOCALE);
	 
	     if (locale == null) {
	         locale = request.getLocale();
	     }
	     if (request.getParameter("language") != null) {
	         locale = new Locale(request.getParameter("language"));
	         this.servletC.setAttribute("lang", request.getParameter("language"));
	     }
	     Config.set(session, Config.FMT_LOCALE, locale);
	     response.setLocale(locale);
	   
	     this.getServletContext().getRequestDispatcher("/index.jsp").forward(request,response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Principal _userPrincipal = request.getUserPrincipal();
		String uid = _userPrincipal.getName();
		// System.out.println("UID TEST="+uid);
		req_idx = reqCounter.getAndIncrement();

		// TODO renanme handlers context
		Object attribute = servletC.getAttribute("handlers");

		if (attribute != null && attribute instanceof ServiceProvidersList) {
			this.serviceProvidersList = (ServiceProvidersList) attribute;
		} else {
			log.error("Failed to load servlet context handlers");
		}

		log.debug("check the parameters in url");
		// PROD LINE
		URLParameters urlParameters = new URLParameters(request, uid);

		// for TEST PURPOSE
		// URLParameters urlParameters = new URLParameters(request,"a189564");

		// for TEST PURPOSE ---
		/*
		 * Parameter param = new Parameter(); param.setSp("salesforce");
		 * param.setUid("a189564"); urlParameters.setParameters(param);
		 */
		// -------------------------

		this.parameter = urlParameters.getParameters();

		log.debug("Parameters sp=" + this.parameter.getSp() + " ipn" + this.parameter.getUid());
		System.out.println("Parameters sp=" + this.parameter.getSp() + " ipn" + this.parameter.getUid());
		try {

			Map<String, List<String>> attributes = this.myLdapConnector.getAttributes(this.parameter.getUid(),
					this.serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()).getAttributes());

			if (attributes != null) {
				Set<String> keySet = attributes.keySet();

				Iterator<String> iterator = keySet.iterator();

				while (iterator.hasNext()) {
					String next = iterator.next();

					System.out.println("ATTR " + next + " = " + attributes.get(next));
				}

				Assertion assertion = mySAMLHandler.createAuthnAssertion(this.parameter.getUid(), attributes,
						this.serviceProvidersList.getSamlHandlers().get(this.parameter.getSp()));
				log.info("assertion : {} ", mySAMLHandler.prettyPrint(assertion));
				// TODO SAML TOKEN GEN
				System.out.println("ASSERTION=" + mySAMLHandler.prettyPrint(assertion));
				// create response
				Response responseSAML;

				responseSAML = mySAMLHandler.createResponse(assertion);
				log.info("response : {} ", mySAMLHandler.prettyPrint(responseSAML));
				signer.sign(responseSAML);
				System.out.println("REPONSE=" + mySAMLHandler.prettyPrint(responseSAML));

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
				// out.print("<HTML><BODY >");
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

			} else {
				log.error("User " + this.parameter.getUid() + " not registered");
			}

		} catch (LdapException e) {
			log.error("My LDAPConnector error");
			e.printStackTrace();
		} catch (MarshallingException e1) {
			log.error("My Marshalling error");
			e1.printStackTrace();
		} catch (TransformerException e1) {
			log.error("Transformer error");
			e1.printStackTrace();
		}

		// TODO HERE SAML TOKEN GENERATION
		ServiceProviderProperties spSAML = this.serviceProvidersList.getSamlHandlers().get(this.parameter.getSp());
		if (spSAML != null) {
			// SAMLHandler SAMLHandler = new SAMLHandler(spSAML);

		}

		// --------------------------------------
		request.setAttribute("handlers", this.serviceProvidersList);
		request.setAttribute("param", this.parameter);

		// request.setAttribute("idpconfig", this.idpConfig);
		// this.getServletContext().getRequestDispatcher("/index.jsp").forward(request,
		// response);
		// FOR TEST
		// this.getServletContext().getRequestDispatcher("/WEB-INF/logresults.jsp").forward(request,
		// response);
	}

	public void init() throws ServletException {
		System.out.println("INIT");
		log.info("Initialisation");

		servletC = getServletContext();
		realPath = servletC.getRealPath(File.separator);

		ResourcesPaths paths = new ResourcesPaths(realPath);
		Properties properties = new Properties();

		String prop_path = System.getProperty("app.properties");

		if ((prop_path == null) || (prop_path.length() == 0)) {
			try {
				properties.load(new FileInputStream(new File(paths.getConfigPath())));
			} catch (FileNotFoundException e) {
				log.error("File not foud");
				e.printStackTrace();
			} catch (IOException e) {
				log.error("Error loading properties");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		reqCounter = new AtomicInteger(1);

		// mySAMLHandler = null;
		try {
			mySAMLHandler = new SAMLHandler("issuerURL");
		} catch (Exception e) {
			log.error("cannot create SAMLHandler :  {}", e.getMessage());
		}

		log.debug("(Init) check all the Handlers process");

		this.serviceProvidersList = new ServiceProvidersList(realPath);

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
		log.debug("fetch identity provider configuration ");
		// this.idpConfig = new IdpConfig(realPath);

		try {
			this.signer = new OpenSAMLSigner(properties.getProperty("keystore.path"),
					properties.getProperty("keystore.password"), properties.getProperty("keystore.type"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			myLdapConnector = LdapConnector.getInstance(properties);
			servletC.setAttribute("ldapctx", myLdapConnector);

		} catch (Exception e) {
			log.error("cannot create context :  {}", e.getMessage());
		}

	}

}
