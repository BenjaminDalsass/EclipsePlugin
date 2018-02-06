package org.deltadore.planet.model.base;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescBaseSites;
import org.deltadore.planet.tools.C_ToolsSite;

public class C_BaseAffairesPlanet 
{
	/** Descriptif base des sites **/
	public C_DescBaseSites						m_descBaseSites;
	public boolean 								m_is_serveurMeylanAccessible;
	
	/**
	 * Lecture de la base des sites.
	 * Le fichier de base se trouve à la racine du répertoire sites
	 * du serveur de distribution.
	 * 
	 * @see C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE
	 * @return true si base éxistante
	 */
	public boolean f_LECTURE_BASE_SITES_XML()
	{
		m_descBaseSites = C_ToolsSite.f_CHARGER_BASE_SITES();
		
		if(m_descBaseSites == null)
			m_is_serveurMeylanAccessible = false;
		else
			m_is_serveurMeylanAccessible = true;
		
		return m_descBaseSites != null;
	}
}
