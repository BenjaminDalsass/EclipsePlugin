package org.deltadore.planet.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.deltadore.planet.model.descriptifs.C_DescBaseSites;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

/**
 * Tools Gestion fichier XML.
 * 
 * @author ylesoeur
 * 
 */
public class C_ToolsXML
{
	
	/**
	 * Retourne racine arbre XML.
	 * 
	 * @param nomFichierXML
	 *           : Nom fichier XML
	 * 
	 * @return racine arbre XML.
	 */
	public static Element f_PARSE_DOCUMENT_XML(String nomFichierXML) throws IOException
	{
		return f_PARSE_DOCUMENT_XML(new FileInputStream(nomFichierXML));
	}
	
	public static Element f_PARSE_DOCUMENT_XML(InputStream stream) throws IOException
	{
		Document document = null;
		Element racine = null;

		SAXBuilder sxb = new SAXBuilder();

		try
		{
			document = sxb.build(stream);

			racine = document.getRootElement();
		}
		catch (JDOMException e)
		{
			System.out.println("Erreur SAXBuilder");
		}
		catch (IOException e)
		{
			System.out.println("Erreur IO Exception");

			throw (e);
		}

		return racine;
	}

	/**
	 * Retourne vrai si version fichier XSD identique au paramètre.
	 * 
	 * @param nomFichierXSD
	 *           : nom schéma XSD
	 * @param versionXSD
	 *           : version schéma XSD
	 * 
	 * @return vrai si version fichier XSD identique au paramètre.
	 */
	public static boolean f_IS_VERSION_SCHEMA_VALIDE(String nomFichierXSD, String versionXSD)
	{
		try
		{
			Element racine = f_PARSE_DOCUMENT_XML(nomFichierXSD);

			String versionFichier = racine.getAttributeValue("version");

			if (!versionFichier.equals(versionXSD))
			{
				System.out.println("Version schema (" + versionFichier + ") différente de version attendue (" + versionXSD + ")");

				return false;
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception : Erreur fichier " + nomFichierXSD + " introuvable.");
		}

		return true;
	}

	/**
	 * Retourne vrai si validation fichier XML correct
	 * 
	 * @param nomFichierXML
	 *           : fichier XML
	 * @param nomFichierXSD
	 *           : schéma XSD
	 * 
	 * @return true si success.
	 */
	public static boolean f_VALIDATION_DOCUMENT_XML(InputStream fichierXML, String nomFichierXSD)
	{
		// "http://www.w3.org/2001/XMLSchema"
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;

		SchemaFactory factory = SchemaFactory.newInstance(language);

		StreamSource ss = new StreamSource(new File(nomFichierXSD));

		try
		{
			Schema schema = factory.newSchema(ss);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(fichierXML));

			return true;
		}
		catch (SAXException e)
		{
			System.err.println("File is not valid because ");
			System.err.println(e.getMessage());
		}
		catch (IOException e)
		{
			System.err.println("Entrées/Sorties exception");
		}

		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static C_DescRelease f_CHARGER_RELEASE(InputStream is)
	{
		Element racine;
		try
		{
			racine = C_ToolsXML.f_PARSE_DOCUMENT_XML(is);
			
			// Validation document XML
			//boolean valide = C_ToolsXML.f_VALIDATION_DOCUMENT_XML(is,"../Ressources/Release.xsd");

			if (racine != null) //&& valide)
			{
				C_DescRelease release = new C_DescRelease();

				release.f_SET_VERSION_MAJEURE(Integer.parseInt(racine.getChild("RELEASE_MAJEUR").getValue()));
				release.f_SET_VERSION_MINEURE(Integer.parseInt(racine.getChild("RELEASE_MINEUR").getValue()));
				
				return release;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void f_SAUVER_DESCRIPTION_RELEASE(C_DescRelease release, OutputStream stream)
	{
		XMLOutputter out = new XMLOutputter();
        try
        {
	         out.output(f_CONVERT_DESC_RELEASE_TO_DOCUMENT(release), stream);
	         stream.flush();
	         stream.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	private static Document f_CONVERT_DESC_RELEASE_TO_DOCUMENT(C_DescRelease release)
	{
		Element rac = new Element("RELEASE");

		// Ajout release majeur
		Element releaseMaj = new Element("RELEASE_MAJEUR");
		releaseMaj.setContent(new Text(new Integer(release.f_GET_VERSION_MAJEURE()).toString()));
		rac.addContent(releaseMaj);
		
		// Ajout release mineur
		Element releaseMin = new Element("RELEASE_MINEUR");
		releaseMin.setContent(new Text(new Integer(release.f_GET_VERSION_MINEURE()).toString()));
		rac.addContent(releaseMin);
		
		Document doc = new Document(rac);
		return doc;
	}
	
	/**
	 * Sauvegarde de la description d'une distribution.
	 * 
	 * @param distribution distribution
	 * @param stream flux en sortie
	 */
	public static void f_SAUVER_DESCRIPTION_DISTRIBUTION(C_DescDistribution distribution, OutputStream stream)
	{
        try
        {
        	XMLOutputter out = new XMLOutputter();
        	Format format = Format.getPrettyFormat();
        	out.setFormat(format);
	        out.output(f_CONVERT_DESC_SITE_TO_DOCUMENT(distribution), stream);
	        stream.flush();
	        stream.close();
        }
        catch(Exception e)
        {
        	// trace
        	e.printStackTrace();
        }
	}
	
	/**
	 * Chargement du fichier descriptof site.
	 * 
	 * @param numero numéro du site
	 * @param nom nom du site
	 * @return description du site
	 */
	public static C_DescDistribution f_CHARGER_DESCRIPTION_SITE_SERVEUR(int numero, String nom, String repertoireServeur)
	{
		try
		{
			File file = C_ToolsSite.f_GET_REPERTOIRE_SITE(numero, nom, repertoireServeur);
			
			// sécurité
			if(file == null)
				return null;
			
			// récupération fichier descriptif site
			File descXML = new File(file.getAbsolutePath() + File.separator + "DescSite.xml");
			
			// sécurité
			if(!descXML.exists())
				return null;
				
			// parse le fichier descriptif
			Element element = f_PARSE_DOCUMENT_XML(new FileInputStream(descXML));
			C_DescDistribution descSite = f_CONVERT_DOCUMENT_TO_DESC_SITE(element);
	
			// affectation du répertoire serveur
			descSite.f_SET_REPERTOIRE_SERVEUR(repertoireServeur);
				
			return descSite;
		} 
		catch (FileNotFoundException e) 
		{
			// trace
			e.printStackTrace();
			
			return null; // ko
		} 
		catch (IOException e) 
		{
			// trace
			e.printStackTrace();
			
			return null; // ko
		}
	}
	
	public static C_DescDistribution f_CHARGER_DESCRIPTION_SITE_LOCAL(InputStream in)
	{
		try
		{
			Element element = f_PARSE_DOCUMENT_XML(in);
			return f_CONVERT_DOCUMENT_TO_DESC_SITE(element);
		} 
		catch (Exception e) 
		{
			// trace
			e.printStackTrace();
			
			return null; // ko
		} 
	}
	
	/**
	 * Converssion de objet de description de site passé en paramètre
	 * en document XML.
	 *  
	 * @param site description de site
	 * @return document XML
	 */
	public static Document f_CONVERT_DESC_SITE_TO_DOCUMENT(C_DescDistribution site)
	{
		Element rac = new Element("SITE");
	
		// Ajout numéro
		Element numero = new Element("NUMERO");
		numero.setContent(new Text(new Integer(site.f_GET_NUMERO_AFFAIRE()).toString()));
		rac.addContent(numero);
		
		// Ajout nom
		Element nom = new Element("NOM");
		nom.setContent(new Text(site.f_GET_NOM_AFFAIRE()));
		rac.addContent(nom);
		
		// Ajout date
		Element releaseDate = new Element("RELEASE_DATE");
		releaseDate.setContent(new Text(site.f_GET_DATE_DISTRIBUTION()));
		rac.addContent(releaseDate);
		
		// Ajout release majeur
		Element releaseMaj = new Element("RELEASE_MAJEUR");
		releaseMaj.setContent(new Text(new Integer(site.f_GET_VERSION_MAJEURE()).toString()));
		rac.addContent(releaseMaj);
		
		// Ajout release mineur
		Element releaseMin = new Element("RELEASE_MINEUR");
		releaseMin.setContent(new Text(new Integer(site.f_GET_VERSION_MINEURE()).toString()));
		rac.addContent(releaseMin);
		
		// Ajout revision
		Element revision = new Element("REVISION_SVN");
		revision.setContent(new Text(new Long(site.f_GET_NUMERO_REVISION()).toString()));
		rac.addContent(revision);
		
		Document doc = new Document(rac);
		return doc;
	}
	
	/**
	 * Converssion de l'élément XML passé en paramètre
	 * en objet de description de site.
	 * 
	 * @param elementRoot élément XML
	 * @return description de site
	 */
	public static C_DescDistribution f_CONVERT_DOCUMENT_TO_DESC_SITE(Element elementRoot)
	{
		int numero = -1;
		
		// numéro de site
		if(elementRoot.getChild("NUMERO") != null)
			numero = new Integer(elementRoot.getChildText("NUMERO")).intValue();
		
		// nom du site
		String nom = elementRoot.getChildText("NOM");
		
		// release majeure
		int majeure = new Integer(elementRoot.getChildText("RELEASE_MAJEUR")).intValue();
		
		// release mineure
		int mineure = new Integer(elementRoot.getChildText("RELEASE_MINEUR")).intValue();
		
		// révision
		long revision = new Long(elementRoot.getChildText("REVISION_SVN")).longValue();
		
		// date de release
		String date = elementRoot.getChildText("RELEASE_DATE");
	
		// création du descripteur site
		C_DescDistribution site = new C_DescDistribution();
		if(numero != -1)
			site.f_SET_NUMERO_AFFAIRE(numero);
		site.f_SET_NOM_AFFAIRE(nom);
		site.f_SET_VERSION_MAJEURE(majeure);
		site.f_SET_VERSION_MINEURE(mineure);
		site.f_SET_NUMERO_REVISION(revision);
		site.f_SET_DATE_DISTRIBUTION(date);
		
		return site;
	}
	
	/**
	 * Sauvegarde de la description de base de sites en
	 * fichier XML.
	 * 
	 * @param baseSites description de base de sites
	 * @param stream flux en sortie
	 */
	public static void f_SAUVER_DESCRIPTION_BASE_SITES(C_DescBaseSites baseSites, OutputStream stream)
	{
        try
        {
        	XMLOutputter out = new XMLOutputter();
        	out.output(f_CONVERT_DESC_BASE_SITES_TO_DOCUMENT(baseSites), stream);
	        stream.flush();
	        stream.close();
        }
        catch(Exception e)
        {
        	// trace
        	e.printStackTrace();
        }
	}
	
	/**
	 * Chargement d'un fichier XML de description de base de sites.
	 * 
	 * @param fichier fichier XML
	 * @return description de base de sites
	 */
	public static C_DescBaseSites f_CHARGER_BASE_SITES(File fichier)
	{
		try
		{
			Element element = f_PARSE_DOCUMENT_XML(new FileInputStream(fichier));
			
			// sécurité
			if(element == null)
				return null;
			
			return f_CONVERT_DOCUMENT_TO_DESC_BASE_SITES(element);
		} 
		catch (Exception e) 
		{
			// trace
			e.printStackTrace();
			
			return null; // ko
		} 
	}
	
	/**
	 * Converssion de objet de description de base de sites passé en paramètre
	 * en document XML.
	 *  
	 * @param baseSites description de base de sites
	 * @return document XML
	 */
	public static Document f_CONVERT_DESC_BASE_SITES_TO_DOCUMENT(C_DescBaseSites baseSites)
	{
		// création root élément
		Element elementRoot = new Element("BASE_SITES");

		// parcours des sites...
		for(C_DescDistribution site : baseSites.f_GET_SITES())
		{
			// Ajout site
			Element elementSite = new Element("SITE");
			
			// Ajout numéro
			Element elementSiteNumero = new Element("NUMERO");
			elementSiteNumero.setContent(new Text(new Integer(site.f_GET_NUMERO_AFFAIRE()).toString()));
			elementSite.addContent(elementSiteNumero);
			
			// Ajout release majeur
			Element releaseMaj = new Element("RELEASE_MAJEUR");
			releaseMaj.setContent(new Text(new Integer(site.f_GET_VERSION_MAJEURE()).toString()));
			elementSite.addContent(releaseMaj);
			
			// Ajout release mineur
			Element releaseMin = new Element("RELEASE_MINEUR");
			releaseMin.setContent(new Text(new Integer(site.f_GET_VERSION_MINEURE()).toString()));
			elementSite.addContent(releaseMin);
			
			// Ajout nom
			Element elementSiteNom = new Element("NOM");
			elementSiteNom.setContent(new Text(site.f_GET_NOM_AFFAIRE()));
			elementSite.addContent(elementSiteNom);
			
			// Ajout répertoire serveur
			Element elementSiteRepertoire= new Element("REPERTOIRE");
			elementSiteRepertoire.setContent(new Text(site.f_GET_REPERTOIRE_SERVEUR()));
			elementSite.addContent(elementSiteRepertoire);
			
			// ajout du site
			elementRoot.addContent(elementSite);
		}
		
		Document doc = new Document(elementRoot);
		return doc;
	}
	
	/**
	 * Converssion de l'élément XML passé en paramètre
	 * en objet de description de base de sites.
	 * 
	 * @param elementRoot élément XML
	 * @return description base des sites
	 */
	public static C_DescBaseSites f_CONVERT_DOCUMENT_TO_DESC_BASE_SITES(Element elementRoot)
	{
		// résultat
		C_DescBaseSites descBase = new C_DescBaseSites();
		
		// parcours des éléments enfants...
		for(Object o : elementRoot.getChildren())
		{
			// récupération enfant
			Element child = (Element) o;
			
			// numéro du site
			int numero = new Integer(child.getChildText("NUMERO")).intValue();
			
			// nom du site
			String nom = child.getChildText("NOM");
			
			// release majeure
			int majeure = new Integer(child.getChildText("RELEASE_MAJEUR")).intValue();
			
			// release mineure
			int mineure = new Integer(child.getChildText("RELEASE_MINEUR")).intValue();
			
			// répertoire serveur
			String repertoire = child.getChildText("REPERTOIRE");
			
			// ajout du site
			descBase.f_AJOUTER_SITE(numero, nom, majeure, mineure, repertoire);
		}
		
		return descBase;
	}
}
