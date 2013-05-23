Widget.SettingsMenuWidget = function(){
	var SETTINGS_MENU_ID = "SETTINGS_MENU_ID_xxxx";
	var me = this;

	this.render = function(div, currRow, prevRow, nextRow){
		var btnId = Utils.getUid();
		var menuId = Utils.getUid();
		div.html("<img id='"+btnId+"' src='/STATIC/images/Settings-icon.png' />");
		
		
		var menuDiv = $("#"+SETTINGS_MENU_ID);
		if (menuDiv.length==0){
			$(document.body).append("<div id='"+SETTINGS_MENU_ID+"'  style='position:absolute' />")
			menuDiv = $("#"+SETTINGS_MENU_ID);
			menuDiv.css("position", "absolute");
			menuDiv.css('cursor', 'pointer');
		}

		$("#"+btnId).click(function(e){
            menuDiv.css("top", e.pageY);
            menuDiv.css("left", e.pageX);
            

			var htm = [];
			htm.push( "<table id='SettingsMenuId' >");
			if (prevRow) htm.push( "<tr><td><a id='SettingsMenu_moveUp'>Move Up</a></td></tr>");
			if (nextRow) htm.push( "<tr><td><a id='SettingsMenu_moveDown'>Move Down</a></td></tr>");
			htm.push( "<tr><td>___________________</td></tr>");
			htm.push( "<tr><td><a id='SettingsMenu_delete'>Delete</a></td></tr>");
			htm.push( "</table>");

            
			menuDiv.html(htm.join(" "));
			
			if (prevRow){
				$("#SettingsMenu_moveUp").click(function(){
					menuDiv.html("");
					swapPriority(currRow, prevRow, "moveUp");
				});
			}

			if (nextRow){
				$("#SettingsMenu_moveDown").click(function(){
					menuDiv.html("");
					swapPriority(currRow, nextRow, "moveDown");
				});
			}
			
			$("#SettingsMenu_delete").click(function(){
				menuDiv.html("");
				deleteRowId(currRow[ENV.ROWID]);
			});			
  		});
		
	}//render
	
	 

	//deleteRowId
	function deleteRowId(rowId){
		
		var r=confirm("Really delete this? "+rowId);
		if (!r) return;
		
		SERVER.delete(rowId, function(data){
    		me.getEventManager().notify("delete");
		});
		

		/*
		var params = {};
		params["_rowId"] = rowId;
		$.post("/DELETE/"+ENV.LadderName+"/", params, function(data){
			
			data = eval("("+data+")");
			if (data.Status=="SUCCESS"){
				//location.reload();
        		me.getEventManager().notify("delete");

			}else if (data.Status=="ERROR"){
				alert("Delete failed: "+data.Message);
			}else{
				alert("Shouldn't reach here");
			}
			
		});
		*/
	}

	 	
	
	var swapPriority = function(r1, r2, event){
       	var params = {};
    	params[ENV.ROWID] = r1[ENV.ROWID];
    	params[ENV.PRIORITY] = r2[ENV.PRIORITY];
    	
    	//Do the first row
		SERVER.update(params, function(newRowData){
        	params[ENV.ROWID] = r2[ENV.ROWID];
        	params[ENV.PRIORITY] = r1[ENV.PRIORITY];
    		SERVER.update(params, function(newRowData){
        		//swap priorities
        		var t = r2[ENV.PRIORITY];
        		r2[ENV.PRIORITY] = r1[ENV.PRIORITY];
        		r1[ENV.PRIORITY] = t;
        		me.getEventManager().notify(event);
    		});
		});
	}//swapPriority()

};
 
