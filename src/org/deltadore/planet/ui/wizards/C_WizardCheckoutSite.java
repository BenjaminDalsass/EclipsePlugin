package org.deltadore.planet.ui.wizards;

import java.io.File;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.swt.C_FormTextContent;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSite;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.deltadore.planet.ui.vues.projet.C_VueProjet;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;

public class C_WizardCheckoutSite extends Wizard
{
    /** Wizard **/
    public static String 											ID = "WIZARD.CHECKOUT_SITE";
    
	/** Page de sélection de site **/
	private C_PageWizardSelectionSite								c_pageSelectionSite;
	
	/** Page de synthèse **/
	private C_PageWizardSyntheseActions								c_pageSynthese;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_WizardCheckoutSite() 
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
		setWindowTitle("Téléchargement d'un site");
	}
	
	
	/**
	 * Finalisation procédure checkout. (Anciennes générations 2.1 et 2.2)
	 * 
	 * @return true si succès
	 */
	private boolean f_PERFORM_FINISH_ANCIENNE_ORGANISATION()
	{
		// récupération site sélectionné
		C_DescDistribution site = c_pageSelectionSite.f_GET_SITE_SELECTION();
		
		// copie release
		File repertoireSite = C_ToolsSite.f_COPIE_DISTRIBUTION_SITE_DANS_DOSSIER_DEVELOPPEMENT(site);
		repertoireSite.mkdirs();
		
		// récupération dossier svn
		RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_SITE(site);
		
		// vérification éxistance projet
		if(C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
		{
			// checkout
			C_ToolsSVN.f_CHECK_OUT_PROJET(getShell(), folder, site.f_GET_NOM_COMPLET_AFFAIRE(), repertoireSite.getAbsolutePath());
			
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
		// récupération site sélectionné
		final C_DescDistribution site = c_pageSelectionSite.f_GET_SITE_SELECTION();
		
		C_DescRelease descRelease = C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASE(site);
		
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		if(!C_ToolsWorkbench.f_IS_PROJET_EXISTE(descRelease.f_GET_NOM()))
		{
			// récupération dossier svn
			RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(site.f_GET_NOM_RELEASE());
			
			// vérification éxistance projet
			if(C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
			{
				// checkout
				C_ToolsSVN.f_CHECK_OUT_PROJET(getShell(), folder, site.f_GET_NOM_COMPLET_AFFAIRE(), cheminDevLocal);
			}
			else return false; //  ko
		}
		
		// récupération de la config
		C_ToolsSite.f_COPIE_CONFIG_SITE_DANS_DOSSIER_DEVELOPPEMENT(site, ResourcesPlugin.getWorkspace().getRoot(), 
				new IJobChangeListener() 
		{
			@Override
			public void done(IJobChangeEvent arg0)
			{
				// récupération vue projet pour mise à jour configs
				C_VueProjet vueProjet = C_VueProjet.f_FIND_VUE();
				if(vueProjet != null)
					vueProjet.f_UPDATE_CONFIGS_AND_SELECT("Config_" + site.f_GET_NOM_COMPLET_AFFAIRE());
			}
			
			@Override
			public void awake(IJobChangeEvent arg0) {}
			@Override
			public void aboutToRun(IJobChangeEvent arg0) {}
			@Override
			public void sleeping(IJobChangeEvent arg0) {}
			@Override
			public void scheduled(IJobChangeEvent arg0) {}
			@Override
			public void running(IJobChangeEvent arg0) {}
		});
		
		return true; // ok
	}
	
	/**
	 * Mise à jour du texte de synthèse.
	 * 
	 */
	private void f_MISE_A_JOUR_TEXTE_SYNTHESE()
	{
		// variables
		C_FormTextContent text = new C_FormTextContent(c_pageSynthese.f_GET_FORM_TEXT());
		
		// site
		C_DescDistribution site = c_pageSelectionSite.f_GET_SITE_SELECTION();
		
		// début texte
		text.f_BEGIN();
		
		if(C_ToolsRelease.f_IS_ANCIENNE_ORGANISATION(site.f_GET_NOM_RELEASE()))
		{
			text.f_AJOUTE_PUCE_BLEUE("Téléchargement distribution site<br/><b>" + site.f_GET_NOM_AFFAIRE() + "</b>");
			text.f_AJOUTE_PUCE_BLEUE("Checkout des sources<br/><b>" + site.f_GET_NOM_RELEASE() + "</b>");
		}
		else
		{
			C_DescRelease descRelease = C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASE(site);
		
			if(C_ToolsWorkbench.f_IS_PROJET_EXISTE(descRelease.f_GET_NOM()))
				text.f_AJOUTE_CHECK_VERT("Checkout distribution<br/><b>" + descRelease.f_GET_NOM() + "</b><br/><span color=\"couleur\">(Présent dans workspace)</span>");
			else
				text.f_AJOUTE_PUCE_BLEUE("Checkout distribution<br/><b>" + descRelease.f_GET_NOM() + " </b>");
			
			text.f_AJOUTE_PUCE_BLEUE("Téléchargement configuration<br/><b>" + site.f_GET_NOM_AFFAIRE() + "</b>");
		}
		
		// fin texte
		text.f_END();
		
		// affectation
		c_pageSynthese.f_SET_TEXTE_SYNTHESE(text.toString());
	}
	
	@Override
	public boolean performFinish() 
	{
		if(C_ToolsRelease.f_IS_ANCIENNE_ORGANISATION(c_pageSelectionSite.f_GET_SITE_SELECTION().f_GET_NOM_RELEASE()))
			return f_PERFORM_FINISH_ANCIENNE_ORGANISATION();
		else
			return f_PERFORM_FINISH_NOUVELLE_ORGANISATION();
	}

	@Override
	public boolean canFinish() 
	{
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// vérif présence répertoire de développement local
		if(!C_ToolsFichiers.f_EXISTE(cheminDevLocal))
			return false;
		
		// vérif pas déja présent dans répertoire de dev
		String cheminDevLocalRelease = cheminDevLocal + File.separator + c_pageSelectionSite.f_GET_SITE_SELECTION();
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease))
			return false;
		
		return true;
	}
	
	@Override
	public void addPages() 
	{
		// page de référence
		c_pageSelectionSite = new C_PageWizardSelectionSite();
		addPage(c_pageSelectionSite);
		
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
			// mise à jour du texte de synthèse
			f_MISE_A_JOUR_TEXTE_SYNTHESE();
		}
		
		return nextPage;
	}
}
