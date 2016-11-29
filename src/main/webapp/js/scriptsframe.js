/**
 * Scripts
 * 
 * @author rng
 * 
 */

// CSS color for IHM warnings
var WARN_COLOR = "red";
var sentence="confirmation";
var rootPath= "rnz_idp2";
// var context = "http://localhost:8080";

/**
 * AJAX dynamic language change
 */
var changeLocale = function(language) {
	
	$.ajax({
		url : "/"+rootPath+"/home",
		method : 'post',
		data : "language=" + language,
		success : function(msg) {
			
			location.reload();
		}
	});
};

/**
 * When add SP : check values
 */
$("#formAdd").submit(function(event) {

	// condition before : is numeric?
	if (!isNum($("#conditionsnotbeforeAdd").val())) {
		$("#conditionsnotbeforeAddWarn").css('visibility', 'visible');
		$("#conditionsnotbeforeAdd").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else {
		$("#conditionsnotbeforeAddWarn").css('visibility', 'hidden');
		$("#conditionsnotbeforeAdd").css('background-color', 'white');
	}

	// Condition after is numeric?
	if (!isNum($("#conditionnotonorafterAdd").val())) {
		$("#conditionnotonorafterAddWarn").css('visibility', 'visible');
		$("#conditionnotonorafterAdd").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else {
		$("#conditionnotonorafterAddWarn").css('visibility', 'hidden');
		$("#conditionnotonorafterAdd").css('background-color', 'white');
	}

	// SP name is mandatory & already
	// exists?
	if (!mandatory($("#spnameAdd").val())) {

		$("#spnameAddWarn").css('visibility', 'visible');
		$("#spnameAdd").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else if (exist($("#spnameAdd").val())) {

		$("#spnameAddWarnDup").css('visibility', 'visible');
		$("#spnameAdd").css('background-color', WARN_COLOR);

		event.preventDefault();
	} else {
		$("#spnameAddWarnDup").css('visibility', 'hidden');
		$("#spnameAddWarn").css('visibility', 'hidden');
		$("#spnameAdd").css('background-color', 'white');
	}

	// Issuer is mandatory
	if (!mandatory($("#issuerurlAdd").val())) {
		$("#issuerurlAddWarn").css('visibility', 'visible');
		$("#issuerurlAdd").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else {
		$("#issuerurlAddWarn").css('visibility', 'hidden');
		$("#issuerurlAdd").css('background-color', 'white');
	}

	// Data recipient is mandatory & is URL
	// format?
	if (!isURL($("#datarecipientAdd").val())) {
		$("#datarecipientWarn").css('visibility', 'visible');
		$("#datarecipientAdd").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else {
		$("#datarecipientWarn").css('visibility', 'hidden');
		$("#datarecipientAdd").css('background-color', 'white');
	}
	
	$("#issuerurlAdd").css('background-color', '#e6e6e6');
	$("#conditionsnotbeforeAdd").css('background-color', '#e6e6e6');
	$("#conditionnotonorafterAdd").css('background-color', '#e6e6e6');
	/**
	 * if
	 * (!mandatory($("#audienceuriAdd").val())) {
	 * $("#audienceuriWarn")
	 * .css('visibility', 'visible');
	 * $("#audienceuriAdd")
	 * .css('background-color', WARN_COLOR);
	 * event.preventDefault(); } else {
	 * $("#audienceurWarn")
	 * .css('visibility', 'hidden');
	 * $("#audienceuriAdd")
	 * .css('background-color', 'white'); }
	 */

});

/**
 * When modify SP : check values
 */
$("#formMod").submit(function(event) {

	if (!isURL($("#datarecipientModif").val())) {
		$("#datarecipientWarnMod").css('visibility', 'visible');
		$("#datarecipientModif").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else {
		$("#datarecipientWarnMod").css('visibility', 'hidden');
		$("#datarecipientModif").css('background-color', 'white');
	}

	if (!mandatory($("#selectModifySp").val())) {
		$("#selectModifySp").css('background-color', WARN_COLOR);
		$("#spnameModWarn").css('visibility', 'visible');
		event.preventDefault();
	} else {
		$("#spnameModWarn").css('visibility', 'hidden');
		$("#selectModifySp").css('background-color', 'white');
	}

	if (!mandatory($("#issuerurlModif").val())) {

		$("#issuerurlModif").css('background-color', WARN_COLOR);
		$("#issuerurlModifWarn").css('visibility', 'visible');
		event.preventDefault();
	} else {
		$("#issuerurlModifWarn").css('visibility', 'hidden');
		$("#issuerurlModif").css('background-color', 'white');
	}

	if (!isNum($("#conditionsnotbeforeMod").val())) {
		$("#conditionsnotbeforeMod").css('background-color', WARN_COLOR);
		$("#conditionsnotbeforeModWarn").css('visibility', 'visible');
		event.preventDefault();

	} else {
		$("#conditionsnotbeforeModWarn").css('visibility', 'hidden');
		$("#conditionsnotbeforeMod").css('background-color', 'white');
	}

	if (!isNum($("#conditionnotonorafterMod").val())) {
		$("#conditionnotonorafterMod").css('background-color', WARN_COLOR);
		$("#conditionnotonorafterModWarn").css('visibility', 'visible');
		event.preventDefault();
	} else {
		$("#conditionnotonorafterModWarn").css('visibility', 'hidden');
		$("#conditionnotonorafterMod").css('background-color', 'white');
	}
	$("#issuerurlModif").css('background-color', '#e6e6e6');
	$("#conditionsnotbeforeMod").css('background-color', '#e6e6e6');
	$("#conditionnotonorafterMod").css('background-color', '#e6e6e6');
});

$("#delform").submit(function(event) {
	if (!mandatory($("#sptodel").val())) {
		$("#sptodel").css('background-color', WARN_COLOR);
		event.preventDefault();
	} else {
		$("#sptodel").css('background-color', 'white');
		if (confirm(sentence)) {
		} else {
			event.preventDefault();
		}

	}
});

$('#selectModifySp').change(
		function() {

			$.ajax({
				url : '/'+rootPath+'/added',
				method : 'get',
				data : {
					action : "getVal",
					spChoosen : $('#selectModifySp').val()
				},
				success : function(response) {
					$('#audienceuriModif').val(response.audience);
					$('#relaystateModif').val(response.relay);
					$('#datarecipientModif').val(response.dataRec);
					$('#profilsModif').val(response.profils);
					$('#adminsModif').val(response.admins);
					var attAjax = response.attributes;

					$('input[name="spAttrib"]').each(function() {
						this.checked = false;
					});
					if ($(attAjax) != null && $(attAjax).length > 0) {
						for (var i = 0; i < $(attAjax).length; i++) {

							$(
									"input[type=checkbox][value="
											+ $(attAjax)[i] + "]").prop(
									"checked", true);
						}

					}

				}
			});
		});

function exist(stringToTest) {
	var listToString = $("#listSP").text().trim();
	var size = listToString.length;
	var listsp = listToString.substring(1, size - 1).split(",");
	for (var i = 0; i < listsp.length; i++) {
		if (listsp[i].trim() === stringToTest.toLowerCase().trim()) {
			return true
		}
	}

	return false
}

function mandatory(value) {
	if (value.trim() === "") {
		return false;
	}
	return true
}

function isNum(value) {
	if (value !== "" && $.isNumeric(value)) {
		return true
	} else if (value === "") {
		return true
	}
	return false;
}

function isURL(url) {
	var regex = /(https?:\/\/(?:www\.|(?!www))[^\s\.]+\.[^\s]{2,}|www\.[^\s]+\.[^\s]{2,})/;

	return regex.test(url);
}
