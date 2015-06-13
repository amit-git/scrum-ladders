package org.ladders.services;

import java.util.ArrayList;

import org.ladders.db.AbstractDataStorage;
import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;

public class DeleteHandler extends BaseHandler {
 

	@Override
	protected void innerHandle() throws Exception {
		if (!inputParams.containsKey(Cols.ROWID)) {
			throw new Exception("No _rowId defined");
		}
		String id = inputParams.get(Cols.ROWID);
		
		AbstractDataStorage dao = LadderFactory.getLadder(this.getLadderName());

		ArrayList<MyRecord > rows = dao.delete(id);
		//successOut("Delete Success "+id, rows);
		successOut("Got rows", JsonUtil.toJsonFromRaw(rows).toString());

	}
 
	@Override
	protected boolean actionOnLadder()
	{
		return true;
	}

}
