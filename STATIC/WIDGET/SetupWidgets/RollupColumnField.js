
Widget.RollupColumnField = function(ele, currentLevel){

	var me = this;
	var jqDiv = null;
	var eid = Utils.getUid();
	
	this.getLabel = function(){ return "Rollup";}
	
	this.render = function(div){
		if (div) jqDiv = $(div);
		jqDiv.html("");

		var htm = [];
		htm.push("<table>");

		var att = "RollupTarget";
		htm.push("<tr><td>Rollup Target Level</td> <td>");
		htm.push("<select id='"+ eid + att +"'> ");
		var renderIt = populateTargetLevels (htm);
		htm.push("</select>");
		htm.push("</td></tr>");
		
		if (!renderIt){
			jqDiv.html("Can't do rollup on the lowest level");
			return;
		}
		

		//RollupColumn
		var att = "RollupColumn";
		htm.push("<tr><td>Rollup Column</td> <td>");
		htm.push("<select id='"+ eid + att +"'></select>");
		htm.push("</td></tr>");
		
		//RollupType
		var att = "RollupType";
		var defVal = ele[att];
		htm.push("<tr><td>Rollup Type</td> <td>");
		htm.push("<select id='"+ eid + att +"'>");
		pushOption(htm, "group", defVal);
		pushOption(htm, "sum", defVal);
		htm.push("</select>");
		htm.push("</td></tr>");
		
		//RollupSumColumn
		var att = "RollupSumColumn";
		htm.push("<tr><td>Sum Column</td> <td>");
		htm.push("<select id='"+ eid + att +"'></select>");
		htm.push("</td></tr>");
		
		
		jqDiv.html(htm.join(""));

		
		$("#"+eid+"RollupTarget").change(targetChanged);
		$("#"+eid+"RollupType").change(groupingChanged);
		
		targetChanged();
		groupingChanged();

	};//render()
	
	var groupingChanged = function(){
		document.getElementById(eid+"RollupSumColumn").options.length = 0;
		var target = $( "#"+eid+"RollupTarget option:selected" ).text();
		if (target=="") return;

		var column = $( "#"+eid+"RollupColumn option:selected" ).text();
		if (column=="") return;
		
		var type = $( "#"+eid+"RollupType option:selected" ).text();
		if (type!="group") return;
		
		var sel = $("#"+eid+"RollupSumColumn");
		sel.append("<option>Count</option>");

		var sch = getSchema(target);
		for (var i=0; i<sch.length; i++){
			if (sch[i].ColumnType!="Select") continue;

			var s = (sch[i].Name==ele.RollupSumColumn)?" selected='selected' ": " ";
			sel.append("<option "+s+">"+sch[i].Name+"</option>");

		}//for i
		
		
	}//groupingChanged

	
	var targetChanged = function(){
		document.getElementById(eid+"RollupColumn").options.length = 0;

		var target = $( "#"+eid+"RollupTarget option:selected" ).text();
		
		if (target=="") return;

		var sel = $("#"+eid+"RollupColumn");
		
		var sch = getSchema(target);
		for (var i=0; i<sch.length; i++){
			if (sch[i].ColumnType!="Select") continue;
			
			var s = (sch[i].Name==ele.RollupColumn)?" selected='selected' ": " ";
			
			sel.append("<option "+s+">"+sch[i].Name+"</option>");		
		}//for i

		groupingChanged();
	};//targetChanged()	
	
	
	var populateTargetLevels = function(htm){
		
		pushOption(htm, "", ele.RollupTarget);

		var flag = false;
		var addedOptions = false;
		for (var i=0; i<ENV.LadderSchema.length; i++){
			if (ENV.LadderSchema[i].Name == currentLevel){
				flag = true;
				continue;
			}else if (!flag){
				continue;
			}
			addedOptions = true;
			pushOption(htm, ENV.LadderSchema[i].Name, ele.RollupTarget );
		}
		
		return addedOptions;
	};//populateTargetLevels()

	
	this.updateValues = function(jsonMap){
		var target = $( "#"+eid+"RollupTarget option:selected" ).text();
		var column = $( "#"+eid+"RollupColumn option:selected" ).text();
		var type = $( "#"+eid+"RollupType option:selected" ).text();
		var sumcolumn = $( "#"+eid+"RollupSumColumn option:selected" ).text();
		
		if (Utils.isEmpty(target)){
			alert("Rollup Target can't be blank");
			return false;
		}
		if (Utils.isEmpty(column)){
			alert("Rollup Column can't be blank");
			return false;
		}
		if (Utils.isEmpty(type)){
			alert("Rollup Type can't be blank");
			return false;
		}

		jsonMap.RollupTarget = target;
		jsonMap.RollupColumn = column;
		jsonMap.RollupType = type;
		jsonMap.RollupSumColumn = sumcolumn;

		return true;
	}

	var pushOption = function(htm, val, def){
		var s = (val==def)?" selected='selected' ": " ";
		htm.push("<option "+s+" value='"+val+"'>"+ val +"</option>");
	}
	
	
	var getSchema = function(target){
		for (var j=0; j<ENV.LadderSchema.length; j++){
			var levelObj = ENV.LadderSchema[j];
			if (target==levelObj.Name){
				return levelObj.Schema;
			}
		}//for j
		return null;
	}
}; //RollupColumnField


