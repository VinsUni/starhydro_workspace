package app.layers;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import star.hydrology.ui.palette.Palette;
import star.hydrology.ui.palette.PaletteCanvas;

public class ComboBoxEditor extends DefaultCellEditor
{

	private static final long serialVersionUID = 1L;

	public ComboBoxEditor(ComboBoxModel model)
	{
		super(new JComboBox(model));
		JComboBox box = (JComboBox) this.editorComponent;
		box.setRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				PaletteCanvas c = new PaletteCanvas((Palette) value);
				return c;
			}
		});

	}

}
