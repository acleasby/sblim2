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
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1565892    2006-11-23  lupusalex    Make SBLIM client JSR48 compliant
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 */

package org.sblim.cimclient.unittest;

/**
 * Class TestResult is responsible for keeping the result of a test case, namely
 * the count of successful and failed tests.
 * 
 */
public class TestResult {

	private int iSuccessful = 0;

	private int iFailed = 0;

	/**
	 * Ctor.
	 * 
	 * @param pSuccessful
	 *            The initial successful count
	 * @param pFailed
	 *            The initial failed count
	 */
	public TestResult(int pSuccessful, int pFailed) {
		this.iSuccessful = pSuccessful;
		this.iFailed = pFailed;
	}

	/**
	 * Returns failed
	 * 
	 * @return The value of failed.
	 */
	public int getFailed() {
		return this.iFailed;
	}

	/**
	 * Sets failed
	 * 
	 * @param pFailed
	 *            The new value of failed.
	 */
	public void setFailed(int pFailed) {
		this.iFailed = pFailed;
	}

	/**
	 * Returns successful
	 * 
	 * @return The value of successful.
	 */
	public int getSuccessful() {
		return this.iSuccessful;
	}

	/**
	 * Sets successful
	 * 
	 * @param pSuccessful
	 *            The new value of successful.
	 */
	public void setSuccessful(int pSuccessful) {
		this.iSuccessful = pSuccessful;
	}

	/**
	 * Adds another <code>TestResult</code> object to this object. The addition
	 * is performed by adding the counts of both objects.
	 * 
	 * @param pAddend
	 *            The operand
	 */
	public void add(TestResult pAddend) {
		this.iFailed += pAddend.iFailed;
		this.iSuccessful += pAddend.iSuccessful;
	}

	/**
	 * Adds two <code>TestResult</code> objects and returns the result as a new
	 * <code>TestResult</code> object.
	 * 
	 * @param pAddend1
	 *            The first operand
	 * @param pAddend2
	 *            The second operand
	 * @return The sum
	 */
	public static TestResult add(TestResult pAddend1, TestResult pAddend2) {
		return new TestResult(pAddend1.iSuccessful + pAddend2.iSuccessful, pAddend1.iFailed
				+ pAddend2.iFailed);
	}

	/**
	 * Adds integers to the successful &amp; failed counts
	 * 
	 * @param pSuccessful
	 *            Value to add to the successful count
	 * @param pFailed
	 *            Value to add to the failed count
	 */
	public void add(int pSuccessful, int pFailed) {
		this.iSuccessful += pSuccessful;
		this.iFailed += pFailed;
	}
}
