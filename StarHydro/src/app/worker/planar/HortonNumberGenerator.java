package app.worker.planar;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;

import star.annotations.Preferences;
import star.annotations.SignalComponent;
import star.hydrology.events.HortonNumberEvent;
import star.hydrology.events.HortonNumberRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;
import utils.LinearRegression;

@Preferences()
@SignalComponent(extend = Object.class, handles = { StreamOrderStatisticsRaiser.class }, raises = { HortonNumberRaiser.class })
public class HortonNumberGenerator extends HortonNumberGenerator_generated
{
	@Override
	public void handleEvent(StreamOrderStatisticsRaiser r)
	{
		setOrderAreas(r.getOrderAreas());
		setOrderCount(r.getOrderCount());
		setOrderLength(r.getOrderLength());
		setOrderSlopes(r.getOrderSlopes());
		updateNumbers();
	}

	private int[] orderCount;
	private float[] orderLength;
	private float[] orderAreas;
	private float[] orderSlopes;

	private void updateRl()
	{
		int nr_orders = getNumberOfOrders();
		ArrayList<Point2D.Float> list = new ArrayList<Point2D.Float>();
		for (int i = 1; i < nr_orders; i++)
		{
			list.add(new Point2D.Float(i, (float) Math.log(getOrderLength(i))));
		}
		LinearRegression lr = new LinearRegression();
		lr.setData(list);
		hortonNumbers.put(Rl_SLOPE, new Float(lr.getSlope()));
		hortonNumbers.put(Rl_INTERCEPT, new Float(lr.getIntercept()));
		hortonNumbers.put(Rl_UNCERTAINTY, new Float(lr.getUncertainty()));
	}

	private void updateRb()
	{
		int nr_orders = getNumberOfOrders();
		ArrayList<Point2D.Float> list = new ArrayList<Point2D.Float>();
		for (int i = 1; i < nr_orders; i++)
		{
			list.add(new Point2D.Float(i, (float) Math.log(getOrderCount(i))));
		}
		LinearRegression lr = new LinearRegression();
		lr.setData(list);
		hortonNumbers.put(Rb_SLOPE, new Float(lr.getSlope()));
		hortonNumbers.put(Rb_INTERCEPT, new Float(lr.getIntercept()));
		hortonNumbers.put(Rb_UNCERTAINTY, new Float(lr.getUncertainty()));

	}

	private void updateRa()
	{
		int nr_orders = getNumberOfOrders();
		ArrayList<Point2D.Float> list = new ArrayList<Point2D.Float>();
		for (int i = 1; i < nr_orders; i++)
		{
			list.add(new Point2D.Float(i, (float) Math.log(getOrderArea(i))));
		}
		LinearRegression lr = new LinearRegression();
		lr.setData(list);
		hortonNumbers.put(Ra_SLOPE, new Float(lr.getSlope()));
		hortonNumbers.put(Ra_INTERCEPT, new Float(lr.getIntercept()));
		hortonNumbers.put(Ra_UNCERTAINTY, new Float(lr.getUncertainty()));
	}

	private void updateRs()
	{
		int nr_orders = getNumberOfOrders();
		ArrayList<Point2D.Float> list = new ArrayList<Point2D.Float>();
		for (int i = 1; i < nr_orders; i++)
		{
			list.add(new Point2D.Float(i, (float) Math.log(getOrderSlopes(i))));
		}
		LinearRegression lr = new LinearRegression();
		lr.setData(list);
		hortonNumbers.put(Rs_SLOPE, new Float(lr.getSlope()));
		hortonNumbers.put(Rs_INTERCEPT, new Float(lr.getIntercept()));
		hortonNumbers.put(Rs_UNCERTAINTY, new Float(lr.getUncertainty()));

	}

	private void updateNumbers()
	{
		hortonNumbers.clear();
		if (orderCount != null && getNumberOfOrders() >= 2)
		{
			updateRa();
			updateRb();
			updateRl();
			updateRs();
		}
		raise();
	}

	private float getOrderArea(int i)
	{
		return orderAreas[i];
	}

	private void setOrderAreas(float[] orderAreas)
	{
		this.orderAreas = orderAreas;
	}

	private int getOrderCount(int i)
	{
		return orderCount[i];
	}

	private void setOrderCount(int[] orderCount)
	{
		this.orderCount = orderCount;
	}

	private float getOrderLength(int i)
	{
		return orderLength[i];
	}

	private void setOrderLength(float[] orderLength)
	{
		this.orderLength = orderLength;
	}

	private int getNumberOfOrders()
	{
		return orderCount != null ? orderCount.length : 0;
	}

	private float getOrderSlopes(int i)
	{
		return orderSlopes[i];
	}

	private void setOrderSlopes(float[] orderSlopes)
	{
		this.orderSlopes = orderSlopes;
	}

	private void raise()
	{
		(new HortonNumberEvent(this)).raise();
	}

	private Hashtable<String, Float> hortonNumbers = new Hashtable<String, Float>();

	public Hashtable<String, Float> getHortonNumbers()
	{
		return hortonNumbers;
	}

}
