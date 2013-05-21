package org.ladders.services;

import org.ladders.db.DataStorage;
import org.ladders.util.Cols;

public class UpdateHandler extends BaseHandler2 {

	@Override
	protected void innerHandle() throws Exception {

		// For now every input change on the client side calls update.
		// That means inputParams should always have size of 2. 1 for rowId and
		// second for the updated field.
		if (inputParams.size() != 2)
			throw new Exception("We can only update one field at a time. Probably a client side bug. inputParams:"+inputParams);

		String data = DataStorage.get(ladderName).update(this.inputParams);
		successOut("Update Success " + inputParams.get(Cols.ROWID), data);

	}

	@Override
	public String getName() {
		return "UPDATE";
	}

}
