package org.deltadore.planet.model.define;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.swt.graphics.Image;

public class C_DefineImages 
{
	/** R�pertoires **/
	public static Image						REPERTOIRE_OUVRIR;	
	
	
	/** Images r�vision **/
	public static Image						REVISION_OK;
	public static Image						REVISION_OBSOLETE;
	public static Image						REVISION_INCONNUE;
	
	/** Applications **/
	public static Image						APPLICATION_ANT;
	
	/**
	 * Initialisation des images.
	 * 
	 */
	public static void f_INIT_IMAGES()
	{
		// r�pertoires
		REPERTOIRE_OUVRIR = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("folder_out.png").createImage();
		
		// r�visions
		REVISION_OK = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("note_ok.png").createImage();
		REVISION_OBSOLETE = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("note_warning.png").createImage();
		REVISION_INCONNUE = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("note_view.png").createImage();
		
		// applications
		APPLICATION_ANT = C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("ant.png").createImage();
	}
	
	/**
	 * Restitution des images.
	 * 
	 */
	public static void f_RESTIT_IMAGES()
	{
		// r�pertoires
		REPERTOIRE_OUVRIR .dispose();
				
		// r�visions
		REVISION_OK.dispose();
		REVISION_OBSOLETE.dispose();
		REVISION_INCONNUE.dispose();
		
		// applications
		APPLICATION_ANT.dispose();
	}
}
