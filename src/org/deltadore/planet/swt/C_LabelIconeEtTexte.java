package org.deltadore.planet.swt;

import org.deltadore.planet.model.define.C_DefinePolices;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class C_LabelIconeEtTexte extends Composite
{
	/** Icône **/
	private Label				c_labelIcone;
	
	/** Text **/
	private Label				c_labelTexte;
	
	private boolean				m_is_horizontal;
	
	/**
	 * Constructeur.
	 * 
	 * @param parent parent
	 */
	public C_LabelIconeEtTexte(Composite parent, boolean horizontal)
	{
		super(parent, SWT.NONE);
		
		// récupération des paramètres
		m_is_horizontal = horizontal;
		
		// initialisation interface
		f_INIT_UI();
	}
	
	/**
	 * Initialisation interface.
	 * 
	 */
	private void f_INIT_UI()
	{
		// composite
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// layout
		GridLayout layout = null;
		
		if(m_is_horizontal)
		{
			layout = new GridLayout(2, false);
			setLayout(layout);
			
			// label icone
			c_labelIcone = new Label(this, SWT.NONE);
			C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelIcone, 1, 1, false, true, GridData.CENTER, GridData.CENTER);
			
			// label texte
			c_labelTexte = new Label(this, SWT.NONE);
			c_labelTexte.setFont(C_DefinePolices.ARIAL_10_BOLD);
			C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelTexte, 1, 1, true, true, GridData.CENTER, GridData.CENTER);
		}
		else
		{
			layout = new GridLayout();
			setLayout(layout);
			
			// label icone
			c_labelIcone = new Label(this, SWT.NONE);
			C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelIcone, 1, 1, true, true, GridData.CENTER, GridData.END);
			
			// label texte
			c_labelTexte = new Label(this, SWT.NONE);
			c_labelTexte.setFont(C_DefinePolices.ARIAL_10_BOLD);
			C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelTexte, 1, 1, true, true, GridData.CENTER, GridData.BEGINNING);
		}
	}
	
	/**
	 * Affectation de la police du texte.
	 * 
	 * @param police police de caractères
	 * @return true si succès
	 */
	public boolean f_SET_POLICE(Font police)
	{
		c_labelTexte.setFont(police);
		
		return true;
	}
	
	/**
	 * Affectation de la couleur du texte.
	 * 
	 * @param couleur couleur du texte
	 * @return true si succès
	 */
	public boolean f_SET_COULEUR_POLICE(Color couleur)
	{
		c_labelTexte.setForeground(couleur);
		
		return true;
	}
	
	/**
	 * Définit le'icône et le texte.
	 * 
	 * @param icone icône message
	 * @param message message à afficher
	 * @return true si succès
	 */
	public boolean f_SET_ICONE_ET_TEXTE(ImageDescriptor icone, String message)
	{
		if(icone != null)
			c_labelIcone.setImage(icone.createImage());
		else
			c_labelIcone.setImage(null);
		c_labelTexte.setText(message);
		
		return true; // ok
	}
}
