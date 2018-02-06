package org.deltadore.planet.plugin.preferences;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class C_PlanetPreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences() 
	{
		IPreferenceStore store = C_PlanetPluginActivator.f_GET().getPreferenceStore();
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE, "192.168.30.5");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_SVN_ROOT_REPOSITORY, "SVN-BUEMS");
		store.setDefault(C_DefinePreferencesPlugin.URL_MANTIS, "http://192.168.30.84/mantisbt/view_all_bug_page.php");
		store.setDefault(C_DefinePreferencesPlugin.URL_JENKINS, "http://meylan-jenkins/jenkins/");
		store.setDefault(C_DefinePreferencesPlugin.URL_ARGOS, "http://mantis.manager.buems.deltadore.bzh/gestion_fonction/index.php");
		store.setDefault(C_DefinePreferencesPlugin.URL_CONTENU_VERSIONS, "\\\\Srv-meylan\\DOCUMENT\\DOCUMENTS INNOVATION\\Integration\\Contenu versions V2.x.x.xlsx");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE, "\\\\Srv-meylan\\REFERENCE\\REFERENCE MULTILINGUE");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE, "\\\\Srv-meylan\\Sites");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_ECHANGE_DOC, "\\\\Srv-meylan\\ECHANGE_DOC\\Boite pour ");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SAUVEGARDE, "\\\\Srv-meylan\\SAUVEGARDE\\");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_DOCUMENT, "\\\\Srv-meylan\\DOCUMENT\\");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_VERSIONS, "\\\\Srv-meylan\\DOCUMENT\\DOCUMENTS INNOVATION\\Integration & Tests\\Versions\\");
		store.setDefault(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SPECIFICATIONS, "\\\\Srv-meylan\\document\\DOCUMENTS INNOVATION\\Projets\\Planet_2_6 et plus\\Spécifications");
		
		store.setDefault(C_DefinePreferencesPlugin.OUTIL_DEBUG, false);
		store.setDefault(C_DefinePreferencesPlugin.OUTIL_SANS_CODE_ACCES, false);
		store.setDefault(C_DefinePreferencesPlugin.OUTIL_DICO_SERVEUR, false);
		
		store.setDefault(C_DefinePreferencesPlugin.LINK_IMAGES, true);
		
		store.setDefault(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CM, true);
		store.setDefault(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR, true);
		store.setDefault(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CYCLONE, true);
		store.setDefault(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR_KNX, false);
		store.setDefault(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_JAR, false);
		
		store.setDefault(C_DefinePreferencesPlugin.VM_ARGUMENTS, "-Xms512m -Xmx1280m");
	}
}
