package org.ladders.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MyExchangeAbstract
{
	public String getRequestURI();

	public String getRequestMethod();

	public Object getAttribute(String string);

	public InputStream getRequestBody() throws IOException;

	public void setResponseHeaders(String string, String string2);

	public OutputStream getResponseBody() throws IOException;

	public void sendResponseHeaders(int i, int j);

	public String getQueryString();
	

}
