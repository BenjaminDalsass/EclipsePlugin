package org.deltadore.planet.model.define;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class C_DefinePolices 
{
	public static Font ARIAL_10_BOLD;
	public static Font VERDANA_12_BOLD;
	
	/**
	 * Initialisation des polices.
	 * 
	 */
	public static void f_INIT_POLICES()
	{
		ARIAL_10_BOLD = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD);
		VERDANA_12_BOLD = new Font(Display.getDefault(), "Verdana", 12, SWT.BOLD);
	}
	
	/**
	 * Restitution des polices.
	 * 
	 */
	public static void f_RESTIT_POLICES()
	{
		ARIAL_10_BOLD.dispose();
		VERDANA_12_BOLD.dispose();
	}
}
