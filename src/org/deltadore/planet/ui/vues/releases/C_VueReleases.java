package org.deltadore.planet.ui.vues.releases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefinePolices;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.define.C_DefineRelease;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.swt.C_FormTextContent;
import org.deltadore.planet.swt.C_LabelIconeEtTexte;
import org.deltadore.planet.swt.C_TextSearch;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNRevision;


public class C_VueReleases extends ViewPart implements ModifyListener, IJobChangeListener, IHyperlinkListener, SelectionListener, IPartListener2
{
	public static final String ID = "VUE.RELEASES";

	/** Toolkit **/
	private FormToolkit 				c_toolkit;
	
	/** TextField de recherche **/
	private C_TextSearch				c_searchText;
	
	/** scrollable **/
	private ScrolledComposite			c_scollForm;
	
	/** contenu scroll **/
	private Composite 					c_content; 
	
	/** Ecouteur **/
	private C_MyListener				m_listener;
	
	/** Zones déroulantes **/
	private ExpandableComposite[] 		c_expandables;
	
	/** Action déroule **/
	private C_ActionDeroule				m_actionDeroule;
	

	/**
	 * Constructeur.
	 * 
	 */
	public C_VueReleases() 
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
		// job
		C_Bases.f_AJOUTE_ECOUTEUR_JOB(this);
		
		// écouteur
		m_listener = new C_MyListener();
	}
	
	@Override
	public void createPartControl(Composite parent) 
	{
		// initalisation barre de boutons
		f_INIT_BARRE_BOUTONS();
		
		// création du kit
		c_toolkit = new FormToolkit(parent.getDisplay()); 
		
		// parent layout
		parent.setLayout(new GridLayout());
		parent.setBackground(C_DefineCouleur.VIOLET);
		
		// création scroll composite
		c_scollForm = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		c_scollForm.setExpandHorizontal(true);
		c_scollForm.getVerticalBar().setIncrement(50);
		c_scollForm.getVerticalBar().setPageIncrement(100);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_scollForm, 1, 1, true, true, GridData.FILL, GridData.FILL);
		
		// création contenu du scroll
		c_content = new Composite(c_scollForm, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.verticalSpacing = 1;
		c_content.setLayout(layout);
		c_scollForm.setContent(c_content);
		
		// text
		c_searchText = new C_TextSearch(parent);
		c_searchText.addModifyListener(this);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_searchText, 1, 1, true, false, GridData.FILL, GridData.FILL);
		
		// mise à jour interface
		f_UPDATE_UI();
		
		// ecoute le focus
		getViewSite().getPage().addPartListener(this);
	}
	
	/**
	 * Mise à jour interface.
	 * 
	 */
	private void f_UPDATE_UI()
	{
		// accès interface par thread
		c_content.getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// raz des anciens contrôleurs
				C_ToolsSWT.f_RAZ_CONTROLS(c_content);
				
				if(C_Bases.f_GET_BASE_RELEASES().m_releases == null)
				{
					if(!C_Bases.f_GET_BASE_RELEASES().f_GET_SERVEUR_SVN_STATE())
					{
						// message
						C_LabelIconeEtTexte compositeMessage = new C_LabelIconeEtTexte(c_content, false);
						compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("message_warning.png"), "Serveur SVN non disponible");
						C_ToolsSWT.f_GRIDLAYOUT_DATA(compositeMessage, 1, 1, true, true, GridData.FILL, GridData.FILL);
						c_scollForm.setExpandVertical(true);
					}
					else if(!C_ToolsSVN.f_IS_REPOSITORY_REFERENCE_EXISTE())
					{
						// message
						C_LabelIconeEtTexte compositeMessage = new C_LabelIconeEtTexte(c_content, false);
						compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("message_error.png"), "Entrée SVN \\Reference introuvable");
						C_ToolsSWT.f_GRIDLAYOUT_DATA(compositeMessage, 1, 1, true, true, GridData.FILL, GridData.FILL);
						c_scollForm.setExpandVertical(true);
					}
					else
					{
						// message
						C_LabelIconeEtTexte compositeMessage = new C_LabelIconeEtTexte(c_content, false);
						compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("nav_refresh_blue.png"), "Initialisation...");
						C_ToolsSWT.f_GRIDLAYOUT_DATA(compositeMessage, 1, 1, true, true, GridData.FILL, GridData.FILL);
						c_scollForm.setExpandVertical(true);
					}
				}
				else
				{
					c_expandables = new ExpandableComposite[C_Bases.f_GET_BASE_RELEASES().m_releases.size()];
					int i = 0;
					
					// parcours des références...
					for(C_DescRelease release : C_Bases.f_GET_BASE_RELEASES().f_GET_RELEASES())
					{
						// si correspond 
						if(release.f_MATCH(c_searchText.f_GET_SEARCH_TEXTE()))
						{
							// création étiquette référence
							Composite c = f_GET_LABEL_RELEASE(i, release);
							C_ToolsSWT.f_GRIDLAYOUT_DATA(c, 1, 1, true, false, GridData.FILL, GridData.FILL);
							
							i++;
						}
					}
					
					c_scollForm.setExpandVertical(false);
				}
				
				// mise à jour du scroll
				f_UPDATE_SCROLL_CONTENT();
			}
		});
	}
	
	/**
	 * Miuse à jour du contenu du scroll.
	 * 
	 */
	private void f_UPDATE_SCROLL_CONTENT()
	{
		Point size = c_content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_content.setSize(size);
	}
	
	/**
	 * Initialisation de la barre de boutons.
	 * 
	 */
	private void f_INIT_BARRE_BOUTONS()
	{
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		toolBar.add(new C_ActionRefresh());
		
		m_actionDeroule = new C_ActionDeroule();
		toolBar.add(m_actionDeroule);
	}
	
	/**
	 * Retourne l'étiquette de la référence passée en paramètre.
	 * 
	 * @param toolkit boite à outils
	 * @param release entrée référence du repository
	 * @return étiquette référence
	 */
	private Composite f_GET_LABEL_RELEASE(int i, C_DescRelease release)
	{
		// variables
		GridData data = null;
		
		// panel
		Composite composite = c_toolkit.createComposite(c_content, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));
		composite.setBackground(new Color(c_scollForm.getDisplay(), 245, 240, 255));
		composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// label image
		Image imageRelease = null;
		if(release.f_IS_TAGGED_RELEASE())
			imageRelease = new Image(c_scollForm.getDisplay(), C_PlanetPluginActivator.class.getResourceAsStream("/icons/jar_bean_new_p.png"));
		else
			imageRelease = new Image(c_scollForm.getDisplay(), C_PlanetPluginActivator.class.getResourceAsStream("/icons/jar_edit_p.png"));
		
		Label label = new Label(composite, SWT.NONE);
		label.setImage(imageRelease);
		data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		label.setLayoutData(data);
		
		// texte
		FormText text = new FormText(composite, SWT.NONE);
		if(release.f_IS_TAGGED_RELEASE())
			text.setText("<form><p><b>Manager Planet " + release.f_GET_VERSION_COURT() + "</b><br/></p></form>", true, false);
		else
			text.setText("<form><p><b>Manager Planet " + " " + C_DefineRelease.VERSION_BETA + "</b><br/></p></form>", true, false);
		data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);
		
		// toolbar
		ToolBar toolBar = new ToolBar(composite,  SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		toolBar.setLayoutData(data);
		
		// bouton history
		ToolItem itemHistory = new ToolItem(toolBar, SWT.PUSH);
		itemHistory.setData(release);
		itemHistory.setToolTipText("Affiche l'historique SVN de la release");
		itemHistory.setText("Historique");
		itemHistory.setImage(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("note_find.png").createImage());
		itemHistory.addSelectionListener(this);
		
		// Planete
		String planete = C_DefineInfosManagerPlanet.f_GET_PLANET(release);
		if(planete != null)
		{
			Label labelPlanet = new Label(composite, SWT.NONE);
			labelPlanet.setFont(C_DefinePolices.ARIAL_10_BOLD);
			labelPlanet.setForeground(C_DefineCouleur.VIOLET_ROUGE);
			labelPlanet.setText(planete);
			
			data = new GridData();
			data.grabExcessHorizontalSpace = true;
			data.horizontalSpan = 3;
			labelPlanet.setLayoutData(data);
		}
		
		// expandable area
		c_expandables[i] = new ExpandableComposite(composite, ExpandableComposite.TREE_NODE|ExpandableComposite.CLIENT_INDENT);
		c_expandables[i].setText("Les dernières modifications...");
		FormText expandedText = new FormText(c_expandables[i], SWT.WRAP);
		expandedText.addHyperlinkListener(this);
		expandedText.setColor("color", new Color(c_scollForm.getDisplay(), 125, 125, 255));
		expandedText.setImage("image", C_ToolsSWT.f_GET_IMAGE("debug_ok.png"));
		expandedText.setData(release);
		c_expandables[i].setClient(expandedText);
		c_expandables[i].addExpansionListener(m_listener);
		c_expandables[i].setData(expandedText);
		data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 3;
		c_expandables[i].setLayoutData(data);
		
		return composite;
	}
	
	/**
	 * Retourne les informations du dossier distant.
	 * 
	 * @param release dossier distant
	 * @return informations associées
	 */
	private String f_GET_INFOS_REVISION(FormText formText, C_DescRelease release)
	{
		if(release != null)
		{
			String userPref = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.UTILISATEUR);
			
			DateFormat format = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
			
			RemoteFolder remoteFolder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release);
			
			String user = null;
			if(m_actionDeroule.isChecked())
				user = userPref;
			
			ISVNLogMessage[] logMessages = C_ToolsSVN.f_GET_HISTORIQUE(remoteFolder, C_ToolsSVN.f_LONG_TO_SVN_REVISION(0), SVNRevision.HEAD, 50, user);
			
			C_FormTextContent text = new C_FormTextContent(formText);
			
			text.f_BEGIN();
			
			for(int i = 0 ; i < logMessages.length ; i++)
			{
				if(logMessages[i].getAuthor().contains(userPref))
					text.f_AJOUTE_DUDE("<b color=\"VIOLET\">{" + logMessages[i].getRevision() + "} " + format.format(logMessages[i].getDate()) + "</b> (modifié par " + logMessages[i].getAuthor() + ")");
				else
					text.f_AJOUTE_CARRE_VIOLET("<b color=\"VIOLET\">{" + logMessages[i].getRevision() + "} " + format.format(logMessages[i].getDate()) + "</b> (modifié par " + logMessages[i].getAuthor() + ")");
				text.f_AJOUTE_TEXTE(logMessages[i].getMessage());
			}
			
			text.f_END();
			
			return text.toString();
		}
		else return "?";
	}
	
	@Override
	public void setFocus() 
	{
		
	}
	
	@Override
	public void modifyText(ModifyEvent e) 
	{
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	@Override
	public void done(IJobChangeEvent event) 
	{
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	// inutilisés
	@Override
	public void aboutToRun(IJobChangeEvent event) {}
	@Override
	public void awake(IJobChangeEvent event) {}
	@Override
	public void running(IJobChangeEvent event) {}
	@Override
	public void scheduled(IJobChangeEvent event) {}
	@Override
	public void sleeping(IJobChangeEvent event) {}

	private class C_MyListener extends ExpansionAdapter
	{
		public void expansionStateChanged(ExpansionEvent e) 
		{
			super.expansionStateChanged(e);
			
			if(e.getState())
			{
				ExpandableComposite expandable = (ExpandableComposite) e.getSource();
				final FormText formText = (FormText) expandable.getData();
				final C_DescRelease descRelease = (C_DescRelease) formText.getData();
				formText.setText("chargement en cours...", false, false);
				
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						final String text = f_GET_INFOS_REVISION(formText, descRelease);
						
						// accès interface par thread
						c_content.getDisplay().asyncExec(new Runnable() 
						{
							@Override
							public void run() 
							{
								try
								{
									String textPurge = text.replaceAll("&", "&amp;");
									
									while(textPurge.contains("MANTIS #"))
									{
										int indexDebutIni = textPurge.indexOf("MANTIS #");
										int indexDebut = textPurge.indexOf("MANTIS #") + 8;
										int indexFin = textPurge.indexOf(" ", indexDebut);
										String textOriginal = textPurge.substring(indexDebutIni, indexFin+1);
										String textSubstitution = "<p color=\"color\"><img href=\"image\"/> <b>" + textPurge.substring(indexDebut, indexFin)  + "</b></p>"; 
										textPurge = textPurge.replace(textOriginal, textSubstitution);
									}
									
									
//									textPurge = textPurge.replaceAll(">", "");
//									textPurge = textPurge.replaceAll("<", "");
									
									if(textPurge.contains("http"))
									{
										int indexDebut = textPurge.indexOf("http");
										
										int indexFin = textPurge.length() - 1;
										int indexFinEspace = textPurge.indexOf(" ", indexDebut);
										int indexFinBalise = textPurge.indexOf("<", indexDebut);
										
										if(indexFinBalise >= 0 && indexFinBalise < indexFinEspace)
											indexFin = indexFinBalise;
										
										
										if(indexFinEspace >= 0 && indexFinEspace < indexFinBalise)
											indexFin = indexFinBalise;
										
										String text = textPurge.substring(indexDebut, indexFin);
										String replace = String.format("<a href=\"%s\">%s</a>", text, text);
//										String replace = String.format("%s", text, text);
										
										C_ToolsSWT.f_NOTIFICATION(E_NotificationType.CONNECTION_FAILED, "Texte", replace);
										
										textPurge = textPurge.replace(text, replace);
									}
									
//									formText.setText(textPurge, true, true);
									formText.setText(textPurge, true, true);
										
								}
								catch(Exception e)
								{
//									formText.setText(text ,false, false);
									
									C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Erreur parsing", e.getMessage());
									
								}
								// mise à jour du scroll
								f_UPDATE_SCROLL_CONTENT();
							}
						});
					}
				}).start();
			}
			
			// mise à jour du scroll
			f_UPDATE_SCROLL_CONTENT();
		}
	}
	
	private class C_ActionRefresh extends Action
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionRefresh()
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
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("refresh_pp.png"));
		}
		
		@Override
		public void run() 
		{
			// mise à jour
//			C_Bases.f_RUN_JOB_RECUPERATION_RELEASES();
		}
	}
	
	private class C_ActionDeroule extends Action
	{
		/**
		 * Constructeur.
		 * 
		 */
		public C_ActionDeroule()
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
			setChecked(false); // devient toggle après appel ? cette m?thode
			setImageDescriptor(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("dude3.png"));
		}
		
		@Override
		public void run() 
		{
		}
	}

	@Override
	public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent linkEvent) 
	{
		C_ToolsWorkbench.f_OPEN_NAVIGATEUR("Navigateur", linkEvent.getHref().toString());
	}

	@Override
	public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetSelected(SelectionEvent selectionEvent) 
	{
		Item item = (Item) selectionEvent.getSource();
		
		C_DescRelease release = (C_DescRelease) item.getData();
		
		if(item.getText().equalsIgnoreCase("Historique"))
		{
			C_ToolsSVN.f_SHOW_HISTORY(release);
		}
	}
	
	@Override
	public void partActivated(IWorkbenchPartReference arg0) 
	{
		//c_searchText.setFocus();
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partClosed(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partHidden(IWorkbenchPartReference arg0) 
	{
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference arg0)
	{
	}

	@Override
	public void partOpened(IWorkbenchPartReference arg0)
	{
		c_searchText.setFocus();
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) 
	{
	}
}