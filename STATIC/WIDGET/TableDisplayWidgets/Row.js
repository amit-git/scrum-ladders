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
		var cellPrefix = pRowData?pRowData[ENV.ROWID]:"New";
		field._cellId = field.Name.replace(/\W/g, '_');
		return field._cellId+"_"+cellPrefix;
		
	};

	//	this.renderRollup = function(cellDiv, field, data){

	this.renderRollup = function(field, rollupDataForRow){
		if (!rollupDataForRow) return;
		for (var i=0; i<cCellWidgetArr.length; i++){
			var cell = cCellWidgetArr[i];
			if (cell.renderRollup && field.Name == cell.field.Name){
				cell.renderRollup(field, rollupDataForRow);
			}
		}//for i
	}//renderRollup

	
	var renderIndexCell = function(rowId, rowData, out){
		if (rowData){
			var childContextObj = Utils.getChildContextByName(rowType);
			var pri = "Priority: "+rowData[ENV.PRIORITY];

			out.push("<td align='right'><img src='/STATIC/images/ChildTreeBranch.png' /></td>");
			out.push("<td>");
			
			var title = rowId;
			//out.push(me.rowIndex)

			if (childContextObj)
			{
				var url = SERVER.url(childContextObj.Name, {"_parentId":rowId});
				out.push("<a href='"+url+"'><span class='ld_link' title='"+pri+"' >"+title+" </span></a>");
			}else{
				out.push("<span class='ld_link'  title='"+pri+"' >"+title+" </span>");
			}

			var settingsId = "settingsDivId"+rowId;
			out.push("<span id='"+settingsId+"'></span>");
			out.push("</td>");
		}else{
			out.push("<td align='right'><img src='/STATIC/images/ChildTreeBranch.png' /></td>");
			out.push("<td style='background-color:red'>New "+rowType+"</td> ");
		}		
	}//renderIndexCell()
	
	this.setContext = function(arr, i){
		me.rowIndex = i+1;

		me.currRow = arr[i];
		me.prevRow = arr[i-1];
		me.nextRow = arr[i+1];
		me.firstRow = arr[0];
		me.lastRow = arr[arr.length-1];
	}
	
	var renderSettingsMenu = function(rowId){
		var settingsId = "settingsDivId"+rowId;
		var menuWidget = new Widget.SettingsMenuWidget();
		menuWidget.render($("#"+settingsId), me.currRow, me.prevRow, me.nextRow, me.firstRow, me.lastRow);

		menuWidget.getEventManager().forwardEvent("moveTop", me.getEventManager());
		menuWidget.getEventManager().forwardEvent("moveUp", me.getEventManager());
		menuWidget.getEventManager().forwardEvent("moveDown", me.getEventManager());
		menuWidget.getEventManager().forwardEvent("moveBottom", me.getEventManager());
		menuWidget.getEventManager().forwardEvent("delete", me.getEventManager());

	}//renderSettingsMenu()
	
	this.render = function(div, schema, currRow, prevRow, nextRow, time){
		if (!time) time= new Utils.TimeCounters();

		time.start("render Index cell");

		rowData = currRow;
		var rowId = rowData?rowData[ENV.ROWID]:"";
		cCellWidgetArr = [];

		var out = [];

		renderIndexCell(rowId, rowData, out);
		time.end("render Index cell");


		time.start("Prep Settings menu");
		for (var i=0; i<schema.length; i++){
			var field = schema[i];
			out.push("<td id='"+ getCellId(field, rowData) +"'>....</td> ");
		}//for i

		div.html(out.join(" ") );
		time.end("Prep Settings menu");
		
		time.start("Render Settings menu");
		renderSettingsMenu(rowId);
		time.end("Render Settings menu");

		

		for (var i=0; i<schema.length; i++){
			
			time.start("render Cell-"+i+" widget");
			
			var field = schema[i];
			var cellDiv = $("#"+ getCellId(field, rowData));

			var cellWidget = new field.Widget(field.Args);
			cCellWidgetArr.push(cellWidget);
			cellWidget.field = field;
			cellWidget.rowIndex = me.rowIndex;
			cellWidget.render(cellDiv, field, rowData);
			time.end("render Cell-"+i+" widget");

			time.start("Add color");
			//Add some color
			if (cellWidget.getColor && field.Colors){
				var color = cellWidget.getColor(field, rowData);
				if (color) div.css('background-color', color);
			}
			time.end("Add color");
			
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
 		
		var rowid = rowData[ENV.ROWID];
		var key = sourceWidget.field.Name;
 		var val = sourceWidget.getVal();
 		var shouldRoll = (sourceWidget.field.RollsUp!=null);
		
		SERVER.update(rowid, key, val, true, function(newRowData){
			me.getEventManager().notify ("update", me, newRowData);
		});
		

	};//updateCallback()
	
	this.getVal = function(){
		
	}
	
	this.setVal = function(inputObj){
		
	} 
	
};


