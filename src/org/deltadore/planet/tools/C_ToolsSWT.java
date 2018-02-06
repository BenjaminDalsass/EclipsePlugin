package org.deltadore.planet.tools;

import java.net.URL;

import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.swt.C_PopupNotification;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

public class C_ToolsSWT 
{
	/**
	 * RAZ des contrôleurs.
	 * 
	 * @param composite composite à nettoyer
	 */
	public static void f_RAZ_CONTROLS(Composite composite)
	{
		// sécurité
		if(composite.isDisposed())
			return;
		
		// récupération des controleurs
		Control[] ctrl = composite.getChildren();
		
		// sécurité
		if(ctrl != null)
		{
			// parcours des contrôleurs...
			for(Control c : ctrl)
			{
				if(c instanceof Label)
				{
					Label l = (Label) c;
					l.getImage().dispose();
				}
				
				c.dispose();
			}
		}
	}
	
	/**
	 * Centre la fenêtre.
	 * 
	 */
	public static void f_CENTER_SHELL(Shell shell)
	{
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		    
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		
		shell.setLocation(x, y);
	}
	
	/**
	 * Affectation d'un GridData au control passé en paramètres.
	 * 
	 * @param composite control SWT
	 * @param largeur nombre de cellules en largeur
	 * @param hauteur nombre de cellules en hauteur
	 * @param grabHorizontal récupérer largeur disponible
	 * @param graVertical récupérer hauteur disponible
	 * @param horizontalAlign alignement horizontal (et FILL)
	 * @param verticalAlign alignement vertical (et FILL)
	 * @return true si succès
	 */
	public static boolean f_GRIDLAYOUT_DATA(Control composite, int largeur, int hauteur, boolean grabHorizontal, boolean graVertical, int horizontalAlign, int verticalAlign, int largeurMini, int HauteurMini)
	{
		// création grid data
		GridData data = new GridData();
		data.horizontalSpan = largeur;
		data.verticalSpan = hauteur;		
		data.grabExcessHorizontalSpace = grabHorizontal;
		data.grabExcessVerticalSpace = graVertical;
		data.horizontalAlignment = horizontalAlign;
		data.verticalAlignment = verticalAlign;
		
		if(largeurMini > 0)
			data.minimumWidth = largeurMini;
		
		if(HauteurMini > 0)
			data.minimumHeight = HauteurMini;
		
		// affectation grid data
		composite.setLayoutData(data);
		
		return true; // ok
	}
	
	/**
	 * Affectation d'un GridData au control passé en paramètres.
	 * 
	 * @param composite control SWT
	 * @param largeur nombre de cellules en largeur
	 * @param hauteur nombre de cellules en hauteur
	 * @param grabHorizontal récupérer largeur disponible
	 * @param graVertical récupérer hauteur disponible
	 * @param horizontalAlign alignement horizontal (et FILL)
	 * @param verticalAlign alignement vertical (et FILL)
	 * @return true si succès
	 */
	public static boolean f_GRIDLAYOUT_DATA(Control composite, int largeur, int hauteur, boolean grabHorizontal, boolean graVertical, int horizontalAlign, int verticalAlign)
	{
		// création grid data
		GridData data = new GridData();
		data.horizontalSpan = largeur;
		data.verticalSpan = hauteur;		
		data.grabExcessHorizontalSpace = grabHorizontal;
		data.grabExcessVerticalSpace = graVertical;
		data.horizontalAlignment = horizontalAlign;
		data.verticalAlignment = verticalAlign;
		
		// affectation grid data
		composite.setLayoutData(data);
		
		return true; // ok
	}
	
	/**
	 * Récupération d'un descripteur d'image.
	 * 
	 * @param chemin chemin de l'image
	 * @return  descripteur d'image
	 */
	public static ImageDescriptor f_GET_IMAGE_DESCRIPTOR(String nom)
	{
		try
		{
	        Bundle bundle = Platform.getBundle(C_PlanetPluginActivator.PLUGIN_ID);
	        IPath path = new Path("/icons/" + nom);
	        URL url = FileLocator.find(bundle, path, null);
	        return ImageDescriptor.createFromURL(url);
		}
		catch(Exception e)
		{
			// trace
			e.printStackTrace();
			
			return null; // ko
		}
	}
	
	/**
	 * Récupération d'une image.
	 * 
	 * @param chemin chemin de l'image
	 * @return image
	 */
	public static Image f_GET_IMAGE(String nom)
	{
		return f_GET_IMAGE_DESCRIPTOR(nom).createImage();
	}
	
	/**
	 * Récupération d'un descripteur d'image.
	 * 
	 * @param ID ID de l'image
	 * @return  descripteur d'image
	 */
	public static ImageDescriptor f_GET_IMAGE_DESCRIPTOR_FROM_REGISTRY(String ID)
	{
		return C_PlanetPluginActivator.f_GET().getImageRegistry().getDescriptor(ID);
	}
	
	/**
	 * Affichage d'un message d'erreur.
	 * 
	 * @param titre titre de la dialogue
	 * @param message message d'erreur
	 */
	public static void f_AFFICHE_MESSAGE_ERREUR(Shell shell, String titre, String message)
	{
		MessageDialog dialog = new MessageDialog(shell, titre, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("logo_dd_p.png").createImage(), message, MessageDialog.ERROR, new String[]{"Ok"}, 0);
		dialog.open();
	}
	
	
	/**
	 * Affichage d'un message d'information.
	 * 
	 * @param titre titre de la dialogue
	 * @param message message d'information
	 */
	public static void f_AFFICHE_MESSAGE_INFORMATION(Shell shell, String titre, String message)
	{
		MessageDialog dialog = new MessageDialog(shell, titre, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("logo_dd_p.png").createImage(), message, MessageDialog.INFORMATION, new String[]{"Ok"}, 0);
		dialog.open();
	}
	
	/**
	 * Affichage une boite de dialogue.
	 * 
	 * @param titre titre de la dialogue
	 * @param message message d'erreur
	 */
	public static boolean f_AFFICHE_QUESTION(Shell shell, String titre, String message)
	{
		MessageDialog dialog = new MessageDialog(shell, titre, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("logo_dd_p.png").createImage(), message, MessageDialog.QUESTION, new String[]{"Oui", "Non"}, 1);
		int result = dialog.open();
		
		if(result == 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Affichage une boite de dialogue.
	 * 
	 * @param titre titre de la dialogue
	 * @param message message d'erreur
	 * @param nomImage
	 */
	public static boolean f_AFFICHE_QUESTION(Shell shell, String titre, String message, String nomImage)
	{
		MessageDialog dialog = new MessageDialog(shell, titre, C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR(nomImage).createImage(), message, MessageDialog.QUESTION, new String[]{"Oui", "Non"}, 1);
		int result = dialog.open();
		
		if(result == 0)
			return true;
		else
			return false;
	}
	
	public static void f_NOTIFICATION(final E_NotificationType type, final String titre, final String message)
	{
		// accès interface par thread
		Display.getDefault().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				C_PopupNotification.notify(titre, message, type);
			}
		});
	}
	
	public static Image f_SCALE_IMAGE_TO_SIZE(Image image, int taille)
	{
		// variables
		Image resultat;
		ImageData scaleImageData;
		
		// récupération des datas
		ImageData imageData = image.getImageData();
		
		// si plus haut que large
		if(imageData.height > imageData.width)
		{
			// si scale nécessaire
			if(imageData.height > taille)
			{
				// ratio pour faire tenir en hauteur
				float ratio = taille / (float) imageData.height;
				scaleImageData = imageData.scaledTo((int)(imageData.width*ratio), taille);
				
				// création image
				resultat = new Image(image.getDevice(), scaleImageData);
				
				// libération image originale
				image.dispose();
			}
			else resultat = image; // pas de transformation
		}
		else // plus large que haut
		{
			// si scale nécessaire
			if(imageData.width > taille)
			{
				// ratio pour faire tenir en largeur
				float ratio = taille / (float) imageData.width;
				scaleImageData = imageData.scaledTo(taille, (int)(imageData.height*ratio));
				
				// création image
				resultat = new Image(image.getDevice(), scaleImageData);
				
				// libération image originale
				image.dispose();
			}
			else resultat = image; // pas de transformation
		}
		
		return resultat;
	}
}
