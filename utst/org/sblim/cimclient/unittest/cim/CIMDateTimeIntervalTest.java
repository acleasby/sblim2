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
 * 1678807    2007-03-12  ebak         Minor CIMDateTime suggestions
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2989367    2010-04-29  blaschke-oss CIMDateTimeInterval(long) constructor range wrong
 * 2989424    2010-04-29  blaschke-oss TCK: CIMDateTimeInterval constructor
 * 2994252    2010-04-30  blaschke-oss CIMDateTimeInterval.getTotalMilliseconds() not unit tested
 *    2674    2013-09-26  blaschke-oss Null pointer exception in CIMDateTime(String)
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.CIMDateTimeInterval;

import org.sblim.cimclient.unittest.TestCase;

/**
 * CIMDateTimeIntervalTest
 */
public class CIMDateTimeIntervalTest extends TestCase {

	private static final int DAYS = 133, HOURS = 10, MINS = 24, SECS = 18, USECS = 1328;

	/**
	 * DTIRef
	 */
	private class DTIRef {

		private int iDays, iHours, iMins, iSecs, iUSecs;

		/**
		 * Ctor.
		 * 
		 * @param pDays
		 * @param pHours
		 * @param pMins
		 * @param pSecs
		 * @param pUSecs
		 */
		public DTIRef(int pDays, int pHours, int pMins, int pSecs, int pUSecs) {
			this.iDays = pDays;
			this.iHours = pHours;
			this.iMins = pMins;
			this.iSecs = pSecs;
			this.iUSecs = pUSecs;

		}

		/**
		 * Ctor.
		 */
		public DTIRef() {
			this(DAYS, HOURS, MINS, SECS, USECS);
		}

		/**
		 * checkFields
		 * 
		 * @param pDTI
		 */
		public void checkFields(CIMDateTimeInterval pDTI) {
			int days = pDTI.getDays();
			verify("getDays() should be " + this.iDays + " not " + days + " !", this.iDays == days);
			int hours = pDTI.getHours();
			verify("getHours() should be " + this.iHours + " not " + hours + " !",
					this.iHours == hours);
			int mins = pDTI.getMinutes();
			verify("getMinutes() should be " + this.iMins + " not " + mins + " !",
					this.iMins == mins);
			int secs = pDTI.getSeconds();
			verify("getSeconds() should be " + this.iSecs + " not " + secs + " !",
					this.iSecs == secs);
			int uSecs = pDTI.getMicroseconds();
			verify("getMicroseconds() shoud be " + this.iUSecs + " not " + uSecs + " !",
					this.iUSecs == uSecs);
		}

	}

	/**
	 * @param pMsg
	 */
	private static void debug(String pMsg) {
	// System.out.println(pMsg);
	}

	/*
	 * CIMDateTimeInterval(int pDays, int pHours, int pMinutes, int pSeconds,
	 * int pMicroseconds)
	 */

	private void negativeConstruction(int pDays, int pHours, int pMins, int pSecs, int pUSecs) {
		try {
			new CIMDateTimeInterval(pDays, pHours, pMins, pSecs, pUSecs);
			verify("IllegalArgumentException was not thrown for invalid "
					+ " constructor params (days=" + pDays + " ,hours=" + pHours + " ,mins="
					+ pMins + " ,secs=" + pSecs + "usecs=" + pUSecs + ") !", false);
		} catch (IllegalArgumentException e) {
			debug(e.getMessage());
		}
	}

	private void checkToString(String pRefStr, CIMDateTimeInterval pDTI) {
		String toStr = pDTI.toString();
		verify("\nrefStr(" + pRefStr + ") !=\n toStr(" + toStr + ")!", pRefStr.equals(toStr));
	}

	private void checkToString(String pRefStr, int pDays, int pHours, int pMins, int pSecs,
			int pUSecs) {
		checkToString(pRefStr, new CIMDateTimeInterval(pDays, pHours, pMins, pSecs, pUSecs));
	}

	/**
	 * testIntConstructor
	 */
	public void testIntConstructor() {
		CIMDateTimeInterval dti = new CIMDateTimeInterval(DAYS, HOURS, MINS, SECS, USECS);
		DTIRef ref = new DTIRef();
		ref.checkFields(dti);
		checkToString("00000133102418.******:000", DAYS, HOURS, MINS, SECS, -1);
		checkToString("000001331024**.******:000", DAYS, HOURS, MINS, -1, -1);
		checkToString("0000013310****.******:000", DAYS, HOURS, -1, -1, -1);
		checkToString("00000133******.******:000", DAYS, -1, -1, -1, -1);
		checkToString("**************.******:000", -1, -1, -1, -1, -1);

		checkToString("00000133102418.001328:000", dti);
		// negative tests
		negativeConstruction(DAYS, HOURS, -1, -1, USECS);

		negativeConstruction(100000000, HOURS, MINS, SECS, USECS);
		negativeConstruction(-2, HOURS, MINS, SECS, USECS);
		negativeConstruction(DAYS, 24, MINS, SECS, USECS);
		negativeConstruction(DAYS, -2, MINS, SECS, USECS);
		negativeConstruction(DAYS, HOURS, 60, SECS, USECS);
		negativeConstruction(DAYS, HOURS, -2, SECS, USECS);
		negativeConstruction(DAYS, HOURS, MINS, 60, USECS);
		negativeConstruction(DAYS, HOURS, MINS, -2, USECS);
		negativeConstruction(DAYS, HOURS, MINS, SECS, 1000000);
		negativeConstruction(DAYS, HOURS, MINS, SECS, -2);
	}

	private void negativeConstruction(long pMillis) {
		try {
			new CIMDateTimeInterval(pMillis);
			verify("IllegalArgumentException was not thrown for invalid "
					+ " constructor params (millis=" + pMillis + ") !", false);
		} catch (IllegalArgumentException e) {
			debug(e.getMessage());
		}
	}

	/**
	 * testLongConstructor
	 */
	public void testLongConstructor() {
		CIMDateTimeInterval dti = new CIMDateTimeInterval(999999);
		DTIRef ref = new DTIRef(0, 0, 16, 39, 999000);
		ref.checkFields(dti);
		checkToString("00000000001639.999***:000", dti);

		dti = new CIMDateTimeInterval(1066666624496789L);
		ref = new DTIRef(12345678, 12, 34, 56, 789000);
		ref.checkFields(dti);
		checkToString("12345678123456.789***:000", dti);

		dti = new CIMDateTimeInterval(0L);
		ref = new DTIRef(0, 0, 0, 0, 0);
		ref.checkFields(dti);
		checkToString("00000000000000.000***:000", dti);

		dti = new CIMDateTimeInterval(1L);
		ref = new DTIRef(0, 0, 0, 0, 1000);
		ref.checkFields(dti);
		checkToString("00000000000000.001***:000", dti);

		dti = new CIMDateTimeInterval(8639999999999999L);
		ref = new DTIRef(99999999, 23, 59, 59, 999000);
		ref.checkFields(dti);
		checkToString("99999999235959.999***:000", dti);

		// negative tests
		negativeConstruction(-1);
		negativeConstruction(8639999999999999L + 1);
	}

	private void negativeConstruction(String pStr) {
		try {
			new CIMDateTimeInterval(pStr);
			verify("IllegalArgumentException was not thrown for invalid "
					+ " constructor params (str=" + pStr + ") !", false);
		} catch (IllegalArgumentException e) {
			debug(e.getMessage());
		}
	}

	/**
	 * testStrConstructor
	 */
	public void testStrConstructor() {
		CIMDateTimeInterval dti = new CIMDateTimeInterval("00000133102418.001328:000");
		DTIRef ref = new DTIRef();
		ref.checkFields(dti);
		checkToString("00000133102418.001328:000", dti);

		dti = new CIMDateTimeInterval("00000001010101.1*****:000");
		ref = new DTIRef(1, 1, 1, 1, 100000);
		ref.checkFields(dti);
		checkToString("00000001010101.1*****:000", dti);
		dti = new CIMDateTimeInterval("00000001010101.10****:000");
		ref.checkFields(dti);
		checkToString("00000001010101.10****:000", dti);
		dti = new CIMDateTimeInterval("00000001010101.100***:000");
		ref.checkFields(dti);
		checkToString("00000001010101.100***:000", dti);
		dti = new CIMDateTimeInterval("00000001010101.1000**:000");
		ref.checkFields(dti);
		checkToString("00000001010101.1000**:000", dti);
		dti = new CIMDateTimeInterval("00000001010101.10000*:000");
		ref.checkFields(dti);
		checkToString("00000001010101.10000*:000", dti);
		dti = new CIMDateTimeInterval("00000001010101.100000:000");
		ref.checkFields(dti);
		checkToString("00000001010101.100000:000", dti);

		dti = new CIMDateTimeInterval("00000000000000.000000:000");
		ref = new DTIRef(0, 0, 0, 0, 0);
		ref.checkFields(dti);
		checkToString("00000000000000.000000:000", dti);
		dti = new CIMDateTimeInterval("00000000000000.******:000");
		ref = new DTIRef(0, 0, 0, 0, -1);
		ref.checkFields(dti);
		checkToString("00000000000000.******:000", dti);
		dti = new CIMDateTimeInterval("000000000000**.******:000");
		ref = new DTIRef(0, 0, 0, -1, -1);
		ref.checkFields(dti);
		checkToString("000000000000**.******:000", dti);
		dti = new CIMDateTimeInterval("0000000000****.******:000");
		ref = new DTIRef(0, 0, -1, -1, -1);
		ref.checkFields(dti);
		checkToString("0000000000****.******:000", dti);
		dti = new CIMDateTimeInterval("00000000******.******:000");
		ref = new DTIRef(0, -1, -1, -1, -1);
		ref.checkFields(dti);
		checkToString("00000000******.******:000", dti);
		dti = new CIMDateTimeInterval("**************.******:000");
		ref = new DTIRef(-1, -1, -1, -1, -1);
		ref.checkFields(dti);
		checkToString("**************.******:000", dti);

		negativeConstruction("00******102418.001328:000");
		negativeConstruction("*****133102418.001328:000");

		negativeConstruction("000001331*2418.001328:000");
		negativeConstruction("00000133*02418.001328:000");
		negativeConstruction("0000013310*418.001328:000");
		negativeConstruction("00000133102*18.001328:000");
		negativeConstruction("00000133102418.001328:***");

		negativeConstruction("00000133102418.001328:001");

		negativeConstruction("-0000133102418.001328:000");

		negativeConstruction("00000133242418.001328:000");
		negativeConstruction("00000133-12418.001328:000");

		negativeConstruction("00000133106018.001328:000");
		negativeConstruction("0000013310-118.001328:000");

		negativeConstruction("00000133102460.001328:000");
		negativeConstruction("000001331024-1.001328:000");

		negativeConstruction("00000133102418.-01328:000");

		negativeConstruction(null);
	}

	private void checkCompareTo(CIMDateTimeInterval pThisDTI, CIMDateTimeInterval pThatDTI,
			int pExpectedResult) {
		int res = pThisDTI.compareTo(pThatDTI);
		verify("\"" + pThisDTI + "\".compareTo(\"" + pThatDTI + "\")=" + res + ", but "
				+ pExpectedResult + " was expected!", res == pExpectedResult);
	}

	private void checkTotalMSecs(CIMDateTimeInterval pThisDTI, CIMDateTimeInterval pThatDTI) {
		long resThis = pThisDTI.getTotalMilliseconds();
		long resThat = pThatDTI.getTotalMilliseconds();
		verify("getTotalMilliseconds() mismatch, " + resThis + " != " + resThat + "!",
				resThis == resThat);
	}

	private CIMDateTimeInterval dti(String pStr) {
		return new CIMDateTimeInterval(pStr);
	}

	private CIMDateTimeInterval dti(int pDays, int pHours, int pMins, int pSecs, int pUSecs) {
		return new CIMDateTimeInterval(pDays, pHours, pMins, pSecs, pUSecs);
	}

	private CIMDateTimeInterval dti(long pMillis) {
		return new CIMDateTimeInterval(pMillis);
	}

	/**
	 * testDays
	 */
	public void testDays() {
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS + 1, HOURS, MINS, SECS, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS - 1, HOURS, MINS, SECS, USECS), 1);

		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, -1, -1, -1, -1), 0);
		checkCompareTo(dti("00000133******.******:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
		checkCompareTo(dti("00000133******.******:000"), dti(DAYS, -1, -1, -1, -1), 0);

		checkCompareTo(dti("00000133******.******:000"), dti(DAYS + 1, HOURS, MINS, SECS, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS - 1, -1, -1, -1, -1), 1);
		checkCompareTo(dti("**************.******:000"), dti(-1, -1, -1, -1, -1), 0);
	}

	/**
	 * testHours
	 */
	public void testHours() {
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS + 1, MINS, SECS, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS - 1, MINS, SECS, USECS), 1);

		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, -1, -1, -1), 0);
		checkCompareTo(dti("0000013310****.******:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
		checkCompareTo(dti("0000013310****.******:000"), dti(DAYS, HOURS, -1, -1, -1), 0);

		checkCompareTo(dti("0000013310****.******:000"), dti(DAYS, HOURS + 1, MINS, SECS, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS - 1, -1, -1, -1), 1);
	}

	/**
	 * testMins
	 */
	public void testMins() {
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS + 1, SECS, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS - 1, SECS, USECS), 1);

		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, -1, -1), 0);
		checkCompareTo(dti("000001331024**.******:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
		checkCompareTo(dti("000001331024**.******:000"), dti(DAYS, HOURS, MINS, -1, -1), 0);

		checkCompareTo(dti("000001331024**.******:000"), dti(DAYS, HOURS, MINS + 1, SECS, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS - 1, -1, -1), 1);
	}

	/**
	 * testSecs
	 */
	public void testSecs() {
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS + 1, USECS),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS - 1, USECS), 1);

		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS, -1), 0);
		checkCompareTo(dti("00000133102418.******:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
		checkCompareTo(dti("00000133102418.******:000"), dti(DAYS, HOURS, MINS, SECS, -1), 0);

		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS, USECS), 0);
	}

	/**
	 * testUSecs
	 */
	public void testUSecs() {
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS, USECS + 1),
				-1);
		checkCompareTo(dti("00000133102418.001328:000"), dti(DAYS, HOURS, MINS, SECS, USECS - 1), 1);

		checkCompareTo(dti("00000133102418.00132*:000"), dti("00000133102418.001328:000"), 0);
		checkCompareTo(dti("00000133102418.0013**:000"), dti("00000133102418.001328:000"), 0);
		checkCompareTo(dti("00000133102418.0013**:000"), dti("00000133102418.00132*:000"), 0);
		checkCompareTo(dti("00000133102418.001***:000"), dti("00000133102418.001328:000"), 0);
		checkCompareTo(dti("00000133102418.001***:000"), dti("00000133102418.0013**:000"), 0);
		checkCompareTo(dti("00000133102418.00****:000"), dti("00000133102418.001328:000"), 0);
		checkCompareTo(dti("00000133102418.00****:000"), dti("00000133102418.001***:000"), 0);
		checkCompareTo(dti("00000133102418.0*****:000"), dti("00000133102418.001328:000"), 0);
		checkCompareTo(dti("00000133102418.0*****:000"), dti("00000133102418.00****:000"), 0);
		checkCompareTo(dti("00000133102418.******:000"), dti("00000133102418.001328:000"), 0);
		checkCompareTo(dti("00000133102418.******:000"), dti("00000133102418.0*****:000"), 0);

		checkCompareTo(dti("00000133102418.001***:000"), dti("00000133102418.002328:000"), -1);
		checkCompareTo(dti("00000133102418.002***:000"), dti("00000133102418.001328:000"), 1);

		checkCompareTo(dti("00000000000000.000000:000"), dti(0), 0);
		checkCompareTo(dti("00000000001639.999000:000"), dti(999999), 0);
		checkCompareTo(dti("00000000001639.999000:000"), dti(999998), 1);
		checkCompareTo(dti("00000000001639.998000:000"), dti(999999), -1);

		checkCompareTo(dti("00000000001739.9*****:000"), dti(999999), 1);
	}

	/**
	 * testTotalMSecs()
	 */
	public void testTotalMSecs() {
		checkTotalMSecs(dti("00000133102418.00132*:000"), dti("00000133102418.001328:000"));
		checkTotalMSecs(dti("00000133102418.0132**:000"), dti("00000133102418.01328*:000"));
		checkTotalMSecs(dti("00000133102418.132***:000"), dti("00000133102418.1328**:000"));

		checkTotalMSecs(dti("00000000000000.000000:000"), dti(0, 0, 0, 0, 0));
		checkTotalMSecs(dti("00000000000000.000000:000"), dti(0L));
		checkTotalMSecs(dti("00000000000000.001000:000"), dti(0, 0, 0, 0, 1000));
		checkTotalMSecs(dti("00000000000000.001000:000"), dti(1L));

		checkTotalMSecs(dti("00000001010101.000001:000"), dti(1, 1, 1, 1, 1));
		checkTotalMSecs(dti("00000001010101.001***:000"), dti(1, 1, 1, 1, 1000));
		checkTotalMSecs(dti("00000001010101.******:000"), dti(1, 1, 1, 1, -1));
		checkTotalMSecs(dti("000000010101**.******:000"), dti(1, 1, 1, -1, -1));
		checkTotalMSecs(dti("0000000101****.******:000"), dti(1, 1, -1, -1, -1));
		checkTotalMSecs(dti("00000001******.******:000"), dti(1, -1, -1, -1, -1));
		checkTotalMSecs(dti("**************.******:000"), dti(-1, -1, -1, -1, -1));

		checkTotalMSecs(dti("00000000000000.999999:000"), dti(0, 0, 0, 0, 999999));
		checkTotalMSecs(dti("00000000000000.999999:000"), dti(999L));
		checkTotalMSecs(dti("00000000000059.999999:000"), dti(0, 0, 0, 59, 999999));
		checkTotalMSecs(dti("00000000000059.999999:000"), dti(59999L));
		checkTotalMSecs(dti("00000000005959.999999:000"), dti(0, 0, 59, 59, 999999));
		checkTotalMSecs(dti("00000000005959.999999:000"), dti(3599999L));
		checkTotalMSecs(dti("00000000235959.999999:000"), dti(0, 23, 59, 59, 999999));
		checkTotalMSecs(dti("00000000235959.999999:000"), dti(86399999L));
		checkTotalMSecs(dti("99999999235959.999999:000"), dti(99999999, 23, 59, 59, 999999));
		checkTotalMSecs(dti("99999999235959.999999:000"), dti(8639999999999999L));
	}
}
