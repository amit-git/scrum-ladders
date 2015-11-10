
Widget.TableWidget = function(cRowType){
	if (!cRowType) throw new Utils.Exception("cRowType can't be "+ cRowType);
	
	var me = this;
	var cChildContext = Utils.getContextByName(cRowType);
	var cRowWidgetArr = [];
	
	var _groupByParentId = function(pId, arr, i, parentRollupId){
		
		var url = null;
		var parentContext = Utils.getParentContextByName(arr[i][ENV.ROWTYPE]);
		if (parentContext){
			var params = {};
			params[ENV.ROWID] = pId;
			url = SERVER.url(parentContext.Name, params);
			
		}
		
		var desc = arr[i]["_PARENT_DESCRIPTION"];

		var htm = [];
		if (url){
			htm.push("<td height=40px><a href='"+url+"' class='nobr'>"+pId+"</a></td>");
			htm.push("<td colspan=2><a href='"+url+"'>"+desc+"</a> </td>");
		}else{
			htm.push("<td height=40px>"+pId+"</td>");
			htm.push("<td colspan=2></td>");
		}
		htm.push("<td colspan=4><span id='"+parentRollupId+"' /></td>");
		
		//return "<td colspan=5 height=40px valign='bottom'><a href='"+url+"'>"+desc+"</a> <span id='"+parentRollupId+"' /></td>";
		
		return htm.join("");
		
	};//_groupByParentId


	var time = new Utils.TimeCounters();
	
	
	var _groupAllNumericValues = function(prevParentId, arr, endAt){
		try{
			if (endAt==null) return;
			if (endAt<0) return;
			if (!prevParentId) return;
			if (!arr[endAt])return
			
			
			var map = {};
			var i=endAt;
	
			//alert(arr[i][ENV.PARENTID] + "=="+prevParentId);
			
			while(arr[i][ENV.PARENTID]==prevParentId){
				for (var key in arr[i]){
					
					if (key.indexOf("_")==0) continue;
					var val = arr[i][key];
					if (!val) continue;
	
					if ($.isNumeric(val)){
						if (!map[key]) map[key] = 0;
						map[key] += parseFloat(val);
					}else{
						delete map[key];
					}
				}//for key
				
				i--;
				if (!arr[i]) break;
			}//while
			
			var htm = [];
			for (var key in map){
				htm.push(key + " = "+map[key])
			}
			$("#RollupId"+prevParentId).html( htm.join("") );
		}catch(ex){
			alert(ex);
			debugger;
		}
	};//_groupAllNumericValues();

	this.renderRollup = function(field, rollupData){

		for (var i=0; i<cRowWidgetArr.length; i++){
			var w = cRowWidgetArr[i];
			var rollupDataForRow = rollupData[w._rowId];
			w.renderRollup (field, rollupDataForRow);
		}//for i
	};//renderRollup()

	this.render = function(div, parentId, arr){
		time.start("All Table Render");
		
		if (!arr) arr = [];
		cRowWidgetArr = [];
		var tableId = Utils.getUid();
		
		time.start("Context Render");		
		_renderTableForContext ( div, tableId, cChildContext, parentId, arr);
		time.end("Context Render");		

		var prevParentId = null;

		var tableObj = $('#'+tableId);
		for (var i=0; i<arr.length; i++){

			time.start("Parent Grouping");

			//populate complete parent rollup
			_groupAllNumericValues (prevParentId, arr, i-1);
			
			//---group rows based on parent
			var pId = arr[i][ENV.PARENTID];
			if (pId != prevParentId){
				var parentTR = _groupByParentId (pId, arr, i, "RollupId"+pId);
				//$('#'+tableId+' > tbody > tr:last').after("<tr >"+ parentTR + "</tr>");
				tableObj.append("<tr >"+ parentTR + "</tr>");
				prevParentId = pId;
			}
			//-----group rows
			time.end("Parent Grouping");

			
			
			var id = Utils.getUid();
			time.start("Append row");
			//$('#'+tableId+' > tbody > tr:last').after("<tr id='"+ id +"'><td>Row at bottom</td></tr>");
			tableObj.append("<tr id='"+ id +"' class='tableRow'></tr>");
			 
			time.end("Append row");

			_renderRowWidget (id, div, arr, i);

		}//for i
		
		//populate last parent rollup
		_groupAllNumericValues (prevParentId, arr, arr.length-1);
		
		
		time.end("All Table Render");
		
		//alert(time);
	};//render()

	var _renderTableForContext = function( containerDiv, tableId, tableContext, parentId, arr){

		var htm = [];
		htm.push("<table id='"+tableId+"' width='100%' class='ld_infobox'>");
		htm.push("<tr class='tableHeaderRow'><td></td>");
		htm.push("<td></td>");//For child arrow

		if (tableContext){
			for (var i=0; i<tableContext.Schema.length; i++){
				var field = tableContext.Schema[i];
				var fieldName = Utils.getNameLabel(field);
				if (field.ColumnType=="Notes") fieldName = "";
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
		r._rowId = arr[i][ENV.ROWID];
		
		cRowWidgetArr.push(r);
		//r.rowIndex = i+1;
		r.setContext (arr, i);

		r.render($("#"+ id), cChildContext.Schema, arr[i], arr[i-1], arr[i+1], time);
		
		r.getEventManager().attachEvent ("update", function(source, newRowObj){
			r.render($("#"+ id), cChildContext.Schema, newRowObj);
			
		});
		
		
		attachSwapEvents(div, r, "moveTop", arr, i, 0);
		attachSwapEvents(div, r, "moveUp", arr, i, i-1);
		attachSwapEvents(div, r, "moveDown", arr, i, i+1);
		attachSwapEvents(div, r, "moveBottom", arr, i, arr.length-1);

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




 