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
	
	/** Nom de la t�che **/
	public static final String  NOM = "T�l�chargement des releases";
	
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
		// s�curit�
		if(!C_ToolsSVN.f_IS_SERVEUR_SVN_REACHABLE())
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Serveur indisponible.\nV�rifiez le param�trage du plugin Planet.");
			
			// flag d'�tat du serveur svn
			C_Bases.f_GET_BASE_RELEASES().f_SET_SERVEUR_SVN_STATE(false);
			
			return Status.CANCEL_STATUS;
		}
		if(!C_ToolsSVN.f_IS_REPOSITORY_REFERENCE_EXISTE())
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Repository Reference in�xistant.\nV�rifiez le param�trage du plugin Planet.");
			
			// flag d'�tat du serveur svn
			C_Bases.f_GET_BASE_RELEASES().f_SET_SERVEUR_SVN_STATE(false);
			
			return Status.CANCEL_STATUS;
		}
		
		// d�but t�che
		monitor.beginTask("Mise � jour", 1);
		
		// r�cup�ration des r�f�rences
		C_Bases.f_GET_BASE_RELEASES().m_releases = C_ToolsRelease.f_GET_RELEASES_SERVEUR_SVN();
		
		// flag d'�tat du serveur svn
		C_Bases.f_GET_BASE_RELEASES().f_SET_SERVEUR_SVN_STATE(true);
		
		// mise � jour moniteur
		monitor.done();
		
		if(C_Bases.f_GET_BASE_RELEASES().m_releases == null)
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "La r�cup�ration des releases � �chou�e.");
			
			return Status.CANCEL_STATUS;
		}
		else	
		{
			// notification
			if(NOTIFICATION_ON_SUCCES)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "La r�cup�ration des releases est termin�e");
			
			return Status.OK_STATUS;
		}
	}
}
