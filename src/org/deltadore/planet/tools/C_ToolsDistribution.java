package org.deltadore.planet.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.actions.compilation.C_ActionConsoleFeedback;
import org.deltadore.planet.plugin.actions.compilation.C_ActionConsoleFeedback.ETAT;
import org.deltadore.planet.plugin.jobs.C_JobExecutionAnt;
import org.deltadore.planet.plugin.jobs.C_JobProcessExecution;
import org.deltadore.planet.swt.E_NotificationType;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;

public class C_ToolsDistribution
{
	private static boolean			FILTRE_INFORMATIONS = true;
	
	public static void f_PREPARATION_DISTRIBUTION(IJavaProject projet, boolean all)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet.getProject());
		
		// compilation CM
		if((all || C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CM)) && descRelease.f_IS_ORGANISATION_AVANT_3_0())
			f_COMPILATION_CM();
		
		// compilation serveur
		if(all || C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR))
			f_COMPILATION_SERVEUR();
		
		// compilation Cyclone
		if(all || C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_CYCLONE))
			f_COMPILATION_CYCLONE();
		
		// compilation Serveur KNX
		if(all || C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_SERVEUR_KNX))
			f_COMPILATION_KNX();
		
		// compilation info fonc
		if((all || C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_INFO_FONC)) && descRelease.f_IS_ORGANISATION_AVANT_3_0())
			f_COMPILATION_INFO_FONC();
		
		// création jar
		if(all || C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.COMPILATION_GROUPEE_JAR))
			f_CREATION_JAR();
	}
	
	public static void f_COMPILATION_CM()
	{
		IJavaProject javaProjet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW());
		f_COMPILATION("Compilation WinCM", f_GET_FILE_PROJET_BORLAND_CM_OF_PROJECT(javaProjet.getProject()), C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_cm.png"), "FEEDBACK_COMPILATION_CM");
	}
	
	public static void f_COMPILATION_SERVEUR()
	{
		IJavaProject javaProjet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW());
		f_COMPILATION("Compilation WinmanServeur", f_GET_FILE_PROJET_BORLAND_SERVEUR_OF_PROJECT(javaProjet.getProject()), C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_serveur.png"), "FEEDBACK_COMPILATION_SERVEUR");
	}
	
	public static void f_COMPILATION_CYCLONE()
	{
		IJavaProject javaProjet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW());
		f_COMPILATION("Compilation Cyclone", f_GET_FILE_PROJET_BORLAND_CYCLONE_OF_PROJECT(javaProjet.getProject()), C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_cyclone.png"), "FEEDBACK_COMPILATION_CYCLONE");
	}
	
	public static void f_COMPILATION_KNX()
	{
		IJavaProject javaProjet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW());
		f_COMPILATION("Compilation Serveur KNX", f_GET_FILE_PROJET_BORLAND_SERVEUR_KNX_OF_PROJECT(javaProjet.getProject()), C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_knx.png"), "FEEDBACK_COMPILATION_SERVEUR_KNX");
	}
	
	public static void f_CREATION_JAR()
	{
		// feedback
		C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_CREATION_JAR", ETAT.RUN);
		
		// lancement ant
		IJavaProject javaProjet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW());
		C_JobExecutionAnt jobExecutionAnt = new C_JobExecutionAnt(javaProjet, "Création du JAR des applications java", "creation_JClient.xml");
		
		// écoute la tâche
		jobExecutionAnt.addJobChangeListener(new IJobChangeListener() 
		{
			@Override
			public void done(IJobChangeEvent arg0) 
			{
				// accès interface par thread
				Display.getDefault().asyncExec(new Runnable() 
				{
					@Override
					public void run() 
					{
						// feedback
						C_ActionConsoleFeedback.f_UPDATE_BOUTON("FEEDBACK_CREATION_JAR", ETAT.OK);
					}
				});
			}
			
			// inutilisés
			@Override
			public void sleeping(IJobChangeEvent arg0) {}
			@Override
			public void scheduled(IJobChangeEvent arg0) {}
			@Override
			public void running(IJobChangeEvent arg0) {}
			@Override
			public void awake(IJobChangeEvent arg0) {}
			@Override
			public void aboutToRun(IJobChangeEvent arg0) {}
		});
		
		// schedule tâche
		jobExecutionAnt.schedule();
	}
	
	public static void f_COMPILATION_INFO_FONC()
	{
		IJavaProject javaProjet = C_ToolsWorkbench.f_GET_PROJET_PLANET_SELECTIONNE(C_ToolsWorkbench.f_GET_ACTIVE_WORBENCH_WINDOW());
		f_COMPILATION("Compilation Info Fonc", f_GET_FILE_PROJET_BORLAND_INFO_FONC_OF_PROJECT(javaProjet.getProject()), C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("compil_infoFonc.png"), "FEEDBACK_COMPILATION_INFO_FONC");
	}
	
	/**
	 * Compilation Borland.
	 * 
	 * @param nom nom de la compilation
	 * @param projet projet associé
	 * @param imageDesc image
	 */
	private static void f_COMPILATION(String nom, File projet, ImageDescriptor imageDesc, String actionID)
	{
		try
		{
			// feedback
			C_ActionConsoleFeedback.f_UPDATE_BOUTON(actionID, ETAT.RUN);
			
			// job de compilation
			C_JobProcessExecution job = new C_JobProcessExecution(nom, imageDesc, C_DefineInfosManagerPlanet.PATH_BORLAND_BCW_EXE, projet.getAbsolutePath(), "-b", "-i");
			
			// nom du fichier message
			String nomFichierMessage = projet.getAbsolutePath().substring(0, projet.getAbsolutePath().length()-3)+"msg";
			
			// monitoring
			job.addJobChangeListener(new C_ProcessListener(nom, nomFichierMessage, imageDesc, actionID));
			job.schedule();
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
		}
	}
	
	public static File f_GET_FILE_PROJET_BORLAND_CM_OF_PROJECT(IProject projet)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		// racine projet
		File fileSrc = projet.getLocation().toFile();
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return new File(fileSrc.getAbsolutePath() + File.separator + "Wincm_src" + File.separator + "wincm.ide");
		else
			return new File(fileSrc.getAbsolutePath() + File.separator + "src" + File.separator + "Wincm_src" + File.separator + "wincm.ide");
	}
	
	public static File f_GET_FILE_PROJET_BORLAND_SERVEUR_OF_PROJECT(IProject projet)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		// racine projet
		File fileSrc = projet.getLocation().toFile();
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return new File(fileSrc.getAbsolutePath() + File.separator + "Winman_serveur_src" + File.separator + "winman_serveur.ide");
		else
			return new File(fileSrc.getAbsolutePath() + File.separator + "src" + File.separator + "Winman_serveur_src" + File.separator + "winman_serveur.ide");
	}
	
	public static File f_GET_FILE_PROJET_BORLAND_SERVEUR_KNX_OF_PROJECT(IProject projet)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		// racine projet
		File fileSrc = projet.getLocation().toFile();
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return new File(fileSrc.getAbsolutePath() + File.separator + "Winknx_src" + File.separator + "winknx.ide");
		else
			return new File(fileSrc.getAbsolutePath() + File.separator + "src" + File.separator + "Winknx_src" + File.separator + "winknx.ide");
	}
	
	public static File f_GET_FILE_PROJET_BORLAND_CYCLONE_OF_PROJECT(IProject projet)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		// racine projet
		File fileSrc = projet.getLocation().toFile();
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return new File(fileSrc.getAbsolutePath() + File.separator + "Winlon_src" + File.separator + "Cyclone_src" + File.separator + "cyclone.ide");
		else
			return new File(fileSrc.getAbsolutePath() + File.separator + "src" + File.separator + "Winlon_src" + File.separator + "Cyclone_src" + File.separator + "cyclone.ide");
	}
	
	public static File f_GET_FILE_PROJET_WINDEV_CME_OF_PROJECT(IProject projet)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// racine affaire
			File fileRoot = projet.getLocation().toFile().getParentFile();
			
			return new File(fileRoot.getAbsolutePath() + File.separator + "_Wincm" + File.separator + "Ecrans" + File.separator + "WINCM.WDP");
		}
		else
		{
			// racine affaire
			File fileRoot = projet.getLocation().toFile();
			
			return new File(fileRoot.getAbsolutePath() + File.separator + "_Wincm" + File.separator + "Ecrans" + File.separator + "WINCM.WDP");
		}
	}
	
	public static File f_GET_FILE_PROJET_BORLAND_INFO_FONC_OF_PROJECT(IProject projet)
	{
		// récupération descriptif release
		C_DescRelease descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(projet);
		
		// racine projet
		File fileSrc = projet.getLocation().toFile();
		
		if(descRelease.f_IS_ORGANISATION_INITIALE())
			return null;
		else
			return new File(fileSrc.getAbsolutePath() + File.separator + "src" + File.separator + "Info_fonc_src" + File.separator + "Info_fonc.ide");
	}
	
	private static class C_ProcessListener implements IJobChangeListener
	{
		private static Color		FATAL = new Color(C_ToolsWorkbench.f_GET_SHELL().getDisplay(), 255, 0, 255);
		private static Color		ERROR = new Color(C_ToolsWorkbench.f_GET_SHELL().getDisplay(), 255, 0, 0);
		private static Color		WARN = new Color(C_ToolsWorkbench.f_GET_SHELL().getDisplay(), 255, 125, 0);
		private static Color		INFO = new Color(C_ToolsWorkbench.f_GET_SHELL().getDisplay(), 0, 0, 255);
		
		private String 				m_str_nom;
		private String 				m_str_nomFichierMessage;
		private ImageDescriptor		m_imageDesc;
		private int					m_int_compteurwarning;
		private int					m_int_compteurFatal;
		private int					m_int_compteurError;
		private String				m_str_actionID;
		
		/**
		 * Constructeur.
		 * 
		 * @param nomFichierMsg
		 */
		public C_ProcessListener(String nom, String nomFichierMsg, ImageDescriptor imageDesc, String actionID)
		{
			super();
			
			// récupération paramètres
			m_str_nom = nom;
			m_str_nomFichierMessage = nomFichierMsg;
			m_imageDesc = imageDesc;
			m_str_actionID = actionID;
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			m_int_compteurError = 0;
			m_int_compteurwarning = 0;
			m_int_compteurFatal = 0;
		}
		
		@Override
		public void done(IJobChangeEvent arg0) 
		{
			// accès interface par thread
			ConsolePlugin.getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() 
			{
				@Override
				public void run() 
				{
					f_CONSOLE();
				}
			});
		}
		
		private void f_CONSOLE()
		{
			try
			{
				MessageConsole consoleTrouvee = (MessageConsole) C_ToolsWorkbench.f_FIND_CONSOLE(m_str_nom);
				
				if(consoleTrouvee == null)
				{
					consoleTrouvee = new MessageConsole(m_str_nom, m_imageDesc);
					ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ consoleTrouvee });
				}
				
				final MessageConsole c = consoleTrouvee;
				
				consoleTrouvee.getDocument().addDocumentListener(new IDocumentListener() {
					
					@Override
					public void documentChanged(DocumentEvent arg0) 
					{
						if(arg0.fModificationStamp > 1
						&& c.getDocument().getLength() == 0)
						{
							C_ActionConsoleFeedback.f_UPDATE_BOUTON(m_str_actionID, ETAT.RAZ);
							ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[]{c});
						}
					}
					
					@Override
					public void documentAboutToBeChanged(DocumentEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				consoleTrouvee.addPropertyChangeListener(new IPropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
//						System.out.println(arg0.getProperty());
						
					}
				});
				
//				consoleTrouvee.clearConsole();
				
				C_ToolsWorkbench.f_SHOW_CONSOLE(consoleTrouvee);
				
				FileReader reader = new FileReader(m_str_nomFichierMessage);
				BufferedReader buff = new BufferedReader(reader);
				
				String line = null;
				
				do
				{
					line = buff.readLine();
					
					IOConsoleOutputStream out = consoleTrouvee.newOutputStream();
					
					if(line != null)
					{
						if(line.startsWith("Fatal"))
						{
							m_int_compteurFatal++;
							out.setColor(FATAL);
							out.write(line+"\n");
						}
						else if(line.startsWith("Error"))
						{
							m_int_compteurError++;
							out.setColor(ERROR);
							out.write(line+"\n");
						}
						else if(line.startsWith("Warn"))
						{
							m_int_compteurwarning++;
							out.setColor(WARN);
							out.write(line+"\n");
						}
						else if(line.startsWith("Info"))
						{
							if(!FILTRE_INFORMATIONS)
							{
								out.setColor(INFO);
								out.write(line+"\n");
							}
						}
					}
					
					out.close();
				}
				while(line != null);
				
				IOConsoleOutputStream out = consoleTrouvee.newOutputStream();
				out.write("\n");
				out.write("-------------------------------------------------");
				out.write("\n");
				out.close();
				
				out = consoleTrouvee.newOutputStream();
				out.write("Fatals: " + m_int_compteurFatal);
				out.write("\n");
				out.close();
				
				out = consoleTrouvee.newOutputStream();
				out.write("Errors: " + m_int_compteurError);
				out.write("\n");
				out.close();
				
				out = consoleTrouvee.newOutputStream();
				out.write("Warnings: " + m_int_compteurwarning);
				out.write("\n");
				out.close();
				
				out = consoleTrouvee.newOutputStream();
				out.setColor(null);
				out.write("-------------------------------------------------");
				out.close();
				
				// feedback
				if(m_int_compteurFatal > 0)
					C_ActionConsoleFeedback.f_UPDATE_BOUTON(m_str_actionID, ETAT.FATAL);
				else if(m_int_compteurError > 0)
					C_ActionConsoleFeedback.f_UPDATE_BOUTON(m_str_actionID, ETAT.ERROR);
				else if(m_int_compteurwarning > 0)
					C_ActionConsoleFeedback.f_UPDATE_BOUTON(m_str_actionID, ETAT.WARNING);
				else
					C_ActionConsoleFeedback.f_UPDATE_BOUTON(m_str_actionID, ETAT.OK);
			} 
			catch(Exception e)
			{
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.CONNECTION_FAILED, "Erreur de compilation", e.getMessage());
				e.printStackTrace();
			}
		}
		
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
