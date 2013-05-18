
Widget.DBConnectionWidget = function(){
	var me = this;
	this.render = function(div){
		$("#"+btnId).attr("disabled", "disabled");
			
		var btnId = "btnSetDBConn";
		var htm = [];
		htm.push("<textarea cols=100 rows=1 id='mongoDBConnectionText'></textarea>");
		htm.push("</br>");
		htm.push("<button id='"+btnId+"'>Load DB</button>");
		div.html(htm.join(" "));

		SERVER.getSetting("DBConnection", function(txt){
			$("#mongoDBConnectionText").val(txt);
			$("#"+btnId).removeAttr("disabled");
		});

		///STATIC/SETTINGS/DBConnection.txt
		
		$("#"+btnId).click(function(){
			me.getEventManager().notify ("loading", me);

			$("#"+btnId).attr("disabled", "disabled");

			var txt = $("#mongoDBConnectionText").val();
			
			SERVER.saveSetting("DBConnection", txt, 
				function(data){
						
 					me.getEventManager().notify ("save", me, data);
 					$("#"+btnId).removeAttr("disabled");
				}, 
				function(errmsg, data){
 					$("#"+btnId).removeAttr("disabled");
				}
			);
		});
		
	}//render*()

}; //AddNewActionWidget()
 
