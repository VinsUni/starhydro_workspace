package starhydro.data.reader.srtm;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.apache.tools.bzip2.CBZip2InputStream;

import starhydro.data.impl.FloatGrid1D;
import starhydro.data.interfaces.FloatGrid;
import starhydro.data.interfaces.TileManager;

public abstract class AbstractSRTMhgtSource implements TileManager
{
	final static int elementsPerGrid = 1200;

	public int getElementsPerGrid()
	{
		return elementsPerGrid;
	}

	String LngLat2FileName(int lng, int lat)
	{
		StringBuilder sb = new StringBuilder();
		DecimalFormat format3 = new DecimalFormat("000");
		DecimalFormat format2 = new DecimalFormat("00");
		if( lat > 0 )
		{
			sb.append("N");
			sb.append(format2.format(lat));
		}
		else
		{
			sb.append("S");
			sb.append(format2.format(-lat));
		}
		if( lng > 0 )
		{
			sb.append("E");
			sb.append(format3.format(lng));
		}
		else
		{
			sb.append("W");
			sb.append(format3.format(-lng));
		}
		sb.append(".hgt.zip");
		return sb.toString();
	}

	public FloatGrid getTile(int lng, int lat)
	{
		java.net.URI file = getBestDatasetFile(lng, lat);
		System.out.println("LocalSRTM getTile" + lng + " " + lat + " file=" + file + " " + LngLat2FileName(lng, lat));
		if( file != null )
		{
			try
			{
				java.net.URL url = file.toURL();
				java.io.InputStream is = null ;
				long size = 0 ;
				if( url.getFile().toString().toLowerCase().endsWith( ".zip" ) )
				{
					ZipInputStream zis = new ZipInputStream(url.openStream());
					ZipEntry entry = zis.getNextEntry();
					size = entry.getSize();
					is = zis ;
				}
				if( url.getFile().toString().toLowerCase().endsWith( ".bz2" ) )
				{
					is = url.openStream() ;
					is.read();is.read();
					is = new CBZip2InputStream( is );
					size = 1201*1201*2;
					
				}
				if( url.getFile().toString().toLowerCase().endsWith( ".gz" ) )
				{
					is = new GZIPInputStream( url.openStream() ) ;
					size = 1201*1201*2;
				}
				if( is == null )
				{
					is = url.openStream() ;
					size = 1201*1201*2;
				}
				SRTMhgtReader reader = new SRTMhgtReader();
				float[] data = reader.read1D(is, (int) size / 2);
				int dim = (int) Math.sqrt(size / 2);
				FloatGrid1D grid = new FloatGrid1D(dim, dim, data);
				return grid;
			}
			catch( Throwable t )
			{
				t.printStackTrace();
			}
		}
		return null;
	}

	public boolean haveTile(int lng, int lat)
	{
		return getBestDatasetFile(lng, lat) != null;
	}

	public void setTile(int lng, int lat, FloatGrid tile)
	{
	}

	abstract java.net.URI getBestDatasetFile(int lng, int lat);
	
	BufferedImage world = null;

	public synchronized BufferedImage getWorldImage()
	{
		Exception exc = null ;
		if( world == null )
		{
			try
			{
				world = ImageIO.read(new java.io.File("resources/world.png"));
			}
			catch( Exception ex )
			{
				exc = ex;
			}
			if( world == null )
			{
				try
				{
					world = ImageIO.read(this.getClass().getResourceAsStream("/resources/world.png") );
				}
				catch( Exception ex )
				{
					exc  = ex;
				}
				
			}
		}
		if( world == null && exc != null )
		{
			exc.printStackTrace();
		}
		return world;
	}
}
