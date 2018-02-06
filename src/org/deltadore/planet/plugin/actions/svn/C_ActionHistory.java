package org.deltadore.planet.plugin.actions.svn;

import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IConsoleConstants;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;

public class C_ActionHistory implements IViewActionDelegate
{
	/** Workbench window **/
	protected IWorkbenchWindow 				m_window;
	
	@Override
	public void run(IAction arg0) 
	{
		// récupération sélection
		TreeSelection sel = (TreeSelection) m_window.getActivePage().getSelection(JavaUI.ID_PACKAGES);
		
//		ISVNRemoteResource[] svnRemoteResource = C_ToolsWorkbench.getNonOverlapping(sel);
		
		Object[] obj = C_ToolsWorkbench.getSelectedAdaptables(sel, IResource.class);
		
//		IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		if(obj.length > 0)
			C_ToolsSVN.f_SHOW_HISTORY2(obj[0]);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		if(m_window.getActivePage().getSelection(JavaUI.ID_PACKAGES) instanceof TreeSelection)
		{
			// récupération sélection
			TreeSelection sel = (TreeSelection) m_window.getActivePage().getSelection(JavaUI.ID_PACKAGES);
			
			if(sel.size() == 0 || sel.size() > 1)
				action.setEnabled(false);
			else
				action.setEnabled(true);
		}
		else action.setEnabled(false);
	}

	@Override
	public void init(IViewPart viewPart)
	{
		m_window = viewPart.getViewSite().getWorkbenchWindow();
	}
}
