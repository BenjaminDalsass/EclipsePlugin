package org.deltadore.planet.plugin.actions.svn;

import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.subclipse.ui.compare.SVNLocalCompareInput;
import org.tigris.subversion.subclipse.ui.compare.SVNLocalCompareSummaryInput;
import org.tigris.subversion.svnclientadapter.SVNRevision;

public class C_ActionCompare implements IViewActionDelegate
{
	/** Workbench window **/
	protected IWorkbenchWindow 				m_window;
	
	@Override
	public void run(IAction action) 
	{
		try
		{
			// variables
			boolean fileSelected = false;
			
			// récupération de la sélection
			TreeSelection sel = (TreeSelection) m_window.getActivePage().getSelection(JavaUI.ID_PACKAGES);
			
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
				IResource ress = resources[0];
				ISVNLocalResource loc = SVNWorkspaceRoot.getSVNResourceFor(ress);
				
				SVNLocalCompareInput compareInput = new SVNLocalCompareInput(loc, SVNRevision.HEAD);
				CompareUI.openCompareEditorOnPage(compareInput, null);
			}
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		// récupération sélection
		TreeSelection sel = (TreeSelection) m_window.getActivePage().getSelection(JavaUI.ID_PACKAGES);
		
		if(sel.size() == 0 || sel.size() > 1)
			action.setEnabled(false);
		else
			action.setEnabled(true);
	}

	@Override
	public void init(IViewPart viewPart)
	{
		m_window = viewPart.getViewSite().getWorkbenchWindow();
	}
}
