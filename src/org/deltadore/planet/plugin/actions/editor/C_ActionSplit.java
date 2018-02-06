package org.deltadore.planet.plugin.actions.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class C_ActionSplit implements IWorkbenchWindowActionDelegate, IPartListener
{
	protected EPartService m_partService;

	protected EModelService m_modelService;
	
	IAction m_action;

	/** Windows **/
	private IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window) 
	{
		m_window = window;
		m_partService = (EPartService) m_window.getService(EPartService.class);
		m_modelService = (EModelService) m_window.getService(EModelService.class);
		
		// écoute les éditeurs
		f_LISTEN_EDITORS();
	}
	
	/**
	 * Ecoute les éditeurs.
	 * 
	 * 
	 */
	private void f_LISTEN_EDITORS()
	{
		try 
		{
			for(IEditorReference editorRef : m_window.getActivePage().getEditorReferences())
			{
				if(editorRef.getId().contains("CEditor") ||  editorRef.getId().contains("CompilationUnitEditor"))
					editorRef.getEditor(true).getSite().getPage().addPartListener(this);;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	 private MPartStack getStackFor(MPart part) 
	 {
		  MUIElement presentationElement = part.getCurSharedRef() == null ? part : part.getCurSharedRef();
		  MUIElement parent = presentationElement.getParent();
		  while (parent != null && !(parent instanceof MPartStack))
		   parent = parent.getParent();
		  
		  return (MPartStack) parent;
	}

	@Override
	public void run(IAction action)
	{
		MPart activeEditor = m_partService.getActivePart();

		 if (activeEditor == null)
		  return;
		  
		 MPartStack stack = getStackFor(activeEditor);
		 if (stack == null)
		  return ;
		  
		 MStackElement stackSelElement = stack.getSelectedElement();
		 
		 if (stackSelElement instanceof MCompositePart) 
		 {
			 C_ToolsWorkbench.unsplitPart(m_partService, m_modelService, (MCompositePart) stackSelElement);
		 } 
		 else
		 {
			 C_ToolsWorkbench.splitPart(m_partService, m_modelService,(MPart)stackSelElement, false);
		 }
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) 
	{
		m_action = action;
		
		f_CHECK();
	}

	@Override
	public void dispose() 
	{
		m_window = null;
	}
	
	
	private void f_CHECK()
	{
		if(m_action == null)
			return;
		
		MPart activeEditor = m_partService.getActivePart();
		
		 if (activeEditor == null)
		 {
			 m_action.setEnabled(false);
			 return;
		 }
		 
		 MPartStack stack = getStackFor(activeEditor);
		 if (stack == null)
		 {
			 m_action.setEnabled(false);
			 return;
		 }
		 
		// si debug
		if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.DEBUG))
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.CONNECTION_FAILED, "Editeur", stack.getChildren().size() + "Elements");
		 
		 if(stack.getChildren().size() == 0)
		 {
			 m_action.setEnabled(false);
			 return;
		 }
		 
		 MStackElement stackSelElement = stack.getSelectedElement();
		 if (stackSelElement instanceof MCompositePart) 
		 {
			 m_action.setEnabled(true);
			 m_action.setChecked(true);
		 }
		 else
		 {
			 m_action.setEnabled(true);
			 m_action.setChecked(false);
		 }
	}
	

	@Override
	public void partActivated(IWorkbenchPart event) 
	{
		if(C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_BOOLEAN(C_DefinePreferencesPlugin.DEBUG))
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.CONNECTED, "Test", event.getClass().getSimpleName());
		
		if(event.getClass().getSimpleName().contains("CEditor"))
			f_CHECK();
		else if(event.getClass().getSimpleName().contains("CompilationUnitEditor"))
			f_CHECK();
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart event) {
	}

	@Override
	public void partClosed(IWorkbenchPart event) {
		f_CHECK();
	}

	@Override
	public void partDeactivated(IWorkbenchPart event) {
		f_CHECK();
	}

	@Override
	public void partOpened(IWorkbenchPart event) {
		f_CHECK();
	}
}
