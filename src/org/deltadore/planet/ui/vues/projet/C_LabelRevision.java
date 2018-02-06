package org.deltadore.planet.ui.vues.projet;

import org.deltadore.planet.model.define.C_DefineImages;
import org.deltadore.planet.model.descriptifs.C_DescRelease;
import org.deltadore.planet.plugin.jobs.C_JobPeekRevisionSVNWorkingCopy;
import org.deltadore.planet.swt.I_EcouteurActions;
import org.deltadore.planet.tools.C_ToolsRelease;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class C_LabelRevision extends Composite implements MouseListener, IJobChangeListener
{
	/** Projet **/
	private IProject			m_projet;
	
	/** Descriptif release **/
	private C_DescRelease 		m_descRelease;
	
	/** Num�ro de r�vision **/
	private long				m_lg_revision;
	
	/** Ecouteur **/
	private I_EcouteurActions	m_ecouteur;
	
	/** Icone **/
	private Label 				c_labelIcone;
	
	/** Version **/
	private Label 				c_labelRevision;
	
	/**
	 * Constructeur.
	 * 
	 * @param parent
	 */
	public C_LabelRevision(Composite parent)
	{
		super(parent, SWT.NONE);

		// initialisation interface
		f_INIT_UI();
	}
	
	/**
	 * Ajoute un �couteur.
	 * 
	 * @param ecouteur �couteur
	 * @return true si succ�s
	 */
	public boolean f_AJOUTE_ECOUTEUR(I_EcouteurActions ecouteur)
	{
		m_ecouteur = ecouteur;
		
		return true;
	}
	
	/**
	 * Affectation d'un projet.
	 * 
	 * @param projet projet associ�
	 * @return true si succ�s
	 */
	public boolean f_SET_PROJET(IProject projet)
	{
		m_projet = projet;
		m_descRelease = C_ToolsRelease.f_CHARGEMENT_DESCRIPTIF_RELEASE_FROM_PROJECT(m_projet);
		
		c_labelRevision.setText("Rev. - - - - - - -");
		c_labelRevision.pack();
		c_labelIcone.setImage(C_DefineImages.REVISION_INCONNUE);
		
		return true;
	}
	
	/**
	 * Initialisation interface.
	 * 
	 */
	private void f_INIT_UI()
	{
		// layout
		setLayout(new GridLayout(2, false));
		setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// font
		Font font = new Font(getDisplay(), "Arial", 10, SWT.BOLD);
		
		// icone
		c_labelIcone = new Label(this, SWT.NONE);
		c_labelIcone.setImage(C_DefineImages.REVISION_INCONNUE);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelIcone, 1, 1, false, false, GridData.FILL, GridData.FILL);
		
		// version
		c_labelRevision = new Label(this, SWT.NONE);
		c_labelRevision.setText("Rev. - - - - - - -");
		c_labelRevision.setFont(font);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_labelRevision, 1, 1, true, false, GridData.FILL, GridData.CENTER);
	}
	
	public boolean f_UPDATE_ON_CLIC()
	{
		addMouseListener(this);
		c_labelIcone.addMouseListener(this);
		c_labelRevision.addMouseListener(this);
		
		return true;
	}
	
	/**
	 * Mise � jour des informations de r�vision.
	 * 
	 * @return
	 */
	public boolean f_UPDATE_INFORMATIONS()
	{
		final C_LabelRevision thisLabel = this;
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// s�curit�
				if(m_projet != null)
				{
					// acc�s interface par thread
					getDisplay().asyncExec(new Runnable() 
					{
						@Override
						public void run() 
						{
							c_labelIcone.setImage(C_DefineImages.REVISION_INCONNUE);
							c_labelRevision.setText("Rev. - - - - - - -");
							c_labelRevision.pack();
						}
					});
					
					C_JobPeekRevisionSVNWorkingCopy jobPeek = new C_JobPeekRevisionSVNWorkingCopy(m_projet, m_descRelease);
					jobPeek.addJobChangeListener(thisLabel);
					jobPeek.schedule();
				}
			}
		}).start();
		
		return true;
	}
	
	/**
	 * D�finit la r�vision.
	 * 
	 * @param revision r�vision
	 * @return true si succ�s
	 */
	private boolean f_SET_REVISION(long revisionLocale, long revisionServeur)
	{
		m_lg_revision = revisionLocale;
		
		c_labelRevision.setText("Rev. " + m_lg_revision);
		c_labelRevision.pack();
		
		if(revisionServeur != -1)
		{
			if(revisionServeur > revisionLocale)
			{
				c_labelIcone.setImage(C_DefineImages.REVISION_OBSOLETE);
				
				if(m_ecouteur != null)
					m_ecouteur.f_ACTION(this, "REVISION_OBSOLETE");
			}
			else
			{
				c_labelIcone.setImage(C_DefineImages.REVISION_OK);
				
				if(m_ecouteur != null)
					m_ecouteur.f_ACTION(this, "REVISION_OK");
			}
		}
		else
		{
			m_ecouteur.f_ACTION(this, "REVISION_OK");
		}
		
		
		return true; // ok
	}
	
	/**
	 * D�finit la r�vision en chargement.
	 * 
	 * @return true si succ�s
	 */
	public boolean f_SET_REVISION_CHARGEMENT()
	{
		c_labelIcone.setImage(C_DefineImages.REVISION_INCONNUE);
		c_labelRevision.setText("Rev.   ?   ");
		c_labelRevision.pack();
		
		layout();
		
		return true; // ok
	}
	
	/**
	 * Retourne le num�ro de r�vision.
	 * 
	 * @return num�ro de r�vision
	 */
	public long f_GET_REVISION()
	{
		return m_lg_revision;
	}
	
	@Override
	public void mouseDown(MouseEvent arg0) 
	{
		// mise � jour des informations
		f_UPDATE_INFORMATIONS();
	}

	// inutilis�s
	@Override
	public void mouseDoubleClick(MouseEvent arg0) {}
	@Override
	public void mouseUp(MouseEvent arg0) {}

	@Override
	public void aboutToRun(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awake(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void done(IJobChangeEvent jobEvent) 
	{
		final C_JobPeekRevisionSVNWorkingCopy job = (C_JobPeekRevisionSVNWorkingCopy) jobEvent.getJob();
		
		// acc�s interface par thread
		getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				if(!isDisposed())
					f_SET_REVISION(job.f_GET_REVISION_LOCALE(), job.f_GET_INFO_SVN().m_revision);
			}
		});
	}

	@Override
	public void running(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduled(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sleeping(IJobChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
