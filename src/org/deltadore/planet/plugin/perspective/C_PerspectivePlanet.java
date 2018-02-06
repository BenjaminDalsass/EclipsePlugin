package org.deltadore.planet.plugin.perspective;

import org.deltadore.planet.ui.vues.changeLog.C_VueChangeLog;
import org.deltadore.planet.ui.vues.fonctionExplorer.C_VueFonctionExplorer;
import org.deltadore.planet.ui.vues.images.C_VueImages;
import org.deltadore.planet.ui.vues.projet.C_VueProjet;
import org.deltadore.planet.ui.vues.releases.C_VueReleases;
import org.deltadore.planet.ui.vues.sites.C_VueSitesPlanet;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.console.IConsoleConstants;
import org.tigris.subversion.subclipse.ui.repository.RepositoriesView;

public class C_PerspectivePlanet implements IPerspectiveFactory
{
	private String 	ID_HAUT_GAUCHE = "HAUT_GAUCHE";
	private String 	ID_BAS_DROITE = "BAS_DROITE";
	private String 	ID_DROITE = "DROITE";
	private String 	ID_BAS = "BAS";
	private String 	ID_BAS2 = "BAS2";
	
	@Override
	public void createInitialLayout(IPageLayout layout) 
	{
        // Editeur
        String editorArea = layout.getEditorArea();
        
        // HAUT - GAUCHE
        IFolderLayout hautGauche = layout.createFolder(ID_HAUT_GAUCHE, IPageLayout.LEFT, 0.2f,  editorArea);
        hautGauche.addView(JavaUI.ID_PACKAGES); 
        hautGauche.addPlaceholder(C_VueFonctionExplorer.ID);
        hautGauche.addPlaceholder(IPageLayout.ID_PROJECT_EXPLORER); // si non visible aparaîtra ici lors de l'ajout
        
        // BAS_GAUCHE
        IFolderLayout bas_gauche = layout.createFolder("BAS_GAUCHE", IPageLayout.BOTTOM, IPageLayout.NULL_RATIO, ID_HAUT_GAUCHE);
        bas_gauche.addView(C_VueProjet.ID); 
        IViewLayout viewLayout = layout.getViewLayout(C_VueProjet.ID);
        viewLayout.setMoveable(false);
        viewLayout.setCloseable(false);
        
        // DROITE
        IFolderLayout droite = layout.createFolder(ID_DROITE, IPageLayout.RIGHT, 0.75f, editorArea);
        droite.addView(IPageLayout.ID_OUTLINE);
        droite.addView(IPageLayout.ID_TASK_LIST);
        droite.addView(C_VueImages.ID);
        
        // BAS
        IFolderLayout bottom = layout.createFolder(ID_BAS, IPageLayout.BOTTOM, 0.7f, editorArea);
        bottom.addView(C_VueChangeLog.ID);
        bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
        
        // BAS 2
        IFolderLayout bottom2 = layout.createFolder(ID_BAS2, IPageLayout.BOTTOM, 0.7f, editorArea);
        bottom2.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
       
        // BAS_DROITE
        IFolderLayout basDroite = layout.createFolder(ID_BAS_DROITE, IPageLayout.BOTTOM, 0.6f, ID_DROITE);
        basDroite.addView(C_VueReleases.ID);
        basDroite.addView(C_VueSitesPlanet.ID);
        basDroite.addPlaceholder(RepositoriesView.VIEW_ID);
        
        // Fast View
        layout.addFastView(NewSearchUI.SEARCH_VIEW_ID, 0.5f);
	}
}
