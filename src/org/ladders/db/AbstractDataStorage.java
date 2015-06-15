package org.ladders.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;
import org.ladders.util.SettingsUtil;
import org.ladders.util.U;

public abstract class AbstractDataStorage
{
	public static boolean		VALIDATE		= true;
	//protected RollupsManager	rollupsManager	= null;

	// Private constructor. Forced singleton
	protected AbstractDataStorage(String ladderName) throws Exception
	{
		if (U.isNullOrBlank(ladderName))
		{
			throw new Exception("LADDER can't be null");
		}

		//rollupsManager = new RollupsManager(this);
	}

	public final MyRecord getRow(String rowId) throws Exception
	{
		HashMap<String, String> criteria = new HashMap<String, String>();
		criteria.put(Cols.ROWID, rowId);
		ArrayList<MyRecord> arr = getRows(criteria, null);
		if (arr.size() == 0)
			return null;
		else if (arr.size() > 1)
		{
			throw new Exception("rowId:" + rowId + " returned " + arr.size() + " records. Something went wrong");
		}
		return arr.get(0);
	}

	public abstract ArrayList<MyRecord> getRows(HashMap<String, String> andPairs, String[] fieldsList) throws Exception;

	public abstract ArrayList<MyRecord> getRows(String keyname, HashSet<String> documentIds, String[] fieldsList) throws Exception;

	//public abstract String getSetting(String key) throws Exception;

	//public abstract void setSetting(String key, String val) throws Exception;

	public abstract void delete(MyRecord row) throws Exception;

	abstract void update(String id, MyRecord row) throws Exception; // Only gets
																	// called
																	// from a
	// friend class
	// RollupManager.

	protected abstract void indexBy(String key);

	public abstract void removeAllRows() throws Exception;

	public final void move(String rowId, String newParentId) throws Exception
	{
		

		MyRecord moveThisRow = getRow(rowId);
		MyRecord newParentRow = getRow(newParentId);

		if (moveThisRow == null)
		{
			throw new Exception("Row to move " + rowId + " doesn't exist");
		}
		if (newParentRow == null)
		{
			throw new Exception("New Parent " + newParentId + " doesn't exist");
		}

		if (moveThisRow.getString(Cols.PARENTID).equals(newParentId))
		{
			throw new Exception(rowId + " is already in the parent " + newParentId);
		}

		//rollupsManager.moveRollup(moveThisRow, newParentRow);
		moveThisRow.put(Cols.PARENTID, newParentId);
		moveThisRow.put(Cols.PARENTID, newParentId);

		this.update(rowId, moveThisRow);
	}

	public final MyRecord update(String _rowId, String forKey, String newVal) throws Exception
	{
		if (_rowId == null)
			throw new Exception("Can't update without _rowId:" + _rowId);

		// BasicDBObject row = getRow(_rowId);
		MyRecord rawRecord = getRow(_rowId);

		rawRecord.put(forKey, newVal);

		validateRow(rawRecord);

		rawRecord.put(Cols.UPDATE_DATE, new java.util.Date());

		this.update(_rowId, rawRecord);

		MyRecord makeSureRowExists = this.getRow(_rowId);
		if (makeSureRowExists == null)
		{
			U.log("AbstractDS.update FAILED. id:" + _rowId);
			throw new Exception("Update failed AbstractDS.update _rowId:" + _rowId + "  rawRecord:" + rawRecord);
		}

		indexBy(forKey);

		/*
		if (rollup && !forKey.startsWith("_"))
		{
			// Now update the rollups
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(forKey, newVal);
			rollupsManager.updateRollups(rawRecord, params);
		}
		*/
		return rawRecord;
	}

	protected final static void validateRow(MyRecord row) throws Exception
	{

		if (!VALIDATE)
			return;

		String _parentId = row.getString(Cols.PARENTID);
		String _rowId = row.getString(Cols.ROWID);
		row.getString(Cols.DESCRIPTION);
		
		if (_rowId == null)
			throw new Exception("BAD ROW _rowId:" + _rowId + " row:" + row);
		else if (_parentId.equals(_rowId))
			throw new Exception("BAD ROW _rowId:" + _rowId + " _parentId:" + _parentId);
		else if (_parentId.substring(0, 2).equals(_rowId.substring(0, 2)))
			throw new Exception("BAD ROW _rowId:" + _rowId + " _parentId:" + _parentId);

		if (!row.containsField(Cols.ROWTYPE))
		{
			throw new Exception(Cols.ROWTYPE + " must exist in the row:" + JsonUtil.toJsonFromRaw((row)));
		}
		if (!row.containsField(Cols.PRIORITY))
		{
			throw new Exception(Cols.PRIORITY + " must exist in the row:" + JsonUtil.toJsonFromRaw((row)));
		}
		if (!row.containsField(Cols.GRANDPAID))
		{
			throw new Exception(Cols.GRANDPAID + " must exist in the row:" + JsonUtil.toJsonFromRaw((row)));
		}

	}

	private final void validate(Map<String, String> params) throws Exception
	{
		if (!params.containsKey(Cols.PARENTID))
			throw new Exception(Cols.PARENTID + " needed.");
		if (params.get(Cols.PARENTID).equals(Cols.ROOT_PARENT_ID))
			return;

		// Make sure PARENTID exists
		MyRecord parentRow = getRow(params.get(Cols.PARENTID));
		if (parentRow == null)
			throw new Exception(params.get(Cols.PARENTID) + " Parent doesn't exist.");
	}

	protected final String getNextId(String rowType) throws Exception
	{

		rowType = rowType.substring(0, 2).toUpperCase();

		String key = rowType + "_nextId";

		long id = 0;
		synchronized (this)
		{ // can optimize this further
			// U.log("key:"+key);
			String nextId = SettingsUtil.getSetting(key);
			// U.log("nextId:"+nextId );
			if (nextId != null)
				id = Long.parseLong(nextId);
			id++;
			SettingsUtil.saveSetting(key, "" + id);
			// U.log("id:"+id );
		}

		String base32 = Long.toString(id, 32);
		return rowType + "-" + base32;
	}

	protected final long getCurrentIdIndex(String rowType) throws Exception
	{
		rowType = rowType.substring(0, 2).toUpperCase();
		String key = rowType + "_nextId";

		String nextId = SettingsUtil.getSetting(key);
		long id = (nextId == null) ? 0 : Long.parseLong(nextId);
		return id;
	}

	public final MyRecord insertNew(String rowType, HashMap<String, String> params) throws Exception
	{
		validate(params);
		if (rowType == null)
			throw new Exception("rowType is NULL");

		String _rowId = getNextId(rowType);
		params.put(Cols.ROWID, _rowId);

		// ---validate
		String _parentId = params.get(Cols.PARENTID);
		if (_parentId.equals(_rowId))
			throw new Exception("_rowId:" + _rowId + " _parentId:" + _parentId);
		else if (_parentId.substring(0, 2).equals(_rowId.substring(0, 2)))
			throw new Exception("_rowId:" + _rowId + " _parentId:" + _parentId);

		MyRecord row = new MyRecord();
		for (Entry<String, String> pair : params.entrySet())
		{
			indexBy(pair.getKey());
			row.put(pair.getKey(), pair.getValue());
		}

		row.put(Cols.ROWTYPE, rowType);

		row.put(Cols.CREATED_DATE, new java.util.Date());
		row.put(Cols.UPDATE_DATE, new java.util.Date());
		row.put(Cols.PRIORITY, getCurrentIdIndex(rowType));

		// put grandpa id
		if (!_parentId.equals(Cols.ROOT_PARENT_ID))
		{
			String grandpaId = getRow(_parentId).getString(Cols.PARENTID);
			row.put(Cols.GRANDPAID, grandpaId);
		} else
		{
			row.put(Cols.GRANDPAID, "GRANDROOT");
		}

		validateRow(row);

		this.update(_rowId, row);

		row = this.getRow(_rowId);
		if (row == null)
		{
			throw new Exception("Expecting to see newly added/updated record for _rowId:" + _rowId + ". We got NULL");

		}

		// Now update the rollups
		//rollupsManager.updateRollups(row, params);

		return row;
	}// insert

	public final ArrayList<MyRecord> delete(String rowId) throws Exception
	{

		// See if this row has any children. If yes, don't delete it

		HashMap<String, String> parentCriteria = new HashMap<>();
		parentCriteria.put(Cols.PARENTID, rowId);
		int numberOfChildren = this.getRows(parentCriteria, null).size();

		//U.log(rowId + " numberOfChildren:" + numberOfChildren);
		if (numberOfChildren > 0)
		{
			throw new Exception("Can't delete parent without deleting children. (" + numberOfChildren + ")");
		}

		MyRecord rawRecord = getRow(rowId);
		this.delete(rawRecord);

		// Now update the rollups
		HashMap<String, String> params = new HashMap<String, String>();
		for (String key : rawRecord.keySet())
		{
			if (key.startsWith("_"))
				continue;
			params.put(key, null);
		}

		//rollupsManager.updateRollups(rawRecord, params);

		ArrayList<MyRecord> rows = new ArrayList<MyRecord>();
		rows.add(rawRecord);

		return rows;
	}

}
