package org.deltadore.planet.plugin.jobs;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.model.descriptifs.C_DescRelease.C_InfosSVN;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobPeekRevisionSVNWorkingCopy extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Récupération révision locale (Working Copy)";
	
	/** Projet asscocié **/
	private IProject	 		m_projet;
	
	/** Descriptif release **/
	private C_DescRelease		m_descRelease;
	
	/** Révision locale **/
	private long 				m_lg_revisionLocale;
	
	/** Informations SVN **/
	private C_InfosSVN			m_infoSVN;
	
	/**
	 * Constructeur.
	 * 
	 * @param projet projet associé
	 * @param descRelease descriptif release
	 */
	public C_JobPeekRevisionSVNWorkingCopy(IProject projet, C_DescRelease descRelease) 
	{
		super(NOM + ": " + projet.getName());
		
		// récupération paramètres
		m_projet = projet;
		m_descRelease = descRelease;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("note_view_pp.png"));
	}
	
	public long f_GET_REVISION_LOCALE()
	{
		return m_lg_revisionLocale;
	}
	
	public C_InfosSVN f_GET_INFO_SVN()
	{
		return m_infoSVN;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try
		{
			// récupération descriptif release
			m_descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet);
			
			// récupération reesource locale
			m_lg_revisionLocale = C_ToolsSVN.f_GET_REVISION_DU_PROJET(m_projet);
			
			// récupération info SVN serveur
			m_infoSVN = m_descRelease.f_RETRIEVE_SVN_INFO();
			
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
