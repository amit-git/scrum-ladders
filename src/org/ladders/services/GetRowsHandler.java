package org.ladders.services;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.DataStorage;
import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class GetRowsHandler extends BaseHandler2 {
	

	@Override
	protected void innerHandle() throws Exception {
		if (StringUtils.isEmpty(rowType))
			throw new Exception("No rowType identified");		
		
		// AND:f1=20,f2=40,f3=dddd
		BasicDBObject criteria = new BasicDBObject();
		if (inputParams.containsKey("AND")) {
			String andParams = inputParams.get("AND");
			for (String cond : andParams.split(",")) {
				String[] pair = splitPair(cond, ":");
				if (pair.length != 2)
					continue;
				criteria.append(pair[0], pair[1]);
			}// for p
		} else if (inputParams.containsKey("IN")) {
			String inParams = inputParams.get("IN");
			String[] pair = splitPair(inParams, ":");

			// BasicDBObject criteria = new BasicDBObject(fieldName, inClause);
			BasicDBList docIds = new BasicDBList();
			for (String v : pair[1].split(",")) {
				docIds.add(v);
			}
			DBObject inClause = new BasicDBObject("$in", docIds);
			criteria.append(pair[0], inClause);
		}

		BasicDBObject fields = null;
		if (inputParams.containsKey("FIELDS")) {
			fields = new BasicDBObject();
			for (String field : inputParams.get("FIELDS").split(",")){
				fields.put(field, 1);
			}
		}

		ArrayList<BasicDBObject> allRows = dao.getRows(criteria, fields);
		tsLogger.log("Got-" + allRows.size() + "-rows");
		
		ArrayList<BasicDBObject> allParentRows = new ArrayList<>();
		if (allRows.size()>0){
			HashSet<String> grandpaIds = getUniqueVals(Cols.GRANDPAID, allRows);
			if (inputParams.containsKey("INCLUDE_PARENTS")) 
			{
				allParentRows = dao.getRows(Cols.PARENTID, grandpaIds);
				if (allParentRows.size()==0){
					allParentRows.add(dao.getRow(parentId));
				}
				tsLogger.log("Got-" + allParentRows.size() + "-Parents");
			}
		}

		StringBuffer buf1 = new StringBuffer("{");
		buf1.append("Rows:").append(JsonUtil.toJson(allRows));
		buf1.append(",\n\nParentRows:").append(JsonUtil.toJson(allParentRows));
		buf1.append("}");

		tsLogger.log("Jsonified");

		successOut("Got rows", buf1.toString());
	}

	private static HashSet<String> getUniqueVals(String fld, ArrayList<BasicDBObject> allRows) {
		HashSet<String> ids = new HashSet<>();
		for (BasicDBObject dbObj : allRows) {
			String pid = dbObj.getString(fld);
			if (pid == null)
				continue;
			ids.add(pid);
		}
		return ids;
	}// getAllParentIds()

	@Override
	public String getName() {
		return "ROWS";
	}

}
