package org.deltadore.planet.tools;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.deltadore.planet.model.define.C_DefineInfosServeurs;
import org.deltadore.planet.model.define.C_DefinePreferencesPlugin;
import org.deltadore.planet.model.define.C_DefineSVN;
import org.deltadore.planet.model.descriptifs.C_DescDistribution;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.C_PlanetPluginActivator;
import org.deltadore.planet.swt.E_NotificationType;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;
import org.eclipse.team.ui.synchronize.ResourceScope;
import org.eclipse.team.ui.synchronize.SubscriberParticipant;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.tigris.subversion.subclipse.core.ISVNCoreConstants;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.ISVNRemoteFolder;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.repo.SVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.resources.RemoteFile;
import org.tigris.subversion.subclipse.core.resources.RemoteFolder;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.subclipse.ui.ISVNUIConstants;
import org.tigris.subversion.subclipse.ui.SVNUIPlugin;
import org.tigris.subversion.subclipse.ui.actions.CheckoutIntoAction;
import org.tigris.subversion.subclipse.ui.dialogs.MergeDialog;
import org.tigris.subversion.subclipse.ui.operations.MergeOperation;
import org.tigris.subversion.subclipse.ui.subscriber.SVNSynchronizeParticipant;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tmatesoft.svn.core.SVNException;

public class C_ToolsSVN 
{
	/**
	 * Retourne la dossier svn de la référence passée en paramètre.
	 * 
	 * @param nomReference nom de la référence souhaitée
	 * @return dossier distant svn
	 */
	public static final RemoteFolder f_GET_REMOTE_FOLDER_REFERENCE(String nomReference)
	{
		try
		{
			// récupération du répository release
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
						
			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			// url repository référence
			SVNUrl url = rootFolder.getUrl().appendPath(nomReference);
			
			return new RemoteFolder(repository, url, SVNRevision.HEAD);	
		}
		catch(Exception e)
		{
			return null; // ko
		}
	}
	
	/**
	 * Affiche l'historique de la référence souhaitée.
	 * 
	 * @param descRelease descriptif release
	 * @return true si succès
	 */
	public static final boolean f_SHOW_HISTORY(C_DescRelease descRelease)
	{
		try
		{
			// Remote Folder
			final RemoteFolder remoteFolder = f_GET_REMOTE_FOLDER_REFERENCE(descRelease);
			
			// Workbench
			final IWorkbench workbench = C_PlanetPluginActivator.f_GET().getWorkbench();
			
			// Active page
			final IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
			
			// vue History
			IHistoryView view = (IHistoryView) activePage.findView(ISVNUIConstants.HISTORY_VIEW_ID);
			
			if(view == null)
			{
				IViewRegistry viewRegistry = workbench.getViewRegistry();
				IViewDescriptor desc = viewRegistry.find(ISVNUIConstants.HISTORY_VIEW_ID);
				IWorkbenchPart part = desc.createView();
				activePage.activate(part);
				final IHistoryView createview = (IHistoryView) part;
				
				activePage.showView(ISVNUIConstants.HISTORY_VIEW_ID);
				createview.showHistoryFor(remoteFolder, true);
			
//				// accès interface par thread
//				workbench.getDisplay().asyncExec(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
//						try {
//							activePage.showView(ISVNUIConstants.HISTORY_VIEW_ID);
//						} catch (PartInitException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						createview.showHistoryFor(remoteFolder, true);
//					}
//				});
			}
			else
			{
				activePage.showView(ISVNUIConstants.HISTORY_VIEW_ID);
				view.showHistoryFor(remoteFolder, true);
			}
			
			return true; // ok
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return false; // ko
		}
	}
	
	/**
	 * Retourne la dossier svn de la référence passée en paramètre.
	 * 
	 * @param descRelease descriptif release
	 * @return dossier distant svn
	 */
	public static final RemoteFolder f_GET_REMOTE_FOLDER_REFERENCE(C_DescRelease descRelease)
	{
		try
		{
			// création du répository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
						
			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			// url repository référence
			SVNUrl url = rootFolder.getUrl().appendPath(descRelease.f_GET_NOM());
			
			return new RemoteFolder(repository, url, SVNRevision.HEAD);	
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Retourne la révision SVN locale du projet.
	 * 
	 * @param projet projet souhaité
	 * @return révision locale
	 */
	public static final long f_GET_REVISION_DU_PROJET(IProject projet)
	{
		try 
		{
			// si gestion par svn
			if(SVNWorkspaceRoot.isManagedBySubclipse(projet))
			{
				String text = C_DefineSVN.CLIENT_SVN_WC.doGetWorkingCopyID(projet.getLocation().toFile(), null);
				
				if(text == null)
				{
					return -1;
				}
				else if(text.contains(":") && text.contains("M"))
				{
					return new Long(text.substring(text.lastIndexOf(":")+1, text.lastIndexOf("M"))).longValue();
				}
				else if(text.contains("M"))
				{
					return new Long(text.substring(0, text.lastIndexOf("M"))).longValue();
				}
				else if(text.contains(":"))
				{
					return new Long(text.substring(text.lastIndexOf(":")+1, text.length()-1)).longValue();
				}
				else
				{
					return new Long(text);
				}
			}
			else return -1;
		} 
		catch (Exception e) 
		{
			// trace
			e.printStackTrace();
			
			return -1;
		}
	}
	
	/**
	 * Retourne la dossier svn du site passé en paramètre.
	 * 
	 * @param descSite descriptif du site
	 * @return dossier distant svn
	 */
	public static final RemoteFolder f_GET_REMOTE_FOLDER_SITE(C_DescDistribution descSite)
	{
		try
		{
			// création du répository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
						
			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			// url repository référence
			SVNUrl url = rootFolder.getUrl().appendPath(descSite.f_GET_NOM_RELEASE());
			
			return new RemoteFolder(repository, url, SVNRevision.HEAD);	
		}
		catch(Exception e)
		{
			return null; // ko
		}
	}
	
	/**
	 * Test le serveur SVN.
	 * 
	 * @return true si accessible
	 */
	public static boolean f_IS_SERVEUR_SVN_REACHABLE()
	{
		try
		{
			// récupération adresse serveur SVN
			String adresseSVN = C_DefinePreferencesPlugin.f_GET_PREFERENCE_AS_STRING(C_DefinePreferencesPlugin.SERVEUR_SVN_ADRESSE);
			
			// adresse INET
			InetAddress adress = InetAddress.getByName(adresseSVN);
			
			Process p1 = java.lang.Runtime.getRuntime().exec("ping " + adresseSVN);
			int returnVal = p1.waitFor();
			boolean reachable = (returnVal==0);
			
			return reachable;
			
//			return adress.isReachable(5000); // ok si accessible ne lmarche depuis migration serveur SVN ?
			
			
		}
		catch(Exception e)
		{
			return false; // ko
		}
	}
	
	/**
	 * Test la présence du repository référence.
	 * 
	 * @return true si présent
	 */
	public static boolean f_IS_REPOSITORY_REFERENCE_EXISTE()
	{
		try
		{
			// création du répository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
			
			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			return rootFolder.exists(new NullProgressMonitor());
		}
		catch(Exception e)
		{
			return false; // ko
		}
	}
	
	/**
	 * Test la présence du repository sites.
	 * 
	 * @return true si présent
	 */	
	public static boolean f_IS_REPOSITORY_SITE_EXISTE()
	{
		try
		{
			// création du répository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_SITE_REPOSITORY());
		
			// root folder sites
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			return rootFolder.exists(new NullProgressMonitor());
		}
		catch(Exception e)
		{
			return false; // ko
		}
	}
	
	public static final ISVNDirEntry[] f_GET_REPERTOIRES_REFERENCE()
	{
		try
		{
			ArrayList<ISVNDirEntry> vec = new ArrayList<ISVNDirEntry>();
			ISVNDirEntry[] resultat;
			
			// création du répository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
			ISVNClientAdapter client = repository.getSVNClient();

			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			// liste des entrées du dossier root distant
			ISVNDirEntry[] list = client.getList(rootFolder.getUrl(), rootFolder.getRevision(), SVNRevision.HEAD, false);
			
			// parcours des entrées...
			for(ISVNDirEntry entry : list)
			{
				String path = entry.getPath();
				
				String[] elements = path.split("_");
				
				if(elements.length == 3)
					vec.add(entry);
			}
			
			// création résultat
			resultat = new ISVNDirEntry[vec.size()];
			vec.toArray(resultat);
			return resultat;
		}
		catch(Exception e)
		{
			// trace
			System.err.println("Problème de récupération répertoire référence");
			
			return null; // ko
		}
	}
	
	public static final String[] f_GET_LISTE_REPERTOIRES_REFERENCE()
	{
		ISVNDirEntry[] entrys = f_GET_REPERTOIRES_REFERENCE();
		
		String [] resultat = new String[entrys.length];
		
		for( int i = 0 ; i < resultat.length ; i++ )
		{
			resultat[i] = entrys[i].getPath();
		}
		
		return resultat;
	}
	
	public static InputStream f_GET_INPUT_STREAM_FICHIER_DISTANT(ISVNDirEntry remoteFolder, String nomFichier)
	{
		try
		{
			// Repository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
			
			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			// remote fichier
			RemoteFile file = (RemoteFile) repository.getRemoteFile(new SVNUrl(rootFolder.getUrl().toString() + "/" + remoteFolder.getPath() + "/" + nomFichier));
			
			return repository.getSVNClient().getContent(file.getUrl(), file.getRevision());
		}
		catch(Exception e)
		{
			System.err.println("Erreur lors de l'accès au fichier distant " + nomFichier);
			return null;
		}
	}
	
	public static InputStream f_GET_INPUT_STREAM_FICHIER_DISTANT(RemoteFolder remoteFolder, String nomFichier)
	{
		try
		{
			// Repository
			SVNRepositoryLocation repository = SVNRepositoryLocation.fromString(C_DefineInfosServeurs.f_GET_SRV_SVN_RELEASE_REPOSITORY());
			
			// root folder références
			ISVNRemoteFolder rootFolder = repository.getRootFolder();
			
			RemoteFile file = (RemoteFile) repository.getRemoteFile(new SVNUrl(rootFolder.getUrl().toString() + "/" + remoteFolder.getRepositoryRelativePath() + "/" + nomFichier));
			
			return repository.getSVNClient().getContent(file.getUrl(), file.getRevision());
		}
		catch(Exception e)
		{
			System.err.println("Erreur lors de l'accès au fichier distant " + nomFichier);
			return null;
		}
	}
	
	/**
	 * Exportation d'un projet sous svn.
	 * 
	 * @param projet projet
	 * @param dest répertoire de destination
	 * @param monitor moniteur de progression
	 * @throws SVNException exception svn
	 */
	public static void f_EXPORTE(IProject projet, File dest, IProgressMonitor monitor) throws SVNException
	{
		try 
		{
			// répertoire projet
			File rep = new File(projet.getLocation().toString());
			
			// ressources
			ISVNLocalResource svnRess = SVNWorkspaceRoot.getSVNResourceFor(projet);
			
			// client SVN
			ISVNClientAdapter client = svnRess.getRepository().getSVNClient();
			
			// destination
			File destPath = new File(dest.getAbsolutePath() + File.separator + rep.getName());
			
//			try 
//			{
				client.doExport(rep, destPath, true);
//			} 
//			catch (SVNClientException e) 
//			{
//				e.printStackTrace();
//			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			monitor.done();
		}    
	}
	
	/**
	 * Exportation d'un dossier d'un projet sous svn.
	 * 
	 * @param projet projet
	 * @param src répertoire source
	 * @param dest répertoire de destination
	 * @param monitor moniteur de progression
	 * @throws SVNException exception svn
	 */
	public static void f_EXPORTE(IProject projet, File src, File dest, IProgressMonitor monitor) throws SVNException
	{
		try 
		{
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.INFO, "SVN", src.getName());
			
			monitor.subTask(src.getName());
			
			// client SVN
			ISVNLocalResource svnRess = SVNWorkspaceRoot.getSVNResourceFor(projet);
			ISVNClientAdapter client = svnRess.getRepository().getSVNClient();
			
//			try 
//			{
//				System.out.println(src + " >> " + dest);
//				
				client.doExport(src, dest, true);
//			} 
//			catch (SVNClientException e) 
//			{
//				C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "SVN", e.getMessage());
//				throw SVNException.wrapException(e);
//			}
		} 
		catch (Exception e) 
		{
			C_ToolsSWT.f_NOTIFICATION(E_NotificationType.ERROR, "SVN", e.getMessage());
//			e.printStackTrace();
		} 
		finally 
		{
//			monitor.done();
		}    
	}
	
	/**
	 * Récupération historique.
	 * 
	 * @param folder dossier source
	 * @param revisionDebut révision de début
	 * @param revisionFin révision de fin
	 * @return messages de log
	 */
	public static ISVNLogMessage[] f_GET_HISTORIQUE(RemoteFolder folder, SVNRevision revisionDebut, SVNRevision revisionFin, int maxLog, String svnUser)
	{
		try
		{
			// resultat
			ArrayList<ISVNLogMessage> logs = new ArrayList<ISVNLogMessage>();
			
			// client
			ISVNClientAdapter client = folder.getRepository().getSVNClient();
			
			ISVNLogMessage[] logsMessages = client.getLogMessages(folder.getUrl(), SVNRevision.HEAD, revisionFin, revisionDebut, false, true, maxLog);
			
			if(svnUser != null)
			{
				for(ISVNLogMessage logMessage : logsMessages)
					if(logMessage.getAuthor().contains(svnUser))
						logs.add(logMessage);
				
				return logs.toArray(new ISVNLogMessage[logs.size()]);
			}
			else
			{
				return logsMessages;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null; // ko
		}
	}
	
	public static SVNRevision f_LONG_TO_SVN_REVISION(long revision)
	{
		try 
		{
			return SVNRevision.getRevision(new Long(revision).toString());
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Retourne les informations SVN du dossier passé en paramètre.
	 * 
	 * @param folder dossier
	 * @return informations SVN
	 */
	public static ISVNInfo f_GET_INFORMATIONS_SVN(ISVNRemoteFolder folder)
	{
		try
		{
			// récupération infos SVN
			ISVNInfo infoSVN = folder.getRepository().getSVNClient().getInfo(folder.getUrl());
			
			return infoSVN;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	/**
	 * Vérification de la présence d'un fichier de projet.
	 * 
	 */
	public static boolean f_CHECK_FOR_PROJECT_FILE(Shell shell, RemoteFolder remoteFolder) 
	{
		try
		{
			// tentative de récupération du projet sur SVN
			SVNWorkspaceRoot.getProject(remoteFolder,null);
			
			return true; // ok
		} 
		catch (InterruptedException e) 
		{
         	return false; // opération annulée
		} 
		catch (Exception e) 
		{
			C_ToolsSWT.f_AFFICHE_MESSAGE_ERREUR(shell, "CheckOut erreur", "Un problème est survenu lors de l'opération de vérification de présence du projet.");
			
			return false; // ko
		}
	}
	
	/**
	 * Check out du project.
	 * 
	 * @return true si succès
	 */
	public static boolean f_CHECK_OUT_PROJET(Shell shell, RemoteFolder remoteFolder, String nomProjet, String path) 
	{
		try 
		{
			// création action
			CheckoutIntoAction checkoutAction = new CheckoutIntoAction(new RemoteFolder[]{remoteFolder}, nomProjet, path, shell);
			
			// préparation action
			checkoutAction.setSvnRevision(SVNRevision.HEAD);
			checkoutAction.setDepth(ISVNCoreConstants.DEPTH_INFINITY);
			checkoutAction.setIgnoreExternals(false);
			checkoutAction.setForce(true);
			
			// éxécution action
			checkoutAction.execute(null);
			
			return true; // ok
		} 
		catch (Exception e)
		{
			C_ToolsSWT.f_AFFICHE_MESSAGE_ERREUR(shell, "CheckOut erreur", "Un problème est survenu lors de l'opération de CheckOut du projet.");
			
			return false; // ko
		}
	}
	
	/**
	 * Affiche l'historique de la référence souhaitée.
	 * 
	 * @param descRelease descriptif release
	 * @return true si succès
	 */
	public static final boolean f_SHOW_HISTORY2(Object objet)
	{
		try
		{
			// Workbench
			final IWorkbench workbench = C_PlanetPluginActivator.f_GET().getWorkbench();
			
			// Active page
			final IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
			
			// vue History
			IHistoryView view = (IHistoryView) activePage.findView(ISVNUIConstants.HISTORY_VIEW_ID);
			
			if(view == null)
			{
				IViewRegistry viewRegistry = workbench.getViewRegistry();
				IViewDescriptor desc = viewRegistry.find(ISVNUIConstants.HISTORY_VIEW_ID);
				IWorkbenchPart part = desc.createView();
				activePage.activate(part);
				final IHistoryView createview = (IHistoryView) part;
				
				activePage.showView(ISVNUIConstants.HISTORY_VIEW_ID);
				createview.showHistoryFor(objet, true);
			}
			else
			{
				activePage.showView(ISVNUIConstants.HISTORY_VIEW_ID);
				view.showHistoryFor(objet, true);
			}
			
			return true; // ok
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return false; // ko
		}
	}
	
	/**
	 * Returns the selected remote resources
	 */
	public static ISVNRemoteResource[] getSelectedRemoteResources(TreeSelection selection) 
	{
		ArrayList<Object> resources = null;
		
		if (selection != null && !selection.isEmpty()) 
		{
			resources = new ArrayList<Object>();
			Iterator elements = selection.iterator();
			
			while (elements.hasNext()) 
			{
				Object next = elements.next();
				
				if (next instanceof ISVNRemoteResource) 
				{
					resources.add(next);
					continue;
				}
				
				if (next instanceof IAdaptable) 
				{
					IAdaptable a = (IAdaptable) next;
					Object adapter = a.getAdapter(ISVNRemoteResource.class);
					if (adapter instanceof ISVNRemoteResource) {
						resources.add(adapter);
						continue;
					}
				}
			}
		}
		if (resources != null && !resources.isEmpty()) {
			ISVNRemoteResource[] result = new ISVNRemoteResource[resources.size()];
			resources.toArray(result);
			
			return result;
		}
		return new ISVNRemoteResource[0];
	}
	
	public static void f_SYNCHRONIZE(IProject projet)
	{
		IResource[] ressources = new IResource[]{projet};
		
		// récupération participation
		SVNSynchronizeParticipant participant = (SVNSynchronizeParticipant)SubscriberParticipant.getMatchingParticipant(SVNSynchronizeParticipant.ID, ressources);
		
		// If there isn't, create one and add to the manager
		if (participant == null)
		{
			participant = new SVNSynchronizeParticipant(new ResourceScope(ressources));
			TeamUI.getSynchronizeManager().addSynchronizeParticipants(new ISynchronizeParticipant[] {participant});
		}
		participant.refresh(new IResource[]{projet}, "Synchronizing", "Synchronizing " + participant.getName(), SVNUIPlugin.getActivePage().getActivePart().getSite());
	}
	
	public static void f_MERGE(IProject projet)
	{
		
		try
		{
			IResource[] ressources = new IResource[]{projet};
		
	        MergeDialog dialog = new MergeDialog(C_ToolsWorkbench.f_GET_SHELL(), ressources[0]);
	       
	        if (dialog.open() == MergeDialog.CANCEL) 
	        	return;
	        
	        SVNUrl svnUrl1 = dialog.getFromUrl();
	        SVNRevision svnRevision1 = dialog.getFromRevision();
	        SVNUrl svnUrl2 = dialog.getToUrl();
	        SVNRevision svnRevision2 = dialog.getToRevision();  
	        MergeOperation mergeOperation = new MergeOperation(null, ressources, svnUrl1, svnRevision1, svnUrl2, svnRevision2);
	        mergeOperation.setForce(dialog.isForce());
	        mergeOperation.setIgnoreAncestry(dialog.isIgnoreAncestry());
	        mergeOperation.run();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
}
