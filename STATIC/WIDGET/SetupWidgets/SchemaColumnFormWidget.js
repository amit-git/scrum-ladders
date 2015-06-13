
Widget.SchemaColumnFormWidget = function(ele, saveCallback, mLevelObject){

	var me = this;
	var jqDiv = null;

	var saveBtnId = Utils.getUid();
	var choiceDivId = Utils.getUid();
	
	var choiceWidget = null;
	
	var widgetsArr = [new Widget.TextColumnField(ele),
	                  new Widget.LargeTextColumnField(ele),
	                  new Widget.SelectColumnField(ele),
	                  new Widget.RollupColumnField(ele, mLevelObject.Name)];
	
	this.render = function(div){
		if (div) jqDiv = $(div);
		jqDiv.html("");

		var htm = [];
		htm.push("<h1>"+ele.Name+"</h1>"); //Read only Name
		
		htm.push("<div id='"+choiceDivId+"'></div>");
		
		for (var i=0; i<widgetsArr.length; i++){
			htm.push("<div id='choiceDiv" + i + "'></div>");
		}

		htm.push("<button id='"+saveBtnId+"'>Save</button>");

		jqDiv.html(htm.join(""));

		//render widgets
		var divArr = [];
		var labelArr = [];
		for (var i=0; i<widgetsArr.length; i++)
		{
			divArr.push($("#choiceDiv"+i));
			labelArr.push(widgetsArr[i].getLabel());
			widgetsArr[i].render( $("#choiceDiv"+i) );

		}//for i
		
		choiceWidget = new Widget.FieldChoiceWidget(
				divArr, 
				labelArr, ele);
		choiceWidget.render($("#"+choiceDivId));
		

		$("#"+saveBtnId).click(saveBtnClicked);

		
	};//render()
	
	var saveBtnClicked = function(){

		var jsonMap = {};
		choiceWidget.updateValues(jsonMap);
				
		for (var i=0; i<widgetsArr.length; i++){
			var w = widgetsArr[i];
			if (jsonMap.ColumnType == w.getLabel()){
				if (w.updateValues(jsonMap)){
					saveCallback(jsonMap);
				}
				break;
			}
		}//for i

	}//saveBtnClicked

	
}; //SchemaColumnFormWidget
 
