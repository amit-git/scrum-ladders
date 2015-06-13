
Widget.FieldChoiceWidget = function(divArr, labelArr, ele){

	var me = this;
	var jqDiv = null;
	var choiceRadioName = Utils.getUid();
	if (!ele.ColumnType) ele.ColumnType = labelArr[0];

	var _radioChanged = function(){
		for (var i =0; i<divArr.length; i++) divArr[i].hide();
		var i = $("input[name='"+choiceRadioName+"']").filter(':checked').val();
		if (!i) return;

		i = parseInt(i);
		if (!divArr[i]){
			alert("Div can't be null here. Going to debugger");
			debugger;
		}
		divArr[i].show();
	}
	
	this.render = function(div){
		if (div) jqDiv = $(div);
		jqDiv.html("");

		var htm = [];

		//Choices
		for (var i =0; i<divArr.length; i++){
			divArr[i].hide();
			var txt = labelArr[i];

			if (ele.ColumnType==txt){
				htm.push(" <input type=radio name="+choiceRadioName+" value='"+i+"' checked=checked>"+txt+"</input>");
			}else{
				htm.push(" <input type=radio name="+choiceRadioName+" value='"+i+"'>"+txt+"</input>");
			}
		}
		jqDiv.html(htm.join(""));

		$("input[name='"+choiceRadioName+"']").change(_radioChanged);

		_radioChanged();
	};//render()
	
	this.updateValues = function(jsonMap){
		var i = $("input[name='"+choiceRadioName+"']").filter(':checked').val();
		jsonMap.ColumnType = labelArr[parseInt(i)];
		return true;
	};//updateValues()
	
}; //FieldChoiceWidget
 
