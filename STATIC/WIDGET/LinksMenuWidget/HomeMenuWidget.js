
Widget.HomeMenuWidget = function(andParams){
 
	
	this.render = function(div){
		var editBtnId = Utils.getUid();

		var htm1 = []; 
		
		htm1.push("<table>");
		htm1.push("<tr>");
		htm1.push("	<td><a href='/SETUP' class='top_menu_table' >All Ladders</a></td>");
		htm1.push("	<td  id='"+editBtnId+"' class='top_menu_table'>Edit Schema</td>");
		

		for (var i=0; i<ENV.LadderSchema.length; i++){
			var rowType = ENV.LadderSchema[i].Name;

			var url = ENV.SavedFilters[rowType+"_ROWS"]
			if (!url) url = SERVER.url(rowType);

			
			var rtPlural = rowType+"s";
			rtPlural = rtPlural.replace("ys", "ies");
			htm1.push("<td><a href='"+url+"' class='top_menu_table'>"+rtPlural +" </a></td>")
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
			}
		}
		
		
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

	}//render()
	 
};
 
