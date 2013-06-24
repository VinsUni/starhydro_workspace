package app.layers;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import star.hydrology.ui.palette.Palette;
import star.hydrology.ui.palette.PaletteCanvas;

public class ComboBoxRenderer extends JComboBox implements TableCellRenderer
{
	private static final long serialVersionUID = 1L;

	public ComboBoxRenderer(ComboBoxModel model)
	{
		super(model);
		setRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				PaletteCanvas c = new PaletteCanvas((Palette) value);
				return c;
			}
		});
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (isSelected)
		{
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		}
		else
		{
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}

		setSelectedItem(value);
		return this;
	}

}
