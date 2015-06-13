package org.ladders.services;

import org.apache.commons.lang3.StringUtils;
import org.ladders.db.LadderFactory;
import org.ladders.model.LaddersTopologyModel;
import org.ladders.util.U;

public class SaveLadderSchema extends BaseHandler
{
	//private static final int	TOTAL_TEST_ROWS	= 0;

	@Override
	protected void innerHandle() throws Exception
	{

		String name = getLadderName();
		String schema = inputParams.get("SCHEMA_JSON");

		U.log("SaveLadderSchema ladderName:"+name);
		
		if (StringUtils.isEmpty(schema))
		{
			throw new Exception("LADDER schema can't be null or empty");
		}

		LaddersTopologyModel ltm = new LaddersTopologyModel(schema);

		//if (!LadderFactory.contains(ladderName))
		{
			//LadderFactory.removeLadder(ladderName);
			LadderFactory.add(name, ltm);
		}

		if (!ltm.contains(name))
		{
			//List<LaddersContextModel> contextQ = ltm.getContexts();
			//insertRows(ladderName, Cols.ROOT_PARENT_ID, contextQ, 0);
		}

		successOut("Created LADDER " + name, schema);

	}//innerHandle

	@Override
	public boolean actionOnLadder()
	{
		return true;
	}

	/*
	private static void insertRows(String ladderName, String parentId, List<LaddersContextModel> contextQ, int i) throws Exception
	{

		if (i >= contextQ.size())
			return;

		LaddersContextModel context = contextQ.get(i);

		ArrayList<String> addedRows = new ArrayList<String>();
		for (int j = 0; j < TOTAL_TEST_ROWS; j++)
		{
			String rowId = insertRow(j, ladderName, context, parentId);
			addedRows.add(rowId);
		}// for j

		i++;
		for (String rowId : addedRows)
		{
			insertRows(ladderName, rowId, contextQ, i);
		}
	}
	
	private static String insertRow(int i, String ladderName, LaddersContextModel context, String parentId) throws Exception
	{

		U.log(" insertRow :" + parentId);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Cols.PARENTID, parentId);

		for (LaddersSchemaModel sm : context.getSchema())
		{
			// U.log("  Schema.Name:" + sm.Name);
			if (sm.Args.size() > 0)
			{
				params.put(sm.Name, sm.Args.get(i % sm.Args.size()));
			} else
			{
				params.put(sm.Name, i + "th " + context.getName() + " for " + parentId);
			}
		}

		U.log("A CreateNewLadder.insertRow ladderName:" + ladderName + " params:" + params);

		MyRecord row = LadderFactory.getLadder(ladderName).insertNew(context.getName(), params);

		U.log("B CreateNewLadder.insertRow row:" + row);

		return row.getString(Cols.ROWID);

	}
	*/

}