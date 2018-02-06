package org.deltadore.planet.plugin.actions.projet;

import java.io.File;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionMergeDistribution extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionMergeDistribution()
	{
		super("Merge du projet"); 
		
		// pas de gestion ancienne organisation
		m_is_gestionAnciennOrganisation = false;
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener) 
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
		
		// dossiers
		File fileProjet = projet.getProject().getLocation().toFile();
		File fileDistrib = fileProjet.getParentFile();
		
		// récupération chemin serveur release
		String cheminSrvRelease = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_MEYLAN_DOSSIER_RELEASE);
		cheminSrvRelease = cheminSrvRelease + File.separator + descRelease.f_GET_NOM();
		
		// lancement winmerge
		String cheminEcransCM = "_Wincm" + File.separator + "Ecrans";
		C_ToolsRunnable.f_EXECUTE2("winmergeU /r /e \"" + fileDistrib.getAbsolutePath() + File.separator + cheminEcransCM + "\" \"" + cheminSrvRelease + File.separator + cheminEcransCM + "\"", null);
		
		String cheminJava = "_Winman_java";
		C_ToolsRunnable.f_EXECUTE2("winmergeU /r /e \"" + fileDistrib.getAbsolutePath() + File.separator + cheminJava + "\" \"" + cheminSrvRelease + File.separator + cheminJava + "\"", null);
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("document_exchange.png");
	}
}
