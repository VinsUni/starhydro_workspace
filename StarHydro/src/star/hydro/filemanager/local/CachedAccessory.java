package star.hydro.filemanager.local;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.server.worker.JNLPPersist;

public class CachedAccessory extends JPanel
{
	static final long serialVersionUID = 1L;
	JButton clearCache;
	JLabel label;
	Cached cached = null;

	public CachedAccessory(Cached c)
	{
		this.cached = c;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		clearCache = new JButton("Clear local cache");
		label = new JLabel("<html><b>Cache location:</b><br>" + JNLPPersist.getWorkspace().getAbsolutePath());

		add(label);
		add(clearCache);
		clearCache.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				(new JNLPPersist()).clearWorkspace();
				cached.reload();
			}
		});
	}

	public void removeNotify()
	{
		super.removeNotify();
		remove(label);
		remove(clearCache);
		label = null;
		clearCache = null;

	}

}
