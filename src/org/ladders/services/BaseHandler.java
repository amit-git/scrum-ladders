package org.ladders.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.io.EofException;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.ladders.main.MyExchangeAbstract;
import org.ladders.util.Cols;
import org.ladders.util.FileUtil;
import org.ladders.util.RegUtil;
import org.ladders.util.TimestampLogger;
import org.ladders.util.U;

public abstract class BaseHandler
{

	private static final String	LADDER_NAME	= "LADDER_NAME";

	public void setExchange(MyExchangeAbstract e)
	{
		exchange = e;
	}

	protected MyExchangeAbstract		exchange;

	private String						ladderName;
	protected TimestampLogger			tsLogger;
	protected HashMap<String, String>	inputParams	= new HashMap<String, String>();
	private ArrayList<String>			inputArr	= new ArrayList<String>();
	protected String					rowType;
	protected String					parentId;

	//private AbstractDataStorage		dao			= null;

	protected boolean isHtml()
	{
		return false;
	}

	protected abstract boolean actionOnLadder();

	protected String getInputFromArr(int i) throws Exception
	{
		assertTrue(inputArr.size() > i, "Need minimum " + (i + 1) + " parameters");
		return inputArr.get(i);
	}

	protected String getLadderName() throws Exception
	{
		if (U.isNullOrBlank(ladderName) && inputParams.containsKey(LADDER_NAME))
		{
			ladderName = inputParams.get(LADDER_NAME);
		}

		assertTrue(!U.isNullOrBlank(ladderName), "Ladder name is empty");

		return ladderName;
	}

	// Template method pattern. Don't let children override this func
	public final void handle()
	{
		try
		{
			String queryUrl = exchange.getRequestURI().toString();
			tsLogger = new TimestampLogger(exchange.getRequestURI().toString());
			String[] urlquery = queryUrl.split("/");

			if (actionOnLadder())
			{

				assertTrue(urlquery.length >= 4, "Transactional service doesn't have a Ladder name and rowType passed.");

				ladderName = urlquery[2];
				rowType = urlquery[3];

				assertTrue(!U.isNullOrBlank(ladderName), "No LADDER identified");
				assertTrue(!U.isNullOrBlank(rowType), "No ROWTYPE identified");
			}

			for (int i = 3; i < urlquery.length; i++)
			{
				String[] pair = splitPair(urlquery[i], ":");
				if (pair.length != 2)
				{
					inputArr.add(urlquery[i]);
					continue;
				}

				String k = pair[0];
				String v = pair[1];

				inputParams.put(k, v);

				// Convenience variables for children
				if (v.contains("_rowType:"))
					rowType = RegUtil.match(v, "_rowType:([0-9a-zA-Z]+)");
				if (v.contains("_parentId:"))
					parentId = RegUtil.match(v, "_parentId:([0-9a-zA-Z-]+)");

			}//for i

			parseGetParameters(inputParams);
			parsePostParameters(inputParams);

			/*
			String andVals = inputParams.get("AND");
			if (!StringUtils.isEmpty(andVals))
			{
				if (U.isNullOrBlank(rowType))
					rowType = RegUtil.match(andVals, "_rowType:([0-9a-zA-Z]+)");

				if (U.isNullOrBlank(parentId))
					parentId = RegUtil.match(andVals, "_parentId:([0-9a-zA-Z-]+)");
			}
			*/

			// U.log("inputParams.containsKey(" + Cols.ROWTYPE + "):" +
			// inputParams.containsKey(Cols.ROWTYPE));
			if (U.isNullOrBlank(rowType) && inputParams.containsKey(Cols.ROWTYPE))
				rowType = inputParams.get(Cols.ROWTYPE);
			if (U.isNullOrBlank(parentId) && inputParams.containsKey(Cols.PARENTID))
				parentId = inputParams.get(Cols.PARENTID);

			inputParams.put(Cols.ROWTYPE, rowType);

			innerHandle();

		} catch (EofException eofe)
		{
		} catch (Exception e)
		{
			errorOut(e.toString());
			U.log(e.toString());
			e.printStackTrace();
		} finally
		{
			// Dump timestamp for each service
			tsLogger.dump(this.getClass().getName());
		}
	}

	protected abstract void innerHandle() throws Exception;

	// ========================

	private void parseGetParameters(HashMap<String, String> params) throws UnsupportedEncodingException
	{
		String query = exchange.getQueryString();
		parseQuery(query, params);
	}

	private void parsePostParameters(HashMap<String, String> params) throws IOException
	{
		if (!"post".equalsIgnoreCase(exchange.getRequestMethod()))
			return;

		Object paramObj = exchange.getAttribute("parameters");
		if (paramObj != null)
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> parameters = (Map<String, Object>) paramObj;
			for (Entry<String, Object> pair : parameters.entrySet())
			{
				Object v = pair.getValue();
				if (v == null)
					v = "";
				params.put(pair.getKey(), v.toString());
			}
		}

		InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		parseQuery(query, params);

	}

	@SuppressWarnings("unchecked")
	private static void parseQuery(String query, HashMap<String, String> parameters)
			throws UnsupportedEncodingException
	{
		if (query == null)
			return;

		String pairs[] = query.split("[&]");

		for (String pair : pairs)
		{
			String param[] = pair.split("[=]");

			String key = null;
			String value = null;
			if (param.length > 0)
			{
				key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
			}

			if (param.length > 1)
			{
				value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
			}

			if (parameters.containsKey(key))
			{
				Object obj = parameters.get(key);
				if (obj instanceof List)
				{
					List values = (List) obj;
					values.add(value);
				} else if (obj instanceof String)
				{
					ArrayList<String> values = new ArrayList<String>();
					values.add((String) obj);
					values.add(value);
					parameters.put(key, "LIST???" + values.toString());
				}
			} else
			{
				parameters.put(key, value);
			}

		}// for pair

	}// parseQuery()

	protected void redirect(String uri) throws IOException
	{
		exchange.setResponseHeaders("Location", uri);
		//exchange.sendResponseHeaders(302, -1);
	}

	protected void errorOut(String msg)
	{
		try
		{
			if (this.isHtml())
			{
				StringBuffer buf = new StringBuffer();
				buf.append("<h1>ERROR</h1><hr/>");
				buf.append("<h2>" + msg + "</h2>");
				buf.append("<hr/>");
				buf.append("<h3>Visit Settings Dashboard: <a href='/SETUP'>SETTINGS</a></h3>");
				writeAndClose(buf.toString());
			} else
			{
				JSONWriter myString = new JSONStringer();
				myString.object();
				myString.key("Status").value("ERROR");
				myString.key("Message").value(msg);
				myString.endObject();

				writeAndClose(myString.toString());
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isString(String data)
	{
		if (U.isNullOrBlank(data))
		{
			return false;
		}
		data = data.trim();
		return (!data.startsWith("[") && !data.startsWith("{"));
	}

	protected void successOut(String msg, String data)
	{
		try
		{

			JSONWriter myString = new JSONStringer();
			myString.object();
			myString.key("Status").value("SUCCESS");
			myString.key("Message").value(msg);

			if (isString(data))
			{
				myString.key("Data").value(data);
			} else
			{
				myString.key("Data").value("VAL_PLACEHOLDER"); // strange hack
																// to
																// reuse
																// JSONStringer.
																// Feeling lazy.
			}
			myString.endObject();

			if (U.isNullOrBlank(data))
			{
				data = "null";
			}

			writeAndClose(myString.toString().replace("\"VAL_PLACEHOLDER\"", data));

			// writeAndClose("{Status:'SUCCESS', Message:'" + msg + "', Data:" +
			// data + "}");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	protected void writeAndClose(String out) throws IOException
	{
		exchange.setResponseHeaders("Content-Type", "text/html");
		exchange.sendResponseHeaders(200, 0);
		OutputStream responseBody = exchange.getResponseBody();

		try
		{
			responseBody.write(out.getBytes());
		} catch (EofException eof)
		{
			U.log("EOF error: " + eof.getMessage());
		}

		responseBody.close();

	}

	protected static String[] splitPair(String q, String separator)
	{
		q = q.trim();
		if (q.length() == 0)
			return new String[] {};
		int colon = q.indexOf(separator);
		if (colon <= 0)
			return new String[] {};

		String k = URLDecoder.decode(q.substring(0, colon));
		String v = URLDecoder.decode(q.substring(colon + 1));

		return new String[] { k, v };
	}

	protected static String fetchWidgets(String indexHtml) throws IOException
	{

		ArrayList<File> paths = new ArrayList<File>();
		File file = new File(U.startPath("STATIC\\WIDGET"));
		FileUtil.readAllChildren(file, paths);

		StringBuffer buf = new StringBuffer();
		for (File path : paths)
		{
			if (!path.getName().endsWith(".js"))
				continue;

			String txt = "" + FileUtil.readTextFromTile(path.toString());
			buf.append(txt).append("\n//==================\n");
			// U.log("path:"+path + "  txt:"+txt.length());
		}
		indexHtml = indexHtml.replace("<!--WIDGETS-->", buf.toString());
		return indexHtml;
	}

	protected static String fetchStaticFiles(String indexHtml) throws IOException
	{

		ArrayList<String> arr = RegUtil.matchAll(indexHtml, "<!--(/STATIC/.+?\\.[jcs]+)-->", 1);
		for (String path : arr)
		{
			// U.log("path:"+path );
			String txt = "" + FileUtil.readTextFromTile(U.startPath(path));
			// U.log("path:"+path + "  txt:"+txt.length());
			indexHtml = indexHtml.replace("<!--" + path + "-->", txt);
		}
		// indexHtml = indexHtml.replace("<!--FRAMEWORK-->", frameworkJS);
		return indexHtml;

	}

	protected void assertTrue(boolean f, String msg) throws Exception
	{
		if (f)
			return;
		String errMsg = "";
		errMsg += "\n Error:" + msg;
		errMsg += "\n" + this.getClass().getName();
		errMsg += "\n inputArr:" + inputArr;
		errMsg += "\n ladderName:" + ladderName;
		errMsg += "\n RequestURI:" + this.exchange.getRequestURI();

		throw new Exception(errMsg);
	}

	public BaseHandler getClone() throws Exception
	{
		return (BaseHandler) this.getClass().getDeclaredConstructors()[0].newInstance(null);
	}
}
