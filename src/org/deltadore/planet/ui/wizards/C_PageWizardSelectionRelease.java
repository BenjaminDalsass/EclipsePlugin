package org.deltadore.planet.ui.wizards;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineAides;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.define.C_DefineRelease;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class C_PageWizardSelectionRelease extends WizardPage implements SelectionListener
{
    /** Wizard **/
    public static String 					ID = "WIZARD.PAGE.SELECTION_RELEASE";
    
    /** Images **/
    private static ImageDescriptor 			IMAGE_WIZARD;
    private static ImageDescriptor 			IMAGE_RELEASE;
    private static ImageDescriptor			IMAGE_DEVELOPPEMENT;
    
    /** Nom de la release **/
    private String							m_str_nomRelease;
    
    static
    {
    	IMAGE_WIZARD = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("wizard/wizard_ref.png");
		IMAGE_RELEASE = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(C_DefineRelease.NOM_IMAGE_RELEASE);
		IMAGE_DEVELOPPEMENT = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(C_DefineRelease.NOM_IMAGE_RELEASE_BETA);
    }
    
	/**
	 * Constructeur.
	 * 
	 */
	public C_PageWizardSelectionRelease() 
	{
		super(ID, "S�lection d'une release", IMAGE_WIZARD);
		
		// r�cup�ration des param�tres
		
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
		setPageComplete(false);
	}
	
	/**
	 * V�rification.
	 * 
	 */
	private void f_CHECK()
	{
		// r�cup�ration chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// pr�sence r�pertoire de d�veloppement local
		if(!C_DefinePreferencesPlugin.f_IS_DOSSIER_DEVELOPPEMENT_LOCAL_EXIST())
		{
			// message
			setMessage("Dossier de d�veloppement absent", DialogPage.ERROR);
			setPageComplete(false);
			return;
		}
		
		// chemin release local
		String cheminDevLocalRelease = cheminDevLocal + "/" + m_str_nomRelease;
		
		// tout est d�j� ok
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease)
		&& C_ToolsWorkbench.f_IS_PROJET_EXISTE(m_str_nomRelease))
		{
			setMessage("La release s�lectionn�e est d�j� pr�sente", DialogPage.INFORMATION);
			setPageComplete(false);
			return;
		}
		
		// unicit� release dans dossier de dev local
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease))
		{
			setMessage("La release s�lectionn�e est d�j� pr�sente dans le dossier de d�veloppement", DialogPage.ERROR);
			setPageComplete(false);
			return;
		}
		
		// pr�sence projet dans workspace
		if(C_ToolsWorkbench.f_IS_PROJET_EXISTE(m_str_nomRelease))
		{
			setMessage("La release s�lectionn�e est d�j� pr�sente dans le workspace", DialogPage.ERROR);
			setPageComplete(false);
			return;
		}
		
		// ok
		setMessage("S�l�ctionnez la release souhait�e dans la liste ci dessous...", WizardPage.INFORMATION);
		setPageComplete(true);
	}
	
	/**
	 * Retourne la s�lection.
	 * 
	 * @return la s�lection
	 */
	public C_DescRelease f_GET_RELEASE()
	{
		return C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASE(m_str_nomRelease);
	}
	
	@Override
	public void createControl(Composite parent) 
	{
		// variables
		boolean first = true;
		
		// scroll form
		ScrolledComposite scrollForm = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		scrollForm.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		scrollForm.setExpandHorizontal(true);
		scrollForm.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// aide
		C_DefineAides.f_ATTACH_AIDE(scrollForm, C_DefineAides.TELECHARGEMENT_RELEASE);
		
		// contenu
		Composite content = new Composite(scrollForm, SWT.NONE);
		content.setLayout(new GridLayout());
		scrollForm.setContent(content);
		
		if(C_Bases.f_GET_BASE_RELEASES().m_releases != null)
		{
			// parcours des r�f�rences...
			for(C_DescRelease release : C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASES())
			{
				Button btn = new Button(content, SWT.RADIO);
				btn.setText(release.f_GET_NOM());
				btn.setData(release.f_GET_NOM());
				btn.addSelectionListener(this);
				
				if(first)
				{
					btn.setFont(new Font(parent.getDisplay(), "Arial", 12, SWT.BOLD));
					btn.setImage(IMAGE_DEVELOPPEMENT.createImage());
					btn.setSelection(true);
					btn.setText(release.f_GET_NOM() + " " + C_DefineRelease.VERSION_BETA);
					m_str_nomRelease = release.f_GET_NOM();
					first = false;
				}
				else
				{
					btn.setImage(IMAGE_RELEASE.createImage());
				}
			}
			
			// maj taille contenu
			Point size = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			content.setSize(size);
		}
		else
		{
			// message
			setErrorMessage("Probl�me de r�cup�ration des releases.");
		}
		
		// affectation du contr�leur
		setControl(scrollForm);
		
		// v�rifications
		f_CHECK();
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) 
	{
		// r�cup�ration bouton s�lectionn�
		Button btn = (Button) e.getSource();
		
		// sur s�lection
		if(btn.getSelection())
		{
			// r�cup�ration s�lectio
			m_str_nomRelease = btn.getData().toString();
			
			// v�rification
			f_CHECK();
		}
	}

	// inutilis�s
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}
}
