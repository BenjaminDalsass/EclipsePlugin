package org.deltadore.planet.plugin.actions.lancement;

import org.deltadore.planet.model.applicationsPlanet.E_Applications;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.plugin.jobs.C_JobLancementApplication;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionLancementClientServeur extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionLancementClientServeur()
	{
		super("Lancement du Client et du Serveur");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		// récupération config actuelle
		String nomConfig = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(projet.getProject().getName() + C_DefinePreferencesPlugin.CONFIG_EN_COURS);
		
		new C_JobLancementApplication(projet, E_Applications.SERVEUR, nomConfig).schedule();
		
		new C_JobLancementApplication(projet, E_Applications.CLIENT, "REPERTOIRE_CONFIG=" + nomConfig).schedule();
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("client_server.png");
	}
}
