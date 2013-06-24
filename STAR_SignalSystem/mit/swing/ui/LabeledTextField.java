package mit.swing.ui;

public class LabeledTextField extends mit.swing.xJPanel
{
	private static final long serialVersionUID = 1L;
	private mit.swing.xJLabel label = new mit.swing.xJLabel();

	public String getLabel()
	{
		return this.label.getText();
	}

	public void setLabel(String label)
	{
		this.label.setText(label);
	}

	public int getLabelAlignment()
	{
		return label.getHorizontalAlignment();
	}

	public void setLabelAlignment(int alignment)
	{
		label.setHorizontalAlignment(alignment);
	}

	private mit.swing.xJTextField text = new mit.swing.xJTextField();

	public String getText()
	{
		return this.text.getText();
	}

	public void setText(String text)
	{
		this.text.setText(text);
	}

	public int getColumns()
	{
		return this.text.getColumns();
	}

	public void setColumns(int columns)
	{
		this.text.setColumns(columns);
	}

	public String getToolTipText()
	{
		return this.text.getToolTipText();
	}

	public void setToolTipText(String text)
	{
		this.text.setToolTipText(text);
	}

	public String getAccessibleName()
	{
		return this.text.getAccessibleContext().getAccessibleName();
	}

	public void setAccessibleName(String s)
	{
		this.text.getAccessibleContext().setAccessibleName(s);
	}

	public boolean isEditable()
	{
		return this.text.isEditable();
	}

	public void setEditable(boolean b)
	{
		this.text.setEditable(b);
		if( b )
		{
			text.setBackground(java.awt.Color.white);
		}
		else
		{
			text.setBackground(java.awt.Color.lightGray);
		}
	}

	public void addNotify()
	{
		super.addNotify();
		java.awt.GridBagLayout gbLayout = new java.awt.GridBagLayout();
		java.awt.GridBagConstraints gbConstraints = new java.awt.GridBagConstraints();
		gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gbConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		gbConstraints.gridx = 0;
		gbConstraints.gridy = 0;
		setLayout(gbLayout);
		add(label, gbConstraints);
		gbConstraints.gridx = 1;
		add(text, gbConstraints);
	}
}