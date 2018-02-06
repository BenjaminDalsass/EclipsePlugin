package org.deltadore.planet.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;

import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;

public class C_Tools 
{
	public static IPackageFragment[] f_GET_FONCTIONS_ACTU()
	{
		try
		{
			String searchFonction = "fonctions.";
			
			ArrayList<IPackageFragment> vec = new ArrayList<IPackageFragment>();
			
			IJavaProject javaProject = C_ToolsWorkbench.f_GET_PROJET_JAVA_FROM_EDITOR(C_PlanetPluginActivator.f_GET().getWorkbench().getActiveWorkbenchWindow());
			
			if(javaProject == null)
				return null;
			
			IPackageFragment[] fragments = javaProject.getPackageFragments();
			
			for(int i = 0 ; i < fragments.length ; i++)
			{
				if(fragments[i].getElementName().startsWith(searchFonction)
				&& !fragments[i].getElementName().substring(searchFonction.length()).contains("."))
					vec.add(fragments[i]);
				
//				fragments[i].
			}
			
			IPackageFragment[] result = new IPackageFragment[vec.size()];
			
			return vec.toArray(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Retourne un verrou fichier.
	 * 
	 * @param repertoire chemin du verrou
	 * @return verrou
	 */
	public static FileLock f_GET_LOCK(String repertoire)
	{
		try
		{
			// fichier verrou
			File fichierlock = new File(repertoire + File.separator + "lock");
			fichierlock.deleteOnExit();
			
			// init verrou
			FileOutputStream out = new FileOutputStream(fichierlock);
		    FileLock lock = out.getChannel().lock();
		   
		    return lock; // ok
		}
		catch(Exception e)
		{
			return null; // ko
		}
	}
}
