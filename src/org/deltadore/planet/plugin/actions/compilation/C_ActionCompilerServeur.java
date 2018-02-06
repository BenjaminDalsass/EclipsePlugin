package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;

public class C_ActionCompilerServeur extends Action
{
	public C_ActionCompilerServeur()
	{
		setText("Compiler Serveur");
		setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_serveur.png"));
	}
	
	@Override
	public void run() 
	{
		// compilation serveur
		C_ToolsDistribution.f_COMPILATION_SERVEUR();
	}
}
