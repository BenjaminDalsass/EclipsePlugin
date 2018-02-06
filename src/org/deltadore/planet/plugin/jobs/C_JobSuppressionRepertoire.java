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

public class C_JobSuppressionRepertoire extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Suppression répertoire";
	
	/** répertoire **/
	private File					m_repertoire;
	
	/** Flag pour garder le dossier contenant **/
	private boolean 				m_is_isFolderKeepParent;
	
	/** Filtre de suppression **/
	private String[]				m_filtres;
	
	/**
	 * Constructeur.
	 * 
	 * @param répertoire répertoire à supprimer
	 * @param ifFolderKeepParent suppression dossier contenant
	 * @param filtres filtre de suppression
	 */
	public C_JobSuppressionRepertoire(File répertoire, boolean ifFolderKeepParent, String... filtres) 
	{
		super(NOM + ": " + répertoire.getName());
		
		// récupération paramètre
		m_repertoire = répertoire;
		m_is_isFolderKeepParent = ifFolderKeepParent;
		m_filtres = filtres;
		
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
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		// suppression du répertoire local
		boolean result = C_ToolsFichiers.f_SUPPRESSION(m_repertoire, monitor, m_is_isFolderKeepParent, false, true, m_filtres);
		
		// mise à jour moniteur
		monitor.done();
		
		// notification
		if(result)
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "Le répertoire " + m_repertoire.getName() + " à été supprimé avec succès !");
		else
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "Le répertoire n'a pas été complètement supprimé !");
		
		
		return Status.OK_STATUS; // ok
	}
}
