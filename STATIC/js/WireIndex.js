
$( document ).ready(function() {
	
	try{
	
		ENV.Setup(); //Must call this in the begining or all sort of things will stop working.
		
		var fields = null;
		/*
		var fields = [ENV.ROWID, ENV.PARENTID, ENV.ROWTYPE, ENV.PRIORITY, ENV.GRANDPAID];
		var schemaArr = Utils.getContextByName(ENV.RowType).Schema;
		for (var i=0; i<schemaArr.length; i++){
			if (schemaArr[i].ReadOnly) continue;
			fields.push(schemaArr[i].Name);
		}
		ENV.InputParams["FIELDS"] = fields.join(",");
		*/

		SERVER.rows (ENV.InputParams, function(data){
			
			var startTime = (new Date()).getTime();
	
			ENV.AllRows = data.Rows;
			ENV.AllParentRows = data.ParentRows;
			
			
			wirePage();
	
			//var endTime = (new Date()).getTime();
			//alert(endTime-startTime);
		}, function(err){
			alert("Error fetching ROWS ");
		}, true);
		
	}catch(ex){
		$(document.body).html("ERROR:"+ex);
	}
});
	
	
	var wirePage = function(){
		try{
			
			var fakeRow = ENV.InputParams;
			/*
			var fakeRow = {};
			debugger;
			var v = ""+ENV.InputParams["AND"];
			var arr = v.split(",");
			for (var i=0; i<arr.length; i++){
				var pair = Utils.splitPair(arr[i], ":");
				fakeRow[pair[0]] = pair[1];
			}
			*/
			
			//Event Manager for delayed data loading for longer fields
			//ENV.dataRefreshEventManager = new EventManager();
			
			ENV.childTable = new Widget.TableWidget(ENV.RowType);
			ENV.childTable.render($("#mainDiv"), ENV.ParentId, ENV.AllRows);

			//Render home links
			var w = new Widget.HomeMenuWidget(fakeRow);
			w.render($("#topMenuDiv") );
			
			
			//Render tree of ladder
			//var w = new Widget.LinksMenuWidget();
			//w.render($("#LinksMenuDiv") );
			
			var w = new Widget.FilterLegendWidget(ENV.RowType);
			w.render($("#FilterLegendDiv") );
	
			var w = new Widget.FilterRowWidget(ENV.RowType, fakeRow);
			w.render($("#filterDiv") );


			//Get rollups aynchroniously. We don't need to wait for them.
			window.setTimeout(fetchAndRenderRollups, 2000);
			
			
		}catch(ex){
		
			alert("wirePage() ERROR "+ex);
		}
	};//wirePage
	
	
	function fetchAndRenderRollups(){

		if (ENV.AllRows.length==0) return;

		var parentIds = [];
		for (var i=0; i<ENV.AllRows.length; i++) parentIds.push(ENV.AllRows[i][ENV.ROWID]);
		parentIds = parentIds.join(",");

		
		//moving the context in the inner function
		var _call = function(field){
			if (field.ColumnType!="Rollup") return;
			
			var rollupSummId = "RLSummary"+field.Name;
			
			SERVER.fetchRollup (parentIds, field.RollupTarget, 
					field.RollupColumn, field.RollupType, field.RollupSumColumn,
					function(rollupData){
						var rsw = new Widget.RollupSummaryWidget();
						rsw.render($("#"+rollupSummId), field, rollupData);

						ENV.childTable.renderRollup(field, rollupData);
					},
					function(data){alert("Error in fetching rollups data:"+data);});
		}
		
		
		var schemaArr = Utils.getContextByName(ENV.RowType).Schema;

		var htm = [];
		htm.push("<table><tr style='font-size:xx-small;'>");
		for (var i=0; i<schemaArr.length; i++){
			var field = schemaArr[i];
			if (field.ColumnType!="Rollup") continue;
			var rollupSummId = "RLSummary"+field.Name;
			htm.push("<td style='padding-left:10px;font-weight:bold;'>"+field.Name+"</td>");
			htm.push("<td id='"+rollupSummId+"'></td>");
		}
		htm.push("</tr></table>");
		$("#RollupSummaryDiv").html(htm.join(""));
		
		for (var i=0; i<schemaArr.length; i++){
			_call(schemaArr[i]);
		}
		

	}//fetchAndRenderRollups

