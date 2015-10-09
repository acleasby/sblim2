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
 * @author : Endre Bak, ebak@de.ibm.com  
 * 
 * Flag       Date        Prog         Description
 * -------------------------------------------------------------------------------
 * 1737141    2007-06-19  ebak         Sync up with JSR48 evolution
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.CIMDataType;
import javax.cim.CIMParameter;

import org.sblim.cimclient.unittest.TestCase;

/**
 * CIMParameterTest contains tests for CIMParameter.
 */
public class CIMParameterTest extends TestCase {

	private static final String REF_NAME = "SampleParameter";

	private static final CIMDataType REF_TYPE = CIMDataType.BOOLEAN_T;

	private static final CIMParameter<Object> REF_PARAM = new CIMParameter<Object>(REF_NAME,
			REF_TYPE, Common.REF_QUALIS);

	private void check(CIMParameter<Object> pParam, boolean pInclQualis, boolean pLocalOnly) {
		verify("Parameter names don't match!", REF_NAME.equalsIgnoreCase(pParam.getName()));
		verify("Parameter types don't match!", REF_TYPE.equals(pParam.getDataType()));
		String msg = Common.checkQualis(pParam, pInclQualis, pLocalOnly);
		verify(msg, msg == null);
	}

	/**
	 * Tests CIMParameter.filter(boolean, boolean).
	 */
	public void testFilter() {
		for (int i = 0; i < 4; i++) {
			boolean inclQualis = (i & 2) > 0;
			boolean localOnly = (i & 1) > 0;
			CIMParameter<Object> param = REF_PARAM.filter(inclQualis, localOnly);
			check(param, inclQualis, localOnly);
		}
	}

}
