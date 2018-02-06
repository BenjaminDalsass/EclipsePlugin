package org.deltadore.planet.ui.wizards;

import java.io.File;

import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.jobs.C_JobCopieRepertoire;
import org.deltadore.planet.swt.C_FormTextContent;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;

public class C_WizardCheckoutRelease extends Wizard
{
    /** Wizard **/
    public static String 											ID = "WIZARD.CHECKOUT_RELEASE";
    
	/** Page de sélection de release **/
	private C_PageWizardSelectionRelease							c_pageSelectionRelease;
	
	/** Page de synthèse **/
	private C_PageWizardSyntheseActions								c_pageSynthese;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_WizardCheckoutRelease() 
	{
		super();
		
		// initialisation interface
		f_INIT_UI();
	}
	
	/**
	 * Initialisation interface.
	 * 
	 */
	private void f_INIT_UI()
	{
		// wizard
		setWindowTitle("Téléchargement d'une release");
	}
	
	/**
	 * Finalisation procédure checkout. (Anciennes générations 2.1 et 2.2)
	 * 
	 * @return true si succès
	 */
	private boolean f_PERFORM_FINISH_ANCIENNE_ORGANISATION()
	{
		// descriptif release
		C_DescRelease release = c_pageSelectionRelease.f_GET_RELEASE();
		
		// copie release
		File repertoireReleaseLocal = C_ToolsRelease.f_COPIE_DISTRIBUTION_RELEASE_DANS_DOSSIER_DEVELOPPEMENT(release.f_GET_NOM(), null);
		
		// récupération dossier svn
		RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release.f_GET_NOM());
		
		// vérification éxistance projet
		if(C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
		{
			// checkout
			C_ToolsSVN.f_CHECK_OUT_PROJET(getShell(), folder, release.f_GET_NOM(), repertoireReleaseLocal.getAbsolutePath());
			
			return true; // ok
		}
		else return false; // ko
	}
	
	/**
	 * Finalisation procédure checkout. (Nouvelles générations > 2.2)
	 * 
	 * @return true si succès
	 */
	private boolean f_PERFORM_FINISH_NOUVELLE_ORGANISATION()
	{
		// descriptif release
		C_DescRelease release = c_pageSelectionRelease.f_GET_RELEASE();
		
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// récupération dossier svn
		RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release.f_GET_NOM());
		
		// vérification éxistance projet
		if(C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
		{
			// checkout
			C_ToolsSVN.f_CHECK_OUT_PROJET(getShell(), folder, release.f_GET_NOM(), cheminDevLocal);
			
			// copie config
			File fileDest = new File(cheminDevLocal, release.f_GET_NOM());
			
			// dossier source
			File fileSrc = new File(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE) + File.separator + release.f_GET_NOM() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
			
			// copie config serveur release
			C_JobCopieRepertoire jobCopie = new C_JobCopieRepertoire(fileSrc, fileDest, "Config");
			jobCopie.setName("Création config");
			jobCopie.setRule(ResourcesPlugin.getWorkspace().getRoot());
			jobCopie.schedule();
			
			return true; // ok
		}
		else return false; //  ko
	}
	
	/**
	 * Mise à jour du texte de synthèse.
	 * 
	 */
	private void f_MISE_A_JOUR_TEXTE_SYNTHESE()
	{
		// variables
		C_FormTextContent text = new C_FormTextContent(c_pageSynthese.f_GET_FORM_TEXT());
		
		// début texte
		text.f_BEGIN();
		
		// descriptif release
		C_DescRelease release = c_pageSelectionRelease.f_GET_RELEASE();
		
		if(release.f_IS_ORGANISATION_INITIALE())
		{
			text.f_AJOUTE_PUCE_BLEUE("Téléchargement distribution release<br/><b>" + release.f_GET_NOM() + "</b>");
			text.f_AJOUTE_PUCE_BLEUE("Checkout des sources<br/><b>" + release.f_GET_NOM() + "</b>");
		}
		else
		{
			text.f_AJOUTE_PUCE_BLEUE("Checkout distribution<br/><b>" + release.f_GET_NOM() + "</b>");
			text.f_AJOUTE_PUCE_BLEUE("Création configuration par défaut");
		}
		
		// fin texte
		text.f_END();
		
		// affectation
		c_pageSynthese.f_SET_TEXTE_SYNTHESE(text.toString());
	}
	
	@Override
	public boolean performFinish() 
	{
		// descriptif release
		C_DescRelease release = c_pageSelectionRelease.f_GET_RELEASE();
		
		if(release.f_IS_ORGANISATION_INITIALE())
			return f_PERFORM_FINISH_ANCIENNE_ORGANISATION();
		else
			return f_PERFORM_FINISH_NOUVELLE_ORGANISATION();
	}

	@Override
	public boolean canFinish() 
	{
		// descriptif release
		C_DescRelease release = c_pageSelectionRelease.f_GET_RELEASE();
		
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// vérif présence répertoire de développement local
		if(!C_ToolsFichiers.f_EXISTE(cheminDevLocal))
			return false;
		
		// sélection release ok
		if(release == null)
			return false;
		
		// vérif pas déja présent dans répertoire de dev
		String cheminDevLocalRelease = cheminDevLocal + File.separator + release.f_GET_NOM();
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease))
			return false;
		
		// vérif pas déja présent dans workspace
		if(C_ToolsWorkbench.f_IS_PROJET_EXISTE(release.f_GET_NOM()))
			return false;
		
		return true;
	}
	
	@Override
	public void addPages() 
	{
		// page de référence
		c_pageSelectionRelease = new C_PageWizardSelectionRelease();
		addPage(c_pageSelectionRelease);
		
		// page de synthèse
		c_pageSynthese = new C_PageWizardSyntheseActions();
		addPage(c_pageSynthese);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) 
	{
		IWizardPage nextPage = super.getNextPage(page);
		
		if(nextPage.equals(c_pageSynthese))
		{
			// mise à jour du texte de synthese
			f_MISE_A_JOUR_TEXTE_SYNTHESE();
		}
		
		return nextPage;
	}
}
