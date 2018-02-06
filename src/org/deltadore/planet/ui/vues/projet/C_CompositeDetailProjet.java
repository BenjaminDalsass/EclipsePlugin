package org.deltadore.planet.ui.vues.projet;

import java.io.File;
import java.util.Arrays;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineDateFormat;
import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefinePolices;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.model.descriptifs.C_DescRelease.C_InfosSVN;
import org.deltadore.planet.plugin.actions.projet.C_ActionInitialisationConfig;
import org.deltadore.planet.plugin.jobs.C_JobSuppressionRepertoire;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_Tools;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsRunnable;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsSite;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public class C_CompositeDetailProjet extends Composite implements SelectionListener
{
	/** Projet **/
	private IJavaProject 			m_projet;
	
	/** Descriptif de Release **/
	private C_DescRelease 			m_descRelease;
	
	/** Libellé **/
	private Label 					c_labelLibelle;
	
	/** Informations release **/
//	private Label 					c_labelInfosRelease;
	
	/** Version **/
	private C_LabelVersion			c_labelVersion;
	
	/** Révision **/
	private C_LabelRevision 		c_labelRevision;
	
	/** Composite configuration **/
	private C_CompositeConfig		c_compositeConfig;
	
	
	/**
	 * Constructeur.
	 * 
	 * @param parent composite parent
	 */
	public C_CompositeDetailProjet(Composite parent)
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
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// layout
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		
		// label libellé
		c_labelLibelle = new Label(this, SWT.NONE);
		c_labelLibelle.setFont(C_DefinePolices.ARIAL_10_BOLD);
		c_labelLibelle.setText("Nom du projet");
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelLibelle, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// tool bar
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		ToolItem itemImages = new ToolItem(toolBar, SWT.PUSH);
		itemImages.setData("DOSSIER_IMAGE");
		itemImages.setToolTipText("Ouvrir le dossier images");
		itemImages.addSelectionListener(this);
		itemImages.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("paysage.png").createImage());
		ToolItem itemRessource = new ToolItem(toolBar, SWT.PUSH);
		itemRessource.setData("DOSSIER_RESSOURCE");
		itemRessource.setToolTipText("Ouvrir le dossier ressources");
		itemRessource.addSelectionListener(this);
	    itemRessource.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("box.png").createImage());
		ToolItem itemConfig = new ToolItem(toolBar, SWT.PUSH);
		itemConfig.setData("DOSSIER_CONFIG");
		itemConfig.setToolTipText("Ouvrir le dossier config");
		itemConfig.addSelectionListener(this);
	    itemConfig.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_cubes2.png").createImage());
		C_ToolsSWT.f_GRIDLAYOUT_DATA(toolBar, 1, 1, false, false, GridData.END, GridData.FILL);

//		// label
//		c_labelInfosRelease = new Label(this, SWT.NONE);
//		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelInfosRelease, 2, 1, true, true, GridData.FILL, GridData.FILL);
		
		// info
		Composite infosVersion = f_GET_COMPOSITE_INFO_VERSION(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(infosVersion, 2, 1, false, false, GridData.END, GridData.FILL);
		
		// Combo config
		c_compositeConfig = new C_CompositeConfig(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_compositeConfig, 2, 1, true, false, GridData.FILL, GridData.FILL);
	}
	
	/**
	 * Retourne le composite version.
	 * 
	 * @param parent composite parent
	 * @return composite version
	 */
	private Composite f_GET_COMPOSITE_INFO_VERSION(Composite parent)
	{
		// composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		// révision
		c_labelRevision = new C_LabelRevision(composite);
		c_labelRevision.f_UPDATE_ON_CLIC();
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelRevision, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// version
		c_labelVersion = new C_LabelVersion(composite);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelVersion, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		return composite;
	}
	
	/**
	 * Mise à jour des informations du projet et de la config
	 * sélectionnée.
	 * 
	 */
	public void f_UPDATE_INFORMATIONS()
	{
		// accès interface par thread
		getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// récupération info SVN serveur
				final C_InfosSVN infoSVN = m_descRelease.f_RETRIEVE_SVN_INFO();
				
//				// label info 
//				if(infoSVN.m_revision == -1)
//					c_labelInfosRelease.setText("(déconnecté)");
//				else
//					c_labelInfosRelease.setText(C_DefineDateFormat.DATE_HEURE.format(infoSVN.m_date) + " (modifié par " + infoSVN.m_auteur + ")");
				
				// version
				c_labelVersion.f_SET_VERSION(m_descRelease);
				
				c_labelLibelle.setText(m_projet.getProject().getName());
				setBackgroundImage(C_VueProjet.m_imgCloud);
				
				// version initiale
				if(m_descRelease.f_IS_ORGANISATION_INITIALE())
				{
					// récupération descriptif site
					C_DescDistribution descSite = C_ToolsSite.f_CHARGEMENT_DESCRIPTIF_SITE_FROM_PROJECT(m_projet.getProject());
					
					// si site
					if(descSite != null)
					{
						c_labelLibelle.setText(descSite.f_GET_NOM_COMPLET_AFFAIRE());
						setBackgroundImage(C_VueProjet.m_imgBat);
					}
					else // sinon release
					{
						if(m_projet.getProject().getName().equalsIgnoreCase(m_descRelease.f_GET_NOM()))
						{
							c_labelLibelle.setText(m_descRelease.f_GET_NOM());
							setBackgroundImage(C_VueProjet.m_imgRelease);
						}
						else
						{
							c_labelLibelle.setText("Pas de correspondance dans la base");
							setBackgroundImage(C_VueProjet.m_imgBat);
						}
					}
				}
				else
				{
					if(f_GET_CONFIGURATION_SELECTIONNEE().equalsIgnoreCase(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG))
					{
						c_labelLibelle.setText(m_descRelease.f_GET_NOM());
						setBackgroundImage(C_VueProjet.m_imgRelease);
					}
					else
					{
						C_DescDistribution descSite = f_GET_DESC_SITE();
						if(descSite != null)
							c_labelLibelle.setText(descSite.f_GET_NOM_COMPLET_AFFAIRE());
						else
							c_labelLibelle.setText("Pas de correspondance dans la base");
						setBackgroundImage(C_VueProjet.m_imgBat);
					}
				}
				
				layout();
			}
		});
	}
	
	/**
	 * Affectation du projet.
	 * 
	 * @param projet projet associé à la vue
	 * @return true si succès
	 */
	public boolean f_SET_PROJET(IJavaProject projet)
	{
		// récupération du projet
		m_projet = projet;
		
		// récupération release
		m_descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
		
		// révision
		c_labelRevision.f_SET_PROJET(m_projet.getProject());
		
		// mise à jour des configurations
		f_UPDATE_CONFIGS();
		
		// configurations
		if(m_descRelease.f_IS_ORGANISATION_INITIALE())
			c_compositeConfig.setEnabled(false);
		else
			c_compositeConfig.setEnabled(true);
		
		// mise à jour des informations
		f_UPDATE_INFORMATIONS();
		
		return true; // ok
	}
	
	/**
	 * Mise à jour des configurations.
	 * 
	 * @return true si succès
	 */
	public boolean f_UPDATE_CONFIGS()
	{
		// mise à jour des configurations
		return c_compositeConfig.f_UPDATE_CONFIGS();
	}
	
	/**
	 * Mise à jour des configurations.
	 * 
	 * @return true si succès
	 */
	public boolean f_UPDATE_CONFIGS_AND_SELECT(String selection)
	{
		c_compositeConfig.f_UPDATE_CONFIGS();
		c_compositeConfig.f_SET_SELECTED(selection);
		
		// mise à jour des informations
		f_UPDATE_INFORMATIONS();
		
		return true;
	}
	
	/**
	 * Retourne le nom de la configuration sélectionnée.
	 * 
	 * @return nom de la configuration sélectionnée
	 */
	public String f_GET_CONFIGURATION_SELECTIONNEE()
	{
		return c_compositeConfig.c_comboConfig.getText();
	}
	
	public boolean f_IS_SITE_SELECTIONNE()
	{
		// récupération release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// récupération descriptif site
			C_DescDistribution descSite = C_ToolsSite.f_CHARGEMENT_DESCRIPTIF_SITE_FROM_PROJECT(m_projet.getProject());
			
			return descSite != null;
		}
		else
		{
			String nomConfig = f_GET_CONFIGURATION_SELECTIONNEE();
			
			if(nomConfig.equalsIgnoreCase(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG))
				return false;
			else
				return true;
		}
	}
	
	public C_DescDistribution f_GET_DESC_SITE()
	{
		// sécurité
		if(!f_IS_SITE_SELECTIONNE())
			return null;
		
		// récupération release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// récupération descriptif site
			return C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites.f_GET_SITE(m_projet.getProject().getName());
		}
		else
		{
			String nomConfig = f_GET_CONFIGURATION_SELECTIONNEE();
			
			if(nomConfig.equalsIgnoreCase(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG))
				return null;
			else if(C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites == null)
				return null;
			else
				return C_Bases.f_GET_BASE_AFFAIRES_PLANET().m_descBaseSites.f_GET_SITE(nomConfig.replace(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_", ""));
		}
	}
	
	/**
	 * Retourne le descriptif release.
	 * 
	 * @return descriptif release
	 */
	public C_DescRelease f_GET_DESC_RELEASE()
	{
		// récupération release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
		
		return descRelease;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) 
	{
		Widget widget = (Widget) e.getSource();
		
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
		
		File dossier = m_projet.getProject().getLocation().toFile();
		
		if(widget.getData().toString().equalsIgnoreCase("DOSSIER_RESSOURCE"))
		{
			if(descRelease.f_IS_ORGANISATION_INITIALE())
			{
				dossier = new File(dossier, "\\_Winman_java\\_Winman_jclient");
			}
			else
			{
				dossier = new File(dossier, "\\Ressources");
			}
				
			C_ToolsRunnable.f_EXECUTE(new File(dossier.getAbsolutePath()));
		}
		else if(widget.getData().toString().equalsIgnoreCase("DOSSIER_CONFIG"))
		{
			dossier = new File(dossier, "\\Config");
			C_ToolsRunnable.f_EXECUTE(new File(dossier.getAbsolutePath()));
		}
		else if(widget.getData().toString().equalsIgnoreCase("DOSSIER_IMAGE"))
		{
			dossier = new File(dossier, "\\Ressources\\Images");
			C_ToolsRunnable.f_EXECUTE(new File(dossier.getAbsolutePath()));
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
		
	}
	
	/**
	 * COMPOSITE CONFIGURATIONS
	 * 
	 */
	private class C_CompositeConfig extends Composite implements SelectionListener, IJobChangeListener
	{
		/** Combo liste des config **/
		private Combo					c_comboConfig;
		
		/** initialiser **/
		private ToolItem 				c_itemInitialiser;
		
//		/** importer **/
//		private ToolItem 				c_itemImporter;
//		
//		/** exporter **/
//		private ToolItem 				c_itemExporter;
		
		/** supprimer **/
		private ToolItem 				c_itemSupprimer;
		
		/** actualiser **/
		private ToolItem 				c_itemActualiser;
		
		/**
		 * Constructeur.
		 * 
		 * @param parent composite parent
		 */
		public C_CompositeConfig(Composite parent)
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
			// layout
			setLayout(new GridLayout(2, false));
			
			// combo
			c_comboConfig = new Combo(this, SWT.READ_ONLY);
			c_comboConfig.addSelectionListener(this);
			c_comboConfig.setData("COMBO");
			C_ToolsSWT.f_GRIDLAYOUT_DATA(c_comboConfig, 1, 1, true, false, GridData.FILL, GridData.FILL);
			
			// toolbar
			ToolBar toolBar = new ToolBar(this,  SWT.FLAT | SWT.WRAP | SWT.RIGHT);
			C_ToolsSWT.f_GRIDLAYOUT_DATA(toolBar, 1, 1, false, false, GridData.FILL, GridData.FILL);
			
			// bouton initialiser
			c_itemInitialiser = new ToolItem(toolBar, SWT.PUSH);
			c_itemInitialiser.setData("INITIALISER");
			c_itemInitialiser.setToolTipText("Initialiser configuration");
			c_itemInitialiser.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("prepa_affaire.png").createImage());
			c_itemInitialiser.addSelectionListener(this);
			
		    // bouton actualiser
		    c_itemActualiser = new ToolItem(toolBar, SWT.PUSH);
		    c_itemActualiser.setData("ACTUALISER");
		    c_itemActualiser.setToolTipText("Actualiser la liste des configurations");
		    c_itemActualiser.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("refresh_pp.png").createImage());
		    c_itemActualiser.addSelectionListener(this);
		    
		    new ToolItem(toolBar, SWT.SEPARATOR);
		    
//			// bouton importer
//			c_itemImporter = new ToolItem(toolBar, SWT.PUSH);
//			c_itemImporter.setData("IMPORTER");
//			c_itemImporter.setToolTipText("Importer configuration");
//			c_itemImporter.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("import_config.png").createImage());
//			c_itemImporter.addSelectionListener(this);
//			
//			// bouton exporter
//			c_itemExporter = new ToolItem(toolBar, SWT.PUSH);
//			c_itemExporter.setData("EXPORTER");
//			c_itemExporter.setToolTipText("Exporter configuration");
//		    c_itemExporter.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("export_config.png").createImage());
//		    c_itemExporter.addSelectionListener(this);
		    
			// bouton supprimer
		    c_itemSupprimer = new ToolItem(toolBar, SWT.PUSH);
		    c_itemSupprimer.setData("SUPPRIMER");
		    c_itemSupprimer.setToolTipText("Supprimer configuration");
		    c_itemSupprimer.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("supprimer_config.png").createImage());
		    c_itemSupprimer.setEnabled(false);
		    c_itemSupprimer.addSelectionListener(this);
		}
		
		/**
		 * Affectation des configurations.
		 * 
		 * @param configs configurations
		 * @return true si succès
		 */
		public boolean f_SET_CONFIGS(String[] configs)
		{
			if(configs.length == 0)
			{
				// combo vide
				c_comboConfig.setItems(new String[]{"Aucune configuration"});
			}
			else
			{
				// remplissage combo
				c_comboConfig.setItems(configs);
			}
			
			// récupération derniere config
			String lastConfig = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(m_projet.getProject().getName() + C_DefinePreferencesPlugin.CONFIG_EN_COURS);
			
			if(lastConfig != null && lastConfig.length() > 0 && Arrays.binarySearch(configs, lastConfig) >= 0)
			{
				c_compositeConfig.f_SET_SELECTED(lastConfig);
			}
			else if(configs.length > 0)
			{
				C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_STRING(m_projet.getProject().getName() + C_DefinePreferencesPlugin.CONFIG_EN_COURS, c_comboConfig.getItem(0));
				c_comboConfig.select(0);
			}
			else
			{
				C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_STRING(m_projet.getProject().getName() + C_DefinePreferencesPlugin.CONFIG_EN_COURS, "");
				c_comboConfig.select(0);
			}
			
			f_UPDATE_BOUTONS_STATE();
			
			return true; // ok
		}
		
		private void f_UPDATE_BOUTONS_STATE()
		{
			// accès interface par thread
			getDisplay().asyncExec(new Runnable() 
			{
				@Override
				public void run() 
				{
					if(c_comboConfig.getText().equalsIgnoreCase("Aucune configuration"))
					{
//						c_itemInitialiser.setEnabled(true);
//						c_itemExporter.setEnabled(false);
//						c_itemImporter.setEnabled(false);
						c_itemSupprimer.setEnabled(false);
					}
					else if(c_comboConfig.getText().equalsIgnoreCase(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG))
					{
//						c_itemInitialiser.setEnabled(false);
						
						if(m_descRelease.f_IS_ORGANISATION_INITIALE() && f_IS_SITE_SELECTIONNE()  && !c_labelLibelle.getText().equalsIgnoreCase("Pas de correspondance dans la base"))
						{
//							c_itemExporter.setEnabled(true);
//							c_itemImporter.setEnabled(true);
						}
						else
						{
//							c_itemExporter.setEnabled(false);
//							c_itemImporter.setEnabled(false);
						}
						
						c_itemSupprimer.setEnabled(!m_descRelease.f_IS_ORGANISATION_INITIALE());
					}
					else
					{
						boolean trouve = Arrays.binarySearch(c_comboConfig.getItems(), C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG) >= 0;
						
//						c_itemInitialiser.setEnabled(!trouve);
						
						if(!c_labelLibelle.getText().equalsIgnoreCase("Pas de correspondance dans la base"))
						{
//							c_itemExporter.setEnabled(true);
//							c_itemImporter.setEnabled(true);
						}
						else
						{
//							c_itemExporter.setEnabled(false);
//							c_itemImporter.setEnabled(false);
						}
						
						c_itemSupprimer.setEnabled(!m_descRelease.f_IS_ORGANISATION_INITIALE());
					}
				}
			});
		}
		
		
		/**
		 * Mise à jour des configurations.
		 * 
		 * @return true si succès
		 */
		public boolean f_UPDATE_CONFIGS()
		{
			// accès interface par thread
			getDisplay().asyncExec(new Runnable() 
			{
				@Override
				public void run() 
				{
					// sécurité aucun projet sélectionné
					if(m_projet != null)
					{
						// affectation des configs
						f_SET_CONFIGS(C_ToolsSite.f_GET_CONFIG_FROM_PROJECT(m_projet.getProject()));
					}
				}
			});
			
			return true; // ok
		}
		
		public boolean f_SET_SELECTED(final String selection)
		{
			// accès interface par thread
			getDisplay().asyncExec(new Runnable() 
			{
				@Override
				public void run() 
				{
					String[] items = c_comboConfig.getItems();
					
					for(int i = 0 ; i < items.length ; i++ )
					{
						if(items[i].equalsIgnoreCase(selection))
						{
							c_comboConfig.select(i);
						}
					}
				}
			});
			
			return true;
		}
		
		public String f_GET_SELECTED()
		{
			return c_comboConfig.getItem(c_comboConfig.getSelectionIndex());
		}
		
		@Override
		public void setEnabled(boolean enabled)
		{
			c_comboConfig.setEnabled(enabled);
			c_itemSupprimer.setEnabled(enabled);
		}

		@Override
		public void widgetSelected(SelectionEvent e)
		{
			Widget widget = (Widget) e.getSource();
			
			if(widget.getData().toString().equalsIgnoreCase("INITIALISER"))
			{
				// question
				if(C_ToolsSWT.f_AFFICHE_QUESTION(getShell(), "Initialisation configuration", "Souhaitez-vous créer la configuration par défaut de la release ?\n\n" + c_labelLibelle.getText(), "prepa_affaire.png"))
				{
					C_ActionInitialisationConfig initialisationConfig = new C_ActionInitialisationConfig();
					initialisationConfig.f_LANCEMENT(m_projet, this);
					
					f_UPDATE_CONFIGS();
				}
			}
			else if(widget.getData().toString().equalsIgnoreCase("IMPORTER"))
			{
				// question
				if(C_ToolsSWT.f_AFFICHE_QUESTION(getShell(), "Importation configuration", "Souhaitez-vous remplacer la configuration locale par la configuration du serveur ?\n\n" + c_labelLibelle.getText(), "import_config.png"))
				{
					// récupération descriptif site
					C_DescDistribution descSite = C_ToolsSite.f_CHARGEMENT_DESCRIPTIF_SITE_FROM_PROJECT(m_projet.getProject());
					
					if(descSite == null)
						C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Importer configuration", "Descriptif de site introuvable.");
					else if(!C_ToolsSite.f_GET_DOSSIER_CONFIG_SITE(m_descRelease, descSite).exists())
						C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Importer configuration", "Dossier de config introuvable sur le serveur.");
					else
						C_ToolsSite.f_COPIE_CONFIG_SITE_DANS_DOSSIER_DEVELOPPEMENT(descSite, null, this);
						
				}
			}
			else if(widget.getData().toString().equalsIgnoreCase("EXPORTER"))
			{
				// question
				if(C_ToolsSWT.f_AFFICHE_QUESTION(getShell(), "Exportation configuration", "Souhaitez-vous remplacer la configuration du serveur par la configuration locale ?\n\n" + c_labelLibelle.getText(), "export_config.png"))
				{
					// récupération descriptif site
					C_DescDistribution descSite = C_ToolsSite.f_CHARGEMENT_DESCRIPTIF_SITE_FROM_PROJECT(m_projet.getProject());
					
					if(descSite == null)
						C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Exporter configuration", "Erreur d'exportation de la configuration.");
					else if(!C_ToolsSite.f_GET_DOSSIER_CONFIG_LOCAL(m_descRelease, descSite).exists())
						C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Importer configuration", "Dossier de config introuvable dans répertoire de développement.");
					else
						C_ToolsSite.f_COPIE_CONFIG_SITE_SUR_SERVEUR(descSite, null, this);
				}
			}
			else if(widget.getData().toString().equalsIgnoreCase("SUPPRIMER"))
			{
				// question
				if(C_ToolsSWT.f_AFFICHE_QUESTION(getShell(), "Suppression configuration", "Souhaitez-vous supprimer la configuration locale ?\n\n" + c_labelLibelle.getText(), "supprimer_config.png"))
				{
					// récupération dossier configuration
					File repertoireConfig = C_ToolsSite.f_GET_REPERTOIRE_CONFIG_FROM_PROJET(m_projet.getProject(), c_comboConfig.getText());
					
					// suppression
					C_JobSuppressionRepertoire jobSuppressionConfig = new C_JobSuppressionRepertoire(repertoireConfig, false);
						jobSuppressionConfig.schedule();
						jobSuppressionConfig.addJobChangeListener(this);
				}
			}
			else if(widget.getData().toString().equalsIgnoreCase("ACTUALISER"))
			{
				// mise à jour des configurations
				f_UPDATE_CONFIGS();
			}
			else if(widget.getData().toString().equalsIgnoreCase("COMBO"))
			{
				f_UPDATE_INFORMATIONS();
				f_UPDATE_BOUTONS_STATE();
				
				// sauvegarde derniere config
				C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_STRING(m_projet.getProject().getName() + C_DefinePreferencesPlugin.CONFIG_EN_COURS, c_comboConfig.getText());
			}
		}
		
		@Override
		public void done(IJobChangeEvent arg0) 
		{
			// mise à jour de la combo configs
			f_UPDATE_CONFIGS();
			f_UPDATE_INFORMATIONS();
			f_UPDATE_BOUTONS_STATE();
		}

		// inutilisés
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {}
		@Override
		public void aboutToRun(IJobChangeEvent arg0) {}
		@Override
		public void awake(IJobChangeEvent arg0) {}
		@Override
		public void running(IJobChangeEvent arg0) {}
		@Override
		public void scheduled(IJobChangeEvent arg0) {}
		@Override
		public void sleeping(IJobChangeEvent arg0) {}
	}
}
