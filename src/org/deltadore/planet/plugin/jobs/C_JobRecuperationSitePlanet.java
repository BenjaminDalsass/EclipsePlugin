package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobRecuperationSitePlanet extends Job
{
	public static final boolean				NOTIFICATION_ON_SUCCES = false;
	
	/** Nom de la tâche **/
	public static final String  NOM = "Téléchargement des affaires planet";
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_JobRecuperationSitePlanet() 
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
		monitor.beginTask("Mise à jour", 1);
		
		// lecture base des sites
		boolean lectureOk = C_Bases.f_GET_BASE_AFFAIRES_PLANET().f_LECTURE_BASE_SITES_XML();
		
		// si ko
		if(!lectureOk)
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "La récupération des affaires planet à échouée.");
			
			// mise à jour moniteur
			monitor.done();
			
			return Status.CANCEL_STATUS;
		}
		else	
		{
			// notification
			if(NOTIFICATION_ON_SUCCES)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "La récupération des affaires planet est terminée");
			
			// mise à jour moniteur
			monitor.done();
			
			return Status.OK_STATUS;
		}
	}
}
