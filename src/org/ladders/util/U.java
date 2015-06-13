package org.ladders.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.ladders.db.MyRecord;

/*
 */
public class U
{

	public static final int	MAX_ROWS	= 200;

	public static String logPath(String file)
	{
		final String ROOT_PATH = System.getProperty("user.dir") + File.separatorChar + ".." + File.separatorChar + "logs" + File.separatorChar;
		return ROOT_PATH + file;
	}

	public static boolean isNullOrBlank(String param)
	{
		return param == null || param.trim().length() == 0;
	}

	public static String startPath(String file)
	{
		final String ROOT_PATH = System.getProperty("user.dir") + File.separatorChar;
		file = file.replace('/', File.separatorChar);
		file = file.replace('\\', File.separatorChar);
		return ROOT_PATH + file;
	}

	public static void redirect(InputStream is, OutputStream os) throws IOException
	{
		final int BUFFER = 2048;

		int count;
		byte data[] = new byte[BUFFER];
		while ((count = is.read(data, 0, BUFFER)) != -1)
		{
			os.write(data, 0, count);
		}

		os.flush();
		os.close();
		is.close();
	}

	public static void log(ArrayList<MyRecord> list)
	{
		log("MyRecord list len:" + list.size());
		int i = 1;
		for (MyRecord rec : list)
		{
			System.out.print(i++ + ") ");
			log(rec);
		}

	}

	public static void log(Object o)
	{
		if (o != null && o instanceof MyRecord)
		{
			try
			{
				o = JsonUtil.toJsonFromRaw(((MyRecord) o));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(o);
	}

	public static double tryParse(Object o)
	{
		try
		{
			return Double.parseDouble(o.toString());
		} catch (Exception ex)
		{
			return 0;
		}
	}

	public static boolean isNumber(Object str) {
	    try {
	        Double.parseDouble(""+str);
	        return true;
	    } catch (Exception e){}
	    return false;
	}

	public static void printCaller(String context)
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();

		int len = elements.length - 1;
		U.log("===Caller " + context + "===");
		String tabs = "";
		for (int i = 2; i < len; i++)
		{

			StackTraceElement e = elements[i];
			String fname = e.getFileName();
			if (U.isNullOrBlank(fname))
				continue;
			U.log(tabs + fname.replaceAll(".java", "") + "." + e.getMethodName());
			tabs += "  ";
		}
	}

	public static void sleep(int i)
	{
		try
		{
			Thread.sleep(i);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
