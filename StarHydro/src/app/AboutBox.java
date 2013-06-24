package app;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.server.worker.JNLPPersist;

public class AboutBox extends JPanel
{

	private static final long serialVersionUID = 1L;

	String getBuild()
	{
		return Version.getBuildDate().toString();
	}

	public void addNotify()
	{
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(new JLabel("<html><body><font size='+1'>Star Hydro<font></html></body>"));
		add(new JLabel("<html><body>Web site: <a href='http://web.mit.edu/star/hydro/'>http://web.mit.edu/star/hydro/</a></body></html>"));
		add(new JLabel("Build: " + getBuild()));
		add(new JLabel("<html><body>Report bugs to: <a href='mailto:star@mit.edu'>star@mit.edu</a></body></html>"));
		add(new JLabel("Java version: " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor")));
		add(new JLabel("OS version: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version")));
		add(new JLabel("Java 3D renderer: " + (System.getProperty("j3d.rend") != null ? System.getProperty("j3d.rend") : "default")));
		add(new JLabel("StarHydro local cache: " + JNLPPersist.getWorkspace()));
	}

	public void removeNotify()
	{
		super.removeNotify();
	}
}
