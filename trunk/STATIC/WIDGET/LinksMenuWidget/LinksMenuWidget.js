
Widget.LinksMenuWidget = function(){
 
	this.render = function(div){

		var htm1 = []; 
		htm1.push("<table>")

		for (var i=0; i<LADDER.length; i++){
			var url = SERVER.url(LADDER[i].Name);
			var rtPlural = LADDER[i].Name+"s";	
			htm1.push("<tr><td style='padding-left:"+(i*20)+"px' >")
			htm1.push(" <a href='"+url+"' class='nobr'>Show all "+rtPlural +" </a>");
			htm1.push("</td></tr>")
			
		}//for i
		htm1.push("</table>")
		
		div.html(htm1.join(" "));
	}
	 
};
 
