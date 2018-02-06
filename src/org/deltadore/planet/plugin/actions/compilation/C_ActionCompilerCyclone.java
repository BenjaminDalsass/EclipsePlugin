package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;

public class C_ActionCompilerCyclone extends Action
{
	public C_ActionCompilerCyclone()
	{
		setText("Compiler Cyclone");
		setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_cyclone.png"));
	}
	
	@Override
	public void run() 
	{
		// compilation Cyclone
		C_ToolsDistribution.f_COMPILATION_CYCLONE();
	}
}
