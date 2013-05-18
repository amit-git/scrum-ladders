
Widget.LaddersListWidget = function(mapLadders, errorDiv){
	var me = this;
	var listLadders = [];
	for (var name in mapLadders) listLadders.push(name);

	var renderLadder = function(htm, name, schemaid, saveid, openid){
		htm.push("<td>");
		htm.push(name);
		htm.push("</td>");

		htm.push("<td>");
		htm.push("<button class='ld_link' id='"+schemaid+"'>SHOW Schema</button>");
		htm.push("</td>");
		htm.push("<td>");
		htm.push("<button class='ld_link' id='"+saveid+"'>SAVE Schema</button>");
		htm.push("</td>");
		htm.push("<td>");
		htm.push("<button class='ld_link' id='"+openid+"'>Open LADDER</button>");
		htm.push("</td>");
	}
	
	this.render = function(div){
		var htm = [];

		htm.push("");
		htm.push("<table class='ld_bluebox' border=1>");
		htm.push("<tr>");
		htm.push("<td colspan=4 ></td>");
		htm.push("<td rowspan=10>");
		htm.push("	<textarea cols=100 rows=10 id='LadderSchemaText'></textarea>");
		htm.push("</td>");
		htm.push("</tr>");

		for (var i=0; i<listLadders.length; i++){
			htm.push("<tr>");
			var schemaid='schemalink'+i;
			var saveid='savelink'+i;
			var openid='openlink'+i;
			renderLadder (htm, listLadders[i], schemaid, saveid, openid);
			htm.push("</tr>");
		}//for i

		htm.push("<tr>");
		htm.push("<td colspan=3>");
		var addNewLadderBtn = "addNewLadderBtn";
		htm.push("<div class='ld_link' id='"+addNewLadderBtn+"'>[Add new Ladder]</div>");
		htm.push("</td>");
		htm.push("</tr>");

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
		$("#LadderSchemaText").val("Creating new LADDER "+name +"...please wait...");

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
 
		
	};//addNewLadder()
	
	
	var attachLoadSchema = function (i, name){
		var schemaid='schemalink'+i;
		var saveid='savelink'+i;
		var openid='openlink'+i;

		$("#"+schemaid).click(function(){
			var schemaJs = mapLadders[name];
			$("#LadderSchemaText").val(schemaJs);
		});
		
		$("#"+saveid).click(function(){
			var newSchema = $("#LadderSchemaText").val();
			saveLadder(name, newSchema);
		});

		$("#"+openid).click(function(){
			var schemaObj = eval(mapLadders[name]);
			window.open("/D/"+name+"/AND:_rowType:"+schemaObj[0].Name, "_blank");
		});		
	};
}; //AddNewActionWidget()
 
