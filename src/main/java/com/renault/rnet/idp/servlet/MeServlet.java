package com.renault.rnet.idp.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.MeBean;
import com.renault.rnet.idp.bean.ServiceProviderProperties;
import com.renault.rnet.idp.bean.ServiceProvidersList;
import com.renault.rnet.idp.ldap.LdapConnector;

/**
 * Display all the information about a UID in JSP page
 * @author rng
 *
 */
@WebServlet("/MeServlet")
public class MeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uid;
	private ServletContext servletC;
	private MeBean meBean = null;
	private String userUid ="";

	private final org.slf4j.Logger log = LoggerFactory.getLogger(MeServlet.class);
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

	
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		servletC = getServletContext();
		Principal _userPrincipal = request.getUserPrincipal();
		uid = _userPrincipal.getName();
		
		this.userUid = uid;
		log.debug("USER="+this.userUid+" UID ="+uid+" user principal="+_userPrincipal);
		
		// FOR TEST PURPOSE
		//uid = "a189564";
		//uid = "p083925";
		//System.out.println(servletC.getAttribute("ldapctx").toString());
		
//		if (servletC.getAttribute("ldapctx") instanceof LdapConnector) {
//			meBean = new MeBean(uid, (LdapConnector) servletC.getAttribute("ldapctx"));
//
//			String myLDAPAttributes = meBean.getMyLDAPAttributes(this.servletC);	
//			request.setAttribute("myUid", uid);
//			request.setAttribute("myInfo", myLDAPAttributes.trim().split(System.lineSeparator()));
//			log.debug("USER="+this.userUid+" User LDAP UID ="+uid);
//			//log.debug("USER="+this.userUid+" User LDAP info ="+myLDAPAttributes);
//		}else{
//			this.log.error("USER="+this.userUid+" fail to fetch ldap contex in Me servlet");
//			
//		}
//		
		String spAttr = request.getParameter("sp");
		if(spAttr!=null && !spAttr.equals("")){
			getSepcificAttrMe(request, request.getParameter("sp"));
		}else{
			request.setAttribute("myUid", uid);
			log.error("Service provider "+spAttr+" not exist");
			request.setAttribute("mySpecificInfo", "No SP");
		}
		
		
		
		this.getServletContext().getRequestDispatcher("/WEB-INF/me.jsp").forward(request, response);
		
	}
	
	private void getSepcificAttrMe(HttpServletRequest request,String sp){
		servletC = getServletContext();
		
		ServiceProvidersList spList = (ServiceProvidersList) servletC.getAttribute("handlers");
		
		ServiceProviderProperties serviceProviderProperties = spList.getSamlHandlers().get(sp);
		List<String> attributes = serviceProviderProperties.getAttributes();
		
		if (servletC.getAttribute("ldapctx") instanceof LdapConnector) {
			meBean = new MeBean(uid, (LdapConnector) servletC.getAttribute("ldapctx"));
			String myLDAPAttributes = meBean.getMySpecificLDAPAttr(this.servletC, attributes.toArray(new String[attributes.size()]));	
			request.setAttribute("myUid", uid);
			request.setAttribute("mySpecificInfo", myLDAPAttributes.trim().split(System.lineSeparator()));
			log.debug("USER="+this.userUid+" User LDAP UID ="+uid);
		}
		
		
	}
	

}
