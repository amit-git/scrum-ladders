package org.ladders.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ladders.model.LaddersTopologyModel;
import org.ladders.util.SettingsUtil;
import org.ladders.util.U;

public class LadderFactory
{

	private static final String	DELIM			= ",,,";
	private static final String	LADDERS_LIST	= "___LADDERS_LIST__";

	//private static final String							PATH	= "/STATIC/SETTINGS/_LadderMaster.txt";
	//private static HashMap<String, AbstractDataStorage>	pool	= new HashMap<String, AbstractDataStorage>();

	public synchronized static AbstractDataStorage getLadder(String ladderName) throws Exception
	{
		if (!contains(ladderName))
		{
			throw new Exception("Ladder '" + ladderName + "' doesn't exist");
			// Hardcoded to Lucene
			//AbstractDataStorage ds = new LuceneDataStorage(ladderName); 
			//pool.put(ladderName, ds);
		}
		return new CSVDataStorage(ladderName);
	}

	/*
	public static void reset() throws Exception
	{
		pool = new HashMap<String, AbstractDataStorage>();
	}
	*/

	public final static void removeLadder(String ladderName) throws Exception
	{
		//Drop the data
		AbstractDataStorage ds = getLadder(ladderName);
		ds.removeAllRows();

		//remove ladder from registry
		{
			ArrayList<String> list = getAll();
			list.remove(ladderName);
			String text = StringUtils.join(list, DELIM);
			SettingsUtil.saveSetting(LADDERS_LIST, text);
		}

		//Remove schema
		SettingsUtil.deleteSetting("SCHEMA_" + ladderName);
	}

	public static ArrayList<String> getAll() throws IOException
	{
		ArrayList<String> list = new ArrayList<String>();
		String txt = SettingsUtil.getSetting(LADDERS_LIST);

		if (U.isNullOrBlank(txt))
			return list;
		else
			txt = txt.trim();

		Collections.addAll(list, txt.split(DELIM));

		return list;
	}

	public synchronized static void add(String ladderName, LaddersTopologyModel ltm) throws Exception
	{
		ArrayList<String> list = getAll();
		if (!list.contains(ladderName))
		{
			list.add(ladderName);
		}
		String text = StringUtils.join(list, DELIM);
		SettingsUtil.saveSetting(LADDERS_LIST, text);
		SettingsUtil.saveSetting("SCHEMA_" + ladderName, ltm.getJsonSchema());

	}

	public static String getSchema(String ladderName) throws IOException
	{
		String js = SettingsUtil.getSetting("SCHEMA_" + ladderName);
		return js;
	}

	public static boolean contains(String ladderName) throws IOException
	{
		List<String> ladders = getAll();
		return (ladders.contains(ladderName));
	}

}
