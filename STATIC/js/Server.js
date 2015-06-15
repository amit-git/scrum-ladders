var SERVER = {};

SERVER.url = function(rowType, map){
	var u = "/D/"+ENV.LadderName+"/"+rowType;
	
	if (map){
		for (var k in map){
			//u += ",";
			//u += k +":"+ map[k];
			u += "/"+k +":"+ map[k];
		}
	}
	return u;
};


SERVER.get = function (url) {
    var d = $.ajax({
        type: "GET",
        url: url,
        async: false,
    });

    return d.responseText;
};//get()

SERVER._needVal = function(params, k, operation){
	if (Utils.isEmpty (params[k]) ){
		alert(operation + " Params doesn't have "+k +"\n params:"+ Utils.toString(params) );
		return false;
	}else{
		return true;
	}
};

SERVER.getAllLadders = function(scallback, errcallback){
	SERVER._post("GETLADDERS", {}, scallback, errcallback);
};

SERVER.deleteLadder= function(name, scallback, errcallback){

	var params = {};
	params["LADDER_NAME"] = name;
	SERVER._post("DELETELADDER", params, scallback, errcallback);

}; //deleteLadder()


SERVER.saveLadder = function(name, schemaJson, scallback, errcallback){
	
	//make sure it is valid.
	try{
		debugger;
		var arr = eval(schemaJson);
		Utils.validateLadders(arr);
	}catch(ex){
		alert("Invalid Schema '"+ex+"'\nJSON:"+schemaJson);
		return;
	}
	
	
	var params = {};
	params["LADDER_NAME"] = name;
	params["SCHEMA_JSON"] = schemaJson;
	
	SERVER._post("SAVELADDER", params, scallback, errcallback);
};

SERVER.insert = function(params, scallback, errcallback){

	if (!SERVER._needVal (params, ENV.PARENTID, "insert")) return;
	if (!SERVER._needVal (params, ENV.ROWTYPE, "insert")) return;

	SERVER._post("INSERT", params, scallback, errcallback);
};
SERVER.rows = function(params, scallback, errcallback, includeParents){
	/*
	if (includeParents){
		//Clone rows
		var newparams = {};
	    for (var k in params) {
	    	newparams[k] = params[k];
	    }
	    params = newparams;
	}
	*/
	//if (!SERVER._assertExists(params[], "ParentId", errcallback)) return;

	SERVER._post("ROWS", params, scallback, errcallback);	
};

SERVER.update = function(rowid, forkey, newval, rollup, scallback, errcallback){
	
	//For now every input change on the client side calls update.
	var params = {};
	params[ENV.ROWID] = rowid;
	params["KEY"]=forkey;
	params["VAL"] = newval;
	//params["ROLL"] = rollup;
	
	/*
	//That means inputParams should always have size of 2. 1 for rowId and second for the updated field.
	var maplen = Utils.mapSize(params);
	if (maplen!=2){
		throw new Utils.Exception("Update can only have 2 params:"+params + " len:"+maplen);
	}
	*/
	SERVER._post("UPDATE", params, scallback, errcallback);	
};

SERVER.fetchRollup = function(parentid, rollupTarget, rollupColumn, rollupType, rollupSumColumn, scallback, errcallback){

	if (!SERVER._assertExists(parentid, "ParentId", errcallback)) return;
	if (!SERVER._assertExists(rollupTarget, "RollupTarget", errcallback)) return;
	if (!SERVER._assertExists(rollupColumn, "RollupColumn", errcallback)) return;
	if (!SERVER._assertExists(rollupType, "RollupType", errcallback)) return;

	var url = "/ROLL/"+ENV.LadderName+"/"+parentid+"/"+rollupTarget+"/"+rollupColumn+"/"+rollupType;

	if (!Utils.isEmpty(rollupSumColumn))
		url += "/"+rollupSumColumn;
	
	SERVER._get(url, scallback, errcallback);	
};

SERVER._assertExists = function(val, errContext, errCallback){
	if (Utils.isEmpty(val)){
		alert(errContext + "\n "+val+" can't be Empty or null");
		errcallback();
		return false;
	}else{
		return true;
	}
}


SERVER.saveSetting = function(name, val, scallback, errcallback){
	
	if (!SERVER._assertExists(val, "saveSettings("+name+", NULL)", errcallback)) return;
	
	var params = {};
	params["SETTING_NAME"] = name;
	params["SETTING_TEXT"] = val;
	SERVER._post("SAVESETTING", params, scallback, errcallback);	
}; // saveSetting()

SERVER.getSetting = function(name, scallback, errcallback){
	if (!SERVER._assertExists(name, "Name parameter in getSettings() is null", errcallback)) return;
	
	var params = {};
	params["SETTING_NAME"] = name;
	SERVER._post("GETSETTING", params, scallback, errcallback);

}; // saveSetting()



SERVER.delete = function(rowId, scallback, errcallback){
	var params = {};
	params[ENV.ROWID] = rowId;
	SERVER._post("DELETE", params, scallback, errcallback);	
	
}; 

SERVER._post = function(service, params, scallback, errcallback){

	if (!scallback){
		throw new Utils.Exception("Callback for SERVER._post can't be null");
		return;
	}
	
	var lName = ENV.LadderName?ENV.LadderName:params["LADDER_NAME"];
	var rType = ENV.RowType?ENV.RowType:params["_rowType"];
	
	var url = "/"+service+"/" + lName+"/" + rType+"/";
			
	//var url = ENV.LadderName?"/"+service+"/"+ENV.LadderName:"/"+service+"/";
	
	$.post(url, params, function(data){

		try{
			if (jQuery.type(data) == "string") data = eval("("+data+")");
					
			if (data.Status=="SUCCESS"){
				scallback(data.Data);
			}else{

				if (errcallback) errcallback(data.Message, data.Data);
				else alert ("ERROR during "+service+"\n"+data.Message);
			}
		}catch(ex){
			debugger;
			alert ("EXCEPTION during "+service+"\n"+ex + "\n data:"+data);
			if (errcallback) errcallback(data.Message, data.Data);
		}
		
	});
	
}; // SERVER._post()

 

SERVER._get = function(url, scallback, errcallback){

	if (!scallback){
		throw new Utils.Exception("Callback for SERVER._get can't be null");
		return;
	}
	
	$.get(url, function(data){

		try{
			if (jQuery.type(data) == "string") data = eval("("+data+")");
					
			if (data.Status=="SUCCESS"){
				scallback(data.Data);
			}else{

				if (errcallback) errcallback(data.Message, data.Data);
				else alert ("ERROR during "+service+"\n"+data.Message);
			}
		}catch(ex){
			alert ("EXCEPTION during "+url+"\n"+ex + "\n data:"+data);
			if (errcallback) errcallback(data.Message, data.Data);
		}
		
	});
	
}; // SERVER.get()
