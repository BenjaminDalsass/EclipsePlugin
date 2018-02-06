package org.deltadore.planet.ui.wizards;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineDateFormat;
import org.deltadore.planet.model.define.C_DefineInfosServeurs;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescBaseSites;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.swt.C_VerificateurSaisie;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsSite;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class C_PageWizardFormulaireSite extends WizardPage implements ModifyListener
{
    /** Wizard **/
    public static String 					ID = "WIZARD.PAGE.FORMULAIRE_SITE";

    /** Images **/
    private static ImageDescriptor 			IMAGE_WIZARD;

    /** entrées SVN **/
    private C_DescRelease[]			 		m_releases;
    
	/** Numero de site **/
	private int								m_int_numeroSite;
	
	/** Nom du site **/
	private String							m_str_nomSite;
	
	/** Répertoire sur serveur site **/
	private File							m_repertoireServeurSite;
	
    /** combo des releases **/
    private Combo 							c_comboRelease;
    
	/** Champs numéro **/
	private Text 							c_fieldNumero;
	
	/** Champs nom **/
	private Text 							c_fieldNom;
	
    static
    {
    	IMAGE_WIZARD = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("wizard/wizard_form_site.png");
    }
    
	/**
	 * Constructeur.
	 * 
	 */
	public C_PageWizardFormulaireSite() 
	{
		super(ID, "Informations du site", IMAGE_WIZARD);
		
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
		setPageComplete(false);
		setDescription("Saisissez les informations sur le site...");
		
		// récupération des releases
		m_releases = C_Bases.f_GET_BASE_RELEASES().f_GET_TAG_RELEASES();
	}
	
	private void f_UPDATE_DESC_SITE()
	{
		if(c_fieldNumero.getText().length() > 0)
			m_int_numeroSite = new Integer(c_fieldNumero.getText());
		else
			m_int_numeroSite = 9999;
		
		m_str_nomSite = c_fieldNom.getText();
	}
	
	/**
	 * Vérification.
	 * 
	 */
	public boolean f_CHECK()
	{
		// update desc site
		f_UPDATE_DESC_SITE();
		
		// récupération descriptif release
		C_DescRelease descRelease = f_GET_DESC_RELEASE();
		
		// numéro
		if(c_fieldNumero.getText().length() < 3 || c_fieldNumero.getText().length() > 6 )
		{
			setMessage("Le numéro de site est invalide.\n Le numéro doit contenir entre 3 et 6 digits.", WizardPage.ERROR);
			setPageComplete(false);
			return false;
		}
		
		// nom
		if(m_str_nomSite.length() < 10)
		{
			setMessage("Le nom de site est invalide.\n Le nom doit contenir au moins 10 caractères.", WizardPage.ERROR);
			setPageComplete(false);
			return false;
		}
		
		// unicité dans base
		C_DescBaseSites descBaseSites = C_ToolsSite.f_CHARGER_BASE_SITES();
		if(descBaseSites.f_IS_SITE_EXIST(m_int_numeroSite))
		{
			setMessage("Le site existe déjà dans la base.", WizardPage.ERROR);
			setPageComplete(false);
			return false;
		}
		
		// vérification présence répertoire srv sites
		File repertoireSites = C_DefineInfosServeurs.f_GET_SRV_MEYLAN_REPERTOIRE_SITE_FILE();
		File[] files = repertoireSites.listFiles(new FileFilter() 
		{
			@Override
			public boolean accept(File pathname) 
			{
				if(pathname.getName().startsWith("_" + m_int_numeroSite))
					return true;
				else
					return false;
			}
		});
		if(files.length == 0)
		{
			setMessage("Le site n'a pas de dossier sur le serveur de site.", WizardPage.ERROR);
			setPageComplete(false);
			return false;
		}
		else if(files.length > 1)
		{
			setMessage("Le site a plusieurs dossiers sur le serveur de site.", WizardPage.ERROR);
			setPageComplete(false);
			return false;
		}
		else
		{
			m_repertoireServeurSite = files[0];
		}
		
		// récupération chemin dossier dev local
		String cheminDevLocal = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_DEVELOPPEMENT);
		
		// unicité release dans dossier de dev local
		String cheminDevLocalRelease = cheminDevLocal + File.separator + m_str_nomSite;
		if(C_ToolsFichiers.f_EXISTE(cheminDevLocalRelease))
		{
			setMessage("La site est déjà présent dans le dossier de développement", DialogPage.ERROR);
			setPageComplete(false);
			return false;
		}
		
		// présence projet dans workspace
		if(descRelease.f_IS_ORGANISATION_INITIALE() 
		&& C_ToolsWorkbench.f_IS_PROJET_EXISTE(m_str_nomSite))
		{
			setMessage("La site est déjà présent dans le workspace", DialogPage.ERROR);
			setPageComplete(false);
			return false;
		}
		
		// ok
		setMessage(null);
		setPageComplete(true);
		return true;
	}
	
	public C_DescRelease f_GET_DESC_RELEASE()
	{
		return m_releases[c_comboRelease.getSelectionIndex()];
	}
	
	public C_DescDistribution f_GET_DESC_SITE()
	{
		// update desc site
		f_UPDATE_DESC_SITE();
		
		C_DescDistribution site = new C_DescDistribution();
		site.f_SET_NUMERO_AFFAIRE(m_int_numeroSite);
		site.f_SET_NOM_AFFAIRE(m_str_nomSite);
		site.f_SET_VERSION_MAJEURE(f_GET_DESC_RELEASE().f_GET_VERSION_MAJEURE());
		site.f_SET_VERSION_MINEURE(f_GET_DESC_RELEASE().f_GET_VERSION_MINEURE());
		site.f_SET_DATE_DISTRIBUTION(C_DefineDateFormat.DATE_HEURE.format(new Date()));
		
		if(m_repertoireServeurSite != null)
			site.f_SET_REPERTOIRE_SERVEUR(m_repertoireServeurSite.getName());
		
		return site;
	}
	
	@Override
	public void createControl(Composite parent) 
	{
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		
		// combo release
		Label labelRelease = new Label(control, SWT.NONE);
		labelRelease.setText("Release:");
		c_comboRelease = new Combo(control, SWT.READ_ONLY);
		c_comboRelease.setItems(C_ToolsRelease.f_CONVERT_TO_ARRAY_STRING(m_releases));
		c_comboRelease.select(0);
		
		// numéro site
		Label labelNumeroSite = new Label(control, SWT.NONE);
		labelNumeroSite.setText("Numéro:");
		c_fieldNumero = new Text(control, SWT.BORDER);
		c_fieldNumero.addModifyListener(this);
		c_fieldNumero.addVerifyListener(new C_VerificateurSaisie("[0-9]{0,5}"));
		c_fieldNumero.forceFocus();
		
		// nom site
		Label labelNomSite = new Label(control, SWT.NONE);
		labelNomSite.setText("Libellé:");
		c_fieldNom = new Text(control, SWT.BORDER);
		c_fieldNom.addModifyListener(this);
		C_VerificateurSaisie verificateur = new C_VerificateurSaisie("[0-9a-zA-Z_]{0,35}");
		verificateur.f_SET_SAISIE_MAJUSCULE(true);
		c_fieldNom.addVerifyListener(verificateur);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_fieldNom, 1, 1, true, false, GridData.FILL, GridData.BEGINNING);
		
		// affectation du contrôleur
		setControl(control);
	}
	
	@Override
	public void modifyText(ModifyEvent e) 
	{
		// vérification saisie
		f_CHECK();
	}
}
