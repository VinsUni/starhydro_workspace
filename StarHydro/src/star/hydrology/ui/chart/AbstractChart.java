package star.hydrology.ui.chart;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import mit.swing.xJButton;
import mit.swing.xJToggleButton;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

import star.hydrology.ui.chart.horton.LogScaleEvent;
import star.hydrology.ui.chart.horton.LogScaleRaiser;
import star.hydrology.ui.chart.horton.TrendLineEvent;
import star.hydrology.ui.chart.horton.TrendLineRaiser;
import star.hydrology.ui.palette.Palette;

public abstract class AbstractChart extends javax.swing.JPanel
{
	private static final long serialVersionUID = 1L;

	private XYPlot plot;
	private JFreeChart chart;
	private LegendItemCollection legend;
	private DrawingSupplier supplier;
	private JTextArea area;
	private JSplitPane splitPane;
	private boolean supportsLogScale = false;
	private boolean supportsTrendLine = false;

	private JComponent trendLine;

	private JComponent logScale;

	protected JTextArea getArea()
	{
		return area;
	}

	protected void setSupportsLogScale(boolean supports)
	{
		supportsLogScale = supports;
		if (logScale != null)
		{
			logScale.setVisible(supports);
		}

	}

	protected void setSupportsTrendLine(boolean supports)
	{
		supportsTrendLine = supports;
		if (trendLine != null)
		{
			trendLine.setVisible(supports);
		}
	}

	protected void setLegend(LegendItemCollection legend)
	{
		this.legend = legend;
	}

	protected LegendItemCollection getLegend()
	{
		return legend;
	}

	protected void addLegend()
	{
		getChart().removeLegend();

		LegendTitle legend = new LegendTitle(new LegendItemSource()
		{

			public LegendItemCollection getLegendItems()
			{
				return getLegend();
			}
		});
		getChart().addLegend(legend);
	}

	public void addNotify()
	{
		init();
		super.addNotify();
	}

	private static class LogScaleLabel extends xJToggleButton implements LogScaleRaiser, ActionListener
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void addNotify()
		{
			// TODO Auto-generated method stub
			super.addNotify();
			try
			{
				setIcon(new ImageIcon(this.getClass().getResource("/resources/spreadsheet_log.png")));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			setToolTipText("Switch to logarithmic/linear view.");
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setMaximumSize(new Dimension(32, 32));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			System.out.println("LogScaleEvent " + e);
			(new LogScaleEvent(this)).raise();
		}
	}

	private static class TrendLineLabel extends xJToggleButton implements TrendLineRaiser, ActionListener
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void addNotify()
		{
			// TODO Auto-generated method stub
			super.addNotify();
			try
			{
				setIcon(new ImageIcon(this.getClass().getResource("/resources/spreadsheet_line.png")));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			setToolTipText("Add remove trendline.");
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setMaximumSize(new Dimension(32, 32));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			(new TrendLineEvent(this)).raise();
		}
	}

	private class ShowTextLabel extends xJButton implements TrendLineRaiser, ActionListener
	{
		private static final long serialVersionUID = 1L;
		private ImageIcon table;

		@Override
		public void addNotify()
		{
			// TODO Auto-generated method stub
			super.addNotify();
			try
			{
				table = new ImageIcon(this.getClass().getResource("/resources/view_text.png"));
				setIcon(table);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			setToolTipText("View chart/data.");
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setMaximumSize(new Dimension(32, 32));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			if (splitPane.getDividerLocation() > splitPane.getWidth() / 2)
			{
				splitPane.setDividerLocation(.5f);
			}
			else
			{
				splitPane.setDividerLocation(1f);
			}
		}
	}

	private class ShowChartLabel extends xJButton implements TrendLineRaiser, ActionListener
	{

		private static final long serialVersionUID = 1L;
		private ImageIcon chart;

		@Override
		public void addNotify()
		{
			// TODO Auto-generated method stub
			super.addNotify();
			try
			{
				chart = new ImageIcon(this.getClass().getResource("/resources/spreadsheet.png"));
				setIcon(chart);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			setToolTipText("View chart/data.");
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setMaximumSize(new Dimension(32, 32));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			if (splitPane.getDividerLocation() < splitPane.getDividerSize())
			{
				splitPane.setDividerLocation(.5f);
			}
			else
			{
				splitPane.setDividerLocation(.0f);

			}
		}

	}

	private void init()
	{
		JPanel pp = new JPanel();
		pp.setLayout(new BoxLayout(pp, BoxLayout.PAGE_AXIS));
		add(pp);

		plot = new XYPlot();
		plot.setDomainAxis(getAxis(getValueAxisLabel(), false));
		plot.setRenderer(getShapeRenderer());
		plot.setRangeAxis(getAxis("", false));
		chart = new JFreeChart(getTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		chart.setBackgroundPaint(getBackground());
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(175, 150));
		// add(panel);
		this.supplier = getPlot().getDrawingSupplier();
		area = new JTextArea(8, 10);
		area.setEditable(false);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, new JScrollPane(area), panel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0);

		add(splitPane);

		pp.add(new ShowChartLabel());
		pp.add(new ShowTextLabel());
		logScale = new LogScaleLabel();
		logScale.setVisible(supportsLogScale);

		trendLine = new TrendLineLabel();
		trendLine.setVisible(supportsTrendLine);

		pp.add(logScale);
		pp.add(trendLine);

	}

	@Override
	public void setBackground(Color bg)
	{
		super.setBackground(bg);
		if (chart != null)
		{
			chart.setBackgroundPaint(getBackground());
		}
	}

	protected XYPlot getPlot()
	{
		return plot;
	}

	protected JFreeChart getChart()
	{
		return chart;
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getShapeRenderer()
	{
		XYItemRenderer ret = new XYLineAndShapeRenderer(false, true);
		ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		// XYItemRenderer ret = new MyRenderer();
		java.awt.Stroke stroke = new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL);
		ret.setBaseStroke(stroke);
		return ret;
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getShapeRenderer(Palette p, float min, float max, int total)
	{
		if (p != null)
		{
			XYItemRenderer ret = new MyRenderer(false, true, min, max, total, p);
			ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			java.awt.Stroke stroke = new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL);
			ret.setBaseStroke(stroke);
			return ret;
		}
		else
		{
			return getShapeRenderer();
		}
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getStackedRenderer(Palette p, float[] values)
	{
		StackedXYBarRenderer ret = new MyStackedXYBarRenderer(p, values);
		ret.setDrawBarOutline(false);
		ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		return ret;
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getStackedRenderer(Color[] values)
	{
		StackedXYBarRenderer ret = new MyColorStackedXYBarRenderer(values);
		ret.setDrawBarOutline(false);
		ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		return ret;
	}

	private static class MyColorStackedXYBarRenderer extends StackedXYBarRenderer
	{
		private static final long serialVersionUID = 1L;
		private Color[] values;

		public MyColorStackedXYBarRenderer(Color[] values)
		{
			this.values = values;
		}

		public Paint getItemPaint(int row, int column)
		{
			try
			{
				return values[row];
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return Color.pink;
			}
		}

	}

	private static class MyStackedXYBarRenderer extends StackedXYBarRenderer
	{
		private static final long serialVersionUID = 1L;
		private Palette p;
		private float[] values;

		public MyStackedXYBarRenderer(Palette p, float[] values)
		{
			this.p = p;
			this.values = values;
		}

		public Paint getItemPaint(int row, int column)
		{
			try
			{
				return p != null ? p.getColor(values != null ? values[row] : 0) : Color.black;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return Color.pink;
			}
		}

	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getLineRenderer(Color c)
	{
		XYItemRenderer ret = new XYLineAndShapeRenderer(true, false);
		ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		java.awt.Stroke stroke = new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL);
		ret.setBaseStroke(stroke);
		ret.setBasePaint(c);
		return ret;
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getLineRenderer()
	{
		XYItemRenderer ret = new XYLineAndShapeRenderer(true, false);
		ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		java.awt.Stroke stroke = new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL);
		ret.setBaseStroke(stroke);
		return ret;
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getLineRenderer(Palette p, float min, float max, int total)
	{
		if (p != null)
		{
			XYItemRenderer ret = new MyRenderer(true, false, min, max, total, p);
			ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			java.awt.Stroke stroke = new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL);
			ret.setBaseStroke(stroke);
			return ret;
		}
		else
		{
			return getLineRenderer();
		}
	}

	protected org.jfree.chart.renderer.xy.XYItemRenderer getBarRenderer(Palette p, float min, float max, int total)
	{
		if (p != null)
		{
			XYItemRenderer ret = new MyBarRenderer(min, max, total, p);
			ret.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			java.awt.Stroke stroke = new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL);
			ret.setBaseStroke(stroke);
			return ret;
		}
		else
		{
			return getLineRenderer();
		}
	}

	class MyBarRenderer extends XYBarRenderer
	{
		private static final long serialVersionUID = 1L;
		Palette p;
		float min, max;
		int total;

		public MyBarRenderer(float min, float max, int total, Palette p)
		{
			super();
			this.total = total;
			this.p = p;
			this.max = max;
			this.min = min;
		}

		@Override
		public Paint getItemPaint(int row, int column)
		{
			if (max >= min)
			{
				return p.getColor(1.0f * column / (total - 1));
			}
			else
			{
				return p.getColor(1.0f * (total - 1 - column) / (total - 1));
			}
		}
	}

	class MyRenderer extends XYLineAndShapeRenderer
	{
		private static final long serialVersionUID = 1L;
		Palette p;
		float min, max;
		int total;

		public MyRenderer(boolean a, boolean b, float min, float max, int total, Palette p)
		{
			super(a, b);
			this.total = total;
			this.p = p;
			this.max = max;
			this.min = min;
		}

		@Override
		public Paint getItemPaint(int row, int column)
		{
			if (max >= min)
			{
				return p.getColor(1.0f * column / (total - 1));
			}
			else
			{
				return p.getColor(1.0f * (total - 1 - column) / (total - 1));
			}
		}
	}

	protected NumberAxis getDomainAxis(String name, boolean logScale)
	{
		NumberAxis rangeAxis;
		if (logScale)
		{
			rangeAxis = new LogarithmicAxis(name);
			rangeAxis.setAutoRange(true);
			rangeAxis.setAutoRangeStickyZero(false);
		}
		else
		{
			rangeAxis = new NumberAxis(name);
			rangeAxis.setAutoRange(true);
			rangeAxis.setAutoRangeStickyZero(false);
		}
		return rangeAxis;
	}

	public NumberAxis rNr = null;
	public LogarithmicAxis rLg = null;

	protected NumberAxis getRangeAxis(String name, boolean logScale)
	{
		NumberAxis rangeAxis;
		if (logScale)
		{
			rangeAxis = new LogarithmicAxis(name);
		}
		else
		{
			rangeAxis = new NumberAxis(name);
			rangeAxis.setAutoRange(true);
			rangeAxis.setAutoRangeStickyZero(false);
		}
		return rangeAxis;
	}

	protected NumberAxis getAxis(String name, boolean logScale)
	{
		NumberAxis rangeAxis;
		if (logScale)
		{
			rangeAxis = new LogarithmicAxis(name);
		}
		else
		{
			rangeAxis = new NumberAxis(name);
			rangeAxis.setAutoRange(true);
			rangeAxis.setAutoRangeStickyZero(false);
		}
		return rangeAxis;
	}

	protected float[] int2float(int[] array)
	{
		float[] ret = new float[array.length];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = array[i];
		}
		return ret;
	}

	protected void addSeriesReuseRange(float[] xpoints, float[] ypoints, String name, int index, int startIndex, int endIndex, XYItemRenderer r)
	{
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		XYSeries xyseries = new XYSeries(name, true, false);
		xyseries.setDescription(name);
		float min_x = Float.MAX_VALUE;
		float max_x = Float.MIN_VALUE;
		float min_y = Float.MAX_VALUE;
		float max_y = Float.MIN_VALUE;

		StringBuffer sb = new StringBuffer();
		for (int i = startIndex; i < endIndex; i++)
		{
			if ( /* xpoints[i] != 0 && */!Float.isNaN(xpoints[i]) && !Float.isNaN(ypoints[i]))
			{
				try
				{
					xyseries.add(xpoints[i], ypoints[i]);
				}
				catch (org.jfree.data.general.SeriesException ex)
				{
					ex.printStackTrace();
				}
				min_x = min_x > xpoints[i] ? xpoints[i] : min_x;
				max_x = max_x < xpoints[i] ? xpoints[i] : max_x;
				min_y = min_y > ypoints[i] ? ypoints[i] : min_y;
				max_y = max_y < ypoints[i] ? ypoints[i] : max_y;
				sb.append(xpoints[i] + " " + ypoints[i] + "\n");
			}
		}
		area.setText(sb.toString());
		if (min_x != Float.MAX_VALUE || max_x != Float.MIN_VALUE)
		{
			NumberAxis domainAxis = getDomainAxis(getValueAxisLabel(), false);
			domainAxis.setRangeWithMargins(min_x, max_x);
			getPlot().setDomainAxis(domainAxis);
		}

		dataset.addSeries(xyseries);
		// getPlot().setRangeAxis(index, rangeAxis);
		getPlot().setDataset(index, dataset);
		getPlot().mapDatasetToRangeAxis(index, 0);
		getPlot().setRenderer(index, r);
		if (legend != null)
		{
			LegendItem legendItem = r.getLegendItem(index, 0);
			LegendItem item = new LegendItem(legendItem.getLabel(), legendItem.getDescription(), legendItem.getToolTipText(), legendItem.getURLText(), legendItem.getShape(), legendItem.getFillPaint());
			legend.add(item);
		}
	}

	protected void addSeries(float[] xpoints, float[] ypoints, String name, int index, int startIndex, int endIndex, boolean logScale, XYItemRenderer r)
	{
		NumberAxis rangeAxis = getRangeAxis(name, logScale);
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		XYSeries xyseries = new XYSeries(name, true, false);
		xyseries.setDescription(name);
		float min_x = Float.MAX_VALUE;
		float max_x = Float.MIN_VALUE;
		float min_y = Float.MAX_VALUE;
		float max_y = Float.MIN_VALUE;

		StringBuffer sb = new StringBuffer();
		for (int i = startIndex; i < endIndex; i++)
		{
			if ( /* xpoints[i] != 0 && */!Float.isNaN(xpoints[i]) && !Float.isNaN(ypoints[i]))
			{
				try
				{
					xyseries.add(xpoints[i], ypoints[i]);
				}
				catch (org.jfree.data.general.SeriesException ex)
				{
					ex.printStackTrace();
				}
				min_x = min_x > xpoints[i] ? xpoints[i] : min_x;
				max_x = max_x < xpoints[i] ? xpoints[i] : max_x;
				min_y = min_y > ypoints[i] ? ypoints[i] : min_y;
				max_y = max_y < ypoints[i] ? ypoints[i] : max_y;
				sb.append(xpoints[i] + " " + ypoints[i] + "\n");
			}
		}
		area.setText(sb.toString());
		if (min_x != Float.MAX_VALUE || max_x != Float.MIN_VALUE)
		{
			NumberAxis domainAxis = getDomainAxis(getValueAxisLabel(), false);
			domainAxis.setRangeWithMargins(min_x, max_x);
			getPlot().setDomainAxis(domainAxis);
		}
		if (min_y <= 0 && logScale)
		{
			rangeAxis = getRangeAxis(name, false);
		}
		dataset.addSeries(xyseries);
		getPlot().setRangeAxis(index, rangeAxis);
		getPlot().setDataset(index, dataset);
		getPlot().mapDatasetToRangeAxis(index, index);
		getPlot().setRenderer(index, r);
		if (legend != null)
		{
			LegendItem legendItem = r.getLegendItem(index, 0);
			LegendItem item = new LegendItem(legendItem.getLabel(), legendItem.getDescription(), legendItem.getToolTipText(), legendItem.getURLText(), legendItem.getShape(), legendItem.getFillPaint());
			legend.add(item);
		}
	}

	protected void addSeries(float[] xpoints, float[] ypoints, String name, int index, int startIndex, int endIndex, boolean logScaleRange, boolean logScaleDomain, XYItemRenderer r)
	{
		NumberAxis rangeAxis = getRangeAxis(name, logScaleRange);
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		XYSeries xyseries = new XYSeries(name, true, false);
		xyseries.setDescription(name);
		float min_x = Float.MAX_VALUE;
		float max_x = Float.MIN_VALUE;
		float min_y = Float.MAX_VALUE;
		float max_y = Float.MIN_VALUE;

		StringBuffer sb = new StringBuffer();
		for (int i = startIndex; i < endIndex; i++)
		{
			if ( /* xpoints[i] != 0 && */!Float.isNaN(xpoints[i]) && !Float.isNaN(ypoints[i]))
			{
				xyseries.add(xpoints[i], ypoints[i]);
				min_x = min_x > xpoints[i] ? xpoints[i] : min_x;
				max_x = max_x < xpoints[i] ? xpoints[i] : max_x;
				min_y = min_y > ypoints[i] ? ypoints[i] : min_y;
				max_y = max_y < ypoints[i] ? ypoints[i] : max_y;
				sb.append(xpoints[i] + " " + ypoints[i] + "\n");
			}
		}
		area.setText(sb.toString());
		if (min_x != Float.MAX_VALUE || max_x != Float.MIN_VALUE)
		{
			NumberAxis domainAxis = getDomainAxis(getValueAxisLabel(), min_x > 0 ? logScaleDomain : false);
			if (min_x > 0 && logScaleDomain)
			{
				domainAxis.setRange(min_x, max_x);
			}
			else
			{
				domainAxis.setRangeWithMargins(min_x, max_x);
			}
			getPlot().setDomainAxis(domainAxis);
		}
		if (min_y <= 0 && logScaleDomain)
		{
			rangeAxis = getRangeAxis(name, false);
		}
		dataset.addSeries(xyseries);
		getPlot().setDataset(index, dataset);
		getPlot().setRangeAxis(index, rangeAxis);
		getPlot().mapDatasetToRangeAxis(index, index);
		getPlot().setRenderer(index, r);
		if (legend != null)
		{
			LegendItem legendItem = r.getLegendItem(index, 0);
			LegendItem item = new LegendItem(legendItem.getLabel(), legendItem.getDescription(), legendItem.getToolTipText(), legendItem.getURLText(), legendItem.getShape(), legendItem.getFillPaint());
			legend.add(item);
		}
	}

	protected void addTrend(float[] points, String name, int index, float slope, float intercept, float uncertainty, XYItemRenderer r)
	{
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		XYSeries xyseries = new XYSeries(name, true, false);
		xyseries.setDescription(name);
		for (int i = 0; i < points.length; i++)
		{
			if (points[i] > 0)
			{
				float y = (float) Math.exp(slope * points[i] + intercept);
				if (!(Float.isNaN(y) || Float.isInfinite(y)))
				{
					xyseries.add(points[i], y);
				}
			}
		}
		dataset.addSeries(xyseries);
		getPlot().setDataset(index, dataset);
		getPlot().mapDatasetToRangeAxis(index, index - 1);
		getPlot().setRenderer(index, r);
		LegendItem legendItem = r.getLegendItem(index, 0);
		LegendItem item = new LegendItem(legendItem.getLabel() + " " + (Math.exp(slope) + "^order*" + intercept + " +- " + uncertainty), legendItem.getDescription(), legendItem.getToolTipText(), legendItem.getURLText(), legendItem.getShape(), legendItem.getFillPaint());
		legend.add(item);

	}

	protected abstract String getValueAxisLabel();

	protected String getRangeAxisLabel()
	{
		return getTitle();
	}

	protected abstract String getTitle();

	protected void reset()
	{
		rNr = null;
		rLg = null;
		if (supplier instanceof DefaultDrawingSupplier)
		{
			try
			{
				getPlot().setDrawingSupplier((DefaultDrawingSupplier) ((DefaultDrawingSupplier) supplier).clone());
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
		}
		for (int i = 0; i < getPlot().getDatasetCount(); i++)
		{
			getPlot().setDataset(i, null);
		}
		getPlot().clearDomainAxes();
		getPlot().clearRangeAxes();
		getPlot().setDomainAxis(getAxis(getValueAxisLabel(), false));
		getPlot().setRenderer(getShapeRenderer());
		getPlot().setRangeAxis(getAxis("", false));

	}
}
