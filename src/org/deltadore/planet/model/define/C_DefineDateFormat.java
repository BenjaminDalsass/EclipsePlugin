package org.deltadore.planet.model.define;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class C_DefineDateFormat 
{
	public static DateFormat							DATE;
	public static DateFormat							DATE_HEURE;
	
	/**
	 * Initialisation des formats.
	 * 
	 */
	public static void f_INIT_DATE_FORMAT()
	{
		DATE = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
		DATE_HEURE = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	}
	
	/**
	 * Restitution des formats.
	 * 
	 */
	public static void f_RESTIT_DATE_FORMAT()
	{
	}
}
