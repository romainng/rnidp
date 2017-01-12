package com.renault.rnet.idp.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.renault.rnet.idp.samlconsumer.SamlTokenValidator;
import com.renault.rnet.idp.samlconsumer.SamlUrl;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ConsumeServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/denied.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		SamlUrl samlurl = new SamlUrl(request);
		
		String saml64 = samlurl.getSaml64();
		String saml = null;
		if (saml64 != null && !saml64.equals("")) {
			try {
				saml = new String(Base64.decode(saml64), "UTF-8");
				SamlTokenValidator validator = new SamlTokenValidator(saml);
				boolean okToken = validator.isOkToken();

				
				if (okToken) {
					//TODO ON RECUPERE LES CHAMPS DANS LE TOKEN 
					Map<String, String> attributeMap = validator.getAttributeMap();
					request.setAttribute("attMap", attributeMap);
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/samlok.jsp");
					dispatcher.forward(request, response);
				} else {
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/denied.jsp");
					dispatcher.forward(request, response);
				}

			} catch (Base64DecodingException e) {
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/denied.jsp");
				dispatcher.forward(request, response);
				e.printStackTrace();
			}
		} else {
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/denied.jsp");
			dispatcher.forward(request, response);
		}

	}

}
