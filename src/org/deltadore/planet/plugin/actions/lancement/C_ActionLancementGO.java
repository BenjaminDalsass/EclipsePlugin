package org.deltadore.planet.plugin.actions.lancement;

import org.deltadore.planet.model.applicationsPlanet.E_Applications;
import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.plugin.jobs.C_JobLancementApplication;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionLancementGO extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionLancementGO()
	{
		super("Lancement de GO");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		new C_JobLancementApplication(projet, E_Applications.GO, "").schedule();
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("earth_new.png");
	}
}
