package starhydro.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import starhydro.data.impl.Coord4D;
import starhydro.data.impl.FloatGridManager;
import starhydro.data.impl.FloatRangeImpl;
import starhydro.data.impl.GridManager;
import starhydro.utils.Rectangle2DInteger;
import starhydro.view2d.Palette;

public abstract class RenderedLayer
{
	protected class X
	{
		boolean render ;
		java.awt.Color c ;
	}
	
	private BufferedImage image ;
	private long paletteTimestamp = Long.MIN_VALUE ;
	private Rectangle2DInteger rectangle ;
	private float step = Float.NaN;
	private java.awt.Color clearColor ;
	private boolean normalizePalette;
	
	public RenderedLayer( java.awt.Color clearColor , boolean normalizePalette )
    {
		this.clearColor = clearColor ;
		this.normalizePalette = normalizePalette ;
    }
	
	private Collection<Rectangle2DInteger> anyVisible(final Rectangle2DInteger r, final GridManager manager)
	{
		ArrayList<Rectangle2DInteger> rectanges = new ArrayList<Rectangle2DInteger>();
		final int x0 = r.getX();
		final int y0 = r.getY();
		final int height = r.getHeight();
		final int width = r.getWidth();
		final int x1 = x0 + width;
		final int y1 = y0 + height;
		final Coord4D ul = manager.getCoordinates(x0, y0);
		final Coord4D br = manager.getCoordinates(x1, y1);
		for (int xx = ul.xx; xx <= br.xx; xx++)
		{
			for (int yy = ul.yy; yy <= br.yy; yy++)
			{
				if( manager.hasTile(xx, yy) )
				{
					rectanges.add(r.intersect(manager.getRectangle(xx, yy)));
				}
			}
		}
		return rectanges;
	}

	protected abstract void render( Palette palette, int managerX , int managerY , X ret);
	
	protected abstract FloatGridManager getFloatGridManager();
	public void needRerender()
	{
		this.rectangle = null ;
	}
	public boolean needRerender( Rectangle2DInteger r , GridManager manager , float step , Palette palette )
	{
		if( this.rectangle == null ) return true ;
		if( !this.rectangle.equals(r) ) return true ;
		if( this.step != step ) return true ;
		if( paletteTimestamp != palette.getTimestamp() ) return true ;
		return false ;
	}
	
	public void normalizePalette(Rectangle2DInteger r, Palette palette)
	{
		FloatGridManager manager = getFloatGridManager();
		int height = r.getHeight();
		int width = r.getWidth();
		int x0 = r.getX();
		int y0 = r.getY();
		int x1 = x0 + width;
		int y1 = y0 + height;

		if( height > 0 && width > 0 && anyVisible(r, manager).size() != 0 )
		{
			Coord4D ul = manager.getCoordinates(x0, y0);
			Coord4D br = manager.getCoordinates(x1, y1);
			FloatRangeImpl range = new FloatRangeImpl();
			for (int yy = ul.yy; yy <= br.yy; yy++)
			{
				for (int xx = ul.xx; xx <= br.xx; xx++)
				{
					range.addRange(manager.getValueRange(xx, yy));
				}
			}
			palette.setRange(range.getMin(), range.getMax());
		}
	}

	BufferedImage renderedImage( Rectangle2DInteger r , GridManager manager , float step , Palette palette )
	{
		if( needRerender(r, manager, step, palette))
		{
			if( normalizePalette )
			{
				//TODO: fix this!
				normalizePalette(r,palette);
			}
			
			final int height = r.getHeight();
			final int width = r.getWidth();
			final int x0 = r.getX();
			final int y0 = r.getY();

			int image_width = Math.round( width/step ) ;
			int image_height = Math.round( height / step ) ;
			
			BufferedImage image = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);

			Collection<Rectangle2DInteger> rectangles = anyVisible(r, manager);
			X render = new X();
			if( rectangles.size() != 0 )
			{
				for( Rectangle2DInteger rect : rectangles )
				{
					int rx = rect.getX();
					int ry = rect.getY();
					int rw = rect.getWidth();
					int rh = rect.getHeight();

					if( clearColor != null )
					{
						java.awt.Graphics g = image.getGraphics() ;
						g.setColor(clearColor);
						g.fillRect(Math.round((rx-x0)/step), Math.round((ry-y0)/step), Math.round(rw/step), Math.round(rh/step));						
					}
					
					for (float y = ry; y < (ry + rh); y += step)
					{
						for (float x = rx; x < (rx + rw); x += step)
						{
							render(palette, Math.round(x), Math.round(y), render);
							if( render.render )
							{
								image.setRGB(Math.round((x - x0) / step), Math.round((y - y0) / step), render.c.getRGB());
							}
						}
					}

				}
			}			
			this.rectangle = r ;
			this.image = image ;
			this.step = step ;
			this.paletteTimestamp = palette.getTimestamp() ;
		}
		return image ;
	}
	
	
}
