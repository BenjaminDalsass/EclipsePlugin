package org.deltadore.planet.ui.dialogs;

import java.io.File;

import org.deltadore.planet.model.applicationsPlanet.patch.C_Patch;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.jobs.C_JobExportationDistribution;
import org.deltadore.planet.swt.I_EcouteurActions;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsSite;
import org.deltadore.planet.ui.vues.projet.C_LabelRevision;
import org.deltadore.planet.ui.vues.projet.C_LabelVersion;
import org.deltadore.planet.ui.vues.projet.C_VueProjet;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;

public class C_FenetreExportationDistribution extends Shell implements SelectionListener, I_EcouteurActions, ModifyListener
{
	/** Projet associé **/
	private IProject					m_projet;
	
	/** Descriptif release **/
	private C_DescRelease				m_descRelease;
	
	/** Descriptif site **/
	private C_DescDistribution			m_descSite;
	
	/** flag projet dirty **/
	private boolean 					m_is_dirty;
	
	/** Formulaire **/
	private Form						c_formulaire;
	
	/** Groupe informations **/
	private Group						c_groupeInfos;
	
	/** Label version **/
	private C_LabelVersion				c_labelVersion;
	
	/** Label révision **/
	private C_LabelRevision				c_labelRevision;
	
	/** Groupe des radios button **/
	private Group						c_groupeRadio;
	
	/** Groupe des radios options button **/
	private Group						c_groupeOptions;
	
	/** Groupe patch **/
	private Group						c_groupePatch;
	
	/** Sélection repertoire **/
	private Composite					c_selectionRepertoire;
	
	private Button 						c_btnExportServeur;
	private Button 						c_btnExportDefaut;
	private Button 						c_btnExportLibre;
	private Button 						c_btnPatch;
	
	/** Barre de boutons **/
	private Composite					c_barreBoutons;
	
	/** Texte sélection répertoire **/
	private Label 						c_texteSelectionRepertoire;
	
	/** Bouton choisir **/
	private Button 						c_btnChoisir;
	
	/** Bouton exporter config **/
	private Button 						c_btExportConfig;
	
	/** Bouton compresser **/
	private Button 						c_btnCompresser;
	
	/** Text field révision to patch **/
	private Text 						c_txt_revisionToPatch;
	
	/** Bouton exporter **/
	private Button 						c_buttonExporter;
	
	/**
	 * Constructeur.
	 * 
	 * @param parent parent shell
	 * @param projet projet à exporter
	 */
	public C_FenetreExportationDistribution(Shell parent, IProject projet)
	{
		super(parent, SWT.DIALOG_TRIM);
		
		// récupération paramètres
		m_projet = projet;
		
		// initialisation
		f_INIT();
		
		// initialisation interface
		f_INIT_UI();
		
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// récupération vue projet
		C_VueProjet vueProjet = C_VueProjet.f_FIND_VUE();
		
		// sécurité
		if(vueProjet != null)
		{
			// récupération descriptif release
			m_descRelease = vueProjet.f_GET_DESC_RELEASE();
			
			// si sité sélectionné
			if(vueProjet.f_IS_SITE_SELECTIONNE())
			{
				// récupération information de localisation sur serveur site
				m_descSite = vueProjet.f_GET_DESC_SITE();
			}
		}
	}
	
	/**
	 * Initialisation interface.
	 * 
	 */
	private void f_INIT_UI() 
	{
		// shell
		setText("Exportation de la distribution");
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginLeft = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		
		// toolkit
		FormToolkit toolkit = new FormToolkit(getDisplay());
		
		// formulaire
		c_formulaire = new Form(this, SWT.NONE);
		c_formulaire.setText("Exportation de la distribution");
		c_formulaire.setFont(new Font(getDisplay(), "Arial", 11, SWT.BOLD));
		c_formulaire.setForeground(new Color(getDisplay(), 44, 89, 211));
		c_formulaire.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("box_software.png").createImage());
		c_formulaire.getBody().setLayout(new GridLayout());
		c_formulaire.getBody().setBackgroundMode(SWT.INHERIT_DEFAULT);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_formulaire, 1, 1, true, true, GridData.FILL, GridData.FILL);
		toolkit.decorateFormHeading(c_formulaire);
		
		// informations
		f_INIT_INFORMATIONS(c_formulaire.getBody());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_groupeInfos, 1, 1, true, false, GridData.FILL, GridData.FILL, 500, -1);
		
		// radios
		f_INIT_RADIO_SELECTION(c_formulaire.getBody());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_groupeRadio, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// option
		f_INIT_CHECK_OPTIONS_SELECTION(c_formulaire.getBody());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_groupeOptions, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// patch
		f_INIT_PATCH(c_formulaire.getBody());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_groupePatch, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
//		// vide
//		Composite c = new Composite(c_formulaire.getBody(), SWT.NONE);
//		C_ToolsSWT.f_GRIDLAYOUT_DATA(c, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// barre de boutons
		f_INIT_BARRE_BOUTONS(c_formulaire.getBody());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_barreBoutons, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		pack();
		C_ToolsSWT.f_CENTER_SHELL(this);
	}
	
	/**
	 * Test si le projet est à jour sur le serveur SVN.
	 * 
	 */
	private void f_CHECK_PROJECT_DIRTY()
	{
		try
		{
			// récupération ressource SVN du projet
			ISVNLocalResource svnLocalRessource = SVNWorkspaceRoot.getSVNResourceFor(m_projet);
			
			// si root workspace
			if(svnLocalRessource.isDirty())
			{
				c_formulaire.setMessage("Attention, mettre à jour le serveur !", DialogPage.WARNING);
			}
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
		}
	}
	
	/**
	 * Mise à jour intarface;
	 * 
	 */
	private void f_UPDATE_UI()
	{
		// accès interface par thread
		getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// test SVN dirty
				f_CHECK_PROJECT_DIRTY();
				
				if(c_btnPatch.getSelection())
					c_groupePatch.setVisible(true);
				else
					c_groupePatch.setVisible(false);
			}
		});
	}
	
	/**
	 * Initialisation du groupe de radios.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_INFORMATIONS(Composite parent)
	{
		// groupe
		c_groupeInfos = new Group(parent, SWT.SHADOW_IN);
		if(m_descSite != null)
			c_groupeInfos.setText("Informations site: " + m_descSite.f_GET_NOM_COMPLET_AFFAIRE());
		else
			c_groupeInfos.setText("Informations release: " + m_descRelease.f_GET_NOM());
		c_groupeInfos.setLayout(new GridLayout(2, false));
		 
		// label version
		c_labelVersion = new C_LabelVersion(c_groupeInfos);
		c_labelVersion.f_SET_VERSION(m_descRelease);
		
		// label révision
		c_labelRevision = new C_LabelRevision(c_groupeInfos);
		c_labelRevision.f_AJOUTE_ECOUTEUR(this);
		c_labelRevision.f_UPDATE_INFORMATIONS();
		c_labelRevision.f_SET_PROJET(m_projet);
	}
	
	/**
	 * Initialisation du groupe de radios.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_RADIO_SELECTION(Composite parent)
	{
		// groupe
		c_groupeRadio = new Group(parent, SWT.SHADOW_IN);
		c_groupeRadio.setText("Exporter vers...");
		c_groupeRadio.setLayout(new GridLayout());
		
		// sécurité
		if(m_descSite != null)
		{
			// radio serveur site
			c_btnExportServeur = new Button(c_groupeRadio, SWT.RADIO);
			if(m_descSite != null)
			{
				c_btnExportServeur.setText("serveur site (" + m_descSite.f_GET_REPERTOIRE_SERVEUR() + ")");
				c_btnExportServeur.setSelection(true);
			}
			else if(m_is_dirty)
			{
				c_btnExportServeur.setText("serveur site (" + m_descSite.f_GET_REPERTOIRE_SERVEUR() + ")");
				c_btnExportServeur.setEnabled(false);
			}
			else
			{
				c_btnExportServeur.setText("serveur site (Pas d'entrée dans base des sites)");
				c_btnExportServeur.setEnabled(false);
			}
			c_btnExportServeur.addSelectionListener(this);
			c_btnExportServeur.setData("SERVEUR");
			C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btnExportServeur, 1, 1, true, true, GridData.FILL, GridData.FILL);
		}
		
		// radio dossier export
		c_btnExportDefaut = new Button(c_groupeRadio, SWT.RADIO);
		c_btnExportDefaut.setText("mon dossier export (" + C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_EXPORT) + ")");
		c_btnExportDefaut.addSelectionListener(this);
		c_btnExportDefaut.setData("DOSSIER");
		if(!C_DefinePreferencesPlugin.f_IS_DOSSIER_EXPORT_EXIST())
			c_btnExportDefaut.setEnabled(false);
		if(m_descSite == null)
			c_btnExportDefaut.setSelection(true);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btnExportDefaut, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// radio libre
		c_btnExportLibre = new Button(c_groupeRadio, SWT.RADIO);
		c_btnExportLibre.setText("un emplacement de mon choix...");
		c_btnExportLibre.addSelectionListener(this);
		c_btnExportLibre.setData("LIBRE");
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btnExportLibre, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// sélection repertoire
		f_INIT_SELECTION_REPERTOIRE(c_groupeRadio);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_selectionRepertoire, 1, 1, true, true, GridData.FILL, GridData.FILL);
	}
	
	/**
	 * Initialisation du groupe de radios.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_CHECK_OPTIONS_SELECTION(Composite parent)
	{
		// groupe
		c_groupeOptions = new Group(parent, SWT.SHADOW_IN);
		c_groupeOptions.setText("Options");
		c_groupeOptions.setLayout(new GridLayout());
		 
		// exporter la config
		c_btExportConfig = new Button(c_groupeOptions, SWT.CHECK);
		c_btExportConfig.setText("Exporter la configuration");
		c_btExportConfig.addSelectionListener(this);
		c_btExportConfig.setData("EXPORT_CONFIG");
		c_btExportConfig.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_cubes.png").createImage());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btExportConfig, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// zipper
		c_btnCompresser = new Button(c_groupeOptions, SWT.CHECK);
		c_btnCompresser.setText("Compresser la distribution");
		c_btnCompresser.addSelectionListener(this);
		c_btnCompresser.setData("COMPRESSER");
		c_btnCompresser.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compress_green.png").createImage());
		c_btnCompresser.setEnabled(false);
		c_btnCompresser.setToolTipText("non implémenté");
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btnCompresser, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
//		// patch
//		c_btnPatch = new Button(c_groupeOptions, SWT.CHECK);
//		c_btnPatch.setText("Créer un patch");
//		c_btnPatch.addSelectionListener(this);
//		c_btnPatch.setData("PATCH");
//		c_btnPatch.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("patch.png").createImage());
//		c_btnPatch.setEnabled(true);
//		c_btnPatch.setToolTipText("Création d'un patch pour la distribution");
//		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btnPatch, 1, 1, true, true, GridData.FILL, GridData.FILL);
	}
	
	/**
	 * Initialisation du groupe de radios.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_PATCH(Composite parent)
	{
		// groupe
		c_groupePatch = new Group(parent, SWT.SHADOW_IN);
		c_groupePatch.setText("Patch");
		c_groupePatch.setLayout(new GridLayout());
		c_groupePatch.setVisible(false);
		
		// Texte révision
		c_txt_revisionToPatch = new Text(c_groupePatch, SWT.BORDER);
		c_txt_revisionToPatch.addModifyListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_txt_revisionToPatch, 1, 1, true, true, GridData.BEGINNING, GridData.FILL);
		
		// List
		List liste = new List(c_groupePatch, SWT.BORDER);
		liste.setSize(250, 80);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(liste, 1, 1, true, true, GridData.FILL, GridData.FILL);
	}
	
	/**
	 * Initialisation de la barre de boutons.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_BARRE_BOUTONS(Composite parent)
	{
		// composite
		c_barreBoutons = new Composite(parent, SWT.NONE);
		c_barreBoutons.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		// exporter
		c_buttonExporter = new Button(c_barreBoutons, SWT.PUSH);
		c_buttonExporter.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("applications.png").createImage());
		c_buttonExporter.setText("Exporter");
		c_buttonExporter.setData("EXPORTER");
		c_buttonExporter.setEnabled(false);
		c_buttonExporter.addSelectionListener(this);
		
		// annuler
		Button buttonAnnuler = new Button(c_barreBoutons, SWT.PUSH);
		buttonAnnuler.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("delete.png").createImage());
		buttonAnnuler.setText("Annuler");
		buttonAnnuler.setData("ANNULER");
		buttonAnnuler.addSelectionListener(this);
	}
	
	/**
	 * Initialisation de la sélection repertoire.
	 * 
	 * @param parent composite parent
	 */
	private void f_INIT_SELECTION_REPERTOIRE(Composite parent)
	{
		// composite
		c_selectionRepertoire = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginRight = 0;
		c_selectionRepertoire.setLayout(layout);
		
		// Label
		c_texteSelectionRepertoire = new Label(c_selectionRepertoire, SWT.BORDER);
		c_texteSelectionRepertoire.setBackground(new Color(getDisplay(), 255, 255, 255));
		c_texteSelectionRepertoire.setText("C:/");
		c_texteSelectionRepertoire.setEnabled(false);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_texteSelectionRepertoire, 1, 1, true, true, GridData.FILL, GridData.CENTER);
		
		// bouton choisir
		c_btnChoisir = new Button(c_selectionRepertoire, SWT.PUSH);
		c_btnChoisir.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("dossier_pp.png").createImage());
		c_btnChoisir.setText("Choisir...");
		c_btnChoisir.setEnabled(false);
		c_btnChoisir.setData("CHOISIR");
		c_btnChoisir.addSelectionListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_btnChoisir, 1, 1, false, true, GridData.FILL, GridData.FILL);
	}
	
	/**
	 * Retourne le chemin du répertoire d'exportation.
	 * 
	 * @return chemin du répertoire d'exportation
	 */
	private String f_GET_CHEMIN_REPERTOIRE()
	{
		if(c_btnExportServeur != null 
		&& c_btnExportServeur.getSelection())
		{
			return C_ToolsSite.f_GET_CHEMIN_REPERTOIRE_PARENT_SITE(m_descSite);
		}
		else if(c_btnExportDefaut.getSelection())
		{
			return C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.POSTE_LOCAL_DOSSIER_EXPORT);
		}
		else if(c_btnExportLibre.getSelection())
		{
			return c_texteSelectionRepertoire.getText();
		}
		else
			return null; // ko
	}
	
	@Override
	protected void checkSubclass() 
	{ 
		// désactiver subclassing TODO
	}

	@Override
	public void widgetSelected(SelectionEvent e) 
	{
		Button btn = (Button) e.getSource();
		
		if(btn.getData().toString().equalsIgnoreCase("SERVEUR"))
		{
			c_texteSelectionRepertoire.setEnabled(false);
			c_btnChoisir.setEnabled(false);
		}
		else if(btn.getData().toString().equalsIgnoreCase("DOSSIER"))
		{
			c_texteSelectionRepertoire.setEnabled(false);
			c_btnChoisir.setEnabled(false);
		}
		else if(btn.getData().toString().equalsIgnoreCase("LIBRE"))
		{
			c_texteSelectionRepertoire.setEnabled(true);
			c_btnChoisir.setEnabled(true);
		}
		else if(btn.getData().toString().equalsIgnoreCase("CHOISIR"))
		{
			// dialogue sélection répertoire
			DirectoryDialog dial = new DirectoryDialog(getShell());
			dial.setText("Choix d'un répertoire...");
			dial.setMessage("Sélectionnez l'emplacement d'exportation de la distribution");
			String filePath = dial.open();
			
			if(filePath != null)
				c_texteSelectionRepertoire.setText(filePath);
		}
		else if(btn.getData().toString().equalsIgnoreCase("EXPORTER"))
		{
			// variables
			boolean suppressionSiExistant = false;
			
			// job d'exportation
			File dossierExport = null;
			if(m_descSite != null)
				dossierExport = new File(f_GET_CHEMIN_REPERTOIRE() + File.separator + m_descSite.f_GET_NOM_COMPLET_AFFAIRE());
			else
				dossierExport = new File(f_GET_CHEMIN_REPERTOIRE() + File.separator + m_descRelease.f_GET_NOM());
			
			// message d'erreur si  dossier destination éxistant
			if(dossierExport.exists())
			{
				suppressionSiExistant = C_ToolsSWT.f_AFFICHE_QUESTION(this, "Exportation distribution", "La distribution est déjà présente dans le répertoire d'exportation, souhaitez-vous la supprimer ?");
				
				if(!suppressionSiExistant)
					return;
			}
			
			// tâche d'exportation
			C_JobExportationDistribution jobExportation = new C_JobExportationDistribution(m_projet, m_descSite, c_labelRevision.f_GET_REVISION(), new File(f_GET_CHEMIN_REPERTOIRE()), c_btExportConfig.getSelection(), suppressionSiExistant);
			jobExportation.schedule();
			
			// fermeture shell (tâche en background)
			setVisible(false);
			dispose();
		}
		else if(btn.getData().toString().equalsIgnoreCase("ANNULER"))
		{
			// fermeture shell
			setVisible(false);
			dispose();
		}
		else if(btn.getData().toString().equalsIgnoreCase("PATCH"))
		{
			f_UPDATE_UI();
		}
	}

	// inutilisés
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void f_ACTION(Object src, String codeTouche) 
	{
		
		c_buttonExporter.setEnabled(true);
	}

	@Override
	public void modifyText(ModifyEvent arg0) 
	{
		C_Patch patch = new C_Patch(m_projet, m_descRelease, new Long(c_txt_revisionToPatch.getText()));
		patch.f_CREATION_FILE_SYSTEM_SYNTHESE();
	}
}
