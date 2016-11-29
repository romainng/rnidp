<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="lang.text" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Service Providers Management</title>
<link rel="stylesheet" type="text/css" href="css/style.css">
<LINK REL="SHORTCUT ICON" HREF="img/favicon.ico">
<script src="https://code.jquery.com/jquery-3.1.1.js"></script>
</head>
<body>

	<form id="formAdd" action="/rnz_idp2/added" method="post">

		<fieldset>
			<legend>
				<fmt:message key="addtitle.manage.label" />
				:
			</legend>

			<label for="spname"><fmt:message key="sp.manage.label" /> :</label><input
				type="text" id="spnameAdd" name="spname" value="" size="25"
				maxlength="25"> <a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="sphint.manage.label" /></span>

			</a> <span id="spnameAddWarn"><fmt:message
					key="mandatory.warn.label" /></span> <span id="spnameAddWarnDup"><fmt:message
					key="duplicate.warn.label" /></span><br /> <label for="issuerurl">
				<fmt:message key="issuer.manage.label" /> :
			</label><input type="text" name="issuerurl" id="issuerurlAdd"
				value="VecturyDealerCommunity" size="30" maxlength="30" readonly>
			<span id="issuerurlAddWarn"><fmt:message
					key="mandatory.warn.label" /></span> <br /> <label for="datarecipient"><fmt:message
					key="recipient.manage.label" /> :</label><input type="text"
				name="datarecipient" id="datarecipientAdd" value="" size="40"
				maxlength="50"> <a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="recipienthint.manage.label" /></span>
			</a> <span id=datarecipientWarn><fmt:message key="url.warn.label" /></span>
			<br /> <label for="audienceuri"><fmt:message
					key="audi.manage.label" /> :</label><input type="text" name="audienceuri"
				id="audienceuriAdd" value="" size="40" maxlength="50"> <a
				href="#" class="info"> <img alt="info" src="img/hint.jpg"
				height="15" width="15"> <span><fmt:message
						key="audihint.manage.label" /></span>
			</a> <span id="audienceuriWarn"><fmt:message
					key="mandatory.warn.label" /></span><br /> <label for="relaystate"><fmt:message
					key="relay.manage.label" /> : </label><input type="text" name="relaystate"
				value="" size="40" maxlength="50"> <a href="#" class="info">
				<img alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="relayhint.manage.label" /></span>
			</a> <br /> <label for="conditionsnotbefore"><fmt:message
					key="condbefore.manage.label" /> :</label> <input type="text"
				name="conditionsnotbefore" id="conditionsnotbeforeAdd" value="10"
				size="5" maxlength="5" readonly> <a href="#" class="info">
				<img alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="beforehint.manage.label" /></span>
			</a> <span id="conditionsnotbeforeAddWarn"><fmt:message
					key="number.warn.label" /></span><br /> <label
				for="conditionnotonorafter"><fmt:message
					key="condafter.manage.label" /> :</label> <input type="text"
				name="conditionnotonorafter" id="conditionnotonorafterAdd"
				value="30" size="5" maxlength="5" readonly> <a href="#"
				class="info"> <img alt="info" src="img/hint.jpg" height="15"
				width="15"> <span><fmt:message
						key="afterhint.manage.label" /></span>
			</a> <span id="conditionnotonorafterAddWarn"><fmt:message
					key="number.warn.label" /></span><br /> <label for="atts"><fmt:message
					key="attribute.manage.label" /> :</label> <br />
			<c:import url="attributeslist.jsp" />

			<br /> <label for="profils"><fmt:message
					key="profil.manage.label" /> : </label>
			<textarea rows="2" cols="20" name="profils"></textarea>
			<a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="speratorhint.manage.label" /></span>

			</a> 
			
			<br /> <label for="admins"><fmt:message
					key="admin.manage.label" /> :</label>
			<textarea rows="2" cols="20" name="admins"></textarea><a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="speratorhint.manage.label" /></span>

			</a> 
			<br /> <input type="submit"
				value="<fmt:message key="addbut.manage.label" />" name="action" />
			<br />
		</fieldset>
	</form>


	<form id="formMod" action="/rnz_idp2/added" method="post">
		<fieldset>
			<legend>
				<fmt:message key="modtitle.manage.label" />
				:
			</legend>

			<label><fmt:message key="sp.manage.label" /> :</label><SELECT
				name="spname" id="selectModifySp">
				<option value="">-Select-</option>
				<c:forEach var="item" items="${applicationScope['spitem']}">
					<option value="${item}">${item}</option>
				</c:forEach>
			</SELECT> <span id="spnameModWarn">mandatory</span> <br /> <label
				for="issuerurl"><fmt:message key="issuer.manage.label" /> :</label>
			<input type="text" id="issuerurlModif" name="issuerurl"
				value="VecturyDealerCommunity" size="30" maxlength="30" readonly>
			<span id="issuerurlModifWarn"><fmt:message
					key="mandatory.warn.label" /></span> <br /> <label for="datarecipient"><fmt:message
					key="recipient.manage.label" /> : </label> <input type="text"
				id="datarecipientModif" name="datarecipient" value="" size="40"
				maxlength="50"> <a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="recipienthint.manage.label" /></span>
			</a> <label id="datarecipientWarnMod"><fmt:message
					key="url.warn.label" /></label> <br /> <label for="audienceuri"><fmt:message
					key="audi.manage.label" /> : </label> <input type="text"
				id="audienceuriModif" name="audienceuri" value="" size="40"
				maxlength="50"> <a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="audihint.manage.label" /></span>
			</a><br /> <label for="relaystate"><fmt:message
					key="relay.manage.label" /> :</label> <input type="text"
				id="relaystateModif" name="relaystate" value="" size="40"
				maxlength="50"> <a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="relayhint.manage.label" /></span>
			</a><br /> <label for="conditionsnotbefore"><fmt:message
					key="condbefore.manage.label" /> :</label> <input type="text"
				name="conditionsnotbefore" id="conditionsnotbeforeMod" value="10"
				size="5" maxlength="5" readonly><a href="#" class="info">
				<img alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="beforehint.manage.label" /></span>
			</a> <span id="conditionsnotbeforeModWarn"><fmt:message
					key="number.warn.label" /></span><br /> <label
				for="conditionnotonorafter"><fmt:message
					key="condafter.manage.label" /> :</label> <input type="text"
				name="conditionnotonorafter" id="conditionnotonorafterMod"
				value="30" size="5" maxlength="5" readonly><a href="#"
				class="info"> <img alt="info" src="img/hint.jpg" height="15"
				width="15"> <span><fmt:message
						key="afterhint.manage.label" /></span>
			</a> <span id="conditionnotonorafterModWarn"><fmt:message
					key="number.warn.label" /></span><br /> <label for="atts"><fmt:message
					key="attribute.manage.label" /> :</label> <br />
		
			<c:import url="attributeslist.jsp" />



			<br /> <label for="profils"><fmt:message
					key="profil.manage.label" /> :</label>
			<textarea id="profilsModif" rows="2" cols="20" name="profils"></textarea>
			<a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="speratorhint.manage.label" /></span>

			</a> <br /> <label for="admins"><fmt:message
					key="admin.manage.label" /> : </label>
			<textarea id="adminsModif" rows="2" cols="20" name="admins"></textarea>
			<a href="#" class="info"> <img
				alt="info" src="img/hint.jpg" height="15" width="15"> <span><fmt:message
						key="speratorhint.manage.label" /></span>

			</a> 
			<br /> <br /> <input type="submit"
				value="<fmt:message key="modbut.manage.label" />" name="action" />
			<br />
		</fieldset>

	</form>


	<form id="delform" action="/rnz_idp2/added" method="post">
		<fieldset>
			<legend>
				<fmt:message key="deltitle.manage.label" />
				:
			</legend>
			<label><fmt:message key="sp.manage.label" /> :</label> <SELECT
				id="sptodel" name="sptodel">
				<option value="">-Select-</option>
				<c:forEach var="item" items="${applicationScope['spitem']}">
					<option value="${item}">${item}</option>
				</c:forEach>
			</SELECT><br /> <br /> <input type="submit"
				value="<fmt:message key="delbut.manage.label" />" name="action" />
			<br />
		</fieldset>

	</form>

	<script src=js/scriptsframe.js></script>
	<div id="listSP">
		<c:out value="${applicationScope['spitem']}"></c:out>
	</div>
	
</body>
</html>
