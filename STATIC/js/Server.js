var SERVER = {};

SERVER.url = function(rowType, map){
	var u = "/D/"+ENV.LadderName+"/AND:_rowType:"+rowType;
	
	if (map){
		for (var k in map){
			u += ",";
			u += k +":"+ map[k];
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



SERVER.saveLadder = function(name, schemaJson, scallback, errcallback){
	
	//make sure it is valid.
	try{
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
SERVER.rows = function(params, scallback, errcallback){
	SERVER._post("ROWS", params, scallback, errcallback);	
};

SERVER.update = function(params, scallback, errcallback){
	
	//For now every input change on the client side calls update.
	//That means inputParams should always have size of 2. 1 for rowId and second for the updated field. 
	SERVER._post("UPDATE", params, scallback, errcallback);	
};


SERVER.saveSetting = function(name, val, scallback, errcallback){
	if (Utils.isEmpty(val)){
		alert(name+" value can't be Empty or null");
		errcallback();
		return;
	}
	var params = {};
	params["SETTING_NAME"] = name;
	params["SETTING_TEXT"] = val;
	SERVER._post("SAVESETTING", params, scallback, errcallback);	
}; // saveSetting()

SERVER.getSetting = function(name, scallback, errcallback){
	if (Utils.isEmpty(name)){
		alert(name+" can't be Empty or null");
		errcallback();
		return;
	}

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
	$.post("/"+service+"/"+ENV.LadderName, params, function(data){

		try{
			data = eval("("+data+")");
			if (data.Status=="SUCCESS"){
				scallback(data.Data);
			}else{

				if (errcallback) errcallback(data.Message, data.Data);
				else alert ("ERROR during "+service+"\n"+data.Message);
			}
		}catch(ex){
			alert ("ERROR during "+service+"\n"+ex);
		}
		
	});
	
}; // saveSetting()


