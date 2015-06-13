package org.ladders.main;

import java.io.IOException;

import org.ladders.services.*;

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
					//There is a bug in the default HTTP implementation. Working around it by creating new instances
					BaseHandler2 b2 =  (BaseHandler2)b.getClass().getDeclaredConstructors()[0].newInstance(null);
					b2.setExchange(exchange);
					b2.handle();
					break;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}