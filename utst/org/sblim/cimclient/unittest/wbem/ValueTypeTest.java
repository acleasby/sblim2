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
 * 1769504	  2007-08-08  ebak         Type identification for VALUETYPE="numeric"
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;

import javax.cim.CIMDataType;
import javax.cim.CIMDateTimeAbsolute;
import javax.cim.CIMDateTimeInterval;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;
import javax.wbem.CloseableIterator;

import org.sblim.cimclient.unittest.TestCase;

/**
 * Tests the handling of VALUETYPE attribute of KEYVALUE XML element.
 */
public class ValueTypeTest extends TestCase {

	private static CIMProperty<Object> mkProp(String pName, Object pValue) {
		return new CIMProperty<Object>(pName, CIMDataType.getDataType(pValue), pValue, true, false,
				null);
	}

	private static final CIMObjectPath SMP_INST_PATH = Common.LOCALPATH.build("SampleClass", null,
			new CIMProperty[] { mkProp("StringProp", "Hello"),
					mkProp("DTAbsoluteProp", new CIMDateTimeAbsolute("20070314160503.566012+010")),
					mkProp("DTIntervalProp", new CIMDateTimeInterval("00000133102418.******:000")),
					mkProp("UInt64Prop", new UnsignedInteger64("112233445566778899")),
					mkProp("SInt64Prop", new Long(-20)), mkProp("Real64Prop", new Double(3.14)),
					mkProp("BooleanProp", Boolean.FALSE) });

	private InputStream getIS(String pName) {
		InputStream is = getClass().getResourceAsStream(pName);
		verify("Failed to load resource! : " + pName, is != null);
		return is;
	}

	private static final String XML_SOURCE = "data/ValueTypeEnumInstanceNames.xml";

	private void test(CloseableIterator<Object> pItr) {
		verify("Empty Iterator!", pItr.hasNext());
		CIMObjectPath path = (CIMObjectPath) pItr.next();
		verify("SMP_INST_PATH != path!\n" + "SMP_INST_PATH : " + SMP_INST_PATH
				+ "\npath          : " + path + '\n', SMP_INST_PATH.equals(path));
	}

	/**
	 * testSAX
	 * 
	 * @throws Exception
	 */
	public void testSAX() throws Exception {
		test(Common.parseWithSAX(getIS(XML_SOURCE)));

	}

	/**
	 * testPULL
	 * 
	 * @throws Exception
	 */
	public void testPULL() throws Exception {
		test(Common.parseWithPULL(getIS(XML_SOURCE)));
	}

	/**
	 * testDOMX
	 * 
	 * @throws Exception
	 */
	public void testDOMX() throws Exception {
		test(Common.parseWithDOM(getIS(XML_SOURCE)));
	}

}
