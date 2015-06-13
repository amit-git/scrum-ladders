package org.ladders.services;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;

public class InsertHandler extends BaseHandler {


	@Override
	protected void innerHandle() throws Exception {
		
		if (StringUtils.isEmpty(rowType))
			throw new Exception("No rowType identified");
		if (StringUtils.isEmpty(parentId))
			throw new Exception("No parentId identified");
		if (!inputParams.containsKey(Cols.DESCRIPTION))
			throw new Exception("Description must be passed in to create a new record");

		MyRecord rawRecord= LadderFactory.getLadder(getLadderName()).insertNew(rowType, this.inputParams);
		String data = JsonUtil.toJsonFromRaw(rawRecord).toString();		
		
		successOut("Insert Success "+ this.inputParams.get(Cols.ROWID), data);
	}

 

	@Override
	protected boolean actionOnLadder()
	{
		return true;
	}

	 
 

}
