package org.deltadore.planet.model.define;

import java.util.HashMap;

import org.deltadore.planet.model.descriptifs.C_DescRelease;

public class C_DefineInfosManagerPlanet 
{
	public static final String		NOM_REPERTOIRE_CONFIG = "Config";
	public static final String		NOM_REPERTOIRE_RESSOURCES = "Ressources";
	
	public static final String		NOM_FICHIER_DETERMINANT_REPERTOIRE_AFFAIRE_PLANET = "Info_SVN.txt";
	public static final String		PATH_BORLAND_BCW_EXE = "C:\\BC5\\BIN\\bcw.exe";
	
	public static final HashMap<String, String>	PLANETS;
//	{
//		"Adana",
//		"Anoth",
//		"Aquaris",
//		"Basteel",
//		"Crollia",
//		"Danuta",
//		"Dorin",
//		"Endor",
//		"Gala",
//		"Iego",
//		"Lola Sayu",
//		"Mygeeto"
//	};
	
	static
	{
		PLANETS = new HashMap<String, String>();
		PLANETS.put("ManagerPlanet_2_1", "Adana");
		PLANETS.put("ManagerPlanet_2_2", "Anoth");
		PLANETS.put("ManagerPlanet_2_3", "Aquaris");
		PLANETS.put("ManagerPlanet_2_4", "Basteel");
		PLANETS.put("ManagerPlanet_2_5", "Crollia");
		PLANETS.put("ManagerPlanet_2_6", "Dorin");
		PLANETS.put("ManagerPlanet_3_0", "Danuta");
	}
	
	public static String f_GET_PLANET(C_DescRelease descRelease)
	{
		return PLANETS.get(descRelease.f_GET_NOM());
	}
}
