Widget.SelectWidget = function(){

	var me = this;
	var id = Utils.getUid();

	this.getColor = function(field, rowData){
		if (!field.Colors) return null;
		var val = rowData?rowData[field.Name]:"";

		for (var i=0; i<field.Args.length; i++){
			if (val== field.Args[i]){ 
				return field.Colors[i];
			}
		}
		return null;
	};//getColor
	
	this._render = function(div, field, rowData){
		div.addClass("selectDivClass");
		var val = rowData?rowData[field.Name]:"";
		div.html(val);		
	}
	
	this.render = function(div, field, rowData){
		

		var out = [];
		out.push("<select id='"+id+"'><option></option>");

		var val = rowData?rowData[field.Name]:"";
		
		for (var i=0; i<field.Args.length; i++){
			var arrVal = field.Args[i];
			var selected = (val==arrVal)?" selected ":"";
			
			if (field.Colors){
				out.push("<option "+selected+" style='background-color:"+field.Colors[i] +"'>"+arrVal+"</option>");
			}else{
				out.push("<option "+selected+" >"+arrVal+"</option>");
			}
			

		}
		out.push("</select>");

		div.html(out.join(" "));
				
		
		$("#"+id).bind('change', function() {
			var newVal = me.getVal();
			var oldVal = rowData?rowData[field.Name]:null;
			if (newVal!=oldVal){
				me.getEventManager().notify("change", me);
			}
		});

		
	}

	this.getVal = function(){
		return $("#"+id).val();
	}

	
	this.setVal = function(v){
		$("#"+id).val(v);
	} 
	
};
 