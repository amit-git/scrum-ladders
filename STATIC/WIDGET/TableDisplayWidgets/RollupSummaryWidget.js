
Widget.RollupSummaryWidget = function(){
	var me = this;
	
	var combineMaps = function(map1, map2){
		var map3 = {};
		for (var k in map1){
			if (!map3[k]) map3[k]= 0;
			map3[k] += map1[k];
		}
		for (var k in map2){
			if (!map3[k]) map3[k]= 0;
			map3[k] += map2[k];
		}

		return map3;
	}//combineMaps()
	

	
	this.render = function(div, field, rollupData){
 		div.html("");
 		
 		var masterData = null;

 		for (var rowId in rollupData){
 			var data = rollupData[rowId];
 			if (!data) continue;
 			var isNum = Utils.isNumeric(data);

 			if (masterData==null){
 				if (isNum) masterData = 0;
 				else masterData = {};
 			}

 			if (isNum) masterData += data;
 			else masterData = combineMaps(masterData, data);
 		}
		var w = new Widget.RollupWidget2();
		w.render(div);
		w.renderRollup(field, masterData);
 		
 	
	};//render()


};