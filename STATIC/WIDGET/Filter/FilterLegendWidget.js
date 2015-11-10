
Widget.FilterLegendWidget = function(cChildRowType){

	
	var getParents = function(){
		var map = {};
		var arr = [];
		
		if (ENV.ParentId){
			map[ENV.ParentId] = 1;
			arr.push(ENV.ParentId);
		}
		for (var i=0; i<ENV.AllRows.length; i++){
			var pid = ENV.AllRows[i][ENV.PARENTID];
			if (map[pid]) continue;
			map[pid] = 1;
			arr.push(pid);
		}
		return arr;
	};
	
	this.render = function(div){
		
		var parentContext = Utils.getParentContextByName(cChildRowType);
		if (!parentContext){
			//div.html("<span class='nobr'>Showing "+ cChildRowType+"s</span>");
			div.html("");
			return;
		}

		var allParentsArr = getParents();
		var totalParents = allParentsArr.length;
		
		var htm = [];
		/*
		htm.push("<span class='nobr'>");
		htm.push("Showing "+ENV.AllRows.length+" "+ cChildRowType+"s for ");
		htm.push(totalParents + " "+parentContext.Name);
		htm.push("</span>");
		*/
		
		if (totalParents==1){
			var params = {};
			params[ENV.ROWID] = allParentsArr[0];
			var url = SERVER.url(parentContext.Name, params); 

			htm.push("<br/><a class='nobr' href='");
			htm.push(url);
			htm.push ("'>back to the parent "+parentContext.Name);
			htm.push (": " + allParentsArr[0]);
			htm.push("</a>");			
		}
		

		
		div.html(htm.join(""));
		
	};

	

};
 
