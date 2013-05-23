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
ENV.ROLLUP_PREFIX = "_RLUP_";


Utils.validateLadders = function(arr){
	
	function _validateArr(arrName, arrVals){
		if (!arrVals) throw new Utils.Exception("null "+arrName+":"+ arrVals);
		if (!arrVals.length || arrVals.length<1) throw new Utils.Exception(arrName+" must be an Array");
	}
	
	
	
	function _validateArrEle (arrName, arrVals, arrIndex){
		var obj = arrVals[arrIndex];
		if (!obj) throw new Utils.Exception(arrIndex+"th element is the "+arrName+" is NULL");
		if (!obj.Name) throw new Utils.Exception(arrIndex+"th element is the "+arrName+" doesn't have a .Name property");
		if( /[^a-zA-Z0-9_]/.test( obj.Name) ) throw new Utils.Exception(arrIndex+"th element is the "+arrName+" must have alphanumeric .Name property ("+obj.Name+")");
	}

	
	_validateArr("LADDER", arr);

	for (var i=0; i<arr.length; i++){
		_validateArrEle("LADDER", arr, i);

		var schemaArr = arr[i].Schema;
		_validateArr("Schema for '"+arr[i].Name+"'", schemaArr);

		for (var j=0; j<schemaArr.length; j++){
			_validateArrEle(arr[i].Name, schemaArr, j);
		}//for j
		
	}//for i
	
	//throw "XXX";
};//validateLadders()

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

Utils.getRowFromId = function(rId){

	if (rId=="ROOT"){
		return {"_rowId":"ROOT"};
	}
	

	if (ENV.IdLadder){
		for (var i=0; i<ENV.IdLadder.length; i++){
			var row = ENV.IdLadder[i];
			if (row["_rowId"]==rId) return row;
		}	
	}
	

	for (var i=0; i<ENV.AllParentRows.length; i++){
		var row = ENV.AllParentRows[i];
		if (row["_rowId"]==rId) return row;
	}	

	for (var i=0; i<ENV.AllRows.length; i++){
		var row = ENV.AllRows[i];
		if (row["_rowId"]==rId) return row;
	}	
	return null;

}; //Utils.getRowFromId

/*
function loadDustTemplate(template, context){
	var id = Utils.getUid();
	var compiled = dust.compile(template, id);
	dust.loadSource(compiled);
	var htm = null;
	dust.render(id, context, function(err, out) {
		htm = out;
	});	
	
	return htm;

}
*/

 

Utils.isFunction= function (v) { var t = {};  return v && t.toString.call(v) === '[object Function]'; };
Utils.isArray = function(v) {	return (v && v instanceof Array); };
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
	return (s=="null" || s =="undefined");
};

var _setupRollups = function(field, j){
	if (!field.RollsUp) return;
	if (j<=0) return;

	var strct = {
				"Name": ENV.ROLLUP_PREFIX +field.Name, 
				"ChildRollupFieldName":field.Name,
				"RollsUp":field.RollsUp,
				"Widget": Widget.RollupWidget, 
				"ReadOnly":true
				};
	
	if (field.Colors){
		if (field.RollsUp=="group"){
			//strct.Widget = Widget.GroupRollupWidget;
		}

		strct.GroupRollupColorMap = {};
		for (var i=0; i<field.Args.length; i++){
			strct.GroupRollupColorMap[ field.Args[i] ] = field.Colors[i]; 
		}
	}
	
	for (var i=j-1; i>=0; i--){
		LADDER[i].Schema.push(strct);
	}

};//_setupRollups

//Must be called from the document.Load method.
ENV.Setup = function(){

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

			
			_setupRollups (field, j);
			//set some default widgets
			if (!field.Widget )
			{
				if ( Utils.isArray(field.Args)) {
					field.Widget = Widget.SelectWidget;
				}else{
					field.Widget = Widget.TextEditor;
				}
			}

		}//for i
	}

	var context = Utils.getContextByName(ENV.RowType);	
	
	if (!context){
		throw new Utils.Exception("context is NULL for ENV.RowType: "+ ENV.RowType);
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

