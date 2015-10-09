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

import javax.cim.CIMClassProperty;
import javax.cim.CIMDataType;

import org.sblim.cimclient.unittest.TestCase;

/**
 * Contains CIMClassPropertyTest related tests.
 */
public class CIMClassPropertyTest extends TestCase {

	private static final String REF_NAME = "SampleClassProperty";

	private static final CIMDataType REF_TYPE = CIMDataType.STRING_T;

	private static final String REF_VALUE = "Hello";

	private static final boolean REF_KEY = false, REF_PROPA = false;

	private static final String REF_CLASS_ORIG = "SampleClass";

	private static final CIMClassProperty<String> REF_PROP = new CIMClassProperty<String>(REF_NAME,
			REF_TYPE, REF_VALUE, Common.REF_QUALIS, REF_KEY, REF_PROPA, REF_CLASS_ORIG);

	private void check(CIMClassProperty<String> pProp, boolean pInclQualis, boolean pInclClassOrig,
			boolean pLocalOnly) {
		verify("ClassProperty names don't match!", REF_NAME.equalsIgnoreCase(pProp.getName()));
		verify("ClassProperty types don't match!", REF_TYPE.equals(pProp.getDataType()));
		verify("OriginClasses don't match!", pInclClassOrig ? REF_CLASS_ORIG.equalsIgnoreCase(pProp
				.getOriginClass()) : pProp.getOriginClass() == null);
		verify("Key flags don't match!", REF_KEY == pProp.isKey());
		verify("Propagated flags don't match!", REF_PROPA == pProp.isPropagated());
		String msg = Common.checkQualis(pProp, pInclQualis, pLocalOnly);
		verify(msg, msg == null);
	}

	/**
	 * Tests CIMClassProperty.filter(boolean, boolean).
	 */
	public void testFilter0() {
		for (int i = 0; i < 4; i++) {
			boolean inclQualis = (i & 1) > 0;
			boolean inclClassOrig = (i & 2) > 0;
			CIMClassProperty<String> prop = REF_PROP.filter(inclQualis, inclClassOrig);
			check(prop, inclQualis, inclClassOrig, false);
		}
	}

	/**
	 * Tests CIMClassProperty.filter(boolean, boolean, boolean).
	 */
	public void testFilter1() {
		for (int i = 0; i < 8; i++) {
			boolean localOnly = (i & 1) > 0;
			boolean inclClassOrig = (i & 2) > 0;
			boolean inclQualis = (i & 3) > 0;
			CIMClassProperty<String> prop = REF_PROP.filter(inclQualis, inclClassOrig, localOnly);
			check(prop, inclQualis, inclClassOrig, localOnly);
		}
	}

}
