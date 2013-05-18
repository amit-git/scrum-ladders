package org.ladders.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LaddersContextModel
{
	private String name;
	private List<LaddersSchemaModel>	schema = new ArrayList<LaddersSchemaModel>();
	
	public String getName(){
		return name;
	}
	public List<LaddersSchemaModel> getSchema(){
		return schema;
	}
	public LaddersContextModel(JSONObject contextObj){
	    name = contextObj.getString("Name");
	    JSONArray contextSchema = contextObj.getJSONArray("Schema");
	    
		for(int j = 0 ; j < contextSchema.length() ; j++){
		    JSONObject fieldObj = contextSchema.getJSONObject(j);
		    LaddersSchemaModel s = new LaddersSchemaModel(fieldObj);
		    schema.add(s);
		}//for j

	}//LaddersContextModel()
 
	

}//class LaddersContextModel