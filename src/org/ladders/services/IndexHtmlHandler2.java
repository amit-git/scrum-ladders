package org.ladders.services;

import org.ladders.util.FileUtil;
import org.ladders.util.U;

public class IndexHtmlHandler2 extends BaseHandler2 {

	@Override
	protected boolean isHtml() {
		return true;
	}
	
	@Override
	protected void innerHandle() throws Exception {
		if (rowType==null || rowType.trim().length()==0) throw new Exception("rowType can't be NULL or EMPTY:"+rowType);
		
		String indexHtml = FileUtil.readTextFromTile(U.startPath("STATIC/html/index.html"));

		// fetch static files & vars
		indexHtml = fetchGlobals(indexHtml);
		indexHtml = fetchStaticFiles(indexHtml);
		indexHtml = fetchWidgets(indexHtml);
		writeAndClose(indexHtml);
	}

	@Override
	public String getName() {
		return "D";
	}





}
