package org.deltadore.planet.plugin.jobs;

import java.io.File;

import org.deltadore.planet.tools.C_ToolsFichiers;
import org.eclipse.core.runtime.IProgressMonitor;

public class C_CopieMonitor 
{
	/** moniteur d'avancement **/
	public IProgressMonitor 		m_progressMonitor;
	
	/** fichier / dossier source **/
	private File					m_fichierSource;
	
	/** nombre octets à copier **/
	private int						m_int_aCopier;
	
	/** nombre octets éffectués **/
	private int						m_int_effectue;

	/** avancement **/
	private int						m_int_avancement;
	
	/** filtres **/
	private String[]				m_str_filtres;
	
	/**
	 * Constructeur.
	 * 
	 * @param monitor moniteur
	 * @param source fichier source
	 */
	public C_CopieMonitor(IProgressMonitor monitor, File source, String... filtres)
	{
		super();
		
		// récupération paramètres
		m_progressMonitor = monitor;
		m_fichierSource = source;
		m_str_filtres = filtres;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		try
		{
			// téléchargement
			m_progressMonitor.beginTask("Copie", 100);
			
			// préparation copie...
			m_progressMonitor.subTask("Estimation...");
			
			// calcul à copier
			m_int_aCopier = C_ToolsFichiers.f_GET_TAILLE_REPERTOIRE(m_fichierSource, 0, false, true, m_str_filtres);
//			m_progressMonitor.worked(IProgressMonitor.UNKNOWN);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	public boolean f_TRAITEMENT(File fichier)
	{
		m_int_effectue += fichier.length();
		
		int avancement = (int) ((float)m_int_effectue / (float)m_int_aCopier * 100);
	
		m_progressMonitor.subTask(fichier.getName());
		m_progressMonitor.worked(avancement-m_int_avancement);
		
		m_int_avancement = avancement;
		
		return true;
	}
}
