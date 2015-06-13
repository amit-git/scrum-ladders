
Widget.TextColumnField = function(ele){

	var me = this;
	var jqDiv = null;
	var eid = Utils.getUid();
	
	this.getLabel = function(){ return "Text";}
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

		//Minimum Length
		var att = "MinLen";
		htm.push("<tr><td>Minimum Length</td> <td>");
		htm.push("<select id='"+ eid + att +"'>");
		for (var i=1;i<=10;i++) pushOption(htm, i, ele[att]);
		for (var i=2;i<=100;i++) pushOption(htm, i*10, ele[att]);
		htm.push("</select>");
		htm.push("</td></tr>");

		//Maximum Length
		var att = "MaxLen";
		htm.push("<tr><td>Maximum Length</td> <td>");
		htm.push("<select id='"+ eid + att +"'>");
		for (var i=1;i<=10;i++) pushOption(htm, i, ele[att]);
		for (var i=2;i<=100;i++) pushOption(htm, i*10, ele[att]);
		htm.push("</select>");
		htm.push("</td></tr>");
		
		
		jqDiv.html(htm.join(""));

	};//render()
	
	this.updateValues = function(jsonMap){
		var name = $("#"+eid+"Name").val();
		var minLen = $( "#"+eid+"MinLen option:selected" ).text();
		var maxLen = $( "#"+eid+"MaxLen option:selected" ).text();
		
		if (Utils.isEmpty(name)){
			alert("Name can't be blank");
			return false;
		}

		if (Utils.hasSpecialChars(name)){
			alert("Invalid value '"+name+"' for Name");
			return false;
		}		
		jsonMap.Name = name;
		jsonMap.MinLen = minLen;
		jsonMap.MaxLen = maxLen;

		return true;
	}

	var pushOption = function(htm, val, def){
		var s = (val==def)?" selected='selected' ": " ";
		htm.push("<option "+s+" value='"+val+"'>"+ val +"</option>");
	}
	
}; //TextColumnField


