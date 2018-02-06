package org.deltadore.planet.ui.vues.projet;

import java.io.File;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.plugin.actions.ouvertureDossiers.C_ActionOuvrirDossierDistribution;
import org.deltadore.planet.plugin.actions.projet.C_ActionExportationDistribution;
import org.deltadore.planet.plugin.actions.projet.C_ActionSuppressionProjetPlanet;
import org.deltadore.planet.plugin.actions.svn.C_ActionMergeProject;
import org.deltadore.planet.plugin.actions.svn.C_ActionSynchronizeProject;
import org.deltadore.planet.plugin.jobs.C_JobOuvertureFermetureProjet;
import org.deltadore.planet.swt.C_LabelIconeEtTexte;
import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

public class C_VueProjet extends ViewPart implements ISelectionListener, SelectionListener, IJobChangeListener
{
	public static final String 			ID = "VUE.PROJET";
	
	enum TYPE {BOLAND_CM , BORLAND_SERVEUR, WINDEV_CM, BORLAND_SERVEUR_KNX, BORLAND_CYCLONE, BORLAND_INFO_FONC};
	
	/** Projet **/
	private IProject							m_projet;
	
	/** Couleur fond détail **/
	private Color 								m_couleurDetail; 
	
	/** Couleur fond message **/
	private Color 								m_couleurMessage; 
	
	/** Composite parent **/
	private Composite 							c_parent;
	
	/** Composite à couche **/
	private Composite							c_compositeStack;
	
	/** stack layout **/
	private StackLayout 						c_layout;
	
	/** Composite détail projet **/
	private C_CompositeDetailProjet				c_compositeDetailProjet;
	
	/** Message **/
	private C_LabelIconeEtTexte 				c_compositeMessage;
	
	/** Outils **/
	private Composite 							c_compositeOutils;
	
	/** Action fermeture **/
	private C_ActionToggleOpenProjet			m_actionToggleFermerProjet;
	
	/** Actions projet **/
	private C_ActionOuvertureBorlandWindev 		m_actionBorlandCM;
	private C_ActionOuvertureBorlandWindev 		m_actionBorlandServeur;
	private C_ActionOuvertureBorlandWindev 		m_actionBorlandCyclone;
	private C_ActionOuvertureBorlandWindev 		m_actionBorlandKnx;
	private C_ActionOuvertureBorlandWindev 		m_actionBorlandInfoFonc;
	private C_ActionOuvertureBorlandWindev 		m_actionWindevCM;
	private IAction							 	m_actionOuvertureDossier;
	private IAction							 	m_actionSuppression;
	private IAction							 	m_actionExportation;
	private IAction							 	m_actionSynchronize;
	private IAction							 	m_actionMerge;
	
	public static Image 						m_imgRelease;
	public static Image 						m_imgOff;
	public static Image 						m_imgBat;
	public static Image 						m_imgCloud;
	public static ImageDescriptor				m_imgDescBorland;
	public static ImageDescriptor 				m_imgDescWindev;
	
	private Button 								c_btnDebug;
	private Button 								c_btnAcces;
//	private Button 								c_btnMemory;
	
	static
	{
		m_imgRelease = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("fond.png").createImage();
		m_imgBat = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("fond_bat.png").createImage();
		m_imgOff = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("fond_off.png").createImage();
		m_imgCloud = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("fond_cloud.png").createImage();
		m_imgDescBorland = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("borland.png");
		m_imgDescWindev = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("windev.png");
	}
	
	/**
	 * Retourne la vue.
	 * 
	 * @return la vue
	 */
	public static C_VueProjet f_FIND_VUE()
	{
		// récupération vue projet
		for(IWorkbenchWindow window : C_PlanetPluginActivator.f_GET().getWorkbench().getWorkbenchWindows())
		{
			C_VueProjet vueProjet = (C_VueProjet) window.getActivePage().findView(ID);
			
			if(vueProjet != null)
				return vueProjet;
		}
		
		return null;
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// écoute les sélections
		C_PlanetPluginActivator.f_GET().getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}
	
	@Override
	public void createPartControl(Composite parent) 
	{
		// récupération parent
		c_parent = parent;
		
		// initialisation
		f_INIT();
		
		// initialisation barre des boutons
		f_INIT_BARRE_BOUTONS();
		f_INIT_MENU();
		
		// layout
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginRight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		
		// composite à couche
		f_INIT_STACK_VUE(parent);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_compositeStack, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// outils
		c_compositeOutils = new Composite(parent, SWT.NONE);
		c_compositeOutils.setBackgroundMode(SWT.INHERIT_DEFAULT);
		m_couleurDetail = new Color(parent.getDisplay(), 139, 138, 170);
		m_couleurMessage = new Color(parent.getDisplay(), 184, 184, 184);
		c_compositeOutils.setBackground(m_couleurMessage);
		c_compositeOutils.setLayout(new RowLayout(SWT.HORIZONTAL));
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_compositeOutils, 1, 1, false, false, GridData.FILL, GridData.FILL);
		
		// option debug
		c_btnDebug = new Button(c_compositeOutils, SWT.CHECK);
		c_btnDebug.setText("Debug");
		c_btnDebug.setData("DEBUG");
		c_btnDebug.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("debug.png").createImage());
		boolean stateDebug = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DEBUG);
		c_btnDebug.setSelection(stateDebug);
		c_btnDebug.addSelectionListener(this);
		
		// option sans code accès
		c_btnAcces = new Button(c_compositeOutils, SWT.CHECK);
		c_btnAcces.setText("Libre");
		c_btnAcces.setData("FREE");
		c_btnAcces.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("lock_open.png").createImage());
		boolean stateCode = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_SANS_CODE_ACCES);
		c_btnAcces.setSelection(stateCode);
		c_btnAcces.addSelectionListener(this);
		
//		// increase mémoire
//		c_btnMemory = new Button(c_compositeOutils, SWT.CHECK);
//		c_btnMemory.setText("Allouer plus de mémoire");
//		c_btnMemory.setData("MEMORY");
//		c_btnMemory.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("control_panel.png").createImage());
//		boolean stateDicoServeur = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_MEMOIRE);
//		c_btnMemory.setSelection(stateDicoServeur);
//		c_btnMemory.addSelectionListener(this);
		
		f_SET_TOOLS_VISIBLES(false);
	}
	
	/**
	 * Mise à jour du bouton Ouvrir / Fermer projet.
	 * 
	 */
	private void f_UPDATE_BOUTON_TOGGLE()
	{
		if(m_projet.getProject().isOpen())
		{
			m_actionToggleFermerProjet.setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_redo_red.png"));
			m_actionToggleFermerProjet.setToolTipText("Fermer le projet");
		}
		else
		{
			m_actionToggleFermerProjet.setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_redo_green.png"));
			m_actionToggleFermerProjet.setToolTipText("Ouvrir le projet");
		}
	}
	
	/**
	 * Initialisation de la barre de boutons de la vue.
	 * 
	 */
	private void f_INIT_BARRE_BOUTONS()
	{
		// action bar
		IActionBars actionBars = getViewSite().getActionBars();
		
		// toolbar manager
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		// action exportation
		m_actionExportation = new C_ActionExportationDistribution().f_GET_ACTION();
		m_actionExportation.setEnabled(false);
		m_actionExportation.setToolTipText("Exportation de la distribution");
		toolBar.add(m_actionExportation);
		
		// action merge
		m_actionMerge = new C_ActionMergeProject().f_GET_ACTION();
		m_actionMerge.setEnabled(false);
		m_actionMerge.setToolTipText("SVN Merge");
		toolBar.add(m_actionMerge);
		
		// action synchronize
		m_actionSynchronize = new C_ActionSynchronizeProject().f_GET_ACTION();
		m_actionSynchronize.setEnabled(false);
		m_actionSynchronize.setToolTipText("SVN Synchronize");
		toolBar.add(m_actionSynchronize);
		
		// action suppression
		m_actionSuppression = new C_ActionSuppressionProjetPlanet().f_GET_ACTION();
		m_actionSuppression.setEnabled(false);
		m_actionSuppression.setToolTipText("Suppression du projet (sources et binaires)");
		toolBar.add(m_actionSuppression);
		
		// action ouverture dossier
		m_actionOuvertureDossier = new C_ActionOuvrirDossierDistribution().f_GET_ACTION();
		m_actionOuvertureDossier.setEnabled(false);
		m_actionOuvertureDossier.setToolTipText("Ouvrir le dossier du projet");
		toolBar.add(m_actionOuvertureDossier);
		
		// action fermeture projet
		m_actionToggleFermerProjet = new C_ActionToggleOpenProjet();
		m_actionToggleFermerProjet.setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_redo_green.png"));
		m_actionToggleFermerProjet.setEnabled(false);
		m_actionToggleFermerProjet.setToolTipText("Fermer le projet");
		toolBar.add(m_actionToggleFermerProjet);
	}
	
	/**
	 * Initialisation du menu de la vue.
	 * 
	 */
	private void f_INIT_MENU()
	{
		// action bar
		IActionBars actionBars = getViewSite().getActionBars();
		
		// menu manager
		IMenuManager menu = actionBars.getMenuManager();
		
		// borland CM
		m_actionBorlandCM = new C_ActionOuvertureBorlandWindev(TYPE.BOLAND_CM);
		m_actionBorlandCM.setEnabled(false);
		menu.add(m_actionBorlandCM);
		
		// boeland Serveur
		m_actionBorlandServeur = new C_ActionOuvertureBorlandWindev(TYPE.BORLAND_SERVEUR);
		m_actionBorlandServeur.setEnabled(false);
		menu.add(m_actionBorlandServeur);
		
		// borland KNX
		m_actionBorlandKnx = new C_ActionOuvertureBorlandWindev(TYPE.BORLAND_SERVEUR_KNX);
		m_actionBorlandKnx.setEnabled(false);
		menu.add(m_actionBorlandKnx);
		
		// borland cyclone
		m_actionBorlandCyclone = new C_ActionOuvertureBorlandWindev(TYPE.BORLAND_CYCLONE);
		m_actionBorlandCyclone.setEnabled(false);
		menu.add(m_actionBorlandCyclone);
		
		// borland info fonc
		m_actionBorlandInfoFonc = new C_ActionOuvertureBorlandWindev(TYPE.BORLAND_INFO_FONC);
		m_actionBorlandInfoFonc.setEnabled(false);
		menu.add(m_actionBorlandInfoFonc);
		
		// windev CM
		m_actionWindevCM = new C_ActionOuvertureBorlandWindev(TYPE.WINDEV_CM);
		m_actionWindevCM.setEnabled(false);
		menu.add(m_actionWindevCM);
	}
	
	/**
	 * Initialisation composite à couche.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_STACK_VUE(Composite parent)
	{
		// composite
		c_compositeStack = new Composite(parent, SWT.NONE);
		
		// layout
		c_layout = new StackLayout();
		c_compositeStack.setLayout(c_layout);
		
		// fond composant
		
		// composite détail projet
		c_compositeDetailProjet = new C_CompositeDetailProjet(c_compositeStack);
		
		// message
		c_compositeMessage = new C_LabelIconeEtTexte(c_compositeStack, false);
		c_compositeMessage.f_SET_ICONE_ET_TEXTE(null, "- Aucun projet sélectionné -");
		c_compositeMessage.setBackgroundImage(m_imgOff);
		c_layout.topControl = c_compositeMessage;
	}
	
	/**
	 * Remplacement du projet de la vue.
	 * 
	 * @param projet nouveau projet
	 */
	private void f_SET_PROJET(IProject projet)
	{
		// sur changement
		if(m_projet != projet)
		{
			// récupération projet
			m_projet = projet;
			
			// mise à jour des boutons
			f_UPDATE_BOUTONS();
			
			// mise à jour des cartes
			f_UPDATE_CARDS();
		}
	}
	
	/**
	 * Mise à jour des boutons de la vue.
	 * 
	 */
	private void f_UPDATE_BOUTONS()
	{
		// accès interface par thread
		c_parent.getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// récupération projet JAVA
				IJavaProject javaProject = JavaCore.create(m_projet);
				
				// si projet sélectionné n'est pas un projet java
				if(m_projet == null || javaProject == null || !C_ToolsWorkbench.f_IS_PROJET_PLANET(m_projet))
				{
					// boutons inactifs
					m_actionToggleFermerProjet.setEnabled(!(m_projet == null));
					m_actionOuvertureDossier.setEnabled(false);
					m_actionExportation.setEnabled(false);
					m_actionSuppression.setEnabled(false);
					m_actionBorlandCM.setEnabled(false);
					m_actionBorlandServeur.setEnabled(false);
					m_actionBorlandCyclone.setEnabled(false);
					m_actionBorlandKnx.setEnabled(false);
					m_actionBorlandInfoFonc.setEnabled(false);
					m_actionWindevCM.setEnabled(false);
					m_actionSynchronize.setEnabled(false);
					m_actionMerge.setEnabled(false);
				}
				else
				{
					// MAJ bouton ouverture/fermeture projet
					f_UPDATE_BOUTON_TOGGLE();
					
					// récupération du descriptif de release
					C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet);
					
					// MAJ actions
					m_actionToggleFermerProjet.setEnabled(true);
					m_actionOuvertureDossier.setEnabled(m_projet.isOpen());
					m_actionExportation.setEnabled(m_projet.isOpen());
					m_actionSuppression.setEnabled(m_projet.isOpen());
					m_actionBorlandCM.setEnabled(m_projet.isOpen() && descRelease.f_IS_ORGANISATION_AVANT_3_0());
					m_actionBorlandServeur.setEnabled(m_projet.isOpen());
					m_actionBorlandCyclone.setEnabled(m_projet.isOpen());
					m_actionBorlandKnx.setEnabled(m_projet.isOpen());
					m_actionBorlandInfoFonc.setEnabled(m_projet.isOpen() && descRelease.f_IS_ORGANISATION_AVANT_2_5());
					m_actionWindevCM.setEnabled(m_projet.isOpen() && descRelease.f_IS_ORGANISATION_AVANT_3_0());
					m_actionSynchronize.setEnabled(m_projet.isOpen());
					m_actionMerge.setEnabled(m_projet.isOpen());
				}
			}
		});
	}
	
	/**
	 * Retourne le nom de la configuration sélectionnée.
	 * 
	 * @return nom de la configuration sélectionnée
	 */
	public String f_GET_CONFIGURATION_SELECTIONNEE()
	{
		String strConfig = c_compositeDetailProjet.f_GET_CONFIGURATION_SELECTIONNEE();
		
		if(strConfig.equalsIgnoreCase("Config"))
			return "Config";
		else 
			return c_compositeDetailProjet.f_GET_CONFIGURATION_SELECTIONNEE();
	}
	
	/**
	 * Mise à jour des configurations.
	 * 
	 * @return true si succès
	 */
	public boolean f_UPDATE_CONFIGS()
	{
		// mise à jour des configurations
		return c_compositeDetailProjet.f_UPDATE_CONFIGS();
	}
	
	/**
	 * Mise à jour des configurations.
	 * 
	 * @return true si succès
	 */
	public boolean f_UPDATE_CONFIGS_AND_SELECT(String selection)
	{
		// mise à jour des configurations
		return c_compositeDetailProjet.f_UPDATE_CONFIGS_AND_SELECT(selection);
	}
	
	@Override
	public void setFocus() 
	{
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
		if(part.getSite().getId().equalsIgnoreCase(JavaUI.ID_PACKAGES))
		{
			if(!(selection instanceof TreeSelection))
					return;
			
			// récupération projet
			IProject projet = C_ToolsWorkbench.f_GET_PROJET_SELECTIONNE((TreeSelection) selection);
			
			// changement du projet de la vue
			f_SET_PROJET(projet);
		}
	}
	
	private void f_SET_TOOLS_VISIBLES(boolean etat)
	{
		c_btnDebug.setVisible(etat);
		c_btnAcces.setVisible(etat);
//		c_btnMemory.setVisible(etat);
//		c_btnDicoServeur.setVisible(etat);
	}
	
	/**
	 * Mise à jour des cartes.
	 * 
	 */
	private void f_UPDATE_CARDS()
	{
		// accès interface par thread
		c_parent.getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				try
				{
					if(m_projet == null)
					{
						c_layout.topControl = c_compositeMessage;
						c_compositeOutils.setBackground(m_couleurMessage);
						
						f_SET_TOOLS_VISIBLES(false);
					}
					else if(m_projet.isOpen())
					{
						if(C_ToolsWorkbench.f_IS_PROJET_PLANET(m_projet))
						{
							IJavaProject javaProject = (IJavaProject) JavaCore.create(m_projet); 
							
							c_compositeDetailProjet.f_SET_PROJET(javaProject);
							c_layout.topControl = c_compositeDetailProjet;
							c_compositeOutils.setBackground(m_couleurDetail);
							f_SET_TOOLS_VISIBLES(true);
						}
						else
						{
							c_compositeMessage.f_SET_ICONE_ET_TEXTE(null, m_projet.getName());
							c_compositeMessage.layout();
							c_layout.topControl = c_compositeMessage;
							c_compositeOutils.setBackground(m_couleurMessage);
							f_SET_TOOLS_VISIBLES(false);
						}
					}
					else
					{
						c_compositeMessage.f_SET_ICONE_ET_TEXTE(null, m_projet.getName());
						c_compositeMessage.layout();
						c_compositeOutils.setBackground(m_couleurMessage);
						c_layout.topControl = c_compositeMessage;
						f_SET_TOOLS_VISIBLES(false);
					}
					
					c_parent.layout();
					c_compositeStack.layout();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Retourne vrai si il s'agit d'un site.
	 * 
	 * @return true si projet site
	 */
	public boolean f_IS_SITE_SELECTIONNE()
	{
		return c_compositeDetailProjet.f_IS_SITE_SELECTIONNE();
	}
	
	/**
	 * Retourne le descriptif site.
	 * 
	 * @return descriptif site
	 */
	public C_DescDistribution f_GET_DESC_SITE()
	{
		return c_compositeDetailProjet.f_GET_DESC_SITE();
	}
	
	/**
	 * Retourne le descriptif release.
	 * 
	 * @return descriptif release
	 */
	public C_DescRelease f_GET_DESC_RELEASE()
	{
		return c_compositeDetailProjet.f_GET_DESC_RELEASE();
	}
	
	private C_VueProjet f_GET_VUE()
	{
		return this;
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) 
	{
	    if (ISizeProvider.class == adapter) 
	    {
	        return new ISizeProvider() 
	        {
	            public int getSizeFlags(boolean width) 
	            {
	                return SWT.MIN | SWT.MAX | SWT.FILL;
	            }

	            public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular, int preferredResult) 
	            {
	            	if(width)
	            		return 300;
	            	else
	            		return 175;
	            }
	        };
	    }
	    return super.getAdapter(adapter);
	}

	@Override
	public void widgetSelected(SelectionEvent e) 
	{
		Button btn = (Button) e.getSource();
		
		if(btn.getData().toString().equalsIgnoreCase("DEBUG"))
		{
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DEBUG, btn.getSelection());
		}
		else if(btn.getData().toString().equalsIgnoreCase("FREE"))
		{
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_SANS_CODE_ACCES, btn.getSelection());
		}
		else if(btn.getData().toString().equalsIgnoreCase("DICO"))
		{
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DICO_SERVEUR, btn.getSelection());
		}
		else if(btn.getData().toString().equalsIgnoreCase("MEMORY"))
		{
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_MEMOIRE, btn.getSelection());
		}		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) 
	{
		
	}
	
	/**
	 * Action toggle ouverture projet.
	 * 
	 */
	private class C_ActionToggleOpenProjet extends Action
	{
		@Override
		public void run() 
		{
			if(m_projet.getProject().isOpen())
			{
				C_JobOuvertureFermetureProjet job = new C_JobOuvertureFermetureProjet(m_projet, false);
				job.addJobChangeListener(f_GET_VUE());
				job.schedule();
			}
			else
			{
				C_JobOuvertureFermetureProjet job = new C_JobOuvertureFermetureProjet(m_projet, true);
				job.addJobChangeListener(f_GET_VUE());
				job.schedule();
			}
		}
	}
	
	@Override
	public void done(IJobChangeEvent arg0) 
	{
		f_UPDATE_BOUTON_TOGGLE();
		f_UPDATE_BOUTONS();
		c_compositeDetailProjet.f_UPDATE_INFORMATIONS();
		f_UPDATE_CARDS();
	}
	
	/**
	 * Action ouverture des projets Borland et Windev..
	 * 
	 */
	private class C_ActionOuvertureBorlandWindev extends Action
	{
		/** type de projet **/
		private TYPE		m_type;
		
		/**
		 * Constructeur.
		 * 
		 * @param type type de projet
		 */
		public C_ActionOuvertureBorlandWindev(TYPE type)
		{
			super();
			
			// récupération des paramètres
			m_type = type;
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			switch(m_type)
			{
				case BOLAND_CM:
					setText("Projet Borland CM");
					setImageDescriptor(m_imgDescBorland);
					break;
					
				case BORLAND_SERVEUR:
					setText("Projet Borland Serveur");
					setImageDescriptor(m_imgDescBorland);
					break;
					
				case BORLAND_SERVEUR_KNX:
					setText("Projet Borland Serveur KNX");
					setImageDescriptor(m_imgDescBorland);
					break;
					
				case BORLAND_CYCLONE:
					setText("Projet Borland Cyclone");
					setImageDescriptor(m_imgDescBorland);
					break;
					
				case BORLAND_INFO_FONC:
					setText("Projet Borland Info Fonc");
					setImageDescriptor(m_imgDescBorland);
					break;
					
				case WINDEV_CM:
					setText("Projet Windev CM");
					setImageDescriptor(m_imgDescWindev);
					break;
			}
		}
		
		@Override
		public void run() 
		{
			switch(m_type)
			{
				case BOLAND_CM:
					File fileIdeCM = C_ToolsDistribution.f_GET_FILE_PROJET_BORLAND_CM_OF_PROJECT(m_projet);
					C_ToolsRunnable.f_EXECUTE(fileIdeCM);
					break;
					
				case BORLAND_SERVEUR:
					File fileIdeServeur = C_ToolsDistribution.f_GET_FILE_PROJET_BORLAND_SERVEUR_OF_PROJECT(m_projet);
					C_ToolsRunnable.f_EXECUTE(fileIdeServeur);
					break;
					
				case BORLAND_SERVEUR_KNX:
					File fileIdeServeurKnx = C_ToolsDistribution.f_GET_FILE_PROJET_BORLAND_SERVEUR_KNX_OF_PROJECT(m_projet);
					C_ToolsRunnable.f_EXECUTE(fileIdeServeurKnx);
					break;
					
				case BORLAND_CYCLONE:
					File fileIdeCyclone = C_ToolsDistribution.f_GET_FILE_PROJET_BORLAND_CYCLONE_OF_PROJECT(m_projet);
					C_ToolsRunnable.f_EXECUTE(fileIdeCyclone);
					break;
					
				case BORLAND_INFO_FONC:
					File fileIdeInfoFonc = C_ToolsDistribution.f_GET_FILE_PROJET_BORLAND_INFO_FONC_OF_PROJECT(m_projet);
					C_ToolsRunnable.f_EXECUTE(fileIdeInfoFonc);
					break;
					
				case WINDEV_CM:
					File fileWindevCM = C_ToolsDistribution.f_GET_FILE_PROJET_WINDEV_CME_OF_PROJECT(m_projet);
					C_ToolsRunnable.f_EXECUTE(fileWindevCM);
					break;
			}
		}
	}

	@Override
	public void aboutToRun(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awake(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void running(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduled(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sleeping(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}