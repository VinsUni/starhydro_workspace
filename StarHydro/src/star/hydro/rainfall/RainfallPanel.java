package star.hydro.rainfall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.vecmath.Point3f;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import star.annotations.SignalComponent;
import star.hydrology.events.RainfallGaugesTimeseriesRaiser;

@SignalComponent(extend = JTabbedPane.class, raises = { RainfallGaugesTimeseriesRaiser.class })
public class RainfallPanel extends RainfallPanel_generated implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private Collection<TimeSeries> raingaugesTimeSeries;
	private Collection<Point3f> raingauges;

	@Override
	public void addNotify()
	{
		super.addNotify();
		addTab("Gauges", new RainfallGauges());
		addTab("Chart", getRainfallChartPanel());
	}

	private JPanel getRainfallChartPanel()
	{
		JPanel panel = new JPanel();
		JButton button = new JButton("Load Rainfall data");
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(button);
		panel.add(new RainfallChart());
		button.addActionListener(this);
		return panel;
	}

	public void actionPerformed(ActionEvent e)
	{
		loadRainGauges();
	}

	private java.io.InputStream loadDialog() throws FileNotFoundException
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Load rainfall data");
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			java.io.File f = fc.getSelectedFile();
			return new java.io.FileInputStream(f);
		}
		return null;
	}

	private void loadRainGauges()
	{
		try
		{
			java.io.InputStream is = loadDialog();
			if (is != null)
			{
				HSSFWorkbook wb = new HSSFWorkbook(is);
				HSSFSheet sheet = wb.getSheetAt(0);
				int lastCell = sheet.getRow(0).getLastCellNum();
				ArrayList<Point3f> raingauges = new ArrayList<Point3f>(lastCell - 1);
				ArrayList<TimeSeries> timeseries = new ArrayList<TimeSeries>(lastCell - 1);
				int lastRow = sheet.getLastRowNum();
				for (short col = 1; col < lastCell; col++)
				{
					boolean skip = true;
					String raingauge = sheet.getRow(0).getCell(col).toString();
					TimeSeries timeSeries = new TimeSeries();
					timeSeries.setGauge(col - 1);
					timeSeries.add(-1, 0);
					int maxHour = 0;
					for (int row = 1; row <= lastRow; row++)
					{
						String hourS = sheet.getRow(row).getCell((short) 0).toString();
						String percS = sheet.getRow(row).getCell(col).toString();
						if (hourS != null && percS != null && hourS.length() != 0 && percS.length() != 0)
						{
							skip = false;
							int hour = Math.round(Float.parseFloat(hourS));
							float perc = Float.parseFloat(percS);
							timeSeries.add(hour, perc);
							maxHour = Math.max(maxHour, hour);
						}
					}
					timeSeries.add(maxHour + 1, 0);
					if (!skip)
					{
						System.out.println("Add " + timeSeries);
						timeseries.add(timeSeries);
						raingauges.add(getPoint(raingauge));
					}
					this.raingauges = raingauges;
					this.raingaugesTimeSeries = timeseries;
				}
				raise_RainfallGaugesTimeseriesEvent();
				is.close();

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private Point3f getPoint(String raingaugeString)
	{
		if (raingaugeString.indexOf('[') != -1)
		{
			raingaugeString = raingaugeString.substring(raingaugeString.indexOf('[') + 1);
		}
		if (raingaugeString.indexOf(']') != -1)
		{
			raingaugeString = raingaugeString.substring(0, raingaugeString.indexOf(']'));
		}
		String[] coords = raingaugeString.split(",");
		float x = 0;
		float y = 0;
		float z = 0;
		if (coords.length >= 2)
		{
			x = Float.parseFloat(coords[0]);
			y = Float.parseFloat(coords[1]);
		}
		if (coords.length == 3)
		{
			z = Float.parseFloat(coords[2]);
		}
		return new Point3f(x, y, z);
	}

	public Collection<TimeSeries> getRainfallTimeseries()
	{
		return raingaugesTimeSeries;

	}

	public Collection<Point3f> getRainfallGauges()
	{
		return raingauges;
	}
}
