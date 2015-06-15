var Widget = {};	//This will get populated by loading .js file for each widget
var Template = {};	//This will get populated by loadig .template file for each Widget
var ENV = {};
var Utils = {};

ENV.ROWID = "_rowId";
ENV.PARENTID = "_parentId";
ENV.ROWTYPE = "_rowType";
ENV.PRIORITY = "_priority";
ENV.CREATED_DATE = "_createdDate";
ENV.UPDATE_DATE = "_updateDate";
ENV.GRANDPAID = "_grandpaid";

Utils.getNameLabel = function(field){
	if (!field.Name) new Utils.Exception("Utils.getNameLabel("+field+") doesn't accept null Name");
	return field.Name.replace("_", " ");	
}
Utils.columnsArrToMapArr = function(twoDarr){
	var mapArr= [];
	for (var i=0; i<twoDarr.length; i++){
		if (!twoDarr[i]) continue;
		var map = {};
		var j=0;
		map.Name = twoDarr[i][j++];

		map.Validation = twoDarr[i][j++];
		map.MinLen = twoDarr[i][j++];
		map.MaxLen = twoDarr[i][j++];
		map.Values = twoDarr[i][j++];
		map.RollsUp = twoDarr[i][j++];
		
		mapArr.push(map);
	}

	return mapArr;

};//columnsArrToMapArr();

Utils.validateColumnEle = function (arrIndex, arrName, obj){
	if (!obj) throw new Utils.Exception(arrIndex+"th element is the "+arrName+" is NULL");
	if (!obj.Name) throw new Utils.Exception(arrIndex+"th element is the "+arrName+" doesn't have a .Name property");
	if( /[^a-zA-Z0-9_]/.test( obj.Name) ) throw new Utils.Exception(arrIndex+"th element is the "+arrName+" must have alphanumeric .Name property ("+obj.Name+")");
	
}

Utils.validateLadders = function(arr){
	
	function _validateArr(arrName, arrVals){
		if (!arrVals) throw new Utils.Exception("null "+arrName+":"+ arrVals);
		if (!arrVals.length || arrVals.length<1) throw new Utils.Exception(arrName+" must be an Array");
	}
	
	_validateArr("LADDER", arr);

	var rowTypesMap = {};
	for (var i=0; i<arr.length; i++) rowTypesMap[arr[i].Name] = 0;

	for (var i=0; i<arr.length; i++){
		Utils.validateColumnEle (i, "LADDER", arr[i]);
		var rowType = arr[i].Name;
		rowTypesMap[rowType]++;
		if (rowTypesMap[rowType]>1) throw new Utils.Exception(rowType+" occurs twice in the list. Not allowed.");

		var schemaArr = arr[i].Schema;
		_validateArr("Schema for '"+rowType+"'", schemaArr);

		for (var j=0; j<schemaArr.length; j++){
			var field = schemaArr[j];
			Utils.validateColumnEle (i, rowType, field);
			
			if (!field.ColumnType) throw new Utils.Exception(rowType+"."+field.Name+" doesn't have ColumnType");
			if ("Text Notes Select Rollup".indexOf(field.ColumnType)<0) throw new Utils.Exception(rowType+"."+field.Name+"'s ColumnType"+field.ColumnType +" isnt supported.");
			
		}//for j
		
	}//for i
	
	//throw "XXX";
};//validateLadders()

Utils.openPage = function(url){
	document.location = url;
}

Utils.hasSpecialChars = function(val){
	return !( (/^[\w&.-]+$/.test( val )) );
}

Utils.indexOf = function(arr, val){
	val = val.toLowerCase();

	var index = -1;
	$.each(arr, function(i, value) {
		value += "";
		if (index == -1 && value.toLowerCase()==val) {
			index = i;
			return false;
		}
	});
	return index;
};//inArray()


Utils.Exception = function(d){
    this.description = d;
    this.message = d;
    this.toString = function(){
    	return d;
    }
};

Utils.tryParse = function(o){
	var v = Number(""+o);
	if (!v || v==NaN) return 0;
	return v;
};

Utils.toString = function(map){
	var s = "";
	for (var k in map){
		s += k + ":"+map[k];
		s += " , "
	}
	return s;
};

Utils.getShortDescription = function(row){
	var desc = row.Description;
	if (desc && desc.length>150) desc = desc.substring(0, 150)+"...";
	return desc;
	
}

Utils.getRowFromId = function(rId){

	if (rId=="ROOT"){
		var map = {};
		map[ENV.ROWID] = "ROOT";
		return map;
	}
	

	if (ENV.IdLadder){
		for (var i=0; i<ENV.IdLadder.length; i++){
			var row = ENV.IdLadder[i];
			if (row[ENV.ROWID]==rId) return row;
		}	
	}
	

	for (var i=0; i<ENV.AllParentRows.length; i++){
		var row = ENV.AllParentRows[i];
		if (row[ENV.ROWID]==rId) return row;
	}	

	for (var i=0; i<ENV.AllRows.length; i++){
		var row = ENV.AllRows[i];
		if (row[ENV.ROWID]==rId) return row;
	}	
	return null;

}; //Utils.getRowFromId


Utils.isFunction= function (v) { var t = {};  return v && t.toString.call(v) === '[object Function]'; };
Utils.isArray = function(v) {	return (v && v instanceof Array); };
Utils.isNumeric = function( obj ) {
    return !jQuery.isArray( obj ) && (obj - parseFloat( obj ) + 1) >= 0;
}


var _guid = 0; 
Utils.getUid= function (){ return "_id"+ (new Date()).getTime() + "_"+(_guid++);} //need better guid function

Utils.splitPair = function(q, separator ){
	q = $.trim(""+q);
	var colon = q.indexOf(separator);
	if (colon <= 0) return [];

	var k = (q.substring(0, colon));
	var v = (q.substring(colon + 1));

	return [k, v];
};//splitPair
Utils.isEmpty = function(s){
	if (!s) return true;
	s += "";
	return (s=="null" || s =="undefined" || s=="");
};

//Must be called from the document.Load method.
ENV.Setup = function(){

	if (Utils.isEmpty(ENV.LadderName)){
		throw new Utils.Exception("ENV.LadderName is undefined : "+ ENV.LadderName);
		return;
	}
	if (Utils.isEmpty(ENV.RowType)){
		throw new Utils.Exception("ENV.RowType is undefined : "+ ENV.RowType);
		return;
	}
	
	var json = SERVER.get("/STATIC/SETTINGS/SCHEMA_"+ENV.LadderName+".txt");
	
	LADDER = null;
	LADDER = eval(json);
	Utils.validateLadders (LADDER);
	
	for (var j=0; j<LADDER.length; j++){
		var schema = LADDER[j].Schema;
		schema.MAP = {}; //Create a MAP for quick lookup

		for (var i=0; i<schema.length; i++){
			var field = schema[i];
			schema.MAP[field.Name] = field;	//map for quick lookup

			//_setupRollups (field, j);
			//set some default widgets
			if (!field.Widget )
			{
				if (field.ColumnType=="Rollup"){
					field.Widget = Widget.RollupWidget2;
				}else if (field.ColumnType=="Select" && Utils.isArray(field.Args)){
					field.Widget = Widget.SelectWidget;
				}else if (field.ColumnType=="Text"){
					field.Widget = Widget.TextEditor;
				}else if (field.ColumnType=="Notes"){
					field.Widget = Widget.LargeTextEditor;
				}else{
					debugger;
					throw new Utils.Exception("Unsupported ColumnType '"+field.ColumnType+"' for RowType '"+ ENV.RowType+"'");
				}

			}

		}//for i
	}

	var context = Utils.getContextByName(ENV.RowType);	
	
	if (!context){
		alert("Invalid RowType: "+ ENV.RowType);
		Utils.openPage("/SETUP");
		return;
	}

	for (var i=0; i<context.Schema.length; i++){
		var field = context.Schema[i];
		field._cellId = field.Name.replace(/\W/g, '_');
	}

	ENV.SetupWidgets();
	
}; //ENV.Setup()

ENV.SetupWidgets = function(){
	//Arm widgets with event maanger
	for (var name in Widget){
		var w = Widget[name];
		if (Utils.isFunction(w)){
			w.prototype.getEventManager = _newEventManagerFun;
			w.prototype.name = name;
		}
	}

};//ENV.SetupWidgets()

function _newEventManagerFun(){ if (!this._em)this._em=new EventManager();return this._em; }

Utils.getColumnSchema = function(levelName, columnName){
	var l = Utils.getContextByName(levelName);
	for (var i=0; i<l.Schema.length; i++){
		if (l.Schema[i].Name==columnName){
			return l.Schema[i]; 
		}
	}
	return null;
};//Utils.getContextByName()


Utils.getContextByName = function(name){
	for (var i=0; i<LADDER.length; i++){
		if (LADDER[i].Name == name) return LADDER[i];
	}
	return null;
};//Utils.getContextByName()

Utils.getParentContextByName = function(childName){
	for (var i=1; i<LADDER.length; i++){
		if (LADDER[i].Name == childName) return LADDER[i-1];
	}
	return null;
};//Utils.getParentContextByName()

Utils.getChildContextByName = function(parentName){
	for (var i=0; i<LADDER.length-1; i++){
		if (LADDER[i].Name == parentName) return LADDER[i+1];
	}
	return null;
};//Utils.getChildContextByName()


Utils.mapSize = function(obj) {
    var size = 0;
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

Utils.contrastingColor = function (color)
{
	function luma(color) // color can be a hx string or an array of RGB values 0-255
	{
	    var rgb = (typeof color === 'string') ? hexToRGBArray(color) : color;
	    return (0.2126 * rgb[0]) + (0.7152 * rgb[1]) + (0.0722 * rgb[2]); // SMPTE C, Rec. 709 weightings
	}
	function hexToRGBArray(color)
	{
	    if (color.length === 3)
	        color = color.charAt(0) + color.charAt(0) + color.charAt(1) + color.charAt(1) + color.charAt(2) + color.charAt(2);
	    else if (color.length !== 6)
	        throw('Invalid hex color: ' + color);
	    var rgb = [];
	    for (var i = 0; i <= 2; i++)
	        rgb[i] = parseInt(color.substr(i * 2, 2), 16);
	    return rgb;
	}

	return (luma(color) >= 165) ? '000' : 'fff';
};


Utils.TimeCounters = function(){
	var map = {};
	var startTimes = {};
	this.start = function(k){
		startTimes[k] = new Date().getTime();
	}
	this.end = function(k){
		if (!map[k]) map[k]=0;
		if (!startTimes[k]) throw new Utils.Exception("Cant end "+k);

		map[k]+= new Date().getTime()-startTimes[k];
	}

	this.toString = function(){
		var s = "";
		for (var k in map){
			s += k+"="+map[k];
			s += " , \n"
		}
		return s;
	}
};//Utils.TimeCounters

