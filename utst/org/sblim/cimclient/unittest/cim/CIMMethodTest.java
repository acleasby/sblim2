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
 * 1737141    2007-06-18  ebak         Sync up with JSR48 evolution
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.CIMDataType;
import javax.cim.CIMMethod;
import javax.cim.CIMParameter;
import javax.cim.CIMQualifier;

import org.sblim.cimclient.unittest.TestCase;

/**
 * CIMMethodTest contains tests related to CIMMethod.
 */
public class CIMMethodTest extends TestCase {

	private static final String REF_NAME = "SampleMethod";

	private static final CIMDataType REF_TYPE = CIMDataType.SINT32_T;

	private static final CIMParameter<?>[] REF_PARAMS = new CIMParameter[] { new CIMParameter<Object>(
			"Param", CIMDataType.STRING_T, new CIMQualifier<?>[] { new CIMQualifier<Boolean>("IN",
					CIMDataType.BOOLEAN_T, Boolean.TRUE, 0) }) };

	private static final boolean REF_PROPA = false;

	private static final String REF_CLASS_ORIG = "SampleClass";

	private static final CIMMethod<Object> REF_METHOD = new CIMMethod<Object>(REF_NAME, REF_TYPE,
			Common.REF_QUALIS, REF_PARAMS, REF_PROPA, REF_CLASS_ORIG);

	private void check(CIMMethod<Object> pMethod, boolean pInclQualis, boolean pInclClassOrig,
			boolean pLocalOnly) {
		verify("Method names don't match!", REF_NAME.equalsIgnoreCase(pMethod.getName()));
		verify("Method types don't match!", REF_TYPE.equals(pMethod.getDataType()));
		verify("OriginClasses don't match!", pInclClassOrig ? REF_CLASS_ORIG
				.equalsIgnoreCase(pMethod.getOriginClass()) : pMethod.getOriginClass() == null);
		verify("Propagated flags don't match!", REF_PROPA == pMethod.isPropagated());
		String msg = Common.checkQualis(pMethod, pInclQualis, pLocalOnly);
		verify(msg, msg == null);
	}

	/**
	 * Tests CIMMethod.filter(boolean, boolean).
	 */
	public void testFilter0() {
		for (int i = 0; i < 4; i++) {
			boolean inclQualis = (i & 1) > 0;
			boolean inclClassOrig = (i & 2) > 0;
			CIMMethod<Object> method = REF_METHOD.filter(inclQualis, inclClassOrig);
			check(method, inclQualis, inclClassOrig, false);
		}
	}

	/**
	 * Tests CIMMethod.filter(boolean, boolean, boolean).
	 */
	public void testFilter1() {
		for (int i = 0; i < 8; i++) {
			boolean localOnly = (i & 1) > 0;
			boolean inclClassOrig = (i & 2) > 0;
			boolean inclQualis = (i & 3) > 0;
			CIMMethod<Object> method = REF_METHOD.filter(inclQualis, inclClassOrig, localOnly);
			check(method, inclQualis, inclClassOrig, localOnly);
		}
	}

}
