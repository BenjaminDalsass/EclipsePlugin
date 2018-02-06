package org.deltadore.planet.ui.vues;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class C_Console extends MessageConsole 
{

	public C_Console(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IPageBookViewPage createPage(IConsoleView view) 
	{
		IPageBookViewPage page = super.createPage(view);
		
		return page;
	}
}
