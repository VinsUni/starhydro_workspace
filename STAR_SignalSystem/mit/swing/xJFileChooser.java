package mit.swing;

public class xJFileChooser extends javax.swing.JFileChooser implements mit.awt.event.ComponentRaiser, mit.awt.event.ContainerRaiser, mit.awt.event.FocusRaiser, mit.awt.event.KeyRaiser, mit.awt.event.MouseRaiser
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

	public xJFileChooser(java.awt.Container owner)
	{
		super();
		this.owner = owner;
	}

	public xJFileChooser(java.awt.Container owner, java.io.File currentDirectory)
	{
		super(currentDirectory);
		this.owner = owner;
	}

	public xJFileChooser(java.awt.Container owner, java.io.File currentDirectory, javax.swing.filechooser.FileSystemView fsv)
	{
		super(currentDirectory, fsv);
		this.owner = owner;
	}

	public xJFileChooser(java.awt.Container owner, javax.swing.filechooser.FileSystemView fsv)
	{
		super(fsv);
		this.owner = owner;
	}

	public xJFileChooser(java.awt.Container owner, String currentDirectoryPath)
	{
		super(currentDirectoryPath);
		this.owner = owner;
	}

	public xJFileChooser(java.awt.Container owner, String currentDirectoryPath, javax.swing.filechooser.FileSystemView fsv)
	{
		super(currentDirectoryPath, fsv);
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