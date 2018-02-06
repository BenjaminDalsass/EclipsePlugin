package org.deltadore.planet.plugin.actions.projet;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.AbstractAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class C_ActionProjetPlanetAbstraite implements IWorkbenchWindowActionDelegate
{
	/** Description **/
	private String							m_str_description;
	
	/** Workbench window **/
	protected IWorkbenchWindow 				m_window;
	
	/** flag pour gestion ancienne organisation **/
	protected boolean 						m_is_gestionAnciennOrganisation;
	
	/** flag pour gestion organisation jusqu'a 2.5 **/
	protected boolean 						m_is_gestionOrgnanisationAvant_2_5;
	
	/** flag pour gestion organisation jusqu'a 3.0 **/
	protected boolean 						m_is_gestionOrgnanisationAvant_3_0;
	
	/** flag pour gestion nouvelle organisation **/
	protected boolean 						m_is_gestionNouvelleOrganisation;
	
	/** flag si besoin projet ouvert **/
	protected boolean 						m_is_needOpenedProject;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionProjetPlanetAbstraite(String description)
	{
		super();
		
		// récupération paramètres
		m_str_description = description;
		
		// init
		m_is_gestionAnciennOrganisation = true;
		m_is_gestionOrgnanisationAvant_2_5 = true;
		m_is_gestionOrgnanisationAvant_3_0 = true;
		m_is_gestionNouvelleOrganisation = true;
		m_is_needOpenedProject = true;
	}
	
	@Override
	public void init(IWorkbenchWindow window)
	{
		this.m_window = window;
	}

	@Override
	public void run(IAction action) 
	{
		if(!C_ToolsWorkbench.f_IS_PROJET_PLANET_SELECTIONNE(m_window))
		{
			C_ToolsSWT.f_AFFICHE_MESSAGE_ERREUR(m_window.getShell(), "Erreur de lancement", "Aucun projet n'est sélectionné");
			return;
		}
		
		IJavaProject projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(m_window);
		f_LANCEMENT(projet, null);
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		// récupération projet planet
		IJavaProject projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(m_window);
		
		// sécurité
		if(projet == null)
		{
			action.setEnabled(false);
		}
		else if(!projet.getProject().isOpen() && m_is_needOpenedProject)
		{
			action.setEnabled(false);
		}
		else
		{
			// récupération descriptif release
			C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
			
			if(descRelease == null)
				action.setEnabled(false);
			else if(descRelease.f_IS_ORGANISATION_INITIALE() && m_is_gestionAnciennOrganisation)
				action.setEnabled(true);
			else if(descRelease.f_IS_ORGANISATION_AVANT_2_5() && m_is_gestionOrgnanisationAvant_2_5)
				action.setEnabled(true);
			else if(descRelease.f_IS_ORGANISATION_AVANT_3_0() && m_is_gestionOrgnanisationAvant_3_0)
				action.setEnabled(true);
			else if(m_is_gestionNouvelleOrganisation)
				action.setEnabled(true);
			else
				action.setEnabled(false);
		}
	}

	@Override
	public void dispose() 
	{
		m_window = null;
	}
	
	public IAction f_GET_ACTION()
	{
		AbstractAction action = new Action() 
		{
			@Override
			public void run() 
			{
				m_window = C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW();
				IJavaProject projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(m_window);
				f_LANCEMENT(projet, null);
			}
		};
		
		action.setImageDescriptor(f_GET_IMAGE_DESCRIPTOR());
		action.setDescription(m_str_description);
		
		return action;
	}
	
	public abstract void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener);
	public abstract ImageDescriptor f_GET_IMAGE_DESCRIPTOR();
}
