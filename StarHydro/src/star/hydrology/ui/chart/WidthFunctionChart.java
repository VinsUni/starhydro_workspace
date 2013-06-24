package star.hydrology.ui.chart;

import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.WidthFunctionRaiser;
import app.worker.widthfunction.Datapoint;

@SignalComponent(extend = AbstractChart.class)
public class WidthFunctionChart extends WidthFunctionChart_generated
{
	private String[] labels = { "Straight Line", "Topologic", "Geometric" };
	private Set<Datapoint>[] set;

	private final static int INVALID = -1;
	private JList selector;
	private DefaultListModel selectorModel;
	private int myType = INVALID;
	private static final long serialVersionUID = 1L;

	public WidthFunctionChart()
	{
		myType = 0;
	}

	public WidthFunctionChart(int type)
	{
		myType = type;
	}

	protected String getValueAxisLabel()
	{
		if (myType == WidthFunctionRaiser.TOPOLOGIC)
		{
			return "Distance";
		}
		return "Distance (m)";
	}

	@Handles(raises = {})
	void handleEvent(WidthFunctionRaiser r)
	{
		reset();
		int type = r.getType();
		Set<Datapoint> data = r.getData();
		setData(type, data);
	}

	private void setData(int type, Set<Datapoint> data)
	{
		if (type >= 0 && type < set.length)
		{
			set[type] = data;
			// updateSelector();
			updateGraph();
		}
		else
		{
			(new RuntimeException("Type is out of range")).printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void addNotify()
	{
		super.addNotify();		
		set = new Set[labels.length];
		if (myType == INVALID)
		{
			JPanel toolsPanel = new JPanel();
			toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.PAGE_AXIS));
			selectorModel = new DefaultListModel();
			selector = new JList(selectorModel);
			selector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			updateSelector();
			toolsPanel.add(new JLabel("Please select series:"));
			toolsPanel.add(selector);
			add(toolsPanel);
			selector.addListSelectionListener(new ListSelectionListener()
			{

				public void valueChanged(ListSelectionEvent e)
				{
					updateGraph();
				}
			});
		}
	}

	protected String getTitle()
	{
		return "";
	}

	private void updateSelector()
	{
		selectorModel.clear();
		for (int i = 0; i < set.length; i++)
		{
			selectorModel.addElement(labels[i]);
		}
	}

	private XYItemRenderer getBarRenderer()
	{
		XYItemRenderer ret = new XYStepRenderer();
		ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		return ret;

	}

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
		for (int i = 0; i < set.length; i++)
		{
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			if (set[i] != null && ((selector != null && selector.isSelectedIndex(i)) || myType == i))
			{
				float[] x = new float[set[i].size()];
				float[] y = new float[set[i].size()];
				int j = 0;
				Iterator<Datapoint> iter = set[i].iterator();
				while (iter.hasNext())
				{
					Datapoint p = iter.next();
					x[j] = p.getDistance();
					y[j] = p.getWidth();
					j++;
				}
				if (utils.Runner.isInterrupted())
				{
					return;
				}
				addSeries(x, y, labels[i], index++, 0, x.length, false, getBarRenderer());
			}
		}

		if (index == 0)
		{
			getPlot().setRangeAxis(getAxis("", false));
		}
		addLegend();

	}

}
