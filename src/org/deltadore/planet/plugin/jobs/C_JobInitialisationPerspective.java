package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobInitialisationPerspective extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Initialisation perspective";
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_JobInitialisationPerspective() 
	{
		super(NOM);
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("replace2.png"));
	}
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		// début tâche
		monitor.beginTask("Initialisation perspective", 1);
		
		IWorkbenchWindow workbenchWindow = C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW();
		workbenchWindow.getSelectionService().addSelectionListener(C_PlanetPluginActivator.f_GET());
		workbenchWindow.addPerspectiveListener(C_PlanetPluginActivator.f_GET());
			
		monitor.done();
		
		return Status.OK_STATUS;
	}
}
