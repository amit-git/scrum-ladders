package org.ladders.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.ladders.services.BaseHandler2;
import org.ladders.services.FileReadHandler;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

////Move to Jetty asap.
class StartLadders {

	private static void setupHandler(String path, HttpServer server, HttpHandler h) {
		HttpContext context = server.createContext(path, h);
		context.getFilters().add(new ParameterFilter());
	}

	public static void main(String[] args) throws Exception {
		InetSocketAddress addr = new InetSocketAddress(82);
		HttpServer server = HttpServer.create(addr, 0);


		//Do transactional handlers
		for (BaseHandler2 b : HttpHandlerFactory.SUPPORTED_SERVICES) {
			setupHandler("/"+b.getName()+"/", server, new HttpHandlerFactory());
		}
		
		//Static handler
		setupHandler("/STATIC/", server, new FileReadHandler());

		//Do Setup handler now
		setupHandler("/", server, new HttpHandler() {
			@Override
			public void handle(com.sun.net.httpserver.HttpExchange arg0) throws IOException {
				//U.log("HERE");
				arg0.getResponseHeaders().add("Location", "/STATIC/html/setup.html");
				arg0.sendResponseHeaders(302, -1);
			}
		} );

		server.setExecutor(Executors.newCachedThreadPool(new NewThreadEveryTimeFactory()));
		server.start();
		System.out.println("Server is listening on port 82");
	}
}

//Need my own thread factory. Java's default one has issues with POST parameters
class NewThreadEveryTimeFactory implements ThreadFactory {
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}
}
