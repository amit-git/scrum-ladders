package org.ladders.services;

import java.util.ArrayList;

import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;

import com.mongodb.BasicDBObject;

public class DeleteHandler extends BaseHandler2 {
 

	@Override
	protected void innerHandle() throws Exception {
		if (!inputParams.containsKey(Cols.ROWID)) {
			throw new Exception("No _rowId defined");
		}
		String id = inputParams.get(Cols.ROWID);
		ArrayList<BasicDBObject> rows = dao.delete(id);
		//successOut("Delete Success "+id, rows);
		successOut("Got rows", JsonUtil.toJson(rows).toString());

	}

	@Override
	public String getName() {
		return "DELETE";
	}


 
	 
 

}
