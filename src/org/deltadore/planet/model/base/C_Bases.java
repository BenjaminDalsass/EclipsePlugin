package org.deltadore.planet.model.base;

import org.deltadore.planet.plugin.jobs.C_JobRecuperationSiteServeur;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

public class C_Bases 
{
	/** base des releases **/
	private static C_BaseReleases 						m_baseReleases;
	
	/** base des affaire planet **/
	private static C_BaseAffairesPlanet 				m_baseAffairesPlanet;
	
	/** base des sites **/
	private static C_BaseSites			 				m_baseSites;
	
//	/** job de récupération des affaires planet **/
//	PRIVATE STATIC C_JOBRECUPERATIONSITEPLANET			m_jobRecuperationAffairespPlanet;
	
	/** job de récupération des sites **/
	private static C_JobRecuperationSiteServeur			m_jobRecuperationSites;
	
//	/** job de récupération des releases **/
//	private static C_JobRecuperationReleasesServeur  	m_jobRecuperationReleases;
	
	/**
	 * Initialisation.
	 * 
	 */
	public static void f_INITIALISATION()
	{
		// base releases
		m_baseReleases = new C_BaseReleases();
		
		// base affaires planet
		m_baseAffairesPlanet = new C_BaseAffairesPlanet();
		
		// bases sites
		m_baseSites = new C_BaseSites();
		
		// initialisation des jobs
//		m_jobRecuperationAffairespPlanet = new C_JobRecuperationSitePlanet();
//		m_jobRecuperationReleases = new C_JobRecuperationReleasesServeur();
		m_jobRecuperationSites = new C_JobRecuperationSiteServeur();
		
//		// récupération des affaires planet
//		f_RUN_JOB_RECUPERATION_AFFAIRES_PLANET();
		
		// récupération des sites
		f_RUN_JOB_RECUPERATION_SITES();
		
//		// récupération des releases
//		f_RUN_JOB_RECUPERATION_RELEASES();
	}
	
	/**
	 * Récupération des la base des releases.
	 * 
	 * @return base des releases
	 */
	public static C_BaseReleases f_GET_BASE_RELEASES()
	{
		return m_baseReleases;
	}
	
	/**
	 * Récupération des la base des affaires planet.
	 * 
	 * @return base des affaires planet
	 */
	public static C_BaseAffairesPlanet f_GET_BASE_AFFAIRES_PLANET()
	{
		return m_baseAffairesPlanet;
	}
	
	/**
	 * Récupération des la base des sites.
	 * 
	 * @return base des sites
	 */
	public static C_BaseSites f_GET_BASE_SITES()
	{
		return m_baseSites;
	}
	
//	/**
//	 * Exécute la tâche de récupération des affaires planet.
//	 * 
//	 * @return true si succès
//	 */
//	public static boolean f_RUN_JOB_RECUPERATION_AFFAIRES_PLANET()
//	{
//		m_jobRecuperationAffairespPlanet.cancel();
//		m_jobRecuperationAffairespPlanet.schedule();
//		
//		return true; // ok
//	}
	
	/**
	 * Exécute la tâche de récupération de sites.
	 * 
	 * @return true si succès
	 */
	public static boolean f_RUN_JOB_RECUPERATION_SITES()
	{
		m_jobRecuperationSites.cancel();
		m_jobRecuperationSites.schedule();
		
		return true; // ok
	}
	
//	/**
//	 * Exécute la tâche de récupération de sites.
//	 * 
//	 * @return true si succès
//	 */
//	public static boolean f_RUN_JOB_RECUPERATION_RELEASES()
//	{
//		m_jobRecuperationReleases.cancel();
//		m_jobRecuperationReleases.schedule();
//		
//		return true; // ok
//	}
	
	/**
	 * Ecouteur des tâches.
	 * 
	 * @param jobChangeListener écouteur
	 * @return true si succès
	 */
	public static boolean f_AJOUTE_ECOUTEUR_JOB(IJobChangeListener jobChangeListener)
	{
//		m_jobRecuperationAffairespPlanet.addJobChangeListener(jobChangeListener);
//		m_jobRecuperationReleases.addJobChangeListener(jobChangeListener);
		m_jobRecuperationSites.addJobChangeListener(jobChangeListener);
		
		return true; // ok
	}
}
