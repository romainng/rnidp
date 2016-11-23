<%@page import="com.renault.rnet.idp.bean.ServiceProviderProperties"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.renault.rnet.idp.bean.Parameter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>home</title>
</head>
<body>

	<h1>URL Parameters</h1>
	<%
		com.renault.rnet.idp.bean.Parameter urlparam = (com.renault.rnet.idp.bean.Parameter) request
				.getAttribute("param");
	%>	
	<p> SP = <% out.println(urlparam.getSp());%> </p>
	<p> IPN = <% out.println(urlparam.getUid());%> </p>


<h1>
		Service provider properties :
		<%
		out.println("SP = " + urlparam.getSp());
	%>
	</h1>

	<p>
		<%
			com.renault.rnet.idp.bean.ServiceProvidersList handlers = (com.renault.rnet.idp.bean.ServiceProvidersList) request
					.getAttribute("handlers");
			com.renault.rnet.idp.bean.ServiceProviderProperties spProp = (com.renault.rnet.idp.bean.ServiceProviderProperties) handlers.getSamlHandlers()
					.get(urlparam.getSp());
		%>
		IssuerURL =
		<%
			out.println(spProp.getIssuerURL());
		%>
		<br /> ConfirmationDataRecipient =
		<%
			out.println(spProp.getConfirmationDataRecipient());
		%><br />
		AudienceURI =
		<%
			out.println(spProp.getAudienceURI());
		%><br /> RelayState =
		<%
			out.println(spProp.getRelaystate());
		%><br />
		ConfirmationDataNotOnOrAfter =
		<%
			out.println(spProp.getConfirmationDataNotOnOrAfter());
		%><br />
		ConditionsNotBefore =
		<%
			out.println(spProp.getConditionsNotBefore());
		%><br />
		ConditionsNotOnOrAfter =
		<%
			out.println(spProp.getConditionsNotOnOrAfter());
		%><br /> Attributes
		=
		<%
			out.println(spProp.getAttributes());
		%><br /> Profiles =
		<%
			out.println(spProp.getProfiles());
		%><br /> Administrators =
		<%
			out.println(spProp.getAdministrators());
		%><br />
	</p>

</body>
</html>