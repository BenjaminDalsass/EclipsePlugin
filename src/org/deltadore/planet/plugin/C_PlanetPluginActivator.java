package org.deltadore.planet.plugin;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.deltadore.planet.model.base.C_Bases;
import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.model.define.C_DefineDateFormat;
import org.deltadore.planet.model.define.C_DefineImages;
import org.deltadore.planet.model.define.C_DefinePolices;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.define.C_DefineSVN;
import org.deltadore.planet.plugin.actions.compilation.C_ActionConsoleFeedback;
import org.deltadore.planet.plugin.jobs.C_JobInitialisationPerspective;
import org.deltadore.planet.swt.C_PopupNotification;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.activities.ws.WorkbenchTriggerPoints;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class C_PlanetPluginActivator extends AbstractUIPlugin implements ISelectionListener, IPerspectiveListener4, FrameworkListener
{
	/** ID **/
	public static final String PLUGIN_ID = "org.deltadore.planet";

	/** Images **/
	
	/** Singleton **/
	private static C_PlanetPluginActivator 				m_plugin;
	
	@Override
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		
		// récupération singleton
		m_plugin = this;
		
		context.addFrameworkListener(this);
		
		// initialisations
		C_DefineCouleur.f_INIT_COULEUR();
		C_DefinePolices.f_INIT_POLICES();
		C_DefineSVN.f_INIT_SVN();
		C_DefineImages.f_INIT_IMAGES();
		C_DefineDateFormat.f_INIT_DATE_FORMAT();
		
		// initialisation des bases
		C_Bases.f_INITIALISATION();
		
		// écoute les sélections
//		getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
//		
//		getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
		
//		// récupération dernier projet
//		String lastProjet = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.PROJET_EN_COURS);
//		
//		if(lastProjet != null && lastProjet.length() > 0)
//		{
//			IProject projet = ResourcesPlugin.getWorkspace().getRoot().getProject(lastProjet);
//			
//			C_ToolsWorkbench.f_SELECT(projet);
//		}
	}

	
	
	@Override
	public void stop(BundleContext context) throws Exception 
	{
		// restauration
		m_plugin = null;
		
		// restitutions
		C_DefineCouleur.f_RESTIT_COULEUR();
		C_DefinePolices.f_RESTIT_POLICES();
		C_DefineSVN.f_RESTIT_SVN();
		C_DefineImages.f_RESTIT_IMAGES();
		C_DefineDateFormat.f_RESTIT_DATE_FORMAT();
		
		super.stop(context);
	}
	
	
	
	/**
	 * Retourne le singleton
	 *
	 * @return singleton
	 */
	public static C_PlanetPluginActivator f_GET() 
	{
		return m_plugin;
	}
	
	/**
	 * Initialisation logger ANT.
	 * 
	 * @param antRunner ant runner
	 */
	public static void f_INIT_ANT_LOGGER(AntRunner antRunner)
	{
		try
		{
			// get the default custom classpath from the preferences
			AntCorePreferences corePreferences = AntCorePlugin.getPlugin().getPreferences();
			URL[] urls = corePreferences.getURLs();
			
			// get the location of the plugin jar
			File bundleFile = FileLocator.getBundleFile(m_plugin.getBundle());
			
			URL url = bundleFile.toURI().toURL();
			
			// bond urls to complete classpath
			ArrayList<URL> classpath = new ArrayList<URL>();
			
			classpath.addAll(Arrays.asList(urls));
			
			classpath.add(url);
			
			// set custom classpath
			antRunner.setCustomClasspath(classpath.toArray(new URL[classpath.size()]));
			
			antRunner.addBuildLogger("org.deltadore.planet.plugin.C_AntLogger");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart workbenchPart, ISelection selection) 
	{	
		if(workbenchPart.getSite().getId().equalsIgnoreCase(JavaUI.ID_PACKAGES))
		{
			// sécurité
			if(!(selection instanceof TreeSelection))
				return;
			
			// récupération projet
			IProject projet = C_ToolsWorkbench.f_GET_PROJET_SELECTIONNE((TreeSelection) selection);
			if(projet == null)
				return;
			
			// sauvegarde dernier projet
			C_DefinePreferencesPlugin.f_SET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.PROJET_EN_COURS, projet.getName());
		}
	}
	
	/**
	 * Désactive les boutons de feedback compilation de la vue console.
	 * 
	 */
	public static void f_RAZ_BOUTONS_FEEDBACK_FROM_CONSOLE_VIEW()
	{
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_COMPILATION_CM", C_ActionConsoleFeedback.ETAT.RAZ);
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_COMPILATION_SERVEUR", C_ActionConsoleFeedback.ETAT.RAZ);
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_COMPILATION_CYCLONE", C_ActionConsoleFeedback.ETAT.RAZ);
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_COMPILATION_SERVEUR_KNX", C_ActionConsoleFeedback.ETAT.RAZ);
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_COMPILATION_INFO_FONC", C_ActionConsoleFeedback.ETAT.RAZ);
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_CREATION_JAR", C_ActionConsoleFeedback.ETAT.RAZ);
	}
	
	@Override
	public void perspectiveActivated(IWorkbenchPage workbenchPage, IPerspectiveDescriptor perspectiveDescriptor) 
	{
		// raz boutons console
		f_RAZ_BOUTONS_FEEDBACK_FROM_CONSOLE_VIEW();
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage workbenchPage, IPerspectiveDescriptor perspectiveDescriptor, IWorkbenchPartReference partReference, String ID) 
	{
		// si affichage vue console
		if(ID.equalsIgnoreCase("viewShow") && partReference.getPartName().equalsIgnoreCase("Console"))
		{
			// raz boutons console
			f_RAZ_BOUTONS_FEEDBACK_FROM_CONSOLE_VIEW();
		}
	}
	
	@Override
	public void frameworkEvent(FrameworkEvent arg0) 
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
	
		window.getSelectionService().addSelectionListener(this);
		window.addPerspectiveListener(this);
		
		// accès interface par thread
		window.getShell().getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				f_RAZ_BOUTONS_FEEDBACK_FROM_CONSOLE_VIEW();
			}
		});
	}
	
	// inutilisés
	@Override
	public void perspectiveChanged(IWorkbenchPage workbenchPage, IPerspectiveDescriptor perspectiveDescriptor, String ID) {}
	@Override
	public void perspectivePreDeactivate(IWorkbenchPage workbenchPage, IPerspectiveDescriptor perspectiveDescriptor) {}
	@Override
	public void perspectiveClosed(IWorkbenchPage workbenchPage,	IPerspectiveDescriptor perspectiveDescriptor) {}
	@Override
	public void perspectiveDeactivated(IWorkbenchPage workbenchPage, IPerspectiveDescriptor perspectiveDescriptor) {}
	@Override
	public void perspectiveOpened(IWorkbenchPage workbenchPage,	IPerspectiveDescriptor perspectiveDescriptor) {}
	@Override
	public void perspectiveSavedAs(IWorkbenchPage workbenchPage, IPerspectiveDescriptor arg1, IPerspectiveDescriptor perspectiveDescriptor) {}
}
