package org.ladders.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/*
*/
public class U {

	public static final int MAX_ROWS = 200;

	public static String logPath(String file) {
		final String ROOT_PATH = System.getProperty("user.dir") + File.separatorChar + ".." + File.separatorChar
				+ "logs" + File.separatorChar;
		return ROOT_PATH + file;
	}

	public static String startPath(String file) {
		final String ROOT_PATH = System.getProperty("user.dir") + File.separatorChar;
		file =  file.replace( '/', File.separatorChar);
		file =  file.replace( '\\', File.separatorChar);
		return ROOT_PATH + file;
	}

	public static void redirect(InputStream is, OutputStream os) throws IOException {
		final int BUFFER = 2048;

		int count;
		byte data[] = new byte[BUFFER];
		while ((count = is.read(data, 0, BUFFER)) != -1) {
			os.write(data, 0, count);
		}

		os.flush();
		os.close();
		is.close();
	}

	public static void log(Object o) {
		System.out.println(o);
	}

	public static double tryParse(Object o) {
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception ex) {
			return 0;
		}
	}

}
