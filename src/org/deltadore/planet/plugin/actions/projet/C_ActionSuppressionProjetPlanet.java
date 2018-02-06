package org.deltadore.planet.plugin.actions.projet;

import org.deltadore.planet.plugin.jobs.C_JobSuppressionProjetPlanet;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionSuppressionProjetPlanet extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionSuppressionProjetPlanet()
	{
		super("Suppression du projet Planet");
		
		// pas de besoin projet ouvert
		m_is_needOpenedProject = false;
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		// message de confirmation
		if(C_ToolsSWT.f_AFFICHE_QUESTION(m_window.getShell(), "Suppression d'un projet Planet", "Etes-vous sûr de vouloir supprimer le projet:\n\n " + projet.getProject().getName()))
		{
			// tâche de suppression
			C_JobSuppressionProjetPlanet jobSuppression = new C_JobSuppressionProjetPlanet(projet);
			jobSuppression.schedule();
		}
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_delete.png");
	}
}
