
Widget.SelectColumnField = function(ele){

	var me = this;
	var jqDiv = null;
	var eid = Utils.getUid();
	
	
	this.getLabel = function(){ return "Select";}
	
	this.render = function(div){
		if (div) jqDiv = $(div);
		jqDiv.html("");

		var htm = [];
		htm.push("<table>");

		//Name
		var att = "Args";
		htm.push("<tr><td>Arguments</td> <td>");
		htm.push("<textarea id='"+eid+att+"' cols=50 rows=2>"+ele[att]+"</textarea>");
		htm.push("</td></tr>");

		//Colors
		var att = "Colors";
		htm.push("<tr><td>Colors</td> <td>");
		htm.push("<textarea id='"+eid+att+"' cols=50 rows=2>"+ele[att]+"</textarea>");
		htm.push("</td></tr>");
		
		jqDiv.html(htm.join(""));

	};//render()
	
	this.updateValues = function(jsonMap){
		var args = $("#"+eid+"Args").val();
		var colors = $("#"+eid+"Colors").val();

		if (!validate("Arguments", args)) return false;
		if (!Utils.isEmpty(colors) && !validate("Colors", colors)) return false;
	
		jsonMap.Args = args;
		jsonMap.Colors = colors;

		return true;
	}

	var validate = function(attr, txt){
		var list = txt.split(",");
		if (list.length==1){
			alert("Normally "+attr + " has comma separated values. ");
			return false;
		}
		
		for (var i=0; i<list.length; i++){
			if (!/^[\w.-_# ]+$/.test(list[i])){
				alert((i+1)+"th element in comma separated list '"+attr+"' has special characters. \n "+list[i]);
				return false;
			}
		}//for i
		return true;
	}


	var pushOption = function(htm, val, def){
		var s = (val==def)?" selected='selected' ": " ";
		htm.push("<option "+s+" value='"+val+"'>"+ val +"</option>");
	}
	
};
