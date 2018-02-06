package org.deltadore.planet.model.define;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNWCClient;

public class C_DefineSVN 
{
	/** Client SVN (spécial Working Copy (SVNKit)**/

	public static SVNWCClient CLIENT_SVN_WC;
	
	/**
	 * Initialisation SVN.
	 * 
	 */
	public static void f_INIT_SVN()
	{
		CLIENT_SVN_WC = new SVNWCClient((ISVNAuthenticationManager)null, (ISVNOptions)null);
	}
	
	/**
	 * Restitution SVN.
	 * 
	 */
	public static void f_RESTIT_SVN()
	{
		CLIENT_SVN_WC = null;
	}
}
