package org.deltadore.planet.model.base;

import java.util.ArrayList;
import java.util.Arrays;

import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;

public class C_BaseReleases 
{
	public ArrayList<C_DescRelease> 				m_releases;
	
	private boolean									m_is_serveurSVNOk;
	
	/**
	 * Retourne un descriptif de release à partir de son nom.
	 * 
	 * @param nom nom de la release
	 * @return descriptif release
	 */
	public C_DescRelease f_GET_RELEASE(String nom)
	{
		// sécurité
		if(m_releases == null)
			return null;
		
		// parcours des releases...
		for(C_DescRelease release : m_releases)
			if(release.f_GET_NOM().equalsIgnoreCase(nom))
				return release; // ok
				
		return null; // ko
	}
	
	/**
	 * Retourne un descriptif de release à partir d'un descriptif site
	 * 
	 * @param descSite descriptif site
	 * @return descriptif release
	 */
	public C_DescRelease f_GET_RELEASE(C_DescDistribution descSite)
	{
		// sécurité
		if(m_releases == null)
			return null;
		
		// parcours des releases...
		for(C_DescRelease release : m_releases)
			if(release.f_GET_VERSION_MAJEURE() == descSite.f_GET_VERSION_MAJEURE()
			&& release.f_GET_VERSION_MINEURE() == descSite.f_GET_VERSION_MINEURE())
				return release; // ok
				
		return null; // ko
	}
	
	public String[] f_GET_NOM_TAGGED_RELEASES()
	{
		ArrayList<String> resultat = new ArrayList<String>();
		
		C_DescRelease[] releases = new C_DescRelease[]{};
		releases = m_releases.toArray(releases);
		Arrays.sort(releases);
		
		for(C_DescRelease desc : releases)
		{
			if(desc.f_IS_TAGGED_RELEASE())
			{
				resultat.add(desc.f_GET_NOM());
			}
		}
		
		return resultat.toArray(new String[]{});
	}
	
	public String[] f_GET_NOM_RELEASES()
	{
		ArrayList<String> resultat = new ArrayList<String>();
		
		C_DescRelease[] releases = new C_DescRelease[]{};
		releases = m_releases.toArray(releases);
		Arrays.sort(releases);
		
		for(C_DescRelease desc : releases)
		{
			resultat.add(desc.f_GET_NOM());
		}
		
		return resultat.toArray(new String[]{});
	}
	
	public C_DescRelease[] f_GET_RELEASES()
	{
		ArrayList<C_DescRelease> resultat = new ArrayList<C_DescRelease>();
		
		C_DescRelease[] releases = new C_DescRelease[]{};
		releases = m_releases.toArray(releases);
		Arrays.sort(releases);
		
		for(C_DescRelease desc : releases)
		{
			resultat.add(desc);
		}
		
		return resultat.toArray(new C_DescRelease[]{});
	}
	
	public C_DescRelease[] f_GET_TAG_RELEASES()
	{
		// sécurité
		if(m_releases == null)
			return null;
		
		ArrayList<C_DescRelease> resultat = new ArrayList<C_DescRelease>();
		
		C_DescRelease[] releases = new C_DescRelease[]{};
		releases = m_releases.toArray(releases);
		Arrays.sort(releases);
		
		for(C_DescRelease desc : releases)
		{
			if(desc.f_IS_TAGGED_RELEASE())
				resultat.add(desc);
		}
		
		return resultat.toArray(new C_DescRelease[]{});
	}
	
	public boolean f_SET_SERVEUR_SVN_STATE(boolean state)
	{
		m_is_serveurSVNOk = state;
		
		return true;
	}
	
	public boolean f_GET_SERVEUR_SVN_STATE()
	{
		return m_is_serveurSVNOk;
	}
}
