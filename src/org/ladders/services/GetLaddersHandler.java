package org.ladders.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.ladders.db.LadderFactory;

public class GetLaddersHandler extends BaseHandler {

	@Override
	protected void innerHandle() throws Exception {

		List<String> ladders = LadderFactory.getAll();

		JSONWriter map = new JSONStringer();
		map.object();

		for (String s : ladders) {
			String js = LadderFactory.getSchema(s);
			if (!StringUtils.isEmpty(js)){
				map.key(s).value(js);
			}
		}
		map.endObject();

		successOut("Got Ladders", map.toString());

	}
 

	@Override
	public boolean actionOnLadder() {
		return false;
	}

}
