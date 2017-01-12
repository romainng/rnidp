var rootPath= "rnz_idp";

$(document).ready(function(){
	console.log("ok");
	$.ajax({
		url : "/"+rootPath+"/audit",
		method : "get",
		data : {action: "getSP"},
		success : function(response) {
			var splist = response.spList;
			
			$.each(splist, function (i, item) {
			    $('#selectSpList').append($('<option>', { 
			        value: item,
			        text : item 
			    }));
			});
			
			

			
		}
	});

});
