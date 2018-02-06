package org.deltadore.planet.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefineInfosServeurs;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescBaseSites;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.jobs.C_JobCopieRepertoire;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.ui.vues.projet.C_VueProjet;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.jdom.Element;

public class C_ToolsSite 
{
	public static final String 				NOM_FICHIER_DESCRIPTIF_BASE_SITES = "baseSites.xml";
	public static final	String				NOM_FICHIER_DESCRIPTIF_SITE = "Distribution.xml";


	/**
	 * Chargement de la base des sites.
	 * 
	 * @return descriptif base des sites
	 */
	public static C_DescBaseSites f_CHARGER_BASE_SITES()
	{
		// serveur inaccessible
		if(!C_DefinePreferencesPlugin.f_IS_MEYLAN_DOSSIER_SITE_ACCESSIBLE())
			return null;
		
		// récupération du chemin serveur meylan site
		String chemin = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
		
		// fichier de description
		File fichierDescBaseSite = new File(chemin + File.separator + NOM_FICHIER_DESCRIPTIF_BASE_SITES);
		if(!fichierDescBaseSite.exists())
			return null;
		
		// descriptif de base
		C_DescBaseSites descBase = C_ToolsXML.f_CHARGER_BASE_SITES(fichierDescBaseSite);
		
		return descBase;
	}
	
	/**
	 * Ajout d'un site à la base des sites.
	 * 
	 * @param site site à ajouter dans la base
	 * @return true si succès
	 */
	public static boolean f_AJOUTER_SITE_BASE_SITES(C_DescDistribution site)
	{
		try
		{
			// récupération du chemin
			String chemin = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
			
			// fichier de description
			File fichierDescBaseSite = new File(chemin + File.separator + NOM_FICHIER_DESCRIPTIF_BASE_SITES);
			
			// obtention lock
		    FileLock lock = C_Tools.f_GET_LOCK(chemin);
			
		    // impossible d'otenir le lock
		    if(lock == null)
		    	return false;
		    
		    try 
		    {
				Element element = C_ToolsXML.f_PARSE_DOCUMENT_XML(new FileInputStream(fichierDescBaseSite));
				C_DescBaseSites base = C_ToolsXML.f_CONVERT_DOCUMENT_TO_DESC_BASE_SITES(element);
				base.f_AJOUTER_SITE(site.f_GET_NUMERO_AFFAIRE(), site.f_GET_NOM_AFFAIRE(), site.f_GET_VERSION_MAJEURE(), site.f_GET_VERSION_MINEURE(), site.f_GET_REPERTOIRE_SERVEUR());
				C_ToolsXML.f_SAUVER_DESCRIPTION_BASE_SITES(base, new FileOutputStream(fichierDescBaseSite));
		    } 
		    finally 
		    {
		    	// relache verrou
		        lock.release();
		    }
			
			return true; // ok
		} 
		catch (Exception e) 
		{
			// trace
			e.printStackTrace();
			
			return false; // ko
		} 
	}
	
	/**
	 * Retourne le répertoire d'un site sur le serveur de sites.
	 * 
	 * @param site site
	 * @return répertoire serveur
	 */
	public static File f_GET_REPERTOIRE_SITE(C_DescDistribution site)
	{
		File repertoireSite = new File(f_GET_CHEMIN_REPERTOIRE_SITE(site));
		
		return repertoireSite;
	}
	
	/**
	 * Retourne le chemin du répertoire d'un site sur le serveur de sites.
	 * 
	 * @param site site
	 * @return chemin répertoire serveur
	 */
	public static String f_GET_CHEMIN_REPERTOIRE_SITE(C_DescDistribution site)
	{
		// répertoire des sites sur serveur
		File repertoireSites = C_DefineInfosServeurs.f_GET_SRV_MEYLAN_REPERTOIRE_SITE_FILE();
		
		String repParent = null;
		
		if(site.f_HAS_REPERTOIRE_SPECIFIQUE())
			repParent = site.f_GET_REPERTOIRE_SERVEUR();
		else
			repParent = "_" + site.f_GET_NOM_COMPLET_AFFAIRE();
		
		return repertoireSites.getAbsolutePath() + File.separator + repParent + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE();
	}
	
	/**
	 * Retourne le chemin du répertoire d'un site sur le serveur de sites.
	 * 
	 * @param site site
	 * @return chemin répertoire serveur
	 */
	public static String f_GET_CHEMIN_REPERTOIRE_PARENT_SITE(C_DescDistribution site)
	{
		String repParent = null;
		
		String pathServeur = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
		
		if(site.f_HAS_REPERTOIRE_SPECIFIQUE())
			repParent = site.f_GET_REPERTOIRE_SERVEUR();
		else
			repParent = "_" + site.f_GET_NOM_COMPLET_AFFAIRE();
		
		return pathServeur + File.separator + repParent;
	}
	
	/**
	 * Retourne le répertoire d'un site donné.
	 *  
	 * @param numSite numéro du site
	 * @param nomSite nom du site 
	 * @param repertoireServeur répertoire parent sur serveur
	 * @return répertoire du site
	 */
	public static File f_GET_REPERTOIRE_SITE(int numSite, String nomSite, String repertoireServeur)
	{
		try
		{
			// serveur inaccessible
			if(!C_DefinePreferencesPlugin.f_IS_MEYLAN_DOSSIER_SITE_ACCESSIBLE())
				return null;
			
			// nom complet affaire
			String strAffaire = numSite + "_" + nomSite;
			
			// répertoire des sites sur serveur
			File repertoireSites = C_DefineInfosServeurs.f_GET_SRV_MEYLAN_REPERTOIRE_SITE_FILE();
			
			// répertoire site
			if(repertoireServeur == null || repertoireServeur.length() == 0)
				repertoireServeur = "_" + strAffaire;
			File repertoireSite = new File(repertoireSites.getAbsolutePath() + File.separator + repertoireServeur);
			if(!repertoireSite.exists())
				return null;
			
			// répertoire distribution site
			File repertoireDistSite = new File(repertoireSite.getAbsolutePath() + File.separator + strAffaire);
			if(!repertoireDistSite.exists())
				return null;
			
			return repertoireDistSite;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null; // ko
		}
	}
	
	/**
	 * Retourne vrai si il s'agit d'un répertoire de site PLANET.
	 * 
	 * @param repertoire répertoire à tester
	 * @return true si contient fichier déterminant
	 */
	public static boolean f_IS_REPERTOIRE_SITE_PLANET(File repertoire)
	{
		// récupération des fichiers du répertoire
		File[] files = repertoire.listFiles();
		
		// dossier vide
		if(files == null)
			return false;
		
		// parcours des fichiers...
		for(int i = 0 ; i < files.length ; i++)
		{
			// si fichier déterminant
			if(files[i].getName().equals(C_DefineInfosManagerPlanet.NOM_FICHIER_DETERMINANT_REPERTOIRE_AFFAIRE_PLANET))
			{
				return true; // ok, répertoire d'affaire
			}
		}
		
		return false; // ko
	}
	
	/**
	 * Retourne vrai si le fichier descriptif des sites éxiste.
	 * 
	 * @return true si le fichier descriptif des sites éxiste
	 */
	public static boolean f_IS_DESCRIPTIF_BASE_SITES_EXIST()
	{
		String chemin = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
		chemin = chemin.concat(File.separator + NOM_FICHIER_DESCRIPTIF_BASE_SITES);
		
		File fichierDesc = new File(chemin);
		
		return fichierDesc.exists();
	}
	
	public static boolean f_IS_SITE_IS_IN_REPERTOIRE_LOCAL(C_DescDistribution site)
	{
		IProject[] projets = C_ToolsWorkbench.f_GET_PROJETS_PLANET_WORKSPACE();
		
		for(IProject projet : projets)
		{
			C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
			
			if(descRelease.f_IS_ORGANISATION_INITIALE())
			{
				if(projet.getName().equalsIgnoreCase(site.f_GET_NOM_COMPLET_AFFAIRE()))
						return true;
			}
			else
			{
				File projetRoot = projet.getLocation().toFile();
				
				File fileConfSite = new File(projetRoot, "Config_" + site.f_GET_NOM_COMPLET_AFFAIRE());
				
				if(fileConfSite.exists())
					return true;
			}
		}
		
		return false;
	}
	
	public static File f_COPIE_DISTRIBUTION_SITE_DANS_DOSSIER_DEVELOPPEMENT(C_DescDistribution site)
	{
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// répertoires source + dest
		File dossierSrc = f_GET_REPERTOIRE_SITE(site);
		File dossierDest = new File(cheminDevLocal);
		
		// lancement copie
		C_JobCopieRepertoire job = new C_JobCopieRepertoire(dossierSrc, dossierDest);
		job.schedule();
		
		return new File(cheminDevLocal + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE());
	}
	
	public static File f_GET_DOSSIER_CONFIG_SITE(C_DescRelease descRelease, C_DescDistribution site)
	{
		return new File(f_GET_REPERTOIRE_SITE(site).getAbsolutePath() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
	}
	
	public static File f_GET_DOSSIER_CONFIG_LOCAL(C_DescRelease descRelease, C_DescDistribution site)
	{
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// dossier destinataire
			return new File(cheminDevLocal + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE() + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
		}
		else
		{
			return new File(cheminDevLocal + File.separator + descRelease.f_GET_NOM() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + site.f_GET_NOM_COMPLET_AFFAIRE());
		}
	}
	
	public static File f_COPIE_CONFIG_SITE_DANS_DOSSIER_DEVELOPPEMENT(C_DescDistribution site, ISchedulingRule schedulingRule, IJobChangeListener listener)
	{
		// variables
		File dossierSrc = null;
		File dossierDest = null;
	
		// récupération descriptif release du site
		C_DescRelease descRelease = C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASE(site);
	
		// répertoires config du site (source)
		dossierSrc = f_GET_DOSSIER_CONFIG_SITE(descRelease, site);
		
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// répertoire site local
			dossierDest = new File(cheminDevLocal + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE());
			
			// dossier destinataire
			File dossierConfig = new File(dossierDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
			
			// suppression si éxistant
			if(dossierConfig.exists())
				C_ToolsFichiers.f_SUPPRESSION(dossierConfig, new NullProgressMonitor(), false, false, true);
			
			// lancement copie
			C_JobCopieRepertoire job = new C_JobCopieRepertoire(dossierSrc, dossierDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
			job.setRule(schedulingRule);
			if(listener != null)
				job.addJobChangeListener(listener);
			job.schedule();
		}
		else
		{
			// répertoire site local
			dossierDest = new File(cheminDevLocal + File.separator + descRelease.f_GET_NOM());
			
			// dossier destinataire
			File dossierConfig = new File(dossierDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + site.f_GET_NOM_COMPLET_AFFAIRE());
			
			// suppression si éxistant
			if(dossierConfig.exists())
				C_ToolsFichiers.f_SUPPRESSION(dossierConfig, new NullProgressMonitor(), false, false, true);
			
			// lancement copie
			C_JobCopieRepertoire job = new C_JobCopieRepertoire(dossierSrc, dossierDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + site.f_GET_NOM_COMPLET_AFFAIRE());
			job.setRule(schedulingRule);
			if(listener != null)
				job.addJobChangeListener(listener);
			job.schedule();
		}
		
		return new File(cheminDevLocal + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE());
	}
	
	public static File f_COPIE_CONFIG_SITE_SUR_SERVEUR(C_DescDistribution site, ISchedulingRule schedulingRule, IJobChangeListener listener)
	{
		// variables
		File dossierSrc = null;
		File dossierDest = null;
		
		// récupération descriptif release du site
		C_DescRelease descRelease = C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASE(site);
		
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// répertoire site distant
		dossierDest = new File(f_GET_REPERTOIRE_SITE(site).getAbsolutePath());
		
		// dossier destinataire
		File dossierConfig = new File(dossierDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
		
		// suppression si éxistant
		if(dossierConfig.exists())
			C_ToolsFichiers.f_SUPPRESSION(dossierConfig, new NullProgressMonitor(), false, false, true);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// répertoires config du site (source)
			dossierSrc = new File(cheminDevLocal + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG );
		}
		else
		{
			// répertoires config du site (source)
			dossierSrc = new File(cheminDevLocal + File.separator + descRelease.f_GET_NOM() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + site.f_GET_NOM_COMPLET_AFFAIRE());
		}
		
		// lancement copie
		C_JobCopieRepertoire job = new C_JobCopieRepertoire(dossierSrc, dossierDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
		job.setRule(schedulingRule);
		if(listener != null)
			job.addJobChangeListener(listener);
		job.schedule();
		
		return new File(cheminDevLocal + File.separator + site.f_GET_NOM_COMPLET_AFFAIRE());
	}
	
	/**
	 * Retourne le chemin du fichier descriptif site du projet passé en paramètre.
	 * 
	 * @param projet projet souhaité
	 * @return chemin fichier descriptif
	 */
	public static String f_GET_CHEMIN_DESCRIPTIF_SITE_FROM_PROJECT(IProject projet)
	{
		// variables
		String chemin;
		
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		// chemin fichier descriptif
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			chemin = projet.getLocation().toFile().getParent();
		}
		else
		{
			chemin = projet.getLocation().toString();
		}
		
		// création chemin
		chemin = chemin + File.separator;
		
		return chemin;
	}
	
	/**
	 * Chargement descriptif site du projet passé en paramètre.
	 * 
	 * @param projet projet souhaité
	 * @return descriptif site
	 */
	public static C_DescDistribution f_CHARGEMENT_DESCRIPTIF_SITE_FROM_PROJECT(IProject projet)
	{
		if(!C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_is_serveurMeylanAccessible)
			return  null;
		
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites.f_GET_SITE(projet.getName());
		else
			return C_VueProjet.f_FIND_VUE().f_GET_DESC_SITE();
		
//		try
//		{
//			// fichier descriptif
//			File fichierDescriptif = new File(f_GET_CHEMIN_DESCRIPTIF_SITE_FROM_PROJECT(projet)+ NOM_FICHIER_DESCRIPTIF_SITE);
//			
//			// création si inéxistant
//			if(!fichierDescriptif.exists())
//				return null;
//			
//			// chargement descriptif
//			C_DescDistribution descSite = C_ToolsXML.f_CHARGER_DESCRIPTION_SITE_LOCAL(new FileInputStream(fichierDescriptif));
//			
//			return descSite; // ok
//		}
//		catch(Exception e)
//		{
//			// trace
//			e.printStackTrace();
//			
//			return null; // ko
//		}
	}
	
	/**
	 * Sauvegarde descriptif site du projet passé en paramètre.
	 * 
	 * @param projet projet souhaité
	 * @param descSite descriptif site
	 * @return true si succès
	 */
	public static boolean f_SAUVEGARDE_DESCRIPTIF_DISTRIBUTION_IN_REPERTOIRE_DISTRIBUTION(File repertoireDescSite, C_DescDistribution descSite)
	{
		try
		{
			// chemin du fichier descriptif
			String pathDesc = repertoireDescSite.getAbsolutePath() + File.separator + NOM_FICHIER_DESCRIPTIF_SITE;
			
			// création descriptif si inexistant
			File fileDesc = new File(pathDesc);
			if(!fileDesc.exists())
			{
				// création
				fileDesc.createNewFile();
				
				// notification
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.CONNECTED, "Création descriptif site", "Le descriptif du site " + descSite.f_GET_NOM_AFFAIRE() + " à été créé avec succès.");
			}
			
			// sauvegarde descriptif
			C_ToolsXML.f_SAUVER_DESCRIPTION_DISTRIBUTION(descSite, new FileOutputStream(fileDesc));
			
			// flag de lecture seule
			fileDesc.setReadOnly();
			
			return true; // ok
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
			
			return false; // ko
		}
	}
	
	/**
	 * Retourne les config disponibles pour le projet.
	 * 
	 * @param projet projet
	 * @return libellés des config
	 */
	public static String[] f_GET_CONFIG_FROM_PROJECT(IProject projet)
	{
		// résultat
		ArrayList<String>  listeConfig = new ArrayList<String>();
		
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			listeConfig.add(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
		}
		else
		{
			File repProjet = projet.getLocation().toFile();
			
			for(File f : repProjet.listFiles())
			{
				if(f.getName().toLowerCase().startsWith(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG.toLowerCase()))
					listeConfig.add(f.getName());	
			}
		}
		
		return listeConfig.toArray(new String[]{});
	}
	
	/**
	 * Retourne le répertoire config souhaité du projet souhaité.
	 * 
	 * @param projet projet
	 * @param nomConfig nom du dossier de configuration
	 * @return dossier
	 */
	public static File f_GET_REPERTOIRE_CONFIG_FROM_PROJET(IProject projet, String nomConfig)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return new File(projet.getLocation().toFile().getParent(), "Config");
		else
			return new File(projet.getLocation().toFile(), nomConfig);
	}
}
