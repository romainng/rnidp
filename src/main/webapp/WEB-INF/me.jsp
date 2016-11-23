<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="lang.text" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="css/style.css" rel="stylesheet" type="text/css" />

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LDAP Resume</title>
</head>
<body >
	<div id="maindiv">
		<h2>
			<fmt:message key="user.label" />
			<c:out value="${myUid}"></c:out>
			<fmt:message key="details.label" />
		</h2>
		<c:forEach items="${ myInfo }" var="att" varStatus="status">
			<c:out value="${ att }" />
			<br />
		</c:forEach>
	</div>
	

</body>
</html>