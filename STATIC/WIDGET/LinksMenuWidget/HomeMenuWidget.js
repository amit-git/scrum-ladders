
Widget.HomeMenuWidget = function(andParams){
 
	var menuTableId = Utils.getUid();
	
	var createMenuLabel = function (rowType, url){
		var menuLabel = "";
		
		var arr = url.split("/");
		for (var i=0; i<arr.length; i++){
			var token = $.trim(arr[i]);
			var pair = token.split(":");
			if (pair.length!=2) continue;
			var k = $.trim(pair[0]);
			var v = $.trim(pair[1]);
			if (k.length==0 || v.length==0) continue;
			
			if (menuLabel.length>0) menuLabel += ",";
			menuLabel += unescape(v);
			
		}//for i
		
		if (menuLabel.length>0) menuLabel = "("+menuLabel+")";
		
		rowType = rowType+"s";
		rowType = rowType.replace("ys", "ies");
		
		return rowType + menuLabel;
	}

	
	this.render = function(div){
		var editBtnId = Utils.getUid();

		var htm1 = []; 
		
		htm1.push("<table id='"+menuTableId+"'>");
		htm1.push("<tr>");
		htm1.push("	<td><a href='/SETUP' class='top_menu_table' >All Ladders</a></td>");
		htm1.push("	<td  id='"+editBtnId+"' class='top_menu_table'>Edit Schema</td>");
		

		for (var i=0; i<ENV.LadderSchema.length; i++){
			var rowType = ENV.LadderSchema[i].Name;

			var url = ENV.SavedFilters[rowType+"_ROWS"]
			if (!url) url = SERVER.url(rowType);

			var menuLabel = createMenuLabel(rowType, url)
			htm1.push("<td><a href='"+url+"' class='top_menu_table'>"+ menuLabel +" </a></td>")
		}//for i
		
		//Rollups
		for (var i=0; i<ENV.LadderSchema.length; i++){
			var rowType = ENV.LadderSchema[i].Name;
			var sch = ENV.LadderSchema[i].Schema;
			for (var j=0; j<sch.length; j++){
				if (sch[j].ColumnType=="Rollup"){
					//Show menu as long there is atleast one Rollup in the level
					htm1.push("<td><a href='/ROLLINDEX/"+ENV.LadderName+"/"+rowType+"' class='top_menu_table'>"+rowType +" Rollups</a></td>")
					break;
				}
			}//for j
		}//for i
		
		
		htm1.push("</tr>");
		htm1.push("</table>");
	
		div.html(htm1.join(" "));
		
		$("#"+editBtnId).click(function(){
		    //$( "#dialog" ).dialog();
			var diaOptions = { minHeight: 600, minWidth:700, 
					height: 600, 
					modal: true, 
					open: function (event, ui) {$('#dialog').css('overflow', 'hidden');} 
			};
			
			var ifrm = $("<iframe />").attr("src", "/SCHEMA/"+ENV.LadderName+"/"+ENV.RowType);
			ifrm.width(diaOptions.minWidth-20).height(diaOptions.minHeight-20);
			ifrm.attr("scrolling", "no");
			ifrm.attr("frameBorder", "0");
			
			$("#dialog").append(ifrm).dialog(diaOptions);
			
			//$("#dialog").attr("src", "/SCHEMA/"+ENV.LadderName).dialog(diaOptions);
		});
		
		var btns = $('#'+menuTableId).find('a.top_menu_table');
		for (var i=0; i<btns.length; i++){
			if (btns[i].href == document.location.href){
				//alert("Found "+btns[i].href);
				$(btns[i]).addClass("top_menu_selected");
			}
		}



	}//render()
	 
};
 
