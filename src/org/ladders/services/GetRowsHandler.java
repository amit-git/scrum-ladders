package org.ladders.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.AbstractDataStorage;
import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;
import org.ladders.util.U;

public class GetRowsHandler extends BaseHandler
{

	@Override
	protected void innerHandle() throws Exception
	{

		String[] fieldsList = null;
		if (inputParams.containsKey("FIELDS"))
		{
			fieldsList = inputParams.get("FIELDS").split(",");
			//U.log("fieldsList:"+inputParams.get("FIELDS"));
		}
		AbstractDataStorage dao = LadderFactory.getLadder(this.getLadderName());

		ArrayList<MyRecord> allRows = null;
		// AND:f1=20,f2=40,f3=dddd
		if (inputParams.containsKey("AND"))
		{
			assertTrue(false, "AND Not supported now");
			/*
			HashMap<String, String> andPairs = new HashMap<String, String>();

			String andParams = inputParams.get("AND");
			for (String cond : andParams.split(","))
			{
				String[] pair = splitPair(cond, ":");
				if (pair.length != 2)
					continue;
				andPairs.put(pair[0], pair[1]);
			}// for p
			allRows = dao.getRows(andPairs, fieldsList);
			*/
		} else if (inputParams.containsKey("IN"))
		{
			String inParams = inputParams.get("IN");
			String[] pair = splitPair(inParams, ":");

			String[] docIdList = pair[1].split(",");
			HashSet<String> h = new HashSet<String>(Arrays.asList(docIdList));
			allRows = dao.getRows(pair[0], h, fieldsList);
		}else{
			HashMap<String, String> andPairs = new HashMap<String, String>();
			andPairs.put(Cols.ROWTYPE, this.rowType);
			
			for (String key : inputParams.keySet())
			{
				andPairs.put(key, inputParams.get(key));
			}// for p
			
			allRows = dao.getRows(andPairs, fieldsList);
		}

		//U.log("Got-" + allRows.size() + "-rows");

		ArrayList<MyRecord> allParentRows = new ArrayList<MyRecord>();
		if (allRows.size() > 0)
		{
			//Get parent name  _PARENT_DESCRIPTION
			fillParentDescriptions(allRows);
		}

		StringBuffer buf1 = new StringBuffer("{");
		buf1.append("Rows:").append(JsonUtil.toJsonFromRaw(allRows));
		buf1.append(",\n\nParentRows:").append(JsonUtil.toJsonFromRaw(allParentRows));
		buf1.append("}");

		tsLogger.log("Jsonified");

		successOut("Got rows", buf1.toString());
	}

	private static HashSet<String> getUniqueVals(String fld, ArrayList<MyRecord> allRows) throws Exception
	{
		HashSet<String> ids = new HashSet<String>();
		for (MyRecord dbObj : allRows)
		{
			String pid = dbObj.getString(fld);
			if (pid == null)
				continue;
			
			ids.add(pid);
		}
		return ids;
	}// getAllParentIds()

	private void fillParentDescriptions(ArrayList<MyRecord> allRows) throws Exception
	{
		HashSet<String> parentIds = getUniqueVals(Cols.PARENTID, allRows);
		
		AbstractDataStorage dao = LadderFactory.getLadder(this.getLadderName());
		
		ArrayList<MyRecord> allParentRows = dao.getRows(Cols.ROWID, parentIds, new String[] { Cols.ROWID, Cols.DESCRIPTION });

		HashMap<String, String> descriptions = new HashMap<String, String>();
		for (MyRecord rec : allParentRows)
		{
			//U.log("PARENTS fillParentDescriptions: "+rec);
			descriptions.put(rec.getString(Cols.ROWID), rec.getString(Cols.DESCRIPTION));
		}
		
		for (MyRecord rec : allRows)
		{
			String parentId = rec.getString(Cols.PARENTID);
			rec.put(Cols._PARENT_DESCRIPTION, descriptions.get(parentId));
			//U.log("CHILD fillParentDescriptions: "+rec);
		}
	}


	@Override
	protected boolean actionOnLadder()
	{
		return true;
	}

}
