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
 * 1565892    2006-12-04  ebak         Make SBLIM client JSR48 compliant
 * 1735614    2007-06-12  ebak         Wrong ARRAYSIZE attribute handling in SAX/PULL
 * 1735693    2007-06-12  ebak         Empty VALUE.ARRAY elements are parsed as nulls
 * 1783288    2007-09-10  ebak         CIMClass.isAssociation() not working for retrieved classes.
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 * 3001680    2010-05-18  blaschke-oss CIMQualifierElementInterfaceImpl changes qualifiers
 * 3293248    2011-05-03  blaschke-oss Support for CIM_ERROR instances within ERROR
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 */

package org.sblim.cimclient.unittest.wbem.data;

import javax.cim.CIMClass;
import javax.cim.CIMClassProperty;
import javax.cim.CIMDataType;
import javax.cim.CIMFlavor;
import javax.cim.CIMInstance;
import javax.cim.CIMMethod;
import javax.cim.CIMObjectPath;
import javax.cim.CIMParameter;
import javax.cim.CIMProperty;
import javax.cim.CIMQualifier;
import javax.cim.UnsignedInteger16;

import org.sblim.cimclient.unittest.wbem.Common;

/**
 * EnumClasses holds static sample data.
 */
public class EnumClasses {

	/*
	 * CIMClass( CIMObjectPath path, String superclass, CIMQualifier[]
	 * qualifiers, CIMClassProperty[] props, CIMMethod[] pMethods, boolean
	 * pIsAssociation, boolean pIsKeyed )
	 * 
	 * CIMClass( String name, String superclass, CIMQualifier[] qualifiers,
	 * CIMClassProperty[] props, CIMMethod[] methods )
	 */
	/**
	 * CLASS_A
	 */
	public static final CIMClass[] CLASS_A = {
			new CIMClass("CIM_Collection", "CIM_ManagedElement",
			/*
			 * CIMQualifier( String pName, CIMDataType pType, Object pValue, int
			 * pFlavor )
			 */
			new CIMQualifier[] {
					new CIMQualifier<Boolean>("Abstract", CIMDataType.BOOLEAN_T, Boolean.TRUE,
							CIMFlavor.RESTRICTED),
					new CIMQualifier<String>("Version", CIMDataType.STRING_T, "2.6.0",
							CIMFlavor.RESTRICTED | CIMFlavor.TRANSLATE) },
			/*
			 * CIMClassProperty( String pName, CIMDataType pType, Object pValue,
			 * CIMQualifier[] pQualifiers, boolean pKey, boolean propagated,
			 * String originClass )
			 */
			new CIMClassProperty[] {
					new CIMClassProperty<Object>("Caption", CIMDataType.STRING_T, null,
							new CIMQualifier[] { new CIMQualifier<String>("Description",
									CIMDataType.STRING_T, "Long long text...",
									CIMFlavor.DISABLEOVERRIDE | CIMFlavor.TRANSLATE) }, false,
							true, "CIM_ManagedElement"),
					new CIMClassProperty<Object>("Description", CIMDataType.STRING_T, null, null,
							false, true, "CIM_ManagedElement") },
			/*
			 * CIMMethod( String name, CIMDataType type, CIMQualifier[]
			 * qualifiers, CIMParameter[] parameters, boolean propagated, String
			 * originClass )
			 */
			new CIMMethod[] { new CIMMethod<Object>("ActivatePolicySet", CIMDataType.UINT32_T,
					new CIMQualifier[] { new CIMQualifier<String[]>("ValueMap",
							CIMDataType.STRING_ARRAY_T, new String[] { "0", "1", "2", "3", "4",
									"..", "0x8000.." }, 0) },
					/*
					 * CIMParameter( String name, CIMDataType type,
					 * CIMQualifier[] qualifiers )
					 */
					new CIMParameter[] {

							new CIMParameter<Object>("Element", new CIMDataType(
									"CIM_ManagedElement"),
									new CIMQualifier[] { new CIMQualifier<Boolean>("IN",
											CIMDataType.BOOLEAN_T, Boolean.TRUE,
											CIMFlavor.DISABLEOVERRIDE) }),
							new CIMParameter<Object>("RefA", new CIMDataType("CIM_ManagedElement",
									0),
									new CIMQualifier[] { new CIMQualifier<Boolean>("IN",
											CIMDataType.BOOLEAN_T, Boolean.TRUE,
											CIMFlavor.DISABLEOVERRIDE) }),
							new CIMParameter<Object>("Count", CIMDataType.SINT32_T,
									new CIMQualifier[] { new CIMQualifier<Boolean>("OUT",
											CIMDataType.BOOLEAN_T, Boolean.TRUE,
											CIMFlavor.DISABLEOVERRIDE) }) }, true,
					"CIM_ManagedElement") }),
			new CIMClass("CIM_Profile", "CIM_Collection", null, new CIMClassProperty[] {
					new CIMClassProperty<Object>("InstanceID", CIMDataType.STRING_T, null,
							new CIMQualifier[] { new CIMQualifier<String>("Description",
									CIMDataType.STRING_T, "This is a local property :)",
									CIMFlavor.TRANSLATE) }, true, true, "CIM_Profile"),
					new CIMClassProperty<Object>("Hello", CIMDataType.STRING_T, null,
							new CIMQualifier[] { new CIMQualifier<String>("Description",
									CIMDataType.STRING_T,
									"This is a local property with PROPAGATED=false :)",
									CIMFlavor.TRANSLATE) }, false, false, "CIM_Profile"),
					new CIMClassProperty<String[]>("EmptyPropA", CIMDataType.STRING_ARRAY_T,
							new String[0], null, false, false, null),
					new CIMClassProperty<Object>("NullPropA", CIMDataType.STRING_ARRAY_T, null,
							null, false, false, null) }, null),
			new CIMClass(
					Common.LOCALPATH.build("SampleAssocClass", null),
					null,
					new CIMQualifier[] { new CIMQualifier<Boolean>("Association",
							CIMDataType.BOOLEAN_T, Boolean.TRUE, 0) },
					new CIMClassProperty[] {
							new CIMClassProperty<Object>("OneSide", new CIMDataType("SampleClass"),
									null, null, false, false, null),
							new CIMClassProperty<Object>("OtherSide",
									new CIMDataType("SampleClass"), null, null, false, false, null), },
					null, true, false) };

	/**
	 * ERROR_A
	 */
	public static final CIMInstance[] ERROR_A = {
			new CIMInstance(new CIMObjectPath(null, null, null, null, "CIM_Error", null),
					new CIMProperty[] {
							new CIMProperty<UnsignedInteger16>("ErrorType", CIMDataType.UINT16_T,
									new UnsignedInteger16(1)),
							new CIMProperty<String>("OtherErrorType", CIMDataType.STRING_T,
									"Password Expired"),
							new CIMProperty<UnsignedInteger16>("ProbableCause",
									CIMDataType.UINT16_T, new UnsignedInteger16(117)) }),
			new CIMInstance(new CIMObjectPath(null, null, null, null, "CIM_Error", null),
					new CIMProperty[] {
							new CIMProperty<Integer>("ErrorType", CIMDataType.SINT32_T,
									new Integer(1)),
							new CIMProperty<String>("OtherErrorType", CIMDataType.STRING_T,
									"Password Expired"),
							new CIMProperty<Long>("ProbableCause", CIMDataType.SINT64_T, new Long(
									118)) }) };
}
