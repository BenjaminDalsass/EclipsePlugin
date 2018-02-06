package org.deltadore.planet.model.applicationsPlanet.patch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class C_PatchTree 
{
	Hashtable<String, C_PatchFile>		m_synthese;
	
	ArrayList<C_PatchFile> 				m_patchFiles;
	
	Hashtable<String, C_PatchTree> 		m_childTree;
	
	public C_PatchTree(Hashtable<String, C_PatchFile> synthese)
	{
		super();
		
		m_synthese = synthese;
		
		f_INIT();
	}
	
	private C_PatchTree()
	{
		super();
		
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		m_patchFiles = new ArrayList<C_PatchFile>();
		m_childTree = new Hashtable<String, C_PatchTree>();
		
		f_SCAN_SYNTHESE();
	}
	
	private void f_SCAN_SYNTHESE()
	{
		System.err.println("f_SCAN_SYNTHESE " + m_synthese);
		for(C_PatchFile file : Collections.list(m_synthese.elements()))
		{
			System.out.println(file.m_strPath);
			String[] tags = file.m_strPath.split("/");
			C_PatchTree lastTree = this;
			
			for(int i = 0 ; i < tags.length ; i++)
			{
				if(i == tags.length-1)
				{
					lastTree.m_patchFiles.add(file);
				}
				else
				{
					if(m_childTree.contains(tags[i]))
					{
						lastTree = lastTree.m_childTree.get(tags[i]);
					}
					else
					{
						C_PatchTree tree = new C_PatchTree();
						m_childTree.put(tags[i], tree);
						lastTree = tree;
					}
				}
			}
		}
	}
}
