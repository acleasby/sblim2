/**
 * (C) Copyright IBM Corp. 2007, 2013
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
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 *    2650    2013-07-18  blaschke-oss SLP opaque value handling incorrect
 */

package org.sblim.cimclient.unittest.slp.internal;

import java.util.Vector;

import org.sblim.cimclient.unittest.GenericUTestExts;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.ServiceLocationAttribute;
import org.sblim.slp.ServiceLocationException;
import org.sblim.slp.internal.AttributeHandler;
import org.sblim.slp.internal.Convert;

/**
 * ConvertTest
 * 
 */
public class ConvertTest extends TestCase {

	private static final String[] RAW_STRINGS = { "(hi,joe)", "hello~(hi,joe!) world" };

	private static final String[] ESCAPED_STRINGS = { "\\28hi\\2Cjoe\\29",
			"hello\\7E\\28hi\\2Cjoe\\21\\29 world" };

	/**
	 * testEscaping
	 * 
	 * @throws Exception
	 */
	public void testEscaping() throws Exception {
		for (int i = 0; i < RAW_STRINGS.length; i++) {
			String rawStr = RAW_STRINGS[i];
			String escapedStr = Convert.escape(rawStr);
			String refEscapedStr = ESCAPED_STRINGS[i];
			verify("Escaping failure!\n\"" + rawStr + "\" -> \"" + escapedStr + "\" != \""
					+ refEscapedStr + " !", refEscapedStr.equals(escapedStr));
			String unescapedStr = Convert.unescape(escapedStr);
			verify("Unescaping failure!\n\"" + escapedStr + "\" -> \"" + unescapedStr + "\" != \""
					+ rawStr + " !", rawStr.equals(unescapedStr));
		}
	}

	private static final Object[][] ATTR_VALS_ARRAY = {
			null,
			new Object[] { "String (o) Value", Boolean.TRUE, Boolean.FALSE,
					new byte[] { 0x20, 0x15, 0x33, 0x5a }, new Integer(42) },
			new Object[] { new byte[] { (byte) 0xff, (byte) 0xef, 0x5a }, "Hello*!" },
			new Object[] { "Just a single string.(,)" } };

	private static Vector<Object> getAttrValues(int pIdx) {
		return GenericUTestExts.mkVec(ATTR_VALS_ARRAY[pIdx]);
	}

	private static ServiceLocationAttribute getAttrib(int pIdx) {
		return new ServiceLocationAttribute("(A)ttrib" + pIdx, getAttrValues(pIdx));
	}

	/**
	 * testAttribStringHandling
	 * 
	 * @throws Exception
	 */
	public void testAttribStringHandling() throws Exception {
		for (int i = 0; i < ATTR_VALS_ARRAY.length; i++) {
			ServiceLocationAttribute refAttrib = getAttrib(i);
			String refAttrStr = AttributeHandler.buildString(refAttrib);
			debug(refAttrStr);
			ServiceLocationAttribute attrib = new ServiceLocationAttribute(refAttrStr);
			String attrStr = AttributeHandler.buildString(attrib);
			debug(attrStr);
			verify("Rebuilt attribute doesn't equal the reference one!", refAttrib.equals(attrib));
			verify("Rebuilt attribute string doesn't equal the original one!", refAttrStr
					.equals(attrStr));
		}
	}

	private static final String[] ATTR_TAG = { //
	"attrib-integer1", // 0
			"attrib-integer2", // 1
			"attrib-integer3", // 2
			"attrib-boolean1", // 3
			"attrib-boolean2", // 4
			"attrib-opaque1", // 5
			"attrib-opaque2", // 6
			"attrib-opaque3", // 7
			"attrib-opaque4", // 8
			"attrib-opaque5", // 9
			"attrib-opaque6", // 10
	};

	private static final Object[][] ATTR_VAL = {//
	new Object[] { new Integer(42) }, // 0
			new Object[] { new Integer(12), new Integer(34) }, // 1
			new Object[] { new Integer(-86) }, // 2
			new Object[] { Boolean.TRUE }, // 3
			new Object[] { Boolean.TRUE, Boolean.FALSE }, // 4
			new Object[] { new byte[] { 0x00 } }, // 5
			new Object[] { new byte[] { 0x01, 0x02 } }, // 6
			new Object[] { new byte[] { 0x03 }, new byte[] { 0x04 } }, // 7
			new Object[] { new byte[] { 5, 6 }, new byte[] { 7, 8 } }, // 8
			new Object[] { new byte[] { 62, 63, 64, 65, 66 } }, // 9
			new Object[] { new byte[] { -34, -79 } }, // 10
	};

	private static final String[] ATTR_EXP = {//
	"(attrib-integer1=42)", // 0
			"(attrib-integer2=12,34)", // 1
			"(attrib-integer3=-86)", // 2
			"(attrib-boolean1=true)", // 3
			"(attrib-boolean2=true,false)", // 4
			"(attrib-opaque1=\\FF\\00)", // 5
			"(attrib-opaque2=\\FF\\01\\02)", // 6
			"(attrib-opaque3=\\FF\\03,\\FF\\04)", // 7
			"(attrib-opaque4=\\FF\\05\\06,\\FF\\07\\08)", // 8
			"(attrib-opaque5=\\FF\\3E\\3F\\40\\41\\42)", // 9
			"(attrib-opaque6=\\FF\\DE\\B1)", // 10
	};

	/**
	 * testValidAttribs
	 * 
	 * @throws Exception
	 */
	public void testValidAttribs() throws Exception {
		verify("testValidAttribs arrays not of same length!", (ATTR_TAG.length == ATTR_VAL.length)
				&& (ATTR_TAG.length == ATTR_EXP.length));

		for (int i = 0; i < ATTR_TAG.length; i++) {
			String tag = ATTR_TAG[i];
			Vector<Object> val = GenericUTestExts.mkVec(ATTR_VAL[i]);
			ServiceLocationAttribute sla = new ServiceLocationAttribute(tag, val);

			verify("Unexpected attribute value for tag " + tag + ": " + sla.toString(), ATTR_EXP[i]
					.equalsIgnoreCase(sla.toString()));
		}
	}

	private static final String[] ATTR_BAD = { "(tag=\\FF)", "(tag=\\FF\\)", "(tag=\\FF\\1)",
			"(tag=\\FF\\123)", "(tag=\\FF\\1G)", "(tag=\\FF789)", "(tag=\\FF\\\\1)" };

	/**
	 * testInvalidAttribs
	 * 
	 * @throws Exception
	 */
	public void testInvalidAttribs() throws Exception {
		for (int i = 0; i < ATTR_BAD.length; i++) {
			try {
				new ServiceLocationAttribute(ATTR_BAD[i]);
				fail("Invalid attribute " + ATTR_BAD[i] + " did not generate exception");
			} catch (Exception e) {
				debug(ATTR_BAD[i] + " produced " + e.getMessage());
				verify("Attribute " + ATTR_BAD[i] + " generated unexpected exception "
						+ e.getClass().getName(), e instanceof ServiceLocationException);
			}
		}
	}

	/**
	 * @param pMsg
	 */
	private static void debug(String pMsg) {
	// System.out.println(pMsg);
	}

}
