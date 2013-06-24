package mit.swing;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.Locale;

import javax.swing.JOptionPane;


import star.ui.Messages;

public enum StarOptionPane
{
	YES_NO, YES_NO_CANCEL, OK_CANCEL, OPEN_CANCEL, SAVE_CANCEL, DEFAULT;

	public static int starOptionDialog(Locale locale, Component parentComponent,
            Object message, String title, int optionType, int messageType, StarOptionPane options)// Object[] options) 
            throws HeadlessException {
            return JOptionPane.showOptionDialog(parentComponent, message, title, optionType, messageType, null, options.get(locale), message);
        }
	
	public static int starOptionDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType, StarOptionPane options)// Object[] options) 
            throws HeadlessException {
		Locale locale = Locale.getDefault();
		return starOptionDialog(locale, parentComponent, message, title, optionType, messageType, options);
        }
	
	public Object[] get(Locale locale)
	{
		switch (this)
		{
		case YES_NO:
			return new Object[] 
			{
				Messages.getString("StarOptionPane.0", locale), //$NON-NLS-1$
				Messages.getString("StarOptionPane.1", locale) //$NON-NLS-1$
			};
		case YES_NO_CANCEL:
			return new Object[]
	    	{
	    		Messages.getString("StarOptionPane.0", locale), //$NON-NLS-1$
	    		Messages.getString("StarOptionPane.1", locale), //$NON-NLS-1$
	    		Messages.getString("StarOptionPane.2", locale) //$NON-NLS-1$
	    	};
		case OK_CANCEL:
			return new Object[] 
			{
		    	Messages.getString("StarOptionPane.3", locale), //$NON-NLS-1$
		    	Messages.getString("StarOptionPane.2", locale) //$NON-NLS-1$
		    };
		case OPEN_CANCEL:
			return new Object[]
			{
		    	Messages.getString("StarOptionPane.4", locale), //$NON-NLS-1$
		    	Messages.getString("StarOptionPane.2", locale) //$NON-NLS-1$
		    };
		case SAVE_CANCEL:
			return new Object[]
			{
		    	Messages.getString("StarOptionPane.5", locale), //$NON-NLS-1$
		    	Messages.getString("StarOptionPane.2", locale) //$NON-NLS-1$
		    };
		case DEFAULT:
			return new Object[]
			{
		    	Messages.getString("StarOptionPane.3", locale), //$NON-NLS-1$
		    };
		default:
			return null;
		}
	}

    
}
