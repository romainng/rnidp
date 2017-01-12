package com.renault.rnet.idp.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.renault.rnet.idp.log.LogManagement;

public class LogServlet extends HttpServlet {

	public final String logPath = "application.log";
	private ServletContext servletC;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final org.slf4j.Logger log = LoggerFactory.getLogger(LogServlet.class);

	private LogManagement logManagement = null;

	private Object[] tempLog = null;

	private String userUid = "";
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String action = request.getParameter("action");
		switch (action) {
		case "refreshLog":
			if (this.logManagement != null) {
				JSONObject jsObj = new JSONObject();
				try {

					if (this.tempLog == null) {
						jsObj.put("newLogs", this.logManagement.getLogListFormat().toArray());
						jsObj.put("mod", true);
					} else {
						if (!Arrays.deepEquals(this.tempLog, this.logManagement.getLogListFormat().toArray())) {
							// log.debug("Ajax refresh log invoked");
							jsObj.put("newLogs", this.logManagement.getLogListFormat().toArray());
							jsObj.put("mod", true);
						} else {
							jsObj.put("mod", false);
						}
					}

					
					this.tempLog = this.logManagement.getLogListFormat().toArray();
					response.setContentType("application/json");
					response.getWriter().write(jsObj.toString());
				} catch (JSONException e) {
					log.error("JSON exception");
					e.printStackTrace();
				}
				
			} else {
				log.error("Ajax function refersh log failed");
			}
			break;
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Principal _userPrincipal = request.getUserPrincipal();
		if(_userPrincipal !=null && !_userPrincipal.equals("")){
			this.userUid = _userPrincipal.getName();
		}

		servletC = getServletContext();
		//logManagement = new LogManagement((String) servletC.getAttribute("logPath"));
		log.debug("USER="+this.userUid+" Access to log app");
		log.debug("USER="+this.userUid+" read log at "+ System.getProperty("app.logFile"));
		logManagement = new LogManagement(System.getProperty("app.logFile"));
		request.setAttribute("logs", logManagement.getLogStringFormat());
		request.setAttribute("logsarray", logManagement.getLogListFormat().toArray());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/logapp.jsp");
		dispatcher.forward(request, response);

	}
}
