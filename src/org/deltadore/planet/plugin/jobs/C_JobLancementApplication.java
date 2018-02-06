package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.model.applicationsPlanet.E_Applications;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobLancementApplication extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Lancement application";
	
	/** Projet **/
	private IJavaProject			m_projet;
	
	/** Application à lancer **/
	private E_Applications			m_application;
	
	/** Arguments **/
	private String					m_str_arguments;
	
	/**
	 * Constructeur.
	 * 
	 * @param projet projet
	 * @param application application
	 * @param arguments arguments
	 */
	public C_JobLancementApplication(IJavaProject projet, E_Applications application, String arguments) 
	{
		super(NOM + " " + application.toString() + ": " + projet.getProject().getName());
		
		// récupération paramètre
		m_projet = projet;
		m_application = application;
		m_str_arguments = arguments;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("gear.png"));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try
		{
			// lancement
			C_ToolsRunnable.f_LANCEMENT_APPLICATION(m_projet, m_application, m_str_arguments);
			
			monitor.done();
			
			return Status.OK_STATUS;
		}
		catch(Exception e)
		{
			return Status.CANCEL_STATUS;
		}
	}
}
