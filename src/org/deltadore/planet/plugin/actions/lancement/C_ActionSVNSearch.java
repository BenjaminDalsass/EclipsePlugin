package org.deltadore.planet.plugin.actions.lancement;

import org.deltadore.planet.tools.C_ToolsSVN;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNRevision;

public class C_ActionSVNSearch implements IWorkbenchWindowActionDelegate 
{
	/** Fenêtre **/
	private IWorkbenchWindow		m_window;
	
	/** Repository **/
	private String						m_str_repository;
	
	/** Recherche **/
	private String						m_str_search;
	
	private Button 					c_btnRecherche;
	
	public void init(IWorkbenchWindow window)
	{
		// récupération fenêtre
		m_window = window;
	}
	
	@Override
	public void run(IAction action) 
	{
		// affichage des résultats
		f_AFFICHE_RESULTAT();
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		action.setEnabled(true);
	}

	@Override
	public void dispose() 
	{
	}
	
	private void f_AFFICHE_RESULTAT()
	{
		Shell shell = new Shell(SWT.SHELL_TRIM & (~SWT.RESIZE));
		shell.setSize(506, 630);
		shell.setLayout(new FillLayout());
		shell.setText("SVN recherche");
		shell.setImage(C_ToolsSWT.f_GET_IMAGE("note_find.png"));
		
		// création du kit
		FormToolkit c_toolkit = new FormToolkit(m_window.getShell().getDisplay()); 
		
		// panel
		Composite composite = new Composite(shell, SWT.FILL);
		GridLayout grid = new GridLayout(1, false);
		grid.marginHeight = 0;
		grid.marginWidth = 0;
		composite.setLayout(grid);
		composite.setBackground(new Color(m_window.getShell().getDisplay(), 245, 240, 255));
		composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// icone
		Label l = new Label(composite, SWT.NONE);
		l.setImage(C_ToolsSWT.f_GET_IMAGE("svn_search.png"));
		
		// panel
		Composite compositeContenu = new Composite(composite, SWT.FILL);
		compositeContenu.setLayout(new GridLayout(1, false));
		compositeContenu.setBackground(new Color(m_window.getShell().getDisplay(), 245, 240, 255));
		compositeContenu.setBackgroundMode(SWT.INHERIT_DEFAULT);
		GridData d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.verticalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		d.grabExcessVerticalSpace = true;
		compositeContenu.setLayoutData(d);
		
		// combo version
		final Combo c = new Combo(compositeContenu, SWT.BORDER | SWT.READ_ONLY);
		c.add("ManagerPlanet_2_6");
		c.add("ManagerPlanet_2_7");
		c.select(0);
		GridData datac = new GridData();
		datac.horizontalAlignment = SWT.FILL;
		datac.grabExcessHorizontalSpace = true;
		c.setLayoutData(datac);
		
		// recherche
		final Text champSearch = new Text(compositeContenu, SWT.BORDER);
		GridData datar = new GridData();
		datar.horizontalAlignment = SWT.FILL;
		datar.grabExcessHorizontalSpace = true;
		champSearch.setLayoutData(datar);
		
		// texte
		final Text text = new Text(compositeContenu, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setSize(350, 800);
		text.setEditable(false);
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		text.setLayoutData(data);
		
		c_btnRecherche = new Button(compositeContenu, SWT.NONE);
		c_btnRecherche.setText("Lancer la recherche");
		GridData data2 = new GridData();
		data2.horizontalAlignment = SWT.FILL;
		data2.verticalAlignment = SWT.FILL;
		data2.grabExcessHorizontalSpace = true;
		c_btnRecherche.setLayoutData(data2);
		c_btnRecherche.addMouseListener(new MouseListener()
			{
				
				@Override
				public void mouseUp(MouseEvent e)
				{
				}
				
				@Override
				public void mouseDown(MouseEvent e)
				{
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							// affichage message chargement
							m_window.getShell().getDisplay().asyncExec(new Runnable() 
							{
								@Override
								public void run() 
								{
									// label chargement
									text.setText("Chargement des révisions...");
									c_btnRecherche.setEnabled(false);
									
									// récupération des entrées du formulaire
									m_str_repository = c.getItem(c.getSelectionIndex());
									m_str_search = champSearch.getText();
									
																		
									new Thread(new Runnable()
										{
											@Override
											public void run()
											{
												// récupération des entrées SVN
												final String contenu = f_GET_LOG(text, m_str_repository, m_str_search);
																			
												// affichage du resultat
												m_window.getShell().getDisplay().asyncExec(new Runnable() 
												{
													@Override
													public void run() 
													{
														try
														{
															// affichage logs
															text.setText(contenu);
														}
														catch(Exception e)
														{
															// label erreur
															text.setText("erreur");
														}
														
														c_btnRecherche.setEnabled(true);
													}
												});	
											}
										}).start();
								}
							});
						}
					}).start();
				}
				
				@Override
				public void mouseDoubleClick(MouseEvent e)
				{
				}
			});
		
		shell.setVisible(true);
	}

	private String f_GET_LOG(Text formText, String release, String search)
	{
		// récupération dossier repository
		RemoteFolder remoteFolder = C_ToolsSVN.f_GET_REMOTE_FOLDER_REFERENCE(release + "/trunk");
		
		// récupération all historiques
		ISVNLogMessage[] logMessages = C_ToolsSVN.f_GET_HISTORIQUE(remoteFolder, C_ToolsSVN.f_LONG_TO_SVN_REVISION(0), SVNRevision.HEAD, 0, null);
		
		// résultat
		StringBuffer resultat = new StringBuffer();
		
		if(logMessages.length == 0)
			return "Aucune entrée dans les logs SVN";
		
		// parcours des logs....
		for(int i = 0 ; i < logMessages.length ; i++)
		{
			// si log corresponda à recherche
			if(search == null || search.length() == 0 || logMessages[i].getMessage().toLowerCase().contains(search.toLowerCase()))
			{
				resultat.append(logMessages[i].getRevision() + " : " + logMessages[i].getMessage() + "\n\r");
			}
		}
		
		return resultat.toString();
	}
}
