package org.ladders.services;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.DataStorage;
import org.ladders.model.LaddersContextModel;
import org.ladders.model.LaddersSchemaModel;
import org.ladders.model.LaddersTopologyModel;
import org.ladders.util.Cols;
import org.ladders.util.FileUtil;
import org.ladders.util.SettingsUtil;
import org.ladders.util.U;

import com.mongodb.BasicDBObject;

public class CreateNewLadder extends BaseHandler2 {

	@Override
	protected void innerHandle() throws Exception {

		List<String> ladders = DataStorage.getAllLadders();

		String name = inputParams.get("LADDER_NAME");
		String schema = inputParams.get("SCHEMA_JSON");
		if (StringUtils.isEmpty(name)) {
			throw new Exception("LADDER name can't be null or empty");
		}
		if (StringUtils.isEmpty(schema)) {
			throw new Exception("LADDER schema can't be null or empty");
		}

		LaddersTopologyModel ltm = new LaddersTopologyModel(schema);

		if (!ladders.contains(name)) {
			String parentId1 = "ROOT";
			String parentId2 = "ROOT";
			for (LaddersContextModel context : ltm.getContexts()) {
				U.log("context.Name:" + context.getName());
				parentId1 = insertRow(name, context, parentId1);
				parentId2 = insertRow(name, context, parentId2);
			}
		}
		SettingsUtil.saveSetting("SCHEMA_" + name, schema);
		successOut("Created LADDER " + name, schema);

	}

	private static String insertRow(String ladderName, LaddersContextModel context, String parentId) throws Exception {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Cols.PARENTID, parentId);

		for (LaddersSchemaModel sm : context.getSchema()) {
			U.log("  Schema.Name:" + sm.Name);
			if (sm.Args.size() > 0) {
				params.put(sm.Name, sm.Args.get((new Random()).nextInt(sm.Args.size() - 1)));
			} else {
				params.put(sm.Name, context.getName() + " for " + parentId);
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