
Widget.LaddersListWidget2 = function(mapLadders, errorDiv){
	var me = this;
	var listLadders = [];
	for (var name in mapLadders) listLadders.push(name);

	
	this.render = function(div){

		var htm = [];

		for (var i=0; i<listLadders.length; i++){
			htm.push("<div>");
			var openid='openlink'+i;
			var deleteid = 'deletelink'+i;
			
			htm.push(listLadders[i]);
			htm.push("<button class='item_action' id='"+openid+"'>Open</button>");
			htm.push("<button class='item_action' id='"+deleteid+"'>Delete</button>");

			htm.push("</div>");
		}//for i

		var addNewLadderBtn = "addNewLadderBtn";
		htm.push("<button class='overall_page_action' id='"+addNewLadderBtn+"'>[Add new Ladder]</button>");

		div.html(htm.join(" "));
		
		for (var i=0; i<listLadders.length; i++){
			var name = listLadders[i];
			attachLoadSchema(i, name);
		}
		
		$("#"+addNewLadderBtn).click(addNewLadder);

	}//render*()
 
	var addNewLadder = function(){
		var name=prompt("Please enter name of LADDER (No space or special chars)", "LADDER_"+listLadders.length);
		if (Utils.isEmpty(name)) return;

		name = name.replace(/\W/g, '-'); //remove stuff.

		if (Utils.indexOf(listLadders, name)>=0){
			alert (name + " already exists as a LADDER");
			return;
		}
		
		var json = SERVER.get("/STATIC/js/_Template_Schema.js");
		saveLadder(name, json);
	};//addNewLadder()

	var saveLadder = function(name, json){
		
		SERVER.saveLadder(name, json, 
			function(data){
				location.reload(false);
			}, 
			function(errmsg, data){
				alert("ERROR in LADDER creation ");
			} 
		);
 
		
	};//saveLadder()
	
	
	var deleteLadder = function(name){
		if (!confirm("Really delete Ladder '"+name+"'?")){
			return;
		}
		SERVER.deleteLadder(name, 
			function(data){
				location.reload(false);
			}, 
			function(errmsg, data){
				alert("ERROR in LADDER deletion ");
			} 
		);
 
		
	};//deleteLadder()	
	var attachLoadSchema = function (i, name){
		var openid='openlink'+i;
		var deleteid = 'deletelink'+i;

		$("#"+openid).click(function(){
			var schemaObj = eval(mapLadders[name]);
			Utils.openPage ("/D/"+name+"/"+schemaObj[0].Name);
			
		});
		
		$("#"+deleteid).click(function(){
			deleteLadder(name);

		});
		
	};
}; //AddNewActionWidget()
 
