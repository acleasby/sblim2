/**
 * (C) Copyright IBM Corp. 2012
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Dave Blaschke, blaschke@us.ibm.com
 * 
 * Flag       Date        Prog         Description
 * -------------------------------------------------------------------------------
 * 3572993    2012-10-01  blaschke-oss parseDouble("2.2250738585072012e-308") DoS vulnerability
 */

package org.sblim.cimclient.unittest.util;

import org.sblim.cimclient.internal.util.Util;
import org.sblim.cimclient.unittest.TestCase;

/**
 * BadDoubleStringsTest
 * 
 * Sun bug 4421494 identifies a range of <code>java.lang.Double</code> values
 * that will hang the JVM due to an error in
 * <code>FloatingDecimal.doubleValue()</code> that results in an infinite loop.
 * 
 * The range of invalid values is
 * ("2.225073858507201136057409796709131975934E-308",
 * "2.225073858507201259573821257020768020078E-308")
 */
public class BadDoubleStringsTest extends TestCase {

	private class NumberResultPair {

		private String iNumber;

		private boolean iResult;

		NumberResultPair(String str, boolean result) {
			this.iNumber = str;
			this.iResult = result;
		}

		public String getNumber() {
			return this.iNumber;
		}

		public boolean getResult() {
			return this.iResult;
		}
	}

	private NumberResultPair[] NumberResult = {
			new NumberResultPair("2.225073858507201100000000000000000000000E-308", false),
			new NumberResultPair("2.225073858507201125000000000000000000000E-308", false),
			new NumberResultPair("2.225073858507201136057409796709131975933E-308", false),
			new NumberResultPair("2.225073858507201136057409796709131975934E-308", false),
			new NumberResultPair("2.225073858507201136057409796709131975935E-308", true),
			new NumberResultPair("2.225073858507201150000000000000000000000E-308", true),
			new NumberResultPair("2.225073858507201175000000000000000000000E-308", true),
			new NumberResultPair("0.000000000222507385850720120000000000000E-298", true),
			new NumberResultPair("0.000000002225073858507201200000000000000E-299", true),
			new NumberResultPair("0.000000022250738585072012000000000000000E-300", true),
			new NumberResultPair("0.000000222507385850720120000000000000000E-301", true),
			new NumberResultPair("0.000002225073858507201200000000000000000E-302", true),
			new NumberResultPair("0.000022250738585072012000000000000000000E-303", true),
			new NumberResultPair("0.000222507385850720120000000000000000000E-304", true),
			new NumberResultPair("0.002225073858507201200000000000000000000E-305", true),
			new NumberResultPair("0.022250738585072012000000000000000000000E-306", true),
			new NumberResultPair("0.222507385850720120000000000000000000000E-307", true),
			new NumberResultPair("2.225073858507201200000000000000000000000E-308", true),
			new NumberResultPair("22.25073858507201200000000000000000000000E-309", true),
			new NumberResultPair("222.5073858507201200000000000000000000000E-310", true),
			new NumberResultPair("2225.073858507201200000000000000000000000E-311", true),
			new NumberResultPair("22250.73858507201200000000000000000000000E-312", true),
			new NumberResultPair("222507.3858507201200000000000000000000000E-313", true),
			new NumberResultPair("2225073.858507201200000000000000000000000E-314", true),
			new NumberResultPair("22250738.58507201200000000000000000000000E-315", true),
			new NumberResultPair("222507385.8507201200000000000000000000000E-316", true),
			new NumberResultPair("2225073858.507201200000000000000000000000E-317", true),
			new NumberResultPair("22250738585.07201200000000000000000000000E-318", true),
			new NumberResultPair("2.225073858507201225000000000000000000000E-308", true),
			new NumberResultPair("2.225073858507201250000000000000000000000E-308", true),
			new NumberResultPair("2.225073858507201259573821257020768020077E-308", true),
			new NumberResultPair("2.225073858507201259573821257020768020078E-308", false),
			new NumberResultPair("2.225073858507201259573821257020768020079E-308", false),
			new NumberResultPair("2.225073858507201275000000000000000000000E-308", false),
			new NumberResultPair("2.225073858507201300000000000000000000000E-308", false),
			new NumberResultPair("2.225073858507201136057409796709131975935E+308", false),
			new NumberResultPair("2.225073858507201259573821257020768020077E+308", false) };

	/**
	 * testDoubleStrings
	 */
	public void testDoubleStrings() {
		for (int i = 0; i < this.NumberResult.length; i++) {
			String numberStr = this.NumberResult[i].getNumber();
			boolean expectedRC = this.NumberResult[i].getResult();
			verify("Unexpected result for index " + i,
					Util.isBadDoubleString(numberStr) == expectedRC);
			if (expectedRC == false) {
				double d = Double.parseDouble(numberStr);
				verify("Better not hang!", d != 0);
			}
		}
	}
}
