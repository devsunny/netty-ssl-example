package com.asksunny.jetty;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.asksunny.rest.MyRestService;

public class RESTfulHandler extends AbstractHandler {

	
	
	ConcurrentHashMap<String, Object> services = new ConcurrentHashMap<>();
	
	public RESTfulHandler()
	{
		
	}
	
		
	
	
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Welcome to Demo world - your are request:" + target +"</h1>");

	}

}
