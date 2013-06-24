package starhydro.data.reader.ascii;

import java.io.PrintStream;

import starhydro.algorithms.WGS84Distances;
import starhydro.data.interfaces.ByteGrid;
import starhydro.data.interfaces.FloatGrid;
import starhydro.utils.Point3DDouble;
import starhydro.utils.Rectangle2DInteger;

public class AsciiGridWriter
{

	public static void saveByteGrid( java.io.OutputStream stream , ByteGrid grid , Rectangle2DInteger rect , int elementsPerDegree)
	{
		final int NODATA = -9999 ;
		PrintStream printStream = new PrintStream(stream);
		printStream.println( "ncols\t\t" + rect.getWidth() );
		printStream.println( "nrows\t\t" + rect.getHeight() );
		printStream.println( "xllcorner\t" + 0 ) ;
		printStream.println( "yllcorner\t" + 0 ) ;
		printStream.println( "cellsize\t" + getCellSize(rect, elementsPerDegree));
		printStream.println( "NODATA_value\t" + NODATA ) ;
		for( int y = 0 ; y < rect.getHeight() ; y++ )
		{
			for( int x = 0 ; x < rect.getWidth() ; x++ )
			{
				int value = grid.get( rect.getX() + x, rect.getY() + y );
				if( value == -128 )
				{
					value = 128 ;
				}
				printStream.print( value + " " ) ;
			}
			printStream.println();
		}		
	}

	public static void saveFloatGrid( java.io.OutputStream stream , FloatGrid grid , Rectangle2DInteger rect , int elementsPerDegree)
	{
		final int NODATA = -9999 ;
		PrintStream printStream = new PrintStream(stream);
		printStream.println( "ncols\t\t" + rect.getWidth() );
		printStream.println( "nrows\t\t" + rect.getHeight() );
		printStream.println( "xllcorner\t" + 0 ) ;
		printStream.println( "yllcorner\t" + 0 ) ;
		printStream.println( "cellsize\t" + getCellSize(rect, elementsPerDegree));
		printStream.println( "NODATA_value\t" + NODATA ) ;
		for( int y = 0 ; y < rect.getHeight() ; y++ )
		{
			for( int x = 0 ; x < rect.getWidth() ; x++ )
			{
				float value = grid.get( rect.getX() + x, rect.getY() + y );
				if(! Float.isNaN( value ) ) 
				{
					printStream.print( value + " " ) ;
				}
				else
				{
					printStream.print( NODATA + " " ) ;
				}
			}
			printStream.println();
		}		
	}
	
	private static float getCellSize(Rectangle2DInteger area, int elementsPerDegree)
	{
		WGS84Distances conv = new WGS84Distances();
		float longitude1 = 1.0f*area.getX()/elementsPerDegree;
		float latitude1 = 90-1.0f*area.getY()/elementsPerDegree;
		float longitude2 = 1.0f*(area.getX()+1)/elementsPerDegree;
		float latitude2 = 90-1.0f*(area.getY()+1)/elementsPerDegree;

		Point3DDouble dist11 = conv.positionDeg(latitude1, longitude1, 0);
		Point3DDouble dist12 = conv.positionDeg(latitude1, longitude2, 0);
		Point3DDouble dist21 = conv.positionDeg(latitude2, longitude1, 0);
		double distX = dist11.distance(dist12);
		double distY = dist11.distance(dist21);

		return Math.round( Math.sqrt( distX * distY ) );
	}
	
	
}
