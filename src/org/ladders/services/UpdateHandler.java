package org.ladders.services;

import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;
import org.ladders.util.U;

public class UpdateHandler extends BaseHandler
{

	@Override
	protected void innerHandle() throws Exception
	{

		// For now every input change on the client side calls update.
		// That means inputParams should always have size of 2. 1 for rowId and
		// second for the updated field.
		if (inputParams.size() != 4)
			throw new Exception("We can only update one field at a time. Probably a client side bug. inputParams:"
					+ inputParams);

		String forRowId = inputParams.get(Cols.ROWID);
		String forKey = inputParams.get("KEY");
		String newVal = inputParams.get("VAL");

		if (forKey.equals(Cols.DESCRIPTION) && U.isNullOrBlank(newVal))
			throw new Exception("Can't update NULL value for :" + forKey);

		MyRecord rawRecord = LadderFactory.getLadder(getLadderName()).update(forRowId, forKey, newVal);

		if (!rawRecord.containsField(Cols.DESCRIPTION))
			throw new Exception("Updated record doesn't contain Description. Must have description.");

		String data = JsonUtil.toJsonFromRaw(rawRecord).toString();

		successOut("Update Success " + inputParams.get(Cols.ROWID), data);

	}

	@Override
	protected boolean actionOnLadder()
	{
		return true;
	}

}
