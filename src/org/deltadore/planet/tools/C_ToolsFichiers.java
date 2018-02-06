package org.deltadore.planet.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.deltadore.planet.plugin.jobs.C_CopieMonitor;
import org.eclipse.core.runtime.IProgressMonitor;

public class C_ToolsFichiers 
{
	/**
	 * Retourne vrai si le répertoire éxiste.
	 * 
	 * @param chemin chemin du répertoire
	 * @return true si éxistant
	 */
	public static boolean f_EXISTE(String chemin)
	{
		return new File(chemin).exists();
	}
	
	/**
	 * Retournbe un répertoire ou un fichier du chemin passé en paramètre.
	 * 
	 * @param chemin chemin du répertoire ou du fichier
	 * @return répertoire
	 */
	public static File f_GET(String chemin)
	{
		return new File(chemin);
	}
	
	public static boolean f_IS_DOSSIER_CONFIG(File dossier)
	{
		if(dossier.isDirectory() && new File(dossier, "Modules\\WINMODUL.CFG").exists())
		{
			return true; // ok
		}
		else return false; // ko
	}
	
	public static boolean f_IS_FILTRE(File element, String... filtres)
	{
		// parcours des filtres...
		for(String filtre : filtres)
		{
			if(filtre.endsWith("*"))
			{
				String f = filtre.substring(0, filtre.length()-1).toLowerCase();
				
				if(!element.getName().equalsIgnoreCase(f) && element.getName().toLowerCase().startsWith(f))
					return true;
			}
			else
			{
				if(element.getName().equalsIgnoreCase(filtre))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Copie d'un répertoire.
	 * 
	 * @param sourceLocation location source
	 * @param targetLocation location destinataire
	 * @param moniteur progress monitor
	 * @param recursif flag de récusivité
	 * @param filtres liste des filtres
	 * @throws IOException exceptions levées
	 */
	public static boolean f_COPIE_REPERTOIRE(File sourceLocation , File targetLocation, C_CopieMonitor moniteur,  boolean recursif, String... filtres) throws IOException
	{
		// si annulé
		if(moniteur != null && moniteur.m_progressMonitor.isCanceled())
			return false;
		
		// ignorés
		if(!recursif || ( !f_IS_FILTRE(sourceLocation, filtres) && !sourceLocation.getName().startsWith(".")))
		{
			// copie répertoire fils
	        if (sourceLocation.isDirectory()) 
	        {
	        	// création répertoire si inexistant
	            if (!targetLocation.exists()) 
	                targetLocation.mkdir();
	            
	            // récupération date de modification
	            targetLocation.setLastModified(sourceLocation.lastModified());
	            
	            String[] children = sourceLocation.list();
	            for (int i=0; i<children.length; i++) 
	            {
	            	// appel récursif
	            	boolean result = f_COPIE_REPERTOIRE(new File(sourceLocation, children[i]), new File(targetLocation, children[i]), moniteur, true, filtres);
	            	if(!result)
	            		return false;
	            }
	        } 
	        else 
	        {
	        	// création des flux
	            InputStream in = new FileInputStream(sourceLocation);
	            OutputStream out = new FileOutputStream(targetLocation);
	            
	            // copie du contenu
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) 
	            {
	                out.write(buf, 0, len);
	            }
	            
	            // monitoring
	            if(moniteur != null)
	            	moniteur.f_TRAITEMENT(sourceLocation);
	            
	            // fermeture des flux
	            in.close();
	            out.close();
	            
	            // récupération date de modification
	            targetLocation.setLastModified(sourceLocation.lastModified());
	        }
		}
		
		return true;
    }
	
	/**
	 * Calcul taille d'un répertoire.
	 * 
	 * @param sourceLocation location source
	 * @param taille résultat
	 * @param recursif flag de récusivité
	 * @param filtres liste des filtres
	 * @throws IOException exceptions levées
	 */
	public static int f_GET_TAILLE_REPERTOIRE(File sourceLocation, int taille, boolean recursif, boolean suppressionDotRep, String... filtres) throws IOException
	{
		// ignorés
		if(!recursif || (!f_IS_FILTRE(sourceLocation, filtres) && (suppressionDotRep &&!sourceLocation.getName().startsWith("."))))
		{
			// copie répertoire fils
	        if (sourceLocation.isDirectory()) 
	        {
	            String[] children = sourceLocation.list();
	            for (int i=0; i<children.length; i++) 
	            {
	            	// appel récursif
	            	taille = f_GET_TAILLE_REPERTOIRE(new File(sourceLocation, children[i]), taille, true, suppressionDotRep, filtres);
	            }
	        } 
	        else
	        {
	        	taille += sourceLocation.length();
	        }
	        
	        return taille;
		}
		else return 0;
	}
	
    /**
     * Suppression d'un fichier ou d'un répertoire.
     * 
     * @param repertoireOuFichier répertoire ou fichier
     * @param progress monitor
     * @return true si succès
     */
    public static boolean f_SUPPRESSION(File repertoireOuFichier, IProgressMonitor monitor, boolean ifFolderKeepParent, boolean recursif, boolean suppressionDotRep, String... filtres) 
    {
    	boolean result = true;
    	
		// ignorés
		if(!recursif || (!f_IS_FILTRE(repertoireOuFichier, filtres) && (suppressionDotRep && !repertoireOuFichier.getName().startsWith("."))) )
		{
	    	// si répertoire
	        if(repertoireOuFichier.isDirectory()) 
	        {
	        	// liste des enfants
	            String[] children = repertoireOuFichier.list();
	            
	            // mise à jour moniteur
	            if(!recursif)
	            	monitor.beginTask("Suppression", children.length);
	            
	            // parcours des enfants...
	            for (int i=0; i<children.length; i++) 
	            {
	                // mise à jour moniteur
	                if(!recursif)
	                	monitor.subTask(children[i]);
	                
	            	// appel récusrsif
	                boolean resultat = f_SUPPRESSION(new File(repertoireOuFichier, children[i]), monitor, ifFolderKeepParent, true, suppressionDotRep, filtres);
	                
	                // mise à jour moniteur
	                if(!recursif)
	                	monitor.worked(1);
	                
	                if (!resultat) 
	                {
	                    return false; // ko
	                }
	            }
	        }
	    
	        // suppression dossier parent
	        if(recursif)
	        	result = repertoireOuFichier.delete();
	        else if(!ifFolderKeepParent)
	        	result = repertoireOuFichier.delete();
		}
		
        // mise à jour moniteur
        if(!recursif)
        	monitor.done();
        
        return result;
    }
}
