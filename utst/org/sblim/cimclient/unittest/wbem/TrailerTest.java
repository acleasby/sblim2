/**
 * (C) Copyright IBM Corp. 2006, 2013
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
 * 1688273    2007-04-16  ebak         Full support of HTTP trailers
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1737141    2007-06-19  ebak         Sync up with JSR48 evolution
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 *    2621    2013-02-23  blaschke-oss Not all chunked input has trailers
 */

package org.sblim.cimclient.unittest.wbem;

import java.util.zip.GZIPInputStream;

import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;

import org.sblim.cimclient.internal.http.io.ChunkedInputStream;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class TrailerTest
 */
public class TrailerTest extends TestCase {

	private ChunkedInputStream getIS0() throws Exception {
		return new ChunkedInputStream(new GZIPInputStream(getClass().getResourceAsStream(
				"data/trailer.bin.gz")), "CIMStatusCode,CIMStatusCodeDescription");
	}

	private static final String ERRDESC0 = "CIM_ERR_INVALID_CLASS: The specified class does not exist: \"ListenerDestinationCIMXML\"";

	private void checkItr0(CloseableIterator<Object> pItr) {
		try {
			pItr.hasNext();
		} catch (RuntimeException rtE) {
			Throwable cause = rtE.getCause();
			verify("There is no embedded WBEMException in the caught RuntimeException!",
					cause != null && (cause instanceof WBEMException));
			WBEMException wbemExc = (WBEMException) cause;
			verify("WBEMException.getID():" + wbemExc.getID() + " != 5 !", wbemExc.getID() == 5);
			verify("WBEMException.getMessage()=\n" + wbemExc.getMessage() + "\n!=\n" + ERRDESC0,
					ERRDESC0.equals(wbemExc.getMessage()));
			// WBEMException have to be retrievable by a member function too
			WBEMException wbemExc2 = pItr.getWBEMException();
			verify("The thrown and the retrieved WBEMExceptions are not equal at reference level!",
					wbemExc == wbemExc2);
			return;
		}
		verify("pItr.hasNext() doesn't throw RuntimeException!", false);
	}

	/**
	 * test0SAX
	 * 
	 * @throws Exception
	 */
	public void test0SAX() throws Exception {
		checkItr0(Common.parseWithSAX(getIS0()));
	}

	/**
	 * test0PULL
	 * 
	 * @throws Exception
	 */
	public void test0PULL() throws Exception {
		checkItr0(Common.parseWithPULL(getIS0()));
	}

	/**
	 * test0DOM
	 * 
	 * @throws Exception
	 */
	public void test0DOM() throws Exception {
		checkItr0(Common.parseWithDOM(getIS0()));
	}

}
