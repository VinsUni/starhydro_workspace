package mit.swing.event;

public class ChangeAdapter implements javax.swing.event.ChangeListener
{
	private javax.swing.event.ChangeEvent changeEvent = null;

	public javax.swing.event.ChangeEvent getChangeEvent()
	{
		return this.changeEvent;
	}

	private mit.swing.event.ChangeRaiser owner = null;

	public mit.swing.event.ChangeRaiser getOwner()
	{
		return this.owner;
	}

	public ChangeAdapter(mit.swing.event.ChangeRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof javax.swing.DefaultSingleSelectionModel )
		{
			((javax.swing.DefaultSingleSelectionModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.JViewport )
		{
			((javax.swing.JViewport) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.JTabbedPane )
		{
			((javax.swing.JTabbedPane) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.SingleSelectionModel )
		{
			((javax.swing.SingleSelectionModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.AbstractButton )
		{
			((javax.swing.AbstractButton) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.DefaultBoundedRangeModel )
		{
			((javax.swing.DefaultBoundedRangeModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.MenuSelectionManager )
		{
			((javax.swing.MenuSelectionManager) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.ButtonModel )
		{
			((javax.swing.ButtonModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.DefaultButtonModel )
		{
			((javax.swing.DefaultButtonModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.BoundedRangeModel )
		{
			((javax.swing.BoundedRangeModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.JProgressBar )
		{
			((javax.swing.JProgressBar) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.JSlider )
		{
			((javax.swing.JSlider) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.colorchooser.DefaultColorSelectionModel )
		{
			((javax.swing.colorchooser.DefaultColorSelectionModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.colorchooser.ColorSelectionModel )
		{
			((javax.swing.colorchooser.ColorSelectionModel) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.text.Style )
		{
			((javax.swing.text.Style) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.text.StyleContext )
		{
			((javax.swing.text.StyleContext) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.text.StyleContext.NamedStyle )
		{
			((javax.swing.text.StyleContext.NamedStyle) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.text.DefaultCaret )
		{
			((javax.swing.text.DefaultCaret) owner).addChangeListener(this);
		}
		else if( owner instanceof javax.swing.text.Caret )
		{
			((javax.swing.text.Caret) owner).addChangeListener(this);
		}
	}

	public void stateChanged(javax.swing.event.ChangeEvent e)
	{
		setEvent(e);
	}

	private void setEvent(javax.swing.event.ChangeEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.changeEvent = e;
				new mit.swing.event.ChangeEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}