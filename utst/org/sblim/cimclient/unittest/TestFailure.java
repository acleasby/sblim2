/**
 * (C) Copyright IBM Corp. 2006, 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1565892    2006-11-23  lupusalex    Make SBLIM client JSR48 compliant
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 */
package org.sblim.cimclient.unittest;

/**
 * Class TestFailure is used to signal a failed test. It extends
 * <code>Error</code> so that it isn't caught by standard catch blocks.
 * 
 */
public class TestFailure extends Error {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6677553562714677437L;

	/**
	 * Ctor.
	 * 
	 * @param pMessage
	 *            The message attached to this failure
	 */
	public TestFailure(String pMessage) {
		super(pMessage);
	}
}
