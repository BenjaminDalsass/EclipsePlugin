package org.deltadore.planet.plugin.jobs;

import java.io.File;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobSuppressionProjetPlanet extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Suppression projet";
	
	/** Projet **/
	private IJavaProject			m_projet;
	
	/** Descriptif release **/
	private C_DescRelease			m_descRelease;

	/**
	 * Constructeur.
	 * 
	 * @param projet projet à supprimer
	 */
	public C_JobSuppressionProjetPlanet(IJavaProject projet) 
	{
		super(NOM + ": " + projet.getProject().getName());
		
		// récupération paramètre
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
		// options job
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("delete.png"));
		
		// récupération descriptof release
		m_descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
	}
	
	private IStatus f_SUPPRESSION_ANCIENNE_ORGANISATION(IProgressMonitor monitor)
	{
		try 
		{
			// récupération répertoire local
			File repProjet = m_projet.getProject().getLocation().toFile();
			
			// répertoire parent
			File repParent = repProjet.getParentFile();
			
			// suppression du projet eclipse
			monitor.beginTask("Suppression Projet Eclipse", 1);
			m_projet.getProject().delete(false, true, monitor);
			monitor.worked(1);
			
			// suppression du répertoire local
			boolean result = C_ToolsFichiers.f_SUPPRESSION(repParent, monitor, false, false, true);
			
			// mise à jour moniteur
			monitor.done();
			
			// notification
			if(result)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "Le projet " + m_projet.getProject().getName() + " à été supprimé avec succès !");
			else
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Le projet n'a pas été complètement supprimé !");
			
			
			return Status.OK_STATUS; // ok
		} 
		catch (Exception e) 
		{
			// trace
			e.printStackTrace();
			
			return Status.CANCEL_STATUS; // ko
		}
	}
	
	private IStatus f_SUPPRESSION_NOUVELLE_ORGANISATION(IProgressMonitor monitor)
	{
		try 
		{
//			// récupération répertoire local
//			File repProjet = m_projet.getProject().getLocation().toFile();
			
			// suppression du projet eclipse
			monitor.beginTask("Suppression Projet Eclipse", 1);
			m_projet.getProject().delete(true, true, monitor);
			monitor.worked(1);
			
//			// suppression du répertoire local
//			boolean result = C_ToolsFichiers.f_SUPPRESSION(repProjet, monitor, false, false, true);
			
			// mise à jour moniteur
			monitor.done();
			
			// notification
//			if(result)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "Le projet " + m_projet.getProject().getName() + " à été supprimé avec succès !");
//			else
//				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Le projet n'a pas été complètement supprimé !");
			
			return Status.OK_STATUS; // ok
		} 
		catch (Exception e) 
		{
			// trace
			e.printStackTrace();
			
			return Status.CANCEL_STATUS; // ko
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		if(m_descRelease.f_IS_ORGANISATION_INITIALE())
			return f_SUPPRESSION_ANCIENNE_ORGANISATION(monitor);
		else
			return f_SUPPRESSION_NOUVELLE_ORGANISATION(monitor);
	}
}
