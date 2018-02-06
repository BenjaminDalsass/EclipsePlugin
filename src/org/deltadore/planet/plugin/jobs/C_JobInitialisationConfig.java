package org.deltadore.planet.plugin.jobs;

import java.io.File;

import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobInitialisationConfig extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Initialisation Config";
	
	/** Projet asscocié **/
	private IJavaProject 		m_projet;
	
	/**
	 * Constructeur.
	 * 
	 * @param projet projet associé
	 * @param nomRepertoireConfig nom du répertoire de config
	 */
	public C_JobInitialisationConfig(IJavaProject projet) 
	{
		super(NOM + ": " + projet.getProject().getName());
		
		// récupération paramètres
		m_projet = projet;
		
		// initialisation
		f_INIT();
	}
	
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("ant.png"));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try
		{
			// execute script ant
			C_ToolsRunnable.f_LANCEMENT_ANT_2(new File(m_projet.getProject().getLocation().toString() + "/src/Tools/script/" + "initialisation_config.xml"), monitor);
			
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "La configuration du projet " + m_projet.getProject().getName() + " à été créée avec succès!");
			
			monitor.done();
			
			return Status.OK_STATUS;
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
			
			return Status.CANCEL_STATUS;
		}
	}
}
