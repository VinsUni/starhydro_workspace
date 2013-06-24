package star.hydro.rainfall.local;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CachedAccessory extends JPanel
{

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private Cached cached = null;

	public CachedAccessory(Cached c)
	{
		this.cached = c;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		label = new JLabel("<html><b>Sample rainfall functions.</b><br>");

		add(label);
	}

	public void removeNotify()
	{
		super.removeNotify();
		remove(label);
		label = null;

	}

}
