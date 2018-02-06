package org.deltadore.planet.swt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public final class C_VerificateurSaisie implements VerifyListener, VerifyKeyListener             
{
	/** masque de saisie **/
    private Pattern 	m_pattern;  
    
    /** flag de forcage **/
    private boolean		m_is_majuscule;

    /**
     * Constructeur.
     * 
     * @param pattern pattern de recherche
     */
    public C_VerificateurSaisie(String pattern)
    {
    	super();
    	
    	// récupération des paramètres
    	m_pattern = Pattern.compile(pattern);  
    }
    
    /**
     * Définit si la saisie est en majuscule.
     * 
     * @param etat 
     * @return
     */
    public boolean f_SET_SAISIE_MAJUSCULE(boolean etat)
    {
    	m_is_majuscule = etat;
    	
    	return true; // ok
    }
    
    /**
     * Vérification saisie.
     * 
     * @param e événement de sasie
     */
    private void f_VERIFICATION (VerifyEvent e) 
    {
        // filtre majuscule
        if(m_is_majuscule)
        	e.text = e.text.toUpperCase();
        
        // field source
        Text text = (Text)e.getSource();

        final String oldS = text.getText();
        String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
        Matcher matcher = m_pattern.matcher(newS);
        if ( !matcher.matches() ) {
            e.doit = false;
            return;
        }
    }
    
    @Override
    public void verifyText(VerifyEvent verifyevent) 
    {
        f_VERIFICATION(verifyevent);
    }

    @Override
    public void verifyKey(VerifyEvent verifyevent) 
    {
        f_VERIFICATION(verifyevent);
    }
}
