package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.tools.C_ToolsDistribution;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

public class C_ActionMultipleCompilation implements IWorkbenchWindowPulldownDelegate2, SelectionListener
{
	/** Workbench window **/
	protected IWorkbenchWindow 				m_window;
	
	/** Projet en cours **/
	private IJavaProject 					m_projet;
	
	/** Actions **/
	private C_ActionCompilerCM				m_actionCompilerCM;
	private C_ActionCompilerServeur			m_actionCompilerServeur;
	private C_ActionCompilerServeurKnx		m_actionCompilerServeurKnx;
	private C_ActionCompilerCyclone			m_actionCompilerCyclone;
	private C_ActionCreationJAR				m_actionCreationJAR;
	private C_ActionCompilerInfoFonc		m_actionCompilerInfoFonc;
	
	@Override
	public Menu getMenu(Control parent)
	{
		// création menu
		Menu menu = new Menu(parent);
		
		// initioalisation menu
		f_INIT_MENU(menu);
		
		return menu;
	}
	
	@Override
	public Menu getMenu(Menu parent) 
	{
		// création menu
		Menu menu = new Menu(parent);
		
		// initioalisation menu
		f_INIT_MENU(menu);
		
		return menu;
	}
	
	/**
	 * Initialisation du menu.
	 * 
	 */
	private void f_INIT_MENU(Menu menu)
	{
		// récupération projet sélectionné
		m_projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(m_window);
		
		// ajout compilation CM
		m_actionCompilerCM = new C_ActionCompilerCM();
		f_AJOUTE_ACTION_AU_MENU(menu, m_actionCompilerCM);
		
		// ajout compilation serveur
		m_actionCompilerServeur = new C_ActionCompilerServeur();
		f_AJOUTE_ACTION_AU_MENU(menu, m_actionCompilerServeur);
		
		// ajout compilation cyclone
		m_actionCompilerCyclone = new C_ActionCompilerCyclone();
		f_AJOUTE_ACTION_AU_MENU(menu, m_actionCompilerCyclone);
		
		// ajout compilation serveur KNX
		m_actionCompilerServeurKnx = new C_ActionCompilerServeurKnx();
		f_AJOUTE_ACTION_AU_MENU(menu, m_actionCompilerServeurKnx);
		
		// ajout compilation info fonc
		m_actionCompilerInfoFonc = new C_ActionCompilerInfoFonc();
		f_AJOUTE_ACTION_AU_MENU(menu, m_actionCompilerInfoFonc);
		
		// ajout création jar
		m_actionCreationJAR = new C_ActionCreationJAR();
		f_AJOUTE_ACTION_AU_MENU(menu, m_actionCreationJAR);
		
		// separateur
		new MenuItem(menu, SWT.SEPARATOR);
		
		// compilation groupée
		f_INIT_MENU_COMPILE_GROUPEE(menu);
		
		// sécurité
		if(m_projet != null)
		{
			// récupération descriptif release
			C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
			
			// sécurité
			if(descRelease != null)
			{
				// test état bouton compilation info fonc
				if(descRelease.f_IS_ORGANISATION_AVANT_3_0())
					m_actionCompilerInfoFonc.setEnabled(true);
				else
					m_actionCompilerInfoFonc.setEnabled(false);
				
				// test état bouton compilation CM
				if(!descRelease.f_IS_ORGANISATION_AVANT_3_0())
					m_actionCompilerCM.setEnabled(false);
				else
					m_actionCompilerCM.setEnabled(true);
			}
		}
	}
	
	private void f_INIT_MENU_COMPILE_GROUPEE(Menu menu)
	{
		// compilation groupée
		MenuItem itemGroupee = new MenuItem(menu, SWT.CHECK);
		itemGroupee.setText("Compilation multiple");
		itemGroupee.setImage(C_ToolsSWT.f_GET_IMAGE("gear.png"));
		itemGroupee.addSelectionListener(this);
		itemGroupee.setData("LOT");
		
		// création menu
		MenuItem menuSelectWhat = new MenuItem(menu, SWT.CASCADE);
		menuSelectWhat.setImage(C_ToolsSWT.f_GET_IMAGE("gear_edit.png"));
		menuSelectWhat.setText("Configuration...");
		
		Menu subMenu = new Menu(menu);
		menuSelectWhat.setMenu(subMenu);
		
		MenuItem itemCM = new MenuItem(subMenu, SWT.CHECK);
		itemCM.setSelection(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CM));
		itemCM.addSelectionListener(this);
		itemCM.setData(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CM);
		itemCM.setText("CM");
		
		MenuItem itemServeur = new MenuItem(subMenu, SWT.CHECK);
		itemServeur.setSelection(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR));
		itemServeur.addSelectionListener(this);
		itemServeur.setData(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR);
		itemServeur.setText("Serveur");
		
		MenuItem itemCyclone = new MenuItem(subMenu, SWT.CHECK);
		itemCyclone.setSelection(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CYCLONE));
		itemCyclone.addSelectionListener(this);
		itemCyclone.setData(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CYCLONE);
		itemCyclone.setText("Cyclone");
		
		MenuItem itemKnx = new MenuItem(subMenu, SWT.CHECK);
		itemKnx.setSelection(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR_KNX));
		itemKnx.addSelectionListener(this);
		itemKnx.setData(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR_KNX);
		itemKnx.setText("Serveur Konnex");
		
		MenuItem itemInfoFonc = new MenuItem(subMenu, SWT.CHECK);
		itemInfoFonc.setSelection(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_INFO_FONC));
		itemInfoFonc.addSelectionListener(this);
		itemInfoFonc.setData(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_INFO_FONC);
		itemInfoFonc.setText("Info Fonc");
		
		MenuItem itemJAR = new MenuItem(subMenu, SWT.CHECK);
		itemJAR.setSelection(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_JAR));
		itemJAR.addSelectionListener(this);
		itemJAR.setData(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_JAR);
		itemJAR.setText("JAR");
		
		// separateur
		new MenuItem(menu, SWT.SEPARATOR);
		
		// compilation complète
		MenuItem itemTout = new MenuItem(menu, SWT.CHECK);
		itemTout.setText("Tout compiler");
		itemTout.setImage(C_ToolsSWT.f_GET_IMAGE("gearAll.png"));
		itemTout.addSelectionListener(this);
		itemTout.setData("ALL");
	}
	
	/**
	 * Ajout d'une action au menu.
	 * 
	 * @param menumenu auquel ajouter l'action
	 * @param action action à ajouter
	 */
	private void f_AJOUTE_ACTION_AU_MENU(Menu menu, IAction action)
	{
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(menu, -1);
	}
	
	/**
	 * Lancement de l'action.
	 * 
	 */
	public void f_LANCEMENT()
	{
		// préparation de la distribution
		C_ToolsDistribution.f_PREPARATION_DISTRIBUTION(m_projet, false);
	}
	
	@Override
	public void init(IWorkbenchWindow window)
	{
		this.m_window = window;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		// récupération projet sélectionné
		m_projet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(m_window);
		
		if(m_projet == null)
			action.setEnabled(false);
		else if(!m_projet.getProject().isOpen())
			action.setEnabled(false);
		else
		{
			// récupération descriptif release
			C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());
			
			if(descRelease == null)
				action.setEnabled(false);
			else
				action.setEnabled(true);
		}
	}
		
	@Override
	public void run(IAction action) 
	{
		if(m_projet == null)
		{
			C_ToolsSWT.f_AFFICHE_MESSAGE_ERREUR(m_window.getShell(), "Erreur de lancement", "Aucun projet n'est sélectionné");
			return;
		}
		
		f_LANCEMENT();
	}	
	
	@Override
	public void dispose() 
	{
		m_window = null;
	}

	@Override
	public void widgetSelected(SelectionEvent selectionEvent)
	{
		MenuItem menuItem = (MenuItem) selectionEvent.getSource();
		
		if(menuItem.getData().toString().equalsIgnoreCase("LOT"))
			C_ToolsDistribution.f_PREPARATION_DISTRIBUTION(m_projet, false);
		else if(menuItem.getData().toString().equalsIgnoreCase("ALL"))
			C_ToolsDistribution.f_PREPARATION_DISTRIBUTION(m_projet, true);		
		else
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_BOOLEAN(menuItem.getData().toString(), menuItem.getSelection());
	}
	
	// inutilisés
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}
}
