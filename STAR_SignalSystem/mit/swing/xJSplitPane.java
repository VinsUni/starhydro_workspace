package mit.swing;

public class xJSplitPane extends javax.swing.JSplitPane implements mit.awt.event.ComponentRaiser, mit.awt.event.ContainerRaiser, mit.awt.event.FocusRaiser, mit.awt.event.KeyRaiser, mit.awt.event.MouseRaiser
{
	private static final long serialVersionUID = 1L;
	private star.event.Adapter adapter = new star.event.Adapter(this);

	public star.event.Adapter getAdapter()
	{
		return this.adapter;
	}

	public java.awt.event.ComponentEvent getComponentEvent()
	{
		return component.getComponentEvent();
	}

	public java.awt.event.ContainerEvent getContainerEvent()
	{
		return container.getContainerEvent();
	}

	public java.awt.event.FocusEvent getFocusEvent()
	{
		return focus.getFocusEvent();
	}

	public java.awt.event.KeyEvent getKeyEvent()
	{
		return key.getKeyEvent();
	}

	public java.awt.event.MouseEvent getMouseEvent()
	{
		return mouse.getMouseEvent();
	}

	protected mit.awt.event.ComponentAdapter component = new mit.awt.event.ComponentAdapter(this);
	protected mit.awt.event.ContainerAdapter container = new mit.awt.event.ContainerAdapter(this);
	protected mit.awt.event.FocusAdapter focus = new mit.awt.event.FocusAdapter(this);
	protected mit.awt.event.KeyAdapter key = new mit.awt.event.KeyAdapter(this);
	protected mit.awt.event.MouseAdapter mouse = new mit.awt.event.MouseAdapter(this);

	private java.awt.Container owner = null;

	public java.awt.Container getOwner()
	{
		return this.owner;
	}

	public xJSplitPane(java.awt.Container owner)
	{
		super();
		this.owner = owner;
	}

	public xJSplitPane(java.awt.Container owner, int newOrientation)
	{
		super(newOrientation);
		this.owner = owner;
	}

	public xJSplitPane(java.awt.Container owner, int newOrientation, boolean newContinuousLayout)
	{
		super(newOrientation, newContinuousLayout);
		this.owner = owner;
	}

	public xJSplitPane(java.awt.Container owner, boolean newContinuousLayout, int newOrientation, java.awt.Component newLeftComponent, java.awt.Component newRightComponent)
	{
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
		this.owner = owner;
	}

	public xJSplitPane(java.awt.Container owner, int newOrientation, java.awt.Component newLeftComponent, java.awt.Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);
		this.owner = owner;
	}

	public void addNotify()
	{
		super.addNotify();
		star.event.Event.invalidate();
	}

	public void removeNotify()
	{
		super.removeNotify();
		star.event.Event.invalidate();
	}

}