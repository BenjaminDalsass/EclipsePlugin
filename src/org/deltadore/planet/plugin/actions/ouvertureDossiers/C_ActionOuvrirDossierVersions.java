package org.deltadore.planet.plugin.actions.ouvertureDossiers;

import java.io.File;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class C_ActionOuvrirDossierVersions implements IWorkbenchWindowActionDelegate 
{
	public void init(IWorkbenchWindow window)
	{
	}
	
	@Override
	public void run(IAction action) 
	{
		String cheminDocuments = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_VERSIONS);
		C_ToolsRunnable.f_EXECUTE(new File(cheminDocuments));
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
