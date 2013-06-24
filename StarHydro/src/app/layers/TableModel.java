package app.layers;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import star.annotations.SignalComponent;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.ui.palette.GrayPalette;
import star.hydrology.ui.palette.LogGrayPalette;
import star.hydrology.ui.palette.Palette;
import star.hydrology.ui.palette.PaletteCanvas;
import star.hydrology.ui.palette.RangePalette;
import star.hydrology.ui.palette.RapidLogPalette;
import star.hydrology.ui.palette.SingleColorPalette;
import star.hydrology.ui.palette.StepPalette;
import star.hydrology.ui.palette.WheelPalette;

@SignalComponent(extend = AbstractTableModel.class)
public class TableModel extends TableModel_generated implements Constants
{

	private static final long serialVersionUID = 1L;
	private ArrayList<Layer> layers = new ArrayList<Layer>();
	private ArrayList<Layer> allLayers = new ArrayList<Layer>();
	private int columnCount = LAST;

	public TableModel()
	{
		initLayers();
	}

	private void initLayers()
	{
		if (true)
		{
			Layer l = new Layer(LayerConstants.TERRAIN, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.STREAMS, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.WATERSHED, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.STREAMNETWORK, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.SIMPLEIUHLAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}

		if (true)
		{
			Layer l = new Layer(LayerConstants.SLOPEPDFLAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.CURVATUREPDFLAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.TOPINDEXPDFLAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.REGLAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.REGDISCRETELAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}
		if (true)
		{
			Layer l = new Layer(LayerConstants.REGCHANNELLAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}

		if (true)
		{
			Layer l = new Layer(LayerConstants.RAINGAUGELAYER, this);
			getAdapter().addComponent(l);
			allLayers.add(l);
		}

	}

	public void setColumns(JTable table)
	{
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setWidth(40);
		table.getColumnModel().getColumn(0).setMaxWidth(40);
		initColors(table.getColumnModel().getColumn(2));
		// initTransparency(table.getColumnModel().getColumn(3));
	}

	public ComboBoxModel getColorModel()
	{
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(new GrayPalette(Color.WHITE, "Gray/White"));
		model.addElement(new GrayPalette(Color.GREEN, "Gray/Green"));
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
		model.addElement(new RangePalette(Color.DARK_GRAY, Color.BLUE.brighter(), "Range/Gray-Blue"));
		model.addElement(new RangePalette(Color.CYAN.brighter(), Color.BLUE.darker(), "Range/Cyan-Blue"));
		model.addElement(new StepPalette("Step"));
		model.addElement(new WheelPalette("Wheel", .5f, .8f));

		return model;
	}

	private void initColors(TableColumn c)
	{
		ComboBoxModel model = getColorModel();
		c.setCellRenderer(new TableCellRenderer()
		{

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				PaletteCanvas c = new PaletteCanvas((Palette) value);
				return c;
			}
		});
		c.setCellEditor(new ComboBoxEditor(model));
		c.setMaxWidth(75);
		c.setWidth(75);
		c.setMinWidth(40);
	}

	public int getColumnCount()
	{
		return columnCount;
	}

	public int getRowCount()
	{
		return layers.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return layers.get(rowIndex).getValue(columnIndex);
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex)
		{
		case VISIBLE:
			return "Visible";
		case LABEL:
			return "Name";
		case COLOR:
			return "Color Schema";
		default:
			return super.getColumnName(columnIndex);
		}
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
		case VISIBLE:
			return Boolean.class;
		case LABEL:
			return String.class;
		case COLOR:
			return Object.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return layers.get(rowIndex).isEditable(columnIndex);
		// return super.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		layers.get(rowIndex).setValue(columnIndex, aValue);
	}

	@Override
	public void fireTableDataChanged()
	{
		layers.clear();
		for (Layer l : allLayers)
		{
			if (l.isEnabled())
			{
				layers.add(l);
			}
		}
		super.fireTableDataChanged();
	}

	public void setFilterName(String filterName)
	{
		for (Layer l : allLayers)
		{
			l.updateWithFilter(filterName);
		}
	}

}
