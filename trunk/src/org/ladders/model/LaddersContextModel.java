package org.ladders.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LaddersContextModel
{
	public String Name;
	public List<LaddersSchemaModel>	Schema = new ArrayList<LaddersSchemaModel>();
	
	public LaddersContextModel(JSONObject contextObj){
	    Name = contextObj.getString("Name");
	    JSONArray contextSchema = contextObj.getJSONArray("Schema");
	    
		for(int j = 0 ; j < contextSchema.length() ; j++){
		    JSONObject fieldObj = contextSchema.getJSONObject(j);
		    LaddersSchemaModel s = new LaddersSchemaModel(fieldObj);
		    Schema.add(s);
		}//for j

	}//LaddersContextModel()
 
	

}//class LaddersContextModel