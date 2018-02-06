package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.tools.C_ToolsRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobProcessExecution extends Job
{
	/** Processus à éxécuter **/
	private String[]		m_process;
	
	/**
	 * Constructeur.
	 * 
	 * @param nom nom du job
	 * @param process arguments processus et arguments du processus
	 */
	public C_JobProcessExecution(String nom, ImageDescriptor image, String... process)
	{
		super(nom);
		
		// récupération des paramètres
		m_process = process;
		
		// initialisation
		f_INIT(image);
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT(ImageDescriptor image)
	{
		setProperty(IProgressConstants.ICON_PROPERTY, image);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try 
		{
			// création process
			Process process = C_ToolsRunnable.f_EXECUTE3(m_process);
			process.waitFor();
			
			monitor.done();
		}
		catch (InterruptedException e)
		{
			// trace
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	}
}
