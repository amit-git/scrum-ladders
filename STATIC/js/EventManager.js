var EventManager = function (){
	var list = {};
	this.attachEvent = function (eventType, callback){
	
		if (!list[eventType]) list[eventType] = [];
		if (this.exists(eventType, callback)) return;

		list[eventType].push([callback, null] );
	}


	this.notify = function (eventType, source, objData){
		var arr = list[eventType];
		if (!arr) return false;
		
		for (var i=0; i<arr.length; i++){
			arr[i][0] (source, objData);
		}
		return true;
		
	}; //notify

	this.exists = function (eventType, callback){
		var arr = list[eventType];
		if (!arr) return false;
		
		for (var i=0; i< arr.length; i++){
			if ( arr[i][0] == callback) return true;
		}
		return false;
	}
	
	this.forwardEvent = function(eventType, em){
		this.attachEvent(eventType, function(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10){
			em.notify(eventType, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 );
		});
		
	};//forwardEvent ()
		
};//EventManager

