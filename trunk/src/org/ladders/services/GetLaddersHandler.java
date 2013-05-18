package org.ladders.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.ladders.db.DataStorage;
import org.ladders.util.SettingsUtil;

public class GetLaddersHandler extends BaseHandler2 {

	@Override
	protected void innerHandle() throws Exception {

		List<String> ladders = DataStorage.getAllLadders();

		JSONWriter map = new JSONStringer();
		map.object();

		for (String s : ladders) {
			String js = SettingsUtil.getSetting("SCHEMA_"+s);
			if (!StringUtils.isEmpty(js)){
				map.key(s).value(js);
			}
		}
		map.endObject();

		successOut("Got Ladders", map.toString());

	}

	@Override
	public String getName() {
		return "GETLADDERS";
	}

	@Override
	public boolean isTransactional() {
		return false;
	}

}
