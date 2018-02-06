package org.deltadore.planet.model.base;

import java.io.File;
import java.util.ArrayList;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;

public class C_BaseSites 
{
	public File[]		 						m_dossiersSites;
	public boolean 								m_is_serveurMeylanAccessible;
	
	public boolean f_SCAN_SITES()
	{
		String dossier = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
		
		File file = new File(dossier);
		
		m_is_serveurMeylanAccessible = file.exists();
		
		m_dossiersSites = file.listFiles();
		
		return m_dossiersSites != null;
	}
	
	public File[] f_GET_SITES(String filtre)
	{
		if(filtre.replaceAll(" ", "").length() == 0)
			return new File[]{};
		
		ArrayList<File> resultat = new ArrayList<File>();
		
		for(File f : m_dossiersSites)
		{
			if(f.getName().toLowerCase().contains(filtre.toLowerCase()))
				resultat.add(f);
		}
		
		return resultat.toArray(new File[resultat.size()]);
	}
}
