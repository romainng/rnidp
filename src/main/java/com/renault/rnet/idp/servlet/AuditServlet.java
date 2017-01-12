package com.renault.rnet.idp.servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.renault.rnet.idp.bean.ServiceProvidersList;

public class AuditServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ServletContext servletC;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if ((String) request.getParameter("action") != null && !request.getParameter("action").equals("")) {
			switch ((String) request.getParameter("action")) {
			case "getSP":
				this.servletC = getServletContext();

				ServiceProvidersList spList = (ServiceProvidersList) this.servletC.getAttribute("handlers");
				Set<String> keySet = spList.getSamlHandlers().keySet();

				JSONObject jsObj = new JSONObject();
				try {
					jsObj.put("spList", keySet);
					response.setContentType("application/json");
					response.getWriter().write(jsObj.toString());
				} catch (JSONException e) {

				}

				return;
			}
		}

	
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/auditPage.jsp");
		dispatcher.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
	}

}
