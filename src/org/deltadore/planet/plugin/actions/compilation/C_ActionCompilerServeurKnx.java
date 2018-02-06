package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;

public class C_ActionCompilerServeurKnx extends Action
{
	public C_ActionCompilerServeurKnx()
	{
		setText("Compiler serveur KNX");
		setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_knx.png"));
	}
	
	@Override
	public void run() 
	{
		// compilation KNX
		C_ToolsDistribution.f_COMPILATION_KNX();
	}
}
