package org.ladders.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONStringer;
import org.json.JSONWriter;
import org.ladders.db.AbstractDataStorage;
import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.db.MyRecord.MyType;
import org.ladders.util.Cols;
import org.ladders.util.U;

public class GetRollupsHandler extends BaseHandler
{
	private static boolean canDoSum(Set<String> values)
	{

		for (String sVal : values)
		{
			if (U.isNullOrBlank(sVal))
				continue;
			if (!U.isNumber(sVal))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	protected void innerHandle() throws Exception
	{

		String pIds = this.getInputFromArr(0);
		String rType = this.getInputFromArr(1);
		String columnName = this.getInputFromArr(2);
		String groupingStyle = this.getInputFromArr(3);
		String sumColumnName = groupingStyle.equals("group") ? this.getInputFromArr(4) : null;

		AbstractDataStorage dao = LadderFactory.getLadder(this.getLadderName());

		StringBuffer json = new StringBuffer();
		json.append("{");

		for (String pid : pIds.split(","))
		{
			String parentJson = getRollup(dao, pid, rType, columnName, groupingStyle, sumColumnName);
			json.append("\"" + pid + "\"").append(":").append(parentJson).append("\n,\n");
		}
		json.append("}");

		successOut("rollup", json.toString());
	}

	private String getRollup(AbstractDataStorage dao, String pId, String rType, String columnName,
			String groupingStyle, String sumColumnName) throws Exception
	{

		HashSet<String> ids = new HashSet<String>();
		ids.add(pId);

		HashMap<String, Double> dataMap = new HashMap<String, Double>();
		findChildrenRecursively(dao, ids, columnName, rType, sumColumnName, dataMap);

		Set<String> values = dataMap.keySet();
		if (values.size() > 0)
		{

			if (groupingStyle.equals("sum") && canDoSum(values))
			{
				double sum = 0;
				for (String sVal : values)
				{
					if (U.isNullOrBlank(sVal))
						continue;
					Double dVal = Double.parseDouble(sVal);
					double occurance = dataMap.get(sVal);
					sum += dVal * occurance;
				}
				return "" + sum;

			} else
			{
				JSONWriter json = new JSONStringer();

				json.object();
				for (String sVal : values)
				{
					double occurance = dataMap.get(sVal);
					json.key(sVal).value(occurance);
				}
				json.endObject();
				return json.toString();
			}
		}

		return "null";
	}

	@Override
	protected boolean actionOnLadder()
	{
		return true;
	}

	private static void findChildrenRecursively(AbstractDataStorage dao, HashSet<String> ids, String columnName,
			String rType, String sumColumnName, HashMap<String, Double> dataMap) throws Exception
	{
		String[] fields = { Cols.ROWID, Cols.ROWTYPE, columnName, sumColumnName };
		ArrayList<MyRecord> children = dao.getRows(Cols.PARENTID, ids, fields);

		HashSet<String> childrenIds = new HashSet<String>();
		for (MyRecord rec : children)
		{
			if (rType.equals(rec.getString(Cols.ROWTYPE)))
			{
				String val = "";
				if (!rec.hasNullValue(columnName))
				{
					val = rec.getString(columnName);
				}

				double sumVal = 0;
				if (sumColumnName==null || sumColumnName.equals("Count"))
				{
					sumVal = 1;
				} else if (rec.getType(sumColumnName) == MyType.Number)
				{
					sumVal = rec.getNumber(sumColumnName);
				}

				if (!dataMap.containsKey(val))
					dataMap.put(val, sumVal);
				else
					dataMap.put(val, dataMap.get(val).doubleValue() + sumVal);
			} else
			{
				childrenIds.add(rec.getString(Cols.ROWID));
			}
		}//for

		if (childrenIds.size() > 0)
		{
			findChildrenRecursively(dao, childrenIds, columnName, rType, sumColumnName, dataMap);
		}

	}
}
