package org.deltadore.planet.model.descriptifs;

import java.util.Date;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefineRelease;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.tigris.subversion.subclipse.core.ISVNRemoteFolder;
import org.tigris.subversion.svnclientadapter.ISVNInfo;

public class C_DescRelease implements Comparable<C_DescRelease>
{
	/** version majeure **/
	private int						m_int_versionMajeure;
	
	/** version mineure **/
	private int						m_int_versionMineure;
	

	/**
	 * Retourne la version mineure.
	 * 
	 * @return version mineure
	 */
	public int f_GET_VERSION_MINEURE() 
	{
		return m_int_versionMineure;
	}
	
	/**
	 * Définit la version mineure.
	 * 
	 * @param versionMineure version mineure
	 */
	public void f_SET_VERSION_MINEURE(int versionMineure) 
	{
		m_int_versionMineure = versionMineure;
	}
	
	/**
	 * Retourne la version majeure.
	 * 
	 * @return version majeure
	 */
	public int f_GET_VERSION_MAJEURE() 
	{
		return m_int_versionMajeure;
	}

	/**
	 * Retourne la version de l'application.
	 * 
	 * @return version de l'application
	 */
	public String f_GET_VERSION()
	{
		return "Version " + m_int_versionMajeure + "." + m_int_versionMineure;
	}

	/**
	 * Retourne la version de l'application.
	 * 
	 * @return version de l'application
	 */
	public String f_GET_VERSION_COURT()
	{
		return "v" + m_int_versionMajeure + "." + m_int_versionMineure;
	}
	
	/**
	 * Retourne vrai si il s'agit d'une release taggée.
	 * 
	 * @return true si release taggée
	 */
	public boolean f_IS_TAGGED_RELEASE()
	{
		if(m_int_versionMajeure == 0 && m_int_versionMineure == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * Définit la version majeure.
	 * 
	 * @param versionMajeure version majeure
	 */
	public void f_SET_VERSION_MAJEURE(int versionMajeure) 
	{
		m_int_versionMajeure = versionMajeure;
	}
	
	/**
	 * Retourne la date de release.
	 * 
	 * @return date de release
	 */
	public C_InfosSVN f_RETRIEVE_SVN_INFO() 
	{
		// result
		C_InfosSVN info = null;
		
		if(!C_Bases.f_GET_BASE_RELEASES().f_GET_SERVEUR_SVN_STATE())
		{
			info = new C_InfosSVN();
			info.m_revision = -1;
			info.m_auteur = "?";
			info.m_date = null;
			return info;
		}
		
		// dossier remote
		ISVNRemoteFolder remoteFolder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(this);
		
		// récupération infos SVN
		ISVNInfo infoSVN = C_ToolsSVN.f_GET_INFORMATIONS_SVN(remoteFolder);
		
		if(infoSVN != null)
		{
			info = new C_InfosSVN();
			info.m_revision = infoSVN.getLastChangedRevision().getNumber();
			info.m_auteur = infoSVN.getLastCommitAuthor();
			info.m_date = infoSVN.getLastChangedDate();
		}
		else
		{
			info = new C_InfosSVN();
			info.m_revision = -1;
			info.m_auteur = "NULL";
			info.m_date = null;
		}
		
		return info;
	}

	/**
	 * Retourne vrai si il s'agit d'une ancienne organisation d'affaire.
	 * 
	 * @return true si ancienne organisation d'affaire
	 */
	public boolean f_IS_ORGANISATION_INITIALE()
	{
		if(m_int_versionMajeure == 2
		&& (m_int_versionMineure == 1 || m_int_versionMineure == 2))
		{
			return true;
		}
		else
		{
			return false; 
		}
	}
	
	/**
	 * Retourne vrai si il s'agit d'une organisation avant version 2.5.
	 * 
	 * @return true si ancienne organisation avant version 2.5.
	 */
	public boolean f_IS_ORGANISATION_AVANT_2_5()
	{
		// béta
		if(m_int_versionMajeure == 0 && m_int_versionMineure == 0)
			return false;
		
		if(m_int_versionMajeure < 2)
			return true;
		
		if(m_int_versionMajeure == 2
		&& m_int_versionMineure <= 5)
			return true;
		
		return false;
	}
	
	/**
	 * Retourne vrai si il s'agit d'une organisation avant version 3.0.
	 * 
	 * @return true si ancienne organisation avant version 3.0.
	 */
	public boolean f_IS_ORGANISATION_AVANT_3_0()
	{
		// béta
		if(m_int_versionMajeure == 0 && m_int_versionMineure == 0)
			return false;
		
		if(m_int_versionMajeure < 3)
			return true;
		
		return false;
	}
	
	/**
	 * Retourne vrai si il s'agit d'une organisation version 2.6.
	 *
	 * @return true si organisation version 2.6.
	 */
	public boolean f_IS_ORGANISATION_2_6()
	{
		return (m_int_versionMajeure == 2 && m_int_versionMineure == 6);
	}
	
	/**
	 * Retourne le nom de la release.
	 * 
	 * @return nom de la release
	 */
	public String f_GET_NOM()
	{
		if(!f_IS_TAGGED_RELEASE())
			return C_DefineRelease.NOM_PROJET;
		else
			return C_ToolsRelease.f_GET_NOM_RELEASE(m_int_versionMajeure, m_int_versionMineure);
	}
	
	/**
	 * Retourne le nom de la planète.
	 *  
	 * @return nom de la planète
	 */
	public String f_GET_PLANET()
	{
		return C_DefineInfosManagerPlanet.f_GET_PLANET(this);
	}
	
	/**
	 * Filtre.
	 * 
	 * @param stringToMatch
	 * @return true si match
	 */
	public boolean f_MATCH(String stringToMatch)
	{
		if(f_GET_NOM().contains(stringToMatch)
		|| (f_GET_PLANET() != null && f_GET_PLANET().contains(stringToMatch)))
			return true;
		else
			return false;
	}

	@Override
	public int compareTo(C_DescRelease descToCompare) 
	{
		if(m_int_versionMajeure == 0 && m_int_versionMineure == 0)
			return -1;
			
		if(m_int_versionMajeure < descToCompare.m_int_versionMajeure)
		{
			return 1;
		}
		else if(m_int_versionMajeure == descToCompare.m_int_versionMajeure)
		{
			if(m_int_versionMineure < descToCompare.m_int_versionMineure)
				return 1;
			else if(m_int_versionMineure == descToCompare.m_int_versionMineure)
				return 0;
			else
				return -1;
		}
		else
		{
			return -1;
		}
	}
	
	public class C_InfosSVN
	{
		public long				m_revision;
		public String			m_auteur;
		public Date				m_date;
	}
}
