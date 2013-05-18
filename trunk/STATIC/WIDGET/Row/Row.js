Widget.Row = function(rowType, envParentId){
	this.rowIndex = 0 ;
	var cCellWidgetArr = [];
	var rowData = null;
	var me = this;
	
	if (!rowType){
		alert("Widget.Row must take rowType");
		rowType = ENV.RowType;
		return;
	}

	if (!envParentId) envParentId = ENV.ParentId;
	
	var getCellId = function(field, pRowData){
		var cellPrefix = pRowData?pRowData["_rowId"]:"New";
		field._cellId = field.Name.replace(/\W/g, '_');
		return field._cellId+"_"+cellPrefix;
		
	};

	this.renderRollups = function(rollupRowData){
		if (!rollupRowData) return;
		for (var i=0; i<cCellWidgetArr.length; i++){
			var cell = cCellWidgetArr[i];
			if (cell.renderRollups){
				cell.renderRollups(rollupRowData);
			}
		}
	}

	
	var renderIndexCell = function(rowId, rowData, out){
		if (rowData){
			var childContextObj = Utils.getChildContextByName(rowType);
			var pri = "Priority: "+rowData[ENV.PRIORITY];

			out.push("<td>");
			if (childContextObj)
			{
				var url = SERVER.url(childContextObj.Name, {"_parentId":rowId});
				out.push("<a href='"+url+"'><span class='ld_link' title='"+pri+"' >"+rowType + " "+ me.rowIndex+" </span></a>");
			}else{
				out.push("<span class='ld_link'  title='"+pri+"' >"+rowType + " "+ me.rowIndex+" </span>");
			}

			var settingsId = "settingsDivId"+rowId;
			out.push("<span id='"+settingsId+"'></span>");
			out.push("</td>");
		}else{
			out.push("<td style='background-color:red'>New "+rowType+"</td> ");
		}		
	}//renderIndexCell()
	
	var renderSettingsMenu = function(rowId, currRow, prevRow, nextRow){
		var settingsId = "settingsDivId"+rowId;
		var menuWidget = new Widget.SettingsMenuWidget();
		menuWidget.render($("#"+settingsId), currRow, prevRow, nextRow);

		menuWidget.getEventManager().forwardEvent("moveUp", me.getEventManager());
		menuWidget.getEventManager().forwardEvent("moveDown", me.getEventManager());
		menuWidget.getEventManager().forwardEvent("delete", me.getEventManager());

	}//renderSettingsMenu()
	
	this.render = function(div, schema, currRow, prevRow, nextRow){
		
		rowData = currRow;
		var rowId = rowData?rowData["_rowId"]:"";
		cCellWidgetArr = [];

		var out = [];

		renderIndexCell(rowId, rowData, out);

		for (var i=0; i<schema.length; i++){
			var field = schema[i];
			out.push("<td id='"+ getCellId(field, rowData) +"'>....</td> ");
		}//for i

		div.html(out.join(" ") );
		renderSettingsMenu(rowId, currRow, prevRow, nextRow);
		

		for (var i=0; i<schema.length; i++){
			var field = schema[i];
			var cellDiv = $("#"+ getCellId(field, rowData));

			var cellWidget = new field.Widget(field.Args);
			cCellWidgetArr.push(cellWidget);
			cellWidget.field = field;
			cellWidget.rowIndex = me.rowIndex;
			cellWidget.render(cellDiv, field, rowData);

			//Add some color
			if (cellWidget.getColor && field.Colors){
				var color = cellWidget.getColor(field, rowData);
				if (color) div.css('background-color', color);
			}
			
			var callbackFun = rowData?updateCallback:insertCallback;
			cellWidget.getEventManager().attachEvent ("change", callbackFun, field);

		}//for i

	};//render()

	
	var insertCallback = function(sourceWidget){
 		var val = sourceWidget.getVal();

		var params = {};
		params[sourceWidget.field.Name] = val;
	
		//Insert a new Row
		if (val){
 			if (!Utils.getParentContextByName(rowType)) params[ENV.PARENTID] = "ROOT";
 			else params[ENV.PARENTID] = envParentId;

 			params[ENV.ROWTYPE] = rowType;
 					
 			SERVER.insert(params, function(newRowData){
 				me.getEventManager().notify ("insert", me, newRowData);
 			});

		}else{
			alert (sourceWidget.field.Name + " can't be NULL into a new Row");
			return;
		}

	};//insertCallback()
	
	
	var updateCallback = function(sourceWidget){
 		var val = sourceWidget.getVal();

		var params = {};
		params[sourceWidget.field.Name] = val;
		
		//Update an existing row 
		params["_rowId"] = rowData["_rowId"];
		
		SERVER.update(params, function(newRowData){
			me.getEventManager().notify ("update", me, newRowData);
		});
		

	};//updateCallback()
	
	this.getVal = function(){
		
	}
	
	this.setVal = function(inputObj){
		
	} 
	
};


