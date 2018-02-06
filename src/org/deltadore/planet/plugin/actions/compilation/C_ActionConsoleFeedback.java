package org.deltadore.planet.plugin.actions.compilation;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.console.IConsoleConstants;

public class C_ActionConsoleFeedback implements IViewActionDelegate
{
	public enum ETAT
	{
		RAZ, RUN, FATAL, ERROR, WARNING, OK 
	}
	
	public static void f_UPDATE_BOUTON(String buttonID, ETAT etat)
	{
		// variables
		String image = null;
		
		// récupération vue console
		IViewPart viewPart = C_ToolsWorkbench.f_FIND_VUE(IConsoleConstants.ID_CONSOLE_VIEW);
		
		if(viewPart == null)
			return;
		
		// récupération toolbar manager
		IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
		
		// récupération items
		IContributionItem[] items = toolBarManager.getItems();
		
		// nom de l'image
		if(buttonID.equalsIgnoreCase("FEEDBACK_COMPILATION_CM"))
			image = "compil_cm";
		else if(buttonID.equalsIgnoreCase("FEEDBACK_COMPILATION_SERVEUR"))
			image = "compil_serveur";
		else if(buttonID.equalsIgnoreCase("FEEDBACK_COMPILATION_CYCLONE"))
			image = "compil_cyclone";
		else if(buttonID.equalsIgnoreCase("FEEDBACK_COMPILATION_SERVEUR_KNX"))
			image = "compil_knx";
		else if(buttonID.equalsIgnoreCase("FEEDBACK_COMPILATION_INFO_FONC"))
			image = "compil_infoFonc";
		else if(buttonID.equalsIgnoreCase("FEEDBACK_CREATION_JAR"))
			image = "compil_jar";
		
		// parcours des items
		for(IContributionItem item : items)
		{		
			if(item instanceof ActionContributionItem)
			{
				ActionContributionItem a = (ActionContributionItem) item;
				
				ToolItem t = (ToolItem) a.getWidget();
				
				if(item.getId() != null 
				&& item.getId().equalsIgnoreCase(buttonID))
				{
					switch(etat)
					{
					case RUN:
						image = image.concat("_run.png");
						t.setImage(C_ToolsSWT.f_GET_IMAGE(image));
						t.setEnabled(true);
						break;
					case FATAL:
						image = image.concat("_error.png");
						t.setImage(C_ToolsSWT.f_GET_IMAGE(image));
						t.setEnabled(true);
						break;
					case ERROR:
						image = image.concat("_error.png");
						t.setImage(C_ToolsSWT.f_GET_IMAGE(image));
						t.setEnabled(true);
						break;
					case WARNING:
						image = image.concat("_warning.png");
						t.setImage(C_ToolsSWT.f_GET_IMAGE(image));
						t.setEnabled(true);
						break;
					case OK:
						image = image.concat("_ok.png");
						t.setImage(C_ToolsSWT.f_GET_IMAGE(image));
						t.setEnabled(true);
						break;
					case RAZ:
						t.setEnabled(false);
						break;
					}
				}
			}
		}
	}

	@Override
	public void run(IAction action) 
	{
		// récupération du texte du bouton
		String texte = action.getText();
		
		// affiche la console correspondante
		C_ToolsWorkbench.f_SHOW_CONSOLE(texte);
	}

	// inutilisés
	@Override
	public void selectionChanged(IAction action, ISelection selection) {}
	@Override
	public void init(IViewPart arg0) {}
}
