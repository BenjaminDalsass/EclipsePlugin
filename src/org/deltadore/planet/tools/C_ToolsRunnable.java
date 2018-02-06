package org.deltadore.planet.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.deltadore.planet.model.applicationsPlanet.E_Applications;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.swt.E_NotificationType;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.ant.launching.IAntLaunchConstants;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jdt.launching.sourcelookup.JavaSourceLocator;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;

@SuppressWarnings("restriction")
public class C_ToolsRunnable 
{
	/**
	 * Execution de processus.
	 * 
	 * @param url url de l'éxécutable
	 * @return processus créé
	 */
	public static void f_EXECUTE(File fichier)
	{		
		try
		{
			Program.launch(fichier.getCanonicalPath());
	  	}
	  	catch(Exception e)
		{
	  		// trace
	  		e.printStackTrace();
		}
	}
	
	/**
	 * Execution de processus.
	 * 
	 * @param url url de l'éxécutable
	 * @return processus créé
	 */
	public static Process f_EXECUTE2(String url, String arguments)
	{		
		try
		{
			if(arguments == null)
				arguments = "";
			
			String command = url + " " + arguments;
			
			return Runtime.getRuntime().exec(command);
	  	}
	  	catch(Exception e)
		{
	  		// trace
	  		e.printStackTrace();
	  		
	  		return null; // ko
		}
	}
	
	/**
	 * Execution de processus.
	 * 
	 * @param url url de l'éxécutable
	 * @return processus créé
	 */
	public static Process f_EXECUTE3(String... commandAndArguments)
	{		
		try
		{
			return new ProcessBuilder(commandAndArguments).start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Lancement d'un classe main du projet voulu.
	 * 
	 * @param projet projet associé
	 * @param mainClass main classe
	 */
	public static void f_LANCEMENT_ECLIPSE2(String nomApplication, IJavaProject projet, final String mainClass, final String arguments)
	{
        try 
        {
        	ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        	ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        	
        	String nomAppli = projet.getProject().getName() + "~" + nomApplication;
        	if(arguments != null && arguments.contains("REPERTOIRE_CONFIG="))
        	{
        		nomAppli =  nomAppli.concat("~" + arguments.replace("REPERTOIRE_CONFIG=", ""));
        	}
        	
        	final ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(projet.getProject(), nomAppli);
        	 
        	
        	//----------------------------------
        	
        	// arguments
        	StringBuffer listeArguments = new StringBuffer(arguments);
        	listeArguments.append(" LAUNCH_FROM_ECLIPSE");
        	
        	// sans code accès
        	if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_SANS_CODE_ACCES))
        		listeArguments.append(" SANS_CODE_ACCES");
        	
        	// dico serveur
        	if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DICO_SERVEUR))
        		listeArguments.append(" DICTIONNAIRE_SERVEUR");
        	
        	workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
        	workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, listeArguments.toString());
        	workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projet.getProject().getName());
        		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.VM_ARGUMENTS));
        	
        	Display.getDefault().asyncExec (new Runnable() {
				
				@Override
				public void run() 
				{
					String runMode = f_GET_RUN_MODE();
					
					DebugUITools.launch(workingCopy, f_GET_RUN_MODE());
					
					if(runMode == ILaunchManager.DEBUG_MODE
					&& C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.SWITCH_DEBUG_PERSPECTIVE))
					{
						C_PlanetPluginActivator.f_GET().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(C_ToolsWorkbench.f_GET_PERSPECTIVE_DESCRIPTOR(IDebugUIConstants.ID_DEBUG_PERSPECTIVE));
					}
				}
			});
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	
        	C_ToolsSWT.f_NOTIFICATION(E_NotificationType.CONNECTED, "erreur", f_GET_STACK(e));
        }
	}
	
	private static String f_GET_RUN_MODE()
	{
    	// mode run debug
    	if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DEBUG))
    		return ILaunchManager.DEBUG_MODE;
    	else
    		return ILaunchManager.RUN_MODE;
	}
	
	private static String f_GET_STACK(Exception e)
	{
		StringBuffer b = new StringBuffer();
		
		for(StackTraceElement element : e.getStackTrace())
		{
			b.append(element.toString() + "\n\r");
		}
		
		return b.toString();
	}
	
	/**
	 * Lancement d'un classe main du projet voulu.
	 * 
	 * @param projet projet associé
	 * @param mainClass main classe
	 */
	public static void f_LANCEMENT_ECLIPSE(final IJavaProject projet, final String mainClass, final String arguments)
	{
        try 
        {
        	ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        	
           	// variables
        	String runMode;
        	
        	// mode run debug
        	if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DEBUG))
        		runMode = ILaunchManager.DEBUG_MODE;
        	else
        		runMode = ILaunchManager.RUN_MODE;
        	
        	// arguments
        	ArrayList<String> listeArguments = new ArrayList<String>(Arrays.asList(arguments));
        	listeArguments.add("LAUNCH_FROM_ECLIPSE");
        	
        	// sans code accès
        	if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_SANS_CODE_ACCES))
        		listeArguments.add("SANS_CODE_ACCES");
        	
        	// dico serveur
        	if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_DICO_SERVEUR))
        		listeArguments.add("DICTIONNAIRE_SERVEUR");
        	
		  IVMInstall vmInstall = JavaRuntime.getVMInstall(projet);
		  
		   if (vmInstall == null)
		      vmInstall = JavaRuntime.getDefaultVMInstall();
		   
		   if (vmInstall != null) 
		   {
			   if(vmInstall instanceof IVMInstall2)
			   {
				   IVMInstall2 IvmInstall = (IVMInstall2) vmInstall;
				   IvmInstall.setVMArgs(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.VM_ARGUMENTS));
			   }
			   else
			   {
				   if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.OUTIL_MEMOIRE))
					   vmInstall.setVMArguments(new String[]{C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.VM_ARGUMENTS)});
			   }
			   
		      IVMRunner vmRunner = vmInstall.getVMRunner(runMode);
		      
		      if (vmRunner != null) 
		      {
		         String[] classPath = null;
		         classPath = JavaRuntime.computeDefaultRuntimeClassPath(projet);
		         
		         if (classPath != null) 
		         {
		        	// Type lancement JAVA
		        	ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		        	
		        	// Configuration
		        	ILaunchConfiguration cfg = type.newInstance(projet.getProject(), "conf");
//					        	 ILaunch launch = DebugUITools.buildAndLaunch(type.newInstance(projet.getProject(), "Conf"), runMode, new NullProgressMonitor());
		        	
		        	// Lancement
		        	ILaunch launch = new Launch(cfg, runMode, new JavaSourceLocator(projet));
		        	
		        	// VM configuration
		            VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(mainClass, classPath);
		            vmConfig.setProgramArguments(listeArguments.toArray(new String[]{}));
		            String path = projet.getProject().getLocation().toFile().getAbsolutePath();
		            vmConfig.setWorkingDirectory(path);
		           		            
		            // VM Runner
		            vmRunner.run(vmConfig, launch, new NullProgressMonitor());
		            
		            // Ajout lancement
		            DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
		            
		            // Console
		            ConsolePlugin.getDefault().getConsoleManager().showConsoleView(DebugUITools.getConsole(launch.getProcesses()[0]));
		         }
		      }
		      
		      if(runMode == ILaunchManager.DEBUG_MODE
		    && C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.SWITCH_DEBUG_PERSPECTIVE))
		      {
		  		// accès interface par thread
		  		Display.getDefault().asyncExec(new Runnable() 
		  		{
		  			@Override
		  			public void run() 
		  			{
		  				C_PlanetPluginActivator.f_GET().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(C_ToolsWorkbench.f_GET_PERSPECTIVE_DESCRIPTOR(IDebugUIConstants.ID_DEBUG_PERSPECTIVE));
		  			}
		  		});
		      }
		   }
        }
        catch(Exception e)
        {
        	// trace
        	e.printStackTrace();
        }
		
//		try
//		{
//			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
//			
//			
//		   ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
//		   
//		   ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "SampleConfig");
//		   wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projet.getProject().getName());
//		   wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
//		   ILaunchConfiguration config = wc.doSave();	
//		   config.launch(ILaunchManager.RUN_MODE, null);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Lancement go software.
	 * 
	 * @param projet projet associé
	 * @param nomGoSoft nom go soft
	 */
	public static void f_LANCEMENT_GO_SOFT(IJavaProject projet, String nomGoSoft, String arguments)
	{
        try 
        {
    		// si debug
    		if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.DEBUG))
    			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.WARN, "Lancement", projet.getProject().getName() + " " + nomGoSoft + " " + arguments);
        	
        	// variables
        	String path = null;
        	File repertoireGoSoft = null;
        	
        	// récupération descriptof release
        	C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
        	
        	// répertoire projet
        	File repertoireProjet = projet.getProject().getLocation().toFile();
        	
        	if(descRelease.f_IS_ORGANISATION_INITIALE())
        	{
        		repertoireGoSoft = repertoireProjet.getParentFile();
        	}
        	else
        	{
        		repertoireGoSoft = repertoireProjet;
        	}
        	
        	if(new File(repertoireGoSoft.getAbsolutePath(), nomGoSoft).exists())
        	{
	        	// éxéxution
	        	path = repertoireGoSoft.getAbsolutePath() + "\\" + nomGoSoft;
	        	f_EXECUTE2(path, arguments);
        	}
        	else
        	{
        		C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "Lancement", projet.getProject().getName() + " " + nomGoSoft + " " + arguments);
        	}
        }
        catch(Exception e)
        {
        	// trace
        	e.printStackTrace();
        }
	}
	
	/**
	 * Lancement d'un application.
	 * 
	 * @param projet prjet associé
	 * @param application application à lancer
	 */
	public static void f_LANCEMENT_APPLICATION(IJavaProject projet, E_Applications application, String arguments)
	{
		// si debug
		if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.DEBUG))
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.WARN, "Lancement", projet.getProject().getName() + " " + application.toString() + " " + arguments);
		
		switch(application)
		{
			case CLIENT:
				f_LANCEMENT_ECLIPSE2("WinmanJClient", projet, "_client.application.MainJClient", arguments);
				break;
			case SERVEUR:
				f_LANCEMENT_GO_SOFT(projet,  "_Winman_serveur" + File.separator + "winman_serveur.exe", arguments);
				break;
			case CM:
				f_LANCEMENT_GO_SOFT(projet, "_Wincm" + File.separator + "Wincm.exe", arguments);
				break;
			case CM_JAVA:
				f_LANCEMENT_ECLIPSE2("WinCM", projet, "_CM.application.MainCM", arguments);
				break;
			case CYCLONE:
				f_LANCEMENT_GO_SOFT(projet, "_Winlon" + File.separator + "Cyclone" + File.separator + "cyclone.exe", arguments);
				break;
			case KNX:
				f_LANCEMENT_GO_SOFT(projet, "_Winknx" + File.separator + "Winknx.exe", arguments);
				break;
			case TRADUCTEUR:
				f_LANCEMENT_ECLIPSE2("Traducteur", projet, "_traducteur.application.MainTraducteur", arguments);
				break;
			case STUDIO:
				f_LANCEMENT_ECLIPSE2("Studio", projet,"_studio.MainStudio", arguments);
				break;
			case DICTIONNAIRE_INVERSE:
				f_LANCEMENT_ECLIPSE2("Dictionnair Inversé", projet, "_traducteur.AnnuaireInverse.MainAnnuaireInverse", arguments);
				break;
			case MOULINETTE:
				f_LANCEMENT_ECLIPSE2("Moulinette", projet, "__global.fonction.historique._moulinetteUniverselle.MainMoulinetteArch", arguments);
				break;
			case GO:
				f_LANCEMENT_ECLIPSE2("GO", projet, "_go.C_MainGo", arguments);
				break;
			case REVECTORISATEUR:
				f_LANCEMENT_ECLIPSE2("Revectorisateur", projet, "_revectorisateur.application.MainRevectorisateur", arguments);
				break;
			case INFO_FONCTION:
				f_LANCEMENT_GO_SOFT(projet, "src" + File.separator + "Tools" + File.separator + "Info_fonc" + File.separator + "Info_fonc.exe", arguments);
				break;
			case ESTAMPILLAGE_VERSION:
				f_LANCEMENT_ECLIPSE2("Estampillage Version", projet, "_go.C_MainGoModificationVersion", arguments);
				break;
			case INFO_VERSIONS:
				f_LANCEMENT_ECLIPSE2("Info versions", projet, "_go.infoVersions.C_MainInfoVersions", arguments);
				break;				
		}
	}
	
	/**
	 * Lancement d'un script ant.
	 * 
	 * @param projet projet associé
	 * @param antScript script
	 * @param monitor moniteur de progression
	 */
	public static void f_LANCEMENT_ANT_1(IJavaProject projet, String antScript, IProgressMonitor monitor)
	{
		try
		{
			// launch manager
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			
			// configuration
			ILaunchConfigurationType lcType = launchManager.getLaunchConfigurationType(IAntLaunchConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
			
			// génération nom
			String name = launchManager.generateLaunchConfigurationName("Lancement script Ant");
			
			// attributs
			ILaunchConfigurationWorkingCopy wc = lcType.newInstance(null, name);
			wc.setAttribute(ILaunchManager.ATTR_PRIVATE, true);
			wc.setAttribute(IExternalToolConstants.ATTR_LOCATION, projet.getProject().getLocation().toString() + "/src/Tools/script/" + antScript);
//			wc.setAttribute(IAntLaunchConstants.ATTR_ANT_TARGETS, "");
			
			// lancement
			ILaunch launch = wc.launch(ILaunchManager.RUN_MODE, null);

			// assure la visibilité de la console
			ConsolePlugin.getDefault().getConsoleManager().showConsoleView(DebugUITools.getConsole(launch.getProcesses()[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Lancement d'un script ant.
	 * 
	 * @param projet projet associé
	 * @param antScript script
	 * @param monitor moniteur de progression
	 */
	public static void f_LANCEMENT_ANT_2(File antScript, IProgressMonitor monitor)
	{
		try
		{
			AntRunner runner = new AntRunner();
			runner.setBuildFileLocation(antScript.getAbsolutePath());
			C_PlanetPluginActivator.f_INIT_ANT_LOGGER(runner);
			runner.run(monitor);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
