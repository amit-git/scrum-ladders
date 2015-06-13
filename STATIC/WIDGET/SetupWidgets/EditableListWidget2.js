
Widget.EditableListWidget2 = function(headerName, objArrWithName, selectionCallback, changeCallback, context){

	var me = this;
	var jqDiv = null;
	var selectId = Utils.getUid();
	
	var addId = Utils.getUid();
	var editId = Utils.getUid();
	var deleteId = Utils.getUid();
	var upId = Utils.getUid();
	var downId = Utils.getUid();
	
	if (!Widget._EDL_MAP) Widget._EDL_MAP = {};
	var MAP = Widget._EDL_MAP;
	//pushSchemaToMap
	{
		for (var i=0; i<objArrWithName.length; i++){
			var ele = objArrWithName[i];
			MAP[ele.Name] = ele;
		}//for i
	}
	
	
	
	this.render = function(div){
		jqDiv = $(div);
		jqDiv.html("");
		
		var htm = [];
		htm.push("<span id='"+addId+"' class='ld_link paddingleftright'>Add New</span>");
		htm.push("<span id='"+editId+"' class='ld_link paddingleftright'>Edit</span>");
		htm.push("<span id='"+deleteId+"' class='ld_link paddingleftright'>Delete</span>");
		htm.push("<br/>");
		htm.push("<span id='"+upId+"' class='ld_link paddingleftright'>Move Up</span>");
		htm.push("<span id='"+downId+"' class='ld_link paddingleftright'>Move Down</span>");
		htm.push("<br/>");

		htm.push("<select size=10 id='"+selectId+"' style='width:200px;'>");
		for (var i=0; i<objArrWithName.length; i++){
			var ele = objArrWithName[i];
			htm.push("<option>"+ele.Name+"</option>");
		}
		htm.push("</select>");
		jqDiv.html(htm.join(""));

		
		$("#"+selectId).bind('change', function() {
			var name = $("#"+selectId).val();
			var i = $("#"+selectId+" option:selected").index()
			selectionCallback(i, MAP[name], context);
		});

		//---- ADD
		$("#"+addId).click(function(){
			var newval = acceptNew (headerName, null);
			if (!newval) return;
		    var newOpt = $("<option>"+newval+"</option>");
			$("#"+selectId+" > option:selected").after(newOpt); //append new
			valuesChanged();
		});

		//--- EDIT 
		$("#"+editId).click(function(){
			var name = $("#"+selectId).val(); if (!name) return;
			var newval = acceptNew (headerName, name);
			if (!newval) return;
			MAP[newval] = MAP[name]; //copy schema
			MAP[newval].Name = newval;

			$("#"+selectId+" > option:selected").text(newval); //edit
			valuesChanged();
		});
		
		//--- DELETE
		$("#"+deleteId).click(function(){
			var name = $("#"+selectId).val();
			if (!name) return;
			if (!confirm("Really delete '"+name+"'?")) return;
			$("#"+selectId+" > option:selected").remove(); 
			valuesChanged();
		});
		
		//--- UP
		$("#"+upId).click(function(){
			var opt1 = $("#"+selectId+" > option:selected");
			var i = opt1.index(); 
			if (i==0) return;
			var opt2 = $("#"+selectId+" option").eq(i-1);

			var val1 = opt1.text(); var val2 = opt2.text();
			opt1.text(val2);
			opt2.text(val1);
			
			valuesChanged();
		});

		//--- Down
		$("#"+downId).click(function(){
			var opt1 = $("#"+selectId+" > option:selected");
			var i = opt1.index(); 
			if (i>$("#"+selectId).size()) return;

			var opt2 = $("#"+selectId+" option").eq(i+1);

			var val1 = opt1.text(); var val2 = opt2.text();
			opt1.text(val2);
			opt2.text(val1);
			
			valuesChanged();
		});
		
	}//render()

 
	var acceptNew = function(label, defVal){
		var newval = prompt("Enter value: "+label, defVal?defVal:null);
		if (Utils.isEmpty(newval) || newval==defVal) return null;
		
		//check special characters
	    if (Utils.hasSpecialChars(newval)){
	    	alert("'"+newval+"' has special characters. Not allowed.");
	    	return null;
	    }
		//check dup
	    if (isDup(newval)){
	    	alert("'"+newval+"' already exists in the list");
	    	return null;
	    }
	    return newval;
		
	}//acceptNew()
	
	var isDup= function(newval){
		var arr = getAllVals();
		for (var i=0; i<arr.length; i++){
		    if (newval==arr[i]) return true; 
		}
		return false;
	}

	var getAllVals = function(){
		var arr = [];
	    $("#"+selectId+" option").each(function(i){
		    arr.push($(this).text()); 
	    });
		return arr;
	}
	var valuesChanged = function(){

		var newObjArr = [];
		var arr = getAllVals();
		for (var i=0; i<arr.length; i++){
			var val = arr[i];
			var ele = MAP[val];
			if (!ele){
				ele = {"Name":val};
				MAP[val] = ele;
			}
			newObjArr.push(MAP[val]);
		}
		
		objArrWithName = changeCallback(newObjArr, context);
	};//valuesChanged()


	
}; //EditableListWidget2()
 
