/**
 * (C) Copyright IBM Corp. 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Dave Blaschke, blaschke@us.ibm.com  
 * 
 * Flag       Date        Prog         Description
 * ------------------------------------------------------------------------
 * 2834838    2009-08-11  blaschke-oss Add interface to retrieve version number and product name	
 */

package org.sblim.cimclient.unittest.cim;

import org.sblim.cimclient.internal.cim.CIMVersion;
import org.sblim.cimclient.unittest.TestCase;

/**
 * 
 * Class CIMVersionTest is responsible for testing CIMVersion
 * 
 */
public class CIMVersionTest extends TestCase {

	private static final String REF_NAME = "SBLIM";

	private static final String REF_COPYRIGHT = "IBM";

	private void check(String pName, String pCopyright) {
		verify("Product name does not contain " + REF_NAME + "!", pName.contains(REF_NAME));
		verify("Copyright does not contain " + REF_COPYRIGHT + "!", pCopyright
				.contains(REF_COPYRIGHT));
	}

	/**
	 * Tests CIMParameter.filter(boolean, boolean).
	 */
	public void testFilter() {
		System.out.println("Running unit test on version " + CIMVersion.getVersion() + " of "
				+ CIMVersion.getProductName() + ", built at " + CIMVersion.getBuildTime() + " on "
				+ CIMVersion.getBuildDate());
		check(CIMVersion.getProductName(), CIMVersion.getCopyright());
	}

}
