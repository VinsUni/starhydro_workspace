package star.hydrology.data.interfaces;

import javax.swing.table.AbstractTableModel;

public class GridTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	Grid g;

	public GridTableModel(Grid g)
	{
		super();
		this.g = g;
	}

	public int getRowCount()
	{
		return g.getRows();
	}

	public int getColumnCount()
	{
		return g.getCols();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return new Float(g.getElementAt(rowIndex, columnIndex));
	}

	public String getColumnName(int columnIndex)
	{
		return Integer.toString(columnIndex);
	}

	public Class<Float> getColumnClass(int columnIndex)
	{
		return Float.class;
	}
}
