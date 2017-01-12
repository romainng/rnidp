function displayLog(logstring) {
	var lineLog = document.createElement("span");

	if (logstring.indexOf("] DEBUG") >= 0) {
		$(lineLog).attr("class", "debuglog");
	} else if (logstring.indexOf("] INFO") >= 0) {
		$(lineLog).attr("class", "infolog");

	} else if (logstring.indexOf("] ERROR") >= 0) {
		$(lineLog).attr("class", "errorlog");
	}

	$(lineLog).text(logstring);
	console.log(logstring);
	$("#logWindow").append(lineLog);
	$("#logWindow").append(document.createElement("br"));
}

$(document).ready(function autoR() {

	window.setTimeout(function() {

		$.ajax({
			type : "POST",
			url : "logapp",
			data : {
				action : "refreshLog",
			},
			success : function(response) {

				if(response.mod){
					$("#logWindow").text("");
					for (var i = 0; i < response.newLogs.length; i++) {
						displayLog(response.newLogs[i]);
					}
					$("#logWindow").scrollTop($('#logWindow')[0].scrollHeight);
				}
		
			}

		});

		autoR();
	}, 2000);

});