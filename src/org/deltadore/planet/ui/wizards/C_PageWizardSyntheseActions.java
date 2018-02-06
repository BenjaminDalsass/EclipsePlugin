package org.deltadore.planet.ui.wizards;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

public class C_PageWizardSyntheseActions extends WizardPage
{
    /** Wizard **/
    public static String 					ID = "WIZARD.PAGE.SYNTHESE";
    
    /** Images **/
    private static ImageDescriptor 			IMAGE_WIZARD;
    
    /** Formulaire **/
    private ScrolledComposite				c_scrollForm;
    
    /** Contenu **/
    private Composite 						c_content;
    
    /** Texte de synthèse **/
    private FormText 						c_texteSynthese;
    
    static
    {
    	IMAGE_WIZARD = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("wizard/wizard_synthese.png");
    }
    
	/**
	 * Constructeur.
	 * 
	 */
	public C_PageWizardSyntheseActions() 
	{
		super(ID, "Synthèse des actions", IMAGE_WIZARD);

		// récupération des paramètres
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// raz flag complet
		setPageComplete(true);
		setMessage("Synthèse des actions menées pour complèter l'assistant.", WizardPage.INFORMATION);
	}
	
	public void createControl(Composite parent) 
	{
		// scroll form
		c_scrollForm = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		c_scrollForm.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		c_scrollForm.setExpandHorizontal(true);
		c_scrollForm.setExpandVertical(true);
		c_scrollForm.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// contenu
		c_content = new Composite(c_scrollForm, SWT.NONE);
		c_content.setLayout(new GridLayout());
		c_scrollForm.setContent(c_content);
		
		// form text
		c_texteSynthese = new FormText(c_content, SWT.NONE);
		c_texteSynthese.setColor("couleur", new Color(parent.getDisplay(), 137, 173, 29));
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_texteSynthese, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// affectation du contrôleur
		setControl(c_scrollForm);
	}
	
	public boolean f_SET_TEXTE_SYNTHESE(String text)
	{
		// affectation du texte
		c_texteSynthese.setText(text, true, true);
		
		// maj taille contenu
		Point size = c_content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_content.setSize(size);
		
		// mise à jour
		c_scrollForm.update();
		
		return  true;
	}
	
	/**
	 * Retourne le composite texte.
	 *  
	 * @return composite texte
	 */
	public FormText f_GET_FORM_TEXT()
	{
		return c_texteSynthese;
	}
	
	public boolean canFlipToNextPage() 
	{
		return false;
	}
}
