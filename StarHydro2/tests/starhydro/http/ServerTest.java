package starhydro.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Runner;

public class ServerTest
{
	Server s;

	@Before
	public void setUp()
	{
		s = new Server();
	}

	@After
	public void tearDown()
	{
		s = null;
	}

	boolean connect()
	{
		try
		{
			URL url = new URL("http://127.0.0.1:7367/");
			URLConnection c = url.openConnection();
			c.setConnectTimeout(250);
			c.getInputStream().close();
		}
		catch( ConnectException ex )
		{
			return false;
		}
		catch( Throwable t )
		{
			t.printStackTrace();
			return false;
		}
		return true;
	}

	boolean query(double x, double y)
	{
		try
		{
			URL url = new URL("http://127.0.0.1:7367/open/" + x + "/" + y);
			URLConnection c = url.openConnection();
			c.setConnectTimeout(250);
			c.connect();
			InputStream is = c.getInputStream();
			is.close();
		}
		catch( ConnectException ex )
		{
			return false;
		}
		catch( Throwable t )
		{
			t.printStackTrace();
			return false;
		}
		return true;

	}

	boolean invalidQuery()
	{
		try
		{
			URL url = new URL("http://127.0.0.1:7367/test");
			URLConnection c = url.openConnection();
			c.setConnectTimeout(250);
			c.connect();
			InputStream is = c.getInputStream();
			is.close();
		}
		catch( ConnectException ex )
		{
			return false;
		}
		catch( Throwable t )
		{
			t.printStackTrace();
			return false;
		}
		return true;

	}

	@Test
	public void startAndStopTest()
	{
		assertTrue(s.init());
		assertTrue(Double.isNaN(s.lat()));
		assertTrue(Double.isNaN(s.lng()));
		Runner.sleep(200);
		assertTrue(s.close());
		assertTrue(!connect());
	}

	@Test
	public void queryTest()
	{
		double x = 42.70867781741311;
		double y = -70.59814453125;
		assertTrue(s.init());
		assertTrue(Double.isNaN(s.lat()));
		assertTrue(Double.isNaN(s.lng()));
		assertTrue(query(x, y));
		assertTrue(!Double.isNaN(s.lat()));
		assertTrue(!Double.isNaN(s.lng()));
		assertEquals(s.lat(), x);
		assertEquals(s.lng(), y);
		assertTrue(s.close());
		assertTrue(!connect());
	}

	@Test
	public void invalidQueryTest()
	{
		double x = 42.70867781741311;
		double y = -70.59814453125;
		assertTrue(s.init());
		assertTrue(Double.isNaN(s.lat()));
		assertTrue(Double.isNaN(s.lng()));
		assertTrue(invalidQuery());
		assertTrue(Double.isNaN(s.lat()));
		assertTrue(Double.isNaN(s.lng()));
		assertTrue(query(x, y));
		assertTrue(!Double.isNaN(s.lat()));
		assertTrue(!Double.isNaN(s.lng()));
		assertEquals(s.lat(), x);
		assertEquals(s.lng(), y);
		assertTrue(s.close());
		assertTrue(!connect());
	}

	@Test
	public void openBrowser()
	{
		assertTrue(s.init());
		assertTrue(Double.isNaN(s.lat()));
		assertTrue(Double.isNaN(s.lng()));
		assertTrue(s.openBrowser());
		while( Double.isNaN(s.lat()) )
		{
			Runner.sleep(200);
		}
		assertTrue(!Double.isNaN(s.lat()));
		assertTrue(!Double.isNaN(s.lng()));
		System.out.println("location " + s.lat() + " " + s.lng());
		assertTrue(s.close());
		assertTrue(!connect());

	}

}
