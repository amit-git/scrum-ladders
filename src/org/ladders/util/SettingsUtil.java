package org.ladders.util;

import java.io.IOException;

import org.ladders.db.DataStorage;

public class SettingsUtil {

	public static String getSetting(String name) throws IOException{
		String settingsFile = U.startPath("/STATIC/SETTINGS/"+name+".txt");
		String txt = FileUtil.readTextFromTile(settingsFile);
		if (txt!=null) txt = txt.trim();
		return txt;
	}
	
	public static void saveSetting(String name, String text) throws Exception{
		String settingsFile = U.startPath("/STATIC/SETTINGS/"+name+".txt");
		FileUtil.writeToFile(settingsFile, text.trim());

		DataStorage.reset();		
		
	}
	
}
