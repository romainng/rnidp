package com.renault.rnet.idp.servlet;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.bean.ServiceProvidersList;

/**
 * this servlet redirect index page to the form 
 * TODO In the future we may need some auth to have the access to the form  
 * @author rng
 */
@WebServlet("/SpAdderServlet")
public class AccessFormSpServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	/**
	 * Log
	 */
	private final org.slf4j.Logger log = LoggerFactory.getLogger(AccessFormSpServlet.class);
	ServletContext sc;
	ServiceProvidersList handlers;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		

		log.debug("redirect to management form");
		sc = getServletContext();
		Object attribute = sc.getAttribute("handlers");

		if (attribute instanceof ServiceProvidersList && attribute != null) {
			this.handlers = (ServiceProvidersList) attribute;
			log.debug("Handlers fetched from context in AccesForm servlet");
		}else{
			log.error("Handlers not fetched from context in Form servlet");
		}
		sc.setAttribute("spitem", this.handlers.getSamlHandlers().keySet());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/management.jsp");
		dispatcher.forward(request, response);

	}

}
