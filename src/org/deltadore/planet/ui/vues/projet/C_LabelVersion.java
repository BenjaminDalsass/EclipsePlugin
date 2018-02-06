package org.deltadore.planet.ui.vues.projet;

import org.deltadore.planet.model.define.C_DefineRelease;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class C_LabelVersion extends Composite
{
	/** Icone **/
	private Label 			c_labelIcone;
	
	/** Version **/
	private Label 			c_labelVersion;
	
	/**
	 * Constructeur.
	 * 
	 * @param parent
	 */
	public C_LabelVersion(Composite parent)
	{
		super(parent, SWT.NONE);

		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// layout
		setLayout(new GridLayout(2, false));
		
		// font
		Font font = new Font(getDisplay(), "Arial", 10, SWT.BOLD);
		
		// icone
		c_labelIcone = new Label(this, SWT.NONE);
		c_labelIcone.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("jar_bean_new.png").createImage());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelIcone, 1, 1, false, false, GridData.FILL, GridData.FILL);
		
		// version
		c_labelVersion = new Label(this, SWT.NONE);
		c_labelVersion.setText("Version _._");
		c_labelVersion.setFont(font);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelVersion, 1, 1, true, false, GridData.FILL, GridData.CENTER);
	}
	
	/**
	 * Définit la version de release.
	 * 
	 * @param descRelease descriptif release
	 * @return true si succès
	 */
	public boolean f_SET_VERSION(C_DescRelease descRelease)
	{
		if(descRelease.f_IS_TAGGED_RELEASE())
		{
			c_labelVersion.setText(descRelease.f_GET_VERSION());
			c_labelIcone.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(C_DefineRelease.NOM_IMAGE_RELEASE).createImage());
		}
		else
		{
			c_labelVersion.setText(C_DefineRelease.VERSION_BETA);
			c_labelIcone.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(C_DefineRelease.NOM_IMAGE_RELEASE_BETA).createImage());
		}
		
		return true;
	}
}
