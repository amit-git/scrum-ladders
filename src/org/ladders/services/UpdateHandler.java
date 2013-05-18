package org.ladders.services;

import java.util.HashMap;

import org.ladders.db.DataStorage;
import org.ladders.util.Cols;
import org.ladders.util.TimestampLogger;

import com.sun.net.httpserver.HttpExchange;

public class UpdateHandler extends BaseHandler2 {

	@Override
	protected void innerHandle() throws Exception {
		String data = DataStorage.get(ladderName).update(this.inputParams);
		successOut("Update Success "+inputParams.get(Cols.ROWID), data);
		
	}

	@Override
	public String getName() {
		return "UPDATE";
	}

	 
 

}
