package org.deltadore.planet.swt;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class C_TextSearch extends Composite implements MouseListener, ModifyListener
{
	/** icônes **/
	private ImageDescriptor		m_imageSearch;
	private ImageDescriptor		m_imageGomme;
	
	/** champs de recherche **/
	private Text 				c_text;
	
	/** Icone **/
	private Label				c_icon;
	
	/**
	 * Constructeur.
	 * 
	 * @param parent composite parent
	 */
	public C_TextSearch(Composite parent)
	{
		super(parent, SWT.BORDER);
		
		// initialisation
		f_INIT();
		
		// initialisation interface
		f_INIT_UI();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		m_imageSearch = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("view.png");
		m_imageGomme = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("eraser.png");
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
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);
		
		// texte
		c_text = new Text(this, SWT.NONE);
		c_text.addModifyListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_text, 1, 1, true, true, GridData.VERTICAL_ALIGN_CENTER, GridData.FILL);
		
		// icône
		c_icon = new Label(this, SWT.SEARCH);
		c_icon.setBackground(new Color(getDisplay(), 255, 255, 255));
		c_icon.setImage(m_imageSearch.createImage());
		c_icon.addMouseListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_icon, 1, 1, false, true, GridData.FILL, GridData.FILL);
	}
	
	/**
	 * Mise à jour interface.
	 * 
	 */
	private void f_UPDATE_UI()
	{	
		if(c_text.getText().length() == 0)
			c_icon.setImage(m_imageSearch.createImage());
		else
			c_icon.setImage(m_imageGomme.createImage());
	}
	
	/**
	 * Retourne le texte.
	 * 
	 * @return
	 */
	public String f_GET_SEARCH_TEXTE()
	{
		return c_text.getText();
	}

	@Override
	public void mouseDown(MouseEvent e) 
	{
		// raz texte
		c_text.setText("");
		
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	public void addModifyListener(ModifyListener listener) 
	{
		c_text.addModifyListener(listener);
	}

	@Override
	public void modifyText(ModifyEvent e) 
	{
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	@Override
	public boolean setFocus() 
	{
		if(!c_text.isDisposed())
			return c_text.setFocus();
		else
			return false;
	}
	
	// inutilisés
	@Override
	public void mouseUp(MouseEvent e) {} 
	@Override
	public void mouseDoubleClick(MouseEvent e) {}
}
