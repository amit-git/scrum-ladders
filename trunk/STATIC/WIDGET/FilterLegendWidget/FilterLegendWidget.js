
Widget.FilterLegendWidget = function(cChildRowType){

	var getParents = function(){
		var map = {};
		var arr = [];
		for (var i=0; i<ENV.AllRows.length; i++){
			var pid = ENV.AllRows[i][ENV.PARENTID];
			if (map[pid]) continue;
			map[pid] = 1;
			arr.push(pid);
		}
		return arr;
	};//
	this.render = function(div){
		
		var parentContext = Utils.getParentContextByName(cChildRowType);
		if (!parentContext){
			div.html("Showing "+ cChildRowType+"s");
			return;
		}

		var allParentsArr = getParents();
		var totalParents = allParentsArr.length;
		
		var htm = [];
		htm.push("Showing "+ENV.AllRows.length+" "+ cChildRowType+"s for ");
		htm.push(totalParents + " "+parentContext.Name);

		if (totalParents==1){
			var params = {};
			params[ENV.ROWID] = allParentsArr[0];
			var url = SERVER.url(parentContext.Name, params); 

			var htm = [];
			htm.push("<br/><a href='");
			htm.push(url);
			htm.push ("'>back to the parent "+parentContext.Name);
			htm.push("</a>");			
		}
		

		
		div.html(htm.join(""));
		
	};

	
	/*
	function dispLadderLegend(id){
		var rowArr = [];
		while(id && id!="ROOT"){
			var row = Utils.getRowFromId (id);
			if (!row) break;
			rowArr.push(row);
			id = row["_parentId"];
		}
		
		
		
		var htm2 = [];
		htm2.push("<table>")
		
		var pad = 0;
		for (var i=rowArr.length-1; i>=0; i--){
			var row = rowArr[i];
			var rowType = row["_rowType"];
			var context = Utils.getChildContextByName(rowType);
			if (!context) continue; //This context row may have been deleted

			var childRowType = context.Name;

			var desc = childRowType +"s for "+rowType+": <i> "+row.Description+"</i>";

			var url = SERVER.url(childRowType, {"_parentId":row["_rowId"]});
			
			htm2.push("<tr><td style='padding-left:"+(pad++*20)+"px' >")
			htm2.push(" <a href='"+url+"'>"+ desc +" </a>");
			htm2.push("</td></tr>")

		}//for i

		htm2.push("</table>")
		return htm2.join(" ");
	}
	this._real_render = function(div){
		var parentContext = Utils.getParentContextByName(cChildRowType);
		var childContext = Utils.getContextByName(cChildRowType);
		var parentId = ENV.ParentId;		

		if (!parentContext){
			var pluChild = cChildRowType+"s";
			div.html("All "+pluChild);
			return;
		}
		 
		var htm = []; 

		if (parentId){
			htm.push( dispLadderLegend(parentId) );
		}else{
			htm.push("Showing "+ cChildRowType+"s");
		}
		div.html(htm.join(" "));
	}
	 */
};
 
