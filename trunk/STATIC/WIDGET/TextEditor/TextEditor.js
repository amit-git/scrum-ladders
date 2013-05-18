
Widget.TextEditor = function(){
	var id = Utils.getUid();
	var me = this;

	this.render = function(div, field, rowData){
		div.html("<textarea id='"+id+"' style='width:600px;'></textarea>");
		//div.html("<div id='"+id+"' class='descriptionText''></div>");
		
		$("#"+id).bind('blur', function() {
			var newVal = me.getVal();
			var oldVal = rowData?rowData[field.Name]:null;
			
			if (newVal!=oldVal){
				
				if (field.MinLen){
					if (newVal<field.MinLen){
						alert(field.Name+" Can't be less than '"+field.MinLen+"' characters");
						return;
					}
				}
					
				me.getEventManager().notify("change", me);
			}
			//alert ("changed eMgr:"+eMgr);
		});		
		
		if (rowData){
			$("#"+id).val( rowData[field.Name] );
			//$("#"+id).html(rowData[field.Name]);
		}
	}
	
	this.getVal = function(){
		return $("#"+id).val();
	}
	
	this.setVal = function(inputObj){
		
	}
	 
	
};