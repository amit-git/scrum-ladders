Widget.RollupWidget2 = function(){
	var me = this;
	var DEFAULT_COLORS = ["FFEBCD", "0000FF", "8A2BE2", "A52A2A", "DEB887", "5F9EA0", "7FFF00", "D2691E", "FF7F50", "6495ED", "FFF8DC", "DC143C", "00FFFF", "00008B", "008B8B", "B8860B", "A9A9A9", "006400", "BDB76B", "8B008B"];
	var guid = Utils.getUid();
	var jqDiv = null;

	//Get consistent color based on value. If green means open, it should always mean open
	var valColorMap = {};
	var valColorMapSize = 0;
	var getColor = function(key, field){
		if (valColorMap[key]) return valColorMap[key];
		
		var col = Utils.getColumnSchema(field.RollupTarget, field.RollupColumn);
		if (col && col.Args && col.Colors && col.Colors.length==col.Args.length){
			for(var i=0; i<col.Args.length; i++){
				valColorMap[col.Args[i]] =col.Colors[i]; 
			}//for i
		}
		if (valColorMap[key]) return valColorMap[key];
		
		if (valColorMapSize>=DEFAULT_COLORS.length) valColorMapSize = 0; 
		valColorMap[key] = DEFAULT_COLORS[valColorMapSize++];
		return valColorMap[key];
	}
	
	this.renderRollup = function(field, data){
		jqDiv.html("");

		if (!data) return;
		if (Utils.isNumeric(data)){
			jqDiv.html(data);
		}else{
			jqDiv.html(createGroupDivs (field, data));
		}
	};

	this.render = function(cellDiv){
		jqDiv = $(cellDiv);
		jqDiv.html("");
	}

	var getRollupVal = function(field, rolloverMap){
		if (rolloverMap){
			if (field.RollsUp=="sum"){
				var total = 0;
				for (var key in rolloverMap){
					var count = rolloverMap[key];
					total += Utils.tryParse(count);
				}
				return total;
			}else if (field.RollsUp=="group"){
 				return createGroupDivs(field, rolloverMap);
 			}else{
				return "Unsupported RollsUp val '"+field.RollsUp+"'";
			}
			
		}else{
			return "";
		}
		
	};

	var createGroupDivs = function(field, map){

		var i = 0;
		var htm = [];
		
 		for (var k in map){

			var colorVal = field.GroupRollupColorMap?field.GroupRollupColorMap[k]:getColor(k, field);
			if (!colorVal) colorVal = "000000";
			else colorVal = colorVal.replace("#", "");

			var count  =map[k];
			 
			var textColor = Utils.contrastingColor(colorVal);
			htm.push("<div style='font-size:xx-small;color:#"+textColor+";white-space:nowrap;background-color:#"+colorVal+";'>"+ count + " " + k+"</div>");
 			i++;
		}
 		return htm.join("");
	};//createGroupDivs
	
	
};

