package org.ladders.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONWriter;
import org.ladders.db.MyRecord;
import org.ladders.db.MyRecord.MyType;

public class JsonUtil
{
	private static HashSet<String>	dontSendList	= new HashSet<String>();
	static
	{
		dontSendList.add("_createdDate");
		dontSendList.add("_updateDate");
		dontSendList.add("_id");
	}

	public static StringBuffer toJsonFromRaw(ArrayList<MyRecord> rows) throws Exception
	{
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for (MyRecord row : rows)
		{
			if (buf.length() > 1)
				buf.append(",");
			buf.append(toJsonFromRaw(row).toString());
		}
		buf.append("]");
		return buf;
	}

	public static JSONWriter toJsonFromRaw(MyRecord row) throws Exception
	{
		JSONWriter myString = new JSONStringer();
		myString.object();
		for (String k : row.keySet())
		{

			if (dontSendList.contains(k))
				continue;

			if (row.getType(k)==MyType.Map)
			{
				myString.key(k).value(row.getMap(k));
			} else
			{
				Object o = row.getObject(k);
				myString.key(k).value(o);
			}
		}
		myString.endObject();

		// U.log("ROW:"+myString);

		return myString;
	}

	public static String map2Json(HashMap<String, String> map) throws Exception
	{
		if (map == null)
			return "";
		JSONWriter myString = new JSONStringer();
		myString.object();
		for (String k : map.keySet())
		{
			myString.key(k).value(map.get(k));
		}
		myString.endObject();
		return myString.toString();
	}

	public static HashMap<String, String> json2Map(String json)
	{
		if (U.isNullOrBlank(json) || json.equals("null"))
			return null;

		//U.log("json:" + json);
		JSONTokener tokener = new org.json.JSONTokener(json);
		JSONObject jobj = new JSONObject(tokener);

		HashMap<String, String> map = new HashMap<String, String>();
		for (Object key : jobj.keySet())
		{
			Object o = jobj.get(key.toString());
			String v = o.toString();
			//U.log("json2Map: "+key+"="+v);
			map.put(key.toString(), v);
		}

		return map;
	}

	
	public static MyRecord toRecord(String json)
	{
		if (U.isNullOrBlank(json) || json.equals("null"))
			return null;

		JSONTokener tokener = new org.json.JSONTokener(json);
		JSONObject jobj = new JSONObject(tokener);

		MyRecord rec = new MyRecord();
		for (Object key : jobj.keySet())
		{
			Object o = jobj.get(key.toString());
			String v = o.toString();
			rec.put(key.toString(), v);
		}

		return rec;
	}
	
	/*
	 * public static StringBuffer toJson1(ArrayList<BasicDBObject> rows) {
	 * StringBuffer buf = new StringBuffer(); buf.append("["); for
	 * (BasicDBObject row : rows) { if (buf.length() > 1) buf.append(",");
	 * buf.append(toJson(row).toString()); } buf.append("]"); return buf; }
	 * 
	 * 
	 * public static JSONWriter toJson1(BasicDBObject row) { JSONWriter myString
	 * = new JSONStringer(); myString.object(); for (Entry<String, Object> pair
	 * : row.entrySet()) { String k = pair.getKey(); // U.log("k:"+k); if
	 * (dontSendList.contains(k)) continue;
	 * 
	 * if (k.startsWith(Cols.ROLLUP_PREFIX)) {
	 * myString.key(k).value(convertToRollup(pair.getValue())); } else {
	 * myString.key(k).value(pair.getValue()); } } myString.endObject();
	 * 
	 * // U.log("ROW:"+myString);
	 * 
	 * return myString; }
	 */
/*
	private static HashMap<String, Double> convertToRollup(Object mapObj)
	{
		HashMap<String, Double> retMap = new HashMap<String, Double>();
		HashMap<String, String> rollupMap = (HashMap<String, String>) mapObj;
		for (Entry<String, String> pair : rollupMap.entrySet())
		{
			String v = pair.getValue();
			// i don't care about ids. Just values.
			if (!retMap.containsKey(v))
			{
				retMap.put(v, 1.0);
			} else
			{
				retMap.put(v, retMap.get(v) + 1);
			}
		}
		return retMap;
	}
*/
	
}
