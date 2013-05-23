package org.ladders.services;

import org.apache.commons.lang3.StringUtils;
import org.ladders.util.SettingsUtil;

public class SaveSettingHandler extends BaseHandler2 {
 
	@Override
	public boolean isTransactional() {
		return false;
	}

	@Override
	protected void innerHandle() throws Exception {
		if (!inputParams.containsKey("SETTING_NAME")) {
			throw new Exception("No SETTING_NAME defined");
		}
		if (!inputParams.containsKey("SETTING_TEXT")) {
			throw new Exception("No SETTING_TEXT defined");
		}
			
		String name = inputParams.get("SETTING_NAME");
		String text = inputParams.get("SETTING_TEXT");

		if (StringUtils.isEmpty(name)) {
			throw new Exception("No SETTING_NAME defined");
		}
		if (StringUtils.isEmpty(text)) {
			throw new Exception("No SETTING_TEXT defined");
		}

		SettingsUtil.saveSetting(name, text);
		successOut("Saved", "");
	}

	@Override
	public String getName() {
		return "SAVESETTING";
	}

 
	 
 

}
