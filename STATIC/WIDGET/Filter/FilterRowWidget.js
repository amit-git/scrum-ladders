
Widget.FilterRowWidget = function(rowType, andParams){
	if (!rowType){
		alert("Widget.FilterRowWidget must take rowType");
		return;
	}
	var ID_PREFIX = Utils.getUid();
	
	var cContext = Utils.getContextByName(rowType);
	var cCellWidgets = [];
			
	this.render = function(div){
		

		var out = [];
		out.push("<span>");
		
		out.push("<a href='"+SERVER.url(rowType)+"'>reset</a>&nbsp;&nbsp;");
		//out.push("Parent:<select id='"+ ID_PREFIX +"Parents'></select> &nbsp;&nbsp;&nbsp;");
		
		for (var i=0; i<cContext.Schema.length; i++){
			var field = cContext.Schema[i];
			if (!field.Args || !Utils.isArray(field.Args)) continue;
			
			out.push(field.Name);
			out.push(" <span id='"+ ID_PREFIX + i +"'  class='nobr'></span> &nbsp;&nbsp;&nbsp;");
			
		}//for i
		out.push("</span>");
		div.html(out.join(" ") );
		
		cCellWidgets = [];
		for (var i=0; i<cContext.Schema.length; i++){
			var field = cContext.Schema[i];
			if (!field.Args || !Utils.isArray(field.Args)) continue;
			var cellDiv = $("#"+ ID_PREFIX + i);

			var cellWidget = new field.Widget(field.Args);
			cellWidget.field = field;
			cellWidget.render(cellDiv, field, andParams);
			cellWidget.getEventManager().attachEvent ("change", gotoUrl, field);
			
			cCellWidgets.push(cellWidget);

			if (!andParams || !andParams[field.Name]){
				//_getDef(field.Name, cellWidget);
			}
			
		}//for i

		
	};//render()
	
	var _getDef = function(name, widget){
		SERVER.getSetting ("_DEF_FILTER_SET_"+name, function(defVal){
			widget.setVal(defVal);
		}, function(){});
		
	}
	
	function gotoUrl(sourceWidget){
		
		//SERVER.saveSetting ("_DEF_FILTER_SET_"+sourceWidget.field.Name, sourceWidget.getVal(), function(){}, function(){});
		
		var url = [SERVER.url(rowType)];
		
		for (var j=0; j<cCellWidgets.length; j++){
			var v = cCellWidgets[j].getVal();
			if (!v) continue;
			url.push("/");
			url.push(cCellWidgets[j].field.Name+":"+v);
		}
		window.location = url.join("");
	}		
	
};
 