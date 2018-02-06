package org.deltadore.planet.plugin.actions.projet;

import org.deltadore.planet.plugin.jobs.C_JobInitialisationConfig;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionInitialisationConfig extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionInitialisationConfig()
	{
		super("Initialisation de la configuration"); 
		
		// pas de gestion ancienne organisation
		m_is_gestionAnciennOrganisation = false;
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		C_JobInitialisationConfig job = new C_JobInitialisationConfig(projet);
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.addJobChangeListener(listener);
		job.schedule();
	}

	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("prepa_affaire.png");
	}
}
