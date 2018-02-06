package org.deltadore.planet.model.applicationsPlanet.patch;

import java.util.ArrayList;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;

public class C_PatchElement 
{
	private C_DescRelease				m_descRelease;
	private ISVNLogMessage				m_svnLogMessage;
	
	private ArrayList<C_PatchFile> 		m_patchFiles;
	
	/**
	 * Constructeur.
	 * 
	 * @param descRelease descriptif release
	 * @param logMessage message log svn
	 */
	public C_PatchElement(C_DescRelease descRelease, ISVNLogMessage logMessage)
	{
		super();
		
		// récupération paramètres
		m_descRelease = descRelease;
		m_svnLogMessage = logMessage;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		m_patchFiles = new ArrayList<C_PatchFile>();
		
		ISVNLogMessageChangePath[] changePaths = m_svnLogMessage.getChangedPaths();
		
		for(ISVNLogMessageChangePath changePath : changePaths)
		{
			switch(changePath.getAction())
			{
				case 'A':
					m_patchFiles.add(new C_PatchFile(E_PatchActions.ADD, changePath.getPath(), m_svnLogMessage.getDate()));
					break;
				case 'U':
					m_patchFiles.add(new C_PatchFile(E_PatchActions.UPDATED, changePath.getPath(), m_svnLogMessage.getDate()));
					break;
				case 'D':
					m_patchFiles.add(new C_PatchFile(E_PatchActions.DELETED, changePath.getPath(), m_svnLogMessage.getDate()));
					break;
			}
		}
	}
	
	public ArrayList<C_PatchFile> f_GET_PATCH_FILES()
	{
		return m_patchFiles;
	}
}
