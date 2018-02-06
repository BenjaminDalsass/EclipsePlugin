package org.deltadore.planet.plugin.preferences;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class C_PlanetPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/** Utilisateur SVN **/
	private StringFieldEditor			c_txt_utilisateur;
	
	/** adresse erveur SVN **/
	private StringFieldEditor			c_txt_adresseSVN;
	
	/** URL Repository SVN **/
	private StringFieldEditor			c_txt_urlRepositorySVN;
	
	/** Adresse IP du serveur subversion **/
	private StringFieldEditor			c_txt_serveurSubversionIP;

	/** Repertoire root du serveur subversion **/
	private StringFieldEditor			c_txt_rootSubversion;
	
	/** URL Mantis **/
	private StringFieldEditor			c_txt_urlMantis;
	
	/** URL Jenkins **/
	private StringFieldEditor			c_txt_urlJenkins;
	
	/** URL Argos **/
	private StringFieldEditor			c_txt_urlArgos;	
	
	/** URL contenu versions **/
	private StringFieldEditor			c_txt_urlContenuVersions;
	
	/** Répertoire distributions **/
	private DirectoryFieldEditor		c_file_repertoireDistributions;
	
	/** Répertoire documents **/
	private DirectoryFieldEditor		c_file_repertoireDocuments;
	
	/** Répertoire release **/
	private DirectoryFieldEditor		c_file_repertoireRelease;
	
	/** Répertoire developpement local **/
	private DirectoryFieldEditor		c_file_repertoireDevLocal;
	
	/** Répertoire export **/
	private DirectoryFieldEditor		c_file_repertoireExportLocal;
	
	/** Switch debug perspective **/
	private BooleanFieldEditor			c_chk_switchDebugPerspective;
	
	/** Flag de debug **/
	private BooleanFieldEditor			c_chk_debug;
	
	/** VM Args **/
	private StringFieldEditor			c_txt_vmArgs;
	
	
	
	public void init(IWorkbench workbench) 
	{
		setPreferenceStore(C_PlanetPluginActivator.f_GET().getPreferenceStore());
		setDescription("Préférences du plugin ManagerPlanet");
	}
	
	@Override
	protected void createFieldEditors() 
	{
		// utilisateur SVN
		c_txt_utilisateur = new StringFieldEditor(C_DefinePreferencesPlugin.UTILISATEUR, "Utilisateur (SVN username)", getFieldEditorParent());
		addField(c_txt_utilisateur);
		
		// adresse SVN
		c_txt_adresseSVN  = new StringFieldEditor(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE, "Adresse serveur SVN", getFieldEditorParent());
		addField(c_txt_adresseSVN);
		
		// adresse SVN
		c_txt_urlRepositorySVN  = new StringFieldEditor(C_DefinePreferencesPlugin.SERVEUR_SVN_ROOT_REPOSITORY, "URL Repository SVN", getFieldEditorParent());
		addField(c_txt_urlRepositorySVN);
		
		// path contenu versions
		c_txt_urlContenuVersions = new StringFieldEditor(C_DefinePreferencesPlugin.URL_CONTENU_VERSIONS, "Chemin fichier contenu versions", getFieldEditorParent());
		addField(c_txt_urlContenuVersions);
		
//		// adresse SVN
//		c_txt_serveurSubversionIP = new StringFieldEditor(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE, "Adresse Serveur SVN", getFieldEditorParent());
//		addField(c_txt_serveurSubversionIP);
//		
//		// répertoire root SVN
//		c_txt_rootSubversion = new StringFieldEditor(C_DefinePreferencesPlugin.SERVEUR_SVN_ROOT_REPOSITORY, "Répertoire root Serveur SVN", getFieldEditorParent());
//		addField(c_txt_rootSubversion);
//		
		// dossier distributions srv meylan
		c_file_repertoireDistributions = new DirectoryFieldEditor(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE, "Répertoire distributions", getFieldEditorParent());
		addField(c_file_repertoireDistributions);
		
		// dossier documentation srv meylan
		c_file_repertoireDocuments = new DirectoryFieldEditor(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_DOCUMENT, "Documents serveur meylan", getFieldEditorParent());
		addField(c_file_repertoireDocuments);
//		
//		// dossier release srv meylan
//		c_file_repertoireRelease = new DirectoryFieldEditor(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE, "Répertoire release", getFieldEditorParent());
//		addField(c_file_repertoireRelease);
		
		// url mantis
		c_txt_urlMantis  = new StringFieldEditor(C_DefinePreferencesPlugin.URL_MANTIS, "URL Mantis", getFieldEditorParent());
		addField(c_txt_urlMantis);
		
		// url jenkins
		c_txt_urlJenkins  = new StringFieldEditor(C_DefinePreferencesPlugin.URL_JENKINS, "URL Jenkins", getFieldEditorParent());
		addField(c_txt_urlJenkins);
		
		// url argos
		c_txt_urlArgos  = new StringFieldEditor(C_DefinePreferencesPlugin.URL_ARGOS, "URL Argos", getFieldEditorParent());
		addField(c_txt_urlArgos);
		
		// dossier local
		c_file_repertoireDevLocal = new DirectoryFieldEditor(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT, "Répertoire développement", getFieldEditorParent());
		addField(c_file_repertoireDevLocal);
		
		// dossier export
		c_file_repertoireExportLocal = new DirectoryFieldEditor(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_EXPORT, "Dossier exportation", getFieldEditorParent());
		addField(c_file_repertoireExportLocal);
		
		// switch debug perspective
		c_chk_switchDebugPerspective = new BooleanFieldEditor(C_DefinePreferencesPlugin.SWITCH_DEBUG_PERSPECTIVE, "Perspective debug auto", getFieldEditorParent());
		addField(c_chk_switchDebugPerspective);
		
		// debug
		c_chk_debug = new BooleanFieldEditor(C_DefinePreferencesPlugin.DEBUG, "Activer le debug", getFieldEditorParent());
		addField(c_chk_debug);
		
		// vm args
		c_txt_vmArgs  = new StringFieldEditor(C_DefinePreferencesPlugin.VM_ARGUMENTS, "Arguments VM", getFieldEditorParent());
		addField(c_txt_vmArgs);
	}
}
