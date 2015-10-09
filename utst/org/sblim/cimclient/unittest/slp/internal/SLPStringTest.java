/**
 * (C) Copyright IBM Corp. 2007, 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Endre Bak, IBM, ebak@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1804402    2007-09-28  ebak         IPv6 ready SLP
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 */

package org.sblim.cimclient.unittest.slp.internal;

import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.internal.SLPString;

/**
 * SLPStringTest
 * 
 */
public class SLPStringTest extends TestCase {

	private static final String UNIFIED = "hi, this is joe!";

	private static final String[] STRS = { " Hi,  this is Joe! ", "Hi, this is Joe!",
			"Hi, this is Joe!   ", UNIFIED };

	/**
	 * testUnify
	 */
	public void testUnify() {
		for (int i = 0; i < STRS.length; i++) {
			String unified = SLPString.unify(STRS[i]);
			verify("unify(\"\") != \"" + unified + "\"", UNIFIED.equals(unified));
		}
	}

}
