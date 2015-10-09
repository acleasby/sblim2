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
 * 1742873    2007-06-25  ebak         IPv6 ready cim-client
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.cim.CIMArgument;

import org.sblim.cimclient.internal.cimxml.CIMResponse;
import org.sblim.cimclient.internal.cimxml.sax.SAXHelper;

/**
 * MethodRspChecker helps to check the correct parsing of MethodResponses.
 */
public class MethodRspChecker {

	private Object iRefRetVal;

	private CIMArgument<?>[] iRefArgA;

	private ByteArrayOutputStream iByteAOStream = new ByteArrayOutputStream();

	/**
	 * Ctor.
	 * 
	 * @param pRefRetVal
	 * @param pRefArgA
	 * @param pStream
	 */
	public MethodRspChecker(Object pRefRetVal, CIMArgument<?>[] pRefArgA, InputStream pStream) {
		init(pRefRetVal, pRefArgA, pStream);
	}

	/**
	 * Ctor.
	 * 
	 * @param pRefRetVal
	 * @param pRefOutArgA
	 * @param pResName
	 */
	public MethodRspChecker(Object pRefRetVal, CIMArgument<?>[] pRefOutArgA, String pResName) {
		InputStream is = getClass().getResourceAsStream(pResName);
		if (is == null) throw new IllegalArgumentException("Failed to load resource! : " + pResName);
		init(pRefRetVal, pRefOutArgA, is);
	}

	private void init(Object pRefRetVal, CIMArgument<?>[] pRefOutArgA, InputStream pStream) {
		this.iRefRetVal = pRefRetVal;
		this.iRefArgA = pRefOutArgA;
		byte[] buf = new byte[4096];
		int read;
		try {
			while ((read = pStream.read(buf)) > 0)
				this.iByteAOStream.write(buf, 0, read);
		} catch (IOException e) {
			// parsing process will fail
		}
	}

	@SuppressWarnings("null")
	private String check(Object pRetVal, CIMArgument<?>[] pArgA) {

		if (this.iRefRetVal == null ? pRetVal != null : !this.iRefRetVal.equals(pRetVal)) { return "retVal should be "
				+ this.iRefRetVal == null ? "null" : "(" + this.iRefRetVal.getClass().getName()
				+ ")" + this.iRefRetVal + ", but it's "
				+ (pRetVal == null ? "null" : "(" + pRetVal.getClass().getName() + ")" + pRetVal)
				+ " !"; }
		int argALen = pArgA == null ? 0 : pArgA.length;
		if (this.iRefArgA.length != argALen) { return "refArgA.length(" + this.iRefArgA.length
				+ ") != argALen(" + argALen + ") !"; }
		for (int i = 0; i < this.iRefArgA.length; i++) {
			CIMArgument<?> refArg = this.iRefArgA[i];
			CIMArgument<?> outArg = pArgA[i];
			if (!refArg.equals(outArg)) { return "At idx:" + i + " refArg!=outArg !" + "\nrefArg: "
					+ refArg + "\noutArg: " + outArg; }
		}
		return null;
	}

	private InputStreamReader getStreamReader() {
		return new InputStreamReader(new ByteArrayInputStream(this.iByteAOStream.toByteArray()));
	}

	/**
	 * Parses the method response with SAX.
	 * 
	 * @param pOutArgA
	 *            - output arguments of method response.
	 * @return return value of method response.
	 * @throws Exception
	 */
	public Object parseSAX(CIMArgument<?>[] pOutArgA) throws Exception {
		return SAXHelper.parseInvokeMethodResponse(getStreamReader(), pOutArgA, Common.LOCALPATH
				.getBasePath());
	}

	/**
	 * Parses the method response with DOM.
	 * 
	 * @param pOutArgA
	 *            - output arguments of method response.
	 * @return return value of method response.
	 * @throws Exception
	 */
	public Object parseDOM(CIMArgument<?>[] pOutArgA) throws Exception {
		CIMResponse response = Common.getSingleResponse(getStreamReader(), Common.LOCALPATH
				.getBasePath());
		response.checkError();
		List<Object> resultSet = response.getFirstReturnValue();

		Object retVal = resultSet.size() > 0 ? resultSet.get(0) : null;

		if (pOutArgA != null) {
			List<Object> outParamValues = response.getParamValues();
			if (outParamValues != null) {
				Iterator<Object> itr = outParamValues.iterator();
				for (int i = 0; i < pOutArgA.length; i++) {
					if (!itr.hasNext()) break;
					pOutArgA[i] = (CIMArgument<?>) itr.next();
				}
			}
		}
		return retVal;
	}

	/**
	 * testSAX
	 * 
	 * @return String, null means no error
	 * @throws Exception
	 */
	public String testSAX() throws Exception {
		CIMArgument<?>[] argA = this.iRefArgA == null ? null
				: new CIMArgument[this.iRefArgA.length];
		Object retVal = parseSAX(argA);
		return check(retVal, argA);
	}

	/**
	 * testDOM
	 * 
	 * @return String, null means no error
	 * @throws Exception
	 */
	public String testDOM() throws Exception {
		CIMArgument<?>[] argA = this.iRefArgA == null ? null
				: new CIMArgument[this.iRefArgA.length];
		Object retVal = parseDOM(argA);
		return check(retVal, argA);
	}

}
