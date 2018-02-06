package org.deltadore.planet.model.define;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class C_DefineCouleur 
{
	public static Color VIOLET;
	public static Color VIOLET_ROUGE;
	public static Color CONSOLE_VERT;
	public static Color CONSOLE_BLEU;
	public static Color CONSOLE_ROUGE;
	
	
	/**
	 * Initialisation des couleurs.
	 * 
	 */
	public static void f_INIT_COULEUR()
	{
		VIOLET = new Color(Display.getDefault(), 144, 133, 196);
		VIOLET_ROUGE = new Color(Display.getDefault(), 116, 12, 77);
		
		CONSOLE_VERT = new Color(Display.getDefault(), 102, 156, 41);
		CONSOLE_BLEU = new Color(Display.getDefault(), 33, 83, 165);
		CONSOLE_ROUGE = new Color(Display.getDefault(), 177, 20, 20);
	}
	
	/**
	 * Restitution des couleurs.
	 * 
	 */
	public static void f_RESTIT_COULEUR()
	{
		VIOLET.dispose();
		VIOLET_ROUGE.dispose();
		
		CONSOLE_VERT.dispose();
		CONSOLE_BLEU.dispose();
		CONSOLE_ROUGE.dispose();
	}
	
	public static Color f_GET_COULEUR(int r, int g, int b)
	{
		return new Color(Display.getDefault(), r,g, b);
	}
}
