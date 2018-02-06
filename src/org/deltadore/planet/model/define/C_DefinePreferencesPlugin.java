package org.deltadore.planet.model.define;

import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

public class C_DefinePreferencesPlugin 
{
	/** Dans page de préférence **/
	public static final String 	SERVEUR_SVN_ADRESSE					= "SVN_ADRESSE";
	public static final String 	SERVEUR_SVN_ROOT_REPOSITORY 		= "SVN_ROOT";
	
	public static final String 	URL_MANTIS 							= "URL_MANTIS";
	public static final String 	URL_JENKINS 						= "URL_JENKINS";
	public static final String 	URL_ARGOS						= "URL_ARGOS";
	public static final String 	URL_CONTENU_VERSIONS 							= "URL_CONTENU_VERSIONS";
	
	public static final String 	SERVEUR_MEYLAN_DOSSIER_RELEASE 		= "MEYLAN_DOSSIER_RELEASE";
	public static final String 	SERVEUR_MEYLAN_DOSSIER_SITE 		= "MEYLAN_DOSSIER_SITE";
	public static final String 	SERVEUR_MEYLAN_DOSSIER_ECHANGE_DOC	= "MEYLAN_DOSSIER_ECHANGE_DOC";
	public static final String 	SERVEUR_MEYLAN_DOSSIER_SAUVEGARDE	= "MEYLAN_DOSSIER_SAUVEGARDE";
	public static final String 	SERVEUR_MEYLAN_DOSSIER_DOCUMENT		= "MEYLAN_DOSSIER_DOCUMENT";
	public static final String 	SERVEUR_MEYLAN_DOSSIER_VERSIONS		= "MEYLAN_DOSSIER_VERSIONS";
	public static final String 	SERVEUR_MEYLAN_DOSSIER_SPECIFICATIONS		= "MEYLAN_DOSSIER_SPECIFICATIONS";
	
	public static final String 	POSTE_LOCAL_DOSSIER_DEVELOPPEMENT 	= "POSTE_LOCAL_DOSSIER_DEV";
	public static final String 	POSTE_LOCAL_DOSSIER_EXPORT 			= "POSTE_LOCAL_DOSSIER_EXPORT";
	
	public static final String 	UTILISATEUR 						= "UTILISATEUR";
	
	/** Au Runtime **/
	
	public static final String 	OUTIL_DEBUG 						= "OUTIL_DEBUG";
	public static final String 	OUTIL_SANS_CODE_ACCES 				= "OUTIL_SANS_CODE_ACCES";
	public static final String 	OUTIL_DICO_SERVEUR 					= "OUTIL_DICO_SERVEUR";
	public static final String 	OUTIL_MEMOIRE 						= "OUTIL_MEMOIRE";
	
	public static final String	LINK_IMAGES 						= "LINK_IMAGES";
	
	public static final String	PROJET_EN_COURS 					= "PROJET_EN_COURS";
	public static final String	CONFIG_EN_COURS 					= "CONFIG_EN_COURS";
	
	public static final String	COMPILATION_GROUPEE_CM				= "COMPILATION_GROUPEE_CM";
	public static final String	COMPILATION_GROUPEE_SERVEUR 		= "COMPILATION_GROUPEE_SERVEUR";
	public static final String	COMPILATION_GROUPEE_CYCLONE			= "COMPILATION_GROUPEE_CYCLONE";
	public static final String	COMPILATION_GROUPEE_SERVEUR_KNX		= "COMPILATION_GROUPEE_SERVEUR_KNX";
	public static final String	COMPILATION_GROUPEE_INFO_FONC		= "COMPILATION_INFO_FONC";
	public static final String	COMPILATION_GROUPEE_JAR				= "COMPILATION_GROUPEE_JAR";
	
	public static final String	DEBUG								= "DEBUG";
	public static final String	VM_ARGUMENTS								= "VM_ARGUMENTS";
	
	public static final String	SWITCH_DEBUG_PERSPECTIVE			= "SWITCH_DEBUG_PERSPECTIVE";
	
	/**
	 * Retourne la propriété voulue.
	 * 
	 * @param id identifiant de la propriété
	 * @return valeur de la propriété
	 */
	public static String f_GET_PREFERENCE_AS_STRING(String id)
	{
        IPreferenceStore store = C_PlanetPluginActivator.f_GET().getPreferenceStore();
        return store.getString(id);
	}
	
	public static boolean f_GET_PREFERENCE_AS_BOOLEAN(String id)
	{
        IPreferenceStore store = C_PlanetPluginActivator.f_GET().getPreferenceStore();
        return store.getBoolean(id);
	}
	
	public static boolean f_SET_PREFERENCE_AS_BOOLEAN(String id, boolean value)
	{
        IPreferenceStore store = C_PlanetPluginActivator.f_GET().getPreferenceStore();
        store.setValue(id, value);
        
        return true;
	}
	
	public static boolean f_SET_PREFERENCE_AS_STRING(String id, String value)
	{
        IPreferenceStore store = C_PlanetPluginActivator.f_GET().getPreferenceStore();
        store.setValue(id, value);
        
        return true;
	}
	
	/**
	 * Sauvegarde des préférences.
	 * 
	 * @return true si succès
	 */
	public boolean f_SAUVEGARDE()
	{
		try
		{
			// sauvegarde preference store
			InstanceScope.INSTANCE.getNode(new Long(C_PlanetPluginActivator.f_GET().getBundle().getBundleId()).toString()).flush();
			
			return true; //ok
		} 
		catch (BackingStoreException e) 
		{
			// trace
			e.printStackTrace();
    	  
			return false; // ko
		}
	}
	
	public static boolean f_IS_MEYLAN_DOSSIER_RELEASE_ACCESSIBLE()
	{
		String cheminDossierRelease = f_GET_PREFERENCE_AS_STRING(SERVEUR_MEYLAN_DOSSIER_RELEASE);
		
		return C_ToolsFichiers.f_EXISTE(cheminDossierRelease);
	}
	
	public static boolean f_IS_MEYLAN_DOSSIER_SITE_ACCESSIBLE()
	{
		String cheminDossierSite = f_GET_PREFERENCE_AS_STRING(SERVEUR_MEYLAN_DOSSIER_SITE);
		
		return C_ToolsFichiers.f_EXISTE(cheminDossierSite);
	}
	
	public static boolean f_IS_DOSSIER_DEVELOPPEMENT_LOCAL_EXIST()
	{
		String dossier = f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		return C_ToolsFichiers.f_EXISTE(dossier);
	}
	
	public static boolean f_IS_DOSSIER_EXPORT_EXIST()
	{
		String dossier = f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_EXPORT);
		
		return C_ToolsFichiers.f_EXISTE(dossier);
	}
}
