package org.ladders.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LaddersTopologyModel
{
	private List<LaddersContextModel>	contexts	= new ArrayList<LaddersContextModel>();
	private String						jsonSchema	= null;

	public String getJsonSchema()
	{
		return jsonSchema;
	}

	public LaddersTopologyModel(String schema)
	{
		this.jsonSchema = schema;

		JSONArray arrRowTypes = new JSONArray(schema);
		//List<String> list = new ArrayList<String>();
		for (int i = 0; i < arrRowTypes.length(); i++)
		{
			JSONObject contextObj = arrRowTypes.getJSONObject(i);
			LaddersContextModel c = new LaddersContextModel(contextObj);
			contexts.add(c);
		}//for i

	}//LaddersTopologyModel()

	/*
	public StringBuffer getJson1(){
		StringBuffer buf = new StringBuffer();
		buf.append("[\n\n");
		for (LaddersContextModel lcm : contexts){
			if (buf.length()==1){
				buf.append(",\n");
			}
			//buf.append(lcm.getJson());
		}
		buf.append("\n\n]");
		return buf;
	}
	*/
	public List<LaddersContextModel> getContexts()
	{
		return contexts;
	}

	public boolean contains(String ladderName)
	{
		for (LaddersContextModel lcm : contexts){
			if (lcm.getName().equals(ladderName)){
				return true;
			}
		}
		return false;
	}

}//class LaddersTopologyModel