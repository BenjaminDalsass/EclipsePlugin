package org.deltadore.planet.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;

public class C_MainExtractJarPatch 
{
	public static void main(String[] args) 
	{
		try
		{
			// fichier auto extractible
			File fichierJar = new File("PlanetPatch.jar");
			
			// input stream
			JarInputStream input = new JarInputStream(new FileInputStream(fichierJar));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
