package org.ladders.util;

import java.io.File;
import java.io.IOException;

import org.ladders.db.LadderFactory;

public class SettingsUtil {

	public static String getSetting(String name) throws IOException{
		String settingsFile = U.startPath("/STATIC/SETTINGS/"+name+".txt");

		if (!new File(settingsFile).exists()){
			return null;
		}
		String txt = FileUtil.readTextFromTile(settingsFile);
		if (txt!=null) txt = txt.trim();
		return txt;
	}
	
	public static void saveSetting(String name, String text) throws Exception{
		String settingsFile = U.startPath("/STATIC/SETTINGS/"+name+".txt");
		FileUtil.writeToFile(settingsFile, text.trim());

		//LadderFactory.reset();
		
	}
	public static void deleteSetting(String name ) throws Exception{
		String settingsFile = U.startPath("/STATIC/SETTINGS/"+name+".txt");
		if (!new File(settingsFile).exists()){
			return;
		}
		
		new File(settingsFile).delete();
		//LadderFactory.reset();
	}	
}
