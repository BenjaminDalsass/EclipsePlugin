package org.deltadore.planet.ui.vues.changeLog;

import org.deltadore.planet.swt.C_TextSearch;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class C_CompositeRechercheChangeLog extends Composite
{
	/** Combo depuis **/
	private Combo				c_comboSince;
	
	/** Combo type entrée **/
	private Combo				c_comboType;

	/** Champ de recherche **/
	private C_TextSearch		c_textSearch;
	
	/**
	 * Constructeur.
	 * 
	 * @param parent composite parent
	 */
	public C_CompositeRechercheChangeLog(Composite parent) 
	{
		super(parent, SWT.NONE);
		
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
		GridLayout layout = new GridLayout(3, false);
		layout.marginTop = 2;
		layout.marginLeft = 2;
		layout.marginBottom = 2;
		layout.marginRight = 2;
		layout.horizontalSpacing = 2;
		setLayout(layout);
		
		// combo depuis
		c_comboSince = new Combo(this, SWT.READ_ONLY);
		c_comboSince.setItems(new String[]{"Tous", "Hier", "Cette semaine"});
		c_comboSince.select(0);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_comboSince, 1, 1, false, false, GridData.FILL, GridData.FILL);
		
		// combo type
		c_comboType = new Combo(this, SWT.READ_ONLY);
		c_comboType.setItems(new String[]{"Tous", "Evolution noyau", "Ajout fonction", "Correction"});
		c_comboType.select(0);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_comboType, 1, 1, false, false, GridData.FILL, GridData.FILL);
		
		// texte de recherche
		c_textSearch = new C_TextSearch(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_textSearch, 1, 1, true, false, GridData.FILL, GridData.FILL);
	}
}
