package org.deltadore.planet.plugin.logger;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.tools.ant.BuildEvent;
import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class C_AntLogger extends org.apache.tools.ant.DefaultLogger 
{
	private IOConsole m_console;
	
	private IOConsoleOutputStream m_outputStream;

	public C_AntLogger() 
	{
		super();
	}
	
	private void f_INITIALISE_CONSOLE(String fichier)
	{
		// cherche la console
		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		if(consoles != null)
		{
			for(IConsole console : consoles)
			{
				if(console.getName().equalsIgnoreCase(fichier))
				{
					m_console = (IOConsole) console;
					m_console.clearConsole();
					break;
				}
			}
		}
		
		if(m_console == null)
		{
			m_console = new IOConsole(fichier, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("ant.png"));
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ m_console });
		}
		
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(m_console);
	}
	
	private void f_OUTPUT(final Color couleur, final String texte)
	{
		// accès interface par thread
		Display.getDefault().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
			    try 
			    {
			    	if(m_console == null)
			    	{
			    		if(texte.contains("Buildfile"))
			    		{
				    		String nom = texte.substring(texte.lastIndexOf("\\")+1, texte.lastIndexOf("."));
				    		f_INITIALISE_CONSOLE(nom);
			    		}
			    			
			    		return;
			    	}
			    	
			    	m_outputStream = m_console.newOutputStream();
			    	
			    	if(texte.contains("BUILD SUCCESSFUL"))
			    		m_outputStream.setColor(C_DefineCouleur.CONSOLE_VERT);
			    	else if(couleur != null)
			    		m_outputStream.setColor(couleur);
			    	m_outputStream.write(texte);
			    } 
			    catch (IOException e) {
			        e.printStackTrace();
			    }
			}
		});
	}

	@Override
	public void buildStarted(BuildEvent event) 
	{
//		Project p = (Project) event.getSource();
		
//		f_INITIALISE_CONSOLE("d");
//		f_OUTPUT(C_DefineCouleur.CONSOLE_VERT, p. + "\n");
	}



	@Override
	protected void printMessage(String message, PrintStream stream, int priority) 
	{
		f_OUTPUT(null, message + "\n");
	}

	@Override
	public void targetFinished(BuildEvent event) 
	{
//		f_OUTPUT(C_DefineCouleur.CONSOLE_BLEU, "\n");
	}

	@Override
	public void targetStarted(BuildEvent event)
	{
		f_OUTPUT(C_DefineCouleur.CONSOLE_BLEU, "{" + event.getTarget().getName() + "}\n");
	}

	@Override
	public void taskFinished(BuildEvent event) 
	{
//		f_OUTPUT(C_DefineCouleur.CONSOLE_ROUGE, "Fin " + event.getTask().getTaskName() + "\n");
	}

	@Override
	public void taskStarted(BuildEvent event) 
	{
//		f_OUTPUT(C_DefineCouleur.CONSOLE_ROUGE, "Début " + event.getTask().getTaskName() + "\n");
	}
	

}
