package org.deltadore.planet.plugin.actions.projet;

import java.io.File;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.jobs.C_JobSuppressionRepertoire;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionSuppressionHistoriques extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionSuppressionHistoriques()
	{
		super("Suppression des historiques");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		// message de confirmation
		if(C_ToolsSWT.f_AFFICHE_QUESTION(m_window.getShell(), "Suppression des historiques serveur", "Etes-vous sûr de vouloir supprimer les historiques du projet:\n\n " + projet.getProject().getName()))
		{
			File fileRepAffaire = null;
			C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
			
			if(descRelease.f_IS_ORGANISATION_INITIALE())
				fileRepAffaire = projet.getProject().getLocation().toFile().getParentFile();
			else
				fileRepAffaire = projet.getProject().getLocation().toFile();
			
			C_JobSuppressionRepertoire jobSuppressionOH = new C_JobSuppressionRepertoire(new File(fileRepAffaire, "\\_Winman_serveur\\Archiv\\Arch"), true, ".svn");
			jobSuppressionOH.schedule();
			C_JobSuppressionRepertoire jobSuppressionOI = new C_JobSuppressionRepertoire(new File(fileRepAffaire, "\\_Winman_serveur\\Archiv\\IndArch"), true, ".svn");
			jobSuppressionOI.schedule();
			C_JobSuppressionRepertoire jobSuppressionCH = new C_JobSuppressionRepertoire(new File(fileRepAffaire, "\\_Winman_serveur\\Check_up\\Arch"), true, ".svn");
			jobSuppressionCH.schedule();
			C_JobSuppressionRepertoire jobSuppressionCI = new C_JobSuppressionRepertoire(new File(fileRepAffaire, "\\_Winman_serveur\\Check_up\\IndArch"), true, ".svn");
			jobSuppressionCI.schedule();
		}
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_delete.png");
	}
}
