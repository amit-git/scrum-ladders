package org.ladders.services;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.ladders.util.FileUtil;
import org.ladders.util.SettingsUtil;
import org.ladders.util.U;

public class GetSettingHandler extends BaseHandler2 {
 
	@Override
	public boolean isTransactional() {
		return false;
	}

	@Override
	protected void innerHandle() throws Exception {
		if (!inputParams.containsKey("SETTING_NAME")) {
			throw new Exception("No SETTING_NAME defined");
		}
		String name = inputParams.get("SETTING_NAME");
		if (StringUtils.isEmpty(name)) {
			throw new Exception("No SETTING_NAME defined");
		}
		String txt = SettingsUtil.getSetting(name);
		successOut(name, txt);
	}

	@Override
	public String getName() {
		return "GETSETTING";
	}

 
	 
 

}
