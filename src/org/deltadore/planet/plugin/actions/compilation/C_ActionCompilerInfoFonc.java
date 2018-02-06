package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;

public class C_ActionCompilerInfoFonc extends Action
{
	public C_ActionCompilerInfoFonc()
	{
		setText("Compiler Info Fonc");
		setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_infoFonc.png"));
	}
	
	@Override
	public void run() 
	{
		// compilation CM
		C_ToolsDistribution.f_COMPILATION_INFO_FONC();
	}
}
