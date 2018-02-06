package org.deltadore.planet.model.define;

import java.io.File;

public class C_DefineInfosServeurs 
{
	/** Sites **/
	private static final String 		SVN_REPERTOIRE_SITE = "Site";
	
	/** Release **/
	private static final String 		SVN_REPERTOIRE_RELEASE = "Reference";
	
	/**
	 * Retourne le repository de distributions spécifiques.
	 * 
	 * @return repository de distributions
	 */
	public static String f_GET_SRV_SVN_SITE_REPOSITORY()
	{
        String adresseSrvSvn = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE);
        return String.format("https://%s/svn/Home/%s", new Object[]{adresseSrvSvn, SVN_REPERTOIRE_SITE});
	}
	
	/**
	 * Retourne le repository de release.
	 * 
	 * @return repository de release
	 */
	public static String f_GET_SRV_SVN_RELEASE_REPOSITORY()
	{
        String adresseSrvSvn = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE);
        String svnRoot = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_SVN_ROOT_REPOSITORY);
        return String.format("https://%s/svn/%s/%s", new Object[]{adresseSrvSvn, svnRoot, SVN_REPERTOIRE_RELEASE});
	}
	
	/**
	 * Retourne le repository de release.
	 * 
	 * @return repository de release
	 */
	public static String f_GET_SRV_SVN_REPOSITORY()
	{
        String adresseSrvSvn = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE);
        String svnRoot = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_SVN_ROOT_REPOSITORY);
        return String.format("https://%s/svn/%s", new Object[]{adresseSrvSvn, svnRoot});
	}
	
	/**
	 * Retourne le chemin des distributions sur serveur de distribution.
	 * 
	 * @return chemin des distributions
	 */
	public static String f_GET_SRV_MEYLAN_REPERTOIRE_SITE_PATH()
	{
		return C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
	}
	
	/**
	 * Retourne le répertoire des distributions sur serveur de distribution.
	 * 
	 * @return répertoire des distributions
	 */
	public static File f_GET_SRV_MEYLAN_REPERTOIRE_SITE_FILE()
	{
		return new File(f_GET_SRV_MEYLAN_REPERTOIRE_SITE_PATH());
	}
	
	/**
	 * Retourne le chemin des releases sur serveur de distribution.
	 * 
	 * @return chemin des releases
	 */
	public static String f_GET_SRV_MEYLAN_REPERTOIRE_RELEASE_PATH()
	{
		return C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE);
	}
	
	/**
	 * Retourne le répertoire des releases sur serveur de distribution.
	 * 
	 * @return répertoire des releases
	 */
	public static File f_GET_SRV_MEYLAN_REPERTOIRE_RELEASE_FILE()
	{
		return new File(f_GET_SRV_MEYLAN_REPERTOIRE_RELEASE_PATH());
	}
}
