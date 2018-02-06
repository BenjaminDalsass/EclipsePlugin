package org.deltadore.planet.plugin.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.deltadore.planet.model.define.C_DefineDateFormat;
import org.deltadore.planet.model.define.C_DefineInfosManagerPlanet;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsFichiers;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsSite;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class C_JobExportationDistribution extends Job
{
	/** Nom de la tâche **/
	public static final String  NOM = "Exportation distribution";
	
	/** Projet **/
	private IProject					m_projet;

	/** Descriptif site**/
	private C_DescDistribution			m_descSite;
	
	/** Numéro de révision **/
	private long						m_lg_revision;
	
	/** Répertoire destinataire copie **/
	private File						m_repertoireDest;
	
	/** Flag d'exportation de config **/
	private boolean						m_is_exportationConfig;
	
	/** Flag de suppression si éxistant **/
	private boolean						m_is_suppressionExistant;
	
	/** Descriptif de release **/
	private	C_DescRelease 				m_descRelease; 
	
	/**
	 * Constructeur.
	 * 
	 * @param projet projet à exporter
	 * @param descSite descriptif site
	 * @param repertoireDest dossier d'exportation
	 * @param exportationConfig flag d'exportation de la configuration
	 * @param suppressionExistant flag de suppression si dossier existant
	 */
	public C_JobExportationDistribution(IProject projet, C_DescDistribution descSite, long revision, File repertoireDest, boolean exportationConfig, boolean suppressionExistant) 
	{
		super(NOM + ": " + projet.getName());
		
		// récupération paramètre
		m_projet = projet;
		m_descSite = descSite;
		m_lg_revision = revision;
		m_repertoireDest = repertoireDest;
		m_is_exportationConfig = exportationConfig;
		m_is_suppressionExistant = suppressionExistant;
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// descriptif de release
		m_descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet.getProject());

		// icône
		setProperty(IProgressConstants.ICON_PROPERTY, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("applications.png"));
	}
	
	/**
	 * Exportation. (Anciennes générations 2.1 et 2.2)
	 * 
	 * @return true si succès
	 */
	private boolean f_EXPORTATION_ORGANISATION_INITIALE(File folder, IProgressMonitor monitor)
	{
		try
		{
			
			// filtre
			ArrayList<String> filtres = new ArrayList<String>();
			filtres.add(folder.getName()); // filtre sources
			filtres.add("src");
			filtres.add(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "*"); // autres config
			if(!m_is_exportationConfig) // filtre configuration
			{
				filtres.add(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG); // config
				filtres.add("BASE_AUTOMATISME.CFG");
				filtres.add("BASE_EBOX.CFG");
				filtres.add("BASE_NOEUD.CFG");
				filtres.add("BASE_NOEUD.EXP");
				filtres.add("BASE_PRODUIT.EXP");
				filtres.add("BASE_VARIABLE.EXP");
				filtres.add("BASE_ZONE.CFG");
				filtres.add("CYCLONE.CFG");
				filtres.add("BASE_PRODUIT.txt");
			}
			
			// suppression si éxistant
			if(m_repertoireDest.exists() && m_is_suppressionExistant)
				C_ToolsFichiers.f_SUPPRESSION(m_repertoireDest, monitor, false, false, true);
			
			// Job copie répertoire
			C_CopieMonitor copieMonitor = new C_CopieMonitor(monitor, folder, folder.getName());
			boolean result = C_ToolsFichiers.f_COPIE_REPERTOIRE(folder, m_repertoireDest, copieMonitor, false, filtres.toArray(new String[]{}));
	
			// création fichier descriptif site
			if(m_descSite != null)
			{
				m_descSite.f_UPDATE_INFORMATIONS(m_projet, m_descRelease);
				C_ToolsSite.f_SAUVEGARDE_DESCRIPTIF_DISTRIBUTION_IN_REPERTOIRE_DISTRIBUTION(m_repertoireDest, m_descSite);
			}
			else
			{
				C_DescDistribution descDistribution = new C_DescDistribution();
				descDistribution.f_SET_NOM_AFFAIRE(m_descRelease.f_GET_NOM());
				descDistribution.f_SET_DATE_DISTRIBUTION(C_DefineDateFormat.DATE_HEURE.format(new Date()));
				descDistribution.f_SET_VERSION_MAJEURE(m_descRelease.f_GET_VERSION_MAJEURE());
				descDistribution.f_SET_VERSION_MINEURE(m_descRelease.f_GET_VERSION_MINEURE());
				descDistribution.f_SET_NUMERO_REVISION(m_lg_revision);
			}
			
			// notification
			if(result)
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "La distribution " + m_projet.getProject().getName() + " à été exportée avec succès !");
			else
				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_FAIL, NOM, "La distribution n'a pas été complètement exportée !");
			
			return true; // ok
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
			
			return false; // ko
		}
	}
	
//	/**
//	 * Exportation.  (Nouvelles générations > 2.2)
//	 * 
//	 * @return true si succès
//	 */
//	private boolean f_EXPORTATION_ORGANISATION_2_3(IProgressMonitor monitor)
//	{
//		try
//		{
//			// racine de la distribution
//			File repertoire = m_projet.getLocation().toFile();
//			
//			// nom dossier
//			String nomDossier = null;
//			if(m_descSite == null)
//				nomDossier = m_descRelease.f_GET_NOM();
//			else
//				nomDossier = m_descSite.f_GET_NOM_COMPLET_AFFAIRE();
//			
//			// filtres
//			File[] files =  repertoire.listFiles(new FileFilter()
//			{
//				@Override
//				public boolean accept(File file) 
//				{
//					if(file.getName().startsWith("."))
//						return false;
//					else if(file.getName().equalsIgnoreCase(C_ToolsRelease.NOM_FICHIER_DESCRIPTIF_RELEASE))
//						return false;
//					else if(file.getName().contains(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG))
//							return false;
//					else if(file.getName().equalsIgnoreCase("src")) // sources
//						return false;
//					else
//						return true;
//				}
//			});
//			
//			m_repertoireDest = new File(m_repertoireDest, nomDossier);
//			
//			// suppression existant
//			if(m_repertoireDest.exists() && m_is_suppressionExistant)
//			{
//				if(m_is_exportationConfig)
//					C_ToolsFichiers.f_SUPPRESSION(m_repertoireDest, monitor, false, false, new String[]{});
//				else
//					C_ToolsFichiers.f_SUPPRESSION(m_repertoireDest, monitor, false, false, new String[]{C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG});
//			}
//			
//			monitor.beginTask(NOM + " " + m_projet.getName(), files.length);
//			
//			// création
//			m_repertoireDest.mkdir();
//				
//			// copie config
//			for(File f : files)
//			{
//				// 1 tache éffectuée en plus
//				monitor.worked(1);
//				
//				File dest = null;
//				if(m_descSite != null && f.getName().equalsIgnoreCase(C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + m_descSite.f_GET_NOM_COMPLET_AFFAIRE())) // configuration
//					dest = new File(m_repertoireDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
//				else
//					dest = new File(m_repertoireDest, f.getName());
//				
//				try
//				{
//					if(f.isDirectory())
//					{
//						dest.mkdir();
//						
//						// exportation SVN
//						C_ToolsSVN.f_EXPORTE(m_projet, f, dest, monitor);
//					}
//					else
//					{
//						C_ToolsSVN.f_EXPORTE(m_projet, f, dest, monitor);
//					}
//				}
//				catch(Exception e)
//				{
//					C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "SVN", e.getMessage());
//					// TODO rapport
//				}
//			}
//			
//			// copie config
//			if(m_is_exportationConfig)
//			{
//				File repSrc = null;
//				File repDest = null;
//				
//				if(m_descSite != null)
//					repSrc = new File(repertoire, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG + "_" + m_descSite.f_GET_NOM_COMPLET_AFFAIRE());
//				else
//					repSrc = new File(repertoire, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
//				
//				repDest = new File(m_repertoireDest, C_DefineInfosManagerPlanet.NOM_REPERTOIRE_CONFIG);
//				
//				if(repSrc.exists())
//					C_ToolsFichiers.f_COPIE_REPERTOIRE(repSrc, repDest, null, false, new String[]{});
//			}
//			
//			// création fichier descriptif site
//			if(m_descSite != null)
//			{
//				m_descSite.f_UPDATE_INFORMATIONS(m_projet, m_descRelease);
//				C_ToolsSite.f_SAUVEGARDE_DESCRIPTIF_DISTRIBUTION_IN_REPERTOIRE_DISTRIBUTION(m_repertoireDest, m_descSite);
//			}
//			else
//			{
//				C_DescDistribution descDistribution = new C_DescDistribution();
//				descDistribution.f_SET_NOM_AFFAIRE(m_descRelease.f_GET_NOM());
//				descDistribution.f_SET_VERSION_MAJEURE(m_descRelease.f_GET_VERSION_MAJEURE());
//				descDistribution.f_SET_VERSION_MINEURE(m_descRelease.f_GET_VERSION_MINEURE());
//				descDistribution.f_SET_NUMERO_REVISION(m_lg_revision);
//				C_ToolsSite.f_SAUVEGARDE_DESCRIPTIF_DISTRIBUTION_IN_REPERTOIRE_DISTRIBUTION(m_repertoireDest, descDistribution);
//			}
//			
//			// mise à jour moniteur
//			monitor.done();
//			
//			// notification
//			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.TRANSACTION_OK, NOM, "La distribution " + m_projet.getProject().getName() + " à été exportée avec succès !");
//			
//			return true; // ok
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			
//			return false; // ko
//		}
//	}
//	
	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		boolean result = false;
		
		if(m_descRelease.f_IS_ORGANISATION_INITIALE())
		{
			// repertoire destinataire = repertoiredest + nom du projet (site et release)
			m_repertoireDest = new File(m_repertoireDest.getAbsolutePath() + File.separator + m_projet.getProject().getName());
				
			// racine de la distribution
			File repertoire = m_projet.getLocation().toFile().getParentFile();
		
			result = f_EXPORTATION_ORGANISATION_INITIALE(repertoire, monitor);
		}
		else
		{
			// repertoire destinataire = repertoiredest + nom du projet (site et release)
			m_repertoireDest = new File(m_repertoireDest.getAbsolutePath() + File.separator + m_projet.getProject().getName());
				
			File repertoire = m_projet.getLocation().toFile();
		
			result = f_EXPORTATION_ORGANISATION_INITIALE(repertoire, monitor);
		}
		
		monitor.done();
		
		if(result)	
			return Status.OK_STATUS;
		else
			return Status.CANCEL_STATUS;
	}
}
