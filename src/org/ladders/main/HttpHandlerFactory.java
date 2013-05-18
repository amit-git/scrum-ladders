package org.ladders.main;

import java.io.IOException;

import org.ladders.services.BaseHandler2;
import org.ladders.services.CreateNewLadder;
import org.ladders.services.DeleteHandler;
import org.ladders.services.GetLaddersHandler;
import org.ladders.services.GetRowsHandler;
import org.ladders.services.GetSettingHandler;
import org.ladders.services.IndexHtmlHandler2;
import org.ladders.services.InsertHandler;
import org.ladders.services.SaveSettingHandler;
import org.ladders.services.UpdateHandler;

import com.sun.net.httpserver.HttpHandler;

public class HttpHandlerFactory implements HttpHandler {

	public static final BaseHandler2[] SUPPORTED_SERVICES = { new GetRowsHandler(), new IndexHtmlHandler2(),
			new DeleteHandler(), new InsertHandler(), new UpdateHandler(), new SaveSettingHandler(),
			new GetLaddersHandler(), new GetSettingHandler(), new CreateNewLadder() };

	@Override
	public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
		try {
			String queryUrl = exchange.getRequestURI().toString();
			for (BaseHandler2 b : SUPPORTED_SERVICES) {
				if (queryUrl.contains("/" + b.getName() + "/")) {
					b.setExchange(exchange);
					b.handle();
					break;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}