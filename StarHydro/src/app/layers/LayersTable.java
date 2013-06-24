package app.layers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.AdjustableValue;
import star.hydrology.events.AdjustableValueRaiser;
import star.hydrology.events.ApplicableLayerRaiser;
import star.hydrology.events.GridStatisticsProviderChangeEvent;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.ui.layers.LogAdjustPanel;

@SignalComponent(raises = { GridStatisticsProviderChangeRaiser.class })
public class LayersTable extends LayersTable_generated
{
	private static final int ALL = 0;
	private static final int FILTER = 1;

	private TableModel model;
	private JXTable layersTable;
	private JRadioButton all;
	private JRadioButton filter;
	private int mode = FILTER;
	private int[] filterLayers = null;
	private String filterName = null;

	private JTextField thresholdValue = new JTextField();
	private LogAdjustPanel thresholdValueSlider = null;
	private float current = Float.NaN;
	private float cellSize = 0;

	private void setThresholdValue(float value, float cellSize)
	{
		float area = value * cellSize * cellSize;
		thresholdValue.setText("" + area);
		thresholdValue.setEnabled(true);
	}

	private void parseThresholdArea()
	{
		System.out.println("Parse");
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

	public float getCurrent()
	{
		return current;
	}

	public int getKind()
	{
		return LayerConstants.STREAMS;
	}

	@Handles(raises = {})
	protected void setAccumulationTreshold(GridStatisticsProviderChangeRaiser r, FilledMapLayerRaiser map)
	{
		if (r.getKind() == LayerConstants.STREAMS && r != this)
		{
			cellSize = map.getLayer().getDataset().getCellsize();
			setThresholdValue(r.getCurrent(), cellSize);
		}
	}

	@Handles(raises = {})
	protected void initAccumulationTreshold(AdjustableValueRaiser raiser, FilledMapLayerRaiser map)
	{
		if (raiser instanceof AdjustableValueRaiser && raiser.getKind() == getKind())
		{
			AdjustableValue r = (AdjustableValue) raiser;
			cellSize = map.getLayer().getDataset().getCellsize();
			setThresholdValue(r.getAdjustableValue(), cellSize);
		}
	}

	private void initThresholdField()
	{
		thresholdValueSlider.setEnabled(false);
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

	public JPanel getPanel()
	{
		JPanel p = new JPanel();
		model = new TableModel();
		layersTable = new JXTable();
		layersTable.setModel(model);
		layersTable.setColumnControlVisible(true);
		layersTable.setCellSelectionEnabled(false);
		layersTable.setCellSelectionEnabled(false);
		layersTable.setColumnSelectionAllowed(false);
		layersTable.setRowSelectionAllowed(false);
		layersTable.setShowGrid(false);
		getAdapter().addComponent(model);
		model.setColumns(layersTable);
		all = new JRadioButton("All");
		filter = new JRadioButton("Filter", true);
		thresholdValueSlider = new LogAdjustPanel(LayerConstants.STREAMS, false);

		MigLayout layout = new MigLayout("fillx");
		p.setLayout(layout);
		// p.add(getForm());
		p.add(new JLabel("Stream accumulation threshold"), "span 1 ");
		p.add(thresholdValue, "span 2, growx ");
		p.add(new JLabel("<html>m<sup>2</sup><sub>&nbsp;</sub></html>"), "span 1, wrap ");
		p.add(new JLabel(" "));
		p.add(thresholdValueSlider, "span 3, growx ,wrap ");
		p.add(new JLabel("Data layers"), "span 1");
		p.add(all, "span 1, right");
		p.add(filter, "span 1, wrap");
		// p.add( filter , "span 1, wrap" ) ;
		p.add(new JScrollPane(layersTable), "span 4, growx , spanx , wrap");
		/*
		 * JPanel p2 = new JPanel(); p2.add(new JLabel("Layers: ")); p2.add(all); p2.add(filter); p.add(p2);
		 */
		filter.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				setFilter(FILTER);
				layersTable.setSortable(false);
			}
		});
		all.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				setFilter(ALL);
				layersTable.setSortable(true);
			}
		});

		ButtonGroup bg = new ButtonGroup();
		bg.add(all);
		bg.add(filter);
		setFilter(null, null);
		// p.add(new JScrollPane(layersTable));
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				initThresholdField();
			}
		});

		return p;
	}

	@Handles(raises = {})
	void filterLayers(ApplicableLayerRaiser r)
	{
		setFilter(r.getLayers(), r.getName());
	}

	private void updateFilter()
	{
		switch (mode)
		{
		case FILTER:
			Filter f = new Filter()
			{
				ArrayList<Integer> list;

				@Override
				public int getSize()
				{
					return list.size();
				}

				@Override
				protected void init()
				{
					list = new ArrayList<Integer>();
				}

				@Override
				protected int mapTowardModel(int index)
				{
					if (index == list.size())
					{
						return list.get(index - 1);
					}
					return list.get(index);
				}

				@Override
				protected void reset()
				{
					list.clear();
					int inputSize = getInputSize();
					fromPrevious = new int[inputSize]; // fromPrevious is inherited protected
					for (int i = 0; i < inputSize; i++)
					{
						fromPrevious[i] = -1;
					}
				}

				@Override
				protected void filter()
				{
					list.clear();
					int inputSize = getInputSize();
					int current = 0;
					for (int i = 0; i < inputSize; i++)
					{
						if (test(i))
						{
							list.add(i);
							fromPrevious[i] = current++;
						}
					}
				}

				boolean test(int row)
				{
					int kind = ((Integer) getInputValue(row, Constants.KIND)).intValue();
					for (int i = 0; i < filterLayers.length; i++)
					{
						if (kind == filterLayers[i])
						{
							return true;
						}
					}
					return false;
				}

			};
			layersTable.setFilters(new FilterPipeline(new Filter[] { f }));
			model.setFilterName(filterName);
			break;
		case ALL:
		default:
			layersTable.setFilters(null);
			break;
		}
	}

	private void setFilter(int mode)
	{
		this.mode = mode;
		updateFilter();
	}

	public void setFilter(int[] layers, String name)
	{
		this.filterLayers = layers;
		this.filterName = name;
		filter.setEnabled(filterLayers != null);
		updateFilter();
	}

}
