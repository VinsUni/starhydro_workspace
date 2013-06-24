package app.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileOutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.vecmath.Point3f;

import mit.awt.event.ActionRaiser;
import mit.swing.xJToggleButton;
import net.miginfocom.swing.MigLayout;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.AdjustableValue;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.events.AdjustableValueRaiser;
import star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser;
import star.hydrology.events.GridStatisticsProviderChangeEvent;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.HypsometricCurveRaiser;
import star.hydrology.events.MainStreamLengthRaiser;
import star.hydrology.events.Pick3DEvent;
import star.hydrology.events.Pick3DRaiser;
import star.hydrology.events.Select3DWatershedOriginRaiser;
import star.hydrology.events.SelectWatershedOriginEvent;
import star.hydrology.events.SelectWatershedOriginRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;
import star.hydrology.events.StreamRootChangeRaiser;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;
import utils.Format;
import app.worker.streamnetwork.Stream;

@SignalComponent(extend = JPanel.class, raises = { GridStatisticsProviderChangeRaiser.class })
public class Watershed extends Watershed_generated
{
	boolean debug = false;

	private static final long serialVersionUID = 1L;
	private xJToggleButton setWatershedButton = new xJToggleButton();
	private JButton saveWatershedButton = new JButton("Export watershed layer");
	private boolean state = true;
	private float length = Float.NaN;
	private float area = Float.NaN;

	private Point3f point;
	private float current = Float.NaN;
	private float cellSize = 0;
	private JLabel outletLabelX = new JLabel();
	private JLabel outletLabelY = new JLabel();
	private JLabel outletLabelUnits = new JLabel();
	private JLabel minHeightLabel = new JLabel();
	private JLabel maxHeightLabel = new JLabel();
	private JLabel areaLabel = new JLabel();
	private JLabel areaUnits = new JLabel();
	private JLabel mainstreamLength = new JLabel();
	private JLabel mainstreamUnits = new JLabel();
	private JLabel totalstreamLength = new JLabel();
	private JLabel totalstreamUnits = new JLabel();

	private JLabel mapCenterX = new JLabel();
	private JLabel mapCenterY = new JLabel();
	private JLabel mapCellX = new JLabel();
	private JLabel mapCellY = new JLabel();
	private JLabel mapDimX = new JLabel();
	private JLabel mapDimY = new JLabel();

	private JLabel larLabel = new JLabel();
	private JLabel larUnit = new JLabel();
	private JLabel streamFrequencyLabel = new JLabel();
	private JLabel drainageDensityLabel = new JLabel();
	private JTextField thresholdValue = new JTextField(8);
	private JLabel streamFrequencyUnit = new JLabel();
	private JLabel drainageDensityUnit = new JLabel();

	private void setTooltip(boolean state)
	{
		if (state)
		{
			setWatershedButton.setToolTipText("Now click on the map to select watershed outlet.");
		}
		else
		{
			setWatershedButton.setToolTipText("Activate button and than click on the map to select watershed outlet.");
		}

	}

	private JPanel getForm2(JPanel panel)
	{
		MigLayout layout = new MigLayout(debug ? "debug 1000,fillx" : "fillx ");
		panel.setLayout(layout);
		panel.add(new MyLabel("DEM Info"), "span 6,growx, wrap");

		panel.add(new JLabel("Map center (long lat):"), "span 2");
		panel.add(mapCenterX, "span 1");
		panel.add(new JLabel(" "), "span 1");
		panel.add(mapCenterY, "span 1");
		panel.add(new JLabel("m"), "span 1,wrap");

		panel.add(new JLabel("Map size (width height):"), "span 2");
		panel.add(mapDimX, "span 1");
		panel.add(new JLabel(" "), "span 1");
		panel.add(mapDimY, "span 1");
		panel.add(new JLabel("m"), "span 1,wrap");

		panel.add(new JLabel("Map cell size (width height):"), "span 2");
		panel.add(mapCellX, "span 1");
		panel.add(new JLabel(" "), "span 1");
		panel.add(mapCellY, "span 1");
		panel.add(new JLabel("m"), "span 1,wrap");

		panel.add(new MyLabel("Watershed setup"), "span 6, growx, wrap");
		panel.add(new JLabel("Watershed outlet (long lat):"), "span 2");
		panel.add(outletLabelX, "span 1");
		panel.add(new JLabel(" "), "span 1");
		panel.add(outletLabelY, "span 1");
		panel.add(new JLabel("m"), "span 1,wrap");
		panel.add(setWatershedButton, "span 6,growx,wrap");

		panel.add(new MyLabel("Watershed statistics"), "span 6, growx, wrap");
		panel.add(new JLabel("Height range"), "span 2");
		panel.add(minHeightLabel, "span 1");
		panel.add(new JLabel(" "), "span 1");
		panel.add(maxHeightLabel, "span 1");
		panel.add(new JLabel("m"), "span 1,wrap");

		panel.add(new JLabel("Area"), "span 2");
		panel.add(new JLabel(" "), "span 2");
		panel.add(areaLabel, "span 1");
		panel.add(areaUnits, "span 1,wrap");

		panel.add(new JLabel("Main stream length"), "span 2");
		panel.add(new JLabel(" "), "span 2");
		panel.add(mainstreamLength, "span 1");
		panel.add(mainstreamUnits, "span 1,wrap");

		panel.add(new JLabel("Total stream length"), "span 2");
		panel.add(new JLabel(" "), "span 2");
		panel.add(totalstreamLength, "span 1");
		panel.add(totalstreamUnits, "span 1,wrap");

		panel.add(new JLabel("Length and area relationship"), "span 2");
		panel.add(new JLabel(" "), "span 2");
		panel.add(larLabel, "span 1");
		panel.add(larUnit, "span 1,wrap");

		panel.add(new JLabel("Stream frequency"), "span 2");
		panel.add(new JLabel(" "), "span 2");
		panel.add(streamFrequencyLabel, "span 1");
		panel.add(streamFrequencyUnit, "span 1,wrap");

		panel.add(new JLabel("Drainage density"), "span 2");
		panel.add(new JLabel(" "), "span 2");
		panel.add(drainageDensityLabel, "span 1");
		panel.add(drainageDensityUnit, "span 1,wrap");

		panel.add(saveWatershedButton, "span 1");

		saveWatershedButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser choose = new JFileChooser();
				int result = choose.showSaveDialog(saveWatershedButton);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						java.io.File f = choose.getSelectedFile();
						if (watershed != null && watershed.getDataset() != null && watershed.getDataset() instanceof FloatDataset)
						{
							FileOutputStream fos = new FileOutputStream(f);
							fos.write(((FloatDataset) watershed.getDataset()).dumpAscii().getBytes());
							fos.close();
						}
						else
						{
							JOptionPane.showMessageDialog(saveWatershedButton, "Failed to save watershed");
						}
					}
					catch (Exception e1)
					{
						JOptionPane.showMessageDialog(saveWatershedButton, "Failed to save watershed! Reason:" + e1.getMessage());
					}

				}
			}
		});
		return panel;
	}

	private void initWatershedButton()
	{
		// setWatershedButton.setText("Set watershed outlet point");
		setWatershedButton.setText("Double-click on the map to delineate watershed");
		setWatershedButton.setSelected(state);
		setWatershedButton.setEnabled(false);
		(new Pick3DEvent(this)).raise();
	}

	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setTooltip(state);
		ToolTipManager.sharedInstance().setInitialDelay(0);

		getForm2(this);

		initThresholdField();
		initWatershedButton();
	}

	private void setPoint(Point3f point)
	{
		this.point = point;
		if (point != null)
		{
			(new SelectWatershedOriginEvent(this)).raise();
		}
		else
		{
			(new SelectWatershedOriginEvent(this, false)).raise();
		}
	}

	public Point3f getPoint()
	{
		return point;
	}

	private PaletteRenderableLayer watershed = null;

	@Handles(raises = {})
	void getWatershed(WatershedLayerRaiser r)
	{
		watershed = r.getLayer();
	}

	@Handles(raises = {})
	void getMap(FilledMapLayerRaiser r)
	{
		Grid g = r.getLayer().getDataset();
		mapCellX.setText("" + g.getCellsize());
		mapCellY.setText("" + g.getCellsize());
		mapDimX.setText("" + g.getCellsize() * g.getCols());
		mapDimY.setText("" + g.getCellsize() * g.getRows());
		if (g instanceof GridwStat)
		{
			Point3f center = ((GridwStat) g).getCenter();
			mapCenterX.setText("" + center.x);
			mapCenterY.setText("" + center.y);
		}
		else
		{
			mapCenterX.setText("");
			mapCenterY.setText("");
		}
	}

	@Handles(raises = { SelectWatershedOriginRaiser.class })
	void selectPoint(Select3DWatershedOriginRaiser raiser)
	{
		if (state)
		{
			setWatershedButton.setSelected(state);
			setPoint(raiser.getPoint());
		}
		setTooltip(state);
	}

	@Handles(raises = { Pick3DRaiser.class })
	void select(ActionRaiser raiser)
	{
		state = setWatershedButton.isSelected();
		if (state)
		{
			(new Pick3DEvent(this)).raise();
		}
		setTooltip(state);
	}

	@Handles(raises = { SelectWatershedOriginRaiser.class })
	void reset(UnprojectedMapChangedRaiser raiser)
	{
		setWatershedButton.setSelected(state);
		setPoint(null);
		setTooltip(state);
	}

	@Handles(raises = {}, handleValid = false)
	void resetStreamRoot(StreamRootChangeRaiser raiser)
	{
		setOutletPoint(new float[] { Float.NaN, Float.NaN, Float.NaN });
		saveWatershedButton.setEnabled(false);
	}

	@Handles(raises = {})
	void setStreamRoot(StreamRootChangeRaiser raiser)
	{
		Stream stream = raiser.getStreamRoot();
		if (stream != null)
		{
			setOutletPoint(stream.getParts().get(0).getPoint(0));
			saveWatershedButton.setEnabled(true);
		}
		else
		{
			setOutletPoint(new float[] { Float.NaN, Float.NaN, Float.NaN });
			saveWatershedButton.setEnabled(false);
		}
	}

	private void setOutletPoint(float[] f)
	{
		outletLabelX.setText("" + (int) f[0]);
		outletLabelY.setText("" + (int) f[1]);
		outletLabelUnits.setText("m");
	}

	@Handles(raises = {}, handleValid = false)
	void resetHeights(HypsometricCurveRaiser raiser)
	{
		setHeights(Float.NaN, Float.NaN);
	}

	@Handles(raises = {})
	void setHeights(HypsometricCurveRaiser raiser)
	{
		setHeights(raiser.getMinimumHeight(), raiser.getMaximumHeight());
	}

	private void setHeights(float min, float max)
	{
		if (Float.isNaN(min) || Float.isNaN(max))
		{
			minHeightLabel.setText("");
			maxHeightLabel.setText("");
		}
		else
		{
			minHeightLabel.setText(Format.formatNumber(min));
			maxHeightLabel.setText(Format.formatNumber(max));
		}
	}

	@Handles(raises = {}, handleValid = false)
	void resetArea(StreamRootChangeRaiser raiser)
	{
		setArea(Float.NaN);
	}

	@Handles(raises = {})
	void setArea(StreamRootChangeRaiser raiser)
	{
		Stream stream = raiser.getStreamRoot();
		setArea(stream.getArea());
	}

	private void setArea(float area)
	{
		String strUnit;
		String strArea;
		if (Float.isNaN(area))
		{
			strUnit = "";
			strArea = "";
		}
		else if (area > 1000000 * 10)
		{
			strUnit = "<html>km<sup>2</sup><sub>&nbsp;</sub></html>";
			;
			strArea = Format.formatNumber(area / 1000000);
		}
		else
		{
			strUnit = "<html>m<sup>2</sup><sub>&nbsp;</sub></html>";
			strArea = Format.formatNumber(area);
		}
		areaLabel.setText(strArea);
		areaUnits.setText(strUnit);
		this.area = area;
		setLengthAreaRelationship();
	}

	@Handles(raises = {}, handleValid = false)
	void resetLength(MainStreamLengthRaiser raiser)
	{
		setLength(Float.NaN);
	}

	@Handles(raises = {})
	void setLength(MainStreamLengthRaiser raiser)
	{
		setLength(raiser.getLength());
	}

	private void setLength(float length)
	{
		String strUnit;
		String strLength;
		if (length > 1000 * 10)
		{
			strUnit = "km";
			strLength = Format.formatNumber(length / 1000);
		}
		else
		{
			strUnit = "m";
			strLength = Format.formatNumber((int) length);
		}
		mainstreamLength.setText(strLength);
		mainstreamUnits.setText(strUnit);
		this.length = length;
		setLengthAreaRelationship();
	}

	private void setTotalLength(float length)
	{
		String strUnit;
		String strLength;
		if (length > 1000 * 10)
		{
			strUnit = "km";
			strLength = Format.formatNumber(length / 1000);
		}
		else
		{
			strUnit = "m";
			strLength = Format.formatNumber((int) length);
		}
		totalstreamLength.setText(strLength);
		totalstreamUnits.setText(strUnit);
		setLengthAreaRelationship();
	}

	private void setLengthAreaRelationship()
	{
		try
		{
			float value = area / length / length;
			larLabel.setText(Format.formatNumber(value));
			larUnit.setText("<html><sup>m</sup>/<sub>m<sup>2</sup></sub>");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Handles(raises = {}, handleValid = false)
	void resetStreamFrequency(DrainageDensityAndStreamFrequencyRaiser raiser)
	{
		setStreamFrequency(Float.NaN);
	}

	@Handles(raises = {})
	void setStreamFrequency(DrainageDensityAndStreamFrequencyRaiser raiser)
	{
		setStreamFrequency(raiser.getStreamFrequency());
	}

	private void setStreamFrequency(float value)
	{
		String unit = "";
		String val = "";
		if (!Float.isNaN(value))
		{
			if (value > 0.001 && value < 1000)
			{
				unit = "";
				val = Format.formatNumber(value);
			}
			else if (value > 1000)
			{
				unit = "000";
				val = Format.formatNumber(value / 1000);
			}
			else if (value < 0.001)
			{
				int exp = (int) -Math.log10(value) + 1;
				unit = "<html><sup>1</sup>/<sub>m<sup>2</sup></sub></html>";
				val = "<html>" + Format.formatNumber(value * Math.pow(10, exp)) + "*10<sup>-" + exp + "</sup><sub>&nbsp;</sub></html>";
			}
		}
		streamFrequencyUnit.setText(unit);
		streamFrequencyLabel.setText(val);
	}

	@Handles(raises = {}, handleValid = false)
	void resetDrainageDensity(DrainageDensityAndStreamFrequencyRaiser raiser)
	{
		setDrainageDensity(Float.NaN);
	}

	@Handles(raises = {})
	void setDrainageDensity(DrainageDensityAndStreamFrequencyRaiser raiser)
	{
		setDrainageDensity(raiser.getDrainageDensity());
	}

	@Handles(raises = {})
	void setTotalStreamLength(StreamOrderStatisticsRaiser r)
	{
		float length = 0;
		float[] len = r.getOrderLength();
		int[] cnt = r.getOrderCount();
		for (int i = 0; i < len.length; i++)
		{
			length += len[i] * cnt[i];
		}
		setTotalLength(length);
	}

	private void setDrainageDensity(float value)
	{
		drainageDensityLabel.setText(Float.toString(value * 1000000));
		drainageDensityUnit.setText("<html><sup>m</sup>/<sub>km<sup>2</sup></sub>");
	}

	@Handles(raises = {})
	void setAccumulationTreshold(GridStatisticsProviderChangeRaiser r, FilledMapLayerRaiser map)
	{
		if (r.getKind() == LayerConstants.STREAMS && r != this)
		{
			cellSize = map.getLayer().getDataset().getCellsize();
			setThresholdValue(r.getCurrent(), cellSize);
		}
	}

	@Handles(raises = {})
	void initAccumulationTreshold(AdjustableValueRaiser raiser, FilledMapLayerRaiser map)
	{
		if (raiser instanceof AdjustableValueRaiser && raiser.getKind() == getKind())
		{
			AdjustableValue r = (AdjustableValue) raiser;
			cellSize = map.getLayer().getDataset().getCellsize();
			setThresholdValue(r.getAdjustableValue(), cellSize);
		}
	}

	private void setThresholdValue(float value, float cellSize)
	{
		float area = value * cellSize * cellSize;
		thresholdValue.setText("" + area);
		thresholdValue.setEnabled(true);
	}

	private void parseThresholdArea()
	{
		float val = Float.parseFloat(thresholdValue.getText());
		if (cellSize != 0)
		{
			float value = val / cellSize / cellSize;
			if (current != value)
			{
				current = value;
				(new GridStatisticsProviderChangeEvent(this)).raise();
			}
		}
	}

	private void initThresholdField()
	{
		thresholdValue.setEnabled(false);
		thresholdValue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				parseThresholdArea();
			}
		});
		thresholdValue.addFocusListener(new FocusListener()
		{

			public void focusGained(FocusEvent e)
			{
			}

			public void focusLost(FocusEvent e)
			{
				parseThresholdArea();
			}
		});

	}

	public float getCurrent()
	{
		return current;
	}

	public int getKind()
	{
		return LayerConstants.STREAMS;
	}

}
