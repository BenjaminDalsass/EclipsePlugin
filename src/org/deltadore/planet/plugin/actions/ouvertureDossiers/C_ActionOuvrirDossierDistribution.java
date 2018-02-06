package org.deltadore.planet.plugin.actions.ouvertureDossiers;

import java.io.File;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionOuvrirDossierDistribution extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionOuvrirDossierDistribution()
	{
		super("Ouverture du dossier Planet");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
		
		File dossier = projet.getProject().getLocation().toFile();
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			dossier = dossier.getParentFile();
		
		C_ToolsRunnable.f_EXECUTE(new File(dossier.getAbsolutePath()));
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_out.png");
	}
}
