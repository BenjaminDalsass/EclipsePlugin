package org.deltadore.planet.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.define.C_DefineRelease;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.plugin.jobs.C_JobCopieRepertoire;
import org.eclipse.core.resources.IProject;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;

public class C_ToolsRelease 
{
	public static final	String				NOM_FICHIER_DESCRIPTIF_RELEASE = "Release.xml";
	
	/**
	 * Retourne les releases du serveur SVN.
	 * 
	 * @return descriptifs de releases
	 */
	public static ArrayList<C_DescRelease> f_GET_RELEASES_SERVEUR_SVN()
	{
		// cr�ation liste
		ArrayList<C_DescRelease> releases = new ArrayList<C_DescRelease>();
		
		// R�cup�ration entr�s SVN  r�pertoire r�f�rence
		ISVNDirEntry[] entrys = C_ToolsSVN.f_GET_REPERTOIRES_REFERENCE();
		
		// s�curit�
		if(entrys == null)
			return null;
		
		// parcours des entr�es SVN du r�p�ertoire r�f�rence...
		for(ISVNDirEntry entry : C_ToolsSVN.f_GET_REPERTOIRES_REFERENCE())
		{
			// flux de lecture
			InputStream stream = C_ToolsSVN.f_GET_INPUT_STREAM_FICHIER_DISTANT(entry, NOM_FICHIER_DESCRIPTIF_RELEASE);
			
			// chargement descriptif
			if(stream != null)
				releases.add(C_ToolsXML.f_CHARGER_RELEASE(stream));
		}
		
		return releases; // ok
	}
	
	/**
	 * Copie release serveur dans dossier de d�veloppement local avec un nouveau nom.
	 * (utilisation sur release 2.1 et 2.2)
	 * 
	 * @param nomRelease nom de la release � copier
	 * @param nouveauNom nouveau nom en local
	 * @return dossier cr��
	 */
	public static File f_COPIE_DISTRIBUTION_RELEASE_DANS_DOSSIER_DEVELOPPEMENT(String nomRelease, String nouveauNom)
	{
		// nom
		String nom = nouveauNom;
		if(nom == null)
			nom = nomRelease;
		
		// r�cup�ration chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// r�cup�ration chemin serveur release
		String cheminSrvRelease = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE);
		
		// r�pertoires source + dest
		File dossierSrc = new File(cheminSrvRelease + File.separator + nomRelease);
		File dossierDest = new File(cheminDevLocal);
		
		// lancement copie
		C_JobCopieRepertoire job = new C_JobCopieRepertoire(dossierSrc, dossierDest, nom);
		job.schedule();
		
		return new File(cheminDevLocal + File.separator + nom);
	}
	
	/**
	 * Retourne vrai si il s'agit d'une ancienne organisation d'affaire.
	 * 
	 * @return true si ancienne organisation d'affaire
	 */
	public static boolean f_IS_ANCIENNE_ORGANISATION(String release)
	{
		if(release.contains("2_1")
		|| release.contains("2_2"))
		{
			// ancienne g�n�ration
			return true;
		}
		else
		{
			// nouvelle g�n�ration
			return false; 
		}
	}
	
	/**
	 * Retourne le chemin du fichier release site du projet pass� en param�tre.
	 * 
	 * @param projet projet souhait�
	 * @return chemin fichier descriptif
	 */
	public static String f_GET_CHEMIN_DESCRIPTIF_RELEASE_FROM_PROJECT(IProject projet)
	{
		// variables
		String chemin;
		
		// cr�ation chemin
		chemin = projet.getLocation().toString() + File.separator;
		
		return chemin; // ok
	}
	
	/**
	 * Retourne le chemin du fichier release site du site pass� en param�tre.
	 * 
	 * @param descSite site souhait�
	 * @return chemin fichier descriptif
	 */
	public static String f_GET_CHEMIN_DESCRIPTIF_RELEASE_FROM_SITE(C_DescDistribution descSite)
	{
		// variables
		String chemin;
		
		// cr�ation chemin
		chemin = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
		chemin = chemin +  File.separator + descSite.f_GET_REPERTOIRE_SERVEUR() + File.separator + descSite.f_GET_NOM_COMPLET_AFFAIRE() + File.separator;
		
		System.out.println(chemin);
		
		return chemin; // ok
	}
	
	/**
	 * Chargement descriptif release du projet pass� en param�tre.
	 * 
	 * @param projet projet souhait�
	 * @return descriptif release
	 */
	public static C_DescRelease f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(IProject projet)
	{
		try
		{
			// chargement descriptif
			C_DescRelease descRelease = C_ToolsXML.f_CHARGER_RELEASE(new FileInputStream(f_GET_CHEMIN_DESCRIPTIF_RELEASE_FROM_PROJECT(projet)+ NOM_FICHIER_DESCRIPTIF_RELEASE));
			
			return descRelease; // ok
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
			
			return null; // ko
		}
	}
	
	/**
	 * Chargement descriptif release du site pass� en param�tre.
	 * 
	 * @param descSite site souhait�
	 * @return descriptif release
	 */
	public static C_DescRelease f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_SITE(C_DescDistribution descSite)
	{
		try
		{
			// chargement descriptif
			C_DescRelease descRelease = C_ToolsXML.f_CHARGER_RELEASE(new FileInputStream(f_GET_CHEMIN_DESCRIPTIF_RELEASE_FROM_SITE(descSite)+ NOM_FICHIER_DESCRIPTIF_RELEASE));
			
			return descRelease; // ok
		}
		catch(Exception e)
		{
			// trace
//			e.printStackTrace();
			
			return null; // ko
		}
	}
	
	/**
	 * Sauvegarde descriptif site du projet pass� en param�tre.
	 * 
	 * @param projet projet souhait�
	 * @param descRelease descriptif release
	 * @return true si succ�s
	 */
	public static boolean f_SAUVEGARDE_DESCRIPTIF_RELEASE_FROM_PROJECT(IProject projet, C_DescRelease descRelease)
	{
		try
		{
			// sauvegarde descriptif
			C_ToolsXML.f_SAUVER_DESCRIPTION_RELEASE(descRelease, new FileOutputStream(f_GET_CHEMIN_DESCRIPTIF_RELEASE_FROM_PROJECT(projet)+ NOM_FICHIER_DESCRIPTIF_RELEASE));
			
			return true; // ok
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
			
			return false; // ko
		}
	}
	
	public static String[] f_CONVERT_TO_ARRAY_STRING(C_DescRelease[] releases)
	{
		String[] resultat = new String[releases.length];
		
		for( int i = 0 ; i < releases.length ; i++ )
		{
			resultat[i] = releases[i].f_GET_NOM();
		}
		
		return resultat;
	}
	
	/**
	 * Retourne le nom de la release correspondant aux versions
	 * pass�es en param�tre.
	 * 
	 * @param versionMajeure version majeur de la release
	 * @param versionMineure version mineure de la release
	 * @return nom de la release
	 */
	public static String f_GET_NOM_RELEASE(int versionMajeure, int versionMineure)
	{
		return String.format("%s_%d_%d", C_DefineRelease.NOM_PROJET, versionMajeure, versionMineure);
	}
}
