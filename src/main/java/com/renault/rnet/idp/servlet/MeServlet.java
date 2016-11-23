package com.renault.rnet.idp.servlet;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.renault.rnet.idp.bean.MeBean;
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

	
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		servletC = getServletContext();
		Principal _userPrincipal = request.getUserPrincipal();
		uid = _userPrincipal.getName();

		// FOR TEST PURPOSE
		//uid = "a189564";
		//uid = "p083925";
		System.out.println(servletC.getAttribute("ldapctx").toString());


		
		
		if (servletC.getAttribute("ldapctx") instanceof LdapConnector) {
			meBean = new MeBean(uid, (LdapConnector) servletC.getAttribute("ldapctx"));

			String myLDAPAttributes = meBean.getMyLDAPAttributes(this.servletC);
			System.out.println("UID="+uid);
			System.out.println("info="+myLDAPAttributes);
			request.setAttribute("myUid", uid);
			request.setAttribute("myInfo", myLDAPAttributes.trim().split(System.lineSeparator()));
		}
		this.getServletContext().getRequestDispatcher("/WEB-INF/me.jsp").forward(request, response);
		
	}

}
