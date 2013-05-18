package org.ladders.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LaddersTopologyModel{
	public List<LaddersContextModel> Contexts = new ArrayList<LaddersContextModel>();
	
	public LaddersTopologyModel(String schema){
		JSONArray arrRowTypes = new JSONArray(schema);
		//List<String> list = new ArrayList<String>();
		for(int i = 0 ; i < arrRowTypes.length() ; i++){
		    JSONObject contextObj = arrRowTypes.getJSONObject(i);
		    LaddersContextModel c = new LaddersContextModel(contextObj);
		    Contexts.add(c);
		}//for i
		
	}//LaddersTopologyModel()
	
	public StringBuffer getJson(){
		StringBuffer buf = new StringBuffer();
		buf.append("[\n\n");
		for (LaddersContextModel lcm : Contexts){
			if (buf.length()==1){
				buf.append(",\n");
			}
			//buf.append(lcm.getJson());
		}
		buf.append("\n\n]");
		return buf;
	}
	
}//class LaddersTopologyModel