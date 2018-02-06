package org.deltadore.planet.plugin.jobs;

import java.io.File;

import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobCopieRepertoire extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Copie répertoire";
	
	/** Répertoire source **/
	private File			m_repertoireSrc;

	/** Répertoire destinataire **/
	private File			m_repertoireDest;
	
	public C_JobCopieRepertoire(File repertoireSource,  File repertoireDest) 
	{
		super(NOM + ": " + repertoireSource.getName());
		
		// récupération paramètre
		m_repertoireSrc = repertoireSource;
		m_repertoireDest = new File(repertoireDest.getAbsolutePath()+ File.separator + repertoireSource.getName());
		
		// initialisation
		f_INIT();
	}
	
	public C_JobCopieRepertoire(File repertoireSource,  File repertoireDest, String nouveauNomRepertoire) 
	{
		super(NOM + ": " + repertoireSource.getName());
		
		// récupération paramètre
		m_repertoireSrc = repertoireSource;
		if(nouveauNomRepertoire == null)
			nouveauNomRepertoire = repertoireSource.getName();
		m_repertoireDest = new File(repertoireDest.getAbsolutePath() + File.separator + nouveauNomRepertoire);
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_up.png"));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try
		{
			// moniteur
			C_CopieMonitor copieMonitor = new C_CopieMonitor(monitor, m_repertoireSrc);
			
			// copie
			boolean result = C_ToolsFichiers.f_COPIE_REPERTOIRE(m_repertoireSrc, m_repertoireDest, copieMonitor, false);
		
			// notification
			if(result)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "Le répertoire " + m_repertoireSrc.getName() + " à été copié avec succès !");
			else
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, NOM, "Le répertoire " + m_repertoireSrc.getName() + " n'a pas été complètement copié !");
			
			monitor.done();
			
			return Status.OK_STATUS;
		}
		catch(Exception e)
		{
			return Status.CANCEL_STATUS;
		}
	}
}
