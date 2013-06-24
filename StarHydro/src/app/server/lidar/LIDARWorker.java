package app.server.lidar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.bzip2.CBZip2InputStream;

import star.localserver.ProxyHandler;
import star.localserver.StarHandler;
import star.localserver.StarServer;
import utils.FileUtils;
import app.server.worker.JNLPPersist;

public class LIDARWorker
{

	public static class LIDARMap implements Comparable<LIDARMap>, Serializable
	{
		private static final long serialVersionUID = 1L;
		private String archive;
		private String metadata;
		private String strValue;
		private float[] coords;

		public LIDARMap(String archiveName, String nextLine)
		{
			this.archive = archiveName.trim();
			this.metadata = nextLine.trim();
			strValue = metadata + " " + archive;
			parseMetadata();
		}

		private void parseMetadata()
		{
			if (metadata != null)
			{
				String[] components = metadata.split(" ");
				if (components.length == 7)
				{
					strValue = MessageFormat.format("{0} {1}", components[3], components[4]);
					coords = parse(components, 3, 4);
				}
			}
		}

		private float[] parse(String[] components, int from, int len)
		{
			float[] ret = new float[len];
			for (int i = 0; i < len; i++)
			{
				String str = components[i + from];
				float value = Float.NaN;
				for (int j = 0; j < str.length(); j++)
				{
					char c = str.charAt(j);
					if (c == ':')
					{
						value = Float.parseFloat(str.substring(j + 1));
						break;
					}
				}
				ret[i] = value;
			}
			return ret;
		}

		public float[] getCoords()
		{
			return coords;
		}

		@Override
		public String toString()
		{
			return strValue;
		}

		public String getArchive()
		{
			return archive;
		}

		public int compareTo(LIDARMap o)
		{
			return this.toString().compareTo(o.toString());
		}
	};

	String URLPrefix = "http://starapp-dev.mit.edu/LIDAR/";
	String IndexFile = "index.txt";

	StarServer starServer;

	public LIDARWorker(final StarHandler starHandler)
	{
		try
		{
			ProxyHandler handler = new ProxyHandler();
			starServer = new StarServer(handler);

			handler.addServlet("/StarHydro/Map", new HttpServlet()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
				{
					resp.getOutputStream().write(getHTML().getBytes());
					resp.getOutputStream().flush();
				}
			});
			handler.addServlet("/StarHydro/IsUp", new HttpServlet()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
				{
					resp.setContentType("text/javascript");
					String output = "StarHydroIsUp();";
					resp.getOutputStream().write(output.getBytes());
					resp.getOutputStream().flush();
				}
			});
			handler.addServlet("/StarHydro/Open", new HttpServlet()
			{
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				@Override
				protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
				{
					starHandler.handle("/StarHydro/Open", (Map<String, String[]>) req.getParameterMap());
					resp.setContentType("text/html");
					String output = "<html><body>StarHydro opened a new map. You can close this window.</body></html>";
					resp.getOutputStream().write(output.getBytes());
					resp.getOutputStream().flush();
				}
			});
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public Set<LIDARMap> list()
	{
		TreeSet<LIDARMap> maps = new TreeSet<LIDARMap>();
		try
		{
			URL url = new URL(MessageFormat.format("{0}{1}", URLPrefix, IndexFile));
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = r.readLine()) != null)
			{
				if (line.startsWith("Archive:"))
				{
					String archiveName = line.substring("Archive:".length() + 1).trim();
					String nextLine = r.readLine();
					LIDARMap map = new LIDARMap(archiveName, nextLine);
					maps.add(map);
				}
				else
				{
					throw new IOException("Invalid format");
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return maps;
	}

	public void load(LIDARMap map)
	{
		try
		{
			URL url = new URL(URLPrefix + map.archive);
			ZipInputStream zip = new ZipInputStream(url.openStream());
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null)
			{
				System.out.println("Checking " + entry.getName());
				if (entry.getName().endsWith(".bz2"))
				{
					String filename = getName(entry.getName());
					zip.read();
					zip.read();
					CBZip2InputStream is = new CBZip2InputStream(zip);
					File workspace = JNLPPersist.getWorkspace();
					File output = new File(workspace, MessageFormat.format("LIDAR_{0}", filename));
					FileOutputStream os = new FileOutputStream(output);
					FileUtils.copy(is, os);
				}
				zip.closeEntry();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private String getName(String name)
	{
		String ret = name;
		if (ret.endsWith(".bz2"))
		{
			ret = ret.substring(0, ret.length() - 4);
		}
		if (ret.indexOf('/') >= 0)
		{
			ret = ret.substring(ret.lastIndexOf('/') + 1);
		}
		return ret;
	}

	public String getHTML() throws IOException
	{
		StringBuffer sb = new StringBuffer();
		String template = "if(true) '{'var rectangle = new google.maps.Rectangle();\n"//
		        + "var sw = new google.maps.LatLng({0},{1});\n"//
		        + "var ne = new google.maps.LatLng({2},{3});\n"//
		        + "var rbounds = new google.maps.LatLngBounds(sw,ne);\n"//
		        + "var rectOptions = '{'strokeColor: \"#FF0000\",strokeOpacity: 0.8,strokeWeight: 2,fillColor: \"#FF0000\",fillOpacity: 0.35,map: map,bounds: rbounds'}';\n"//
		        + "rectangle.setOptions(rectOptions);\n"//
		        + "google.maps.event.addListener(rectangle, \"click\", function(e) '{'\n"//
		        + " var sw = new google.maps.LatLng({0},{1});" + "var ne = new google.maps.LatLng({2},{3});"//
		        + " var rbounds = new google.maps.LatLngBounds(sw,ne);"//
		        + " openMap(\"http://127.0.0.1:{5,number,#####}/StarHydro/Open?archive={4}\");'}'" //  
		        + ");\n'}'\n";

		//sb.append(new String(FileUtils.getStreamToByteArray(this.getClass().getResourceAsStream("/resources/html/prefix.html"))));
		Set<LIDARMap> maps = list();
		for (LIDARMap m : maps)
		{
			float[] coords = m.getCoords();
			sb.append(MessageFormat.format(template, coords[1], coords[0], coords[3], coords[2], m.archive, starServer.getPort()));
		}
		//sb.append(new String(FileUtils.getStreamToByteArray(this.getClass().getResourceAsStream("/resources/html/suffix.html"))));

		return sb.toString();
	}

}
