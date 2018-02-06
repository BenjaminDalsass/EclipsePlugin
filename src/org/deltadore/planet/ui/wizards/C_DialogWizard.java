package org.deltadore.planet.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class C_DialogWizard extends WizardDialog
{
	public C_DialogWizard(Shell parentShell, IWizard newWizard) 
	{
		super(parentShell, newWizard);
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		setHelpAvailable(true);
	}
}
