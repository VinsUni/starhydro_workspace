package star.hydro.tribs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import star.hydrology.events.tribs.AnimationRaiser;
import star.tribs.io.Voronoi;
import star.tribs.io.VoronoiCanvas;
import star.tribs.io.Voronoi.Point2D;
import star.tribs.io.Voronoi.Polygon;
import star.tribs.io.Voronoi.Range;
import utils.FileUtils;

@SignalComponent(extend = JPanel.class, raises = { AnimationRaiser.class })
public class TRibsForm extends TRibsForm_generated
{
	private static final long serialVersionUID = 1L;
	private static final String prefix = "http://starapp.mit.edu/star/hydro/datasets/";
	private static final String infix = "simulation";
	private String projectName;
	private int data_length;
	private int str_length;
	private VoronoiCanvas canvas;
	private String[] variableNames;

	private byte[] variable_data;
	private JLabel project = new JLabel();
	private JComboBox variables = new JComboBox();
	private JCheckBox animate = new JCheckBox();
	private JSlider slider = new JSlider();

	private String currentTime;
	private float[] currentData;
	private int currentLoc;
	private String variableName;

	private JTextField offsetX = new JTextField(12);
	private JTextField offsetY = new JTextField(12);
	private JTextField scaleX = new JTextField(12);
	private JTextField scaleY = new JTextField(12);

	private Timer timer = new Timer();

	private class Animate extends TimerTask
	{
		boolean skip = false;

		@Override
		public void run()
		{
			if (animate.isSelected() && !skip)
			{
				skip = true;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						try
						{
							int val = slider.getValue() + 1;
							if (val > slider.getMaximum())
							{
								val = slider.getMinimum();
							}
							slider.setValue(val);
						}
						catch (Throwable ex)
						{
							ex.printStackTrace();
						}
						skip = false;
					}
				});
			}
		}
	};

	@Override
	public void addNotify()
	{
		super.addNotify();

		setLayout(new MigLayout());
		timer.schedule(new Animate(), 5000, 1000);

		add(new JLabel("Project Name"), "span 1 ");
		add(project, "span 1,wrap ");
		add(new JLabel("Variables"), "span 1 ");
		add(variables, "span 1,wrap ");
		add(new JLabel("Animate"), "span 1 ");
		add(animate, "span 1,wrap ");
		add(new JLabel("Position"), "span 1 ");
		add(slider, "span 1,wrap ");
		add(new JLabel("OffsetX pix"), "span 1 ");
		add(offsetX, "span 1,wrap ");
		add(new JLabel("OffsetY pix"), "span 1 ");
		add(offsetY, "span 1,wrap ");

		add(new JLabel("ScaleX"), "span 1 ");
		add(scaleX, "span 1,wrap ");
		add(new JLabel("ScaleY"), "span 1 ");
		add(scaleY, "span 1,wrap ");

		variables.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateVariable();
			}
		});

		animate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("flip animation " + animate.isSelected());
			}
		});

		slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (!slider.getValueIsAdjusting())
				{
					updateTimestep(slider.getValue(), false);
				}
			}
		});

		ChangeListener cl = new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				raise_AnimationEvent();
			}
		};
		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				raise_AnimationEvent();

			}
		};
		offsetX.addActionListener(al);
		offsetY.addActionListener(al);
		scaleX.addActionListener(al);
		scaleY.addActionListener(al);

	}

	void updateTimestep(int loc, boolean updateSlider)
	{
		if (currentLoc != loc)
		{
			int value = slider.getValue();
			int offset = (data_length * 4 + str_length) * value;

			byte[] date = new byte[str_length];
			System.out.println("timesteps " + variable_data.length + " " + " " + offset);
			System.arraycopy(variable_data, offset, date, 0, date.length);
			currentTime = new String(date);
			byte[] bbpoints = new byte[data_length * 4];
			System.out.println("timesteps " + variable_data.length + " " + bbpoints.length + " " + (offset + str_length));
			System.arraycopy(variable_data, offset + str_length, bbpoints, 0, bbpoints.length);
			currentData = new float[data_length];
			java.nio.ByteBuffer.wrap(bbpoints).asFloatBuffer().get(currentData);
			if (updateSlider)
			{
				slider.setValue(loc);
			}
			currentLoc = loc;
			// min = 0 ;
			// max = 1 ;
			// ArrayNumerics.normalize(currentData);
			raise_AnimationEvent();
		}
	}

	void updateVariable()
	{
		try
		{
			variableName = variables.getSelectedItem().toString().trim();
			String url = getSimulationURL_gz(projectName, variableName);
			variable_data = FileUtils.getStreamToByteArray(new GZIPInputStream(new URL(url).openStream()));
			normalize();
			slider.setValueIsAdjusting(true);
			slider.setMinimum(0);
			System.out.println(variable_data.length);
			slider.setMaximum(variable_data.length / (data_length * 4 + str_length) - 1);
			slider.setValue(0);
			slider.setValueIsAdjusting(false);
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setPaintTrack(true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private float min;
	private float max;

	private void normalize()
	{
		min = Float.NaN;
		max = Float.NaN;
		int value = 0;
		int stride = (data_length * 4 + str_length);
		while (true)
		{
			int offset = (data_length * 4 + str_length) * value;
			value++;
			if (offset + stride > variable_data.length)
			{
				break;
			}

			byte[] bbpoints = new byte[data_length * 4];
			System.arraycopy(variable_data, offset + str_length, bbpoints, 0, bbpoints.length);
			float[] currentData = new float[data_length];
			java.nio.ByteBuffer.wrap(bbpoints).asFloatBuffer().get(currentData);
			if (Float.isNaN(min))
			{
				min = currentData[0];
				max = currentData[0];
			}
			for (float x : currentData)
			{
				min = Math.min(min, x);
				max = Math.max(max, x);
			}
			System.out.println("range " + value + " " + min + " " + max);
		}
		// System.out.println("range " + min + " " + max);
	}

	private float normalize(float data)
	{
		float ret = (data - min) / (max - min);
		return ret;
	}

	@Handles(raises = {})
	void handleNewMap(UnprojectedMapChangedRaiser r)
	{
		try
		{
			setEnabled(false);
			InputStream is = (new URL(getSimulationURL(r.getMap().getFilename(), "simul.prop"))).openConnection().getInputStream();
			Properties p = new Properties();
			p.load(is);
			parse(p, r.getMap().getFilename());
			projectName = r.getMap().getFilename();
			updateProject();
			setEnabled(true);
		}
		catch( FileNotFoundException exc  )
		{
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	void parse(Properties p, String project) throws IOException
	{
		String vars = "variables";
		String len = "points_length";
		String str = "hours_str_length";
		String vor = "voronoi_file";

		String[] variables = p.getProperty(vars).toString().trim().split(" ");
		int length = Integer.parseInt(p.getProperty(len).toString().trim());
		int str_len = Integer.parseInt(p.getProperty(str).toString().trim());
		VoronoiCanvas canvas = loadVoronoiPoints(getSimulationURL_gz(project, p.getProperty(vor).toString().trim()));
		this.canvas = canvas;
		this.str_length = str_len;
		this.data_length = length;
		this.variableNames = variables;
	}

	void updateProject()
	{
		animate.setSelected(false);
		project.setText(projectName);
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (int i = 0; i < variableNames.length; i++)
		{
			model.addElement(variableNames[i]);
		}
		variables.setModel(model);

	}

	private VoronoiCanvas loadVoronoiPoints(String url) throws IOException
	{
		VoronoiCanvas canvas = null;

		ArrayList<Polygon> polygons = Voronoi.read(new GZIPInputStream((new URL(url)).openStream()));
		canvas = new VoronoiCanvas(polygons);
		return canvas;
	}

	private String getSimulationURL(String project, String file)
	{
		return MessageFormat.format("{0}/{2}/{1}/{3}", prefix, infix, project, file);
	}

	private String getSimulationURL_gz(String project, String file)
	{
		return MessageFormat.format("{0}/{2}/{1}/{3}.gz", prefix, infix, project, file);
	}

	public double getValue(String text, double defaultValue)
	{
		double ret = defaultValue;
		try
		{
			ret = Double.parseDouble(text);
		}
		catch (Throwable t)
		{
		}
		return ret;

	}

	public void paint(Graphics gr, int width, int height)
	{
		Graphics2D g = (Graphics2D) gr;
		Range range = canvas.getRange();
		double trX = -range.min.x;
		double trY = -range.max.y;
		double rX = (range.max.x - range.min.x);
		double rY = (range.max.y - range.min.y);
		double xx = width * 1.0 / rX;
		double yy = height * 1.0 / rY;
		double rr = Math.min(xx, yy);
		System.out.println(rr);
		double sx = rr;
		double sy = rr;

		double offsetX = getValue(this.offsetX.getText(), 0);
		double offsetY = getValue(this.offsetY.getText(), 0);
		double scaleX = getValue(this.scaleX.getText(), 1);
		double scaleY = getValue(this.scaleY.getText(), 1);

		g.scale(scaleX, scaleY);
		g.translate(offsetX, offsetY);
		g.scale(sx, -sy);
		g.translate(trX, trY);

		if (currentData != null || false)
		{
			canvas.p(g, currentTime, variableName, new VoronoiCanvas.GetColor()
			{
				public Color getColor(int point)
				{
					float v = normalize(currentData[point]);
					if (Float.isNaN(v) || v < 0 || v > 1)
					{
						return new java.awt.Color(1f, 0f, 0f, 0f);
					}
					return new java.awt.Color(v, v, v);
				}

				public Point2D getPoint(Point2D location)
				{
					return location;
				}

			});
		}
		g.translate(-trX, trY);
		g.scale(1 / sx, -1 / sy);
		g.translate(-offsetX, -offsetY);
	}

}
