
$( document ).ready(function() {
	
	try{
	
		ENV.Setup(); //Must call this in the begining or all sort of things will stop working.
		
		//Render home links
		var w = new Widget.HomeMenuWidget();
		w.render($("#topMenuDiv") );
		
		
		SERVER.rows (ENV.InputParams, function(data){
			ENV.AllRows = data.Rows;
			ENV.AllParentRows = data.ParentRows;
			
			var parentIds = [];
			for (var i=0; i<ENV.AllRows.length; i++) parentIds.push(ENV.AllRows[i][ENV.ROWID]);
			parentIds = parentIds.join(",");
			
			var schemaArr = Utils.getContextByName(ENV.RowType).Schema;
			for (var i=0; i<schemaArr.length; i++) fetchAndRenderRollup(parentIds, schemaArr[i]);

		}, function(err){
			alert("Error fetching ROWS ");
		}, true);

		var w = new Widget.FilterRowWidget(ENV.RowType, ENV.InputParams);
		w.render($("#filterDiv") );
	 
	}catch(ex){
		$(document.body).html("ERROR:"+ex);
	}
});
	
	
function fetchAndRenderRollup(parentIds, field){
	if (field.ColumnType!="Rollup" || ENV.AllRows.length==0) return;

	SERVER.fetchRollup (parentIds, field.RollupTarget, 
			field.RollupColumn, field.RollupType, field.RollupSumColumn,
			function(data){
				if (field.RollupType=="group") renderRollupGroupTable(field, data);
				else  renderRollupSumTable(field, data);
			},
			function(data){alert("Error in fetching rollups data:"+data);});
 

}//fetchAndRenderRollups


var renderRollupSumTable = function(field, rollupData){

	var htm = [];
	htm.push("<br/><h1>"+field.Name+" : sum("+field.RollupColumn+") </h1><br/>");
	htm.push("<table border=1 cellspacing=0 cellpadding=10>");
	
	htm.push("<tr style='font-weight:bolder;'><td></td><td>"+ENV.RowType+"</td><td>Sum</td></tr>");

	for (var rowId in rollupData){
		var val = rollupData[rowId];
		if (!val) val = 0;
		
		var desc = Utils.getShortDescription(Utils.getRowFromId(rowId));
		htm.push("<tr><td>"+rowId+"</td>  <td>"+desc+"</td>  <td>"+val+"</td></tr>");
	}
	htm.push("</table>");

	$("#mainDiv").append(htm.join(""));

}//renderRollupSumTable()

var renderRollupGroupTable = function(field, rollupData){
	//var groupsArr = field.Args;

	var groupsArr = Utils.getColumnSchema (field.RollupTarget, field.RollupColumn).Args;
	var colorArr = Utils.getColumnSchema (field.RollupTarget, field.RollupColumn).Colors;

	/*
	var groupsMap = {};
	for (var rowId in rollupData){
		if (!rollupData[rowId]) continue;
		for (var key in rollupData[rowId]){
			if (groupsMap[key]) continue;
			groupsMap[key] = 1;
			groupsArr.push(key);
		}
	}
	*/
	
	var htm = [];
	htm.push("<br/><h1>"+field.Name+" : sum("+field.RollupSumColumn+") group by "+field.RollupColumn+"</h1><br/>");
	htm.push("<table border=1 cellspacing=0 cellpadding=10>");
	
	htm.push("<tr style='font-weight:bolder;'><td></td><td>"+ENV.RowType+"</td>");
	for (var i=0; i<groupsArr.length; i++){
		var color = colorArr?colorArr[i]:null;
		color = color?"style='background-color:"+color+"'":"";
		htm.push("<td "+color+">"+groupsArr[i]+"</td>");
	}
	htm.push("<tr>");

	for (var rowId in rollupData){
		if (!rollupData[rowId]) continue;
		htm.push("<tr><td>"+rowId+"</td>");
		
		var desc = Utils.getShortDescription(Utils.getRowFromId(rowId));
		htm.push("<td>"+desc+"</td>");
		
		for (var i=0; i<groupsArr.length; i++){
			var key = groupsArr[i];
			var val = rollupData[rowId][key];
			if (!val) val = 0;
			
			var color = colorArr?colorArr[i]:null;
			color = color?"style='background-color:"+color+"'":"";
			
			htm.push("<td "+color+">"+val+"</td>");
		}
		htm.push("<tr>");
	}
		
	htm.push("</table>");

	$("#mainDiv").append(htm.join(""));
}//renderRollupGroupTable()

 