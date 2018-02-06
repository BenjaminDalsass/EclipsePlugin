package org.deltadore.planet.plugin.actions.svn;

import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionMergeProject extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionMergeProject()
	{
		super("Merge SVN du projet"); 
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener) 
	{
		C_ToolsSVN.f_MERGE(projet.getProject());
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("environment_merge.png");
	}
}
