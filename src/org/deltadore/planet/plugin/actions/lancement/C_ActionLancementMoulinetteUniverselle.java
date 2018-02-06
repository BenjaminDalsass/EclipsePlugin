package org.deltadore.planet.plugin.actions.lancement;

import org.deltadore.planet.model.applicationsPlanet.E_Applications;
import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.plugin.jobs.C_JobLancementApplication;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.ui.vues.projet.C_VueProjet;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionLancementMoulinetteUniverselle extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionLancementMoulinetteUniverselle()
	{
		super("Lancement moulinette universelle");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		// variables
		String nomConfig = null;
		
		// récupération vue projet
		C_VueProjet vueProjet = C_VueProjet.f_FIND_VUE();
		
		// si vue projet ok, récupération config actuelle
		if(vueProjet != null)
			nomConfig = "REPERTOIRE_CONFIG=" + vueProjet.f_GET_CONFIGURATION_SELECTIONNEE();
		
		new C_JobLancementApplication(projet, E_Applications.MOULINETTE, nomConfig).schedule();
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("historique_pp.png");
	}
}
