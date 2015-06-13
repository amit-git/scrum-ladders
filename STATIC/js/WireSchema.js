
var WireSchema = {};

$( document ).ready(function() {
	try{
		ENV.SetupWidgets(); //Must call this in the begining or all sort of things will stop working.
		
		$("#mainDiv").html("");
		$("#columnTableDiv").html("");
		$("#columnAttributesDiv").html("");
		
		
		var levelsWidget = new Widget.EditableListWidget2("Level Name", ENV.LadderSchema, 
				WireSchema.levelSelected, 
				WireSchema.levelValChanged);
		levelsWidget.render($("#mainDiv"));


	}catch(ex){
		$(document.body).html("WireSchema doc.ready ERROR:"+ex);
	}
});

WireSchema.save = function(){
	var json_text = JSON.stringify(ENV.LadderSchema, null, 2);
	SERVER.saveLadder(name, json_text, 
			function(data){
				//location.reload(false);
			}, 
			function(errmsg, data){
				alert("ERROR in LADDER creation ");
			} 
		);
	
};//WireSchema.save()



WireSchema.levelSelected = function(rowIndex, ele){
	//Selection callback
	$("#columnAttributesDiv").html("");
	$("#columnTableDiv").html("");
	
	if (!ele) return;
	
	WireSchema.ColumnTable = new Widget.EditableListWidget2("Columns for "+ele.Name, ele.Schema, 
			WireSchema.columnValSelected, 
			WireSchema.columnValChanged, 
			{"LevelIndex":rowIndex, "LevelObj":ele});

	WireSchema.ColumnTable.render($("#columnTableDiv"));

};//levelSelected()


WireSchema.levelValChanged = function(newObjArrWithName){
	//Change callback
	ENV.LadderSchema = [];
	for (var i =0; i<newObjArrWithName.length; i++){
		var ele = newObjArrWithName[i];
		if (!ele.Schema) ele.Schema = [{Name:"Description", ColumnType:"Text", MaxLen:50}];
		ENV.LadderSchema.push(ele);
	}
	
	WireSchema.save();
	return ENV.LadderSchema;
}


WireSchema.columnValSelected = function(rowIndex, ele, levelContext){
	$("#columnAttributesDiv").html("");
	
	if (!ele) return;
	var f = new Widget.SchemaColumnFormWidget(ele, function(jsonMap){
		//Save clicked
		for (var key in jsonMap){
			ele[key]=jsonMap[key];
		}

		$("#columnAttributesDiv").html("");
		WireSchema.ColumnTable.render($("#columnTableDiv"));
		WireSchema.save();
		
	}, levelContext.LevelObj);
	f.render($("#columnAttributesDiv"));
	
};//columnValSelected()


WireSchema.columnValChanged= function(newObjArrWithName, levelContext){

	levelContext.LevelObj.Schema = newObjArrWithName;
	WireSchema.save();
	return newObjArrWithName;
	
};//columnValChanged()

