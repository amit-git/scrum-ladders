
Widget.AddNewActionWidget = function(cTableId, cTableContext, cParentId, cRowsArr){
	if (!cTableContext) throw new Utils.Exception("cTableContext can't be "+ cTableContext);
	
	
	this.render = function(div){
		var parentContext = Utils.getParentContextByName(cTableContext.Name);
		if (!parentContext){
			cParentId = "ROOT";
		}
		
		
		if (Utils.isEmpty(cParentId) ){
			if (cTableContext){
				var parentContext = Utils.getParentContextByName(cTableContext.Name);
	
				if (parentContext){
					
					
					var htm = [];
					htm.push("You must select a specific "+parentContext.Name+" to create a new "+cTableContext.Name );
					htm.push("<br/> <a href='"+ SERVER.url(parentContext.Name) +"'>Go select an "+parentContext.Name+"</a>");
					htm.push("<br/><br/><br/>");
					div.html(htm.join(" "));
				}else{
					var htm = [];
					htm.push("You must go to the ROOT to create a new "+cTableContext.Name );
					htm.push("<br/> <a href='"+ SERVER.url(cTableContext.Name) +"'>Go to the ROOT</a>");
					htm.push("<br/><br/><br/>");
					div.html(htm.join(" "));
				}
			}
		
			return;
		}
		
		var btnId = Utils.getUid();
		div.html("<button id='"+btnId+"'>Add New "+cTableContext.Name+"</button><br/><br/><br/><br/>");
		
		$("#"+btnId).click(function(){
			$("#"+btnId).attr("disabled", "disabled");
			var id = Utils.getUid();
			
			$('#'+cTableId+' > tbody > tr:last').after("<tr id='"+ id +"'><td></td></tr>");

			
			var r = new Widget.Row(cTableContext.Name, cParentId);
			r.render($("#"+ id), cTableContext.Schema);
			
			r.getEventManager().attachEvent ("insert", function(source, newRowObj){
				r.render($("#"+ id), cTableContext.Schema, newRowObj);
				$("#"+btnId).removeAttr("disabled");
			});

		});
		
	}//render*()

}; //AddNewActionWidget()
 
