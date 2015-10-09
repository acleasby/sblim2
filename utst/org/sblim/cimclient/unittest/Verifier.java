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
 * Interface Verifier is the abstraction of an assertion.
 * 
 */
public interface Verifier {

	/**
	 * Asserts that a given value fulfills a certain condition in respect to the
	 * target value. The specific condition to be met is defined by the
	 * implementing class.
	 * 
	 * @param pValue
	 *            The value to assert
	 * @param pTargetValue
	 *            The reference value to test against
	 * @return <code>true</code> if the condition is met, <code>false</code>
	 *         otherwise
	 */
	public boolean verify(Object pValue, Object pTargetValue);
}
