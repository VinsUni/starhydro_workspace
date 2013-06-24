package star.localserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ProxyHandler extends AbstractHandler
{
	class StarHandlerWrapper
	{
		String prefix;
		StarHandler handler;

		public StarHandlerWrapper(String prefix2, StarHandler handler2)
		{
			this.prefix = prefix2;
			this.handler = handler2;
		}

		@SuppressWarnings("unchecked")
		boolean handle(HttpServletRequest request)
		{
			boolean ret = false;
			String pathinfo = request.getPathInfo();
			if (pathinfo.startsWith(prefix))
			{
				ret = handler.handle(pathinfo, (Map<String, String[]>) request.getParameterMap());
			}
			return ret;
		}

	}

	Map<String,HttpServlet> servlets = new TreeMap<String,HttpServlet>();
	ArrayList<StarHandlerWrapper> handlers = new ArrayList<ProxyHandler.StarHandlerWrapper>();

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		boolean handled = false;
		for( Entry<String, HttpServlet> entry : servlets.entrySet() )
		{
			if( entry.getKey().equals(target))
			{
				entry.getValue().service(request, response);
				handled = true ;
				return ;
			}
		}
		for (StarHandlerWrapper w : handlers)
		{
			handled |= w.handle(request);
		}
		if (handled)
		{
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			response.getWriter().println("Processed: " + request.getPathInfo());
		}
		else
		{
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			baseRequest.setHandled(true);
			response.getWriter().println("Failed processing " + request.getPathInfo() + " visit: <a href='http://web.mit.edu/star'>STAR site for more information</a>");

		}
	}

	public void addServlet( String prefix , HttpServlet servlet )
	{
		servlets.put( prefix, servlet );
	}
	public void addApplicationHandler(String prefix, StarHandler handler)
	{
		handlers.add(new StarHandlerWrapper(prefix, handler));
	}

}
