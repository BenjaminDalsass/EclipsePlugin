package org.deltadore.planet.plugin;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class C_Startup implements IStartup, ISelectionListener
{

	@Override
	public void earlyStartup() {
		System.out.println("C_Startup.earlyStartup()");
//		C_JobInitialisationPerspective init = new C_JobInitialisationPerspective();
//		init.schedule();
		
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
		
		System.out.println(PlatformUI.getWorkbench().getService(ISelectionService.class));
		
		
	}

	@Override
	public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

}
