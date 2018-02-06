package org.deltadore.planet.model.applicationsPlanet.patch;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNRevision;

public class C_Patch 
{
	/** Projet **/
	private IProject			m_projet;
	
	/** Descriptif release**/
	private C_DescRelease 		m_descRelease;
	
	/** Numéro révision à patcher **/
	private	long				m_lg_revisionToPatch;
	
	private ArrayList<C_PatchElement> 		m_vecPatchElements;
	
	/**
	 * Constructeur.
	 * 
	 * @param descRelease descriptif de release
	 * @param revision révision à patcher
	 */
	public C_Patch(IProject projet, C_DescRelease descRelease, long revisionToPatch)
	{
		super();
		
		// récupération des paramètres
		m_projet = projet;
		m_descRelease = descRelease;
		m_lg_revisionToPatch = revisionToPatch;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		try
		{
			m_vecPatchElements = new ArrayList<C_PatchElement>();
			
			RemoteFolder folder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(m_descRelease);
			
			ISVNLogMessage[] messages = C_ToolsSVN.f_GET_HISTORIQUE(folder, SVNRevision.getRevision(new Long(m_lg_revisionToPatch).toString()), SVNRevision.HEAD, 50, null);
			
			for(ISVNLogMessage m : messages)
			{
				m_vecPatchElements.add(new C_PatchElement(m_descRelease, m));
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	public Hashtable<String, C_PatchFile> f_CREATION_FILE_SYSTEM_SYNTHESE()
	{
		// résultat
		Hashtable<String, C_PatchFile> synthese = new Hashtable<String, C_PatchFile>();
		
		String path = m_projet.getLocation().toFile().getAbsolutePath();
		
		for(C_PatchElement element : m_vecPatchElements)
		{
			ArrayList<C_PatchFile> patchFiles = element.f_GET_PATCH_FILES();
			
			for(C_PatchFile patchFile : patchFiles)
			{
				C_PatchFile existingPath = synthese.get(patchFile.m_strPath);
				
				if(existingPath != null)
				{
					if(patchFile.m_date.after(existingPath.m_date))
					{
						synthese.put(patchFile.m_strPath.replace(path, ""), patchFile);
					}
				}
				else
				{
					synthese.put(patchFile.m_strPath.replace(path, ""), patchFile);
				}
			}
		}
		
		new C_PatchTree(synthese);
		
		return synthese;
	}
}
