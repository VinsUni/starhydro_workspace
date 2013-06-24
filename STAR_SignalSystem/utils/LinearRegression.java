package utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

public class LinearRegression
{
	boolean isValid = false;
	float[] x, y;
	float sxy, sxx, syy;
	float slope, intercept;

	float xavg = 0;
	float yavg = 0;
	float sr = 0;

	public void setData(ArrayList<Point2D.Float> list)
	{
		synchronized( this )
		{

			Iterator<Point2D.Float> iter = list.iterator();
			while( iter.hasNext() )
			{
				Point2D.Float p = iter.next();
				if( Float.isNaN(p.x) || Float.isNaN(p.y) || Float.isInfinite(p.x) || Float.isInfinite(p.y) )
				{
					iter.remove();
				}
			}
			this.x = new float[list.size()];
			this.y = new float[list.size()];
			int pos = 0;
			for (Point2D.Float p : list)
			{
				this.x[pos] = p.x;
				this.y[pos] = p.y;
				pos++;
			}
			isValid = false;
		}

	}

	public void setData(float[] x, float[] y)
	{
		synchronized( this )
		{
			this.x = x;
			this.y = y;
			isValid = false;
		}
	}

	void calculateSums()
	{

		float sumx2 = 0;
		float sumy2 = 0;
		float sumxy = 0;

		float sumx = 0;
		float sumy = 0;
		for (int i = 0; i < x.length; i++)
		{
			sumx2 += x[i] * x[i];
			sumxy += x[i] * y[i];
			sumy2 += y[i] * y[i];
			sumx += x[i];
			sumy += y[i];
		}
		this.sxx = sumx2 - sumx * sumx / x.length;
		this.sxy = sumxy - sumx * sumy / x.length;
		this.syy = sumy2 - sumy * sumy / x.length;
		this.xavg = sumx / x.length;
		this.yavg = sumy / y.length;
	}

	void calculateSlopeAndIntercept()
	{
		slope = sxy / sxx;
		intercept = yavg - slope * xavg;
	}

	void calculateUncertanty()
	{
		sr = (float) Math.sqrt((syy - slope * slope * sxx) / (x.length - 2));
	}

	void calculate()
	{
		synchronized( this )
		{
			calculateSums();
			calculateSlopeAndIntercept();
			calculateUncertanty();
			isValid = true;
		}
	}

	public float getSlope()
	{
		if( !isValid )
		{
			calculate();
		}
		return slope;
	}

	public float getIntercept()
	{
		if( !isValid )
		{
			calculate();
		}
		return intercept;
	}

	public float getUncertainty()
	{
		if( !isValid )
		{
			calculate();
		}
		return sr;
	}

}
