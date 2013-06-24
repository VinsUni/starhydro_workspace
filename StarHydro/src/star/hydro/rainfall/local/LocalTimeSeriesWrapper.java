package star.hydro.rainfall.local;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import star.hydro.rainfall.TimeSeries;
import edu.mit.star.plugins.filemanager.helpers.Project;
import edu.mit.star.plugins.filemanager.interfaces.AbstractFileProvider;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;
import edu.mit.star.plugins.filemanager.interfaces.ProjectProvider;

public class LocalTimeSeriesWrapper extends AbstractFileProvider implements ProjectProvider, Project
{

	private static final long serialVersionUID = 1L;
	private File file;
	private Component comp;

	public LocalTimeSeriesWrapper(File parent, Component comp)
	{
		super(parent.getParentFile(), parent.getName());
		file = parent;
		this.comp = comp;
	}

	public Project getParsedObject()
	{
		return this;
	}

	public Object getCachedInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Icon getIcon()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getParsed() throws ProjectException
	{
		try
		{
			BufferedReader is = new BufferedReader(new InputStreamReader((InputStream) getSource()));
			final TimeSeries ts = new TimeSeries();
			String line = is.readLine();
			if (line.startsWith("#StarHydro simple time series"))
			{
				StringBuilder warnings = new StringBuilder();
				while ((line = is.readLine()) != null)
				{
					if (line.length() == 0 || line.startsWith("#"))
					{
						continue;
					}
					StringTokenizer st = new StringTokenizer(line, " \t,;:");
					String key = null;
					String value = null;
					if (st.hasMoreTokens())
					{
						key = st.nextToken();
					}
					if (st.hasMoreTokens())
					{
						value = st.nextToken();
					}
					if (key != null && value != null)
					{
						try
						{
							ts.add(Float.parseFloat(key), Float.parseFloat(value));
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
							warnings.append("line: '" + line + "' has following problem: " + ex.getLocalizedMessage() + "\n");
						}
					}
					else
					{
						warnings.append("line: '" + line + "' does not have: number number format - line skipped.\n");
					}
				}
				if (warnings.length() != 0)
				{
					JOptionPane.showMessageDialog(comp, new JLabel("<html><b>File parsed with following warnings:<br><pre>" + warnings.toString() + "</pre><br></html>"));
				}
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				sb.append("<html><b>Invalid file format.</b><hr> Supported format is:<br>");
				sb.append("first line should be: #StarHydro simple time series<br>");
				sb.append("all other lines are: Hour Precipitation<br>");
				sb.append("all other lines are: Hour Precipitation<hr>");
				sb.append("Example:<br>");
				sb.append("#StarHydro simple time series<br>");
				sb.append("0 12<br>");
				sb.append("1 6<br>");
				sb.append("2 3<br>");
				sb.append("<br></html>");
				JOptionPane.showMessageDialog(comp, new JLabel(sb.toString()));
			}
			return ts;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("<html><b>Invalid file format.</b><hr> Supported format is:<br>");
			sb.append("first line should be: #StarHydro simple time series<br>");
			sb.append("all other lines are: Hour Precipitation<br>");
			sb.append("all other lines are: Hour Precipitation<hr>");
			sb.append("Example:<br>");
			sb.append("#StarHydro simple time series<br>");
			sb.append("0 12<br>");
			sb.append("1 6<br>");
			sb.append("2 3<br>");
			sb.append("<br></html>");
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
			p.add(new JLabel(ex.getMessage()));
			p.add(new JLabel(sb.toString()));
			JOptionPane.showMessageDialog(comp, p);
			throw new ProjectException();
		}
	}

	public InputStream getSource() throws ProjectException
	{
		try
		{
			return new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw new ProjectException();
		}
	}

	public String getSourceName()
	{
		return "File System";
	}

}
