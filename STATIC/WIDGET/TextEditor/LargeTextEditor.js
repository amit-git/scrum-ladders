
Widget.LargeTextEditor = function(){
	var txtid = Utils.getUid();
	var btnid = Utils.getUid();
	
	var me = this;
	this.render = function(div, field, rowData){
		div.html("");
		if (!rowData) return;
	
		
		var saveClicked = function() {
			var newVal = me.getVal();
			var oldVal = rowData?rowData[field.Name]:null;
			//alert(newVal);
			if (newVal!=oldVal){
				me.getEventManager().notify("change", me);
			}
			$("#dialog").dialog('close');

		};//saveClicked()
		
		var val = rowData[field.Name];

		div.html("<div class='ld_link'>Notes</div>");

		div.click(function(){
			$("#dialog").html("");
			var diaOptions = {
					width: 500,
					height: 400,
					modal: true, 
					open: function (event, ui) {$('#dialog').css('overflow', 'scroll');} 
			};
			
			var htm = [];
			htm.push("<h1>Edit "+field.Name+"</h1>");
			htm.push("<h3>"+Utils.getShortDescription(rowData)+"</h3>");
			
			htm.push("<input type='button' id='Btn1"+btnid+"' value='Save'/>");
			htm.push("<br/><textarea id='"+txtid+"' style='width:"+(diaOptions.width-70)+"px;height:"+(diaOptions.height-200)+"px;'></textarea>");
			htm.push("<br/><input type='button' id='Btn2"+btnid+"' value='Save'/>");

			$("#dialog").append(htm.join("")).dialog(diaOptions);

			$("#"+txtid).val(val);

			$("#Btn1"+btnid).click(saveClicked);
			$("#Btn2"+btnid).click(saveClicked);
			
		});
		
	}
	
	
	this.getVal = function(){
		return $("#"+txtid).val();
	}
	
	this.setVal = function(inputObj){
		
	}
	 
	
};