package com.renault.rnet.idp.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.bean.ServiceProvidersList;
import com.renault.rnet.idp.controller.ServiceProviderXMLManage;

/**
 * Manage the jsp SP management page
 * 
 * @author rng
 *
 */
@WebServlet("/ManagementSpServlet")
public class ManagementSpServlet extends HttpServlet {

	private ServiceProvidersList handlers = null;
	private ServletContext sc;
	// private ResourcesPaths paths;
	/**
	 * 
	 */
	/**
	 * Log
	 */
	private final org.slf4j.Logger log = LoggerFactory.getLogger(ManagementSpServlet.class);

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// define which action to launch
		String action = request.getParameter("action");
		sc = getServletContext();

		Object attribute = sc.getAttribute("handlers");
		// paths = new ResourcesPaths((String)
		// sc.getAttribute("serviceProviderXmlPath"));
		if (attribute instanceof ServiceProvidersList && attribute != null) {
			this.handlers = (ServiceProvidersList) attribute;
			log.debug("Handlers fetched from context in ManagementSpServlet servlet");
		} else {
			log.error("Handlers not fetched from context in ManagementSpServlet servlet");

		}

		if (action.equals("add") || action.equals("ajouter")) {
			log.info("ACTION = add process");
			addProccess(request, false);
		} else if ((action.equals("modify") || action.equals("modifier"))) {
			log.info("ACTION = modify process");
			addProccess(request, true);
		} else if ((action.equals("delete") || action.equals("supprimer"))) {
			log.info("ACTION = delete process");
			String spnameToDel = (String) request.getParameter("sptodel");
			if (spnameToDel != null && !spnameToDel.equals("") && !spnameToDel.equals("-Select-")) {
				delProcess(spnameToDel);
			} else {
				log.error("FAILED to delete sp");
			}
		} else {
			if (action != null) {
				log.error("ACTION : no action available for " + action);
			}

		}

		sc.setAttribute("spitem", this.handlers.getSamlHandlers().keySet());
		this.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

	}

	private void delProcess(String spToDel) {
		// ServiceProviderXMLManage spXML = new
		// ServiceProviderXMLManage(paths.getPropertiesPath());
		ServiceProviderXMLManage spXML = new ServiceProviderXMLManage(
				(String) this.sc.getAttribute("serviceProviderXmlPath"));
		spXML.delSpXML(spToDel);

		if (this.handlers != null) {
			this.handlers.getSamlHandlers().remove(spToDel);
			sc.setAttribute("handlers", this.handlers);
			log.info("Succes " + spToDel + " deleted.");
		} else {
			log.error("Failed to delete Service provider to handlers");
		}

	}

	private void addProccess(HttpServletRequest request, boolean isModify) {
		ServiceProviderProperties createServiceProviderProperties = null;
		String spname = null;
		String contextPath = (String) sc.getAttribute("serviceProviderXmlPath");
		this.handlers = new ServiceProvidersList(contextPath);
		if (isModify) {
			log.debug("Modify a Service provider");
			spname = (String) request.getParameter("spname");
			log.debug("entry service provider name to modify=" + spname);
		} else {
			log.debug("Create a new Service provider");
			spname = (String) request.getParameter("spname");
			log.debug("entry service provider name=" + spname);
		}
		String issuerirl = request.getParameter("issuerurl");
		log.debug("entry issuer url=" + issuerirl);

		String datarecipient = request.getParameter("datarecipient");
		log.debug("entry data recipient=" + datarecipient);

		String audienceuri = request.getParameter("audienceuri");
		log.debug("entry audience uri=" + audienceuri);

		String relaystate = request.getParameter("relaystate");
		log.debug("entry relay state=" + relaystate);
		// String confirmationdata = request.getParameter("confirmationdata");
		int confirmationdata = 0;
		log.debug("entry confirmation data=" + confirmationdata);
		String conditionsnotbefore = request.getParameter("conditionsnotbefore");
		log.debug("entry conditions not before=" + conditionsnotbefore);
		String conditionnotonorafter = request.getParameter("conditionnotonorafter");
		log.debug("entry condition not on or after=" + conditionnotonorafter);

		String[] atts = request.getParameterValues("spAttrib");
		List<String> listAtts = new ArrayList<String>();

		if (atts != null) {
			StringBuilder strbAtt = new StringBuilder();
			for (String att : atts) {
				if (!att.trim().equals("")) {
					listAtts.add(att.trim());
					strbAtt.append(att.trim());
					strbAtt.append(" ");
				}
			}
			log.debug("entry Attributes " + listAtts.size() + " item(s) = " + strbAtt.toString());
		}

		String profiles = request.getParameter("profils");
		log.debug("entry profiles =" + profiles);
		List<String> listProfiles = new ArrayList<String>();
		String[] splitProfiles = profiles.split(",| ");

		if (splitProfiles.length > 0) {
			StringBuilder strbPro = new StringBuilder();
			for (String profile : splitProfiles) {

				if (!profile.trim().equals("") && !profile.trim().equals(" ")) {
					listProfiles.add(profile.trim());
					strbPro.append(profile.trim());
					strbPro.append(" ");
				}
			}
			log.debug("entry profiles " + listProfiles.size() + " item(s) =" + strbPro.toString());
		} else {
			log.debug("entry profiles empty");
		}

		String admins = request.getParameter("admins");
		log.debug("entry admins =" + admins);
		List<String> listAdmins = new ArrayList<String>();
		String[] splitAdmins = admins.split(",| ");

		if (splitAdmins.length > 0) {
			StringBuilder strbAdm = new StringBuilder();
			for (String admin : splitAdmins) {
				if (!admin.trim().equals("")) {
					listAdmins.add(admin.trim());
					strbAdm.append(admin.trim());
					strbAdm.append(" ");
				}
			}
			log.debug("entry admins " + listAdmins.size() + " item(s) = " + strbAdm.toString());
		} else {
			log.debug("entry admins empty");
		}

		// boolean which trigger or not the add process. False means some
		// problems in values.
		// in this way we can fetch all problems
		boolean validSp = true;
		if (!validSPName(spname, isModify)) {
			validSp = false;
		}
		if (!validIssuer(issuerirl)) {
			validSp = false;
		}
		if (!validDataRec(datarecipient)) {
			validSp = false;
		}
		if (!validAudience(audienceuri)) {
			validSp = false;
		}
		if (!validRelay(relaystate)) {
			validSp = false;
		}
		if (!validProfiles(listProfiles)) {
			validSp = false;
		}
		if (!validAttributes(listAtts)) {
			validSp = false;
		}
		if (!validAdmin(listAdmins)) {
			validSp = false;
		}
		if (!validConditionAfter(conditionnotonorafter)) {
			validSp = false;
		}
		if (!validConditionBefore(conditionsnotbefore)) {
			validSp = false;
		}

		if (validSp) {
			createServiceProviderProperties = new ServiceProviderProperties(spname.toLowerCase());
			createServiceProviderProperties.setIssuerURL(issuerirl);
			createServiceProviderProperties.setConfirmationDataRecipient(datarecipient);
			createServiceProviderProperties.setAudienceURI(audienceuri);
			createServiceProviderProperties.setRelaystate(relaystate);
			createServiceProviderProperties.setProfiles(listProfiles);
			createServiceProviderProperties.setAttributes(listAtts);
			createServiceProviderProperties.setAdministrators(listAdmins);
			// TODO field supp
			createServiceProviderProperties.setConfirmationDataNotOnOrAfter(Integer.valueOf(confirmationdata));
			createServiceProviderProperties.setConditionsNotBefore(Integer.valueOf(conditionsnotbefore));
			createServiceProviderProperties.setConditionsNotOnOrAfter(Integer.valueOf(conditionnotonorafter));

			if (isModify) {
				// ServiceProviderXMLManage spXML = new
				// ServiceProviderXMLManage(paths.getPropertiesPath());
				ServiceProviderXMLManage spXML = new ServiceProviderXMLManage(
						(String) this.sc.getAttribute("serviceProviderXmlPath"));
				spXML.modifySPXML(createServiceProviderProperties);

				if (this.handlers != null) {
					this.handlers.getSamlHandlers().remove((createServiceProviderProperties.getSpName()));
					this.handlers.getSamlHandlers().put((createServiceProviderProperties.getSpName()),
							createServiceProviderProperties);
					sc.setAttribute("handlers", this.handlers);
				} else {
					log.error("Failed to delete Service provider to handlers");
				}
			} else {

				this.handlers.putSamlHandler(createServiceProviderProperties.getSpName(),
						createServiceProviderProperties);

				// ServiceProviderAdder.addServiceProvider(ResourcesPaths.getPropertiesPath(),createServiceProviderProperties);
				// ServiceProviderXMLManage spXML = new
				// ServiceProviderXMLManage(paths.getPropertiesPath());
				ServiceProviderXMLManage spXML = new ServiceProviderXMLManage(
						(String) this.sc.getAttribute("serviceProviderXmlPath"));
				spXML.addSPXML(createServiceProviderProperties);
				log.info("SUCCES create Service provider :" + createServiceProviderProperties.getSpName());

				if (this.handlers != null) {
					this.handlers.getSamlHandlers().put(createServiceProviderProperties.getSpName(),
							createServiceProviderProperties);
					sc.setAttribute("handlers", this.handlers);
				} else {
					log.error("Failed to add Service provider to handlers");
				}

			}

		} else {
			log.error("ERROR : Service provider not added, check log for details.");
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String spChoosen = request.getParameter("spChoosen");
		sc = getServletContext();
		switch (action) {
		case "getVal":
			JSONObject jsObj = new JSONObject();

			Object attribute = sc.getAttribute("handlers");
			ServiceProvidersList spList = null;
			if (attribute instanceof ServiceProvidersList && attribute != null) {
				spList = (ServiceProvidersList) attribute;
				ServiceProviderProperties sppropAjax = spList.getSamlHandlers().get(spChoosen);

				if (sppropAjax != null) {
					try {
						jsObj.put("audience", sppropAjax.getAudienceURI());
						jsObj.put("dataRec", sppropAjax.getConfirmationDataRecipient());
						jsObj.put("relay", sppropAjax.getRelaystate());
						jsObj.put("profils", sppropAjax.getProfiles().toString().trim().substring(1,
								sppropAjax.getProfiles().toString().length() - 1));
						jsObj.put("admins", sppropAjax.getAdministrators().toString().trim().substring(1,
								sppropAjax.getAdministrators().toString().length() - 1));
						jsObj.put("attributes", sppropAjax.getAttributes());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					response.setContentType("application/json");
					response.getWriter().write(jsObj.toString());
				}

			} else {
				log.error("Failed to retreive servlet contex");

			}
			break;
		}

	}

	private boolean validSPName(String spname, boolean isMod) {

		if (spname == null) {
			log.error("SP not added : SP name null.");
			return false;
		} else if (spname.equals("") && spname.equals("-Select-")) {
			log.error("SP not added : service provider name: \"" + spname + "\" not valid.");
			return false;
		} else if (!isMod && this.handlers.getSamlHandlers().containsKey(spname)) {
			log.error("SP not added : service provider name: \"" + spname + "\" already exist.");
			return false;
		}

		return true;
	}

	private boolean validIssuer(String issuer) {
		if (issuer == null) {
			log.error("SP not added : Issuer name null.");
			return false;
		} else if (!issuer.equals("VecturyDealerCommunity")) {
			log.error("SP not added : Issuer name " + issuer + " not match \"VecturyDealerCommunity\".");
			return false;
		}
		return true;
	}

	private boolean validDataRec(String dataRec) {
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

		Pattern patt = Pattern.compile(regex);
		Matcher matcher = patt.matcher(dataRec);

		if (dataRec == null) {
			log.error("SP not added : Data recipient name null.");
			return false;
		} else if (!matcher.matches()) {
			log.error("SP not added : Data recipient name " + dataRec + " not valid URL.");
			return false;
		}

		return true;
	}

	private boolean validAudience(String audi) {
		if (audi == null) {
			log.error("SP not added : Audience uri name null.");
			return false;
		}
		return true;
	}

	private boolean validRelay(String relay) {
		if (relay == null) {
			log.error("SP not added : relay state name null.");
			return false;
		}
		return true;
	}

	private boolean validConditionAfter(String condition) {
		if (condition == null) {
			log.error("SP not added : confirmation data after value null.");
			return false;
		} else if (condition.equals("")) {
			log.error("SP not added : confirmation data after value empty.");
			return false;
		} else if (!(Integer.valueOf(condition) instanceof Integer)) {
			log.error("SP not added : confirmation data after value " + condition + " not valid.");
			return false;
		} else if (Integer.valueOf(condition) != 30) {
			log.error("SP not added : confirmation data after value " + condition + " not equals to 30.");
			return false;
		}
		return true;
	}

	private boolean validConditionBefore(String condition) {
		if (condition == null) {
			log.error("SP not added : confirmation data before value null.");
			return false;
		} else if (condition.equals("")) {
			log.error("SP not added : confirmation data before value empty.");
			return false;
		} else if (!(Integer.valueOf(condition) instanceof Integer)) {
			log.error("SP not added : confirmation data after before " + condition + " not valid.");
			return false;
		} else if (Integer.valueOf(condition) != 10) {
			log.error("SP not added : confirmation data after before " + condition + " not equals to 10.");
			return false;
		}
		return true;
	}

	private boolean validAttributes(List<String> atts) {
		if (atts == null) {
			log.error("SP not added : attributes list null.");
			return false;
		}
		return true;
	}

	private boolean validAdmin(List<String> admins) {
		if (admins == null) {
			log.error("SP not added : admins list null.");
			return false;
		}
		return true;
	}

	private boolean validProfiles(List<String> profiles) {
		if (profiles == null) {
			log.error("SP not added : profiles list null.");
			return false;
		}
		return true;
	}

}
