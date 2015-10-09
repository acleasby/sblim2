/**
 * (C) Copyright IBM Corp. 2012, 2013
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
 * 3513353    2012-03-30  blaschke-oss TCK: CIMDataType arrays must have length >= 1
 * 3513349    2012-03-31  blaschke-oss TCK: CIMDataType must not accept null string
 * 3592502    2012-12-04  blaschke-oss Enhance CIMDataType unit test
 *    2632    2013-05-02  blaschke-oss Potential Null Point Exception in CIMDataType
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.CIMClass;
import javax.cim.CIMDataType;
import javax.cim.CIMDateTime;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.UnsignedInteger16;
import javax.cim.UnsignedInteger32;
import javax.cim.UnsignedInteger64;
import javax.cim.UnsignedInteger8;

import org.sblim.cimclient.internal.cim.CIMHelper;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class CIMDataTypeTest is responsible for testing the CIMDataType class
 */
public class CIMDataTypeTest extends TestCase {

	private static final String REF_CLASS = "SampleClass";

	private static final int ARRAY_SIZE = 4;

	private CIMDataType CIMScalarDataTypes[] = {
	/* 00 */CIMDataType.UINT8_T,
	/* 01 */CIMDataType.SINT8_T,
	/* 02 */CIMDataType.UINT16_T,
	/* 03 */CIMDataType.SINT16_T,
	/* 04 */CIMDataType.UINT32_T,
	/* 05 */CIMDataType.SINT32_T,
	/* 06 */CIMDataType.UINT64_T,
	/* 07 */CIMDataType.SINT64_T,
	/* 08 */CIMDataType.STRING_T,
	/* 09 */CIMDataType.BOOLEAN_T,
	/* 10 */CIMDataType.REAL32_T,
	/* 11 */CIMDataType.REAL64_T,
	/* 12 */CIMDataType.DATETIME_T,
	/* 13 */CIMDataType.CHAR16_T,
	/* 14 */new CIMDataType(""),
	/* 15 */CIMDataType.OBJECT_T,
	/* 16 */null,
	/* 17 */CIMDataType.CLASS_T };

	/**
	 * testScalarDataTypes
	 */
	public void testScalarDataTypes() {
		for (int i = 0; i < this.CIMScalarDataTypes.length; i++) {
			CIMDataType type1 = this.CIMScalarDataTypes[i];
			CIMDataType type2 = CIMHelper.ScalarDataType(i);
			if (type1 == null) {
				verify("Scalar data type #" + i + " nulls do not match", type2 == null);
			} else {
				verify("Scalar data type #" + i + " does not match: " + type1.toString() + " != "
						+ type2.toString(), type1.equals(type2));
				verify("Scalar data type #" + i + " fails isArray()", type1.isArray() == false);
			}
		}
	}

	private CIMDataType CIMArrayDataTypes[] = {
	/* 00 */CIMDataType.UINT8_ARRAY_T,
	/* 01 */CIMDataType.SINT8_ARRAY_T,
	/* 02 */CIMDataType.UINT16_ARRAY_T,
	/* 03 */CIMDataType.SINT16_ARRAY_T,
	/* 04 */CIMDataType.UINT32_ARRAY_T,
	/* 05 */CIMDataType.SINT32_ARRAY_T,
	/* 06 */CIMDataType.UINT64_ARRAY_T,
	/* 07 */CIMDataType.SINT64_ARRAY_T,
	/* 08 */CIMDataType.STRING_ARRAY_T,
	/* 09 */CIMDataType.BOOLEAN_ARRAY_T,
	/* 10 */CIMDataType.REAL32_ARRAY_T,
	/* 11 */CIMDataType.REAL64_ARRAY_T,
	/* 12 */CIMDataType.DATETIME_ARRAY_T,
	/* 13 */CIMDataType.CHAR16_ARRAY_T,
	/* 14 */new CIMDataType("", 0),
	/* 15 */CIMDataType.OBJECT_ARRAY_T,
	/* 16 */null,
	/* 17 */CIMDataType.CLASS_ARRAY_T };

	/**
	 * testUnboundedArrayDataTypes
	 */
	public void testUnboundedArrayDataTypes() {
		for (int i = 0; i < this.CIMArrayDataTypes.length; i++) {
			CIMDataType type1 = this.CIMArrayDataTypes[i];
			CIMDataType type2 = CIMHelper.UnboundedArrayDataType(i);
			if (type1 == null) {
				verify("Unbounded array data type #" + i + " nulls do not match", type2 == null);
			} else {
				verify("Unbounded array data type #" + i + " does not match: " + type1.toString()
						+ " != " + type2.toString(), type1.equals(type2));
				verify("Unbounded array data type #" + i + " fails isArray()",
						type1.isArray() == true);
				verify("Unbounded array data type #" + i + " fails getSize()", type1.getSize() == 0);
			}
		}
	}

	/**
	 * testBoundedArrayDataTypes
	 */
	public void testBoundedArrayDataTypes() {
		for (int i = 0; i < this.CIMArrayDataTypes.length; i++) {
			CIMDataType type1 = new CIMDataType(i, ARRAY_SIZE);
			CIMDataType type2 = CIMHelper.UnboundedArrayDataType(i);
			if (type2 != null) {
				verify("Bounded array data type #" + i + " does not match: " + type1.getType()
						+ " != " + type2.getType(), type1.getType() == type2.getType());
				verify("Bounded array data type #" + i + " fails isArray()",
						type1.isArray() == true);
				verify("Bounded array data type #" + i + " fails getSize()",
						type1.getSize() == ARRAY_SIZE);
			}
		}
	}

	/**
	 * testReferenceType
	 */
	public void testReferenceType() {
		CIMDataType type = new CIMDataType(REF_CLASS);
		verify("Scalar reference data type getRefClassName() does not match: "
				+ type.getRefClassName() + " != " + REF_CLASS, type.getRefClassName().compareTo(
				REF_CLASS) == 0);
		verify("Scalar reference data type getType() should not be " + type.getType(), type
				.getType() == CIMDataType.REFERENCE);
		verify("Scalar reference data type isArray() should not be " + type.isArray(), type
				.isArray() == false);

		type = new CIMDataType(REF_CLASS, 0);
		verify("Unbounded array reference data type getRefClassName() does not match: "
				+ type.getRefClassName() + " != " + REF_CLASS, type.getRefClassName().compareTo(
				REF_CLASS) == 0);
		verify("Unbounded array reference data type getType() should not be " + type.getType(),
				type.getType() == CIMDataType.REFERENCE);
		verify("Unbounded array reference data type isArray() should not be " + type.isArray(),
				type.isArray() == true);
		verify("Unbounded array reference data type getSize() should not be " + type.getSize(),
				type.getSize() == 0);

		type = new CIMDataType(REF_CLASS, ARRAY_SIZE);
		verify("Bounded array reference data type getRefClassName() does not match: "
				+ type.getRefClassName() + " != " + REF_CLASS, type.getRefClassName().compareTo(
				REF_CLASS) == 0);
		verify("Bounded array reference data type getType() should not be " + type.getType(), type
				.getType() == CIMDataType.REFERENCE);
		verify("Bounded array reference data type isArray() should not be " + type.isArray(), type
				.isArray() == true);
		verify("Bounded array reference data type getSize() should not be " + type.getSize(), type
				.getSize() == ARRAY_SIZE);

		type = new CIMDataType("");
		verify("Empty scalar reference data type getRefClassName() does not match: "
				+ type.getRefClassName() + " != \"\"", type.getRefClassName().compareTo("") == 0);
		verify("Empty scalar reference data type getType() should not be " + type.getType(), type
				.getType() == CIMDataType.REFERENCE);
		verify("Empty scalar reference data type isArray() should not be " + type.isArray(), type
				.isArray() == false);

		try {
			type = new CIMDataType(null);
			verify("Null scalar reference data type did not result in exception", false);
		} catch (Exception e) {
			verify("Null scalar reference data type did not result in IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}

		type = new CIMDataType("", 0);
		verify("Empty unbounded array reference data type getRefClassName() does not match: "
				+ type.getRefClassName() + " != \"\"", type.getRefClassName().compareTo("") == 0);
		verify("Empty unbounded array reference data type getType() should not be "
				+ type.getType(), type.getType() == CIMDataType.REFERENCE);
		verify("Empty unbounded array reference data type isArray() should not be "
				+ type.isArray(), type.isArray() == true);
		verify("Empty unbounded array reference data type getSize() should not be "
				+ type.getSize(), type.getSize() == 0);

		try {
			type = new CIMDataType(null, 0);
			verify("Null unbounded array reference data type did not result in exception", false);
		} catch (Exception e) {
			verify(
					"Null unbounded array reference data type did not result in IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}

		type = new CIMDataType("", ARRAY_SIZE);
		verify("Empty bounded array reference data type getRefClassName() does not match: "
				+ type.getRefClassName() + " != \"\"", type.getRefClassName().compareTo("") == 0);
		verify("Empty bounded array reference data type getType() should not be " + type.getType(),
				type.getType() == CIMDataType.REFERENCE);
		verify("Empty bounded array reference data type isArray() should not be " + type.isArray(),
				type.isArray() == true);
		verify("Empty bounded array reference data type getSize() should not be " + type.getSize(),
				type.getSize() == ARRAY_SIZE);

		try {
			type = new CIMDataType(null, ARRAY_SIZE);
			verify("Null bounded array reference data type did not result in exception", false);
		} catch (Exception e) {
			verify(
					"Null bounded array reference data type did not result in IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}
	}

	private CIMObjectPath[] copA = { new CIMObjectPath(
			"http://1.2.3.4:5/root/cimv2:My_ComputerSystem") };

	private Object ArrayDataTypes[] = {
	/* 00 */new UnsignedInteger8[1],
	/* 01 */new Byte[1],
	/* 02 */new UnsignedInteger16[1],
	/* 03 */new Short[1],
	/* 04 */new UnsignedInteger32[1],
	/* 05 */new Integer[1],
	/* 06 */new UnsignedInteger64[1],
	/* 07 */new Long[1],
	/* 08 */new String[1],
	/* 09 */new Boolean[1],
	/* 10 */new Float[1],
	/* 11 */new Double[1],
	/* 12 */new CIMDateTime[1],
	/* 13 */new Character[1],
	/* 14 */this.copA,
	/* 15 */new CIMInstance[1],
	/* 16 */null,
	/* 17 */new CIMClass[1] };

	/**
	 * testGetDataType
	 */
	public void testGetDataType() {
		CIMDataType type;
		for (int i = 0; i < this.ArrayDataTypes.length; i++) {
			if (this.ArrayDataTypes[i] != null) {
				type = CIMDataType.getDataType(this.ArrayDataTypes[i]);
				verify("Array data type #" + i + " does not match: " + type.getType() + " != " + i,
						type.getType() == i);
			}
		}

		try {
			type = CIMDataType.getDataType(null);
			verify("Null data type did not result in exception", false);
		} catch (Exception e) {
			verify("Null data type did not result in IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}

		try {
			type = CIMDataType.getDataType(new StringBuffer());
			verify("Bad data type did not result in exception", false);
		} catch (Exception e) {
			verify("Bad data type did not result in IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}

		try {
			type = CIMDataType.getDataType(new CIMObjectPath[] { null, null, this.copA[0] });
			verify(
					"COP array with one non-null entry did not result in exception but type invalid: "
							+ type.getType(), type.getType() == CIMDataType.REFERENCE);
		} catch (Exception e) {
			verify("COP array with one non-null entry resulted in exception", false);
		}

		try {
			type = CIMDataType.getDataType(new CIMObjectPath[] { null, null, null });
			verify("COP array with all null entries did not result in exception", false);
		} catch (Exception e) {
			verify("COP array with all null entries did not result in IllegalArgumentException",
					e instanceof IllegalArgumentException);
		}
	}
}
