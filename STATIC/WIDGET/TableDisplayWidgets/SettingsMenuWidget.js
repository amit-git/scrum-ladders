Widget.SettingsMenuWidget = function(){
	var SETTINGS_MENU_ID = "SETTINGS_MENU_ID_xxxx";
	var me = this;

	
	var _attachEvent = function(menuDiv, id, currRow, destRow, event){
		if (!destRow) return;

		$("#"+id).click(function(){
			menuDiv.html("");
			swapPriority(currRow, destRow, event);
		});
	};//_attachEvent()
	
	this.render = function(div, currRow, prevRow, nextRow, firstRow, lastRow){
		var btnId = Utils.getUid();
		var menuId = Utils.getUid();
		div.html("<img id='"+btnId+"' src='/STATIC/images/Settings-icon.png' />");
		
		
		var menuDiv = $("#"+SETTINGS_MENU_ID);
		if (menuDiv.length==0){
			$(document.body).append("<div id='"+SETTINGS_MENU_ID+"'  style='position:absolute' />")
			menuDiv = $("#"+SETTINGS_MENU_ID);
			menuDiv.css("position", "absolute");
			menuDiv.css('cursor', 'pointer');
			
			menuDiv.mouseleave(function() {
				menuDiv.html("");
			});			
			
		}

		$("#"+btnId).mouseover(function(e){
            menuDiv.css("top", e.pageY);
            menuDiv.css("left", e.pageX);
            

			var htm = [];
			htm.push( "<table id='SettingsMenuId' >");
			if (firstRow && prevRow && prevRow!=firstRow) htm.push( "<tr><td><a id='SettingsMenu_moveTop'>Move to Top</a></td></tr>");
			if (prevRow) htm.push( "<tr><td><a id='SettingsMenu_moveUp'>Move Up</a></td></tr>");
			if (nextRow) htm.push( "<tr><td><a id='SettingsMenu_moveDown'>Move Down</a></td></tr>");
			if (lastRow && nextRow && nextRow!=lastRow) htm.push( "<tr><td><a id='SettingsMenu_moveBottom'>Move to Bottom</a></td></tr>");

			
			var parentRowType = Utils.getParentContextByName(currRow[ENV.ROWTYPE]);
			if (parentRowType){
				htm.push( "<tr><td>___________________</td></tr>");
				htm.push( "<tr><td><a id='SettingsMenu_changeParent'>Change Parent</a></td></tr>");
			}
			
			htm.push( "<tr><td>___________________</td></tr>");
			htm.push( "<tr><td><a id='SettingsMenu_delete'>Delete</a></td></tr>");
			htm.push( "</table>");

            
			menuDiv.html(htm.join(" "));

			_attachEvent (menuDiv, "SettingsMenu_moveTop", currRow, firstRow, "moveTop");
			_attachEvent (menuDiv, "SettingsMenu_moveUp", currRow, prevRow, "moveUp");
			_attachEvent (menuDiv, "SettingsMenu_moveDown", currRow, nextRow, "moveDown");
			_attachEvent (menuDiv, "SettingsMenu_moveBottom", currRow, lastRow, "moveBottom");
			
			/*
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
			*/

			$("#SettingsMenu_delete").click(function(){
				menuDiv.html("");
				deleteRowId(currRow[ENV.ROWID]);
			});
			
			
			$("#SettingsMenu_changeParent").click(function(){
				menuDiv.html("");
				changeParent(currRow, parentRowType);
			});
			

  		});
		
	}//render


	

	var changeParent = function(currRow, parentRowType){

		var currRowId = currRow[ENV.ROWID];
		var currParentId = currRow[ENV.PARENTID];

		SERVER.rows ({"_rowType": parentRowType.Name}, function(parentsData){
			var arr = [];
			var selectedIndex = 0;

			for (var i=0; i<parentsData.Rows.length; i++){
				var row = parentsData.Rows[i];
				arr.push(row[ENV.ROWID]+": "+Utils.getShortDescription(row));
				if (row[ENV.ROWID] == currParentId)
					selectedIndex = i;
			}//for i

			var description = currRowId +": "+Utils.getShortDescription(currRow);

			Utils.dialogArr (arr, "Change Parent", description, "Change", selectedIndex, function(i){
				if (selectedIndex==i) return;
				var newParentId = parentsData.Rows[i][ENV.ROWID];
				currRow[ENV.PARENTID] = newParentId;

				SERVER.update (currRowId, ENV.PARENTID, newParentId, false, function(){
					//success
					//alert("Changed Parent");
		    		me.getEventManager().notify("parentChanged");
					window.location.reload(true);
		    		
				}, function(){
					//error
					alert("Failed to change Parent");
		    		me.getEventManager().notify("parentChanged");
					window.location.reload(true);
		    		
				});

			});
			
			
		}, function(err){
			alert("Error fetching Parent rows ");
		}, true);
		

	};//changeParent()



	//deleteRowId
	function deleteRowId(rowId){
		
		var r=confirm("Really delete this? "+rowId);
		if (!r) return;
		
		SERVER.delete(rowId, function(data){
    		me.getEventManager().notify("delete");
		});
		

		/*
		var params = {};
		params[ENV.ROWID] = rowId;
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
    	var forKey = ENV.PRIORITY;

    	var rowId = r1[ENV.ROWID];
    	var newVal = r2[ENV.PRIORITY];
    	
    	//Do the first row
		SERVER.update(rowId, forKey, newVal, false, function(newRowData){
        	rowId = r2[ENV.ROWID];
        	newVal = r1[ENV.PRIORITY];
        	
    		SERVER.update(rowId, forKey, newVal, false, function(newRowData){
    			
        		//swap priorities
        		var t = r2[ENV.PRIORITY];
        		r2[ENV.PRIORITY] = r1[ENV.PRIORITY];
        		r1[ENV.PRIORITY] = t;
        		me.getEventManager().notify(event);
    		});
		});
	}//swapPriority()

};
 
