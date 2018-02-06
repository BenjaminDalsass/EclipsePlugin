package org.deltadore.planet.plugin.actions.projet;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.ui.dialogs.C_FenetreExportationDistribution;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;

public class C_ActionExportationDistribution extends C_ActionProjetPlanetAbstraite
{
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionExportationDistribution()
	{
		super("Exportation de la distribution");
	}
	
	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener) 
	{
		// fenêtre d'exportation
		C_FenetreExportationDistribution frm = new C_FenetreExportationDistribution(m_window.getShell(), projet.getProject());
		frm.setVisible(true);
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("applications.png");
	}
}
