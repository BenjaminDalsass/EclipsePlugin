package org.deltadore.planet.ui.vues.fonctionExplorer;

import org.deltadore.planet.tools.C_Tools;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

public class C_VueFonctionExplorer extends ViewPart
{
	public static final String 			ID = "VUE.FONCTIONS_EXPLORER";

	/** Toolkit **/
	private FormToolkit 				c_toolkit;
	
	/** Formulaire **/
	private Form						c_form;
	
	/** Contenu **/
	private Composite 					c_contenu;
	
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_VueFonctionExplorer() 
	{
		super();
	}

	public void createPartControl(Composite parent) 
	{
		// création du kit
		c_toolkit = new FormToolkit(parent.getDisplay()); 
		
		// images
		ImageDescriptor	IMAGE_CUBE = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("module.png");
			
		// création contenu
		c_form = new Form(parent, SWT.NONE);
		c_form.setText("Explorateur de fonctions");
		c_form.setImage(IMAGE_CUBE.createImage());
		c_toolkit.decorateFormHeading(c_form);
		
		// texte
//		c_form.getHead().setLayout(new GridLayout());
//		Text t = new Text(c_form.getHead(), SWT.BORDER);
//		t.setText("");
		
		// composite
		GridLayout layout = new GridLayout();
		c_form.getBody().setLayout(layout);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		f_INIT_CONTENT();
	}
	
	private void f_INIT_CONTENT()
	{
		ImageDescriptor	IMAGE_SERVEUR = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("logo_dd_p.png");
		ImageDescriptor	IMAGE_CM = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("cm.png");
		ImageDescriptor	IMAGE_JAVA = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("sample.gif");
		
		GridData gridData;
		
		c_contenu = new Composite(c_form.getBody(), SWT.NONE);
//		c_contenu.setBackground(new Color(c_form.getDisplay(), 213, 213, 255));
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		c_contenu.setLayoutData(gridData2);
		
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		c_contenu.setLayout(layout);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		List listFonctions = new List(c_contenu, SWT.V_SCROLL | SWT.BORDER);
		listFonctions.setLayoutData(gridData);
		
		IPackageFragment[] elements = C_Tools.f_GET_FONCTIONS_ACTU();
		
		if(elements != null)
		{
			for(IPackageFragment e : elements)
			{
				try
				{
					String search = "fct_";
	//				IJavaElement[] eles = e.getChildren();
	//				for(IJavaElement en : eles)
					String name = e.getElementName();
					int index = name.indexOf(search);
					listFonctions.add(name.substring(index+search.length()));
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					
				}
			}
		}
		
		Button btnJava = new Button(c_contenu, SWT.NONE);
		btnJava.setText("Java");
		btnJava.setImage(IMAGE_JAVA.createImage());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnJava.setLayoutData(gridData);
		
		Button btnCM = new Button(c_contenu, SWT.NONE);
		btnCM.setText("CM");
		btnCM.setImage(IMAGE_CM.createImage());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnCM.setLayoutData(gridData);
		
		Button btnServeur = new Button(c_contenu, SWT.NONE);
		btnServeur.setText("Serveur");
		btnServeur.setImage(IMAGE_SERVEUR.createImage());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnServeur.setLayoutData(gridData);
	}
	
	@Override
	public void setFocus() 
	{
		
	}
	
}