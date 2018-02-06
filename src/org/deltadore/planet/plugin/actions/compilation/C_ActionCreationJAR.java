package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;

public class C_ActionCreationJAR extends Action
{
	public C_ActionCreationJAR()
	{
		setText("Création JAR");
		setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_jar.png"));
	}
	
	@Override
	public void run() 
	{
		// création JAR
		C_ToolsDistribution.f_CREATION_JAR();
	}
}
