package org.deltadore.planet.ui.wizards;

import java.io.File;

import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.plugin.jobs.C_JobCopieRepertoire;
import org.deltadore.planet.swt.C_FormTextContent;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSite;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.deltadore.planet.ui.vues.projet.C_VueProjet;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;

public class C_WizardCreationSite extends Wizard
{
    /** Wizard **/
    public static String 											ID = "WIZARD.CREATION_SITE";
    
    /** Formulaire site **/
    private C_PageWizardFormulaireSite								c_pageFormulaire;
    
	/** Page de synth�se **/
	private C_PageWizardSyntheseActions								c_pageSynthese;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_WizardCreationSite() 
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
		setWindowTitle("Cr�ation d'un site");
	}
	
	/**
	 * Finalisation proc�dure checkout. (Anciennes g�n�rations 2.1 et 2.2)
	 * 
	 * @param release descriptif release
	 * @param site descriptif site
	 * @return true si succ�s
	 */
	private boolean f_PERFORM_FINISH_ORGANISATION_INITIALE(C_DescRelease release, C_DescDistribution site)
	{
		// copie distribution dans dossier d�veloppement local
		File repertoireReleaseLocal = C_ToolsRelease.f_COPIE_DISTRIBUTION_RELEASE_DANS_DOSSIER_DEVELOPPEMENT(release.f_GET_NOM(), site.f_GET_NOM_COMPLET_AFFAIRE());
		
		// r�cup�ration dossier svn de la release
		RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release.f_GET_NOM());
		
		// v�rification �xistance projet (sur SVN)
		if(C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
		{
			// checkout du projet
			C_ToolsSVN.f_CHECK_OUT_PROJET(getShell(), folder, site.f_GET_NOM_COMPLET_AFFAIRE(), repertoireReleaseLocal.getAbsolutePath());
			
			// ajout du site � la base des sites
			C_ToolsSite.f_AJOUTER_SITE_BASE_SITES(site);
			
			// ajout du descriptif � la distribution
			C_ToolsSite.f_SAUVEGARDE_DESCRIPTIF_DISTRIBUTION_IN_REPERTOIRE_DISTRIBUTION(repertoireReleaseLocal, site);
			
			return true; // ok
		}
		else return false; // ko
	}
	
	/**
	 * Finalisation proc�dure checkout. (Nouvelles g�n�rations > 2.2)
	 * 
	 * @param release descriptif release
	 * @param site descriptif site
	 * @return true si succ�s
	 */
	private boolean f_PERFORM_FINISH_ORGANISATION_2_3(C_DescRelease release, C_DescDistribution site)
	{
		// r�cup�ration chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// v�rification si projet SVN d�ja pr�sent (un projet pour n sites)
		if(!C_ToolsWorkbench.f_IS_PROJET_EXISTE(release.f_GET_NOM()))
		{
			// r�cup�ration dossier svn de la release
			RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release.f_GET_NOM());
			
			// v�rification �xistance fichier .project (sur SVN)
			if(C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
			{
				// checkout du projet
				C_ToolsSVN.f_CHECK_OUT_PROJET(getShell(), folder, release.f_GET_NOM(), cheminDevLocal);
			}
			else return false; // ko
		}
		
		// copie config
		File fileDest = new File(cheminDevLocal, release.f_GET_NOM());
		
		// dossier source
		File fileSrc = new File(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE) + File.separator + release.f_GET_NOM() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
		
		// copie config serveur release
		C_JobCopieRepertoire jobCopie = new C_JobCopieRepertoire(fileSrc, fileDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + site.f_GET_NOM_COMPLET_AFFAIRE());
		jobCopie.setName("Cr�ation config");
		jobCopie.setRule(ResourcesPlugin.getWorkspace().getRoot());
		jobCopie.schedule();
		
		// ajout du site � la base des sites
		C_ToolsSite.f_AJOUTER_SITE_BASE_SITES(site);
		
		// r�cup�ration vue projet
		C_VueProjet vueProjet = C_VueProjet.f_FIND_VUE();
		
		// si vue projet ok, r�cup�ration config actuelle
		if(vueProjet != null)
			vueProjet.f_UPDATE_CONFIGS();
		
		return true; // ok
	}
	
	/**
	 * Mise � jour du texte de synth�se.
	 * 
	 */
	private void f_MISE_A_JOUR_TEXTE_SYNTHESE()
	{
		// variables
		C_FormTextContent text = new C_FormTextContent(c_pageSynthese.f_GET_FORM_TEXT());
		
		// d�but texte
		text.f_BEGIN();
		
		// site
		C_DescRelease release = c_pageFormulaire.f_GET_DESC_RELEASE();
		
		if(release.f_IS_ORGANISATION_INITIALE())
		{
			text.f_AJOUTE_PUCE_BLEUE("T�l�chargement distribution release<br/><b>" + release.f_GET_NOM() + "</b>");
			text.f_AJOUTE_PUCE_BLEUE("Checkout des sources<br/><b>" + release.f_GET_NOM() + "</b>");
		}
		else
		{
			if(C_ToolsWorkbench.f_IS_PROJET_EXISTE(release.f_GET_NOM()))
				text.f_AJOUTE_CHECK_VERT("Checkout distribution<br/><b>" + release.f_GET_NOM() + "</b><br/><span color=\"couleur\">(Pr�sent dans workspace)</span>");
			else
				text.f_AJOUTE_PUCE_BLEUE("Checkout distribution<br/><b>" + release.f_GET_NOM() + " </b>");
			
			text.f_AJOUTE_PUCE_BLEUE("Cr�ation configuration pour le site");
		}
		
		// fin texte
		text.f_END();
		
		// affectation
		c_pageSynthese.f_SET_TEXTE_SYNTHESE(text.toString());
	}
	
	@Override
	public boolean performFinish() 
	{
		// r�cup�ration release s�lectionn�e
		C_DescRelease release = c_pageFormulaire.f_GET_DESC_RELEASE();
		
		// r�cup�ration desc site � partir du formulaire
		C_DescDistribution site = c_pageFormulaire.f_GET_DESC_SITE();
		
		// si ancienne organisation
		if(release.f_IS_ORGANISATION_INITIALE())
			return f_PERFORM_FINISH_ORGANISATION_INITIALE(release, site);
		else
			return f_PERFORM_FINISH_ORGANISATION_2_3(release, site);
	}

	@Override
	public boolean canFinish() 
	{
		// r�cup�ration chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// v�rif pr�sence r�pertoire de d�veloppement local
		if(!C_ToolsFichiers.f_EXISTE(cheminDevLocal))
			return false;
		
		// v�rif pas d�ja pr�sent dans r�pertoire de dev
		String cheminDevLocalRelease = cheminDevLocal + File.separator + c_pageFormulaire.f_GET_DESC_SITE().f_GET_NOM_COMPLET_AFFAIRE();
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease))
			return false;
		
		// verif pas d�ja pr�sent dans workspace
		if(C_ToolsWorkbench.f_IS_PROJET_EXISTE(c_pageFormulaire.f_GET_DESC_SITE().f_GET_NOM_COMPLET_AFFAIRE()))
			return false;
		
		// v�rid formulaire saisie
		if(!c_pageFormulaire.isPageComplete())
			return false;
		
		return true;
	}
	
	@Override
	public void addPages() 
	{
		// page formulaire
		c_pageFormulaire = new C_PageWizardFormulaireSite();
		addPage(c_pageFormulaire);
		
		// page de synth�se
		c_pageSynthese = new C_PageWizardSyntheseActions();
		addPage(c_pageSynthese);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) 
	{
		IWizardPage nextPage = super.getNextPage(page);
		
		if(nextPage.equals(c_pageSynthese))
		{
			// mise � jour du texte de synth�se
			f_MISE_A_JOUR_TEXTE_SYNTHESE();
		}
		
		return nextPage;
	}
}
