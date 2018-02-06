package org.deltadore.planet.plugin.actions.svn;

import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionSynchronizeProject extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionSynchronizeProject()
	{
		super("Synchronisation SVN du projet"); 
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener) 
	{
		C_ToolsSVN.f_SYNCHRONIZE(projet.getProject());
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("environment_synch.png");
	}
}
