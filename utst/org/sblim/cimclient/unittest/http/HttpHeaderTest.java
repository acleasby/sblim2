/**
 * (C) Copyright IBM Corp. 2005, 2013
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *
 * Change History
 * Flag     Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1715612  2007-05-09  lupusalex    FVT: Status 0 in trailer is parsed as error
 * 2003590  2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131  2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2641758  2009-02-27  blaschke-oss CIM Client does not recognize HTTP extension headers
 *    2718  2013-11-29  blaschke-oss Bad CIMStatusCode generates NumberFormatException
 */
package org.sblim.cimclient.unittest.http;

import java.io.ByteArrayInputStream;

import org.sblim.cimclient.internal.http.HttpHeader;
import org.sblim.cimclient.internal.http.io.TrailerException;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class HttpHeaderTest is responsible for testing the HttpHeader class.
 * 
 */
public class HttpHeaderTest extends TestCase {

	/**
	 * Tests examineTrailer(). Several http trailers are created and checked if
	 * the trailer exceptions are thrown only when expected and that status
	 * codes and description are correctly set in the exception.
	 * 
	 * @throws Exception
	 */
	public void testExamineTrailer() throws Exception {
		{
			ByteArrayInputStream stream = new ByteArrayInputStream("CIMStatusCode: 0\n\n"
					.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
			} catch (TrailerException e) {
				fail("TrailerException thrown on status code 0");
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream("CIMStatusCode: 1\n\n"
					.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 1");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(1));
				verify("Status description", e.getWBEMException().getMessage() == null);
			} catch (Exception e) {
				throw e;
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream(
					"CIMStatusCode: 6\nCIMStatusCodeDescription: CIM_ERR_NOT_FOUND%3A%20The%20requested%20object%20could%20not%20be%20found%3A%20%22CIM_ManagedElement%22\n\n"
							.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 6");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(6));
				verify("Status description", EQUAL, e.getWBEMException().getMessage(),
						"CIM_ERR_NOT_FOUND: The requested object could not be found: \"CIM_ManagedElement\"");
			} catch (Exception e) {
				throw e;
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream(
					"Content-language: de-DE\nCIMStatusCode: 19\nSome-unknown-header-field: bla bla bla\n\n"
							.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 19");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(19));
				verify("Status description", e.getWBEMException().getMessage() == null);
			} catch (Exception e) {
				throw e;
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream("12-CIMStatusCode: 1\n\n"
					.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 1 with HTTP extension");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(1));
				verify("Status description", e.getWBEMException().getMessage() == null);
			} catch (Exception e) {
				throw e;
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream("12-CIMStatusCode: one\n\n"
					.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 'one' as HTTP extension");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(1));
				verify("Status description", e.getWBEMException().getMessage() != null
						&& e.getWBEMException().getMessage().contains("\"one\""));
			} catch (Exception e) {
				throw e;
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream(
					"12-CIMStatusCode: one\n12-CIMStatusCodeDescription: something failed\n\n"
							.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 'one' as 1st HTTP extension");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(1));
				verify("Status description", "something failed".equalsIgnoreCase(e
						.getWBEMException().getMessage()));
			} catch (Exception e) {
				throw e;
			}
		}

		{
			ByteArrayInputStream stream = new ByteArrayInputStream(
					"12-CIMStatusCodeDescription: something failed\n12-CIMStatusCode: one\n\n"
							.getBytes());
			HttpHeader header = new HttpHeader(stream);
			try {
				header.examineTrailer();
				fail("No TrailerException thrown on status code 'one' as 2nd HTTP extension");
			} catch (TrailerException e) {
				verify("Status code", EQUAL, new Integer(e.getWBEMException().getID()),
						new Integer(1));
				verify("Status description", "something failed".equalsIgnoreCase(e
						.getWBEMException().getMessage()));
			} catch (Exception e) {
				throw e;
			}
		}

	}
}
