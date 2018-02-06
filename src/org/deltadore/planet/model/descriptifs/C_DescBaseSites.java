package org.deltadore.planet.model.descriptifs;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;

public class C_DescBaseSites 
{
	/** Liste des sites **/
	private ArrayList<C_DescDistribution>			m_arr_sites;
	
	/** Lock descriptif base **/
	RandomAccessFile file = null;
	FileChannel f = null;
	FileLock lock = null; 
	    
	/**
	 * Constructeur.
	 * 
	 */
	public C_DescBaseSites()
	{
		super();
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// initialisation liste
		m_arr_sites = new ArrayList<C_DescDistribution>();
	}
	
	/**
	 * Ajout d'un site à la base.
	 * 
	 * @param numSite numéro du site
	 * @param nom nom du site
	 * @param repertoireServeur répertoire conteneur sur serveur site
	 * @return true si succès
	 */
	public boolean f_AJOUTER_SITE(int numSite, String nom, int versionMajeure, int versionMineure, String repertoireServeur)
	{
		// création locale à la base
		C_DescDistribution descSite = new C_DescDistribution();
		descSite.f_SET_NUMERO_AFFAIRE(numSite);
		descSite.f_SET_NOM_AFFAIRE(nom);
		descSite.f_SET_VERSION_MAJEURE(versionMajeure);
		descSite.f_SET_VERSION_MINEURE(versionMineure);
		descSite.f_SET_REPERTOIRE_SERVEUR(repertoireServeur);
		
		// ajout
		m_arr_sites.add(descSite);
		
		return true; // ok
	}
	
	/**
	 * Retourne le descriptif site avec le numéro passé en paramètre.
	 * 
	 * @param numero numéro du site
	 * @return descriptif site
	 */
	public C_DescDistribution f_GET_SITE(int numero)
	{
		// parcours des sites...
		for(C_DescDistribution site : m_arr_sites)
		{
			// si trouvé
			if(site.f_GET_NUMERO_AFFAIRE() == numero)
				return site;
		}
		
		return null; // ko
	}
	
	/**
	 * Retourne le descriptif site avec le nom complet passé en paramètre.
	 * 
	 * @param nom complet nom complet du site
	 * @return descriptif site
	 */
	public C_DescDistribution f_GET_SITE(String nomComplet)
	{
		// parcours des sites...
		for(C_DescDistribution site : m_arr_sites)
		{
			// si trouvé
			if(site.f_GET_NOM_COMPLET_AFFAIRE().equalsIgnoreCase(nomComplet))
				return site;
		}
		
		return null; // ko
	}
	
	/**
	 * Retourne les sites de la base.
	 * 
	 * @return tableau de descriptif site
	 */
	public C_DescDistribution[] f_GET_SITES()
	{
		return m_arr_sites.toArray(new C_DescDistribution[m_arr_sites.size()]);
	}
	
	/**
	 * Retourne les sites correspondants au filtre
	 * passé en paramètre.
	 * 
	 * @param filtre filtre
	 * @return tableau de descriptif site
	 */
	public C_DescDistribution[] f_GET_SITES(String filtre)
	{
		// résultat
		ArrayList<C_DescDistribution> result = new ArrayList<C_DescDistribution>();
		
		// parcours des sites...
		for(C_DescDistribution site : m_arr_sites)
		{
			// trouvé
			if(site.f_GET_NOM_COMPLET_AFFAIRE().toLowerCase().replaceAll("_", " ").contains(filtre.replaceAll("_", " ").toLowerCase()))
				result.add(site);
		}
		
		return result.toArray( new C_DescDistribution[result.size()]); // ok
	}
	
	/**
	 * Retourne les sites correspondants au filtre
	 * passé en paramètre.
	 * 
	 * @param filtre filtre
	 * @param descRelease descriptif release
	 * @return tableau de descriptif site
	 */
	public C_DescDistribution[] f_GET_SITES(String filtre, C_DescRelease descRelease)
	{
		// résultat
		ArrayList<C_DescDistribution> result = new ArrayList<C_DescDistribution>();
		
		// parcours des sites...
		for(C_DescDistribution site : m_arr_sites)
		{
			// trouvé
			if(site.f_GET_NOM_COMPLET_AFFAIRE().toLowerCase().replaceAll("_", " ").contains(filtre.replaceAll("_", " ").toLowerCase())
			&& (descRelease == null || site.f_GET_VERSION().equalsIgnoreCase(descRelease.f_GET_VERSION())))
				result.add(site);
		}
		
		return result.toArray( new C_DescDistribution[result.size()]); // ok
	}
	
	/**
	 * Vérifie l'éxistance d'un site.
	 * 
	 * @param numero numéro du site
	 * @return true si éxiste
	 */
	public boolean f_IS_SITE_EXIST(int numero)
	{
		// parcours des sites....
		for(C_DescDistribution site : m_arr_sites)
		{
			// si trouvé
			if(site.f_GET_NUMERO_AFFAIRE() == numero)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Retourne le nombre de sites de la base.
	 * 
	 * @return nombre de sites de la base
	 */
	public int f_GET_NOMBRE_SITE()
	{
		return m_arr_sites.size();
	}
}
