package star.hydrology.ui.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.LegendItemCollection;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.HortonNumberRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;

@SignalComponent(extend = AbstractChart.class)
public class HortonNumberChart extends HortonNumberChart_generated
{
	private static final long serialVersionUID = 1L;
	private JList selector;
	private DefaultListModel selectorModel;
	private Hashtable<String, Float> hortonLinReg;
	private JCheckBox trendLine;
	private JCheckBox logScale;

	public void addNotify()
	{
		super.addNotify();
		JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.PAGE_AXIS));
		// toolsPanel.setLayout( new FlowLayout() ) ;
		selectorModel = new DefaultListModel();
		selector = new JList(selectorModel);
		selector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		selectorModel.clear();
		selectorModel.addElement("Area");
		selectorModel.addElement("Count");
		selectorModel.addElement("Length");
		selectorModel.addElement("Slope");

		trendLine = new JCheckBox("Trend line ");
		logScale = new JCheckBox("Log Scale");
		toolsPanel.add(new JLabel("Please select series:"));
		toolsPanel.add(selector);
		toolsPanel.add(trendLine);
		toolsPanel.add(logScale);
		add(toolsPanel);
		selector.addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent e)
			{
				updateGraph();
			}
		});

		logScale.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateGraph();
			}
		});

		trendLine.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateGraph();
			}
		});
	}

	public void removeNotify()
	{
		super.removeNotify();
	}

	protected String getValueAxisLabel()
	{
		return "Stream order";
	}

	@Handles(raises = {})
	void handleEvent(HortonNumberRaiser r)
	{
		hortonLinReg = r.getHortonNumbers();
		updateGraph();
	}

	@Handles(raises = {})
	void handleEvent(StreamOrderStatisticsRaiser r)
	{
		areas = r.getOrderAreas();
		count = r.getOrderCount();
		length = r.getOrderLength();
		slopes = r.getOrderSlopes();
		updateGraph();
	}

	private float[] areas;
	private int[] count;
	private float[] length;
	private float[] slopes;

	private void updateGraph()
	{
		reset();
		setLegend(new LegendItemCollection());
		int index = 0;
		for (int i = 0; i < getPlot().getDatasetCount(); i++)
		{
			getPlot().setDataset(i, null);
			getPlot().setRangeAxis(i, null);
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		if (areas != null && selector.isSelectedIndex(0))
		{
			addSeries(getIndexesAsArray(areas.length, 0), areas, "Areas", index++, 1, areas.length, logScale.isSelected(), getShapeRenderer());
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine.isSelected())
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Ra_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Ra_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Ra_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(areas.length - 1, 1), "Areas", index++, slope, intercept, uncertainty, getLineRenderer());
				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		if (count != null && selector.isSelectedIndex(1))
		{
			addSeries(getIndexesAsArray(count.length, 0), int2float(count), "Count", index++, 1, areas.length, logScale.isSelected(), getShapeRenderer());
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine.isSelected())
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Rb_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Rb_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Rb_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(areas.length - 1, 1), "Count", index++, slope, intercept, uncertainty, getLineRenderer());
				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		if (length != null && selector.isSelectedIndex(2))
		{
			addSeries(getIndexesAsArray(length.length, 0), length, "Length", index++, 1, areas.length, logScale.isSelected(), getShapeRenderer());
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine.isSelected())
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Rl_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Rl_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Rl_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(length.length - 1, 1), "Length", index++, slope, intercept, uncertainty, getLineRenderer());

				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		if (length != null && selector.isSelectedIndex(3))
		{
			addSeries(getIndexesAsArray(slopes.length, 0), slopes, "Slope", index++, 1, areas.length, logScale.isSelected(), getShapeRenderer());
			float slope = Float.NaN;
			float intercept = Float.NaN;
			float uncertainty = Float.NaN;
			if (hortonLinReg != null && trendLine.isSelected())
			{
				try
				{
					slope = hortonLinReg.get(HortonNumberRaiser.Rs_SLOPE).floatValue();
					intercept = hortonLinReg.get(HortonNumberRaiser.Rs_INTERCEPT).floatValue();
					uncertainty = hortonLinReg.get(HortonNumberRaiser.Rs_UNCERTAINTY).floatValue();
					addTrend(getIndexesAsArray(slopes.length - 1, 1), "Slope", index++, slope, intercept, uncertainty, getLineRenderer());

				}
				catch (NullPointerException ex)
				{
				}
			}
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		if (index == 0)
		{
			getPlot().setRangeAxis(getAxis("", false));
		}

		addLegend();
	}

	private float[] getIndexesAsArray(int len, int offset)
	{
		float[] ret = new float[len];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = i + offset;
		}
		return ret;
	}

	protected String getTitle()
	{
		return "Horton numbers";
	}

}
