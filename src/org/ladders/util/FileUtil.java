package org.ladders.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileUtil {

	public static void appendToFile(String path, Object diff) throws IOException {

		FileWriter fWriter = null;
		BufferedWriter writer = null;
		try {
			fWriter = new FileWriter(path, true);
			writer = new BufferedWriter(fWriter);
			writer.append("" + diff);
			writer.newLine();
			writer.close();
		} catch (Exception e) {
		}
	}
	
	
	public static void writeToFile(String path, Object diff) throws IOException {

		FileWriter fWriter = null;
		BufferedWriter writer = null;
		try {
			fWriter = new FileWriter(path);
			writer = new BufferedWriter(fWriter);
			writer.append("" + diff);
			writer.newLine();
			writer.close();
		} catch (Exception e) {
		}
	}

	public static final void readAllChildren(final File f, ArrayList<File> paths) throws IOException {
		if (f.isDirectory()) {
			final File[] childs = f.listFiles();
			for (File child : childs) {
				readAllChildren(child, paths);
			}
			return;
		} else {
			paths.add(f);
		}
	}

	public static String readTextFromTile(String path) throws IOException {

		try {

			StringBuffer list = new StringBuffer();
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(path);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// strLine = strLine.trim();
				// if (strLine.length() == 0)
				// continue;

				// Print the content on the console
				list.append(strLine).append("\n");
			}
			// Close the input stream
			in.close();

			return list.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			//U.log("File not found:"+ex);
		}
		return null;
	}

}
