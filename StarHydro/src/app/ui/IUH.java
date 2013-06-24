package app.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mit.awt.event.ActionEvent;
import mit.awt.event.ActionRaiser;
import mit.swing.xJTextField;
import net.miginfocom.swing.MigLayout;
import star.annotations.Handles;
import star.annotations.Preferences;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.events.common.InitializeRaiser;
import star.hydro.rainfall.Convolute;
import star.hydro.rainfall.ConvoluteChart;
import star.hydro.rainfall.ConvolutionChartBinsEvent;
import star.hydro.rainfall.ConvolutionChartBinsRaiser;
import star.hydro.rainfall.ConvolutionPaletteEvent;
import star.hydro.rainfall.ConvolutionPaletteRaiser;
import star.hydro.rainfall.OpenRainfall;
import star.hydro.rainfall.RainfallPanel;
import star.hydro.rainfall.TimeSeries;
import star.hydrology.events.GridStatisticsProviderChangeEvent;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.giuh.GIUHVelocitiesEvent;
import star.hydrology.events.giuh.GIUHVelocitiesRaiser;
import star.hydrology.ui.chart.SimpleIUHChart;
import star.hydrology.ui.layers.LogAdjustPanel;
import star.hydrology.ui.palette.GrayPalette;
import star.hydrology.ui.palette.LogGrayPalette;
import star.hydrology.ui.palette.Palette;
import star.hydrology.ui.palette.PaletteCanvas;
import star.hydrology.ui.palette.RangePalette;
import star.hydrology.ui.palette.RapidLogPalette;
import star.hydrology.ui.palette.SingleColorPalette;
import star.hydrology.ui.palette.StepPalette;
import app.viewers.SimpleIUHViewer;
import app.worker.SimpleIUH;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

@Preferences
@SignalComponent(extend = JPanel.class, contains = { ActionEvent.class, GridStatisticsProviderChangeEvent.class }, excludeExternal = { ActionEvent.class }, raises = { ConvolutionPaletteRaiser.class, ConvolutionChartBinsRaiser.class })
@Properties( { @Property(name = "hillslopeVelocity", type = float.class, getter = Property.PUBLIC), @Property(name = "channelVelocity", type = float.class, getter = Property.PUBLIC) })
public class IUH extends IUH_generated
{
	private JComboBox paletteSelector = null;
	private xJTextField channelRoutingVelocity;
	private xJTextField hillslopeRoutingVelocity;
	private JButton loadRainfall;
	private LogAdjustPanel slider;
	private TimeSeries ts = null;
	private static final long serialVersionUID = 1L;
	private boolean debug = false;
	private Palette convolutionPalette;

	private void setConvolutionPalette(Palette p)
	{
		convolutionPalette = p;
		(new ConvolutionPaletteEvent(this)).raise();
	}

	private JSlider convolutionBins = new JSlider(10, 250, 100);

	public int getNumberOfConvolutionChartBins()
	{
		return convolutionBins.getValue();
	}

	public Palette getConvolutionPalette()
	{
		return convolutionPalette;
	}

	private void initPaletteSelector()
	{
		if (paletteSelector == null)
		{
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement(new GrayPalette(Color.GREEN, "Gray/Green"));
			model.addElement(new GrayPalette(Color.WHITE, "Gray/White"));
			model.addElement(new RangePalette(Color.RED, Color.GREEN, "Range/Red-Green"));
			model.addElement(new GrayPalette(Color.RED, "Gray/Red"));
			model.addElement(new GrayPalette(Color.BLUE, "Gray/Blue"));
			model.addElement(new LogGrayPalette(Color.BLUE, "LogGray/Blue"));
			model.addElement(new RapidLogPalette(Color.BLUE, "Rapid/Blue"));
			model.addElement(new SingleColorPalette(Color.BLUE, "Single/Blue"));
			model.addElement(new SingleColorPalette(Color.CYAN, "Single/Cyan"));
			model.addElement(new SingleColorPalette(Color.YELLOW, "Single/Yellow"));
			model.addElement(new SingleColorPalette(Color.RED, "Single/Red"));
			model.addElement(new RangePalette(Color.RED, Color.RED.darker().darker(), "Range/Red-DarkRed"));
			model.addElement(new RangePalette(Color.RED, Color.GREEN, "Range/Red-Green"));
			model.addElement(new RangePalette(Color.GREEN, Color.RED, "Range/Green-Red"));
			model.addElement(new RangePalette(Color.DARK_GRAY, Color.GREEN.brighter(), "Range/Gray-Green"));
			model.addElement(new StepPalette("Step"));
			paletteSelector = new JComboBox();
			paletteSelector.setModel(model);
			paletteSelector.setPreferredSize(new Dimension(200, 20));
			paletteSelector.setRenderer(new ListCellRenderer()
			{
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
				{
					PaletteCanvas c = new PaletteCanvas((Palette) value);
					return c;
				}
			});
			paletteSelector.addActionListener(new ActionListener()
			{

				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					setConvolutionPalette((Palette) paletteSelector.getModel().getSelectedItem());
					raiseConvolutionListener();
				}
			});
		}
	}

	public void addNotify()
	{
		super.addNotify();
		initPaletteSelector();

		ts = new TimeSeries();
		SimpleIUHViewer viewer = new SimpleIUHViewer();

		getAdapter().addComponent(viewer);
		getAdapter().addComponent(new SimpleIUH());
		getAdapter().addComponent(ts);
		getAdapter().addComponent(new Convolute());

		channelRoutingVelocity = new xJTextField();
		hillslopeRoutingVelocity = new xJTextField();
		channelRoutingVelocity.setAlignmentX(1f);
		hillslopeRoutingVelocity.setAlignmentX(1f);
		slider = new LogAdjustPanel(0, true);
		slider.setMinimum(.01f);
		slider.setMaximum(100f);

		loadRainfall = new JButton("Filename...");
		loadRainfall.addActionListener(new ActionListener()
		{

			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				OpenRainfall map = new OpenRainfall();
				Project p = map.getProject(loadRainfall);
				loadRainfall.setText(p.getName());
				try
				{
					TimeSeries series = (TimeSeries) p.getParsed();
					ts.setTimeSeries(series);
				}
				catch (ProjectException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		setChannelVelocity(getPreferences().getInt("channelvelocity", 750));
		setHillslopeVelocity(getPreferences().getInt("hillslopevelocity", 75));
		handleAction(this);
		JPanel ret = this;
		MigLayout layout = new MigLayout(debug ? "debug 1000" : "wrap 5");
		ret.setLayout(layout);
		ret.add(new MyLabel("GIUH Chart"), "span 5,growx, wrap");
		ret.add(new SimpleIUHChart(), "span 5, wrap");
		ret.add(new JLabel("Channel routing velocity"), "span 2");
		ret.add(channelRoutingVelocity, "growx, span 2");
		ret.add(new JLabel("<html><sup>m</sup>/<sub>hour</sub></html>"), "wrap");
		ret.add(new JLabel("Overland flow routing velocity"), "span 2");
		ret.add(hillslopeRoutingVelocity, "growx, span 2");
		ret.add(new JLabel("<html><sup>m</sup>/<sub>hour</sub></html>"), "wrap");
		ret.add(new JLabel("Overland flow/channel routing velocity"), "span 2");
		ret.add(slider, "wrap, span 2");
		// ret.add(new MyLabel("Rain fall data"), "span 5,growx, wrap");
		// ret.add(new JLabel("Current data:"));
		// ret.add(loadRainfall, "span 2, growx, wrap");
		ret.add(new RainfallPanel(), "span 5,  wrap");
		ret.add(new MyLabel("Convolution"), "span 5,growx, wrap");
		// ret.add(new JLabel("Select first rainfall point: "), "span 2");
		// ret.add(paletteSelector, "span 3,wrap");
		// ret.add(new JLabel("Select number of points in convolution chart: "), "span 2");
		// ret.add(convolutionBins, "span 3,wrap");
		ret.add(new ConvoluteChart(), "span 5,wrap");
		convolutionBins.setPaintTicks(true);
		convolutionBins.setPaintTrack(true);
		convolutionBins.setMajorTickSpacing(50);
		convolutionBins.setMinorTickSpacing(10);
		convolutionBins.setPaintLabels(true);

		ts.defaultDelta();

		convolutionBins.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				raiseConvolutionListener();
			}
		});
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				setConvolutionPalette((Palette) paletteSelector.getModel().getSelectedItem());
			}
		});
	}

	private  void raiseConvolutionListener()
	{
		(new ConvolutionChartBinsEvent(this)).raise();
	}

	@Handles(raises = { GIUHVelocitiesRaiser.class })
	void handleInit(InitializeRaiser r)
	{
		(new GIUHVelocitiesEvent(this)).raise();
	}

	@Handles(raises = { GIUHVelocitiesRaiser.class })
	void handleAction(ActionRaiser r)
	{
		System.out.println("Action start");
		setChannelVelocity(Float.parseFloat(channelRoutingVelocity.getText()));
		channelRoutingVelocity.setText(String.valueOf(getChannelVelocity()));
		setHillslopeVelocity(Float.parseFloat(hillslopeRoutingVelocity.getText()));
		hillslopeRoutingVelocity.setText(String.valueOf(getHillslopeVelocity()));
		(new GIUHVelocitiesEvent(this)).raise();
	}

	@Handles(raises = {})
	void handleAction(GIUHVelocitiesRaiser r)
	{
		setChannelVelocity(r.getChannelVelocity());
		channelRoutingVelocity.setText(String.valueOf(getChannelVelocity()));
		setHillslopeVelocity(r.getHillslopeVelocity());
		hillslopeRoutingVelocity.setText(String.valueOf(getHillslopeVelocity()));
		slider.updateCurrent(getHillslopeVelocity() / getChannelVelocity());
	}

	@Handles(raises = {})
	void handleSlider(GridStatisticsProviderChangeRaiser r)
	{
		setHillslopeVelocity(getChannelVelocity() * slider.getCurrent());
		(new GIUHVelocitiesEvent(this)).raise();
	}
}
