
Widget.ChildTableWidget = function(cRowType){
	if (!cRowType) throw new Utils.Exception("cRowType can't be "+ cRowType);
	
	var me = this;
	var cChildContext = Utils.getContextByName(cRowType);
	var cRowWidgetArr = [];
	
	this.renderRollups = function(rowArr){
		if (cRowWidgetArr.length != rowArr.length){
			
			throw new Utils.Exception("Rollups aren't the same size as rows: "+cRowWidgetArr.length+ "!="+ rowArr.length);
		}
		for (var i=0; i<cRowWidgetArr.length; i++){
			cRowWidgetArr[i].renderRollups(rowArr[i]);
		}
	}
	
	this.render = function(div, parentId, arr, childTableIdMap){
		if (!arr) arr = [];
		cRowWidgetArr = [];
		
		var tableId = Utils.getUid();
		_renderTableForContext ( div, tableId, cChildContext, parentId, arr);

		for (var i=0; i<arr.length; i++){
			var id = Utils.getUid();

			$('#'+tableId+' > tbody > tr:last').after("<tr id='"+ id +"'><td>Row at bottom</td></tr>");
			_renderRowWidget (id, div, arr, i);

			if (childTableIdMap){
				var childRowId = Utils.getUid();
				childTableIdMap[arr[i][ENV.ROWID]] = childRowId;

				$('#'+tableId+' > tbody > tr:last').after("<tr><td></td><td colspan=10 id='"+childRowId+"'></td></tr>");
			}

		}//for i
	};//render()

	var _renderTableForContext = function( containerDiv, tableId, tableContext, parentId, arr){

		var htm = [];
		htm.push("<table id='"+tableId+"' width='100%' class='ld_infobox'>");
		htm.push("<tr><td></td>");

		if (tableContext){
			for (var i=0; i<tableContext.Schema.length; i++){
				var field = tableContext.Schema[i];
				var fieldName = field.Name.replace(ENV.ROLLUP_PREFIX, "");
				htm.push("<td>"+fieldName+"</td>");
			}//for i
		}

		htm.push("</tr>");
		htm.push("</table>");
		var addNewId = Utils.getUid();
		htm.push("<div id='"+addNewId+"'> </div>");
		$(containerDiv).html(htm.join(" "));
		
		
		var r = new Widget.AddNewActionWidget(tableId, tableContext, parentId, arr);
		r.render($("#"+addNewId));
		
	};//_renderTableForContext

	


	
	var _renderRowWidget = function(id, div, arr, i){
		var pId = arr[i][ENV.PARENTID];
		
		var r = new Widget.Row(cChildContext.Name);
		cRowWidgetArr.push(r);

		r.rowIndex = i+1;
		r.render($("#"+ id), cChildContext.Schema, arr[i], arr[i-1], arr[i+1]);

		r.getEventManager().attachEvent ("update", function(source, newRowObj){
			r.render($("#"+ id), cChildContext.Schema, newRowObj);
			
		});
		
		attachSwapEvents(div, r, "moveUp", arr, i, i-1);
		attachSwapEvents(div, r, "moveDown", arr, i, i+1);

		r.getEventManager().attachEvent("delete", function(){
			arr.splice(i, 1);
			me.render (div, pId, arr);
 		});
		
	};//_renderRowWidget()
	
	var attachSwapEvents = function(div, r, command, arr, i, j){
		r.getEventManager().attachEvent (command, function(){
			
			var pId = arr[i][ENV.PARENTID];
			var t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
			me.render (div, pId, arr);

			me.getEventManager().notify("refresh");
			
		});
	};//

};




 