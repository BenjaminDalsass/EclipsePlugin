package org.deltadore.planet.ui.vues.sites;

import java.io.File;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.model.define.C_DefineImages;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.swt.C_LabelIconeEtTexte;
import org.deltadore.planet.swt.C_TextSearch;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsSite;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

public class C_VueSitesPlanet extends ViewPart implements ModifyListener, IJobChangeListener, MouseListener, SelectionListener, IPartListener2
{
	public static final String 			ID = "VUE.SITES_PLANET";

	/** images **/
	private ImageDescriptor 			c_imageEntree;
	private ImageDescriptor 			c_imageEntreeAbsente;
	private ImageDescriptor 			c_imageEntreePresente;
	private ImageDescriptor 			c_imageReleaseOk;
	
	/** Descriptifs release **/
	private C_DescRelease[] 			m_releases; 
	
	/** Combo version **/
	private Combo						c_comboVersions;
	
	/** TextField de recherche **/
	private C_TextSearch				c_searchText;
	
	/** scrollable **/
	private ScrolledComposite			c_scollForm;
	
	/** contenu scroll **/
	private Composite 					c_content; 
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_VueSitesPlanet() 
	{
		super();
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// images
		c_imageEntree = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("bullet_triangle_grey.png");
		c_imageEntreeAbsente = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("bullet_triangle_red.png");
		c_imageEntreePresente = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("bullet_triangle_green.png");
		c_imageReleaseOk = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("jar_bean_new_pp.png");
		
		// job
		C_Bases.f_AJOUTE_ECOUTEUR_JOB(this);
	}

	@Override
	public void createPartControl(Composite parent) 
	{
		// initalisation barre de boutons
		f_INIT_BARRE_BOUTONS();
		
		// parent layout
		parent.setLayout(new GridLayout(2, false));
		parent.setBackground(C_DefineCouleur.VIOLET);
		
		// création scroll composite
		c_scollForm = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		c_scollForm.setExpandHorizontal(true);
		c_scollForm.setExpandVertical(true);
		c_scollForm.getVerticalBar().setIncrement(50);
		c_scollForm.getVerticalBar().setPageIncrement(100);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_scollForm, 2, 1, true, true, GridData.FILL, GridData.FILL);
		
		// création contenu du scroll
		c_content = new Composite(c_scollForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.verticalSpacing = 1;
		c_content.setLayout(layout);
		c_scollForm.setContent(c_content);
		
		// combo versions
		c_comboVersions = new Combo(parent, SWT.READ_ONLY);
		c_comboVersions.addSelectionListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_comboVersions, 1, 1, false, false, GridData.FILL, GridData.FILL);
		
		// text
		c_searchText = new C_TextSearch(parent);
		c_searchText.addModifyListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_searchText, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// mise à jour interface
		f_UPDATE_UI();
		f_UPDATE_COMBO_VERSIONS();
		
		// ecoute le focus
		getViewSite().getPage().addPartListener(this);
	}
	
	/**
	 * Envoi une requête au serveur.
	 * 
	 */
	private void f_REQUETE_SERVEUR()
	{
//		C_Bases.f_RUN_JOB_RECUPERATION_AFFAIRES_PLANET();
		c_scollForm.setExpandVertical(false);
	}
	
	/**
	 * Mise à jour de la combo des versions.
	 * 
	 */
	private void f_UPDATE_COMBO_VERSIONS()
	{
		// accès interface par thread
		c_content.getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				m_releases = C_Bases.f_GET_BASE_RELEASES().f_GET_TAG_RELEASES();
				
				if(m_releases == null)
				{
					c_comboVersions.setEnabled(false);
					return;
				}
				
				String[] items = new String[m_releases.length+1];
				items[0] = "Toutes";
				
				for( int  i = 0 ; i < m_releases.length ; i++ )
				{
					items[i+1] = m_releases[i].f_GET_VERSION_COURT();
				}
				
				c_comboVersions.setItems(items);
				c_comboVersions.setEnabled(true);
				c_comboVersions.select(0);
			}
		});
	}
	
	/**
	 * Mise à jour interface.
	 * 
	 */
	private void f_UPDATE_UI()
	{
		// accès interface par thread
		c_content.getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// raz des anciens contrôleurs
				C_ToolsSWT.f_RAZ_CONTROLS(c_content);
				
				if(C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites == null)
				{
					// composite message
					C_LabelIconeEtTexte compositeMessage = new C_LabelIconeEtTexte(c_content, false);
					C_ToolsSWT.f_GRIDLAYOUT_DATA(compositeMessage, 1, 1, true, true, GridData.FILL, GridData.FILL);
					c_scollForm.setExpandVertical(true);
					
					if(!C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_is_serveurMeylanAccessible)
						compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("message_warning.png"), "Serveur Sites non disponible");
					else if(!C_ToolsSite.f_IS_DESCRIPTIF_BASE_SITES_EXIST())
						compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("message_error.png"), "Fichier descriptif base manquant !");
					else
						compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_refresh_blue.png"), "Initialisation");
				}
				else
				{
					// release selectionne
					C_DescRelease release = null;
					int index = c_comboVersions.getSelectionIndex();
					if(index > 0)
						release = m_releases[index-1];
					
					// parcours des sites...
					for(C_DescDistribution site : C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites.f_GET_SITES(c_searchText.f_GET_SEARCH_TEXTE(), release))
					{
						// data layout
						GridData data = new GridData();
						data.horizontalAlignment = SWT.FILL;
						data.grabExcessHorizontalSpace = true;
						
						// création étiquette référence
						Composite c = f_GET_LABEL_DISTRIBUTION(site, c_content);
						c.setLayoutData(data);
					}
					
					c_scollForm.setExpandVertical(false);
				}
				
				// mise à jour du scroll
				f_UPDATE_SCROLL_CONTENT();
			}
		});
	}
	
	/**
	 * Mise à jour du contenu du scroll.
	 * 
	 */
	private void f_UPDATE_SCROLL_CONTENT()
	{
		Point size = c_content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_content.setSize(size);
	}
	
	/**
	 * Initialisation de la barre de boutons.
	 * 
	 */
	private void f_INIT_BARRE_BOUTONS()
	{
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		toolBar.add(new C_ActionRefresh());
		
		getViewSite().getActionBars().getMenuManager().add(new C_ActionOuvrirDossierServeur());
	}

	/**
	 * Retourne un label de distribution.
	 * 
	 * @param site site associé
	 * @param parent composant parent
	 * @return label de distribution
	 */
	private Composite f_GET_LABEL_DISTRIBUTION(C_DescDistribution site, Composite parent)
	{
		// variables
		GridData data = null;
		
		// panel
		Composite composite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(4, false);
		composite.setLayout(layout);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		composite.setBackground(new Color(c_scollForm.getDisplay(), 245, 240, 255));
		composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// label image
		Label label = new Label(composite, SWT.NONE);
		if(!site.f_IS_PRESENCE_DESC_SERVEUR())
			label.setImage(c_imageEntreeAbsente.createImage());
		else if(C_ToolsSite.f_IS_SITE_IS_IN_REPERTOIRE_LOCAL(site)) // todo project existe
			label.setImage(c_imageEntreePresente.createImage());
		else
			label.setImage(c_imageEntree.createImage());
		label.setToolTipText(site.f_GET_REPERTOIRE_SERVEUR());
		data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		label.setLayoutData(data);
		
		// texte
		Label text = new Label(composite, SWT.NONE);
		text.setText(site.f_GET_NOM_COMPLET_AFFAIRE());
		data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);
		
		// repertoire
		Label labelRep = new Label(composite, SWT.NONE);
		labelRep.setImage(C_DefineImages.REPERTOIRE_OUVRIR);
		labelRep.setToolTipText("Ouvrir le dossier serveur du site");
		File rep = new File(C_ToolsSite.f_GET_CHEMIN_REPERTOIRE_PARENT_SITE(site));
		labelRep.addMouseListener(this);
		labelRep.setData(rep);
		labelRep.setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_HAND));
		C_ToolsSWT.f_GRIDLAYOUT_DATA(labelRep, 1, 1, false, false, GridData.CENTER, GridData.CENTER);
		
		// version
		Composite labelVersion = f_GET_LABEL_VERSION(composite, site);
		labelVersion.setData("VERSION");
		data = new GridData();
		labelVersion.setLayoutData(data);
		
		return composite;
	}
	
	/**
	 * Retourne un label de version.
	 * 
	 * @param parent control parent
	 * @param site site associé
	 * @return le label
	 */
	private Composite f_GET_LABEL_VERSION(Composite parent, C_DescDistribution site)
	{
		Composite labelVersion = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		labelVersion.setLayout(layout);		
		labelVersion.setBackground(new Color(parent.getDisplay(), 200, 200, 200));
			
		// icône
		Label labelIcone = new Label(labelVersion, SWT.NONE);
		labelIcone.setToolTipText(labelVersion.getToolTipText());
		labelIcone.setImage(c_imageReleaseOk.createImage());
		
		// texte
		Label labelTexte = new Label(labelVersion, SWT.NONE);
		labelTexte.setToolTipText(labelVersion.getToolTipText());
		labelTexte.setText(site.f_GET_VERSION_COURT());
		
		return labelVersion;
	}
	
	@Override
	public void modifyText(ModifyEvent e) 
	{
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	@Override
	public void done(IJobChangeEvent event) 
	{
		// mise à jour interface
		f_UPDATE_UI();
		
		// mise à jour combo versions
		f_UPDATE_COMBO_VERSIONS();
	}

	@Override
	public void mouseDown(MouseEvent e) 
	{
		Control c = (Control) e.getSource();
		File rep = (File) c.getData();
		C_ToolsRunnable.f_EXECUTE(rep);
	}

	// inutilisés
	@Override
	public void mouseDoubleClick(MouseEvent e) {}
	@Override
	public void mouseUp(MouseEvent e) {}
	@Override
	public void setFocus() {}
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
	
	/**
	 * ACTION REFRESH
	 *
	 */
	private class C_ActionRefresh extends Action
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionRefresh()
		{
			super();
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("refresh_pp.png"));
			setText("Actualiser");
		}
		
		@Override
		public void run() 
		{
			// mise à jour interface
			f_REQUETE_SERVEUR();
		}
	}
	
	
	/**
	 * ACTION OUVRIS DOSSIER SERVEUR
	 *
	 */
	private class C_ActionOuvrirDossierServeur extends Action
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionOuvrirDossierServeur()
		{
			super();
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("dossier_pp.png"));
			setText("Ouvrir le dossier serveur");
		}
		
		@Override
		public void run() 
		{
			String chemin = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_SITE);
			C_ToolsRunnable.f_EXECUTE(new File(chemin));
		}
	}


	@Override
	public void widgetDefaultSelected(SelectionEvent selectionEvent) 
	{
		
	}

	@Override
	public void widgetSelected(SelectionEvent selectionEvent) 
	{
		f_UPDATE_UI();
	}

	@Override
	public void partActivated(IWorkbenchPartReference arg0) 
	{
		c_searchText.setFocus();
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partClosed(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partHidden(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference arg0)
	{
	}

	@Override
	public void partOpened(IWorkbenchPartReference arg0)
	{
		c_searchText.setFocus();
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) 
	{
	}
}