<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="lang.text" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Log service</title>
<link rel="stylesheet" type="text/css" href="css/style.css">
<LINK REL="SHORTCUT ICON" HREF="img/favicon.ico">
<script src="https://code.jquery.com/jquery-3.1.1.js"></script>
 <script type="text/javascript" src="js/scriptlog.js"></script>
</head>
<body>

	<h2>Log service</h2>


	<div id="logWindow">
		<%-- <c:set var="logs" value='${requestScope["logs"]}' />
<c:out value="${logs}"/> --%>

		<c:set var="logs" value='${requestScope["logsarray"]}' />

		<c:forEach var="log" items="${logs}">
<%-- 			<c:out value="${log}" />
			<br /> --%>
<!-- 			<script type="text/javascript">
			displayLog("<c:out value="${log}" />");
			</script> -->
			<br/>
		</c:forEach>

	</div>

</body>
</html>
