

$( document ).ready(function() {
	
	try{
	
		ENV.Setup(); //Must call this in the begining or all sort of things will stop working.
		
		var fields = [ENV.ROWID, ENV.PARENTID, ENV.ROWTYPE, ENV.PRIORITY, ENV.GRANDPAID];
		var schemaArr = Utils.getContextByName(ENV.RowType).Schema;
		for (var i=0; i<schemaArr.length; i++){
			if (schemaArr[i].ReadOnly) continue;
			fields.push(schemaArr[i].Name);
		}
		ENV.InputParams["FIELDS"] = fields.join(",");

		
		SERVER.rows (ENV.InputParams, function(data){
			
			var startTime = (new Date()).getTime();
	
			ENV.AllRows = data.Rows;
			ENV.AllParentRows = data.ParentRows;
			wirePage();
	
			var endTime = (new Date()).getTime();
			//alert(endTime-startTime);
		});
		
	}catch(ex){
		$(document.body).html("ERROR:"+ex);
	}
});
	
	
	var wirePage = function(){
		try{
			//Event Manager for delayed data loading for longer fields
			//ENV.dataRefreshEventManager = new EventManager();
			
			ENV.childTable = new Widget.ChildTableWidget(ENV.RowType);
			
			var childTableIdMap = {};
			ENV.childTable.render($("#mainDiv"), ENV.ParentId, ENV.AllRows, childTableIdMap );
			
			/*
			{
				var w = new Widget.RowsTable(ENV.RowType, ENV.AllRows);
				w.render($("#mainDiv") );
				w.getEventManager().attachEvent ("rollupchange", function(rollupWidget){
					summaryWidget.setRollupWidget(rollupWidget);
				});
			}
			*/
			var w = new Widget.LinksMenuWidget();
			w.render($("#LinksMenuDiv") );

			var w = new Widget.FilterLegendWidget(ENV.RowType);
			w.render($("#FilterLegendDiv") );
	
			var fakeRow = {};
			var v = ""+ENV.InputParams["AND"];
			var arr = v.split(",");

			for (var i=0; i<arr.length; i++){
				var pair = Utils.splitPair(arr[i], ":");
				fakeRow[pair[0]] = pair[1];
			}
			var w = new Widget.FilterRowWidget(ENV.RowType, fakeRow);
			w.render($("#filterDiv") );
			
			window.setTimeout(fetchAndRenderRollups, 2000);
			
		}catch(ex){
		
			alert("ERROR "+ex);
		}
	};//wirePage
	

	function fetchAndRenderRollups(){
		var fields = [ENV.ROWID, ENV.PARENTID, ENV.ROWTYPE, ENV.PRIORITY, ENV.GRANDPAID];
		var schemaArr = Utils.getContextByName(ENV.RowType).Schema;
		for (var i=0; i<schemaArr.length; i++){
			if (schemaArr[i].ReadOnly){	//Get only Rollups
				fields.push(schemaArr[i].Name);
			}
		}
		ENV.InputParams["FIELDS"] = fields.join(",");
		
		SERVER.rows (ENV.InputParams, function(data){
			ENV.childTable.renderRollups(data.Rows);
			var summaryWidget = new Widget.RollupSummaryWidget();
			summaryWidget.render($("#RollupSummaryDiv"), data.Rows);

		});
		
	}//fetchAndRenderRollups

	