package org.ladders.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ladders.util.U;

public class LaddersSchemaModel {
	public String Name;
	public String Validation;
	public int MaxLen;
	public int MinLen;
	public List<String> Args = new ArrayList<String>();

	public LaddersSchemaModel(JSONObject fieldObj) {
		Name = fieldObj.getString("Name");
		if (fieldObj.has("Args")) {
			JSONArray arrArgs = fieldObj.getJSONArray("Args");
			for (int i = 0; i < arrArgs.length(); i++) {
				//U.log(" arrArgs["+i+"]"+arrArgs.get(i));
				String argVal = ""+arrArgs.get(i);
				Args.add(argVal);
			}
		}

	}// LaddersSchemaModel()
}// class LaddersSchemaModel