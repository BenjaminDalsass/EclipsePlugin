package org.deltadore.planet.plugin.actions.creations;

import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class C_ActionCreationProjet implements IWorkbenchWindowActionDelegate 
{
	protected IWorkbenchWindow window;
	
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	@Override
	public void run(IAction action) 
	{
//		try
//		{
//			// récupération workspace
//			IWorkspace worspace = ResourcesPlugin.getWorkspace();
//			
//			// création descriptif projet
//			IProjectDescription description = worspace.newProjectDescription("Test");
//			description.setLocation(new Path("c:/DD"));
//			
//			// création du projet
//			IProgressMonitor progressMonitor = new NullProgressMonitor();
//			IProject project = worspace.getRoot().getProject("Test");
//			project.create(description, progressMonitor);
//			project.open(progressMonitor);
//		}
//		catch(Exception e)
//		{
//			// trace
//			e.printStackTrace();
//		}
		C_ToolsSWT.f_NOTIFICATION(E_NotificationType.WARN, "Titre", "Message");
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		action.setEnabled(true);
	}

	@Override
	public void dispose() 
	{
		window = null;
	}
}
