package org.ladders.services;

import org.ladders.db.LadderFactory;

public class DeleteLadderHandler extends BaseHandler
{

	@Override
	protected void innerHandle() throws Exception
	{
		//ladderName = inputParams.get("LADDER_NAME");
		String name = getLadderName();
		LadderFactory.removeLadder(name);
		successOut("Deleted LADDER " + name, name);
	}//innerHandle
 

	@Override
	public boolean actionOnLadder()
	{
		return true;
	}

}