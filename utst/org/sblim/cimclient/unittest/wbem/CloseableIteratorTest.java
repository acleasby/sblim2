/**
 * (C) Copyright IBM Corp. 2006, 2011
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
 * 1708584    2007-04-27  ebak         CloseableIterator might not clean up streams
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 3293248    2011-05-03  blaschke-oss Support for CIM_ERROR instances within ERROR
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.cim.CIMClass;
import javax.cim.CIMInstance;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;

import org.sblim.cimclient.internal.wbem.CloseableIteratorDOM;
import org.sblim.cimclient.internal.wbem.CloseableIteratorPULL;
import org.sblim.cimclient.internal.wbem.CloseableIteratorSAX;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.cimclient.unittest.wbem.data.EnumClasses;

/**
 * CloseableIteratorTest
 */
public class CloseableIteratorTest extends TestCase {

	private InputStreamReader getEnumClassesIS() {
		String resName = "data/enumerateClasses.xml";
		InputStream is = getClass().getResourceAsStream(resName);
		verify("Failed to load resource! : " + resName, is != null);
		return new InputStreamReader(is);
	}

	private InputStreamReader getEnumClassesErrorIS() {
		String resName = "data/enumerateClassesError.xml";
		InputStream is = getClass().getResourceAsStream(resName);
		verify("Failed to load resource! : " + resName, is != null);
		return new InputStreamReader(is);
	}

	private void checkItr(CloseableIterator<?> pItr) {
		CIMClass[] classA = EnumClasses.CLASS_A;
		int cnt = 0;
		while (pItr.hasNext()) {
			CIMClass cl = (CIMClass) pItr.next();
			verify("\nCIMClass from sample array doesn't equal CIMClass from iterator!\n\n"
					+ classA[cnt] + "\n" + cl, classA[cnt].equals(cl));
			++cnt;
		}
		verify("Number of CIMClasses doesn't match! (" + classA.length + "!=" + cnt + ")\n",
				cnt == classA.length);
	}

	/**
	 * testEnumerateClasses
	 */
	public void testEnumerateClassesPULL() {
		checkItr(new CloseableIteratorPULL(getEnumClassesIS(), null));
	}

	/**
	 * testEnumerateClassesSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumerateClassesSAX() throws Exception {
		checkItr(new CloseableIteratorSAX(getEnumClassesIS(), null));
	}

	/**
	 * testEnumerateClassesDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumerateClassesDOM() throws Exception {
		checkItr(new CloseableIteratorDOM(getEnumClassesIS(), null));
	}

	private void checkErr(CloseableIterator<?> pItr) {
		try {
			if (pItr.hasNext()) {
				verify("Exception not thrown for error response!", pItr.next() != null);
			}
		} catch (Exception e) {
			verify("Exception not instance of WBEMException!",
					e.getCause() instanceof WBEMException);
			WBEMException exception = (WBEMException) e.getCause();
			verify("Exception contains no CIM_Error instances!", exception.getCIMErrors() != null);
			CIMInstance[] ci = exception.getCIMErrors();
			verify("Number of CIM_Errors doesn't match! (" + ci.length + "!="
					+ EnumClasses.ERROR_A.length + ")", ci.length == EnumClasses.ERROR_A.length);
			for (int i = 0; i < ci.length; i++) {
				verify("CIM_Error[" + i + "] doesn't match!\n\n" + ci[i] + "\n!=\n"
						+ EnumClasses.ERROR_A[i] + "\n", ci[i].equals(EnumClasses.ERROR_A[i]));
			}
		}
	}

	/**
	 * testEnumerateClassesErrPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumerateClassesErrPULL() throws Exception {
		checkErr(new CloseableIteratorPULL(getEnumClassesErrorIS(), null));
	}

	/**
	 * testEnumerateClassesErrSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumerateClassesErrSAX() throws Exception {
		checkErr(new CloseableIteratorSAX(getEnumClassesErrorIS(), null));
	}

	/**
	 * testEnumerateClassesErrDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumerateClassesErrDOM() throws Exception {
		checkErr(new CloseableIteratorDOM(getEnumClassesErrorIS(), null));
	}

	private void checkClose(CloseableIterator<Object> pItr) {
		verify("true is expected for itr.hasNext()", pItr.hasNext());
		Object obj = pItr.next();
		verify("Failed to get Object from itr!", obj != null);
		verify("true is expected for itr.hasNext()", pItr.hasNext());
		pItr.close();
		verify("Closed iterator's hasNext() should be false!", !pItr.hasNext());
	}

	/**
	 * testClosePULL
	 * 
	 * @throws Exception
	 */
	public void testClosePULL() throws Exception {
		checkClose(new CloseableIteratorPULL(getEnumClassesIS(), null));
	}

	/**
	 * testCloseSAX
	 * 
	 * @throws Exception
	 */
	public void testCloseSAX() throws Exception {
		checkClose(new CloseableIteratorSAX(getEnumClassesIS(), null));
	}

	/**
	 * testCloseDOM
	 * 
	 * @throws Exception
	 */
	public void testCloseDOM() throws Exception {
		checkClose(new CloseableIteratorDOM(getEnumClassesIS(), null));
	}

}
