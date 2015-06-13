package org.ladders.services;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.ladders.db.LadderFactory;
import org.ladders.util.Cols;
import org.ladders.util.FileUtil;
import org.ladders.util.JsonUtil;
import org.ladders.util.SettingsUtil;
import org.ladders.util.U;

public class HtmlFileHandler extends BaseHandler
{

	private String	path			= null;
	private boolean	actionOnLadder	= false;

	public HtmlFileHandler(String path, boolean actionOnLadder)
	{
		this.path = path;
		this.actionOnLadder = actionOnLadder;
	}

	@Override
	protected boolean isHtml()
	{
		return true;
	}

	public BaseHandler getClone() throws Exception
	{
		return new HtmlFileHandler(path, actionOnLadder);
	}

	@Override
	protected void innerHandle() throws Exception
	{
		//if (actionOnLadder)
		//	assertTrue(!U.isNullOrBlank(rowType), "rowType can't be NULL or EMPTY:" + this.path);

		String indexHtml = FileUtil.readTextFromTile(U.startPath(path));
		// fetch static files & vars
		indexHtml = fetchGlobals(indexHtml);
		indexHtml = fetchStaticFiles(indexHtml);
		indexHtml = fetchWidgets(indexHtml);
		writeAndClose(indexHtml);
	}

	private String fetchGlobals(String indexHtml) throws UnknownHostException, Exception
	{

		StringBuffer buf = new StringBuffer();

		if (!U.isNullOrBlank(rowType))
			buf.append(" ENV.RowType = '" + this.rowType + "';");

		if (actionOnLadder)
		{
			String lName = getLadderName();
			String schemajs = LadderFactory.getSchema(lName);
			;
			buf.append(" ENV.LadderName = '" + lName + "';");
			buf.append("\n\n ENV.LadderSchema = " + schemajs + ";\n\n");
		}

		if (!U.isNullOrBlank(parentId))
			buf.append(" ENV.ParentId = '" + parentId + "';");

		buf.append("ENV.InputParams = {");
		for (Entry<String, String> pair : inputParams.entrySet())
		{
			buf.append("\"" + pair.getKey() + "\":'" + pair.getValue() + "',");
		}
		buf.append("}; \n ");

		//Saved filters
		if (actionOnLadder)
		{
			buf.append("\n ENV.SavedFilters = " + addSavedFilters());
			buf.append("; \n ");
		}

		return indexHtml.replace("<!--FRAMEWORK-->", buf);
	}// fetchGlobals()

	private String addSavedFilters() throws Exception
	{

		String filtersKey = "LAST_QUERY_STRING_" + this.getLadderName();
		//filtersKey = filtersKey.replaceAll("[^a-zA-Z]+", "_");

		String savedFiltersJson = SettingsUtil.getSetting(filtersKey);
		HashMap<String, String> map = JsonUtil.json2Map(savedFiltersJson);
		if (map == null)
			map = new HashMap<String, String>();

		String uri = exchange.getRequestURI();
		if (!uri.contains(Cols.ROWID) && !uri.contains(Cols.PARENTID)) //Don't save row specific urls
		{
			String type = path.contains("index.html") ? "_ROWS" : "_ROLLUPS";
			map.put(this.rowType + type, uri);
		}

		String returnJson = JsonUtil.map2Json(map);

		SettingsUtil.saveSetting(filtersKey, returnJson);

		return returnJson;
	}

	@Override
	protected boolean actionOnLadder()
	{
		return actionOnLadder;
	}

}
