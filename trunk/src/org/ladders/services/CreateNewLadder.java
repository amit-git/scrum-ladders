package org.ladders.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.DataStorage;
import org.ladders.model.LaddersContextModel;
import org.ladders.model.LaddersSchemaModel;
import org.ladders.model.LaddersTopologyModel;
import org.ladders.util.Cols;
import org.ladders.util.SettingsUtil;
import org.ladders.util.U;

import com.mongodb.BasicDBObject;

public class CreateNewLadder extends BaseHandler2 {

	@Override
	protected void innerHandle() throws Exception {

		List<String> ladders = DataStorage.getAllLadders();

		ladderName = inputParams.get("LADDER_NAME");
		String schema = inputParams.get("SCHEMA_JSON");
		if (StringUtils.isEmpty(ladderName)) {
			throw new Exception("LADDER name can't be null or empty");
		}
		if (StringUtils.isEmpty(schema)) {
			throw new Exception("LADDER schema can't be null or empty");
		}

		LaddersTopologyModel ltm = new LaddersTopologyModel(schema);

		if (ladders.contains(ladderName)) {
			DataStorage.removeLadder(ladderName);
		}

		List<LaddersContextModel> contextQ = ltm.getContexts();
		insertRows(ladderName, "ROOT", contextQ, 0);

		SettingsUtil.saveSetting("SCHEMA_" + ladderName, schema);
		successOut("Created LADDER " + ladderName, schema);
	}

	static final int TOTAL_TEST_ROWS = 5;

	private static void insertRows(String ladderName, String parentId, List<LaddersContextModel> contextQ, int i)
			throws Exception {

		if (i>= contextQ.size()) return;

		LaddersContextModel context = contextQ.get(i);

		ArrayList<String> addedRows = new ArrayList<>();
		for (int j = 0; j < TOTAL_TEST_ROWS; j++) {
			String rowId = insertRow(j, ladderName, context, parentId);
			addedRows.add(rowId);
		}// for j

		i++;
		for (String rowId : addedRows) {
			insertRows(ladderName, rowId, contextQ, i);
		}
	}

	private static String insertRow(int i, String ladderName, LaddersContextModel context, String parentId)
			throws Exception {

		U.log(" insertRow :" + parentId);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Cols.PARENTID, parentId);

		for (LaddersSchemaModel sm : context.getSchema()) {
			// U.log("  Schema.Name:" + sm.Name);
			if (sm.Args.size() > 0) {
				params.put(sm.Name, sm.Args.get(i % sm.Args.size()));
			} else {
				params.put(sm.Name, i + "th " + context.getName() + " for " + parentId);
			}
		}

		BasicDBObject row = DataStorage.get(ladderName).insertNew(context.getName(), params);

		return row.get(Cols.ROWID).toString();

	}

	@Override
	public String getName() {
		return "SAVELADDER";
	}

	@Override
	public boolean isTransactional() {
		return false;
	}

}