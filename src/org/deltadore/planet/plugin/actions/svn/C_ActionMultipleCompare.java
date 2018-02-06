package org.deltadore.planet.plugin.actions.svn;

import org.deltadore.planet.model.define.C_DefineInfosServeurs;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.ISVNRemoteFile;
import org.tigris.subversion.subclipse.core.ISVNResource;
import org.tigris.subversion.subclipse.core.repo.SVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.resources.RemoteFile;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.subclipse.ui.actions.ShowDifferencesAsUnifiedDiffActionWC;
import org.tigris.subversion.subclipse.ui.compare.SVNLocalBranchTagCompareInput;
import org.tigris.subversion.subclipse.ui.compare.SVNLocalCompareInput;
import org.tigris.subversion.subclipse.ui.compare.SVNLocalCompareSummaryInput;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tmatesoft.svn.core.SVNURL;

public class C_ActionMultipleCompare implements IViewActionDelegate, IMenuCreator, SelectionListener
{
	/** Workbench window **/
	protected IWorkbenchWindow 				m_window;
	
	private IAction							m_action;
	
	@Override
	public Menu getMenu(Control parent)
	{
		// création menu
		Menu menu = new Menu(parent);
		
		// initioalisation menu
		f_INIT_MENU(menu);
		
		return menu;
	}
	
	@Override
	public Menu getMenu(Menu parent) 
	{
		// création menu
		Menu menu = new Menu(parent);
		
		// initioalisation menu
		f_INIT_MENU(menu);
		
		return menu;
	}
	
	/**
	 * Initialisation du menu.
	 * 
	 */
	private void f_INIT_MENU(Menu menu)
	{
		// comparaison
		MenuItem itemCompSVN = new MenuItem(menu, SWT.PUSH);
		itemCompSVN.setText("Comparaison SVN");
		itemCompSVN.setImage(C_ToolsSWT.f_GET_IMAGE("documents_new.png"));
		itemCompSVN.addSelectionListener(this);
		
		// separateur
		new MenuItem(menu, SWT.SEPARATOR);
		
		for(ISVNDirEntry dirEntry : C_ToolsSVN.f_GET_REPERTOIRES_REFERENCE())
		{
			// comparaison autre branche
			MenuItem itemGroupee = new MenuItem(menu, SWT.PUSH);
			itemGroupee.setText(dirEntry.getPath());
			itemGroupee.setImage(C_ToolsSWT.f_GET_IMAGE("documents_new.png"));
			itemGroupee.addSelectionListener(this);
			itemGroupee.setData(dirEntry.getPath());
		}
	}
	
	/**
	 * Lancement de l'action.
	 * 
	 */
	public void f_LANCEMENT(String compare)
	{
		try
		{
			// variables
			boolean fileSelected = false;
			
			// récupération de la sélection
			TreeSelection sel = (TreeSelection) C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW().getActivePage().getSelection(JavaUI.ID_PACKAGES);
			
			IWorkbenchWindow window = C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW();
			IJavaProject projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(window);
			C_DescRelease release = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
			RemoteFolder remoteFolder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release);
			
			// extraction des ressources
			IResource[] resources = (IResource[]) C_ToolsWorkbench.getSelectedAdaptables(sel, IResource.class);
			
			// parcours des ressources...
			for (int i = 0; i < resources.length; i++) 
			{
				// si ressourec fichier
				if (resources[i] instanceof IFile) 
				{
					fileSelected = true;
				}
			}
			
			if(!fileSelected)
			{
				// récupération ressources locales
				ISVNLocalResource[] localResources = new ISVNLocalResource[resources.length];
				for (int i = 0; i < resources.length; i++) 
					localResources[i] = SVNWorkspaceRoot.getSVNResourceFor(resources[i]);
				
				SVNLocalCompareSummaryInput compareInput = new SVNLocalCompareSummaryInput(localResources, SVNRevision.HEAD);
				CompareUI.openCompareEditorOnPage(compareInput,null);	
			}
			else
			{
				ISVNRemoteFile remoteFile = null;
				
				// récupération svn ressources
				ISVNResource[] svnRessources =  SVNWorkspaceRoot.getSVNResourcesFor(resources);
				
				IResource ress = resources[0];
				ISVNResource svnRess = svnRessources[0];
				
				ISVNLocalResource loc = SVNWorkspaceRoot.getSVNResourceFor(ress);
				
//				SVNLocalCompareInput compareInput = new SVNLocalCompareInput(loc, SVNRevision.HEAD);
//				CompareUI.openCompareEditorOnPage(compareInput, null);
				
				if(compare != null)
				{
					// récupération du répository release
					SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY() + "/" + compare);
				
					if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.DEBUG))
						C_ToolsSWT.f_NOTIFICATION(E_NotificationType.WARN, "Comparaison SVN", repository.toString() + repository.getUrl().toString());
					
					String repoURL = repository.getUrl().toString();
					
					SVNUrl url = new SVNUrl(repoURL + svnRess.getUrl().toString().replaceAll(remoteFolder.getUrl().toString(), ""));
					
					remoteFile = new RemoteFile(repository, url, SVNRevision.HEAD);
				}
				else
				{
					remoteFile = new RemoteFile(svnRess.getRepository(), svnRess.getUrl(), SVNRevision.HEAD);
				}
				
//				CompareResource r = new CompareResource()
				
//				SVNLocalBranchTagCompareInput in = new SVNLocalBranchTagCompareInput(resources, urls, remoteRevision, targetPart)
				
//				ISVNRemoteResource remote = SVNWorkspaceRoot.getSVNResourceFor(ress).getBaseResource();
				
				SVNLocalCompareInput compareInput = new SVNLocalCompareInput(loc, remoteFile);
				CompareUI.openCompareEditorOnPage(compareInput, null);
			}
		}
		catch(Exception e)
		{
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Comparaison SVN", e.getMessage());
			
			// trace
			e.printStackTrace();
		}
	}
	
//	@Override
//	public void init(IWorkbenchWindow window)
//	{
//		this.m_window = window;
//	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
	   if (action != m_action)
	    {
	      action.setMenuCreator(this);
	      m_action = action;
	    }
		   
	   if(selection instanceof TreeSelection)
	   {
		   // récupération sélection
		   TreeSelection sel = (TreeSelection) selection;
		   
			if(sel.size() == 0 || sel.size() > 1)
				action.setEnabled(false);
			else
				action.setEnabled(true);
	   }
	   else action.setEnabled(false);
	}
		
	@Override
	public void run(IAction action) 
	{
//		if(m_projet == null)
//		{
//			C_ToolsSWT.f_AFFICHE_MESSAGE_ERREUR(C_ToolsWorkbench.f_GET_SHELL(), "Erreur de lancement", "Aucun projet n'est sélectionné");
//			return;
//		}
//		
		f_LANCEMENT(null);
	}	
	
	@Override
	public void dispose() 
	{
		m_window = null;
	}

	@Override
	public void widgetSelected(SelectionEvent selectionEvent)
	{
		MenuItem menuItem = (MenuItem) selectionEvent.getSource();
		
		f_LANCEMENT(menuItem.getData().toString());
	}
	
	// inutilisés
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	@Override
	public void init(IViewPart arg0) 
	{
	}
}
