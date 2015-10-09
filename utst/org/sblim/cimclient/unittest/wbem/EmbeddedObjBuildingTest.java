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
 * 1660756    2007-03-02  ebak         Embedded object support
 * 1686000    2007-04-20  ebak         modifyInstance() missing from WBEMClient
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1820763    2007-10-29  ebak         Supporting the EmbeddedInstance qualifier
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.cim.CIMClass;
import javax.wbem.CloseableIterator;

import org.sblim.cimclient.internal.cimxml.CIMClientXML_HelperImpl;
import org.sblim.cimclient.unittest.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class EmbeddedObjBuildingTest
 * 
 */
public class EmbeddedObjBuildingTest extends TestCase {

	/*
	 * CIMClass: PROPERTY, PROPERTY.ARRAY, PARAMETER, METHOD
	 */
	private static final CIMClass REF_CLASS = EmbeddedObjParsingTest.REF_CLASS;

	/**
	 * testCIMClass
	 * 
	 * @throws Exception
	 */
	public void testCIMClass() throws Exception {
		Document doc = CIMBuilder.createDoc();
		Element e = CIMClientXML_HelperImpl.enumerateClasses_response(doc,
				new CIMClass[] { REF_CLASS });
		byte[] bytes = CIMBuilder.getXML(doc, e);
		// dumpBytes(bytes);
		InputStream is = new ByteArrayInputStream(bytes);
		CloseableIterator<Object> itr = Common.parseWithSAX(is);
		verify("Failed to parse CIMClass!", itr.hasNext());
		CIMClass cl = (CIMClass) itr.next();
		verify("REF_CLASS!=cl\nREF_CLASS:" + REF_CLASS + "\n\ncl:" + cl, REF_CLASS.equals(cl));
	}

	/*
	 * private static void dumpBytes(byte[] pBytes) { String str = new
	 * String(pBytes); System.out.println(str); }
	 */

}
