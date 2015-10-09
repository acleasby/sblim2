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
 * 1931621    2008-04-02  blaschke-oss CIMDateTimeAbsolute(Calendar) does not respect DST
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2210455    2008-10-30  blaschke-oss Enhance javadoc, fix potential null pointers
 * 2412389    2008-12-09  rgummada     Fix Test case failure: Java5 Complier : CIMDateTimeAbsoluteTest.testDaylightSavingtime
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2806362    2009-06-14  blaschke-oss Missing new CIMDateTimeAbsolute.getUTCOffset() method
 * 2944826    2010-02-08  blaschke-oss getUTCOffset() incorrect if not significant field
 *    2674    2013-09-26  blaschke-oss Null pointer exception in CIMDateTime(String)
 */

package org.sblim.cimclient.unittest.cim;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.cim.CIMDateTimeAbsolute;

import org.sblim.cimclient.unittest.TestCase;

/**
 * CIMDateTimeAbsoluteTest
 */
public class CIMDateTimeAbsoluteTest extends TestCase {

	private static final int YEAR = 2007, MONTH = 3, DAY = 14, HOUR = 16, MIN = 5, SEC = 3,
			USEC = 566012, UTC = 10;

	private class DTARef {

		/**
		 * Ctor. Fills up variables with default values.
		 */
		public DTARef() {
			this(YEAR, MONTH, DAY, HOUR, MIN, SEC, USEC, UTC);
		}

		/**
		 * Ctor.
		 * 
		 * @param pYear
		 * @param pMonth
		 * @param pDay
		 * @param pHour
		 * @param pMin
		 * @param pSec
		 * @param pUSec
		 * @param pUtc
		 */
		public DTARef(int pYear, int pMonth, int pDay, int pHour, int pMin, int pSec, int pUSec,
				int pUtc) {
			this.iYear = pYear;
			this.iMonth = pMonth;
			this.iDay = pDay;
			this.iHour = pHour;
			this.iMin = pMin;
			this.iSec = pSec;
			this.iUSec = pUSec;
			this.iUtc = pUtc;
		}

		private void ver(String pFuncName, int pRef, int pVal) {
			verify(pFuncName + "() function! " + pVal + "!=" + pRef, pVal == pRef);
		}

		/**
		 * checkFields
		 * 
		 * @param pDTA
		 */
		public void checkFields(CIMDateTimeAbsolute pDTA) {
			ver("getYear", this.iYear, pDTA.getYear());
			ver("getMonth", this.iMonth, pDTA.getMonth());
			ver("getDay", this.iDay, pDTA.getDay());
			ver("getHour", this.iHour, pDTA.getHour());
			ver("getMin", this.iMin, pDTA.getMinute());
			ver("getSec", this.iSec, pDTA.getSecond());
			ver("getMicrosecond", this.iUSec, pDTA.getMicrosecond());
			ver("getUTCOffset", this.iUtc, pDTA.getUTCOffset());
			checkToString(pDTA);
		}

		/**
		 * checkToString
		 * 
		 * @param pDTA
		 */
		public void checkToString(CIMDateTimeAbsolute pDTA) {
			/*
			 * yyyymmddhhmmss.uuuuuu:sutc
			 */
			StringReader reader = new StringReader(pDTA.toString());
			checkStrField("year", reader, 4, this.iYear);
			checkStrField("month", reader, 2, this.iMonth);
			checkStrField("day", reader, 2, this.iDay);
			checkStrField("hour", reader, 2, this.iHour);
			checkStrField("minute", reader, 2, this.iMin);
			checkStrField("second", reader, 2, this.iSec);
			char ch = readChar(reader);
			verify("Character after second field must be '.'!", ch == '.');
			checkStrField("microsecond", reader, 6, this.iUSec);
			ch = readChar(reader);
			verify("Character after microsecond field must be '+' or '-'!", ch == '+' || ch == '-');
			checkStrField("utc", reader, 3, (ch == '-' ? -this.iUtc : this.iUtc));
		}

		/**
		 * readChar
		 * 
		 * @param pReader
		 * @return char
		 */
		private char readChar(StringReader pReader) {
			try {
				return (char) pReader.read();
			} catch (Exception e) {
				return 0;
			}
		}

		@SuppressWarnings("null")
		private void checkStrField(String pFieldName, StringReader pReader, int pChars, int pRefVal) {
			String str = read(pReader, pChars);
			verify(pFieldName + " could not be read!", str != null);
			verify(pFieldName + " has got invalid length! " + str.length() + " instead of "
					+ pChars + '.', str.length() == pChars);
			int value = allStar(str) ? -1 : Integer.parseInt(str);
			verify(pFieldName + "'s value(" + value + ") doesn't match with the refValue("
					+ pRefVal + ")!", value == pRefVal);
		}

		private boolean allStar(String pStr) {
			for (int i = 0; i < pStr.length(); i++)
				if (pStr.charAt(i) != '*') return false;
			return true;
		}

		private String read(StringReader pReader, int pChars) {
			char[] buf = new char[pChars];
			try {
				int res = pReader.read(buf);
				if (res <= 0) return null;
				return new String(buf, 0, res);
			} catch (IOException e) {
				return null;
			}
		}

		/**
		 * Year, yyyy in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iYear;

		/**
		 * Month, first mm in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iMonth;

		/**
		 * Day, dd in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iDay;

		/**
		 * Hour, hh in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iHour;

		/**
		 * Minute, last mm in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iMin;

		/**
		 * Second, ss in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iSec;

		/**
		 * Microseconds, uuuuuu in yyyymmddhhmmss.uuuuuu:sutc
		 */
		public int iUSec;

		/**
		 * UTC offset, utc in yyyymmddhhmmss.uuuuuu:sutc where preceding s is +
		 * or -
		 */
		public int iUtc;
	}

	/**
	 * @param pMsg
	 */
	private static void debug(String pMsg) {
	// System.out.println(pMsg);
	}

	/*
	 * To test: - constructors - error cases - methods - toString() -
	 * compareTo()
	 */

	/*
	 * private void checkToString(CIMDateTimeAbsolute pDTA, String pRefStr) {
	 * String toStr = pDTA.toString(); String dtStr = pDTA.getDateTimeString();
	 * verify("\ntoString() :" + toStr + " !=" + "\ngetDateTimeString() :" +
	 * dtStr, toStr .equals(dtStr)); verify("\ntoString() :" + toStr + " !=" +
	 * "\nrefStr :" + pRefStr, pRefStr.equals(toStr)); }
	 */

	/**
	 * testCalendarConstructor
	 */
	public void testCalendarConstructor() {
		int iUtc;
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, YEAR);
		cal.set(Calendar.MONTH, MONTH - 1);
		cal.set(Calendar.DAY_OF_MONTH, DAY);
		cal.set(Calendar.HOUR_OF_DAY, HOUR);
		cal.set(Calendar.MINUTE, MIN);
		cal.set(Calendar.SECOND, SEC);
		cal.set(Calendar.MILLISECOND, USEC / 1000);
		cal.set(Calendar.ZONE_OFFSET, UTC * 60000);
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(cal);

		// handle daylight savings, if applicable
		if (cal.getTimeZone().inDaylightTime(cal.getTime())) {
			iUtc = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / 60000;
		} else {
			iUtc = cal.get(Calendar.ZONE_OFFSET) / 60000;
		}
		DTARef ref = new DTARef(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal
				.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal
				.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
				cal.get(Calendar.MILLISECOND) * 1000, iUtc);
		ref.iUSec = (USEC / 1000) * 1000;
		ref.checkFields(dta);
		// negative case
		cal.set(Calendar.YEAR, 10000);
		try {
			dta = new CIMDateTimeAbsolute(cal);
			verify("IllegalArgumentException was not thrown for year>9999!", false);
		} catch (IllegalArgumentException e) {
			debug(e.getMessage());
		}
	}

	private void testTimeZoneDST(Calendar cal, TimeZone tz) {
		int iUTCoff, iUTCon, iOff;
		cal.setTimeZone(tz);

		cal.set(Calendar.MONTH, Calendar.JANUARY);
		CIMDateTimeAbsolute cdtaDSToff = new CIMDateTimeAbsolute(cal);

		cal.set(Calendar.MONTH, Calendar.JULY);
		CIMDateTimeAbsolute cdtaDSTon = new CIMDateTimeAbsolute(cal);
		debug(tz.getDisplayName() + ": Jan = " + cdtaDSToff.toString() + ", Jul = "
				+ cdtaDSTon.toString());

		iUTCon = cdtaDSTon.getUTCOffset();
		iUTCoff = cdtaDSToff.getUTCOffset();

		if (cal.getTimeZone().inDaylightTime(cal.getTime())) {
			iOff = cal.get(Calendar.DST_OFFSET) / 60000;
			verify("Daylight savings time does not work for TimeZone that observes DST",
					iUTCon == iUTCoff + iOff);
		} else {
			verify("Daylight savings time does not work for TimeZone that does not observe DST",
					iUTCon == iUTCoff);
		}
	}

	/**
	 * testDaylightSavingsTime
	 */
	public void testDaylightSavingsTime() {
		Calendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, YEAR);
		gc.set(Calendar.MONTH, MONTH - 1);
		gc.set(Calendar.DAY_OF_MONTH, DAY);
		gc.set(Calendar.HOUR_OF_DAY, HOUR);
		gc.set(Calendar.MINUTE, MIN);
		gc.set(Calendar.SECOND, SEC);
		gc.set(Calendar.MILLISECOND, USEC / 1000);
		gc.set(Calendar.ZONE_OFFSET, UTC * 60000);
		gc.set(Calendar.DST_OFFSET, gc.get(Calendar.DST_OFFSET)); // java5 bug
		// workaround

		// Phoenix negative offset that does not observe DST
		testTimeZoneDST(gc, TimeZone.getTimeZone("America/Phoenix"));
		// Chicago negative offset that observes DST
		testTimeZoneDST(gc, TimeZone.getTimeZone("America/Chicago"));
		// Tokyo positive offset that does not observe DST
		testTimeZoneDST(gc, TimeZone.getTimeZone("Asia/Tokyo"));
		// Berlin positive offset that observes DST
		testTimeZoneDST(gc, TimeZone.getTimeZone("Europe/Berlin"));
	}

	/**
	 * testMilliSecConstructor
	 */
	public void testStringConstructor() {
		String str = "20070314160503.566012+010";
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(str);
		DTARef ref = new DTARef();
		ref.checkFields(dta);
	}

	private void negativeConstruction(String pFieldName, String pStr) {
		try {
			new CIMDateTimeAbsolute(pStr);
			verify("IllegalArgumentException was not thrown for invalid " + pFieldName
					+ " field in " + pStr + " !", false);
		} catch (IllegalArgumentException e) {
			debug(e.getMessage());
		}
	}

	/**
	 * testStringConstructorNegative
	 */
	public void testStringConstructorNegative() {
		negativeConstruction("", "20070314160503.566012+0100");
		negativeConstruction("", "20070314160503-566012+010");
		negativeConstruction("", "20070314160503.5660120010");
		negativeConstruction("", "200703141605-3.566012+010");
		negativeConstruction("", "200703141605+3.566012+010");
		negativeConstruction("", null);
	}

	/**
	 * testUnsignificantYear
	 */
	public void testUnsignificantYear() {
		String str = "****0314160503.566012+010";
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(str);
		DTARef ref = new DTARef();
		ref.iYear = -1;
		ref.checkFields(dta);
		// negative
		negativeConstruction("year", "***70314160503.566012+010");
		negativeConstruction("year", "2*070314160503.566012+010");
		negativeConstruction("year", "2***0314160503.566012+010");
	}

	/**
	 * testUnsignificantMonth
	 */
	public void testMonth() {
		String str = "2007**14160503.566012+010";
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(str);
		DTARef ref = new DTARef();
		ref.iMonth = -1;
		ref.checkFields(dta);
		// negative
		negativeConstruction("month", "2007*314160503.566012+010");
		negativeConstruction("month", "20070*14160503.566012+010");
		negativeConstruction("month", "20071314160503.566012+010");
		negativeConstruction("month", "2007-114160503.566012+010");
		negativeConstruction("month", "20070014160503.566012+010");
	}

	/**
	 * testDay
	 */
	public void testDay() {
		String str = "200703**160503.566012+010";
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(str);
		DTARef ref = new DTARef();
		ref.iDay = -1;
		ref.checkFields(dta);
		negativeConstruction("day", "200703*4160503.566012+010");
		negativeConstruction("day", "2007031*160503.566012+010");
		negativeConstruction("day", "20070300160503.566012+010");
		negativeConstruction("day", "20070332160503.566012+010");
	}

	/**
	 * testHour
	 */
	public void testHour() {
		String str = "20070314**0503.566012+010";
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(str);
		DTARef ref = new DTARef();
		ref.iHour = -1;
		ref.checkFields(dta);
		negativeConstruction("hour", "20070314*60503.566012+010");
		negativeConstruction("hour", "200703141*0503.566012+010");
		negativeConstruction("hour", "20070314-10503.566012+010");
		negativeConstruction("hour", "20070314320503.566012+010");
	}

	/**
	 * testMinute
	 */
	public void testMinute() {
		String str = "2007031416**03.566012+010";
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute(str);
		DTARef ref = new DTARef();
		ref.iMin = -1;
		ref.checkFields(dta);
		negativeConstruction("minute", "2007031416*503.566012+010");
		negativeConstruction("minute", "20070314160*03.566012+010");
		negativeConstruction("minute", "2007031416-103.566012+010");
		negativeConstruction("minute", "20070314166003.566012+010");
	}

	/**
	 * testSec
	 */
	public void testSec() {
		/*
		 * String str = "200703141605**.566012+010"; CIMDateTimeAbsolute dta =
		 * new CIMDateTimeAbsolute(str);
		 */
		DTARef ref = new DTARef();
		ref.iSec = -1;
		negativeConstruction("sec", "200703141605*3.566012+010");
		negativeConstruction("sec", "2007031416050*.566012+010");
		negativeConstruction("sec", "200703141605-1.566012+010");
		negativeConstruction("sec", "20070314160560.566012+010");
	}

	/**
	 * testUSec
	 */
	public void testUSec() {
		/*
		 * String str = "20070314160503.******+010"; CIMDateTimeAbsolute dta =
		 * new CIMDateTimeAbsolute(str);
		 */
		DTARef ref = new DTARef();
		ref.iUSec = -1;
		negativeConstruction("usec", "20070314160503.**6012+010");
		negativeConstruction("usec", "20070314160503.56**12+010");
		negativeConstruction("usec", "20070314160503.5660**+010");
		negativeConstruction("usec", "20070314160503.-66012+010");
	}

	/**
	 * testUTC
	 */
	public void testUTC() {
		CIMDateTimeAbsolute dta = new CIMDateTimeAbsolute("20070314160503.566012+***");
		DTARef ref = new DTARef();
		ref.iUtc = -1;
		ref.checkFields(dta);

		dta = new CIMDateTimeAbsolute("20070314160503.566012+101");
		ref = new DTARef();
		ref.iUtc = 101;
		ref.checkFields(dta);

		negativeConstruction("utc", "20070314160503.566012+0*0");
		negativeConstruction("utc", "20070314160503.566012+-10");
	}

	private void compare(String pThisStr, String pThatStr, int pExpectedRes) {
		CIMDateTimeAbsolute thisDTA = new CIMDateTimeAbsolute(pThisStr);
		CIMDateTimeAbsolute thatDTA = new CIMDateTimeAbsolute(pThatStr);
		int res = thisDTA.compareTo(thatDTA);
		res = res < 0 ? -1 : (res == 0 ? 0 : 1);
		verify(thisDTA.toString() + " vs. " + thatDTA.toString() + " : " + pExpectedRes + " != "
				+ res + " !", pExpectedRes == res);
	}

	/**
	 * testCompare
	 */
	public void testCompareUTC() {
		compare("20070314160503.566012+010", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+000", "20070314160503.566012+010", -1);
		compare("20070314160503.566012+010", "20070314160503.566012+000", 1);

		compare("**************.******+010", "20070314160503.566012+012", -1);
		compare("20070314160503.566012+010", "**************.******+009", 1);

		compare("20070314160503.566012+***", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+***", "20070314160503.566012+***", 0);
		compare("**************.******+010", "**************.******+011", -1);
		compare("**************.******+010", "**************.******+009", 1);
	}

	/**
	 * testCompareUSec
	 */
	public void testCompareUSec() {
		compare("20070314160503.566012+010", "20070314160503.566013+010", -1);
		compare("20070314160503.566012+010", "20070314160503.566010+010", 1);

		compare("20070314160503.******+010", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+010", "20070314160503.******+010", 0);
		compare("20070314160503.******+010", "20070314160503.******+010", 0);

		compare("**************.566012+010", "**************.566012+010", 0);
		compare("20070314160503.566012+010", "**************.566012+010", 0);
		compare("20070314160503.566012+010", "**************.566015+010", -1);
		compare("20070314160503.566012+010", "**************.566010+010", 1);
		compare("**************.566012+010", "20070314160503.566012+010", 0);
	}

	/**
	 * testCompareSec
	 */
	public void testCompareSec() {
		compare("20070314160503.566012+010", "20070314160504.566012+010", -1);
		compare("20070314160503.566012+010", "20070314160502.566012+010", 1);

		compare("200703141605**.566012+010", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+010", "200703141605**.566012+010", 0);
		compare("200703141605**.566012+010", "200703141605**.566012+010", 0);

		compare("************03.******+010", "************03.******+010", 0);
		compare("20070314160503.566012+010", "************03.******+010", 0);
		compare("************03.******+010", "20070314160504.566012+010", -1);
		compare("20070314160503.566012+010", "************02.******+010", 1);
	}

	/**
	 * testCompareMin
	 */
	public void testCompareMin() {
		compare("20070314160503.566012+010", "20070314160603.566012+010", -1);
		compare("20070314160503.566012+010", "20070314160403.566012+010", 1);

		compare("2007031416**03.566012+010", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+010", "2007031416**03.566012+010", 0);
		compare("2007031416**03.566012+010", "2007031416**03.566012+010", 0);

		compare("**********05**.******+010", "**********05**.******+010", 0);
		compare("20070314160503.566012+010", "**********05**.******+010", 0);
		compare("**********05**.******+010", "20070314160603.566012+010", -1);
		compare("20070314160503.566012+010", "**********04**.566012+010", 1);
	}

	/**
	 * testCompareHour
	 */
	public void testCompareHour() {
		compare("20070314160503.566012+010", "20070314170503.566012+010", -1);
		compare("20070314160503.566012+010", "20070314150503.566012+010", 1);

		compare("20070314**0503.566012+010", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+010", "20070314**0503.566012+010", 0);
		compare("20070314**0503.566012+010", "20070314**0503.566012+010", 0);

		compare("********16****.******+010", "********16****.******+010", 0);
		compare("20070314160503.566012+010", "********16****.******+010", 0);
		compare("********16****.******+010", "20070314170503.566012+010", -1);
		compare("20070314160503.566012+010", "********15****.******+010", 1);
	}

	/**
	 * testCompareDay
	 */
	public void testCompareDay() {
		compare("200703**160503.566012+010", "200703**160503.566012+010", 0);
		compare("20070314160503.566012+010", "20070315160503.566012+010", -1);
		compare("20070314160503.566012+010", "20070313160503.566012+010", 1);

		compare("200703**160503.566012+010", "20070314160503.566012+010", 0);
		compare("20070314160503.566012+010", "200703**160503.566012+010", 0);

		compare("******14******.******+010", "******14******.******+010", 0);
		compare("20070314160503.566012+010", "******14******.******+010", 0);
		compare("******14******.******+010", "20070315160503.566012+010", -1);
		compare("20070314160503.566012+010", "******13******.******+010", 1);
	}

	/**
	 * testCompareMonth
	 */
	public void testCompareMonth() {
		compare("2007**14160503.566012+010", "2007**14160503.566012+010", 0);
		compare("20070314160503.566012+010", "20070414160503.566012+010", -1);
		compare("20070314160503.566012+010", "20070214160503.566012+010", 1);

		compare("****03********.******+010", "****03********.******+010", 0);
		compare("20070314160503.566012+010", "****03********.******+010", 0);
		compare("****03********.******+010", "20070414160503.566012+010", -1);
		compare("20070314160503.566012+010", "****02********.******+010", 1);
		compare("20070314160503.566012+010", "20070314160503.566012+010", 0);
	}

	/**
	 * testCompareYear
	 */
	public void testCompareYear() {
		compare("****0314160503.566012+010", "****0314160503.566012+010", 0);
		compare("20070314160503.566012+010", "20080314160503.566012+010", -1);
		compare("20070314160503.566012+010", "20060314160503.566012+010", 1);

		compare("2007**********.******+010", "2007**********.******+010", 0);
		compare("20070314160503.566012+010", "2007**********.******+010", 0);
		compare("2007**********.******+010", "20080314160503.566012+010", -1);
		compare("20070314160503.566012+010", "2006**********.******+010", 1);
	}

}
