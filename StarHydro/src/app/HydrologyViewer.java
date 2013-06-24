package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Point;

import javax.media.j3d.BranchGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import star.annotations.Handles;
import star.annotations.Preferences;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.event.Adapter;
import star.events.common.InitializeEvent;
import star.events.common.InitializeRaiser;
import star.hydro.tribs.TRibsForm;
import star.hydrology.events.ApplicableLayerEvent;
import star.hydrology.events.ApplicableLayerRaiser;
import star.hydrology.events.RenderableVisibleEvent;
import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import utils.Memory;
import app.layers.LayersTable;
import app.ui.HortonNumbers;
import app.ui.IUH;
import app.ui.PDFns;
import app.ui.Topography;
import app.ui.Watershed;
import app.ui.WidthFns;
import app.viewers.PDFViewer;
import app.viewers.ProjectedTerrainViewer;
import app.viewers.RainGaugeViewer;
import app.viewers.StreamOrderViewer;
import app.viewers.StreamsViewer;
import app.viewers.WatershedViewer;
import app.viewers.map.RegionalizationForm;
import app.worker.DrainageDenistyAndStreamFrequencyWorker;
import app.worker.MainstreamLengthWorker;
import app.worker.RainGaugeWorker;
import app.worker.StreamNetworkWorker;
import app.worker.StreamOrderStatisticsWorker;
import app.worker.WatershedDelineator;
import app.worker.map.FillWorker;
import app.worker.map.FlowAccomulationWorker;
import app.worker.map.FlowDirectionWorker;
import app.worker.map.FlowUpstramDirectionWorker;
import app.worker.map.ProjectionWorker;
import app.worker.map.WatershedWorker;
import app.worker.pdf.CurvaturePDFWorker;
import app.worker.pdf.ElevationPDFWorker;
import app.worker.pdf.RegionPointEliminatorWorker;
import app.worker.pdf.SlopePDFWorker;
import app.worker.pdf.TopIndexPDFWorker;
import app.worker.pdf.reg.RegionalizationChannelMapGenerator;
import app.worker.pdf.reg.RegionalizationExporter;
import app.worker.pdf.reg.RegionalizationWorkerColorer;
import app.worker.pdf.reg.RegionalizeWorker;
import app.worker.planar.HortonNumberGenerator;
import app.worker.relief.HypsometricCurveWorker;
import app.worker.relief.LinkConcentrationWorker;
import app.worker.widthfunction.GeometricWidthFunctionWorker;
import app.worker.widthfunction.StraightLineWidthFunctionWorker;
import app.worker.widthfunction.TopographicsWidthFunctionWorker;

@SignalComponent(extend = Panel.class, raises = InitializeRaiser.class)
@Preferences
@Properties(@Property(name = "hydrologyContainer", type = HydrologyContainer.class))
public class HydrologyViewer extends HydrologyViewer_generated
{
	private static final long serialVersionUID = 1L;
	private static JPanel status = null;

	private JComponent initAnalysisPanel()
	{
		final JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		pane.addTab("Watershed", new Watershed());
		pane.addTab("Horton ratios", new JScrollPane(new HortonNumbers(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		pane.addTab("Topography", new JScrollPane(new Topography()));
		pane.addTab("Width Fns", new JScrollPane(new WidthFns(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		pane.addTab("GIUH", new JScrollPane(new IUH(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		pane.addTab("PDFns", new JScrollPane(new PDFns()));
		pane.addTab("Regionalization", new JScrollPane(new RegionalizationForm()));
		pane.addTab("tRIBS animations", new JScrollPane(new TRibsForm()));

		for (int i = 0; i < pane.getTabCount(); i++)
		{
			pane.setToolTipTextAt(i, getPreferences().get("" + i, "No tooltip"));
		}

		final ApplicableLayer applicableLayer = new ApplicableLayer();
		getAdapter().addComponent(applicableLayer);

		pane.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				switch (((JTabbedPane) e.getSource()).getSelectedIndex())
				{
				case 0: // Basic
					applicableLayer.setLayers(ApplicableLayerRaiser.watershedSetup, new int[] { LayerConstants.TERRAIN, LayerConstants.FILLED, LayerConstants.WATERSHED, LayerConstants.STREAMS });
					break;
				case 1: // Planar
					applicableLayer.setLayers(ApplicableLayerRaiser.planar, new int[] { LayerConstants.TERRAIN, LayerConstants.WATERSHED, LayerConstants.STREAMS, LayerConstants.STREAMNETWORK });
					break;
				case 2: // Topography
					applicableLayer.setLayers(ApplicableLayerRaiser.topography, new int[] { LayerConstants.TERRAIN, LayerConstants.WATERSHED, LayerConstants.STREAMS, LayerConstants.STREAMNETWORK });
					break;
				case 3: // Relief
					applicableLayer.setLayers(ApplicableLayerRaiser.relief, new int[] { LayerConstants.WATERSHED, LayerConstants.STREAMNETWORK });
					break;
				case 4: // Hydrographic
					applicableLayer.setLayers(ApplicableLayerRaiser.hydrographic, new int[] { LayerConstants.WATERSHED, LayerConstants.STREAMS, LayerConstants.STREAMNETWORK, LayerConstants.SIMPLEIUHLAYER, LayerConstants.RAINGAUGELAYER });
					break;
				case 5: // PDF
					applicableLayer.setLayers(ApplicableLayerRaiser.pdf, new int[] { LayerConstants.WATERSHED, LayerConstants.SLOPEPDFLAYER, LayerConstants.CURVATUREPDFLAYER, LayerConstants.TOPINDEXPDFLAYER });
					break;
				case 6: // Regionalization
					applicableLayer.setLayers(ApplicableLayerRaiser.regionalization, new int[] { LayerConstants.WATERSHED, LayerConstants.REGLAYER, LayerConstants.REGDISCRETELAYER, LayerConstants.REGCHANNELLAYER });
					break;
				default:
					applicableLayer.setLayers(ApplicableLayerRaiser.other, new int[] { LayerConstants.TERRAIN });
					break;
				}
				;
			}

		});
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				applicableLayer.setLayers(ApplicableLayerRaiser.watershedSetup, new int[] { LayerConstants.TERRAIN, LayerConstants.FILLED, LayerConstants.WATERSHED, LayerConstants.STREAMS });
			}
		});
		return pane;
	}

	class ApplicableLayer implements ApplicableLayerRaiser
	{
		int[] layers = null;
		String name = null;

		public void setLayers(String name, int[] layers)
		{
			this.layers = layers;
			this.name = name;
			(new ApplicableLayerEvent(this)).raise();
		}

		public int[] getLayers()
		{
			return layers;
		}

		public String getName()
		{
			return name;
		}

		public void addNotify()
		{
		}

		Adapter adapter = new Adapter(this);

		public Adapter getAdapter()
		{
			return adapter;
		}

		public void removeNotify()
		{
		}

	}

	private void initUI()
	{
		getHydrologyContainer().getStarContainer().setLayout(new BorderLayout());

		final JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT, initAnalysisPanel(), initLayers());
		p.setOneTouchExpandable(true);
		add(BorderLayout.WEST, p);
		setBackground(Color.black);

		final HydrologyViewer self = this;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				(new InitializeEvent(self)).raise();
				p.setDividerLocation(.6f);
			}
		});
	}

	JPanel init2D()
	{
		JPanel f = new JPanel();
		HydrologyViewer2D v2d = new HydrologyViewer2D();
		f.setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(v2d);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getViewport().setViewPosition(new Point(0, 0));
		f.add(BorderLayout.CENTER, scroll);
		f.add(BorderLayout.WEST, v2d.getSlider());
		f.setVisible(true);
		getAdapter().addComponent(v2d);
		return f;
	}

	private Component initLayers()
	{
		LayersTable table = new LayersTable();
		getAdapter().addComponent(table);
		return table.getPanel();
	}

	public void addComponents()
	{
		getAdapter().addComponent(new ProjectionWorker());
		getAdapter().addComponent(new FillWorker());
		getAdapter().addComponent(new FlowDirectionWorker());
		getAdapter().addComponent(new FlowAccomulationWorker());
		getAdapter().addComponent(new ProjectedTerrainViewer());
		getAdapter().addComponent(new StreamsViewer());
		getAdapter().addComponent(new WatershedViewer());
		getAdapter().addComponent(new RainGaugeViewer());
		getAdapter().addComponent(new WatershedDelineator());
		getAdapter().addComponent(new RainGaugeWorker());

		getAdapter().addComponent(new WatershedWorker());
		getAdapter().addComponent(new StreamNetworkWorker());
		getAdapter().addComponent(new StreamOrderStatisticsWorker());
		getAdapter().addComponent(new HortonNumberGenerator());
		getAdapter().addComponent(new DrainageDenistyAndStreamFrequencyWorker());
		getAdapter().addComponent(new MainstreamLengthWorker());
		getAdapter().addComponent(new TopographicsWidthFunctionWorker());
		getAdapter().addComponent(new GeometricWidthFunctionWorker());
		getAdapter().addComponent(new StraightLineWidthFunctionWorker());
		getAdapter().addComponent(new HypsometricCurveWorker());
		getAdapter().addComponent(new LinkConcentrationWorker());
		getAdapter().addComponent(new StreamOrderViewer());
		getAdapter().addComponent(new ElevationPDFWorker());
		getAdapter().addComponent(new SlopePDFWorker());
		getAdapter().addComponent(new CurvaturePDFWorker());
		getAdapter().addComponent(new TopIndexPDFWorker());
		getAdapter().addComponent(new FlowUpstramDirectionWorker());

		getAdapter().addComponent(new RegionalizeWorker());
		getAdapter().addComponent(new RegionPointEliminatorWorker());
		getAdapter().addComponent(new PDFViewer(LayerConstants.CURVATUREPDFLAYER, "Curvature"));
		getAdapter().addComponent(new PDFViewer(LayerConstants.SLOPEPDFLAYER, "Slope"));
		getAdapter().addComponent(new PDFViewer(LayerConstants.TOPINDEXPDFLAYER, "Top index"));
		getAdapter().addComponent(new PDFViewer(LayerConstants.REGLAYER, "Reg Layer"));
		getAdapter().addComponent(new PDFViewer(LayerConstants.REGDISCRETELAYER, "Reg Color Layer"));
		getAdapter().addComponent(new PDFViewer(LayerConstants.REGCHANNELLAYER, "Reg Channel Layer"));

		getAdapter().addComponent(new RegionalizationWorkerColorer());
		getAdapter().addComponent(new RegionalizationChannelMapGenerator());
		getAdapter().addComponent(new RegionalizationExporter());

	}

	public void addNotify()
	{
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		super.addNotify();
		addComponents();

		getAdapter().addHandled(RenderableVisibleEvent.class);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1024, 768));
		initializeStatusbar();
		initializeViewer();
		initUI();
		doLayout();
	}

	@Handles(raises = {})
	protected void updateBranchGroup(RenderableVisibleRaiser r)
	{
		// TODO: this is place where stuff comes...
		if (r.getAction() == RenderableVisibleRaiser.REPLACE)
		{
			if (r.getOldBranchGroup() != null)
			{
				getHydrologyContainer().getStarContainer().removeBranchGroup((BranchGroup) r.getOldBranchGroup());
			}
			if (r.getBranchGroup() != null)
			{
				getHydrologyContainer().getStarContainer().addBranchGroup((BranchGroup) r.getBranchGroup());
			}
		}
		if (r.getAction() == RenderableVisibleRaiser.ADD)
		{
			if (r.getBranchGroup() != null)
			{
				getHydrologyContainer().getStarContainer().addBranchGroup((BranchGroup) r.getBranchGroup());
			}
		}
		else if (r.getAction() == RenderableVisibleRaiser.REMOVE)
		{
			if (r.getBranchGroup() != null)
			{
				getHydrologyContainer().getStarContainer().removeBranchGroup((BranchGroup) r.getBranchGroup());
			}
			if (r.getOldBranchGroup() != null)
			{
				getHydrologyContainer().getStarContainer().removeBranchGroup((BranchGroup) r.getOldBranchGroup());
			}
		}
	}

	private void initializeStatusbar()
	{
		status = new JPanel();
		utils.Runner.getLabel().setText("Initializing...");
		status.add(PickerBehaviour.getLabel());
		status.add(utils.Runner.getLabel());
		utils.Runner.getLabel().setPreferredSize(new Dimension(200, 34));
		status.invalidate();
		status.setMinimumSize(new Dimension(34, 34));
		status.add(new Memory(JProgressBar.HORIZONTAL));
		add(BorderLayout.SOUTH, status);
	}

	private void initializeViewer()
	{
		HydrologyContainer viewer = new HydrologyContainer();
		setHydrologyContainer(viewer);
		viewer.setVisible(true);
		JTabbedPane tabbed = new JTabbedPane();
		//add(BorderLayout.CENTER, tabbed);
		//tabbed.addTab("3D", viewer);
		//tabbed.addTab("2D", init2D());
		add(BorderLayout.CENTER,viewer);
	}
}
