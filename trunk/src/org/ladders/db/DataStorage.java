package org.ladders.db;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ladders.util.Cols;
import org.ladders.util.FileUtil;
import org.ladders.util.JsonUtil;
import org.ladders.util.U;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;
import com.sun.xml.internal.fastinfoset.stax.events.Util;


public class DataStorage {

	private static String dbUrl = null;
	private static final String SETTINGS_TABLE = "SettingsTable3";
	private static final String ROWS_TABLE = "RowsTable3";
	public static boolean VALIDATE = true;

	private DBCollection rowsTable = null;
	private DBCollection settTable = null;

	private static HashMap<String, DataStorage> pool = new HashMap<String, DataStorage>();
	private RollupsManager rollupsManager = null;

	//No locking. Worst case we will get 2 instances. We can prime it later if it becomes a big problem.
	public static DataStorage get(String ladderName) throws Exception {
		if (!pool.containsKey(ladderName))
			pool.put(ladderName, new DataStorage(ladderName));
		return pool.get(ladderName);
	}

	public static void reset() throws Exception {
		pool = new HashMap<String, DataStorage>();
	}

	public static List<String> getAllLadders() throws Exception {
		validateDbConnection();
		MongoClientURI uri = new MongoClientURI(dbUrl);
		MongoClient m = new MongoClient(uri);
		List<String>  list = m.getDatabaseNames();
		m.close();
		
		return list;
	}

	
	private static void validateDbConnection() throws Exception {
		//if (Util.isEmptyString(dbUrl))
		{
			dbUrl = FileUtil.readTextFromTile( U.startPath("/STATIC/SETTINGS/DBConnection.txt"));
			dbUrl = dbUrl.trim();
		}
		if (Util.isEmptyString(dbUrl)){
			throw new Exception("Database Connection not specified");
		}else{
			U.log("validateDbConnection URL:"+dbUrl);
		}
	}	
	private DataStorage(String ladderName) throws Exception {
		validateDbConnection();
		if (Util.isEmptyString(ladderName)){
			throw new Exception("LADDER can't be null");
		}
		
		
		MongoClientURI uri = new MongoClientURI(dbUrl);
		MongoClient m = new MongoClient(uri);
		DB db2 = m.getDB(ladderName);
		rowsTable = db2.getCollection(ROWS_TABLE);
		settTable = db2.getCollection(SETTINGS_TABLE);

		{
			// Make _rowId unique
			BasicDBObject query = new BasicDBObject(Cols.ROWID, 1);
			rowsTable.ensureIndex(query, "_rowId_unique", true);
		}
		{
			// Index by priority and by created date
			BasicDBObject query = new BasicDBObject(Cols.PRIORITY, 1).append(Cols.CREATED_DATE, 1);
			rowsTable.ensureIndex(query, "_pri_createddate_sort");
		}

		{
			// Index by parentId
			BasicDBObject query = new BasicDBObject(Cols.PARENTID, 1);
			rowsTable.ensureIndex(query, "_parent_sort");
		}
		
		rollupsManager = new RollupsManager(this, rowsTable);
	}

	/*
	public ArrayList<BasicDBObject> getRows(String rowType, HashMap<String, String> params) throws Exception {

		BasicDBObject criteria = new BasicDBObject();

		for (Entry<String, String> pair : params.entrySet()) {
			String v = pair.getValue();
			String[] arr = v.split(",");
			if (arr.length > 1) {
				BasicDBList docIds = new BasicDBList();
				docIds.addAll(Arrays.asList(arr));
				DBObject inClause = new BasicDBObject("$in", docIds);
				criteria.append(pair.getKey(), inClause);
			} else {
				criteria.append(pair.getKey(), v);
			}
		}

		if (rowType != null)
			criteria.append(Cols.ROWTYPE, rowType);

		return getRows(criteria);
	}
	*/
	public String getAllRowsInLadder(String rowId) throws Exception {

		StringBuffer buf = new StringBuffer();

		BasicDBObject row = null;
		while ((row = getRow(rowId)) != null) {
			if (buf.length() == 0) {
				buf.append("[");
			} else {
				buf.append(",");
			}

			buf.append(row.toString());
			rowId = row.getString(Cols.PARENTID);
		}
		buf.append("]");
		return buf.toString();
	}

	public ArrayList<BasicDBObject> getRows(BasicDBObject criteria, BasicDBObject fields) throws Exception {
		// criteria.
		//U.log("SELECT " + fields + " FROM WHERE " + criteria);

		DBCursor cursor = fields != null ? rowsTable.find(criteria, fields) : rowsTable.find(criteria);

		cursor.sort(new BasicDBObject(Cols.PRIORITY, 1).append(Cols.CREATED_DATE, 1));
		cursor.limit(U.MAX_ROWS);

		ArrayList<BasicDBObject> arr = new ArrayList<BasicDBObject>();
		ArrayList<BasicDBObject> badArr = new ArrayList<BasicDBObject>();

		while (cursor.hasNext()) {
			BasicDBObject row = (BasicDBObject) cursor.next();

			// try {
			validateRow(row);
			// } catch (Exception ex) {
			// badArr.add(row);
			// continue;
			// }
			if (arr.size() > U.MAX_ROWS)
				break;

			arr.add(row);
		}

		// Looks like sort isn't working. I will figure it out later.
		// For now sort it again.
		Collections.sort(arr, new Comparator<BasicDBObject>() {
			@Override
			public int compare(BasicDBObject row1, BasicDBObject row2) {
				double d1 = U.tryParse(row1.getString(Cols.PRIORITY));
				double d2 = U.tryParse(row2.getString(Cols.PRIORITY));
				return (int) (d1 - d2);
			}
		});
		for (BasicDBObject row : badArr) {
			rowsTable.remove(row);
		}
		return arr;
	}

	public BasicDBObject getRow(String rowId) throws Exception {
		BasicDBObject criteria = new BasicDBObject(Cols.ROWID, rowId);
		ArrayList<BasicDBObject> arr = getRows(criteria, null);
		if (arr.size() == 0)
			return null;
		return arr.get(0);
	}

	public String getSetting(String key) throws UnknownHostException {
		BasicDBObject criteria = new BasicDBObject("KEY", key);
		DBCursor cursor = settTable.find(criteria);
		if (cursor.hasNext()) {
			DBObject row = cursor.next();
			return row.get("VAL").toString();
		}
		return null;
	}

	public void setSetting(String key, String val) throws Exception {

		BasicDBObject criteria = new BasicDBObject("KEY", key);
		BasicDBObject newVal = new BasicDBObject("KEY", key);
		newVal.append("VAL", val);

		WriteResult result = settTable.update(criteria, newVal);
		if (result.getN() == 0) {
			// No update done. Just insert it.
			settTable.insert(newVal);
		}
	}
	public void move(String rowId, String newParentId) throws Exception {
		BasicDBObject moveThisRow = getRow(rowId);
		BasicDBObject newParentRow = getRow(newParentId);

		if (moveThisRow == null){
			throw new Exception("Row to move "+ rowId+ " doesn't exist");
		}
		if (newParentRow == null){
			throw new Exception("New Parent "+newParentId+ " doesn't exist");
		}

		if (moveThisRow.getString(Cols.PARENTID).equals(newParentId)){
			throw new Exception(rowId+ " is already in the parent "+newParentId);
		}

		rollupsManager.moveRollup(moveThisRow, newParentRow);
		moveThisRow.put(Cols.PARENTID, newParentId);
		moveThisRow.put(Cols.PARENTID, newParentId);
	}

	public String update(HashMap<String, String> params) throws Exception {

		String _rowId = params.get(Cols.ROWID);
		if (_rowId == null)
			throw new Exception("Can't update without _rowId:" + _rowId);

		BasicDBObject row = getRow(_rowId);

		for (Entry<String, String> pair : params.entrySet()) {
			String k = pair.getKey();
			String v = pair.getValue();
			indexBy(k);
			row.append(k, v);
		}

		validateRow(row);

		row.put(Cols.UPDATE_DATE, new java.util.Date());

		rowsTable.update(new BasicDBObject(Cols.ROWID, _rowId), row);

		// Now update the rollups
		rollupsManager.updateRollups(row, params);

		return row.toString();
	}



	private void indexBy(String key) {
		// Index by field
		BasicDBObject query = new BasicDBObject(key, 1);
		rowsTable.ensureIndex(query, "_key_" + key + "_sort");
	}

	private static void validateRow(BasicDBObject row) throws Exception {

		if (!VALIDATE)
			return;

		String _parentId = row.getString(Cols.PARENTID);
		String _rowId = row.getString(Cols.ROWID);
		if (_rowId == null)
			throw new Exception("BAD ROW _rowId:" + _rowId + " row:"+row);

		if (_parentId.equals(_rowId))
			throw new Exception("BAD ROW _rowId:" + _rowId + " _parentId:" + _parentId);
		else if (_parentId.substring(0, 2).equals(_rowId.substring(0, 2)))
			throw new Exception("BAD ROW _rowId:" + _rowId + " _parentId:" + _parentId);

		if (!row.containsField(Cols.ROWTYPE)) {
			throw new Exception(Cols.ROWTYPE + " must exist in the row:" + JsonUtil.toJson(row));
		}
		if (!row.containsField(Cols.PRIORITY)) {
			throw new Exception(Cols.PRIORITY + " must exist in the row:" + JsonUtil.toJson(row));
		}
		if (!row.containsField(Cols.GRANDPAID)) {
			throw new Exception(Cols.GRANDPAID + " must exist in the row:" + JsonUtil.toJson(row));
		}

	}

	private void validate(Map<String, String> params) throws Exception {
		if (!params.containsKey(Cols.PARENTID))
			throw new Exception(Cols.PARENTID + " needed.");
		if (params.get(Cols.PARENTID).equals("ROOT"))
			return;

		// Make sure PARENTID exists
		BasicDBObject parentRow = getRow(params.get(Cols.PARENTID));
		if (parentRow == null)
			throw new Exception(params.get(Cols.PARENTID) + " Parent doesn't exist.");
	}

	private String getNextId(String rowType) throws Exception {

		rowType = rowType.substring(0, 2).toUpperCase();

		String key = rowType + "_nextId";

		long id = 0;
		synchronized (this) { // can optimize this further
			// U.log("key:"+key);
			String nextId = getSetting(key);
			// U.log("nextId:"+nextId );
			if (nextId != null)
				id = Long.parseLong(nextId);
			id++;
			setSetting(key, "" + id);
			// U.log("id:"+id );
		}

		String base32 = Long.toString(id, 32);
		return rowType + "-" + base32;
	}

	private long getCurrentIdIndex(String rowType) throws Exception {
		rowType = rowType.substring(0, 2).toUpperCase();
		String key = rowType + "_nextId";

		String nextId = getSetting(key);
		long id = (nextId == null) ? 0 : Long.parseLong(nextId);
		return id;
	}

	public BasicDBObject insertNew(String rowType, HashMap<String, String> params) throws Exception {
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

		BasicDBObject row = new BasicDBObject();
		for (Entry<String, String> pair : params.entrySet()) {
			indexBy(pair.getKey());
			row.append(pair.getKey(), pair.getValue());
		}

		row.append(Cols.ROWTYPE, rowType);

		row.put(Cols.CREATED_DATE, new java.util.Date());
		row.put(Cols.UPDATE_DATE, new java.util.Date());
		row.put(Cols.PRIORITY, getCurrentIdIndex(rowType));

		// put grandpa id
		if (!_parentId.equals("ROOT")) {
			String grandpaId = getRow(_parentId).getString(Cols.PARENTID);
			row.put(Cols.GRANDPAID, grandpaId);
		} else {
			row.put(Cols.GRANDPAID, "GRANDROOT");
		}

		validateRow(row);

		rowsTable.save(row);

		// Now update the rollups
		rollupsManager.updateRollups(row, params);

		return row;
	}// insert

	public ArrayList<BasicDBObject> getRows(String fieldName, HashSet<String> documentIds) throws Exception {
		if (documentIds.size()==0){
			return new ArrayList<BasicDBObject>();
		}
		BasicDBList docIds = new BasicDBList();
		docIds.addAll(documentIds);
		DBObject inClause = new BasicDBObject("$in", docIds);
		BasicDBObject criteria = new BasicDBObject(fieldName, inClause);

		return getRows(criteria, null);
	}

	public ArrayList<BasicDBObject> delete(String rowId) throws Exception {

		// See if this row has any children. If yes, don't delete it
		//BasicDBObject fields = new BasicDBObject(Cols.ROWID, 1);
		int numberOfChildren = getRows(new BasicDBObject(Cols.PARENTID, rowId), null).size();
		U.log(rowId+ " numberOfChildren:"+numberOfChildren);
		if (numberOfChildren > 0) {
			throw new Exception("Can't delete parent without deleting children. (" + numberOfChildren + ")");
		}
		
		

		BasicDBObject row = getRow(rowId);
		rowsTable.remove(row);

		// Now update the rollups
		HashMap<String, String> params = new HashMap<String, String>();
		for (Entry<String, Object> pair : row.entrySet()) {
			if (pair.getKey().startsWith("_"))
				continue;
			params.put(pair.getKey(), null);
		}
		rollupsManager.updateRollups(row, params);

		ArrayList<BasicDBObject> rows = new ArrayList<BasicDBObject>();
		rows.add(row);
		return rows;
	}

	public void delete(BasicDBObject row) throws Exception {
		rowsTable.remove(row);
	}

	public static StringBuffer toJson(ArrayList<BasicDBObject> rows) throws UnknownHostException {

		StringBuffer buf = new StringBuffer();
		buf.append("[");

		for (DBObject row : rows) {
			if (buf.length() > 1)
				buf.append(",\n");
			// if (row.toString().equals("null")) continue;
			buf.append(row.toString());
		}
		buf.append("]");
		return buf;

	}

	public static void removeLadder(String ladderName) throws Exception {
		DataStorage.get(ladderName);
		pool.get(ladderName).rowsTable.drop();
		pool.remove(ladderName);
	}

}
