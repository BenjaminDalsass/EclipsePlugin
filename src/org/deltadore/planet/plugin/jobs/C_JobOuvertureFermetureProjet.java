package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobOuvertureFermetureProjet extends Job
{
	/** Projet **/
	private IProject			m_projet;

	/** Flag pour ouverture **/
	private boolean				m_is_ouverture;
	
	private static String f_GET_ACTION(boolean ouevrture)
	{
		if(ouevrture)
			return "Ouverture projet";
		else
			return "Fermeture projet";
	}
	
	public C_JobOuvertureFermetureProjet(IProject projet, boolean ouverture) 
	{
		super(f_GET_ACTION(ouverture) + ": " + projet.getName());
		
		// récupération paramètre
		m_projet = projet;
		m_is_ouverture = ouverture;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		if(m_is_ouverture)
			setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_redo_green.png"));
		else
			setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_redo_red.png"));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try
		{
			if(m_is_ouverture)
				// ouverture projet
				m_projet.open(monitor);
			else
				// fermeture projet
				m_projet.close(monitor);
			
			monitor.done();
			
			return Status.OK_STATUS;
		}
		catch(Exception e)
		{
			return Status.CANCEL_STATUS;
		}
	}
}
