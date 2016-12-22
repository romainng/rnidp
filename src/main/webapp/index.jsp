<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="lang.text" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<LINK REL="SHORTCUT ICON" HREF="img/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Vectury</title>

<link href="css/rnSkin.css" rel="stylesheet" type="text/css">

<!-- <script type='text/javascript' charset='utf-8' src='js/jquery-1.12.0.js'></script>  -->
<!-- <script src="https://code.jquery.com/jquery-3.1.1.js"></script> -->
<script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
<script>
	/**
	 * 
	 * @param brand with first car upper
	 */
	function skinResize(brand) {
		try {
			getViewPortSize();

			var eltAreaLeft = $("#divId" + brand + "Left");
			var position = eltAreaLeft.position();
			setDivHeight(eltAreaLeft, top.viewportheight - position.top);

			var eltAreaPub = $("#frameId" + brand + "Body");
			position = eltAreaPub.position();
			setDivHeight(eltAreaPub, top.viewportheight - position.top);
		} catch (error) {
			//alert("skinCommonResize:" + error);
		}
	}

	/**
	 * 
	 * @param divObj
	 * @param divH
	 */
	function setDivHeight(divObj, divH) {
		try {
			divObj.css({
				height : divH + 'px'
			});
		} catch (error) {
		}
	}

	/**
	 *
	 */
	function getViewPortSize() {
		if (typeof window.innerWidth != 'undefined') {
			//Standards compliant browsers (mozilla/netscape/opera/IE7)
			top.viewportwidth = window.innerWidth;
			top.viewportheight = window.innerHeight;
		} else if (typeof document.documentElement != 'undefined'
				&& typeof document.documentElement.clientWidth != 'undefined'
				&& document.documentElement.clientWidth != 0) {
			// IE6
			top.viewportwidth = document.documentElement.clientWidth;
			top.viewportheight = document.documentElement.clientHeight;
		} else {
			//Older IE
			top.viewportwidth = document.getElementsByTagName('body')[0].clientWidth;
			top.viewportheight = document.getElementsByTagName('body')[0].clientHeight;
		}
	}

	/**
	 * 
	 */
	function skinResizeVectury() {
		skinResize("Vectury");
	}

	$(document).ready(function() {
		skinResizeVectury();
		$(window).bind("resize", skinResizeVectury);
	});

	if (top.location != self.location) {
		//top.location.href = self.location.href;
		if(top!==null)
			top.location.href = "/rnz_idp/";
	}
</script>

<title>Vectury Application</title>


</head>

<body class="HomeBody" id="htmlBodyId">


	<div class="homeAREA" id="divIdHome">
		<div class="VecturyAREA" id="VecturyDivId">
			<table class="VecturyTABLE">
				<tbody>
					<tr class="VecturyHeaderTopTR">
						<td class="VecturyHeaderTopLeftTD" rowspan="3"><a
							class="VecturyHeaderVecturyA" id="VecturyAId" href="#">Vectury</a></td>
						<td class="VecturyHeaderTopRightTD"><span
							class="VecturyHeaderApplicationNameSPAN"
							id="VecturyApplicationNameSpanId"><fmt:message
									key="appname.label" /></span> &nbsp;</td>
					</tr>
					<tr class="VecturyHeaderMiddleTR">
						<td class="VecturyHeaderMiddleRightTD">&nbsp;</td>
					</tr>
					<tr class="VecturyHeaderBottomTR">
						<td class="VecturyHeaderBottomRightTD"><%-- <a
							class="VecturyHeaderMENUA" id="VecturyHelpAId" href="help.jsp"
							target="body"><fmt:message key="help.label" /></a> --%> &nbsp;</td>
					</tr>

					<tr class="VecturyHeaderBottomBoundTR">
						<td class="VecturyHeaderBottomBoundTD" colspan="2"></td>
					</tr>

					<tr class="VecturyAppTR">
						<td class="VecturyLeftTD">
							<div class="VecturyLeftAREA" id="divIdVecturyLeft"
								style="height: 100%;">
								<table class="VecturyMenu0TABLE" id="tableIdMenuVectury">
									<tbody>

										<tr class="VecturyMenuSelected1ChildrenTR"
											id="trIdMenuChildrenVecturyAPP">
											<td class="VecturyMenuSelected1ChildrenTD"
												id="tdIdMenuChildrenVecturyAPP">
												<table class="VecturyMenu1TABLE" id="tableIdMenuVecturyAPP">
													<tbody>
														<tr class="VecturyMenu2TR">
															<td><a class="VecturyMenu2TR" href="indexframe.jsp"
																target="body"><fmt:message key="home.label" /></a></td>
														</tr>



														<tr class="VecturyMenu2TR">
															<td><fmt:message key="servicepro.label" /></td>
														</tr>

														<c:forEach var="item"
															items="${applicationScope['spitem']}">

															<tr class="VecturyMenu3TR">
																<td class="VecturyMenu3TR"><a
																	href="<c:url value="/SAMLProvider?sp=${item}"/>" >${item}</a></td>
															</tr>
														</c:forEach>

														<tr class="VecturyMenu2TR">
															<td><%-- <a class="VecturyMenu2TR" href="/rnz_idp/me"
																target="body"><fmt:message key="me.label" /></a> --%><fmt:message key="me.label" /></td>
																
																<c:forEach var="item"
															items="${applicationScope['spitem']}">

															<tr class="VecturyMenu3TR">
																<td class="VecturyMenu3TR"><a
																	href="<c:url value="me?sp=${item}" />" target="body">${item}</a></td>
															</tr>
														</c:forEach>
																
														</tr>

														<tr class="VecturyMenu2TR">
															<td><a class="VecturyMenu2TR" href="management"
																target="body"><fmt:message key="serviceproman.label" />
															</a></td>
														</tr>

														<tr class="VecturyMenu2TR">
															<td><a class="VecturyMenu2TR" href="/rnz_idp/logapp"
																target="body">Logs</a></td>
														</tr>
														
														<tr class="VecturyMenu2TR" id="lang">
															<td><label><fmt:message key="lang.label" />
																	:</label> <img id="langfr" src="img/fr.png" height="13"
																width="18" onclick="changeLocale('fr');" /> <img
																id="langen" src="img/en.png" height="13" width="18"
																onclick="changeLocale('en')" /></td>
														</tr>
														
														
														
													</tbody>
												</table>
											</td>
										</tr>
									</tbody>
								</table>
								<br>
								<!-- <table class="VecturyMenu0TABLE" id="tableIdMenuVectury">
									<tbody>
										<tr class="VecturyMenu0TR" id="trIdMenuVecturyAbout">
											<td class="VecturyMenu0TD" id="tdIdMenuVecturyAbout"><span
												class="VecturyLeftVersionSpan">Version <a>0.2</a><br/>
											</span></td>
										</tr>
									</tbody>
								</table> -->
							</div>
						</td>
						<td class="VecturyRightTD" id="tdIdVecturyBody"><iframe
								name="body" class="VecturyBodyFRAME" id="frameIdVecturyBody"
								src="indexframe.jsp" style="height: 100%;"> </iframe></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>


	<script src=js/scriptsframe.js></script>
</body>

</html>
