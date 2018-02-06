package org.deltadore.planet.plugin.actions.projet;

import java.io.File;

import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.jobs.C_JobCopieRepertoire;
import org.deltadore.planet.swt.C_PopupNotification;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionRecuperationDictionnaireServeur extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionRecuperationDictionnaireServeur()
	{
		super("Récupération dictionnaire serveur");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener) 
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
		
		// sécurité
		if(descRelease != null)
		{
			String path = "\\\\Srv-meylan\\REFERENCE\\REFERENCE MULTILINGUE\\_Dictionnaire\\_Global\\" + descRelease.f_GET_NOM();
			
			if(descRelease.f_IS_ORGANISATION_2_6())
			{
				path = path.concat("\\trunk");
			}
			File fileSrc = new File(path);
			
			C_PopupNotification.notify("Récup. dico", path, E_NotificationType.INFO);
			
			if(!fileSrc.exists())
			{
				C_ToolsSWT.f_AFFICHE_MESSAGE_ERREUR(m_window.getShell(), "Récupération dictionnaire serveur", "Le dictionnaire serveur est introuvable.");
				return;
			}
			
			// message de confirmation
			if(C_ToolsSWT.f_AFFICHE_QUESTION(m_window.getShell(), "Récupération dictionnaire serveur", "Etes-vous sûr de vouloir écraser le dictionnaire global local:\n\n " + projet.getProject().getName(), "get_dico.png"))
			{
				File fileDst = null;
				
				if(descRelease.f_IS_ORGANISATION_INITIALE())
				{
					fileDst = new File(projet.getProject().getLocation().toFile().getParent() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + File.separator + "Dictionnaire");
					C_JobCopieRepertoire job = new C_JobCopieRepertoire(fileSrc, fileDst);
					job.schedule();
				}
				else
				{
					fileDst = new File(projet.getProject().getLocation().toFile().toString() + File.separator + C_DefineInfosManagerPlanet.NOM_REPERTOIRE_RESSOURCES + File.separator + "Global");
					C_JobCopieRepertoire job = new C_JobCopieRepertoire(fileSrc, fileDst, "Dictionnaire");
					job.schedule();
				}
			}
		}
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("get_dico.png");
	}
}
