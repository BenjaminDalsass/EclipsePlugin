package org.deltadore.planet.model.applicationsPlanet.patch;

import java.util.Date;

public class C_PatchFile 
{
	protected E_PatchActions 			m_svnAction;
	protected String 					m_strPath;
	protected Date						m_date;
	
	public C_PatchFile(E_PatchActions action, String path, Date date)
	{
		super();
		
		// récupération des paramètres
		m_svnAction = action;
		m_strPath = path;
		m_date = date;
	}
}
