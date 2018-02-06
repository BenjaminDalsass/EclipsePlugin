package org.deltadore.planet.ui.vues.images;

import java.io.File;

import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.swt.C_ImageGridPreview;
import org.deltadore.planet.swt.C_TextSearch;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

public class C_VueImages extends ViewPart implements ModifyListener, ISelectionListener, IPartListener2
{
	public static final String 			ID = "VUE.IMAGES";

	/** Projet **/
	private IJavaProject				m_projet;
	
	/** Image view **/
	private C_ImageGridPreview 			c_imageView; 
	
	/** TextField de recherche **/
	private C_TextSearch				c_searchText;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_VueImages() 
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
		// écoute les sélections
		C_PlanetPluginActivator.f_GET().getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}

	@Override
	public void createPartControl(Composite parent) 
	{
		f_INIT_BARRE_BOUTONS();
		
		// parent layout
		parent.setLayout(new GridLayout());
		parent.setBackground(C_DefineCouleur.f_GET_COULEUR(144, 144, 150));
		
		c_imageView = new C_ImageGridPreview(parent);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_imageView, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// text
		c_searchText = new C_TextSearch(parent);
		c_searchText.addModifyListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_searchText, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// ecoute le focus
		getViewSite().getPage().addPartListener(this);
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
		toolBar.add(new C_ActionLink());
	}

	@Override
	public void modifyText(ModifyEvent e) 
	{
		c_imageView.f_SET_FILTRE(c_searchText.f_GET_SEARCH_TEXTE());
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
		if(part.getSite().getId().equalsIgnoreCase(JavaUI.ID_PACKAGES))
		{
			// sécurité
			if(!(selection instanceof TreeSelection))
					return;
			
			// récupération projet
			IJavaProject projet = C_ToolsWorkbench.f_GET_PROJET_JAVA_SELECTIONNE((TreeSelection) selection);
			f_SET_PROJET(projet);
		}
	}
	
	private void f_SET_PROJET(IJavaProject projet)
	{
		if(projet != null
		&& projet.getProject().isOpen())
		{
			if(m_projet != projet
			&& C_ToolsWorkbench.f_IS_PROJET_PLANET(projet.getProject()))
			{
				// récupération projet
				m_projet = projet;
				
				// récupération descriptif release
				C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
				
				if(descRelease.f_IS_ORGANISATION_INITIALE())
				{
					String repAff = projet.getProject().getLocation().toFile().getParent();
					c_imageView.f_SET_REPERTOIRE_IMAGE(new File(repAff + File.separator + "_Winman_java" + File.separator + "_Winman_jclient" + File.separator + "Images"));
				}
				else
				{
					String repAff = projet.getProject().getLocation().toString();
					c_imageView.f_SET_REPERTOIRE_IMAGE(new File(repAff + File.separator + "Ressources" + File.separator + "Images"));
				}
				
				if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.LINK_IMAGES))
					c_imageView.f_UPDATE_UI();
				else
					c_imageView.f_RAZ();
			}
		}
		else
		{
			// raz vue images
			m_projet = null;
			c_imageView.f_SET_REPERTOIRE_IMAGE(null);
			c_imageView.f_RAZ();
		}
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
		
		// récupération projet
		IJavaProject projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(getSite().getWorkbenchWindow());
		f_SET_PROJET(projet);
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) 
	{
	}
	
	@Override
	public void setFocus() {}
	
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
			setToolTipText("Attacher la vue Images aux séléctions de projets");
		}
		
		@Override
		public void run() 
		{
			c_imageView.f_UPDATE_UI();
		}
	}
	
	private class C_ActionLink extends Action 
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionLink()
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
			setChecked(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.LINK_IMAGES));
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("link.png"));
		}
		
		@Override
		public void run() 
		{
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.LINK_IMAGES, isChecked());
			
			if(isChecked())
				c_imageView.f_UPDATE_UI();
		}
	}
	
}