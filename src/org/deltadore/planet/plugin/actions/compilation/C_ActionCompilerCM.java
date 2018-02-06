package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;

public class C_ActionCompilerCM extends Action
{
	public C_ActionCompilerCM()
	{
		setText("Compiler CM");
		setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_cm.png"));
	}
	
	@Override
	public void run() 
	{
		// compilation CM
		C_ToolsDistribution.f_COMPILATION_CM();
	}
}
