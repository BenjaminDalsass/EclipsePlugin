package org.deltadore.planet.swt;

import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.ui.forms.widgets.FormText;

public class C_FormTextContent 
{
	/** ID **/
	public static String		ID_IMAGE_BULLET = "bullet";
	
	/** Texte **/
	private StringBuffer		m_strTexte;
	
	/**
	 * Constructeur.
	 * 
	 */
	public C_FormTextContent(FormText formText)
	{
		super();
		
		formText.setImage("puce_bleue", C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("bullet_ball_blue.png").createImage());
		formText.setImage("check_vert", C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("check2.png").createImage());
		formText.setImage("carre_violet", C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("bullet_square_violet.png").createImage());
		formText.setImage("utilisateur", C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("dude3.png").createImage());
		
		formText.setColor("VIOLET", C_DefineCouleur.VIOLET_ROUGE);
		
		// initialisation
		f_INIT();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		m_strTexte = new StringBuffer();
	}
	
	public void f_BEGIN()
	{
		m_strTexte.append("<form>");
	}
	
	public void f_AJOUTE_PUCE_BLEUE(String texte)
	{
		m_strTexte.append("<li style=\"image\" value=\"puce_bleue\">" + texte + "<br/></li>");
	}
	
	public void f_AJOUTE_CHECK_VERT(String texte)
	{
		m_strTexte.append("<li style=\"image\" value=\"check_vert\">" + texte + "<br/></li>");
	}
	
	public void f_AJOUTE_CARRE_VIOLET(String texte)
	{
		m_strTexte.append("<li style=\"image\" value=\"carre_violet\">" + texte + "<br/></li>");
	}
	
	public void f_AJOUTE_DUDE(String texte)
	{
		m_strTexte.append("<li style=\"image\" value=\"utilisateur\">" + texte + "<br/></li>");
	}
	
	public void f_AJOUTE_TEXTE(String texte)
	{
		m_strTexte.append(texte);
	}
	
	public void f_AJOUTE_TEXTE_VIOLET(String texte)
	{
		m_strTexte.append("<li color=\"VIOLET\">" + texte + "<br/></li>");
	}
	
	public void f_END()
	{
		m_strTexte.append("</form>");
	}
	
	@Override
	public String toString() 
	{
		return m_strTexte.toString();
	}
}
