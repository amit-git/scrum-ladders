
Widget.LargeTextColumnField = function(ele){

	var me = this;
	var jqDiv = null;
	var eid = Utils.getUid();
	
	this.getLabel = function(){ return "Notes";}
	this.render = function(div){
		if (div) jqDiv = $(div);
		jqDiv.html("");

		var htm = [];
		htm.push("<table>");

		//Name
		var att = "Name";
		htm.push("<tr><td>Name</td> <td>");
		htm.push("<textarea id='"+eid+att+"' cols=50 rows=2>"+ele[att]+"</textarea>");
		htm.push("</td></tr>");

		jqDiv.html(htm.join(""));

	};//render()
	
	this.updateValues = function(jsonMap){
		var name = $("#"+eid+"Name").val();
		if (Utils.isEmpty(name)){
			alert("Name can't be blank");
			return false;
		}
		if (Utils.hasSpecialChars(name)){
			alert("Invalid value '"+name+"' for Name");
			return false;
		}		
		jsonMap.Name = name;
		return true;
	}

}; //TextColumnField


