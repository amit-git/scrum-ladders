package org.ladders.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.ladders.util.FileUtil;
import org.ladders.util.RegUtil;
import org.ladders.util.U;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileReadHandler implements HttpHandler {


	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String queryUrl = exchange.getRequestURI().toString();

		File f = new File(U.startPath(queryUrl));
		U.log("Read static: " + f);
		if (!f.exists()) {
			String response = "404 (Not Found)\n";
			exchange.sendResponseHeaders(404, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
			return;
		}

		String absPath = f.getAbsolutePath();
		Headers responseHeaders = exchange.getResponseHeaders();
		if (absPath.endsWith(".png")) {
			handleBinary("img/png", responseHeaders, exchange, f);
		} else if (absPath.endsWith(".gif")) {
			handleBinary("img/gif", responseHeaders, exchange, f);
		} else if (absPath.endsWith(".jpg")) {
			handleBinary("img/jpg", responseHeaders, exchange, f);
		} else if (absPath.endsWith(".js")) {
			handleBinary("text/html", responseHeaders, exchange, f);
		} else if (absPath.endsWith(".html") || absPath.endsWith(".txt")) {
			handleHtml(responseHeaders, exchange, f);
		}

	}

	private void handleBinary(String type, Headers responseHeaders, HttpExchange exchange, File f) throws IOException {
		responseHeaders.set("Content-Type", type);
		exchange.sendResponseHeaders(200, 0);
		OutputStream responseBody = exchange.getResponseBody();
		FileInputStream fstream = new FileInputStream(f);
		U.redirect(fstream, responseBody);
	}

	private void handleHtml(Headers responseHeaders, HttpExchange exchange, File f) throws IOException {
		responseHeaders.set("Content-Type", "text/html");
		String indexHtml = FileUtil.readTextFromTile(f.getAbsolutePath());
		indexHtml = BaseHandler2.fetchStaticFiles(indexHtml);
		indexHtml = BaseHandler2.fetchWidgets(indexHtml);
		exchange.sendResponseHeaders(200, 0);
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(indexHtml.getBytes());
		responseBody.close();
		return;

	}

}