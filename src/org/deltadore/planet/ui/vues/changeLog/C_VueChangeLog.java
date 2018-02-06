package org.deltadore.planet.ui.vues.changeLog;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

public class C_VueChangeLog extends ViewPart implements MouseListener
{
	public static final String 					ID = "VUE.CHANGE_LOG";

	private static int							NOMBRE_LAPINS = 13;
	
	/** Toolkit **/
	private FormToolkit 						c_toolkit;
	
	/** Formulaire **/
	private Form 								c_form;
	
	/** Formulaire scrollable **/
	private ScrolledComposite					c_scollForm;
	private Composite 							c_contenu;
	
	/** composite de recherche **/
	private C_CompositeRechercheChangeLog 		c_searchComposite;
	
	/** Lapin crétin **/
	private Label								c_labelLapin;
	private	int									m_int_numeroLapin;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_VueChangeLog() 
	{
		super();
		
		m_int_numeroLapin = 1;
	}

	public void createPartControl(Composite parent) 
	{
		// initalisation barre de boutons
		f_INIT_BARRE_BOUTONS();
		
		// création du kit
		c_toolkit = new FormToolkit(parent.getDisplay()); 
		
		c_form = c_toolkit.createForm(parent);
		c_form.setText("Notes des crétins à propos de Planet");
		c_toolkit.decorateFormHeading(c_form);
		c_form.getBody().setLayout(new GridLayout(2, false));
		c_form.getBody().setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		C_ActionAjouter actionAjouter = new C_ActionAjouter();
		C_ActionRechercher actionRechercher = new C_ActionRechercher();
		
		actionAjouter.setEnabled(false);
		c_form.getToolBarManager().add(actionRechercher);
		c_form.getToolBarManager().add(actionAjouter);
		c_form.getToolBarManager().update(true);
		c_form.getMenuManager().add(actionAjouter);
		c_form.getMenuManager().add(actionRechercher);
		c_form.setToolBarVerticalAlignment(SWT.TOP);
		
		c_searchComposite = new C_CompositeRechercheChangeLog(c_form.getHead());
		
		c_labelLapin = new Label(c_form.getBody(), SWT.NONE);
		f_RANDOM_LAPIN();
		c_labelLapin.addMouseListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelLapin, 1, 1, false, true, GridData.HORIZONTAL_ALIGN_CENTER, GridData.VERTICAL_ALIGN_CENTER);
		
		// création formulaire
		c_scollForm = new ScrolledComposite(c_form.getBody(), SWT.V_SCROLL);
		c_scollForm.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		c_scollForm.setExpandHorizontal(true);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_scollForm, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		c_contenu = new Composite(c_scollForm, SWT.NONE);
		c_contenu.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_contenu, 1, 1, true, true, GridData.FILL, GridData.FILL);
		c_contenu.setLayout(new GridLayout(1, false));
		
		C_DescRelease rel1 = new C_DescRelease();
		rel1.f_SET_VERSION_MAJEURE(2);
		rel1.f_SET_VERSION_MINEURE(3);
		f_GET_SECTION_RELEASE(c_contenu, rel1);
		
		C_DescRelease rel2 = new C_DescRelease();
		rel2.f_SET_VERSION_MAJEURE(2);
		rel2.f_SET_VERSION_MINEURE(2);
		f_GET_SECTION_RELEASE(c_contenu, rel2);
		
		C_DescRelease rel3 = new C_DescRelease();
		rel3.f_SET_VERSION_MAJEURE(2);
		rel3.f_SET_VERSION_MINEURE(1);
		f_GET_SECTION_RELEASE(c_contenu, rel3);
		
		Point size = c_contenu.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_contenu.setSize(size);
		
		c_scollForm.setContent(c_contenu);
	}
	
	private Section f_GET_SECTION_RELEASE(Composite parent, C_DescRelease release)
	{
		Section section = c_toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(section, 1, 1, true, false, GridData.FILL, GridData.FILL);
		section.setText(release.f_GET_VERSION());
		section.addExpansionListener(new c_MyListener());
		f_SECTION_FAKE(section);
		
		return section;
	}
	
	private void f_SECTION_FAKE(Section section)
	{
		FormText text = new FormText(section, SWT.NONE);
		StringBuffer buff = new StringBuffer();
		buff.append("<form>");
		for(int i = 1 ; i < 10 ; i++)
			buff.append(String.format("<li style=\"image\" value=\"bullet\"><b color=\"blue\">234%d</b> Changelog</li>", i));
		buff.append("</form>");
		text.setText(buff.toString(), true, true);
		text.setImage("bullet", C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("bullet_ball_blue.png").createImage());
		
		section.setClient(text);
	}
	
	/**
	 * Initialisation de la barre de boutons.
	 * 
	 */
	private void f_INIT_BARRE_BOUTONS()
	{
		
	}

	@Override
	public void setFocus() 
	{
		
	}

	private void f_RANDOM_LAPIN()
	{
		m_int_numeroLapin = (int) (1+(NOMBRE_LAPINS-1)*Math.random());
		String image = String.format("lapins/lapin%d.png", m_int_numeroLapin);
		c_labelLapin.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(image).createImage());
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		if(e.button == 1)
			m_int_numeroLapin++;
		else
			m_int_numeroLapin--;
		
		if(m_int_numeroLapin == NOMBRE_LAPINS)
			m_int_numeroLapin = 1;
		if(m_int_numeroLapin == 0)
			m_int_numeroLapin = NOMBRE_LAPINS;
		
		String image = String.format("lapins/lapin%d.png", m_int_numeroLapin);
		c_labelLapin.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(image).createImage());
	}

	private void f_FORM_REFLOW()
	{
		Point size = c_contenu.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_contenu.setSize(size);
		c_scollForm.update();
	}
	
	// inutilisés
	@Override
	public void mouseDoubleClick(MouseEvent e) {}
	@Override
	public void mouseUp(MouseEvent e) {}
	
	private class c_MyListener extends ExpansionAdapter
	{
		public void expansionStateChanged(ExpansionEvent e) 
		{
			f_FORM_REFLOW();
		}
	}
	
	/**
	 * ACTION AJOUTER
	 *
	 */
	private class C_ActionAjouter extends Action
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionAjouter()
		{
			super();
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("add2.png"));
			setText("Ajouter une entrée");
		}
		
		@Override
		public void run() 
		{
		}
	}
	
	/**
	 * ACTION RECHERCHER
	 *
	 */
	private class C_ActionRechercher extends Action
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionRechercher()
		{
			super();
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("view.png"));
			setText("Activer la recherche");
			setChecked(false);
		}
		
		@Override
		public void run() 
		{
			if(isChecked())
			{
				c_form.setHeadClient(c_searchComposite);
			}
			else
			{
				c_form.setHeadClient(null);
			}
		}
	}
}