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
 * 1660743    2007-02-15  lupusalex    SSLContext is static
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 */

package org.sblim.cimclient.unittest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class TestCase is the superclass for all test case implementations. It offers
 * methods to do assertions, defines verifiers and implements the run() method.
 * 
 */
public abstract class TestCase {

	/**
	 * This <code>Verifier</code> performs an equals() comparison
	 */
	public static final Verifier EQUAL = new Verifier() {

		public boolean verify(Object pValue, Object pTargetValue) {
			return pTargetValue == null ? pValue == null : pTargetValue.equals(pValue);
		}

		@Override
		public String toString() {
			return "==";
		}
	};

	/**
	 * This <code>Verifier</code> performs an !equals() comparison
	 */
	public static final Verifier NOT_EQUAL = new Verifier() {

		public boolean verify(Object pValue, Object pTargetValue) {
			return pTargetValue == null ? pValue != null : !pTargetValue.equals(pValue);
		}

		@Override
		public String toString() {
			return "!=";
		}
	};

	/**
	 * This <code>Verifier</code> performs an compareTo()<0 comparison
	 */
	public static final Verifier LESS = new Verifier() {

		@SuppressWarnings("unchecked")
		public boolean verify(Object pValue, Object pTargetValue) {
			if (pTargetValue instanceof Comparable) { return ((Comparable<Object>) pTargetValue)
					.compareTo(pValue) > 0; }
			throw new IllegalArgumentException("Not comparable");
		}

		@Override
		public String toString() {
			return "<";
		}
	};

	/**
	 * This <code>Verifier</code> performs an compareTo()>0 comparison
	 */
	public static final Verifier GREATHER = new Verifier() {

		@SuppressWarnings("unchecked")
		public boolean verify(Object pValue, Object pTargetValue) {
			if (pTargetValue instanceof Comparable) { return ((Comparable<Object>) pTargetValue)
					.compareTo(pValue) < 0; }
			throw new IllegalArgumentException("Not comparable");
		}

		@Override
		public String toString() {
			return ">";
		}
	};

	/**
	 * This <code>Verifier</code> performs an compareTo()<=0 comparison
	 */
	public static final Verifier LESS_OR_EQUAL = new Verifier() {

		@SuppressWarnings("unchecked")
		public boolean verify(Object pValue, Object pTargetValue) {
			if (pTargetValue instanceof Comparable) { return ((Comparable<Object>) pTargetValue)
					.compareTo(pValue) >= 0; }
			throw new IllegalArgumentException("Not comparable");
		}

		@Override
		public String toString() {
			return "<=";
		}
	};

	/**
	 * This <code>Verifier</code> performs an compareTo()>=0 comparison
	 */
	public static final Verifier GREATHER_OR_EQUAL = new Verifier() {

		@SuppressWarnings("unchecked")
		public boolean verify(Object pValue, Object pTargetValue) {
			if (pTargetValue instanceof Comparable) { return ((Comparable<Object>) pTargetValue)
					.compareTo(pValue) <= 0; }
			throw new IllegalArgumentException("Not comparable");
		}

		@Override
		public String toString() {
			return ">=";
		}
	};

	/**
	 * Checks for a given condition. Throws an TestFailure if the condition is
	 * not met.
	 * 
	 * @param pMessage
	 *            The message to show when the condition is not met
	 * @param pVerifier
	 *            The <code>Verifier</code> to use for the condition check
	 * @param pValue
	 *            The value to check
	 * @param pTargetValue
	 *            The target value to compare with
	 * @throws TestFailure
	 *             When the condition is not met
	 */
	public void verify(String pMessage, Verifier pVerifier, Object pValue, Object pTargetValue)
			throws TestFailure {
		if (!pVerifier.verify(pValue, pTargetValue)) {
			fail(pMessage + " (" + String.valueOf(pValue) + " " + String.valueOf(pVerifier) + " "
					+ String.valueOf(pTargetValue) + ")");
		}
	}

	/**
	 * Checks a given result. Throws an TestFailure if the result is false.
	 * 
	 * @param pMessage
	 *            The message to show when the result is false
	 * @param pResult
	 *            The result of the assertion
	 * @throws TestFailure
	 *             When result is false
	 */
	public void verify(String pMessage, boolean pResult) throws TestFailure {
		if (!pResult) {
			fail(pMessage);
		}
	}

	/**
	 * Fails a test by throwing an TestFailure.
	 * 
	 * @param pMessage
	 *            The message to show
	 * @throws TestFailure
	 */
	public void fail(String pMessage) throws TestFailure {
		throw new TestFailure(pMessage);
	}

	/**
	 * Shows the message on stdout when console mode is NORMAL or VERBOSE.
	 * 
	 * @param pMessage
	 *            The message
	 */
	public void warning(String pMessage) {
		if (TestSuite.getSuite().getMode() > TestSuite.QUIET) {
			System.out.println("--> " + pMessage);
		}
	}

	/**
	 * Executes the tests in the concrete subclass. A test is recognized as a
	 * public method starting with "test".
	 * 
	 * @return The test result, that is the count of successful and failed
	 *         tests.
	 */
	public TestResult run() {
		int mode = TestSuite.getSuite().getMode();
		int successful = 0;
		int failed = 0;
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);
		Method[] methods = this.getClass().getMethods();
		for (int i = 0; i < methods.length; ++i) {
			Method method = methods[i];
			if (method.getName().startsWith("test")) {
				try {
					if (mode == TestSuite.VERBOSE) {
						System.out.println("-> Executing " + method.getName());
					}
					method.invoke(this, (Object[]) null);
					++successful;
				} catch (InvocationTargetException e) {
					if (e.getCause() != null && e.getCause() instanceof TestFailure) {
						StackTraceElement caller = getThrower(e.getCause().getStackTrace());
						System.err.println("Test case failed: " + className + "."
								+ method.getName() + "(" + caller.getFileName() + ":"
								+ caller.getLineNumber() + ") \"" + e.getCause().getMessage()
								+ "\"");
					} else {
						System.err.println("Exception during test case:");
						if (e.getCause() != null) {
							e.getCause().printStackTrace(System.err);
						} else {
							e.printStackTrace(System.err);
						}
					}
					++failed;
				} catch (Exception e) {
					System.err.println("Unable to execute test case: " + method.getName());
				}
			}
		}
		return new TestResult(successful, failed);
	}

	/**
	 * Analyzes the stack trace and determines from where the
	 * <code>Exception</code> was thrown.
	 * 
	 * @param pStack
	 *            StackTrace of exception
	 * @return First <code>StackTraceElement</code> outside the
	 *         <code>TestCase</code>
	 */
	private StackTraceElement getThrower(StackTraceElement[] pStack) {
		final String thisClass = TestCase.class.getName();
		for (int i = 0; i < pStack.length; ++i) {
			StackTraceElement frame = pStack[i];
			String cname = frame.getClassName();
			if (!thisClass.equals(cname)) { return frame; }
		}
		return null;
	}
}
