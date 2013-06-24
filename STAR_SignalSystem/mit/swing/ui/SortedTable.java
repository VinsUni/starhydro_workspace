package mit.swing.ui;

import javax.swing.table.TableModel;

public class SortedTable extends mit.swing.xJTable
{
	public SortedTable(TableModel tableModel)
	{
		super(tableModel);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	private String[] colNames = null;

	public String[] getColNames()
	{
		return this.colNames;
	}

	public void setColNames(String[] colNames)
	{
		this.colNames = colNames;
	}

	private Object[][] tableData = null;

	public Object[][] getTableData()
	{
		return this.tableData;
	}

	public void setTableData(Object[][] tableData)
	{
		this.tableData = tableData;
	}

	public void addNotify()
	{
		super.addNotify();
		try
		{
			javax.swing.table.TableModel model = new javax.swing.table.AbstractTableModel()
			{

				private static final long serialVersionUID = 1L;

				public int getColumnCount()
				{
					if( null != getColNames() )
					{
						return getColNames().length;
					}
					return 0;
				}

				public int getRowCount()
				{
					if( null != getTableData() )
					{
						return getTableData().length;
					}
					return 0;
				}

				public Object getValueAt(int row, int col)
				{
					Object[][] cells = getTableData();
					if( (null != cells) && (cells.length > row) && (cells[row].length > col) )
					{

						return getTableData()[row][col];
					}
					return null;
				}

				public String getColumnName(int col)
				{
					String[] colNames = getColNames();
					if( (null != colNames) && (colNames.length > col) )
					{
						return getColNames()[col];
					}
					return null;
				}

				@SuppressWarnings("unchecked")
				public Class getColumnClass(int col)
				{
					Object[][] cells = getTableData();
					if( (null != cells) && (cells[0].length > col) )
					{
						Object obj = getValueAt(0, col);
						if( null != obj )
						{
							return obj.getClass();
						}
					}
					return null;
				}

				public boolean isCellEditable(int row, int col)
				{
					Object[][] cells = getTableData();
					return((null != cells) && (cells.length > row) && (cells[row].length > col));
				}

				public void setValueAt(Object value, int row, int col)
				{
					Object[][] cells = getTableData();
					if( (null != cells) && (cells.length > row) && (cells[row].length > col) )
					{
						getTableData()[row][col] = (String) value;
					}
				}
			};
			setModel(model);
			setSize(getPreferredSize());
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void setModel(javax.swing.table.TableModel model)
	{
		super.setModel(model);
		/*
		 * try { if( null != model ) { mit.swing.table.SorterFilter sf = new mit.swing.table.SorterFilter( model ) ; sf.addMouseListenerToHeaderInTable( this ) ;
		 * super.setModel( sf ) ; } } catch( Exception ex ) { ex.printStackTrace() ; }
		 */
	}

	public static void main(String[] args)
	{
		mit.swing.ui.SortedTable table = new mit.swing.ui.SortedTable(null);
		String[] colNames = { "First Name", "Last Name", "Favorite Color", "Favorite Number", "Vegetarian" };
		table.setColNames(colNames);

		Object[][] tableData = { { "Mark", "Andrews", new javax.swing.JColorChooser(java.awt.Color.red), new Integer(2), new Boolean(true) }, { "Tom", "Ball", new javax.swing.JColorChooser(java.awt.Color.blue), new Integer(99), new Boolean(false) },
		        { "Alan", "Chung", new javax.swing.JColorChooser(java.awt.Color.green), new Integer(838), new Boolean(false) }, { "Arnaud", "Weber", new javax.swing.JColorChooser(java.awt.Color.pink), new Integer(44), new Boolean(false) } };
		table.setTableData(tableData);

		table.setPreferredSize(new java.awt.Dimension(700, 300));

		mit.swing.xJFrame frame = new mit.swing.xJFrame(null, "SortedTable");
		mit.swing.xJScrollPane scrollpane = new mit.swing.xJScrollPane(frame);
		scrollpane.add(table);
		scrollpane.setPreferredSize(new java.awt.Dimension(700, 300));

		frame.getContentPane().add(scrollpane);
		frame.pack();
		frame.setVisible(true);
	}
}
