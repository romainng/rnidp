<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="lang.text" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="css/style.css" rel="stylesheet" type="text/css">

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<script src="https://code.jquery.com/jquery-3.1.1.js"></script>

</head>
<body>

<form id="auditForm" action="/rnz_idp/auditTrigger" method="post">
	<fieldset>
		<legend>
			<fmt:message key="legend.audit.label" /> : 			
		</legend>
		
		<label for="uid"> <fmt:message key="uid.audit.label" /> : </label>
		<input type="text" id="uidAudit" name="uid" value="" size="10" maxlength="10" />
		<br/>
		<label for="dateDebut"> <fmt:message key="datebeg.audit.label" /> : </label>
		<input type="date" name="dateDebut" />
		<br/>
		<label for="timeDebut"> <fmt:message key="timebeg.audit.label" /> : </label>
		<input type="time" name="timeDebut" />
		<br/>
		<label for="dateFin"> <fmt:message key="dateend.audit.label" /> : </label>
		<input type="date" name="dateFin" />
		<br/>
		<label for="timeFin"><fmt:message key="timeend.audit.label" /> : </label>
		<input type="time" name="timeFin" />
		<br/>
		<label><fmt:message key="signer.audit.label" /> : </label>
		<input type="file" name="signerFile" accept=".pfx, .p12">
		<br/>
		<label><fmt:message key="sp.audit.label" /> :</label>
		<select id="selectSpList" name="spChoose">
			<option>-Select-</option>
		</select>
		<br/>
		<input type="submit" value="<fmt:message key="send.audit.label" />" name="auditGo" />
	</fieldset>

</form>
 <script type="text/javascript" src="js/auditScripts.js"></script>
</body>
</html>