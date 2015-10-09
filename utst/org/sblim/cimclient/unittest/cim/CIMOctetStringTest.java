/**
 * (C) Copyright IBM Corp. 2011
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Dave Blaschke, IBM, blaschke@us.ibm.com 
 * 
 * Change History
 * Flag       Date        Prog         Description
 * ------------------------------------------------------------------------
 * 3397922    2011-08-30  blaschke-oss support OctetString	
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.UnsignedInteger8;

import org.sblim.cimclient.internal.cim.CIMOctetString;
import org.sblim.cimclient.unittest.TestCase;

/**
 * 
 * Class CIMOctetStringTest is responsible for testing CIMOctetString
 * 
 */
public class CIMOctetStringTest extends TestCase {

	// Make sure the entries in goodByte, goodHex and goodAscii match up!!!
	UnsignedInteger8 goodByte[][] = {
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("4") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("5"), new UnsignedInteger8("32") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("17"), new UnsignedInteger8("72"),
					new UnsignedInteger8("101"), new UnsignedInteger8("108"),
					new UnsignedInteger8("108"), new UnsignedInteger8("111"),
					new UnsignedInteger8("44"), new UnsignedInteger8("32"),
					new UnsignedInteger8("119"), new UnsignedInteger8("111"),
					new UnsignedInteger8("114"), new UnsignedInteger8("108"),
					new UnsignedInteger8("100"), new UnsignedInteger8("33") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("12"), new UnsignedInteger8("32"),
					new UnsignedInteger8("33"), new UnsignedInteger8("34"),
					new UnsignedInteger8("35"), new UnsignedInteger8("36"),
					new UnsignedInteger8("37"), new UnsignedInteger8("38"),
					new UnsignedInteger8("39") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("40"),
					new UnsignedInteger8("41"), new UnsignedInteger8("42"),
					new UnsignedInteger8("43"), new UnsignedInteger8("44"),
					new UnsignedInteger8("45"), new UnsignedInteger8("46"),
					new UnsignedInteger8("47"), new UnsignedInteger8("48"),
					new UnsignedInteger8("49") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("50"),
					new UnsignedInteger8("51"), new UnsignedInteger8("52"),
					new UnsignedInteger8("53"), new UnsignedInteger8("54"),
					new UnsignedInteger8("55"), new UnsignedInteger8("56"),
					new UnsignedInteger8("57"), new UnsignedInteger8("58"),
					new UnsignedInteger8("59") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("60"),
					new UnsignedInteger8("61"), new UnsignedInteger8("62"),
					new UnsignedInteger8("63"), new UnsignedInteger8("64"),
					new UnsignedInteger8("65"), new UnsignedInteger8("66"),
					new UnsignedInteger8("67"), new UnsignedInteger8("68"),
					new UnsignedInteger8("69") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("70"),
					new UnsignedInteger8("71"), new UnsignedInteger8("72"),
					new UnsignedInteger8("73"), new UnsignedInteger8("74"),
					new UnsignedInteger8("75"), new UnsignedInteger8("76"),
					new UnsignedInteger8("77"), new UnsignedInteger8("78"),
					new UnsignedInteger8("79") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("80"),
					new UnsignedInteger8("81"), new UnsignedInteger8("82"),
					new UnsignedInteger8("83"), new UnsignedInteger8("84"),
					new UnsignedInteger8("85"), new UnsignedInteger8("86"),
					new UnsignedInteger8("87"), new UnsignedInteger8("88"),
					new UnsignedInteger8("89") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("90"),
					new UnsignedInteger8("91"), new UnsignedInteger8("92"),
					new UnsignedInteger8("93"), new UnsignedInteger8("94"),
					new UnsignedInteger8("95"), new UnsignedInteger8("96"),
					new UnsignedInteger8("97"), new UnsignedInteger8("98"),
					new UnsignedInteger8("99") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("100"),
					new UnsignedInteger8("101"), new UnsignedInteger8("102"),
					new UnsignedInteger8("103"), new UnsignedInteger8("104"),
					new UnsignedInteger8("105"), new UnsignedInteger8("106"),
					new UnsignedInteger8("107"), new UnsignedInteger8("108"),
					new UnsignedInteger8("109") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("14"), new UnsignedInteger8("110"),
					new UnsignedInteger8("111"), new UnsignedInteger8("112"),
					new UnsignedInteger8("113"), new UnsignedInteger8("114"),
					new UnsignedInteger8("115"), new UnsignedInteger8("116"),
					new UnsignedInteger8("117"), new UnsignedInteger8("118"),
					new UnsignedInteger8("119") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("11"), new UnsignedInteger8("120"),
					new UnsignedInteger8("121"), new UnsignedInteger8("122"),
					new UnsignedInteger8("123"), new UnsignedInteger8("124"),
					new UnsignedInteger8("125"), new UnsignedInteger8("126") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("1"),
					new UnsignedInteger8("8"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48"), new UnsignedInteger8("49"),
					new UnsignedInteger8("50"), new UnsignedInteger8("51"),
					new UnsignedInteger8("52"), new UnsignedInteger8("53"),
					new UnsignedInteger8("54"), new UnsignedInteger8("55"),
					new UnsignedInteger8("56"), new UnsignedInteger8("57"),
					new UnsignedInteger8("48") } };

	String goodHex[] = {
			"0x00000004",
			"0x0000000520",
			"0x0000001148656C6C6F2C20776F726C6421",
			"0x0000000C2021222324252627",
			"0x0000000E28292A2B2C2D2E2F3031",
			"0x0000000E32333435363738393A3B",
			"0x0000000E3C3D3E3F404142434445",
			"0x0000000E464748494A4B4C4D4E4F",
			"0x0000000E50515253545556575859",
			"0x0000000E5A5B5C5D5E5F60616263",
			"0x0000000E6465666768696A6B6C6D",
			"0x0000000E6E6F7071727374757677",
			"0x0000000B78797A7B7C7D7E",
			"0x000001083132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930" };

	String goodAscii[] = {
			"",
			" ",
			"Hello, world!",
			" !\"#$%&\'",
			"()*+,-./01",
			"23456789:;",
			"<=>?@ABCDE",
			"FGHIJKLMNO",
			"PQRSTUVWXY",
			"Z[\\]^_`abc",
			"defghijklm",
			"nopqrstuvw",
			"xyz{|}~",
			"12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" };

	UnsignedInteger8 goodByteUnprint[][] = {
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("9"), new UnsignedInteger8("127"),
					new UnsignedInteger8("128"), new UnsignedInteger8("129"),
					new UnsignedInteger8("130"), new UnsignedInteger8("131") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("9"), new UnsignedInteger8("27"),
					new UnsignedInteger8("28"), new UnsignedInteger8("29"),
					new UnsignedInteger8("30"), new UnsignedInteger8("31") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("9"), new UnsignedInteger8("48"),
					new UnsignedInteger8("0"), new UnsignedInteger8("48"),
					new UnsignedInteger8("0"), new UnsignedInteger8("48") } };

	String goodHexUnprint[] = { "0x000000097F80818283", "0x000000091B1C1D1E1F",
			"0x000000093000300030" };

	String goodAsciiUnprint[] = { ".....", ".....", "0.0.0" };

	UnsignedInteger8 badByte[][] = {
			null,
			{ new UnsignedInteger8("0") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), null, new UnsignedInteger8("4") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("5"), null },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("2") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("4"), new UnsignedInteger8("32") },
			{ new UnsignedInteger8("0"), new UnsignedInteger8("0"), new UnsignedInteger8("0"),
					new UnsignedInteger8("50"), new UnsignedInteger8("72"),
					new UnsignedInteger8("101"), new UnsignedInteger8("108"),
					new UnsignedInteger8("108"), new UnsignedInteger8("111"),
					new UnsignedInteger8("44"), new UnsignedInteger8("32"),
					new UnsignedInteger8("119"), new UnsignedInteger8("111"),
					new UnsignedInteger8("114"), new UnsignedInteger8("108"),
					new UnsignedInteger8("100"), new UnsignedInteger8("33") } };

	String badHex[] = { null, "", "00000004", "x00000004", "0x00000005", "0x0000000623",
			"0x00000005C", "0x000000050S", "0xLENGTH04" };

	String badAscii[] = { null };

	private boolean equalByteArrays(UnsignedInteger8 a[], UnsignedInteger8 b[]) {
		if (a == null) return (b == null);
		if (b == null) return false;
		if (a.length != b.length) return false;
		for (int i = 0; i < a.length; i++)
			if (a[i].byteValue() != b[i].byteValue()) return false;
		return true;
	}

	/**
	 * Tests CIMOctetString(UnsignedInteger8[]) constructor with valid input
	 */
	public void testByteConstructorGood() {
		for (int i = 0; i < this.goodByte.length; i++) {
			CIMOctetString os = new CIMOctetString(this.goodByte[i]);
			verify(
					"Byte array constructor, ascii string " + this.goodAscii[i]
							+ " does not match!", this.goodAscii[i].equalsIgnoreCase(os
							.getASCIIString('.')));
			verify("Byte array constructor, hex string " + this.goodHex[i] + " does not match!",
					this.goodHex[i].equalsIgnoreCase(os.getHexString()));
		}
		for (int i = 0; i < this.goodByteUnprint.length; i++) {
			CIMOctetString os = new CIMOctetString(this.goodByteUnprint[i]);
			verify("Byte array constructor, ascii string " + this.goodAsciiUnprint[i]
					+ " does not match!", this.goodAsciiUnprint[i].equalsIgnoreCase(os
					.getASCIIString('.')));
			verify("Byte array constructor, hex string " + this.goodHexUnprint[i]
					+ " does not match!", this.goodHexUnprint[i]
					.equalsIgnoreCase(os.getHexString()));
		}
	}

	/**
	 * Tests CIMOctetString(String, true) constructor with valid input
	 */
	public void testHexConstructorGood() {
		for (int i = 0; i < this.goodHex.length; i++) {
			CIMOctetString os = new CIMOctetString(this.goodHex[i], true);
			verify(
					"Hex string constructor for " + this.goodHex[i]
							+ ", byte array does not match!", equalByteArrays(this.goodByte[i], os
							.getBytes()));
			verify("Hex string constructor for " + this.goodHex[i]
					+ ", ascii string does not match!", this.goodAscii[i].equalsIgnoreCase(os
					.getASCIIString('.')));
		}
		for (int i = 0; i < this.goodHexUnprint.length; i++) {
			CIMOctetString os = new CIMOctetString(this.goodHexUnprint[i], true);
			verify("Hex string constructor for " + this.goodHexUnprint[i]
					+ ", byte array does not match!", equalByteArrays(this.goodByteUnprint[i], os
					.getBytes()));
			verify("Hex string constructor for " + this.goodHexUnprint[i]
					+ ", ascii string does not match!", this.goodAsciiUnprint[i]
					.equalsIgnoreCase(os.getASCIIString('.')));
		}
	}

	/**
	 * Tests CIMOctetString(String, false) constructor with valid input
	 */
	public void testAsciiConstructorGood() {
		for (int i = 0; i < this.goodAscii.length; i++) {
			CIMOctetString os = new CIMOctetString(this.goodAscii[i], false);
			verify("Ascii string constructor for " + this.goodAscii[i]
					+ ", byte array does not match!", equalByteArrays(this.goodByte[i], os
					.getBytes()));
			verify("Ascii string constructor for " + this.goodAscii[i]
					+ ", hex string does not match!", this.goodHex[i].equalsIgnoreCase(os
					.getHexString()));
		}
	}

	/**
	 * Tests CIMOctetString(UnsignedInteger8[]) constructor with invalid input
	 */
	public void testByteConstructorBad() {
		for (int i = 0; i < this.badByte.length; i++) {
			try {
				new CIMOctetString(this.badByte[i]);
				verify(
						"IllegalArgumentException was not thrown for invalid byte array constructor #"
								+ i, false);
			} catch (IllegalArgumentException e) {
				// System.out.println("Invalid byte array constructor threw " +
				// e.toString());
			}
		}
	}

	/**
	 * Tests CIMOctetString(String, true) constructor with invalid input
	 */
	public void testHexConstructorBad() {
		for (int i = 0; i < this.badHex.length; i++) {
			try {
				new CIMOctetString(this.badHex[i], true);
				verify(
						"IllegalArgumentException was not thrown for invalid hex string constructor #"
								+ i, false);
			} catch (IllegalArgumentException e) {
				// System.out.println("Invalid hex string constructor" +
				// e.toString());
			}
		}
	}

	/**
	 * Tests CIMOctetString(String, false) constructor with invalid input
	 */
	public void testAsciiConstructorBad() {
		for (int i = 0; i < this.badAscii.length; i++) {
			try {
				new CIMOctetString(this.badAscii[i], false);
				verify(
						"IllegalArgumentException was not thrown for invalid ascii string constructor #"
								+ i, false);
			} catch (IllegalArgumentException e) {
				// System.out.println("Invalid ascii string constructor" +
				// e.toString());
			}
		}
	}

	/**
	 * Tests equals()
	 */
	public void testEquals() {
		CIMOctetString osUpper = new CIMOctetString("0x000000075A5B58", true);
		CIMOctetString osLower = new CIMOctetString("0x000000075a5b58", true);
		verify("Different case hex strings not equal!", osUpper.equals(osLower));
		verify("Hashcodes for different case hex strings not equal!", osUpper.hashCode() == osLower
				.hashCode());

		for (int i = 0; i < this.goodByte.length; i++) {
			CIMOctetString os1 = new CIMOctetString(this.goodByte[i]);
			CIMOctetString os2 = new CIMOctetString(this.goodByte[i]);
			CIMOctetString os3 = new CIMOctetString(this.goodHex[i], true);
			CIMOctetString os4 = new CIMOctetString(this.goodHex[i], true);
			CIMOctetString os5 = new CIMOctetString(this.goodAscii[i], false);
			CIMOctetString os6 = new CIMOctetString(this.goodAscii[i], false);
			verify("Byte array does not equal byte array for index " + i + "!", os1.equals(os2));
			verify("Byte array does not equal hex string for index " + i + "!", os1.equals(os3));
			verify("Byte array does not equal ascii string for index " + i + "!", os1.equals(os5));
			verify("Hex string does not equal byte array for index " + i + "!", os3.equals(os1));
			verify("Hex string does not equal hex string for index " + i + "!", os3.equals(os4));
			verify("Hex string does not equal ascii string for index " + i + "!", os3.equals(os5));
			verify("Ascii string does not equal byte array for index " + i + "!", os5.equals(os1));
			verify("Ascii string does not equal hex string for index " + i + "!", os5.equals(os3));
			verify("Ascii string does not equal ascii string for index " + i + "!", os5.equals(os6));
			verify("Byte array hashcode does not equal byte array hashcode for index " + i + "!",
					os1.hashCode() == os2.hashCode());
			verify("Byte array hashcode does not equal hex string hashcode for index " + i + "!",
					os1.hashCode() == os3.hashCode());
			verify("Byte array hashcode does not equal ascii string hashcode for index " + i + "!",
					os1.hashCode() == os5.hashCode());
			verify("Hex string hashcode does not equal byte array hashcode for index " + i + "!",
					os3.hashCode() == os1.hashCode());
			verify("Hex string hashcode does not equal hex string hashcode for index " + i + "!",
					os3.hashCode() == os4.hashCode());
			verify("Hex string hashcode does not equal ascii string hashcode for index " + i + "!",
					os3.hashCode() == os5.hashCode());
			verify("Ascii string hashcode does not equal byte array hashcode for index " + i + "!",
					os5.hashCode() == os1.hashCode());
			verify("Ascii string hashcode does not equal hex string hashcode for index " + i + "!",
					os5.hashCode() == os3.hashCode());
			verify("Ascii string hashcode does not equal ascii string hashcode for index " + i
					+ "!", os5.hashCode() == os6.hashCode());
		}

		for (int i = 0; i < this.goodByte.length; i++) {
			CIMOctetString osArrayi[] = { new CIMOctetString(this.goodByte[i]),
					new CIMOctetString(this.goodHex[i], true),
					new CIMOctetString(this.goodAscii[i], false) };
			for (int j = 0; j < this.goodByte.length; j++) {
				CIMOctetString osArrayj[] = { new CIMOctetString(this.goodByte[j]),
						new CIMOctetString(this.goodHex[j], true),
						new CIMOctetString(this.goodAscii[j], false) };
				// Byte array = 0, hex string = 1, ascii string = 2
				if (i != j) {
					verify("Byte array #" + i + " equals hex string #" + j + "!", !osArrayi[0]
							.equals(osArrayj[1]));
					verify("Byte array #" + i + " equals ascii string #" + j + "!", !osArrayi[0]
							.equals(osArrayj[2]));
					verify("Hex string #" + i + " equals ascii string #" + j + "!", !osArrayi[1]
							.equals(osArrayj[2]));
					verify("Byte array #" + i + " hashcode equals hex string #" + j + " hashcode!",
							osArrayi[0].hashCode() != osArrayj[1].hashCode());
					verify(
							"Byte array #" + i + " hashcode equals ascii string #" + j
									+ "hashcode!", osArrayi[0].hashCode() != osArrayj[2].hashCode());
					verify(
							"Hex string #" + i + " hashcode equals ascii string #" + j
									+ "hashcode!", osArrayi[1].hashCode() != osArrayj[2].hashCode());
				}
			}
		}
	}
}
