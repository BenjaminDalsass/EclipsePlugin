package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobRecuperationReleasesServeur extends Job
{
	public static final boolean				NOTIFICATION_ON_SUCCES = false;
	
	/** Nom de la tâche **/
	public static final String  NOM = "Téléchargement des releases";
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_JobRecuperationReleasesServeur() 
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
		// sécurité
		if(!C_ToolsSVN.f_IS_SERVEUR_SVN_REACHABLE())
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Serveur indisponible.\nVérifiez le paramétrage du plugin Planet.");
			
			// flag d'état du serveur svn
			C_Bases.f_GET_BASE_RELEASES().f_SET_SERVEUR_SVN_STATE(false);
			
			return Status.CANCEL_STATUS;
		}
		if(!C_ToolsSVN.f_IS_REPOSITORY_REFERENCE_EXISTE())
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Repository Reference inéxistant.\nVérifiez le paramétrage du plugin Planet.");
			
			// flag d'état du serveur svn
			C_Bases.f_GET_BASE_RELEASES().f_SET_SERVEUR_SVN_STATE(false);
			
			return Status.CANCEL_STATUS;
		}
		
		// début tâche
		monitor.beginTask("Mise à jour", 1);
		
		// récupération des références
		C_Bases.f_GET_BASE_RELEASES().m_releases = C_ToolsRelease.f_GET_RELEASES_SERVEUR_SVN();
		
		// flag d'état du serveur svn
		C_Bases.f_GET_BASE_RELEASES().f_SET_SERVEUR_SVN_STATE(true);
		
		// mise à jour moniteur
		monitor.done();
		
		if(C_Bases.f_GET_BASE_RELEASES().m_releases == null)
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "La récupération des releases à échouée.");
			
			return Status.CANCEL_STATUS;
		}
		else	
		{
			// notification
			if(NOTIFICATION_ON_SUCCES)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "La récupération des releases est terminée");
			
			return Status.OK_STATUS;
		}
	}
}
