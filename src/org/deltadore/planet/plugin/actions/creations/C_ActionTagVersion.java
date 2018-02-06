package org.deltadore.planet.plugin.actions.creations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.swt.C_VerificateurSaisie;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.IProgressConstants;

public class C_ActionTagVersion extends C_ActionProjetPlanetAbstraite 
{
	ArrayList<String> 			m_str_fichiers;
	HashMap<String, String>     m_mapReplacement;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_ActionTagVersion() 
	{
		super("Tagger une version");
		
		// pas de gestion ancienne organisation
		m_is_gestionAnciennOrganisation = false;
	}
	
	/**
	 * Initialisation.
	 *
	 */
	private void f_INIT_TASK(IJavaProject projet, int versionMajeur, int versionMineure)
	{
		m_str_fichiers = new ArrayList<String>();
		m_mapReplacement = new HashMap<String, String>();
		
		// fichiers
		m_str_fichiers.add("/src/_Global_src/$g_project_version.h");
		m_str_fichiers.add("/src/java/_client/application/C_VersionClient.java");
		m_str_fichiers.add("Release.xml");
		m_str_fichiers.add(".project");
		
		// $g_project_version.h
		m_mapReplacement.put("#define DD_VERSION_MAJOR", "\t#define DD_VERSION_MAJOR " + versionMajeur);
		m_mapReplacement.put("#define DD_VERSION_MINOR", "\t#define DD_VERSION_MINOR " + versionMineure);
		m_mapReplacement.put("#define DD_VERSION_STRING", "\t#define DD_VERSION_STRING " + versionMajeur + "." + versionMineure + ".0.0");
		
		// C_VersionClient.java
		m_mapReplacement.put("public static final int\t\t\tVERSION_MAJEUR", "public static final int\t\t\tVERSION_MAJEUR = " + versionMajeur + ";");
		m_mapReplacement.put("public static final int\t\t\tVERSION_MINEUR", "public static final int\t\t\tVERSION_MINEUR = " + versionMineure + ";");
		
		// Release.xml
		m_mapReplacement.put("<RELEASE_MAJEUR>", "<RELEASE_MAJEUR>" + versionMajeur + "</RELEASE_MAJEUR>");
		m_mapReplacement.put("<RELEASE_MINEUR>", "<RELEASE_MINEUR>" + versionMineure + "</RELEASE_MINEUR>");
		
		// .project
		m_mapReplacement.put("<name>ManagerPlanet", "<name>ManagerPlanet_" + versionMajeur + "_" + versionMineure + "</name>");
	}

	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		f_AFFICHE_FENETRE_SAISIE_FONCTION(projet);
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("certificate_new.png");
	}
	
	/**
	 * Affichage d'une fenêtre de saisie des paramètres.
	 * 
	 * @param projet projet associé
	 * @return true si succès
	 */
	private boolean f_AFFICHE_FENETRE_SAISIE_FONCTION(final IJavaProject projet)
	{
		final Shell shell = new Shell(m_window.getShell(), SWT.TITLE | SWT.APPLICATION_MODAL | SWT.CLOSE);
		shell.setSize(400, 300) ;
		shell.setText("Tagger une version de Manager Planet");
		
		// layout
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);
		
		// Version Majeure
		Label labelNumeroFonction = new Label(shell, SWT.NONE);
		labelNumeroFonction.setText("Version Majeur:");
		final Text c_fieldMajeur = new Text(shell, SWT.BORDER);
		c_fieldMajeur.addVerifyListener(new C_VerificateurSaisie("[0-9]{0,3}"));
		c_fieldMajeur.forceFocus();
		
		//Version Mineure
		Label labelNomSite = new Label(shell, SWT.NONE);
		labelNomSite.setText("Version Mineure:");
		final Text c_fieldMineure = new Text(shell, SWT.BORDER);
		C_VerificateurSaisie verificateur = new C_VerificateurSaisie("[0-9]{0,3}");
		c_fieldMineure.addVerifyListener(verificateur);
		
		// bouton
		Button btnValider = new Button(shell, SWT.NONE);
		btnValider.setText("Valider");
		btnValider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.setVisible(false);
				
				if(c_fieldMajeur.getText().length() > 0
				&& c_fieldMineure.getText().length() > 0)
				{
					Integer versionMajeure = new Integer(c_fieldMajeur.getText());
					Integer versionMineure = new Integer(c_fieldMineure.getText());
					
					C_Job job = new C_Job(projet, versionMajeure, versionMineure);
					job.schedule();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		
		shell.setVisible(true);
		
		   while (!shell.isDisposed()) {
			      if (!m_window.getShell().getDisplay().readAndDispatch()) {
			        // If no more entries in event queue
			    	  m_window.getShell().getDisplay().sleep();
			      }
			    }
		   
		
		return true;
	}
	
	private class C_Job extends Job
	{
		/** Projet associé **/
		private IJavaProject 			m_projet;
		
		/** Version majeure **/
		private int 					m_int_versionMajeur;
		
		/** Version mineure **/
		private int	 					m_int_versionMineure;

		/**
		 * Constructeur.
		 * 
		 * @param name no
		 * @param projet
		 * @param versionMajeur
		 * @param versionMineure
		 */
		public C_Job(IJavaProject projet, int versionMajeur, int versionMineure) 
		{
			super("Tagger une version");
			
			// récupération des paramètres
			m_projet = projet;
			m_int_versionMajeur = versionMajeur;
			m_int_versionMineure = versionMineure;
			
			// initialisation
			f_INIT();
		}
		
		/**
		 * Initialisation.
		 * 
		 */
		private void f_INIT()
		{
			setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("certificate_new.png"));
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			try
			{
				// variables
				String line = null;
				boolean traite = false;
				
				// moniteur
				monitor.beginTask("Initialisation...", 0);
				
				// initialisation séquence
				f_INIT_TASK(m_projet, m_int_versionMajeur, m_int_versionMineure);
				
				// moniteur
				monitor.beginTask("Traitement...", m_str_fichiers.size());
				
				// parcours des fichiers à traiter...
				for(String nomFichier : m_str_fichiers)
				{
					// récupération projet
					IFile fichier = m_projet.getProject().getFile(nomFichier);
					
					// moniteur
					monitor.subTask(fichier.getName());
					
					// temporaire
					File temp = File.createTempFile(fichier.getName(), null);
					
					// reader
					InputStream in = fichier.getContents();
					InputStreamReader inr = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(inr);
					
					// writer (fichier temporaire)
					FileOutputStream out = new FileOutputStream(temp);
					OutputStreamWriter outw = new OutputStreamWriter(out);
					BufferedWriter bw = new BufferedWriter(outw);
					
					do
					{
						// lecture d'une ligne
						line = br.readLine();
						
						// sécurité
						if(line != null)
						{
							traite = false;
							
							// parcours des entrées à remplacer...
							for(Entry<String, String> entree : m_mapReplacement.entrySet())
							{
								// si entrée trouvée
								if(line.contains(entree.getKey()))
								{
									// on remplace
									bw.write(entree.getValue());
									traite = true;
									break;
								}
							}
							
							// si pas de traitement particulier
							if(!traite)
								// on remet la ligne intacte
								bw.write(line);
							
							// fin de ligne
							bw.newLine();
						}
					}
					while(line != null);
					
					// fermeture des flux
					bw.close();
					in.close();
					out.close();
					
					// mise à jour du contenu du fichier
					fichier.setContents(new FileInputStream(temp), true, true, new NullProgressMonitor());
					
					// mise à jour moniteur
					monitor.worked(1);
				}
				
				return Status.OK_STATUS; // ok
			}
			catch(Exception e)
			{
				// trace
				e.printStackTrace();
				
				return Status.CANCEL_STATUS; // ko
			}
		}
		
	}
}
