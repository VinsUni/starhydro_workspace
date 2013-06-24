package starhydro.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import starhydro.data.interfaces.TileManager;
import starhydro.data.reader.srtm.AbstractSRTMhgtSource;
import starhydro.data.reader.srtm.StarSRTMhgtSource;
import starhydro.utils.Rectangle2DInteger;

public class WorldElevation extends FloatWorld
{
	private AbstractSRTMhgtSource source;

	private AbstractSRTMhgtSource getLocalSource()
	{
		if( source == null )
		{
			//source = new LocalSRTMhgtSource();
			source = new StarSRTMhgtSource();
		}
		return source;
	}

	public int getElementsPerGrid()
	{
		return getLocalSource().getElementsPerGrid();
	}

	protected TileManager getSource()
	{
		return getLocalSource();
	}
	
	public void renderBackground(Graphics2D g, Rectangle2DInteger r , float step , boolean grid)
	{
		final int height = r.getHeight();
		final int width = r.getWidth();
		final int x0 = r.getX();
		final int y0 = r.getY();
		if( height > 0 && width > 0 )
		{
			BufferedImage world = source.getWorldImage() ;
			int img_width = world.getWidth() ;
			int img_height = world.getHeight() ;
			double x0f = x0 * 1.0 / 360 / getElementsPerGrid() ;
			double y0f = y0 * 1.0 / 180 / getElementsPerGrid() ;
			double x1f = (x0+width)* 1.0 / 360 / getElementsPerGrid() ;
			double y1f = (y0+height)* 1.0 / 180 / getElementsPerGrid() ;
					
			System.out.println(MessageFormat.format( "numbers {0} {1} - {2} {3} ; {4} {5} - {6} {7}" , 0, 0, width/step, height/step, (int)(x0f*img_width), (int)(y0f*img_height), (int)(x1f*img_width), (int)(y1f*img_height)));
			g.drawImage(world, 0, 0, Math.round(width/step), Math.round(height/step), (int)(x0f*img_width), (int)(y0f*img_height), (int)(x1f*img_width), (int)(y1f*img_height), null);
			if( grid )
			{
				renderGrid(g, r, step);
			}
		}
	}
	
}
