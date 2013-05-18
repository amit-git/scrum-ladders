
Widget.RollupSummaryWidget = function(){
	var me = this;
 
	var expandValueCountMap = function (valueMap, newMap){
		if (!valueMap) valueMap = {};
		for (var k in newMap){
			if (!valueMap[k]) valueMap[k] = 0;
			valueMap[k] += Utils.tryParse(newMap[k]);
		}
		return valueMap;
	};//expandValueCountMap()

	var getRollupFieldsMap = function(allRows, fieldsArr){
		var rollupFieldsMap = {};
		
		for (var i=0; i<allRows.length; i++){
			for (var j=0; j<fieldsArr.length; j++){
				var field = fieldsArr[j];
				var valueMap = rollupFieldsMap[field.Name];
				valueMap = expandValueCountMap (valueMap, allRows[i][field.Name]);

				rollupFieldsMap[field.Name] = valueMap;
			}//for fieldsArr
		}//for i rows
		return rollupFieldsMap;
	};//

	var getFieldsArr = function(){
		var arr = [];
		var schema = Utils.getContextByName(ENV.RowType).Schema;
		for (var j=0; j<schema.length; j++){
			var field = schema[j];
			if (field.RollsUp && field.Name.indexOf(ENV.ROLLUP_PREFIX)==0){
				arr.push(field);
			}
		}//for j
		return arr;
	};//getFieldsArr()
	
	this.render = function(div, allRows){
 		div.html("");

		if (allRows.length==0){
			return;
		}
		var fieldsArr = getFieldsArr();
		
		var rollupFieldsMap = getRollupFieldsMap(allRows, fieldsArr);
		
		var keyTrId = Utils.getUid();
		var valTrId = Utils.getUid();
		var table = $("<table class='rollupSummaryTab'><tr id='"+keyTrId+"'></tr><tr id='"+valTrId+"'></tr></table>");
		div.append(table);
		
		for (var j=0; j<fieldsArr.length; j++){
			var field = fieldsArr[j];
				
			var valTd = $("<td></td>");
			$("#"+valTrId).append(valTd);

			var fieldName = field.Name.replace(ENV.ROLLUP_PREFIX, "");
			
			var keyTd = $("<td>"+fieldName+"</td>");
			$("#"+keyTrId).append(keyTd);
			
			
			var w = new Widget.RollupWidget();
			w.backgroundColor = "FFFFFF";
			w.size = "200x40";
			w.render (valTd, field);
			w.renderRollups(rollupFieldsMap);
		}
 
	};//render()


};