package org.ladders.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.json.JSONStringer;
import org.json.JSONWriter;

import com.mongodb.BasicDBObject;

public class JsonUtil {
	private static HashSet<String> dontSendList = new HashSet<>();
	static {
		dontSendList.add("_createdDate");
		dontSendList.add("_updateDate");
		dontSendList.add("_id");
	}

	public static StringBuffer toJson(ArrayList<BasicDBObject> rows) {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for (BasicDBObject row : rows) {
			if (buf.length() > 1)
				buf.append(",");
			buf.append(toJson(row).toString());
		}
		buf.append("]");
		return buf;
	}


	public static JSONWriter toJson(BasicDBObject row) {
		JSONWriter myString = new JSONStringer();
		myString.object();
		for (Entry<String, Object> pair : row.entrySet()) {
			String k = pair.getKey();
			// U.log("k:"+k);
			if (dontSendList.contains(k))
				continue;

			if (k.startsWith(Cols.ROLLUP_PREFIX)) {
				myString.key(k).value(convertToRollup(pair.getValue()));
			} else {
				myString.key(k).value(pair.getValue());
			}
		}
		myString.endObject();

		// U.log("ROW:"+myString);

		return myString;
	}

	private static HashMap<String, Double> convertToRollup(Object mapObj) {
		HashMap<String, Double> retMap = new HashMap<>();
		HashMap<String, String> rollupMap = (HashMap<String, String>) mapObj;
		for (Entry<String, String> pair : rollupMap.entrySet()) {
			String v = pair.getValue();
			// i don't care about ids. Just values.
			if (!retMap.containsKey(v)) {
				retMap.put(v, 1.0);
			} else {
				retMap.put(v, retMap.get(v) + 1);
			}
		}
		return retMap;
	}
}
