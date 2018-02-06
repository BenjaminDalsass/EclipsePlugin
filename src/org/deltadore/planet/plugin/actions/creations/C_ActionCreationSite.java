package org.deltadore.planet.plugin.actions.creations;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.ui.wizards.C_DialogWizard;
import org.deltadore.planet.ui.wizards.C_WizardCreationSite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class C_ActionCreationSite implements IWorkbenchWindowActionDelegate 
{
	private IWorkbenchWindow window;
	
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	@Override
	public void run(IAction action) 
	{    
		// wizard création site
	    C_WizardCreationSite wizard = new C_WizardCreationSite();
	    C_DialogWizard dialog = new C_DialogWizard(window.getShell(), wizard);
	    dialog.open();
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		if(C_Bases.f_GET_BASE_RELEASES().f_GET_SERVEUR_SVN_STATE()
		&& C_Bases.f_GET_BASE_RELEASES().m_releases != null)
			action.setEnabled(true);
		else
			action.setEnabled(false);
	}

	@Override
	public void dispose() 
	{
		window = null;
	}
}
