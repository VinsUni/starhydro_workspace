package app;

import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import starhydro.model.WorldElevation;

public class Main2 implements Runnable
{
	private JFrame f;
	private JPanel sidebar;
	public View2D view;
	private JScrollPane scroll;
	private ScaleSlider slider;
	private AccumulationThresholdSlider accSlider;
	private WorldElevation model;
	private OpenInStarHydro openInStarHydroButton;
	private JLabel coords ;
	
	JComponent initSidebar()
	{
		sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));

		sidebar.add( view.getThumbnail() ) ;
		
		JPanel zoomAndAccu = new JPanel();
		zoomAndAccu.setLayout(new FlowLayout());

		accSlider = new AccumulationThresholdSlider();
		zoomAndAccu.add(accSlider);
		slider = new ScaleSlider();
		zoomAndAccu.add(slider);

		openInStarHydroButton = new OpenInStarHydro();
		openInStarHydroButton.setAlignmentX(.5f);
		sidebar.add(zoomAndAccu);
		sidebar.add(openInStarHydroButton);
		sidebar.add( coords ) ;
		sidebar.add(new SwingRunnerPanel());
		sidebar.add(new utils.Memory(JProgressBar.HORIZONTAL));
		return sidebar;
	}

	private void init()
	{
		f = new JFrame();
		coords = new JLabel( "Coordinates");
		model = new WorldElevation();
		view = new View2D(model, coords);
		view.getAdapter().addComponent(model);
		scroll = new JScrollPane(view);
		scroll.getViewport().setViewPosition(new Point((180 - 71) * model.getElementsPerGrid(), (90 - 42) * model.getElementsPerGrid()));
		f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.LINE_AXIS));

		f.getContentPane().add(initSidebar());
		view.setScaleSlider(slider);
		f.getContentPane().add(scroll);

		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
	}

	public void run()
	{
		System.gc();
		init();
	}

	public static void main(String[] str)
	{
		/*
		try
		{
		UIManager.setLookAndFeel("org.jvnet.substance.SubstanceLookAndFeel");
		}
		catch( Throwable t)
		{
			t.printStackTrace();
		}
		*/
		if( str != null && str.length == 1 && "OpenStarHydro".equals(str[0]) )
		{
			OpenInStarHydro.openHydro(null);
		}
		else
		{
			System.gc();
			SwingUtilities.invokeLater(new Main2());
		}
	}
}
