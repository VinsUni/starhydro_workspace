package star.hydro.rainfall;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import star.annotations.SignalComponent;

@SignalComponent(raises = { TimeSeriesRaiser.class })
public class TimeSeries extends TimeSeries_generated
{
	private TreeMap<Float, Float> dataset = new TreeMap<Float, Float>();
	private float value;
	private int gauge;
	private Color color;

	public void setColor(Color c)
	{
		this.color = c;
	}

	public Color getColor()
	{
		return color;
	}

	public void setGauge(int gauge)
	{
		this.gauge = gauge;
	}

	public int getGauge()
	{
		return gauge;
	}

	public void setValue(float value)
	{
		this.value = value;
	}

	public float getValue()
	{
		return value;
	}

	public void clear()
	{
		dataset.clear();
	}

	public void add(float hour, float prec)
	{
		dataset.put(hour, prec);
	}

	public TreeMap<Float, Float> getDataset()
	{
		return dataset;
	}

	public void addValue(float hour, float prec)
	{
		Float get = dataset.get(hour);
		if (get != null)
		{
			dataset.put(hour, prec + get.floatValue());
		}
		else
		{
			dataset.put(hour, prec);
		}
	}

	@SuppressWarnings("deprecation")
	public void load(InputStream is) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = r.readLine()) != null)
		{
			String station = line.substring(0, 8);
			int month = Integer.parseInt(line.substring(8, 10).trim());
			int day = Integer.parseInt(line.substring(10, 12).trim());
			int year = Integer.parseInt(line.substring(12, 14));
			int hour = Integer.parseInt(line.substring(14, 16));
			int min = Integer.parseInt(line.substring(16, 18));
			String prec = line.substring(19, 32);
			// String misc = line.substring(33);
			System.out.println(station + " " + month + " " + day + " " + year + " " + hour + " " + min + " " + prec);
			float fhour = ((new GregorianCalendar(1900 + year, month + 1, day, hour % 24, min, 0)).getTime().getTime()) / 3600;
			float fprec = Float.parseFloat(prec);
			add(fhour, fprec);
		}
	}

	public void setTimeSeries(TimeSeries ts)
	{
		this.dataset = ts.dataset;
		(new TimeSeriesEvent(this)).raise();
	}

	public void defaultInit()
	{
		String file = "C:\\Documents and Settings\\All Users\\Documents\\Limited\\little_washita_hydrolap_arsusda_gov_1969_1991_l69dlyp\\RG000001.L69";
		try
		{
			load(new FileInputStream(file));
			(new TimeSeriesEvent(this)).raise();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void defaultDelta()
	{
		clear();
		add(-1, 0);
		add(0, 1);
		add(1, 0);
		add(2, 0);
		(new TimeSeriesEvent(this)).raise();
	}

	public static void main(String[] args)
	{
		TimeSeries ts = new TimeSeries();
		ts.defaultInit();
	}

	public TimeSeries getTimeSeries()
	{
		return this;
	}

}
