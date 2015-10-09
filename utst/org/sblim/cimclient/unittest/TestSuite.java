/**
 * (C) Copyright IBM Corp. 2006, 2010
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
 * 1716502    2007-05-10  ebak         Adding inclusion feature to the test framework
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 3022541    2010-06-30  blaschke-oss File descriptor leak in sample/unittest
 */

package org.sblim.cimclient.unittest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class TestSuite is responsible for executing all units tests. In order to be
 * recognized by the test suite a test case has to register itself in the
 * <code>testcases.txt</code> file in this package.
 * 
 */
public class TestSuite {

	/**
	 * QUIET mode. All messages but test case failures are suppressed.
	 */
	public static final int QUIET = 0;

	/**
	 * NORMAL mode. Failures and warnings are shown.
	 */
	public static final int NORMAL = 1;

	/**
	 * VERBOSE mode. Failures, warnings and test case execution are reported.
	 */
	public static final int VERBOSE = 2;

	private static TestSuite cSingleton;

	/**
	 * Return the test suite
	 * 
	 * @return The test suite
	 */
	public static TestSuite getSuite() {
		return cSingleton;
	}

	private int iMode;

	private String iRootFile;

	private ConfTrc iConfTrc = new ConfTrc();

	private TestSuite(String pFile, int pMode) {
		this.iRootFile = pFile;
		this.iMode = pMode;
	}

	/**
	 * Executes the test cases. The VM will return with the number of failed
	 * tests as result code (by System.exit(rc)).
	 * 
	 * @param args
	 * <br />
	 *            &nbsp;&nbsp;&nbsp;&nbsp;[0] name of the file containing the
	 *            test case list - default: <code>testcases.txt</code><br />
	 *            &nbsp;&nbsp;&nbsp;&nbsp;[1] either
	 *            <code>quiet, normal, verbose</code> (the console modes)
	 */
	@SuppressWarnings("fallthrough")
	public static void main(String[] args) {
		try {
			String configFile = "testcases.txt";
			int mode = NORMAL;
			switch (args.length) {
				case 3:
					System.err.println("Invalid number of command line parameters");
					System.exit(1);
				case 2:
					if ("QUIET".equalsIgnoreCase(args[1])) {
						mode = QUIET;
					} else if ("VERBOSE".equalsIgnoreCase(args[1])) {
						mode = VERBOSE;
					} else if (!"NORMAL".equalsIgnoreCase(args[1])) {
						System.err.println("Invalid console mode");
						System.exit(1);
					}
					// fall through intended !
				case 1:
					configFile = args[0];
				default:
					// nothing to do !
			}
			cSingleton = new TestSuite(configFile, mode);
			cSingleton.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to launch test suite");
		}
	}

	private void exec(String pResourceName, TestResult pResult) throws Exception {
		InputStream stream = TestSuite.class.getResourceAsStream(pResourceName);
		if (stream == null) {
			System.err.println("Failed to load testcase config file: " + pResourceName);
			if (this.iConfTrc.size() > 0) System.err.println(this.iConfTrc);
			System.exit(1);
		}
		int lineNum = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		while (reader.ready()) {
			String testCaseName = reader.readLine();
			if (testCaseName != null && (testCaseName = testCaseName.trim()).length() > 0) {
				boolean include = testCaseName.charAt(0) == '!';
				if (include) testCaseName = testCaseName.substring(1).trim();
				if (this.iMode == VERBOSE) {
					System.out.println((include ? "Including config file: " : "Test case: ")
							+ testCaseName);
				}
				this.iConfTrc.push(pResourceName, lineNum);
				if (include) {
					exec(testCaseName, pResult);
				} else {
					String tcClassName = "org.sblim.cimclient.unittest." + testCaseName;
					try {
						Class<?> testCaseClass = Class.forName(tcClassName);
						TestCase test = (TestCase) testCaseClass.newInstance();
						pResult.add(test.run());
					} catch (ClassNotFoundException e) {
						System.err.println("class for testcase not found: " + tcClassName + "!");
						if (this.iConfTrc.size() > 0) System.err.println(this.iConfTrc);
						e.printStackTrace();
						System.exit(1);
					} catch (TestFailure e) {
						// This should be caught inside the test
						pResult.add(0, 1);
					} catch (Exception e) {
						// This should be caught inside the test
						pResult.add(0, 1);
					}
				}
				this.iConfTrc.pop();
			}
			++lineNum;
		}
		reader.close();
	}

	private void run() {
		try {
			TestResult result = new TestResult(0, 0);
			exec(this.iRootFile, result);
			/*
			 * ebak: stderr and stdout appeared asynchronously in eclipse's
			 * console, this made the test report ugly. System.err.flush() alone
			 * didn't help.
			 */
			System.err.flush();
			try {
				Thread.sleep(100);
			} catch (Exception e) { /* don't care */}
			System.out
					.println("Tests total: " + (result.getSuccessful() + result.getFailed())
							+ ", successful: " + result.getSuccessful() + ", failed: "
							+ result.getFailed());
			System.exit(result.getFailed());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to execute test cases");
			System.exit(1);
		}
	}

	/**
	 * Returns the console mode.
	 * 
	 * @return One of the constants <code>QUIET, NORMAL or VERBOSE</code>
	 */
	public int getMode() {
		return this.iMode;
	}
}

/**
 * ConfTrc is responsible for tracking the inclusion hierarchy of config files.
 */
class ConfTrc {

	/**
	 * push
	 * 
	 * @param pName
	 * @param pLineNum
	 */
	public void push(String pName, int pLineNum) {
		push(new Entry(pName, pLineNum));
	}

	/**
	 * push
	 * 
	 * @param pEnt
	 */
	public void push(Entry pEnt) {
		this.iAL.add(pEnt);
	}

	/**
	 * pop
	 * 
	 * @return the top entry
	 */
	public Entry pop() {
		if (this.iAL.size() == 0) return null;
		return this.iAL.remove(this.iAL.size() - 1);
	}

	/**
	 * size
	 * 
	 * @return number of entries in the trace
	 */
	public int size() {
		return this.iAL.size();
	}

	@Override
	public String toString() {
		if (this.iAL.size() == 0) return "";
		StringBuffer strBuf = new StringBuffer("Inclusion trace:\n");
		for (int i = 0; i < this.iAL.size(); i++) {
			Entry ent = this.iAL.get(i);
			strBuf.append("    " + ent.toString() + "\n");
		}
		return strBuf.toString();
	}

	private ArrayList<Entry> iAL = new ArrayList<Entry>();

	/**
	 * Entry is a trace entry for tracking the inclusion hierarchy of config
	 * files.
	 */
	private static class Entry {

		private String iName;

		private int iLineNum;

		/**
		 * Ctor.
		 * 
		 * @param pName
		 * @param pLineNum
		 */
		public Entry(String pName, int pLineNum) {
			this.iName = pName;
			this.iLineNum = pLineNum;
		}

		/**
		 * getName
		 * 
		 * @return file name
		 */
		public String getName() {
			return this.iName;
		}

		/**
		 * getLineNum
		 * 
		 * @return line number
		 */
		public int getLineNum() {
			return this.iLineNum;
		}

		@Override
		public String toString() {
			return this.iName + ", LineNum:" + this.iLineNum;
		}
	}
}
