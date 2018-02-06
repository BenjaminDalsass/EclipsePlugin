package org.deltadore.planet.plugin.actions.ouvertureDossiers;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class C_ActionOuvrirMantis implements IWorkbenchWindowActionDelegate 
{
	public void init(IWorkbenchWindow window)
	{
	}
	
	@Override
	public void run(IAction action) 
	{
		C_ToolsWorkbench.f_OPEN_NAVIGATEUR("Mantis",  C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.URL_MANTIS));
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		action.setEnabled(true);
	}

	@Override
	public void dispose() 
	{
	}
}
