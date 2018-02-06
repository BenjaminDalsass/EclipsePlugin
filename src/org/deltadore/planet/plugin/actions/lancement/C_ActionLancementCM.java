package org.deltadore.planet.plugin.actions.lancement;

import java.io.File;

import org.deltadore.planet.model.applicationsPlanet.E_Applications;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.plugin.jobs.C_JobLancementApplication;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionLancementCM extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionLancementCM()
	{
		super("Lancement de CM");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		// r�cup�ration config actuelle
		String nomConfig = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(projet.getProject().getName() + C_DefinePreferencesPlugin.CONFIG_EN_COURS);
		
    	// r�pertoire projet
    	File repertoireProjet = new File(projet.getProject().getLocation().toFile(), "_Wincm" + File.separator + "Wincm.exe");
    	
		// lancement CM
    	if(repertoireProjet.exists())
    		new C_JobLancementApplication(projet, E_Applications.CM, nomConfig).schedule();
    	else
    		new C_JobLancementApplication(projet, E_Applications.CM_JAVA, "REPERTOIRE_CONFIG=" + nomConfig).schedule();
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("cubes_yellow.png");
	}
}
