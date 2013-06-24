package app;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.jdesktop.swingworker.SwingWorker;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import starhydro.events.OpenInStarHydroRaiser;
import starhydro.events.WatershedAvailableRaiser;
import starhydro.model.FloatWorld;
import utils.UIHelpers;

@SignalComponent(extend = JButton.class)
public class OpenInStarHydro extends OpenInStarHydro_generated
{
	private static final long serialVersionUID = 1L;
	private FloatWorld world;

	@Override
	public void addNotify()
	{
		super.addNotify();
		setText("Open watershed in StarHydro");
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				raise_OpenInStarHydroEvent();
			}
		});
	}

	@Handles(raises = { OpenInStarHydroRaiser.class }, handleValid = true)
	public void handleWatershedAvailable(WatershedAvailableRaiser r)
	{
		System.out.println(this.getClass().getName() + " Enable button");
		setEnabled(true);
		world = r.getView2D();
	}

	@Handles(raises = { OpenInStarHydroRaiser.class }, handleValid = false)
	public void handleWatershedNotAvailable(WatershedAvailableRaiser r)
	{
		System.out.println(this.getClass().getName() + " Disable button");
		setEnabled(false);
		world = null;
	}

	@Handles(raises = {})
	public void handleOpenInStarHydro(OpenInStarHydroRaiser r)
	{
		final Component comp = this ;
		new SwingWorker() {

			@Override
            protected Object doInBackground() throws Exception
            {
				try
				{
					java.io.File folder = new java.io.File(getWorkspace(), "StarHydro2");
					if( !folder.exists() )
					{
						folder.mkdirs();
					}
					ProgressMonitor progress = new ProgressMonitor( comp , new JLabel( "Transfering data to StarHydro") , "Starting transfer..." , 0 , 100 ) ;	
					progress.setMillisToPopup(50);
					
					boolean ok = world.saveForStarHydro(folder,progress);
					if( ok )
					{
						JFrame frame = new JFrame( "Opening..." ) ;
						frame.pack();
						frame.setVisible(true);
						UIHelpers.disposeTop(comp);
						openHydro("StarHydro2");
						frame.dispose();
					}
				}
				catch( IOException e )
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(comp, "Unable to transfer data - error is: " + e.getMessage());
				}
				return null;
            }
		}.execute();
	}

	public static File getWorkspace()
	{
		String home = System.getProperty("user.home");
		File f = new File(home, "/starhydro_workspace");
		boolean ok = f.exists();
		if( !ok )
		{
			ok = f.mkdirs();
		}
		return f;
	}

	static class OpenStarHydro2 implements Runnable
	{
		public void run()
		{
			app.Main2.main(null);		
		}
	}
	public static void openHydro(String pack)
	{
		try
		{
		Class c = OpenInStarHydro.class.forName("app.Main");
		c.getField("hydro2").set(null, new OpenStarHydro2() ) ;
		String[] str = null ;
		if( pack != null )
		{
			String[] str2 = { "StarHydro2Import" , pack , "1" , "1" }; 
			str = str2 ;
		}
		Method[] methods = c.getMethods();
		for( Method m : methods)
		{
			if( "main".equals(m.getName()))
			{
				m.invoke(null, (Object)str);
			}
		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
			throw new RuntimeException( ex ) ;
		}
	}
}
