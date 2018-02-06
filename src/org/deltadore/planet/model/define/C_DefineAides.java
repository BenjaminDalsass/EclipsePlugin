package org.deltadore.planet.model.define;

import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class C_DefineAides
{
	public static final String 	TELECHARGEMENT_RELEASE = "TELECHARGEMENT_RELEASE";
	
	/**
	 * Retourne la propri�t� voulue.
	 * 
	 * @param id identifiant de la propri�t�
	 * @return valeur de la propri�t�
	 */
	public static void f_ATTACH_AIDE(Composite composant, String id)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composant, C_PlanetPluginActivator.PLUGIN_ID + "." + id);
	}
}
