package org.deltadore.planet.plugin.jobs;

import java.io.File;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobExecutionAnt extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Exécution script Ant";
	
	/** Projet asscocié **/
	private IJavaProject 		m_projet;
	
	/** Nom du script **/
	private String				m_str_nomScript;
	
	/** Nom du fichier de script **/
	private String				m_str_nomFichierScript;
	
	/**
	 * Constructeur.
	 * 
	 * @param projet projet associé
	 * @param nomRepertoireConfig nom du répertoire de config
	 */
	public C_JobExecutionAnt(IJavaProject projet, String nomScript, String nomfichier) 
	{
		super(NOM + " " + nomScript + ": " + projet.getProject().getName());
		
		// récupération paramètres
		m_projet = projet;
		m_str_nomScript = nomScript;
		m_str_nomFichierScript = nomfichier;
		
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
			// récupération descriptif release
			C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
			
			// execute script ant
			if(descRelease.f_IS_ORGANISATION_INITIALE())
				C_ToolsRunnable.f_LANCEMENT_ANT_2(new File(m_projet.getProject().getLocation().toString() + "/Winjava_src/script/" + m_str_nomFichierScript), monitor);
			else
				C_ToolsRunnable.f_LANCEMENT_ANT_2(new File(m_projet.getProject().getLocation().toString() + "/src/Tools/script/" + m_str_nomFichierScript), monitor);
			
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "Le script " + m_str_nomScript + " à été éxécuté avec succès!");
			
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
