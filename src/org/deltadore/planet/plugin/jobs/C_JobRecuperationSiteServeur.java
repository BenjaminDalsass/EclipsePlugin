package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobRecuperationSiteServeur extends Job
{
	public static final boolean				NOTIFICATION_ON_SUCCES = false;
	
	/** Nom de la t�che **/
	public static final String  NOM = "T�l�chargement des sites du serveur";
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_JobRecuperationSiteServeur() 
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
		// d�but t�che
		monitor.beginTask("Mise � jour", 1);
		
		// scan
		boolean lectureOk = C_Bases.f_GET_BASE_SITES().f_SCAN_SITES();
		
		// si ko
		if(!lectureOk)
		{
			// notification
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "La r�cup�ration des sites � �chou�e.");
			
			// mise � jour moniteur
			monitor.done();
			
			return Status.CANCEL_STATUS;
		}
		else	
		{
			// notification
			if(NOTIFICATION_ON_SUCCES)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, NOM, "La r�cup�ration des sites est termin�e");
			
			// mise � jour moniteur
			monitor.done();
			
			return Status.OK_STATUS;
		}
	}
}
