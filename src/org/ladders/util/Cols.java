package org.ladders.util;

public class Cols
{
	public static final String	ROWID				= "_rowId";
	public static final String	PARENTID			= "_parentId";
	//public static final String CREATEDDATE = "_createdDate";
	//public static final String UPDATEDDATE = "_updatedDate";
	public static final String	ROWTYPE				= "_rowType";
	public static final String	CREATED_DATE		= "_createdDate";
	public static final String	UPDATE_DATE			= "_updateDate";
	public static final String	PRIORITY			= "_priority";
	public static final String	GRANDPAID			= "_grandpaid";
	//public static final String	ROLLUP_PREFIX		= "_RLUP_";
	public static final String	ROOT_PARENT_ID		= "ROOT";
	public static final String	_PARENT_DESCRIPTION	= "_PARENT_DESCRIPTION";
	public static final String	DESCRIPTION			= "Description";
	public static final String	NAME				= "Name";

	public static boolean isSystemCol(String name)
	{
		if (name.equals(ROWID))
			return true;
		if (name.equals(PARENTID))
			return true;
		if (name.equals(ROWTYPE))
			return true;
		if (name.equals(CREATED_DATE))
			return true;
		if (name.equals(UPDATE_DATE))
			return true;
		if (name.equals(GRANDPAID))
			return true;
		if (name.equals(ROOT_PARENT_ID))
			return true;

		return false;
	}
}