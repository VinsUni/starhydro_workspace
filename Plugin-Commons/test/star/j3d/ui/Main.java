package test.star.j3d.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.media.j3d.BranchGroup;
import javax.swing.JButton;
import javax.swing.JLabel;

import star.j3d.ui.StarContainer;

import com.sun.j3d.utils.geometry.Sphere;

public class Main extends Frame
{
	private static final long serialVersionUID = 1L;

	StarContainer container;

	BranchGroup Node2Group(javax.media.j3d.Node node)
	{
		BranchGroup contentBranch = new BranchGroup();
		contentBranch.addChild(node);
		return contentBranch;
	}

	void set3D(StarContainer container)
	{
		Sphere sphere = new Sphere(2);
		container.addBranchGroup(Node2Group(sphere));
	}

	void set2D(StarContainer container)
	{
		final JButton b = new JButton("Hello World");
		final Button b2 = new Button("Hello");
		container.setLayout(new BorderLayout());
		container.add(BorderLayout.EAST, new JLabel("Hello World"));
		container.add(BorderLayout.WEST, b2);
		container.add(BorderLayout.SOUTH, b);
		container.add(BorderLayout.NORTH, new Label("Hello World"));
		b.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				System.out.println(e);
			}

		});
		b2.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				System.out.println(e);
			}

		});
		(new Thread(new Runnable()
		{

			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(1000);
						b.setText((new Date()).toString());
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}

			}
		})).start();
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		container = new StarContainer();
		add(container);
		set3D(container);
		set2D(container);
	}

	public static void main(String[] str)
	{
		/*
		 * System.setProperty( "apple.awt.graphics.EnableLazyDrawing", "false" ) ; System.setProperty( "apple.awt.graphics.OptimizeShapes", "false" ) ;
		 * System.setProperty( "apple.awt.graphics.UseQuartz", "false" ) ; System.setProperty( "com.apple.eawt.CocoaComponent.CompatibilityMode", "false" ) ;
		 */

		System.out.println(System.getProperties());
		Main m = new Main();
		m.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				System.exit(0);
			}

		});
		m.pack();
		m.setVisible(true);
	}
}
