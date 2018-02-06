package org.deltadore.planet.model.descriptifs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSite;
import org.eclipse.core.resources.IProject;

public class C_DescDistribution 
{
	/** Num�ro d'affaire **/
	private int 					m_numAffaire;
	
	/** Nom affaire **/
	private String 					m_nomAffaire;
	
	/** R�pertoire serveur de distribution **/
	private String					m_str_repertoireServeur;
	
	/** Num�ro de r�vision **/
	private long 					m_long_numRevision;
	
	/** version majeure **/
	private int						m_int_versionMajeure;
	
	/** version mineure **/
	private int						m_int_versionMineure;
	
	/** Date de cr�ation distribution **/
	private String					m_dateDistribution;
	
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
	 * D�finit la version majeure.
	 * 
	 * @param versionMajeure version majeure
	 */
	public void f_SET_VERSION_MAJEURE(int versionMajeure) 
	{
		m_int_versionMajeure = versionMajeure;
	}
	
	/**
	 * D�finit la version mineure.
	 * 
	 * @param versionMajeure version mineure
	 */
	public void f_SET_VERSION_MINEURE(int versionMineure) 
	{
		m_int_versionMineure = versionMineure;
	}

	/**
	 * Retourne le num�ro de la r�vision.
	 * 
	 * @return num�ro de la r�vision
	 */
	public long f_GET_NUMERO_REVISION() 
	{
		return m_long_numRevision;
	}

	/**
	 * D�finit la r�vision.
	 * 
	 * @param numRevision
	 */
	public void f_SET_NUMERO_REVISION(long numRevision) 
	{
		m_long_numRevision = numRevision;
	}

	/**
	 * Retourne la date de la distribution.
	 * 
	 * @return date de la distribution
	 */
	public String f_GET_DATE_DISTRIBUTION() 
	{
		return m_dateDistribution;
	}

	/**
	 * D�finit la date de la distribution.
	 * 
	 * @param dateDistribution date de la distribution
	 */
	public void f_SET_DATE_DISTRIBUTION(String dateDistribution) 
	{
		m_dateDistribution = dateDistribution;
	}
	
	/**
	 * D�finit le num�ro d'affaire.
	 * 
	 * @param dateRelease date de la release
	 */
	public void f_SET_NUMERO_AFFAIRE(int num) 
	{
		m_numAffaire = num;
	}
	
	/**
	 * Retourne le num�ro d'affaire.
	 * 
	 * @return date de release
	 */
	public int f_GET_NUMERO_AFFAIRE() 
	{
		return m_numAffaire;
	}

	/**
	 * D�finit le nom d'affaire.
	 * 
	 * @param dateRelease date de la release
	 */
	public void f_SET_NOM_AFFAIRE(String nom) 
	{
		m_nomAffaire = nom;
	}
	
	/**
	 * Retourne le nom d'affaire.
	 * 
	 * @return date de release
	 */
	public String f_GET_NOM_AFFAIRE() 
	{
		return m_nomAffaire;
	}
	
	/**
	 * Retourne le nom complet de l'affaire.
	 * 
	 * @return nom complet de l'affaire
	 */
	public String f_GET_NOM_COMPLET_AFFAIRE()
	{
		return m_numAffaire + "_" + m_nomAffaire;
	}
	
	/**
	 * D�finit le r�pertoire sur le serveur de distributions.
	 * 
	 * @param repertoire r�pertoire sur le serveur
	 * @return true si succ�s
	 */
	public boolean f_SET_REPERTOIRE_SERVEUR(String repertoire)
	{
		m_str_repertoireServeur = repertoire;
		
		return true; // ok
	}
	
	/**
	 * Retourne le r�pertoire sur le serveur de distributions.
	 * 
	 * @return r�pertoire sur le serveur 
	 */
	public String f_GET_REPERTOIRE_SERVEUR()
	{
		if(m_str_repertoireServeur == null || m_str_repertoireServeur.length() == 0)
			return "_" + f_GET_NOM_COMPLET_AFFAIRE();
		else
			return m_str_repertoireServeur;
	}
	
	/**
	 * Retourne vrai si le site � un chemin sp�cifique.
	 * (sinon le chemin du site = _{NUMERO_SITE}_{NOM_SITE}
	 * 
	 * @return true si chemin sp�cifique
	 */
	public boolean f_HAS_REPERTOIRE_SPECIFIQUE()
	{
		if(m_str_repertoireServeur == null
		|| m_str_repertoireServeur.length() == 0)
			return false; // pas de chemin sp�cifique
		else
			return true; // chemin sp�cifique
	}
	
	/**
	 * Retourne la release correspondant dans le tableau de releases
	 * pass� en param�tre.
	 * 
	 * @param releases tableau de release
	 * @return release correspondant
	 */
	public C_DescRelease f_GET_DESC_RELEASE(ArrayList<C_DescRelease> releases)
	{
		// s�curit�
		if(releases == null)
			return null;
		
		// parcours des releases...
		for(C_DescRelease release : releases)
		{
			// si version identique
			if(release.f_GET_VERSION_COURT().equalsIgnoreCase(f_GET_VERSION_COURT()))
				return release; // trouv�
		}
		
		return null; // ko
	}
	
	public boolean f_IS_PRESENCE_DESC_SERVEUR()
	{
		return new File(C_ToolsSite.f_GET_CHEMIN_REPERTOIRE_SITE(this), C_ToolsSite.NOM_FICHIER_DESCRIPTIF_SITE).exists();
	}
	
	/**
	 * Retourne le nom de la release.
	 * 
	 * @return nom de la release
	 */
	public String f_GET_NOM_RELEASE()
	{
		return C_ToolsRelease.f_GET_NOM_RELEASE(m_int_versionMajeure, m_int_versionMineure);
	}
	
	@Override
	public String toString()
	{
		return f_GET_NOM_COMPLET_AFFAIRE() + " (" + f_GET_VERSION_COURT() + " rev. " + m_long_numRevision + ")";
	}
	
	public boolean f_UPDATE_INFORMATIONS(IProject projet, C_DescRelease descRelease)
	{
		// r�cup�ration reesource locale
		long revisionLocale = C_ToolsSVN.f_GET_REVISION_DU_PROJET(projet);
		f_SET_NUMERO_REVISION(revisionLocale);
		
		f_SET_VERSION_MAJEURE(descRelease.f_GET_VERSION_MAJEURE());
		f_SET_VERSION_MINEURE(descRelease.f_GET_VERSION_MINEURE());
		f_SET_DATE_DISTRIBUTION(new Date().toString());
		
		return true;
	}
}
