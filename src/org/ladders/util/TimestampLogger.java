package org.ladders.util;

import java.io.IOException;
import java.util.Calendar;


public class TimestampLogger {

	StringBuffer buf = new StringBuffer();
	long st = Calendar.getInstance().getTimeInMillis();

	public TimestampLogger(String query) {
		buf.append(query).append(":\t");
	}

	public void log(String msg) {
		
		long end = Calendar.getInstance().getTimeInMillis();
		
		//U.log("TLOGGER: "+msg + ":" + (end - st));

		buf.append(msg + ":" + (end - st)).append("\t");
		st = end;
		
	}

	public void dump(String name) {
		try {
			log("Last");
			buf.append("\n");
			FileUtil.appendToFile(U.logPath(name + ".log"), buf.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
