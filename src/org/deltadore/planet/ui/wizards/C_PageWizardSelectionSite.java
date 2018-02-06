package org.deltadore.planet.ui.wizards;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.plugin.jobs.C_JobRecuperationSitePlanet;
import org.deltadore.planet.swt.C_TextSearch;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsSite;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;

public class C_PageWizardSelectionSite extends WizardPage implements SelectionListener, ModifyListener, IJobChangeListener
{
    /** Wizard **/
    public static String 					ID = "WIZARD.PAGE.SELECTION_SITE";

    /** Images **/
    private static ImageDescriptor 			IMAGE_WIZARD;
    private static ImageDescriptor 			IMAGE_SITE;
    
	/** job watching site serveur **/
	private C_JobRecuperationSitePlanet 	m_job;
	
	/** scrollable **/
	private ScrolledComposite				c_scrollForm;
	
    /** contenu scroll **/
    private Composite						c_content;
    
    /** Search field **/
    private C_TextSearch					c_searchText;
    
    /** Site s�lectionn� **/
    private C_DescDistribution						m_siteSelection;
    
    static
    {
    	IMAGE_WIZARD = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("wizard/wizard_site.png");
		IMAGE_SITE = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("site_pp.png");
    }
    
	/**
	 * Constructeur.
	 * 
	 */
	public C_PageWizardSelectionSite() 
	{
		super(ID, "S�lection d'un site", IMAGE_WIZARD);
		
		// r�cup�ration des param�tres
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// raz flag complet
		setPageComplete(false);
		setDescription("S�l�ctionnez le site souhait� dans la liste ci dessous...");
		
		// job
		m_job = new C_JobRecuperationSitePlanet();
		m_job.addJobChangeListener(this);
	}
	
	/**
	 * V�rification.
	 * 
	 */
	private void f_CHECK()
	{
		// r�cup�ration chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// pas encore de s�lection
		if(m_siteSelection == null)
		{
			setPageComplete(false);
			return;
		}
		
		// pr�sence r�pertoire de d�veloppement local
		if(!C_ToolsFichiers.f_EXISTE(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT)))
		{
			setErrorMessage("Dossier de d�veloppement local absent");
			setPageComplete(false);
			return;
		}
		
		// pr�sence r�pertoire de d�veloppement local
		String cheminDevLocalRelease = cheminDevLocal + "/" + f_GET_SITE_SELECTION();
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease))
		{
			setErrorMessage("Un dossier du m�me nom est d�ja pr�sent dans le dossier de d�veloppement local");
			setPageComplete(false);
			return;
		}
		
		// pr�sence repository
		RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_SITE(m_siteSelection);
		if(folder == null)
		{
			setErrorMessage("Repository inexistant " + m_siteSelection.f_GET_NOM_RELEASE());
			setPageComplete(false);
			return;
		}
		
		// pr�sence fichier projet
		if(!C_ToolsSVN.f_CHECK_FOR_PROJECT_FILE(getShell(), folder))
		{
			setErrorMessage("Fichier .project  manquant " + m_siteSelection.f_GET_NOM_RELEASE());
			setPageComplete(false);
			return;
		}
		
		// ok
		setErrorMessage(null);
		setPageComplete(true);
	}
	
	/**
	 * Retourne lae site s�lectionn�.
	 * 
	 * @return site s�lectionn�
	 */
	public C_DescDistribution f_GET_SITE_SELECTION()
	{
		return m_siteSelection;
	}
	
	/**
	 * Envoi une requ�te au serveur.
	 * 
	 */
	private void f_REQUETE_SERVEUR()
	{
		// si serveur meylan site
		if(!C_DefinePreferencesPlugin.f_IS_MEYLAN_DOSSIER_SITE_ACCESSIBLE())
		{	
			setErrorMessage("R�pertoire sites non disponible !");
		}
		else if(!C_ToolsSite.f_IS_DESCRIPTIF_BASE_SITES_EXIST())
		{			
			setErrorMessage("Fichier descriptif base manquant !");
		}
		else
		{
			m_job.cancel();
			m_job.schedule();
			
			c_scrollForm.setExpandVertical(false);
		}
	}
	
	/**
	 * Mise � jour interface.
	 * 
	 */
	private void f_UPDATE_UI()
	{
		// acc�s interface par thread
		c_content.getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// raz des anciens contr�leurs
				C_ToolsSWT.f_RAZ_CONTROLS(c_content);
				
				// parcours des sites...
				for(C_DescDistribution site : C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites.f_GET_SITES(c_searchText.f_GET_SEARCH_TEXTE()))
				{
					Button btn = new Button(c_content, SWT.RADIO);
					btn.setText(site.f_GET_NOM_COMPLET_AFFAIRE() + " (" + site.f_GET_VERSION_COURT() + ")");
					btn.addSelectionListener(f_GET_SELECTION_LISTENER());
					btn.setData(site);
					btn.setImage(IMAGE_SITE.createImage());
					
					// s�curit� pr�sence dur serveur
					if(!site.f_IS_PRESENCE_DESC_SERVEUR())
						btn.setEnabled(false);
				}
				
				// mise � jour du scroll
				f_UPDATE_SCROLL_CONTENT();
			}
		});
	}
	
	/**
	 * Miuse � jour du contenu du scroll.
	 * 
	 */
	private void f_UPDATE_SCROLL_CONTENT()
	{
		Point size = c_content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_content.setSize(size);
	}
	
	/**
	 * Retourne l'�couteur de s�lection.
	 * 
	 * @return �couteur de s�lection
	 */
	public SelectionListener f_GET_SELECTION_LISTENER()
	{
		return this;
	}
	
	public void createControl(Composite parent) 
	{
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());
		
		// scroll form
		c_scrollForm = new ScrolledComposite(control, SWT.BORDER | SWT.V_SCROLL);
		c_scrollForm.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		c_scrollForm.setExpandHorizontal(true);
		c_scrollForm.setBackgroundMode(SWT.INHERIT_DEFAULT);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_scrollForm, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// contenu
		c_content = new Composite(c_scrollForm, SWT.NONE);
		c_content.setLayout(new GridLayout());
		c_scrollForm.setContent(c_content);
		
		// maj taille contenu
		Point size = c_content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_content.setSize(size);
		
		// text
		c_searchText = new C_TextSearch(control);
		c_searchText.addModifyListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_searchText, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// affectation du contr�leur
		setControl(control);
		
		// v�rifications
		f_CHECK();
		
		// requ�te au serveur
		f_REQUETE_SERVEUR();
	}
	
	public boolean canFlipToNextPage() 
	{
		return true;
	}

	@Override
	public void widgetSelected(SelectionEvent e) 
	{
		// r�cup�ration bouton s�lectionn�
		Button btn = (Button) e.getSource();
		
		// sur s�lection
		if(btn.getSelection())
		{
			// r�cup�ration s�lectio
			m_siteSelection = (C_DescDistribution) btn.getData();
			
			// v�rification
			f_CHECK();
		}
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		// mise � jour interface
		f_UPDATE_UI();
	}
	
	@Override
	public void done(IJobChangeEvent event) 
	{
		if(event.getResult().isOK())
		{
			// mise � jour interface
			f_UPDATE_UI();
		}
	}
	
	// inutilis�s
	@Override
	public void widgetDefaultSelected(SelectionEvent e)  {}
	@Override
	public void aboutToRun(IJobChangeEvent event) {}
	@Override
	public void awake(IJobChangeEvent event) {}
	@Override
	public void running(IJobChangeEvent event) {}
	@Override
	public void scheduled(IJobChangeEvent event) {}
	@Override
	public void sleeping(IJobChangeEvent event) {}
}
