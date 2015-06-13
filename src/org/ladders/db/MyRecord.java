package org.ladders.db;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;
import org.ladders.util.U;

public class MyRecord
{
	private HashMap<String, Object>	map	= new HashMap<String, Object>();

	public double getNumber(String key) throws Exception
	{
		if (this.getType(key) == MyType.Number)
			return Double.parseDouble(map.get(key).toString());
		else
			throw new Exception(key + " isn't Double. It is " + map.get(key).getClass() + " val:" + map.get(key));
	}

	public boolean hasNullValue(String k) throws Exception
	{
		Object val = map.get(k);
		return (val==null);
	}
	
	public String getString(String k) throws Exception
	{
		Object val = map.get(k);
		if (val instanceof String)
			return (String) val;
		else if (val==null)
			throw new Exception(k + " value is NULL");
		else
			throw new Exception(k + " isn't String. It is " + val.getClass() + " val:" + val);
	}

	public Object getObject(String k) throws Exception
	{
		return map.get(k);
	}

	public HashMap<String, String> getMap(String key) throws Exception
	{
		if (!map.containsKey(key))
			return null;

		Object val = map.get(key);
		if (val == null)
			return null;

		if (val instanceof HashMap)
			return (HashMap<String, String>) val;
		else
			throw new Exception(key + " isn't Hashmap. It is " + val.getClass() + " val:" + val);

	}

	public void put(String k, String v)
	{
		map.put(k, v);
	}

	public void put(String k, double v)
	{
		map.put(k, v);
	}

	public void put(String k, HashMap<String, String> v)
	{
		map.put(k, v);
	}

	public void put(String k, Date v)
	{
		map.put(k, v);
	}

	public boolean containsField(String k)
	{
		return map.containsKey(k);
	}

	public Set<String> keySet()
	{
		return map.keySet();
	}

	public void remove(String rowId)
	{
		map.remove(rowId);
	}

	/*
	 * public boolean isString(String key) { return map.get(key) instanceof
	 * String; }
	 * 
	 * public boolean isNumber(String key) { return map.get(key) instanceof Long
	 * || map.get(key) instanceof Integer || map.get(key) instanceof Float ||
	 * map.get(key) instanceof Double; }
	 * 
	 * public boolean isMap(String key) { return map.get(key) instanceof
	 * HashMap; }
	 * 
	 * public boolean isDate(String key) { // TODO Auto-generated method stub
	 * return map.get(key) instanceof Date; }
	 */

	public MyType getType(String key) throws Exception
	{
		/*if (key.startsWith(Cols.ROLLUP_PREFIX))
			return MyType.Map;*/
		Object o = map.get(key);
		if (o == null)
			return MyType.String;
		else if (U.isNumber(o) )
			return MyType.Number;
		else if (o instanceof Date)
			return MyType.Date;
		else if (o instanceof String)
			return MyType.String;
		else
			throw new Exception(key + "=" + o + " Not supported");
	}

	@Override
	public String toString()
	{
		try
		{
			return "" + JsonUtil.toJsonFromRaw(this);
		} catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR:" + e.toString();
		}

	}

	public enum MyType
	{
		Number, Date, String, Map
	};

}
