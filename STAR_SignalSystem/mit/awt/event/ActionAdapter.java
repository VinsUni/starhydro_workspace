package mit.awt.event;

public class ActionAdapter implements java.awt.event.ActionListener
{
	private java.awt.event.ActionEvent actionEvent = null;

	public java.awt.event.ActionEvent getActionEvent()
	{
		return this.actionEvent;
	}

	private mit.awt.event.ActionRaiser owner = null;

	public mit.awt.event.ActionRaiser getOwner()
	{
		return this.owner;
	}

	public ActionAdapter(mit.awt.event.ActionRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.MenuItem )
		{
			((java.awt.MenuItem) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.JMenuItem )
		{
			((javax.swing.JMenuItem) owner).addActionListener(this);
		}
		else if( owner instanceof java.awt.List )
		{
			((java.awt.List) owner).addActionListener(this);
		}
		else if( owner instanceof java.awt.TextField )
		{
			((java.awt.TextField) owner).addActionListener(this);
		}
		else if( owner instanceof java.awt.Button )
		{
			((java.awt.Button) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.AbstractButton )
		{
			((javax.swing.AbstractButton) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.JTextField )
		{
			((javax.swing.JTextField) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.ButtonModel )
		{
			((javax.swing.ButtonModel) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.JComboBox )
		{
			((javax.swing.JComboBox) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.Timer )
		{
			((javax.swing.Timer) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.DefaultButtonModel )
		{
			((javax.swing.DefaultButtonModel) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.ComboBoxEditor )
		{
			((javax.swing.ComboBoxEditor) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.JFileChooser )
		{
			((javax.swing.JFileChooser) owner).addActionListener(this);
		}
		else if( owner instanceof javax.swing.plaf.basic.BasicComboBoxEditor )
		{
			((javax.swing.plaf.basic.BasicComboBoxEditor) owner).addActionListener(this);
		}
	}

	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		setEvent(e);
	}

	public void actionLost(java.awt.event.ActionEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.ActionEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.actionEvent = e;
				new mit.awt.event.ActionEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}