/**
 * (C) Copyright IBM Corp. 2013
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Endre Bak, ebak@de.ibm.com  
 *           Dave Blaschke, blaschke@us.ibm.com
 * 
 * Flag       Date        Prog         Description
 * -------------------------------------------------------------------------------
 *    2696    2013-10-29  blaschke-oss parseIRETURNVALUE ignores VALUE and VALUE.ARRAY
 *    2715    2013-11-26  blaschke-oss Add VALUE.NULL support
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;

import org.sblim.cimclient.unittest.TestCase;

/**
 * Tests the handling of VALUETYPE attribute of KEYVALUE XML element.
 */
public class IReturnValueTest extends TestCase {

	private static final String TEST_STR = "DEB";

	private static final String VALUE_STR = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<CIM CIMVERSION=\"2.0\" DTDVERSION=\"2.0\">"
			+ "<MESSAGE ID=\"7\" PROTOCOLVERSION=\"1.0\">" + "<SIMPLERSP>"
			+ "<IMETHODRESPONSE NAME=\"EnumerateInstanceNames\">" + "<IRETURNVALUE>" + "<VALUE>"
			+ TEST_STR + "</VALUE>" + "</IRETURNVALUE>" + "</IMETHODRESPONSE>" + "</SIMPLERSP>"
			+ "</MESSAGE>" + "</CIM>";

	private static final String VALUEARRAY_STR = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<CIM CIMVERSION=\"2.0\" DTDVERSION=\"2.0\">"
			+ "<MESSAGE ID=\"7\" PROTOCOLVERSION=\"1.0\">" + "<SIMPLERSP>"
			+ "<IMETHODRESPONSE NAME=\"EnumerateInstanceNames\">" + "<IRETURNVALUE>"
			+ "<VALUE.ARRAY>" + "<VALUE>" + TEST_STR + "</VALUE>" + "</VALUE.ARRAY>"
			+ "</IRETURNVALUE>" + "</IMETHODRESPONSE>" + "</SIMPLERSP>" + "</MESSAGE>" + "</CIM>";

	private static final String VALUEREFERENCE_STR = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<CIM CIMVERSION=\"2.0\" DTDVERSION=\"2.0\">"
			+ "<MESSAGE ID=\"7\" PROTOCOLVERSION=\"1.0\">" + "<SIMPLERSP>"
			+ "<IMETHODRESPONSE NAME=\"EnumerateInstanceNames\">" + "<IRETURNVALUE>"
			+ "<VALUE.REFERENCE>" + "<CLASSNAME NAME=\"" + TEST_STR + "\"/>" + "</VALUE.REFERENCE>"
			+ "</IRETURNVALUE>" + "</IMETHODRESPONSE>" + "</SIMPLERSP>" + "</MESSAGE>" + "</CIM>";

	private static final String VALUEARRAYDETAIL_STR = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<CIM CIMVERSION=\"2.0\" DTDVERSION=\"2.0\">"
			+ "<MESSAGE ID=\"7\" PROTOCOLVERSION=\"1.0\">" + "<SIMPLERSP>"
			+ "<IMETHODRESPONSE NAME=\"EnumerateInstanceNames\">" + "<IRETURNVALUE>"
			+ "<VALUE.ARRAY>" + "<VALUE>abc</VALUE>" + "<VALUE></VALUE>" + "<VALUE.NULL/>"
			+ "<VALUE>def</VALUE>" + "</VALUE.ARRAY>" + "</IRETURNVALUE>" + "</IMETHODRESPONSE>"
			+ "</SIMPLERSP>" + "</MESSAGE>" + "</CIM>";

	private static final String ValueArrayDetail[] = { "abc", "", null, "def" };

	private InputStream getIS(String pXML) {
		return new ByteArrayInputStream(pXML.getBytes());
	}

	private void testValue(CloseableIterator<Object> pItr) {
		verify("Empty Iterator!", pItr.hasNext());
		Object o = pItr.next();
		verify("Return value not String!", o instanceof String);
		String s = (String) o;
		verify("Unexpected return value! " + TEST_STR + "!=" + s, TEST_STR.equalsIgnoreCase(s));
	}

	private void testValueArray(CloseableIterator<Object> pItr) {
		verify("Empty Iterator!", pItr.hasNext());
		Object o = pItr.next();
		verify("Return value not String[]!", o instanceof String[]);
		String s[] = (String[]) o;
		verify("Unexpected return value! " + TEST_STR + "!=" + s[0], TEST_STR
				.equalsIgnoreCase(s[0]));
	}

	private void testValueReference(CloseableIterator<Object> pItr) {
		verify("Empty Iterator!", pItr.hasNext());
		Object o = pItr.next();
		verify("Return value not String!", o instanceof CIMObjectPath);
		CIMObjectPath cop = (CIMObjectPath) o;
		verify("Unexpected return value! " + TEST_STR + "!=" + cop.getObjectName(), TEST_STR
				.equalsIgnoreCase(cop.getObjectName()));
	}

	private void testValueArrayDetail(CloseableIterator<Object> pItr) {
		verify("Empty Iterator!", pItr.hasNext());
		Object o = pItr.next();
		verify("Return value not String[]!", o instanceof String[]);
		String s[] = (String[]) o;
		verify("Unexpected String[] length! " + s.length + "!=" + ValueArrayDetail.length,
				s.length == ValueArrayDetail.length);
		for (int i = 0; i < s.length; i++)
			verify("Unexpected return value[" + i + "]! " + ValueArrayDetail[i] + "!=" + s[i],
					ValueArrayDetail[i] == null ? s[i] == null : ValueArrayDetail[i]
							.equalsIgnoreCase(s[i]));
	}

	/**
	 * testSAX
	 * 
	 * @throws Exception
	 */
	public void testSAX() throws Exception {
		testValue(Common.parseWithSAX(getIS(VALUE_STR)));
		testValueArray(Common.parseWithSAX(getIS(VALUEARRAY_STR)));
		testValueReference(Common.parseWithSAX(getIS(VALUEREFERENCE_STR)));
		testValueArrayDetail(Common.parseWithSAX(getIS(VALUEARRAYDETAIL_STR)));
	}

	/**
	 * testPULL
	 * 
	 * @throws Exception
	 */
	public void testPULL() throws Exception {
		testValue(Common.parseWithPULL(getIS(VALUE_STR)));
		testValueArray(Common.parseWithPULL(getIS(VALUEARRAY_STR)));
		testValueReference(Common.parseWithPULL(getIS(VALUEREFERENCE_STR)));
		testValueArrayDetail(Common.parseWithPULL(getIS(VALUEARRAYDETAIL_STR)));
	}

	/**
	 * testDOM
	 * 
	 * @throws Exception
	 */
	public void testDOM() throws Exception {
		testValue(Common.parseWithDOM(getIS(VALUE_STR)));
		testValueArray(Common.parseWithDOM(getIS(VALUEARRAY_STR)));
		testValueReference(Common.parseWithDOM(getIS(VALUEREFERENCE_STR)));
		testValueArrayDetail(Common.parseWithDOM(getIS(VALUEARRAYDETAIL_STR)));
	}

}
