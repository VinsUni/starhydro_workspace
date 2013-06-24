package mit.swing.ui;

public class LabeledTextFieldUpperCase extends mit.swing.xJPanel
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

	private mit.swing.xJTextFieldUpperCase text = new mit.swing.xJTextFieldUpperCase();

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

	public String getAccessibleName()
	{
		return this.text.getAccessibleContext().getAccessibleName();
	}

	public void setAccessibleName(String s)
	{
		this.text.getAccessibleContext().setAccessibleName(s);
	}

	public void addNotify()
	{
		super.addNotify();
		try
		{
			if( 0 == getComponentCount() )
			{
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
				gbc.gridheight = 1;
				gbc.weightx = 0.1;
				gbc.weighty = 0.0;
				gbc.anchor = java.awt.GridBagConstraints.EAST;
				gbc.fill = java.awt.GridBagConstraints.BOTH;
				gbc.insets = new java.awt.Insets(0, 0, 0, 0);
				gbc.ipadx = 0;
				gbc.ipady = 0;

				java.awt.GridBagLayout gbl = new java.awt.GridBagLayout();
				setLayout(gbl);

				setLabelAlignment(javax.swing.SwingConstants.LEADING);
				addAComponent(label, gbl, gbc);
				gbc.gridx = 1;
				gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
				gbc.weightx = 2.9;
				addAComponent(text, gbl, gbc);
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	private void addAComponent(javax.swing.JComponent component, java.awt.GridBagLayout layout, java.awt.GridBagConstraints constraints)
	{
		layout.setConstraints(component, constraints);
		add(component);
	}

	public java.awt.Dimension getPreferredSize()
	{
		java.awt.Dimension size = super.getPreferredSize();
		if( null != getLabel() )
		{
			java.awt.FontMetrics fm = getFontMetrics(label.getFont());
			size = new java.awt.Dimension(fm.stringWidth(getLabel()) + 60, fm.getMaxAscent() + fm.getMaxDescent() + 2);
		}
		return size;
	}

}