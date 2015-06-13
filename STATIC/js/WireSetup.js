
$( document ).ready(function() {
	
	try{
		ENV.SetupWidgets(); //Must call this in the begining or all sort of things will stop working.
		
		wirePage();
		
	}catch(ex){
		$(document.body).html("ERROR:"+ex);
	}
});
	
var wirePage = function(){
	//ENV.dbWidget = new Widget.DBConnectionWidget();
	//ENV.dbWidget.render ($("#dbconnectionDiv"));

	loadAllLadders();
	/*
	ENV.dbWidget.getEventManager().attachEvent("save", loadAllLadders);
	ENV.dbWidget.getEventManager().attachEvent("loading", function(){
		$("#laddersListDiv").html("...loading");
	});
	*/
	
};//wirePage
	
var loadAllLadders = function(){
	SERVER.getAllLadders(function(list){
		$("#errorHead").html("");

		ENV.laddersWidget = new Widget.LaddersListWidget2(list, $("#errorHead"));
		ENV.laddersWidget.render ($("#laddersListDiv"));

	}, function(err){
		$("#errorHead").html(err);
	});
};
