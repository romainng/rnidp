/**
 * Scripts
 * 
 * @author rng
 * 
 */


$(document).ready( function () {
	$(".adminRole").hide();
	$(".logRole").hide();
	$(".auditRole").hide();
	$.ajax({
		url : "/"+rootPath+"/SAMLProvider",
		method : "post",
		data : {action: "userRole"},
		success : function(response) {
			console.log("check user role");
			var roles = response.roles;
			for(i in roles){
		
				switch(roles[i]) {
				case "admin":
					$(".adminRole").show();
					break;
				case "logviewer":
					$(".logRole").show();
					break;
				case "audit":
					$(".auditRole").show();
				}
				
			}
			
		}
	});

}
) ;
