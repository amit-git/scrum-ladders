
Widget.FilterRowWidget = function(rowType, andParams){
	if (!rowType){
		alert("Widget.FilterRowWidget must take rowType");
		return;
	}
	var cContext = Utils.getContextByName(rowType);
	var cCellWidgets = [];
			
	this.render = function(div){
		

		var out = [];
		out.push("<span class='nobr'>");
		for (var i=0; i<cContext.Schema.length; i++){
			var field = cContext.Schema[i];
			if (!field.Args || !Utils.isArray(field.Args)) continue;
			
			//out.push(field.Name+" <span id='filerRowDivId"+ i +"'></span> &nbsp;&nbsp;&nbsp;");
			out.push(field.Name);
			out.push(" <span id='filerRowDivId"+ i +"'></span> &nbsp;&nbsp;&nbsp;");
			
		}//for i
		out.push("</span>");
		div.html(out.join(" ") );
		
		cCellWidgets = [];
		for (var i=0; i<cContext.Schema.length; i++){
			var field = cContext.Schema[i];
			if (!field.Args || !Utils.isArray(field.Args)) continue;
			var cellDiv = $("#filerRowDivId"+ i);

			var cellWidget = new field.Widget(field.Args);
			cellWidget.field = field;
			cellWidget.render(cellDiv, field, andParams);
			cellWidget.getEventManager().attachEvent ("change", gotoUrl, field);
			
			cCellWidgets.push(cellWidget);

		}//for i

		
	};//render()
	
	function gotoUrl(sourceWidget){
		
		var url = [SERVER.url(rowType)];
		
		for (var j=0; j<cCellWidgets.length; j++){
			var v = cCellWidgets[j].getVal();
			if (!v) continue;
			url.push(",");
			url.push(cCellWidgets[j].field.Name+":"+v);
		}
		window.location = url.join("");
	}		
	
};
 