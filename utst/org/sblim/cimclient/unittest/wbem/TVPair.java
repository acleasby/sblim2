/**
 * (C) Copyright IBM Corp. 2006, 2012
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
 * 1689085    2007-05-02  ebak         Embedded object enhancements for Pegasus
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1714878    2007-05-08  ebak         Empty string property values are parsed as nulls
 * 1734888    2007-06-11  ebak         Wrong reference building in METHODCALL request
 * 1735693    2007-06-12  ebak         Empty VALUE.ARRAY elements are parsed as nulls
 * 1827728    2007-11-20  ebak         rework: embeddedInstances: attribute EmbeddedObject not set
 * 1848607    2007-12-11  ebak         Strict EmbeddedObject types
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 * 3027615    2010-07-12  blaschke-oss Use CLASS_ARRAY_T instead of new CIMDataType(CLASS,0)
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 */

package org.sblim.cimclient.unittest.wbem;

import java.util.ArrayList;

import javax.cim.CIMClass;
import javax.cim.CIMClassProperty;
import javax.cim.CIMDataType;
import javax.cim.CIMDateTime;
import javax.cim.CIMDateTimeAbsolute;
import javax.cim.CIMDateTimeInterval;
import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger16;
import javax.cim.UnsignedInteger32;
import javax.cim.UnsignedInteger64;
import javax.cim.UnsignedInteger8;

import org.sblim.cimclient.internal.util.Util;

/**
 * TVPair is for type-value pair representation and generation for unit tests.
 */
public class TVPair {

	/**
	 * REF
	 */
	public static final int REF = 1;

	/**
	 * VALUE
	 */
	public static final int VALUE = 2;

	/**
	 * NULL
	 */
	public static final int NULL = 4;

	/**
	 * SCALAR
	 */
	public static final int SCALAR = 8;

	/**
	 * ARRAY
	 */
	public static final int ARRAY = 16;

	/**
	 * EMBOBJ
	 */
	public static final int EMBOBJ = 32;

	/**
	 * ALL
	 */
	public static final int ALL = REF | VALUE | NULL | SCALAR | ARRAY | EMBOBJ;

	private CIMDataType iType;

	private Object iValue;

	/**
	 * Ctor.
	 * 
	 * @param pType
	 * @param pValue
	 */
	public TVPair(CIMDataType pType, Object pValue) {
		this.iType = pType;
		this.iValue = pValue;
	}

	/**
	 * getType
	 * 
	 * @return CIMDataType
	 */
	public CIMDataType getType() {
		return this.iType;
	}

	/**
	 * getValue
	 * 
	 * @return Object
	 */
	public Object getValue() {
		return this.iValue;
	}

	/**
	 * getAll
	 * 
	 * @return TVPair[]
	 */
	public static TVPair[] getAll() {
		return PAIRS;
	}

	/**
	 * getAll
	 * 
	 * @param pBitSet
	 * @return TVPair[]
	 */
	public static TVPair[] getAll(int pBitSet) {
		return getAll(bit(pBitSet, REF), bit(pBitSet, VALUE), bit(pBitSet, NULL), bit(pBitSet,
				SCALAR), bit(pBitSet, ARRAY), bit(pBitSet, EMBOBJ));
	}

	private static boolean isEmbObj(CIMDataType pType) {
		int typeCode = pType.getType();
		return typeCode == CIMDataType.CLASS || typeCode == CIMDataType.OBJECT;
	}

	private static TVPair[] getAll(boolean pRef, boolean pValue, boolean pNullToo, boolean pScalar,
			boolean pArray, boolean pEmbObj) {
		ArrayList<TVPair> aL = new ArrayList<TVPair>();
		for (int i = 0; i < PAIRS.length; i++) {
			TVPair pair = PAIRS[i];
			CIMDataType type = pair.getType();
			if (!pEmbObj && isEmbObj(type)) continue;

			if (!pArray && type.isArray()) continue;
			if (!pScalar && !type.isArray()) continue;

			if (!pRef && type.getType() == CIMDataType.REFERENCE) continue;
			if (!pValue) {
				aL.add(new TVPair(type, null));
			} else {
				aL.add(pair);
				if (pNullToo) aL.add(new TVPair(type, null));
			}
		}
		return aL.toArray(new TVPair[aL.size()]);
	}

	private static boolean bit(int pSet, int pMask) {
		return (pSet & pMask) > 0;
	}

	private static CIMInstance mkInst(String pKeyStr) {
		CIMProperty<String> key = new CIMProperty<String>("Name", CIMDataType.STRING_T, pKeyStr,
				true, false, null);
		return new CIMInstance(Common.LOCALPATH.build("Clazz", null, null),
				new CIMProperty[] { key });
	}

	private static final CIMClass EMB_CLASS = new CIMClass("MyTestClass", null, null,
			new CIMClassProperty[] { new CIMClassProperty<Object>("KeyProp", CIMDataType.STRING_T,
					null, null, false, false, null) }, null);

	private static final TVPair[] PAIRS = {
			new TVPair(CIMDataType.BOOLEAN_T, Boolean.TRUE),
			new TVPair(CIMDataType.BOOLEAN_ARRAY_T, new Boolean[] { Boolean.TRUE, Boolean.FALSE }),
			new TVPair(CIMDataType.CHAR16_T, new Character('c')),
			new TVPair(CIMDataType.CHAR16_ARRAY_T, new Character[] { new Character('a'),
					new Character('b') }),
			new TVPair(CIMDataType.DATETIME_T, new CIMDateTimeAbsolute("20060825155622.000000+000")),
			new TVPair(CIMDataType.DATETIME_ARRAY_T, new CIMDateTime[] {
					new CIMDateTimeAbsolute("20060825155913.000000+000"),
					new CIMDateTimeInterval("00000000160136.000000:000") }),
			new TVPair(CIMDataType.CLASS_T, EMB_CLASS),
			new TVPair(CIMDataType.CLASS_ARRAY_T, new CIMClass[] { EMB_CLASS, EMB_CLASS }),
			new TVPair(CIMDataType.OBJECT_T, mkInst("Lofax")),
			new TVPair(CIMDataType.OBJECT_ARRAY_T, new CIMInstance[] { mkInst("Lofax"),
					mkInst("Jo munkas ember!") }),

			new TVPair(CIMDataType.REAL32_T, new Float(0.01)),
			new TVPair(CIMDataType.REAL32_ARRAY_T, new Float[] { new Float(0.01), new Float(0.02) }),
			new TVPair(CIMDataType.REAL64_T, new Double(0.01)),
			new TVPair(CIMDataType.REAL64_ARRAY_T, new Double[] { new Double(0.01),
					new Double(0.02) }),
			new TVPair(CIMDataType.SINT16_T, new Short((short) -16)),
			new TVPair(CIMDataType.SINT16_ARRAY_T, new Short[] { new Short((short) -3),
					new Short((short) 16) }),
			new TVPair(CIMDataType.SINT32_T, new Integer(-32)),
			new TVPair(CIMDataType.SINT32_ARRAY_T, new Integer[] { new Integer(32),
					new Integer(-32) }),
			new TVPair(CIMDataType.SINT64_T, new Long(-64)),
			new TVPair(CIMDataType.SINT64_ARRAY_T, new Long[] { new Long(64), new Long(-64) }),
			new TVPair(CIMDataType.SINT8_T, new Byte((byte) -8)),
			new TVPair(CIMDataType.SINT8_ARRAY_T, new Byte[] { new Byte((byte) 8),
					new Byte((byte) -8) }),
			new TVPair(CIMDataType.UINT16_T, new UnsignedInteger16(16)),
			new TVPair(CIMDataType.UINT16_ARRAY_T, new UnsignedInteger16[] {
					new UnsignedInteger16(1), new UnsignedInteger16(6) }),
			new TVPair(CIMDataType.UINT32_T, new UnsignedInteger32(32)),
			new TVPair(CIMDataType.UINT32_ARRAY_T, new UnsignedInteger32[] {
					new UnsignedInteger32(3), new UnsignedInteger32(2) }),
			new TVPair(CIMDataType.UINT64_T, new UnsignedInteger64("64")),
			new TVPair(CIMDataType.UINT64_ARRAY_T, new UnsignedInteger64[] {
					new UnsignedInteger64("6"), new UnsignedInteger64("4") }),
			new TVPair(CIMDataType.UINT8_T, new UnsignedInteger8((short) 8)),
			new TVPair(CIMDataType.UINT8_ARRAY_T, new UnsignedInteger8[] {
					new UnsignedInteger8((short) 0), new UnsignedInteger8((short) 8) }),
			new TVPair(CIMDataType.STRING_T, "It's a string."),
			// for testing empty strings
			new TVPair(CIMDataType.STRING_T, ""),
			new TVPair(CIMDataType.STRING_ARRAY_T, new String[] { "String0", "String1", "" }),
			new TVPair(CIMDataType.STRING_ARRAY_T, new String[0]),
			new TVPair(new CIMDataType("CIM_RefClass"), Common.LOCALPATH
					.build("root/cimv2:CIM_RefClass.StrKey=\"This instance.\",RefKey="
							+ Util.quote("root/cimv2:CIM_OtherClass.key=\"Hello\""))) };

}
