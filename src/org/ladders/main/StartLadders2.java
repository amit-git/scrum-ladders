package org.ladders.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.ladders.services.BaseHandler;
import org.ladders.services.DeleteHandler;
import org.ladders.services.DeleteLadderHandler;
import org.ladders.services.GetLaddersHandler;
import org.ladders.services.GetRollupsHandler;
import org.ladders.services.GetRowsHandler;
import org.ladders.services.GetSettingHandler;
import org.ladders.services.HtmlFileHandler;
import org.ladders.services.InsertHandler;
import org.ladders.services.SaveLadderSchema;
import org.ladders.services.SaveSettingHandler;
import org.ladders.services.UpdateHandler;
import org.ladders.util.U;

class StartLadders2
{
	private static HashMap<String, BaseHandler>	MAP	= new HashMap<String, BaseHandler>();
	static
	{
		MAP.put("ROWS", new GetRowsHandler());
		MAP.put("STICKIES", new GetRowsHandler());
		
		MAP.get("STICKIES").setOutputType("STICKIES");
		
		MAP.put("DELETE", new DeleteHandler());
		MAP.put("INSERT", new InsertHandler());
		MAP.put("UPDATE", new UpdateHandler());
		MAP.put("SAVESETTING", new SaveSettingHandler());
		MAP.put("GETLADDERS", new GetLaddersHandler());
		MAP.put("GETSETTING", new GetSettingHandler());
		MAP.put("SAVELADDER", new SaveLadderSchema());
		MAP.put("DELETELADDER", new DeleteLadderHandler());

		MAP.put("ROLL", new GetRollupsHandler());

		MAP.put("D", new HtmlFileHandler("STATIC/html/index.html", true));
		//MAP.put("INDEX", new HtmlFileHandler("STATIC/html/menu.html", true));
		MAP.put("SCHEMA", new HtmlFileHandler("STATIC/html/schema.html", true));
		MAP.put("SETUP", new HtmlFileHandler("STATIC/html/setup.html", false));
		MAP.put("ROLLINDEX", new HtmlFileHandler("STATIC/html/rollups.html", true));

		MAP.put("COMMENT", new HtmlFileHandler("STATIC/html/commentEditor/EditComments.html", false));
		//MAP.put("STATIC", new FileReadHandler());

	}

	static void serve(BaseHandler b2, Request baseRequest, HttpServletRequest request, HttpServletResponse response)

	{
		BaseHandler b3;
		try
		{
			b3 = b2.getClone();

			b3.setExchange(new JettyExchange(request, response));
			b3.handle();
			baseRequest.setHandled(true);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "/STATIC/html/landing.html" });
		resource_handler.setResourceBase(U.startPath("."));

		AbstractHandler masterHandler = new AbstractHandler() {

			@Override
			public void handle(String target, Request baseRequest, HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException
			{
				String[] arr = target.split("/");
				String handlerName = arr[1];
				BaseHandler b1 = MAP.get(handlerName);
				if (b1 == null)
				{
					response.getWriter().write("Not supported '" + handlerName + "'. target:" + target);
				} else
				{
					serve(b1, baseRequest, request, response);
				}
			}
		};

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, masterHandler });
		server.setHandler(handlers);

		server.start();
		System.out.println("Server is listening on port 8080");
		server.join();

	}
}//StartLadders2

class JettyExchange implements MyExchangeAbstract
{

	//Request request; 
	private HttpServletRequest	httpRequest;
	private HttpServletResponse	httpResponse;

	JettyExchange(HttpServletRequest sreq, HttpServletResponse sresp)
	{
		httpRequest = sreq;
		httpResponse = sresp;
	}

	@Override
	public String getRequestURI()
	{
		return httpRequest.getRequestURI();
	}

	@Override
	public String getRequestMethod()
	{
		return httpRequest.getMethod();
	}

	@Override
	public Object getAttribute(String name)
	{
		return httpRequest.getAttribute(name);
	}

	@Override
	public InputStream getRequestBody() throws IOException
	{

		return httpRequest.getInputStream();
	}

	@Override
	public void setResponseHeaders(String k, String v)
	{

		httpResponse.setHeader(k, v);
	}

	@Override
	public OutputStream getResponseBody() throws IOException
	{

		return httpResponse.getOutputStream();

	}

	@Override
	public void sendResponseHeaders(int i, int j)
	{

	}

	@Override
	public String getQueryString()
	{
		return httpRequest.getQueryString();
	}

}