package org.ladders.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.ladders.util.U;

public class LaddersSchemaModel
{
	private static final String	ARGS	= "Args";
	public String				Name;
	public String				Validation;
	public int					MaxLen;
	public int					MinLen;
	public List<String>			Args	= new ArrayList<String>();

	public LaddersSchemaModel(JSONObject fieldObj)
	{
		Name = fieldObj.getString("Name");
		if (fieldObj.has(ARGS))
		{
			U.log("Args Obj:" + fieldObj.get(ARGS));
			U.log("Args Class:" + fieldObj.get(ARGS).getClass());

			if (fieldObj.get(ARGS) instanceof JSONArray)
			{
				JSONArray arrArgs = fieldObj.getJSONArray(ARGS);
				for (int i = 0; i < arrArgs.length(); i++)
				{
					String a = "" + arrArgs.get(i);
					if (a.trim().length() > 0)
						Args.add(a.trim());
				}
			} else if (fieldObj.get(ARGS) instanceof JSONString)
			{
				String strArgs = fieldObj.getString(ARGS);
				U.log("strArgs:"+strArgs);

				for (String a : strArgs.split(","))
				{
					if (a.trim().length() > 0)
						Args.add(a.trim());
				}
			}

		}

	}// LaddersSchemaModel()
}// class LaddersSchemaModel