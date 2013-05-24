package org.ladders.db;

import java.util.HashMap;
import java.util.Map.Entry;

import org.ladders.util.Cols;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class RollupsManager {

	private DataStorage db = null;
	private DBCollection rowsTable = null;

	public RollupsManager(DataStorage db, DBCollection rowsTable) {
		this.db = db;
		this.rowsTable = rowsTable;
	}

	public void updateRollups(final BasicDBObject row, HashMap<String, String> params) throws Exception {
		updateRollups(row, params, 0);
	}

	private void updateRollups(final BasicDBObject row, HashMap<String, String> params, final int rollupLevel)
			throws Exception {

		String rollupToId = null;
		BasicDBObject rollupToRow = row;
		for (int i = 0; i <= rollupLevel; i++) {
			rollupToId = rollupToRow.getString(Cols.PARENTID);
			rollupToRow = db.getRow(rollupToId);
			if (rollupToRow == null)
				return;
		}

		String rowId = row.getString(Cols.ROWID);
		// U.log(" FOR rollupToId:" + rollupToId);
		// 0:1,5:1,10:1
		// Multilevel rollups ,ST111:Dev1,ST111:Dev2,ST222:Dev3
		for (Entry<String, String> pair : params.entrySet()) {
			String k = pair.getKey();
			if (k.startsWith("_"))
				continue;

			k = Cols.ROLLUP_PREFIX + k;

			HashMap<String, String> rollupMap = null;
			if (rollupToRow.containsField(k)) {
				rollupMap = (HashMap<String, String>) rollupToRow.get(k);
				// U.log(k + " - rollupMap:" + rollupMap);
			} else {
				rollupMap = new HashMap<>();
			}

			String v = pair.getValue();

			if (v == null) {
				// Delete this entry from the rollup. His ass got deleted.
				rollupMap.remove(rowId);
			} else {

				if (v.length() >= 20)
					v = v.substring(0, 20); // No point in rolling up values
											// more than 20.
				rollupMap.put(rowId, v); // ST111:5
			}

			// Replace it back
			rollupToRow.put(k, rollupMap);
		}

		rowsTable.update(new BasicDBObject(Cols.ROWID, rollupToId), rollupToRow);

		// Rollup more.
		updateRollups(row, params, rollupLevel + 1);
	}

	public void moveRollup(BasicDBObject moveThisRow, BasicDBObject newParentRow) throws Exception {

		// First for the fields in this row
		deleteRollup(moveThisRow);

		// Then for all the rollups rolled up to this row
		// Get all rollup tables for all fields
		for (Entry<String, Object> pair : moveThisRow.entrySet()) {
			if (!pair.getKey().startsWith(Cols.ROLLUP_PREFIX))
				continue;

			// For each field table get all keys
			HashMap<String, String> rollupMap = (HashMap<String, String>) pair.getValue();

			//Delete from old dad
			deleteRollupTable(moveThisRow, rollupMap);

			//Make changes to the new dad
			
		}


	}// deleteRollup()

	private void deleteRollupTable(BasicDBObject moveThisRow, HashMap<String, String> rollupMap) throws Exception {
		// For all fields
		for (Entry<String, String> rowIdValPair : rollupMap.entrySet()) {
			String rowId = rowIdValPair.getKey();

			// Remove from the parents
			HashMap<String, String> params = getDeleteParams(rowId);
			updateRollups(moveThisRow, params);

		}// for rowIdValPair		
	}

	private void addRollupTable(BasicDBObject moveThisRow, HashMap<String, String> rollupMap) throws Exception {
		// For all fields
		for (Entry<String, String> rowIdValPair : rollupMap.entrySet()) {
			String rowId = rowIdValPair.getKey();

			// Remove from the parents
			HashMap<String, String> params = getDeleteParams(rowId);
			updateRollups(moveThisRow, params);

		}// for rowIdValPair		
	}	
	public void deleteRollup(BasicDBObject aboveThisRow) throws Exception {
		HashMap<String, String> params = getDeleteParams(aboveThisRow);
		updateRollups(aboveThisRow, params);
	}// deleteRollup()

	private HashMap<String, String> getDeleteParams(String rowId) throws Exception {
		BasicDBObject deleteThisRow = db.getRow(rowId);
		return getDeleteParams(deleteThisRow);
	}

	private HashMap<String, String> getDeleteParams(BasicDBObject deleteThisRow) {
		HashMap<String, String> params = new HashMap<>();
		for (Entry<String, Object> pair : deleteThisRow.entrySet()) {
			if (pair.getKey().startsWith("_"))
				continue;
			params.put(pair.getKey(), null); // Null as a value means remove
												// this field rollup
		}
		return params;
	}// getDeleteParams()
}
