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
 * 1776114    2007-08-21  ebak         Cannot derive instance of class CIM_IndicationSubscription
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 * 3598613    2013-01-11  blaschke-oss different data type in cim instance and cim object path
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger16;
import javax.cim.UnsignedInteger32;
import javax.cim.UnsignedInteger64;
import javax.cim.UnsignedInteger8;

import org.sblim.cimclient.internal.cim.CIMHelper;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class CIMInstanceTest is responsible for testing CIMInstance.
 * 
 */
public class CIMInstanceTest extends TestCase {

	private static CIMProperty<Object> mkProp(String pName, Object pValue, boolean pKey,
			boolean pPropagated, String pOriginClass) {
		return new CIMProperty<Object>(pName, CIMDataType.getDataType(pValue), pValue, pKey,
				pPropagated, pOriginClass);
	}

	private static CIMProperty<Object> mkKey(String pName, Object pValue) {
		return mkProp(pName, pValue, true, false, null);
	}

	private static CIMProperty<CIMObjectPath> mkProp(String pName, String pRefClassName,
			CIMObjectPath pPath) {
		return new CIMProperty<CIMObjectPath>(pName, new CIMDataType(pRefClassName), pPath, false,
				false, null);
	}

	private static final CIMProperty<?>[] KEY_PROPS = { mkKey("StringProp", "Hello") };

	private static final CIMProperty<?>[] INST_PROPS = {
			mkKey("StringProp", "Hello"),
			mkProp("RefProp0", "BaseClass", new CIMObjectPath(null, null, null, "root/cimv2",
					"Clazz", new CIMProperty[] { mkKey("KeyProp", "ZzZzzz...") })) };

	private static final CIMProperty<?>[] DERIVED_PROPS = {
			mkKey("StringProp", "Hallo"),
			mkProp("RefProp0", "DerivedClass", new CIMObjectPath(null, null, null, "root/cimv2",
					"Clazz", new CIMProperty[] { mkKey("KeyProp", "ZzZzzz...") })) };

	private static final CIMInstance INST = new CIMInstance(new CIMObjectPath(null, null, null,
			"root/cimv2", "CIM_Anything", KEY_PROPS), INST_PROPS);

	private static final CIMInstance DERIVED_INST = new CIMInstance(new CIMObjectPath(null, null,
			null, "root/cimv2", "CIM_Anything", KEY_PROPS), DERIVED_PROPS);

	/**
	 * tests CIMInstance.deriveInstance(CIMProperty[])
	 */
	public void testDeriveInstanceProperties() {
		CIMInstance derived = INST.deriveInstance(DERIVED_PROPS);
		verify("Wrong deriveInstance() result! :\n" + derived + "\nExpected result is :\n"
				+ DERIVED_INST, DERIVED_INST.equals(derived));
	}

	/**
	 * testNumericKeys tests that CIMInstanceWithSynchonizedNumericKeyDataTypes
	 * correctly updates numeric data types of keys in CIMObjectPath to reflect
	 * the data type of the corresponding keys from CIMProperty[]
	 */
	public void testNumericKeys() {
		CIMProperty<?>[] copProps, instProps;

		CIMProperty<Byte> s8 = new CIMProperty<Byte>("Prop_Integer", CIMDataType.SINT8_T, Byte
				.valueOf("21"), true, false, null);
		CIMProperty<Short> s16 = new CIMProperty<Short>("Prop_Integer", CIMDataType.SINT16_T, Short
				.valueOf("21"), true, false, null);
		CIMProperty<Integer> s32 = new CIMProperty<Integer>("Prop_Integer", CIMDataType.SINT32_T,
				Integer.valueOf("21"), true, false, null);
		CIMProperty<Long> s64 = new CIMProperty<Long>("Prop_Integer", CIMDataType.SINT64_T, Long
				.valueOf("21"), true, false, null);
		CIMProperty<UnsignedInteger8> u8 = new CIMProperty<UnsignedInteger8>("Prop_Integer",
				CIMDataType.UINT8_T, new UnsignedInteger8("21"), true, false, null);
		CIMProperty<UnsignedInteger16> u16 = new CIMProperty<UnsignedInteger16>("Prop_Integer",
				CIMDataType.UINT16_T, new UnsignedInteger16("21"), true, false, null);
		CIMProperty<UnsignedInteger32> u32 = new CIMProperty<UnsignedInteger32>("Prop_Integer",
				CIMDataType.UINT32_T, new UnsignedInteger32("21"), true, false, null);
		CIMProperty<UnsignedInteger64> u64 = new CIMProperty<UnsignedInteger64>("Prop_Integer",
				CIMDataType.UINT64_T, new UnsignedInteger64("21"), true, false, null);
		CIMProperty<Float> r32 = new CIMProperty<Float>("Prop_Real", CIMDataType.REAL32_T, Float
				.valueOf("21.0"), true, false, null);
		CIMProperty<Double> r64 = new CIMProperty<Double>("Prop_Real", CIMDataType.REAL64_T, Double
				.valueOf("21.0"), true, false, null);
		CIMProperty<String> str = new CIMProperty<String>("Prop_String", CIMDataType.STRING_T,
				"21.0", true, false, null);
		CIMProperty<Boolean> bool = new CIMProperty<Boolean>("Prop_Boolean", CIMDataType.BOOLEAN_T,
				Boolean.TRUE, true, false, null);
		CIMProperty<Character> ch = new CIMProperty<Character>("Prop_Char", CIMDataType.CHAR16_T,
				Character.valueOf('2'), true, false, null);

		// Change sint8 to other sint types
		copProps = new CIMProperty[] { bool, s8, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, s16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s32, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s64, bool, str }, "Prop_Integer");

		// Change sint16 to other sint types
		copProps = new CIMProperty[] { bool, s16, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, s8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s32, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s64, bool, str }, "Prop_Integer");

		// Change sint32 to other sint types
		copProps = new CIMProperty[] { bool, s32, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, s8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s64, bool, str }, "Prop_Integer");

		// Change sint64 to other sint types
		copProps = new CIMProperty[] { bool, s64, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, s8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s32, bool, str }, "Prop_Integer");

		// Change uint8 to other uint types
		copProps = new CIMProperty[] { bool, u8, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, u16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u32, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u64, bool, str }, "Prop_Integer");

		// Change uint16 to other uint types
		copProps = new CIMProperty[] { bool, u16, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, u8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u32, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u64, bool, str }, "Prop_Integer");

		// Change uint32 to other uint types
		copProps = new CIMProperty[] { bool, u32, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, u8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u64, bool, str }, "Prop_Integer");

		// Change uint64 to other uint types
		copProps = new CIMProperty[] { bool, u64, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, u8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u32, bool, str }, "Prop_Integer");

		// Change real32 to other real type
		checkKeyChanged(new CIMProperty[] { bool, r32, str }, new CIMProperty[] { ch, r64, bool,
				str }, "Prop_Real");

		// Change real64 to other real type
		checkKeyChanged(new CIMProperty[] { bool, r64, str }, new CIMProperty[] { ch, r32, bool,
				str }, "Prop_Real");

		// Change sint32 to other uint types
		copProps = new CIMProperty[] { bool, s32, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, u8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u32, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, u64, bool, str }, "Prop_Integer");

		// Change uint32 to other sint types
		copProps = new CIMProperty[] { bool, u32, str };
		checkKeyChanged(copProps, new CIMProperty[] { ch, s8, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s16, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s32, bool, str }, "Prop_Integer");
		checkKeyChanged(copProps, new CIMProperty[] { ch, s64, bool, str }, "Prop_Integer");

		instProps = new CIMProperty[] { ch, bool, str };

		// Make sure sint types not changed when key not in prop array
		checkKeyUnchanged(new CIMProperty[] { bool, s8, str }, instProps, "Prop_Integer");
		checkKeyUnchanged(new CIMProperty[] { bool, s16, str }, instProps, "Prop_Integer");
		checkKeyUnchanged(new CIMProperty[] { bool, s32, str }, instProps, "Prop_Integer");
		checkKeyUnchanged(new CIMProperty[] { bool, s64, str }, instProps, "Prop_Integer");

		// Make sure uint types not changed when key not in prop array
		checkKeyUnchanged(new CIMProperty[] { bool, u8, str }, instProps, "Prop_Integer");
		checkKeyUnchanged(new CIMProperty[] { bool, u16, str }, instProps, "Prop_Integer");
		checkKeyUnchanged(new CIMProperty[] { bool, u32, str }, instProps, "Prop_Integer");
		checkKeyUnchanged(new CIMProperty[] { bool, u64, str }, instProps, "Prop_Integer");

		// Make sure real types not changed when key not in prop array
		checkKeyUnchanged(new CIMProperty[] { bool, r32, str }, instProps, "Prop_Real");
		checkKeyUnchanged(new CIMProperty[] { bool, r64, str }, instProps, "Prop_Real");
	}

	private void checkKeyChanged(CIMProperty<?>[] propsCOP, CIMProperty<?>[] propsInst,
			String propName) {
		CIMObjectPath cop = new CIMObjectPath("http", "1.2.3.4", "5988", "cimom", "root/interop",
				propsCOP);
		CIMInstance inst = CIMHelper.CIMInstanceWithSynchonizedNumericKeyDataTypes(cop, propsInst);
		verify(" keys[] not expected size! " + inst.getObjectPath().getKeys().length + " != "
				+ cop.getKeys().length,
				inst.getObjectPath().getKeys().length == cop.getKeys().length);
		verify(propName + " types do not match! "
				+ inst.getObjectPath().getKey(propName).getDataType() + " != "
				+ inst.getProperty(propName).getDataType(), inst.getObjectPath().getKey(propName)
				.getDataType().equals(inst.getProperty(propName).getDataType()));
		verify(propName + " values do not match! "
				+ inst.getObjectPath().getKey(propName).getValue() + " != "
				+ inst.getProperty(propName).getValue(), inst.getObjectPath().getKey(propName)
				.getValue().equals(inst.getProperty(propName).getValue()));
	}

	private void checkKeyUnchanged(CIMProperty<?>[] propsCOP, CIMProperty<?>[] propsInst,
			String propName) {
		CIMObjectPath cop = new CIMObjectPath("http", "1.2.3.4", "5988", "cimom", "root/interop",
				propsCOP);
		CIMInstance inst = new CIMInstance(cop, propsInst);
		verify(" keys[] not expected size! " + inst.getObjectPath().getKeys().length + " != "
				+ cop.getKeys().length,
				inst.getObjectPath().getKeys().length == cop.getKeys().length);
		verify(propName + " types do not match! "
				+ inst.getObjectPath().getKey(propName).getDataType() + " != "
				+ cop.getKey(propName).getDataType(), inst.getObjectPath().getKey(propName)
				.getDataType().equals(cop.getKey(propName).getDataType()));
	}
}
