package starhydro.utils;

import java.text.MessageFormat;

public class Point3DDouble
{
	private static final long serialVersionUID = 1L;
	double x;
	double y;
	double z;
	
	public Point3DDouble( double x , double y , double z )
    {
		this.x = x ; this.y = y ; this.z = z ;
    }

	public double getX()
    {
    	return x;
    }

	public double getY()
    {
    	return y;
    }

	public double getZ()
    {
    	return z;
    }

	@Override
	public String toString()
	{
	    return MessageFormat.format( "[Point3DDouble {0} {1} {2}]" , x,y,z ) ;
	}
	
	public double distance( Point3DDouble that )
	{
		double dX = this.getX() - that.getX() ;
		double dY = this.getY() - that.getY() ;
		double dZ = this.getZ() - that.getZ() ;
		return Math.sqrt( dX*dX+dY*dY+dZ*dZ ) ;
	}
}
