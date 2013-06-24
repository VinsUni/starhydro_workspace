package app.viewers.map;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mit.swing.xJButton;
import mit.swing.event.ChangeEvent;
import star.annotations.Handles;
import star.annotations.Preferences;
import star.annotations.SignalComponent;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.RegionalizationEvent;
import star.hydrology.events.map.RegionalizationRaiser;
import star.hydrology.events.map.RegionalizationStatisticsRaiser;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@Preferences
@SignalComponent(extend = JPanel.class, contains = { ChangeEvent.class, ActionEvent.class }, excludeExternal = { ChangeEvent.class, ActionEvent.class })
public class RegionalizationForm extends RegionalizationForm_generated
{

	private static final long serialVersionUID = 1L;
	private RegionalizationModel parameters = new RegionalizationModel();
	private RegionalizationModel revert_to = null;

	private HashMap<RegionalizationRaiser.Parameters, Component[]> components = new HashMap<RegionalizationRaiser.Parameters, Component[]>();
	private HashMap<RegionalizationRaiser.Parameters, JCheckBox> enabled = new HashMap<RegionalizationRaiser.Parameters, JCheckBox>();
	private HashMap<RegionalizationRaiser.Parameters, JLabel> impact = new HashMap<RegionalizationRaiser.Parameters, JLabel>();

	private JButton submit = new xJButton("Recalculate");
	private JButton cancel = new xJButton("Revert values");

	@Handles(raises = { RegionalizationRaiser.class })
	void buttons(mit.awt.event.ActionRaiser event)
	{
		if (event.getActionEvent().getSource() == submit)
		{
			System.out.println("Submit");
			revert_to = parameters;
			cancel.setEnabled(true);
			raiseEvent();
		}
		else if (event.getActionEvent().getSource() == cancel)
		{
			System.out.println("Cancel");
			if (revert_to != null)
			{
				parameters = revert_to;
				updateForm();
				cancel.setEnabled(false);
			}
		}
	}

	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(getForm());
		JPanel buttons = new JPanel();
		add(buttons);
		buttons.add(submit);
		buttons.add(cancel);
		add(new Chart());
	}

	private void raiseEvent()
	{
		(new RegionalizationEvent(this)).raise();
	}

	private void updateForm()
	{
		System.out.println("//This will reload form with last data");
	}

	private static RegionalizationForm f;

	public static void main(String[] args)
	{
		f = new RegionalizationForm();
		final JFrame fr = new JFrame();
		fr.add(f);
		fr.setVisible(true);
		fr.pack();
	}

	private Component getForm()
	{
		FormLayout layout = new FormLayout("10dlu, pref:grow, 3dlu, left:pref:grow, 3dlu, left:pref, 3dlu, pref, 3dlu, pref", // columns
		        "p, 3dlu" // network label
		                + ",p, 3dlu " // params
		                + ",p, 3dlu " // params
		                + ",p, 9dlu" // separator
		                + ", p, 3dlu" // channel
		                + ", p, 3dlu, p, 3dlu" // elevation
		                + ", p, 3dlu, p, 3dlu" // slope
		                + ", p, 3dlu, p, 3dlu" // aspect
		                + ", p, 3dlu, p, 3dlu" // curvature
		                + ", p, 3dlu, p, 3dlu" // top index
		                + ", p, 3dlu, p, 3dlu" // merge/split size

		                + ", p, 9dlu" // separator
		                + ", p, 3dlu" // hill slope
		                + ", p, 3dlu, p, 3dlu" // elevation
		                + ", p, 3dlu, p, 3dlu" // slope
		                + ", p, 3dlu, p, 3dlu" // aspect
		                + ", p, 3dlu, p, 3dlu" // curvature
		                + ", p, 3dlu, p, 3dlu" // top index
		                + ", p, 3dlu, p, 3dlu" // merge/split size
		                + ", p, 9dlu" // separator
		                + ", p, 3dlu" // post processing
		                // + ", p, 3dlu, p, 3dlu" // elevation
		                + ", p, 3dlu, p, 3dlu" // slope
		                + ", p, 9dlu" // separator
		                + ", p, 3dlu" // Statistics
		                + ", p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu" // ratios
		); // rows

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		// Fill the grid with components; the builder can create
		// frequently used components, e.g. separators and labels.

		// Add a titled separator to cell (1, 1) that spans 7 columns.
		int row = 1;
		builder.addSeparator("HortonNumbers", cc.xyw(1, row, 7));
		row += 2;
		builder.addLabel("DEM cell size", cc.xyw(1, row, 5));
		builder.add(cellsizeLabel, cc.xyw(6, row, 1));
		row += 2;
		builder.addLabel("Accumulation threshold", cc.xyw(1, row, 5));
		builder.add(getAccumulation(RegionalizationRaiser.Parameters.networkAccumulationThreshold, Float.class), cc.xyw(6, row, 1));
		builder.add(getUnits(RegionalizationRaiser.Parameters.networkAccumulationThreshold), cc.xy(8, row));
		if (true)
		{
			JLabel imp = getImpact(RegionalizationRaiser.Parameters.networkAccumulationThreshold);
			builder.add(imp, cc.xy(10, row));
			impact.put(RegionalizationRaiser.Parameters.networkAccumulationThreshold, imp);
		}
		row += 2; // separator
		for (String section : new String[] { "Channel", "Hillslope" })
		{
			row += 2;
			row = add(builder, section, row);
			for (String param : new String[] { "Elevation", "Slope", "Aspect", "Curvature", "Top index" })
			{
				for (String type : new String[] { "Min/Max", "Range" })
				{
					row = add(builder, section, param, type, row);
				}
			}
			for (String param : new String[] { "MergeSize", "SplitSize" })
			{
				row = add(builder, section, param, "Threshold", row, param.equals("MergeSize"));
			}
		}
		row += 2;
		row = add(builder, "Post processing options", row);
		/*
		 * builder.add(getEnabled(RegionalizationRaiser.Parameters.mergeRegionsArea), cc.xy(1, row)); builder.addLabel("Merge regions area < ", cc.xyw(2, row,
		 * 4)); builder.add(getInput(RegionalizationRaiser.Parameters.mergeRegionsArea, Float.class), cc.xyw(6, row, 1));
		 * builder.add(getUnits(RegionalizationRaiser.Parameters.mergeRegionsArea), cc.xy(8, row)); if (true) { JLabel imp =
		 * getImpact(RegionalizationRaiser.Parameters.mergeRegionsArea); builder.add(imp, cc.xy(10, row));
		 * impact.put(RegionalizationRaiser.Parameters.mergeRegionsArea, imp); } row += 2;
		 * builder.add(getEnabled(RegionalizationRaiser.Parameters.splitRegionsArea), cc.xy(1, row)); builder.addLabel("Split regions area > ", cc.xyw(2, row,
		 * 4)); builder.add(getInput(RegionalizationRaiser.Parameters.splitRegionsArea, Float.class), cc.xyw(6, row, 1));
		 * builder.add(getUnits(RegionalizationRaiser.Parameters.splitRegionsArea), cc.xy(8, row)); if (true) { JLabel imp =
		 * getImpact(RegionalizationRaiser.Parameters.splitRegionsArea); builder.add(imp, cc.xy(10, row));
		 * impact.put(RegionalizationRaiser.Parameters.splitRegionsArea, imp); }
		 */
		row += 2;
		JCheckBox compressRegions = getEnabled(RegionalizationRaiser.Parameters.compressRegions);
		enabled.put(RegionalizationRaiser.Parameters.compressRegions, compressRegions);
		compressRegions.setEnabled(false);
		builder.add(compressRegions, cc.xy(1, row));
		builder.addLabel("Merge adjescent similar regions", cc.xyw(2, row, 8));
		if (true)
		{
			JLabel imp = getImpact(RegionalizationRaiser.Parameters.compressRegions);
			builder.add(imp, cc.xy(10, row));
			impact.put(RegionalizationRaiser.Parameters.compressRegions, imp);
		}
		row += 2;
		JCheckBox makeConvex = getEnabled(RegionalizationRaiser.Parameters.makeConvex);
		enabled.put(RegionalizationRaiser.Parameters.makeConvex, makeConvex);
		makeConvex.setEnabled(false);
		builder.add(makeConvex, cc.xy(1, row));
		builder.addLabel("Guarantee convex regions", cc.xyw(2, row, 8));
		if (true)
		{
			JLabel imp = getImpact(RegionalizationRaiser.Parameters.makeConvex);
			builder.add(imp, cc.xy(10, row));
			impact.put(RegionalizationRaiser.Parameters.makeConvex, imp);
		}
		row += 2;
		row = add(builder, "Statistics", row);
		row += 2;
		builder.addLabel("Original points", cc.xyw(2, row, 8));
		if (true)
		{
			JLabel imp = getImpact(RegionalizationRaiser.Parameters.totalPoints);
			builder.add(imp, cc.xy(10, row));
			impact.put(RegionalizationRaiser.Parameters.totalPoints, imp);
		}
		row += 2;
		builder.addLabel("New regions", cc.xyw(2, row, 8));
		if (true)
		{
			JLabel imp = getImpact(RegionalizationRaiser.Parameters.totalRegions);
			builder.add(imp, cc.xy(10, row));
			impact.put(RegionalizationRaiser.Parameters.totalRegions, imp);
		}
		row += 2;
		builder.addLabel("% of points surviving", cc.xyw(2, row, 8));
		if (true)
		{
			JLabel imp = getImpact(RegionalizationRaiser.Parameters.pointsSurviving);
			builder.add(imp, cc.xy(10, row));
			impact.put(RegionalizationRaiser.Parameters.pointsSurviving, imp);
		}

		return builder.getPanel();
	}

	private int add(PanelBuilder builder, String text1, int row)
	{
		CellConstraints cc = new CellConstraints();
		builder.addSeparator(text1, cc.xyw(1, row, 10));
		return row + 2;
	}

	private int add(PanelBuilder builder, String text0, String text1, String text2, int row)
	{
		return add(builder, text0, text1, text2, row, true);
	}

	private int add(PanelBuilder builder, String text0, String text1, String text2, int row, boolean enabledUI)
	{
		String text = text0.toLowerCase().replaceAll(" ", "") + text1.replaceAll(" ", "") + text2.replaceAll("/", "");
		RegionalizationRaiser.Parameters bind = RegionalizationRaiser.Parameters.valueOf(text);
		CellConstraints cc = new CellConstraints();
		enabled.put(bind, getEnabled(bind));
		builder.add(enabled.get(bind), cc.xy(1, row));
		builder.addLabel(text1, cc.xy(2, row));
		builder.addLabel(text2, cc.xy(4, row));
		Component[] c = new Component[2];
		c[0] = getInput(bind, Float.class);
		builder.add(c[0], cc.xy(6, row));
		c[1] = getUnits(bind);
		builder.add(c[1], cc.xy(8, row));
		components.put(bind, c);
		JLabel imp = getImpact(bind);
		builder.add(imp, cc.xy(10, row));
		impact.put(bind, imp);
		updateEnabled(bind, getPreferences().node(bind.toString()).getBoolean("enabled", false));
		if (!enabledUI)
		{
			enabled.get(bind).setEnabled(false);
			updateEnabled(bind, false);
		}
		return row + 2;
	}

	private Component getAccumulation(final RegionalizationRaiser.Parameters bind, Class type)
	{
		final JTextField field = new JTextField(8);
		field.setHorizontalAlignment(JTextField.TRAILING);
		if (true) // initialize
		{
			try
			{
				float value = getPreferences().node(bind.toString()).getFloat("value", 0);
				field.setText("" + value);
				float val = Float.parseFloat(field.getText());
				getParameters().setValue(bind, val / cellsize / cellsize);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				field.setText("0");
				getParameters().setValue(bind, new Float(0));
			}
		}
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				float val = Float.parseFloat(field.getText());
				getParameters().setValue(bind, val / cellsize / cellsize);
			}
		});
		field.addFocusListener(new FocusListener()
		{

			public void focusGained(FocusEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void focusLost(FocusEvent e)
			{
				try
				{
					float val = Float.parseFloat(field.getText());
					getParameters().setValue(bind, val / cellsize / cellsize);
				}
				catch (Exception ex)
				{
					field.setText("" + getParameters().getValue(bind));
				}

			}
		});

		return field;
	}

	private Component getInput(final RegionalizationRaiser.Parameters bind, Class type)
	{
		final JTextField field = new JTextField(8);
		if (true) // initialize
		{
			try
			{
				float value = getPreferences().node(bind.toString()).getFloat("value", 0);
				field.setText("" + value);
				field.setHorizontalAlignment(JTextField.TRAILING);
				float val = Float.parseFloat(field.getText());
				getParameters().setValue(bind, val);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				field.setText("0");
				getParameters().setValue(bind, new Float(0));
			}
		}
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				float val = Float.parseFloat(field.getText());
				getParameters().setValue(bind, val);
			}
		});
		field.addFocusListener(new FocusListener()
		{

			public void focusGained(FocusEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void focusLost(FocusEvent e)
			{
				try
				{
					float val = Float.parseFloat(field.getText());
					getParameters().setValue(bind, val);
				}
				catch (Exception ex)
				{
					field.setText("" + getParameters().getValue(bind));
				}

			}
		});

		return field;
	}

	public RegionalizationModel getParameters()
	{
		return parameters;
	}

	private Component getUnits(RegionalizationRaiser.Parameters bind)
	{
		String units = getPreferences().node(bind.toString()).get("units", ".");
		return new JLabel(units);
	}

	private JCheckBox getEnabled(final RegionalizationRaiser.Parameters bind)
	{
		final JCheckBox field = new JCheckBox();
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println(bind + " action " + field.isSelected());
				updateEnabled(bind, field.isSelected());
			}
		});
		return field;
	}

	private void updateEnabled(RegionalizationRaiser.Parameters bind, boolean enabled)
	{
		if (this.enabled.get(bind).isSelected() != enabled)
		{
			this.enabled.get(bind).setSelected(enabled);
		}
		getParameters().setEnabled(bind, enabled);
		Component[] c = components.get(bind);
		if (c != null)
		{
			for (Component cmp : c)
			{
				cmp.setEnabled(enabled);
			}
		}
	}

	private JLabel getImpact(RegionalizationRaiser.Parameters bind)
	{
		return new JLabel(".");
	}

	@Handles(raises = {})
	void updateStatistics(RegionalizationStatisticsRaiser r)
	{
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		for (JLabel l : impact.values())
		{
			l.setText("N/A");
		}
		RegionalizationModel x = r.getStatistics();
		for (RegionalizationRaiser.Parameters p : x.keySet())
		{
			if (impact.get(p) != null && !p.equals(RegionalizationRaiser.Parameters.totalPoints) && !p.equals(RegionalizationRaiser.Parameters.totalRegions))
			{
				float val = (x.get(p) != null) ? x.get(p) : Float.NaN;
				if (!Float.isNaN(val))
				{
					max = max > val ? max : val;
					min = min < val ? min : val;
				}
			}
		}
		for (RegionalizationRaiser.Parameters p : x.keySet())
		{
			if (impact.get(p) != null && !p.equals(RegionalizationRaiser.Parameters.totalPoints) && !p.equals(RegionalizationRaiser.Parameters.totalRegions))
			{
				float val = (x.get(p) != null) ? x.get(p) : Float.NaN;
				if (!Float.isNaN(val))
				{
					val = Math.round(val);
					max = Math.round(max);
					int a = (int) val;
					int b = (int) max + 1;
					a *= 100;
					int perc = a / b;
					if (perc < 33)
					{
						impact.get(p).setText("small " + perc);
					}
					else if (perc < 66)
					{
						impact.get(p).setText("medium " + perc);
					}
					else
					{
						impact.get(p).setText("significant " + perc);
					}

				}
			}
		}
		float points = x.get(RegionalizationRaiser.Parameters.totalPoints).floatValue();
		float regions = x.get(RegionalizationRaiser.Parameters.totalRegions).floatValue();
		impact.get(RegionalizationRaiser.Parameters.totalPoints).setText("" + points);
		impact.get(RegionalizationRaiser.Parameters.totalRegions).setText("" + regions);
		impact.get(RegionalizationRaiser.Parameters.pointsSurviving).setText("" + Math.round(regions / points * 100));

	}

	private final static float CELLSIZE_DEFAULT = Float.NaN;
	private float cellsize = CELLSIZE_DEFAULT;
	private JLabel cellsizeLabel = new JLabel("" + CELLSIZE_DEFAULT);

	@Handles(raises = {})
	void cellSize(FilledMapLayerRaiser map)
	{
		cellsize = map.getLayer().getDataset().getCellsize();
		cellsizeLabel.setText("" + cellsize);
	}
}
