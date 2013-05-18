Widget.RollupWidget = function(){
	var me = this;
	var mediv = null;
	var mefield = null;
	me.rowIndex = 1;
	var DEFAULT_COLORS = ["FFEBCD", "0000FF", "8A2BE2", "A52A2A", "DEB887", "5F9EA0", "7FFF00", "D2691E", "FF7F50", "6495ED", "FFF8DC", "DC143C", "00FFFF", "00008B", "008B8B", "B8860B", "A9A9A9", "006400", "BDB76B", "8B008B"];

	me.backgroundColor = "FF000000";
	me.size = "185x40";

	this.renderRollups = function(rollupRow){
		mediv.html(getRollupVal(mefield, rollupRow));
	};//renderRollups()
	
	this.render = function(div, field){
		if (!field.RollsUp) throw new Utils.Exception("field.RollsUp is NULL field:"+field.Name);
		mediv = div;
		mefield = field;
		
		mediv.css("text-align", "center");

	};
	
	var getRollupVal = function(field, rowData){
		var map = rowData[ENV.ROLLUP_PREFIX + field.ChildRollupFieldName];
		if (map){
			if (field.RollsUp=="sum"){
				var total = 0;
				for (var val in map){
					var count = map[val];
					total += Utils.tryParse(val) * Utils.tryParse(count);
				}
				return total;
			}else if (field.RollsUp=="group"){
				return groupRollupVal(field, map);
			}else{
				return "Unsupported RollsUp val '"+field.RollsUp+"'";
			}
			
		}else{
			return "";
		}
		
	};

	var groupRollupVal = function(field, map){
		var labelArr = []; var valArr = []; var colorArr = [];

		var i=0;
		for (var k in map){
			var count = map[k];

			if (labelArr.length>0)labelArr.push("|"); labelArr.push(count+" "+k);
			if (valArr.length>0)valArr.push(","); valArr.push(count);

			
			var colorVal = field.GroupRollupColorMap?field.GroupRollupColorMap[k]:DEFAULT_COLORS[i++];
			
			if (!colorVal) colorVal = "00000000";
			else colorVal = colorVal.replace("#", "");
			
			if (colorArr.length>0)colorArr.push(","); colorArr.push(colorVal);
			
		}
		
		var htm = ["<img src='https://chart.googleapis.com/chart?cht=p&chf=bg,s,"+me.backgroundColor+"&chd=s:Uf9a&chs="+me.size];
		
		htm.push("&chl="); htm.push(labelArr.join(""));
		htm.push("&chd=t:"); htm.push(valArr.join(""));
		htm.push("&chco="); htm.push(colorArr.join(""));
		
		
		htm.push("' />");
		return htm.join("");		
		
	};//groupRollupVal()
	

};

