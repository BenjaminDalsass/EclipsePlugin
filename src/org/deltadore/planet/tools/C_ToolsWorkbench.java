package org.deltadore.planet.tools;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class C_ToolsWorkbench 
{
    /**
     * Retourne le projet associ� a l'�dition actuelle.
     * 
     * @param window worbench window
     * @return projet
     */
    public static IProject f_GET_PROJET_FROM_EDITOR(IWorkbenchWindow window)
    {
    	// s�curit�
    	if(window.getActivePage() == null)
    		return null;
    	
    	// s�curit�
    	if(window.getActivePage().getActiveEditor() == null)
    		return null;
    	
    	// r�cup�ration �diteur
        IEditorInput editorinput = window.getActivePage().getActiveEditor().getEditorInput();
        
        // file �diteur
        FileEditorInput fileEditorInput = (FileEditorInput) editorinput.getAdapter(FileEditorInput.class);

        if( fileEditorInput == null || fileEditorInput.getFile()== null)
        {
        	return null;
        }
        return fileEditorInput.getFile().getProject();
    }
    
    /**
     * Retourne le projet java associ� a l'�dition actuelle.
     * 
     * @param window worbench window
     * @return projet java
     */
    public static IJavaProject f_GET_PROJET_JAVA_FROM_EDITOR(IWorkbenchWindow window)
    {
    	IProject projet = f_GET_PROJET_FROM_EDITOR(window);
    	
    	if(projet == null)
    		return null;
    	
    	return (IJavaProject) JavaCore.create(projet);
    }
    
	/**
	 * V�rifie l'�xistance d'un projet dans le workspace.
	 * 
	 * @param nomProjet nom du projet
	 * @return true si le projet �xiste
	 */
	public static boolean f_IS_PROJET_EXISTE(String nomProjet)
	{
		// r�cup�ration des projets du workspace
		IProject[] projets = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		// parcours des projets...
		for(IProject projet : projets)
		{
			// si trouv�
			if(projet.getName().equalsIgnoreCase(nomProjet))
				return true;
		}
		
		return false; // in�xistant
	}
	
	public static boolean f_IS_PROJET_PLANET_SELECTIONNE(IWorkbenchWindow window)
	{
		try
		{
			// variables
			IProject projet = null;
			
			// r�cup�ration s�lection
			TreeSelection sel = (TreeSelection) window.getActivePage().getSelection(JavaUI.ID_PACKAGES);
			
			// s�curit�
			if(sel == null)
				return false;
			
			if(sel.getFirstElement() instanceof IProject)
			{
				projet = (IProject) sel.getFirstElement();
			}
			else if(sel.getFirstElement() instanceof IJavaProject)
			{
				projet = ((IJavaProject) sel.getFirstElement()).getProject();
			}
			else if(sel.getFirstElement() instanceof IJavaElement)
			{
				IJavaElement javaElement = (IJavaElement) sel.getFirstElement();
				projet = javaElement.getJavaProject().getProject();
			}
			else if(sel.getFirstElement() instanceof IPackageFragment)
			{
				IPackageFragment packageFragment = (IPackageFragment) sel.getFirstElement();
				projet = packageFragment.getJavaProject().getProject();
			}
			else if(sel.getFirstElement() instanceof IFolder)
			{
				IFolder folder = (IFolder) sel.getFirstElement();
				projet = folder.getProject();
			}
			else if(sel.getFirstElement() instanceof IFile)
			{
				IFile file = (IFile) sel.getFirstElement();
				projet = file.getProject();
			}
			
			// s�curit�
			if(projet == null)
				return false;

			File fichier = new File(projet.getLocation().toString() + File.separator + "Release.xml");
			
			return fichier.exists();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean f_IS_PROJET_PLANET(IProject projet)
	{
		File fichier = new File(projet.getLocation().toString() + File.separator + "Release.xml");
		
		return fichier.exists();
	}
	
	public static IProject f_GET_PROJET_SELECTIONNE(TreeSelection sel)
	{
		try
		{
			// s�curit�
			if(sel == null)
				return null;
			
			if(sel.getFirstElement() instanceof IProject)
			{
				return (IProject) sel.getFirstElement();
			}
			else if(sel.getFirstElement() instanceof IJavaProject)
			{
				IJavaProject javaProject = (IJavaProject) sel.getFirstElement();
				return javaProject.getProject();
			}
			else if(sel.getFirstElement() instanceof IJavaElement)
			{
				IJavaElement javaElement = (IJavaElement) sel.getFirstElement();
				
				return javaElement.getJavaProject().getProject();
			}
			else if(sel.getFirstElement() instanceof IPackageFragment)
			{
				IPackageFragment packageFragment = (IPackageFragment) sel.getFirstElement();
				
				return packageFragment.getJavaProject().getProject();
			}
			else if(sel.getFirstElement() instanceof IFolder)
			{
				IFolder folder = (IFolder) sel.getFirstElement();
				return folder.getProject();
			}
			else if(sel.getFirstElement() instanceof IFile)
			{
				IFile file = (IFile) sel.getFirstElement();
				return file.getProject();
			}
			
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static IJavaProject f_GET_PROJET_JAVA_SELECTIONNE(TreeSelection sel)
	{
		try
		{
			// s�curit�
			if(sel == null)
				return null;
			
			if(sel.getFirstElement() instanceof IJavaProject)
			{
				return (IJavaProject) sel.getFirstElement();
			}
			else if(sel.getFirstElement() instanceof IJavaElement)
			{
				IJavaElement javaElement = (IJavaElement) sel.getFirstElement();
				
				return javaElement.getJavaProject();
			}
			else if(sel.getFirstElement() instanceof IPackageFragment)
			{
				IPackageFragment packageFragment = (IPackageFragment) sel.getFirstElement();
				
				return packageFragment.getJavaProject();
			}
			else if(sel.getFirstElement() instanceof IFolder)
			{
				IFolder folder = (IFolder) sel.getFirstElement();
				IProject projet = folder.getProject();
				
				if(projet.hasNature(JavaCore.NATURE_ID))
					return (IJavaProject) JavaCore.create(projet); 
			}
			else if(sel.getFirstElement() instanceof IFile)
			{
				IFile file = (IFile) sel.getFirstElement();
				IProject projet = file.getProject();
				
				if(projet.hasNature(JavaCore.NATURE_ID))
					return (IJavaProject) JavaCore.create(projet); 
			}
			
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static IJavaProject f_GET_PROJET_PLANET_SELECTIONNE(IWorkbenchWindow window)
	{
		if(window.getActivePage().getSelection(JavaUI.ID_PACKAGES) instanceof TreeSelection)
		{
			// r�cup�ration s�lection
			TreeSelection sel = (TreeSelection) window.getActivePage().getSelection(JavaUI.ID_PACKAGES);
			
			return f_GET_PROJET_JAVA_SELECTIONNE(sel);
		}
		else return null;
	}
	
	/**
	 * Fermeture du projet pass� en param�tre.
	 * 
	 * @param projet projet � fermer
	 * @return true si succ�s
	 */
	public static boolean f_FERMETURE_PROJET(IProject projet)
	{
		try 
		{
			// fermeture projet
			projet.close(new NullProgressMonitor());
			
			return true; // ok
		} 
		catch (CoreException e) 
		{
			// trace
			e.printStackTrace();
			 
			return false; // ko
		}
	}
	
	/**
	 * Fermeture du projet pass� en param�tre.
	 * 
	 * @param projet projet � fermer
	 * @return true si succ�s
	 */
	public static boolean f_FERMETURE_PROJET(IJavaProject projet)
	{
		return f_FERMETURE_PROJET(projet.getProject());
	}
	
	/**
	 * Ouverture du projet pass� en param�tre.
	 * 
	 * @param projet projet � ouvrir
	 * @return true si succ�s
	 */
	public static boolean f_OUVERTURE_PROJET(IProject projet)
	{
		try 
		{
			// ouverture projet
			projet.open(new NullProgressMonitor());
			
			return true; // ok
		} 
		catch (CoreException e) 
		{
			// trace
			e.printStackTrace();
			
			return false; // ko
		}
	}
	
	/**
	 * Ouverture du projet pass� en param�tre.
	 * 
	 * @param projet projet � ouvrir
	 * @return true si succ�s
	 */
	public static boolean f_OUVERTURE_PROJET(IJavaProject projet)
	{
		return f_OUVERTURE_PROJET(projet.getProject());
	}
	
    /**
     * Retounre un descriptif de Perspective avec l'identifiant pass� en param�tre.
     * 
     * @return descriptif de la Perspective
     */
	public static IPerspectiveDescriptor f_GET_PERSPECTIVE_DESCRIPTOR(String perspectiveID) 
    {
        IPerspectiveRegistry perspectiveRegistry = C_PlanetPluginActivator.f_GET().getWorkbench().getPerspectiveRegistry();
        IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry.findPerspectiveWithId(perspectiveID);
        return perspectiveDescriptor;
    }
	
	/**
	 * Ouvre le fichier pass� en param�tre dans l'�diteur associ�.
	 * 
	 * @param fichier fichier � ouvrir
	 * @return true si succ�s
	 */
	public static  boolean f_OPEN_FILE_IN_EDITOR(File fichier)
	{
		// r�cup�ration file store
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fichier.toURI());
		
		// s�curit�
		if(!fileStore.fetchInfo().isDirectory() 
		&& fileStore.fetchInfo().exists()) 
		{
			// r�cup�ration workbench page
		    IWorkbenchPage page =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		    
		    try 
		    {
		    	// ouverture fichier dans �diteur
		        IDE.openEditorOnFileStore(page, fileStore);
		        
		        return true; // ok
		    } 
		    catch (PartInitException ex) 
		    {
		    	// trace
		    	ex.printStackTrace();
		    	
		    	return false; // ko
		    }
		}
		else return false; // ko
	}
	
	/**
	 * Ajout contenu texte dans presse papier.
	 * 
	 * @param display display SWT
	 * @param text texte � inserer dans presse papier
	 * @return true si succ�s
	 */
	public static boolean f_SET_SIMPLE_CLIPBOARD_TEXTE_CONTENT(Display display, String text)
	{
		// cr�ation clipboard
		Clipboard clipboard = new Clipboard(display);
		
		// affectation du contenu
		clipboard.setContents(new Object[] {text},
	              new Transfer[] { TextTransfer.getInstance() });
		
		return true;
	}
	
	/**
	 * Insertion de texte dans l'�diteur.
	 * 
	 * @param text texte � ins�rer
	 * @return true si succ�s
	 */
	public static boolean f_INSERT_INTO_TEXT_EDITOR(String text)
	{
	    try 
	    {
			// r�cup�ration workbench page
		    IWorkbenchPage page =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	
		    // r�cup�ration active part
		    IEditorPart part = page.getActiveEditor();
		    
		    // s�curit�
		    if (!(part instanceof AbstractTextEditor))
		       return false;
		    
		    // r�cup�ration editeur texte
		    ITextEditor editor = (ITextEditor)part;
		    
		    // r�cup�ration provider
		    IDocumentProvider dp = editor.getDocumentProvider();
		    
		    // r�cup�ration document
		    IDocument doc = dp.getDocument(editor.getEditorInput());
		    
		    // r�cup�ration s�lection
		    ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
			
		    // insertion du texte
		    doc.replace(sel.getOffset(), 0, text);
		    
		    return true; // ok
		} 
	    catch (BadLocationException e1)
	    {
	    	// trace
			e1.printStackTrace();
			
			return false; // ko
		}
	}
	
	/**
	 * Retourne le workbench actif.
	 * 
	 * @return workbench actif
	 */
	public static IWorkbenchWindow f_GET_ACTIVE_WORBENCH_WINDOW()
	{
		return C_PlanetPluginActivator.f_GET().getWorkbench().getActiveWorkbenchWindow();
	}
	
	/**
	 * Retourne tous les projets du workspace.
	 * 
	 * @return projets du workspace
	 */
	public static IProject[] f_GET_PROJETS_WORKSPACE()
	{
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}
	
	/**
	 * Retourne les projets PLANET du workspace.
	 * 
	 * @return projets du workspace
	 */
	public static IProject[] f_GET_PROJETS_PLANET_WORKSPACE()
	{
		ArrayList<IProject> projetsPlanet = new ArrayList<IProject>();
		
		IProject[] projets = f_GET_PROJETS_WORKSPACE();
		
		for(IProject projet : projets)
		{
			if(f_IS_PROJET_PLANET(projet))
				projetsPlanet.add(projet);
		}
		
		return projetsPlanet.toArray(new IProject[projetsPlanet.size()]);
	}
	
	/**
	 * Retourne le shell du workbench.
	 * 
	 * @return shell
	 */
	public static Shell f_GET_SHELL()
	{
		return f_GET_ACTIVE_WORBENCH_WINDOW().getShell();
	}
	
	/**
	 * Ouvre un explorateur.
	 * 
	 * @param url url � ouvrir
	 * @return true si succ�s
	 */
	public static boolean f_OPEN_NAVIGATEUR(String nom, String url)
	{
		try 
		{
			IWebBrowser btw = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(IWorkbenchBrowserSupport.NAVIGATION_BAR , nom, nom, url);
			
			btw.openURL(new URL(url));
			
			return true; // ok
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			
			return false; // ko
		} 
	}
	
	public static Object[] getSelectedAdaptables(ISelection selection, Class<?> c) {
		ArrayList<Object> result = null;
		if (selection != null && !selection.isEmpty()) {
			result = new ArrayList<Object>();
			Iterator<?> elements = ((IStructuredSelection) selection).iterator();
			while (elements.hasNext()) {
				Object adapter = getAdapter(elements.next(), c);
				if (c.isInstance(adapter)) {
					result.add(adapter);
				}
			}
		}
		if (result != null && !result.isEmpty()) {
			return result.toArray((Object[])Array.newInstance(c, result.size()));
		}
		return (Object[])Array.newInstance(c, 0);
	}
	
	public static Object getAdapter(Object adaptable, Class<?> c) {
		if (c.isInstance(adaptable)) {
			return adaptable;
		}
		if (adaptable instanceof IAdaptable) {
			IAdaptable a = (IAdaptable) adaptable;
			Object adapter = a.getAdapter(c);
			if (c.isInstance(adapter)) {
				return adapter;
			}
		}
		return null;
	}
	
	public static void f_SELECT(IResource resource)
	{
		PackageExplorerPart part= PackageExplorerPart.getFromActivePerspective();

		part.selectAndReveal(resource);
	}
	
	public static void f_GET_ACTION_BY_ID(String ID)
	{
//		IExtensionRegistry registry = Platform.getExtensionRegistry();
//		
//		for (IConfigurationElement element : registry
//		.getConfigurationElementsFor("core.parties")) 
//		{
//			String partyName = element.getAttribute("name");
//			IConfigurationElement actionsElement = element.getChildren("actions")[0];
//			String actionId = actionsElement.getAttribute("add");
//			actionsElement.
//			IAction partyAction = ... ; // get action class referenced
//			// by actionId variable
//			manager.add(partyAction);
//		}
	}
	
	/**
	 * Recherche une console avec le nom indiqu�.
	 * 
	 * @param consoleName nom de la console
	 * @return la console
	 */
	public static IConsole f_FIND_CONSOLE(String consoleName)
	{
		// r�cup�ration des c
		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		
		// s�curit�
		if(consoles != null)
		{
			// parcours des consoles...
			for(IConsole console : consoles)
			{
				// si nom �quivalent
				if(console.getName().equalsIgnoreCase(consoleName))
				{
					return console; // ok
				}
			}
		}
		
		return null; // ko
	}
	
	/**
	 * Affiche la console avec le nom pass� en param�tre.
	 * 
	 * @param consoleName nom de la console � afficher
	 */
	public static void f_SHOW_CONSOLE(String consoleName)
	{
		// r�cup�ration console
		IConsole console = f_FIND_CONSOLE(consoleName);
		
		// affichage
		if(console != null)
			f_SHOW_CONSOLE(console);
	}
	
	/**
	 * Affiche la console pass�e en param�tre.
	 * 
	 * @param console console � afficher
	 */
	public static void f_SHOW_CONSOLE(IConsole console)
	{
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}
	
	/**
	 * Recherche une vue.
	 * 
	 * @return la vue
	 */
	public static IViewPart f_FIND_VUE(String view)
	{
		// r�cup�ration vue projet
		for(IWorkbenchWindow window : C_PlanetPluginActivator.f_GET().getWorkbench().getWorkbenchWindows())
		{
			IViewPart vue = (IViewPart) window.getActivePage().findView(view);
			
			if(vue != null)
				return vue;
		}
		
		return null;
	}
	
	public static MCompositePart createCompositePart(EPartService partService, EModelService modelService, MPart originalPart)
	{
		MCompositePart compPart = modelService.createModelElement(MCompositePart.class);
		compPart.setElementId("Split Host(" + originalPart.getLabel() + ")");
		compPart.setLabel(originalPart.getLabel());
		compPart.setTooltip(originalPart.getTooltip());
		compPart.setIconURI(originalPart.getIconURI());
		compPart.setCloseable(true);
		compPart.setContributionURI(
				"bundleclass://org.eclipse.e4.ui.workbench.addons.swt/org.eclipse.e4.ui.workbench.addons.splitteraddon.SplitHost");

		// Always remove the composite part from the model
		compPart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);

		return compPart;
	}

	public static void splitPart(EPartService partService, EModelService modelService, MPart partToSplit, boolean horizontal)
	{
		MElementContainer<MUIElement> parent = partToSplit.getParent();
		int index = parent.getChildren().indexOf(partToSplit);

		MPart editorClone = (MPart) modelService.cloneElement(partToSplit, null);

		MCompositePart compPart = createCompositePart(partService, modelService, partToSplit);

		// Add the new composite part to the model
		compPart.getChildren().add(editorClone);
		compPart.setSelectedElement(editorClone);
		parent.getChildren().add(index, compPart);
		parent.setSelectedElement(compPart);

		// Now, add the original part into the composite
		int orientation = horizontal ? EModelService.ABOVE : EModelService.LEFT_OF;
		modelService.insert(partToSplit, editorClone, orientation, 0.5f);

		partService.activate(partToSplit);
	}

	/**
	 * Finds the CompositePart containing the given part (if any)
	 * 
	 * @param part
	 * @return The MCompositePart or 'null' if none is found
	 */
	public static MCompositePart findContainingCompositePart(EPartService partService, EModelService modelService, MPart part) 
	{
		if (part == null)
			return null;

		MUIElement curParent = part.getParent();
		while (curParent != null && !(curParent instanceof MCompositePart))
			curParent = curParent.getParent();

		return (MCompositePart) curParent;
	}

	public static void unsplitPart(EPartService partService, EModelService modelService, MCompositePart compositePart)
	{
		if (compositePart == null)
			return;

		List<MPart> innerElements = modelService.findElements(compositePart, null, MPart.class, null);
		if (innerElements.size() < 2)
			return;

		MPart originalEditor = innerElements.get(1); // '0' is the composite
														// part

		MElementContainer<MUIElement> compParent = compositePart.getParent();
		int index = compParent.getChildren().indexOf(compositePart);
		compParent.getChildren().remove(compositePart);
		originalEditor.getParent().getChildren().remove(originalEditor);
		compParent.getChildren().add(index, originalEditor);

		if (partService.getActivePart() == originalEditor)
			partService.activate(null);
		partService.activate(originalEditor);
	}
}
