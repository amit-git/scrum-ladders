package org.ladders.services;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.DataStorage;
import org.ladders.util.Cols;
import org.ladders.util.TimestampLogger;

import com.sun.net.httpserver.HttpExchange;

public class InsertHandler extends BaseHandler2 {


	protected void validateTransactionParams() throws Exception{
		if (StringUtils.isEmpty(rowType))
			throw new Exception("No rowType identified");
		if (StringUtils.isEmpty(parentId))
			throw new Exception("No parentId identified");
	}

	@Override
	protected void innerHandle() throws Exception {
		validateTransactionParams();
		String data = DataStorage.get(ladderName).insertNew(rowType, this.inputParams).toString();
		successOut("Insert Success "+ this.inputParams.get(Cols.ROWID), data);
	}

	@Override
	public String getName() {
		return "INSERT";
	}

	 
 

}
