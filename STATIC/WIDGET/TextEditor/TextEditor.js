
Widget.TextEditor = function(){
	var id = Utils.getUid();
	var me = this;
	var MAX_HEIGHT = 60;
	this.render = function(div, field, rowData){
		div.html("<textarea id='"+id+"' style='width:400px;'></textarea>");
		//div.html("<div id='"+id+"' class='descriptionText''></div>");
		
		var obj = $("#"+id);
		
		obj.bind('blur', function() {
			var newVal = me.getVal();
			var oldVal = rowData?rowData[field.Name]:null;
			
			if (newVal!=oldVal){

				if (field.MinLen && newVal.length<field.MinLen){
					alert(field.Name+" Can't be less than '"+field.MinLen+"' characters.\n Current length is "+newVal.length+" characters");
					return;
				}else if (field.MaxLen && newVal.length>field.MaxLen){
					alert(field.Name+" Can't be more than '"+field.MaxLen+"' characters.\n Current length is "+newVal.length+" characters");
					return;
				}
					
				me.getEventManager().notify("change", me);
			}
			//alert ("changed eMgr:"+eMgr);
		});		
		
		if (rowData){
			obj.val( rowData[field.Name] );
			//obj.html(rowData[field.Name]);			
			window.setTimeout( function() { obj.height( Math.min(obj[0].scrollHeight, MAX_HEIGHT) ); }, 1);
		}
		
		obj.keyup(function(e) {
			window.setTimeout( function() { obj.height( Math.min(obj[0].scrollHeight, MAX_HEIGHT) ); }, 1);
		    //while($(this).outerHeight() < this.scrollHeight + parseFloat($(this).css("borderTopWidth")) + parseFloat($(this).css("borderBottomWidth"))) {
		    //    $(this).height($(this).height()+1);
		    //};
		});
		
	}
	
	this.getVal = function(){
		return $("#"+id).val();
	}
	
	this.setVal = function(inputObj){
		
	}
	 
	
};