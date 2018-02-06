package org.deltadore.planet.swt;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import org.deltadore.planet.model.define.C_DefineCouleur;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class C_ImageGridPreview extends ScrolledComposite implements MouseListener
{
	private static int		TAILLE_PREFFERREE = 30;
	
	private File 			m_repertoire;
	
	private Composite		c_contenu;
	
	private String			m_str_filtre;
	
	private Cursor			m_cursor;
	
	public C_ImageGridPreview(Composite parent)
	{
		super(parent, SWT.V_SCROLL);
		
		// initialisation
		f_INIT();
		
		// initialisation interface
		f_INIT_UI();
		
		// mise à jour interface
		f_UPDATE_UI();
	}
	
	/**
	 * Initialisation.
	 * 
	 */
	private void f_INIT()
	{
		// curseur main
		m_cursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
	}
	
	/**
	 * Initialisation interface.
	 * 
	 */
	private void f_INIT_UI()
	{
		// occupe l'espace horizontal
		setExpandHorizontal(true);
		getVerticalBar().setIncrement(40);
		
		// création contenu
		c_contenu = new Composite(this, SWT.NONE);
		setContent(c_contenu);
	}
	
	/**
	 * Affectation d'un filtre sur nom image.
	 * 
	 * @param filtre filtre sur nom image
	 * @return true si succès
	 */
	public boolean f_SET_FILTRE(String filtre)
	{
		m_str_filtre = filtre;
		
		// mise à jour interface
		f_UPDATE_UI();
		
		return true; // ok
	}
	
	public boolean f_RAZ()
	{
		// accès interface par thread
		getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				// raz des controls
				C_ToolsSWT.f_RAZ_CONTROLS(c_contenu);
				
				// composite message
				C_LabelIconeEtTexte compositeMessage = new C_LabelIconeEtTexte(c_contenu, false);
				c_contenu.setLayout(new FillLayout());
				compositeMessage.f_SET_ICONE_ET_TEXTE(C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("refresh_pp.png"), "Actualisez la vue");
				compositeMessage.f_SET_COULEUR_POLICE(C_DefineCouleur.f_GET_COULEUR(80, 140, 200));
//				C_ToolsSWT.f_GRIDLAYOUT_DATA(compositeMessage, 1, 1, true, true, GridData.FILL, GridData.FILL);
				setExpandVertical(true);
				
				// mise à jour du scroll
				f_UPDATE_SCROLL_CONTENT();
			}
		});
		
		return true;
	}
	
	public void f_UPDATE_UI()
	{
		final MouseListener l = this;
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// sécurité
				if(m_repertoire != null 
				&& m_repertoire.exists() && m_repertoire.isDirectory())
				{
					// filtre les images
					final File[] images = m_repertoire.listFiles(new FileFilter() 
					{
						@Override
						public boolean accept(File file) 
						{
							if(file.isFile() && file.getName().contains(".png") 
							&& (m_str_filtre == null || m_str_filtre.length() == 0 || file.getName().contains(m_str_filtre)))
								return true;
							else
								return false;
						}
					});
					
					// accès interface par thread
					getDisplay().asyncExec(new Runnable() 
					{
						@Override
						public void run() 
						{
							try
							{
								// raz des controls
								C_ToolsSWT.f_RAZ_CONTROLS(c_contenu);
								
								setExpandVertical(false);
								
								GridLayout layout = new GridLayout((int)( (getSize().x - 10) / (float) TAILLE_PREFFERREE), true);
								layout.horizontalSpacing = 1;
								layout.verticalSpacing = 1;
								layout.marginWidth = 1;
								layout.marginRight = 1;
								
								c_contenu.setLayout(layout);
								
								// parcours des images...
								for(File f : images)
								{
									// création label
									Label label = new Label(c_contenu, SWT.NONE);
									label.setSize(TAILLE_PREFFERREE, TAILLE_PREFFERREE);
									label.setCursor(m_cursor);
									label.addMouseListener(l);
									label.setToolTipText(f.getName());
									label.setData(f);
									// récupération image
									Image img = new Image(getDisplay(), new FileInputStream(f));
									
									// scale image si necessaire
									img = C_ToolsSWT.f_SCALE_IMAGE_TO_SIZE(img, TAILLE_PREFFERREE); 
									
									// affectation image
									label.setImage(img);
									
									// layout
									C_ToolsSWT.f_GRIDLAYOUT_DATA(label, 1, 1, true, true, GridData.CENTER, GridData.CENTER);
								}
								
								// mise à jour du scroll
								f_UPDATE_SCROLL_CONTENT();
									
							}
							catch(Exception e)
							{
								// trace
								e.printStackTrace();
							}
						}
					});
				}
			}
		}).start();
	}
	
//	/**
//	 * Mise à jour interface.
//	 * 
//	 */
//	public void f_UPDATE_UI()
//	{
//		final MouseListener l = this;
//		
//		new Thread(new Runnable() 
//		{
//			@Override
//			public void run() 
//			{
//				// sécurité
//				if(m_repertoire != null 
//				&& m_repertoire.exists() && m_repertoire.isDirectory())
//				{
//					// filtre les images
//					final File[] images = m_repertoire.listFiles(new FileFilter() 
//					{
//						@Override
//						public boolean accept(File file) 
//						{
//							if(file.isFile() && file.getName().contains(".png") 
//							&& (m_str_filtre == null || m_str_filtre.length() == 0 || file.getName().contains(m_str_filtre)))
//								return true;
//							else
//								return false;
//						}
//					});
//					
//					// accès interface par thread
//					getDisplay().asyncExec(new Runnable() 
//					{
//						@Override
//						public void run() 
//						{
//							try
//							{
//								// raz des controls
//								C_ToolsSWT.f_RAZ_CONTROLS(c_contenu);
//								
//								setExpandVertical(false);
//								
//								GridLayout layout = new GridLayout((int)( (getSize().x - 10) / (float) TAILLE_PREFFERREE), true);
//								layout.horizontalSpacing = 1;
//								layout.verticalSpacing = 1;
//								layout.marginWidth = 1;
//								layout.marginRight = 1;
//								
//								c_contenu.setLayout(layout);
//							}
//							catch(Exception e)
//							{
//								// trace
//								e.printStackTrace();
//							}
//						}
//					});
//					
//					File[][] paquets = f_GET_PAQUETS(images, 1000);
//		
//					for(int i = 0 ; i < paquets.length ; i++ )
//					{
//						final File[] paquet = paquets[i];
//					
//						// accès interface par thread
//						getDisplay().syncExec(new Runnable() 
//						{
//							@Override
//							public void run() 
//							{
//								try
//								{
//									// parcours des images...
//									for(File f : paquet)
//									{
//										if(f == null)
//											break;
//										
//										// création label
//										Label label = new Label(c_contenu, SWT.NONE);
//										label.setSize(TAILLE_PREFFERREE, TAILLE_PREFFERREE);
//										label.setCursor(m_cursor);
//										label.addMouseListener(l);
//										label.setToolTipText(f.getName());
//										label.setData(f);
//										// récupération image
//										Image img = new Image(getDisplay(), new FileInputStream(f));
//										
//										// scale image si necessaire
//										img = C_ToolsSWT.f_SCALE_IMAGE_TO_SIZE(img, TAILLE_PREFFERREE); 
//										
//										// affectation image
//										label.setImage(img);
//										
//										// layout
//										C_ToolsSWT.f_GRIDLAYOUT_DATA(label, 1, 1, true, true, GridData.CENTER, GridData.CENTER);
//									}
//									
//									// mise à jour du scroll
//									f_UPDATE_SCROLL_CONTENT();
//									
////									// accès interface par thread
////									getDisplay().asyncExec(new Runnable() 
////									{
////										@Override
////										public void run() 
////										{
//////											 mise à jour du scroll
////											f_UPDATE_SCROLL_CONTENT();
////										}
////									});
//								}
//								catch(Exception e)
//								{
//									// trace
//									e.printStackTrace();
//								}
//							}
//						});
//					}
//					
////					// accès interface par thread
////					getDisplay().asyncExec(new Runnable() 
////					{
////						@Override
////						public void run() 
////						{
//////							 mise à jour du scroll
//////							f_UPDATE_SCROLL_CONTENT();
////						}
////					});
//				}
//			}
//		}).start();
//	}
	
	public File[][] f_GET_PAQUETS(File[] initial, int capacite)
	{
		int numPaquet = 0;
		int cpt = 0;
		
		File[][] result = new File[initial.length%capacite][capacite];
		
		for(File f : initial)
		{
			if(cpt == capacite)
			{
				cpt = 0;
				numPaquet++;
			}
			
			result[numPaquet][cpt] = f;
			
			cpt++;
		}
		
		return result;
	}
	
	public boolean f_SET_REPERTOIRE_IMAGE(File repertoire)
	{
		if(repertoire == null 
		&& m_repertoire != null)
		{
			m_repertoire = null;
			
			return true; // ok
		}
		else if(m_repertoire == null 
		|| !repertoire.getAbsolutePath().equalsIgnoreCase(m_repertoire.getAbsolutePath()))
		{
			m_repertoire = repertoire;
			
			return true; // ok
		}
		else return false; // ko
	}
	
	/**
	 * Miuse à jour du contenu du scroll.
	 * 
	 */
	private void f_UPDATE_SCROLL_CONTENT()
	{
		Point size = c_contenu.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		c_contenu.setSize(size);
		layout();
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDown(MouseEvent e)
	{		
		// label source de l'événement
		Label src = (Label) e.getSource();
	
		// fichier associé
		File fichier = (File) src.getData();
	
		// clic gauche
		if(e.button == 1)
			C_ToolsWorkbench.f_OPEN_FILE_IN_EDITOR(fichier);
		else if(e.button == 2)
			C_ToolsWorkbench.f_SET_SIMPLE_CLIPBOARD_TEXTE_CONTENT(getDisplay(), fichier.getName());
		else if(e.button == 3)
			C_ToolsWorkbench.f_INSERT_INTO_TEXT_EDITOR("C_GestArbor.f_GET_IMAGE(\"" + fichier.getName() + "\")");
	}

	@Override
	public void mouseUp(MouseEvent e) 
	{
		
	}
}
