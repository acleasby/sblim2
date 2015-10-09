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
 * 1663270    2007-02-19  ebak         Minor performance problems
 * 1660756    2007-03-02  ebak         Embedded object support
 * 1686000    2007-04-20  ebak         modifyInstance() missing from WBEMClient
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1714878    2007-05-08  ebak         Empty string property values are parsed as nulls
 * 1719991    2007-05-16  ebak         FVT: regression ClassCastException in EmbObjHandler
 * 1737141    2007-06-18  ebak         Sync up with JSR48 evolution
 * 1820763    2007-10-29  ebak         Supporting the EmbeddedInstance qualifier
 * 1827728    2007-11-20  ebak         rework: embeddedInstances: attribute EmbeddedObject not set
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 3411879    2011-09-20  blaschke-oss TCK: CIM element value must match type
 * 3500619    2012-03-16  blaschke-oss JSR48 1.0.0: CIMClass association/key clean up
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.cim.CIMArgument;
import javax.cim.CIMClass;
import javax.cim.CIMClassProperty;
import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMMethod;
import javax.cim.CIMObjectPath;
import javax.cim.CIMParameter;
import javax.cim.CIMProperty;
import javax.cim.CIMQualifier;
import javax.cim.CIMQualifierType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sblim.cimclient.internal.cim.CIMElementSorter;
import org.sblim.cimclient.internal.cimxml.CIMClientXML_HelperImpl;
import org.sblim.cimclient.internal.cimxml.CIMXMLBuilderImpl;
import org.sblim.cimclient.internal.cimxml.CimXmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CIMBuilder
 */
public class CIMBuilder {

	private static byte[] cEnumClassesRsp;

	private static byte[] cEnumInstancesRsp;

	private static byte[] cClassObjPathesRsp;

	private static byte[] cInstObjPathesRsp;

	private static byte[] cAssocClassesRsp;

	private static byte[] cAssocInstancesRsp;

	private static byte[] cEnumQualiTypesRsp;

	private static byte[] cMethodRsp;

	/*
	 * CIMObjectPath( String host, String namespace, String objectName,
	 * CIMProperty[] keys )
	 */

	/**
	 * createDoc
	 * 
	 * @return Document
	 * @throws Exception
	 */
	public static Document createDoc() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		return docBuilder.newDocument();
	}

	/**
	 * getXML
	 * 
	 * @param pDoc
	 * @param pMsgChildE
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getXML(Document pDoc, Element pMsgChildE) throws Exception {
		Element cimE = CIMXMLBuilderImpl.createCIM(pDoc);
		Element msgE = CIMXMLBuilderImpl.createMESSAGE(pDoc, cimE, "1.0", "2.0");
		msgE.appendChild(pMsgChildE);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		CimXmlSerializer.serialize(os, cimE, true);
		return os.toByteArray();
	}

	/**
	 * getEnumerateClassesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getEnumerateClassesRsp() throws Exception {
		if (cEnumClassesRsp == null) {
			CIMClass[] clA = buildClasses();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.enumerateClasses_response(doc, clA);
			cEnumClassesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cEnumClassesRsp);
	}

	/**
	 * getEnumerateInstancesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getEnumerateInstancesRsp() throws Exception {
		if (cEnumInstancesRsp == null) {
			CIMInstance[] instA = buildInstances();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.enumerateInstances_response(doc, instA);
			cEnumInstancesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cEnumInstancesRsp);
	}

	/**
	 * getClassObjPathesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getClassObjPathesRsp() throws Exception {
		if (cClassObjPathesRsp == null) {
			CIMObjectPath[] opA = buildClassObjPathes();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.associatorNames_response(doc, opA);
			cClassObjPathesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cClassObjPathesRsp);
	}

	/**
	 * getInstObjPathesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getInstObjPathesRsp() throws Exception {
		if (cInstObjPathesRsp == null) {
			CIMObjectPath[] opA = buildInstObjPathes();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.associatorNames_response(doc, opA);
			cInstObjPathesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cInstObjPathesRsp);
	}

	/**
	 * getAssocClassesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getAssocClassesRsp() throws Exception {
		if (cAssocClassesRsp == null) {
			CIMClass[] clA = buildClasses();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.associators_response(doc, clA);
			cAssocClassesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cAssocClassesRsp);
	}

	/**
	 * getAssocInstancesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getAssocInstancesRsp() throws Exception {
		if (cAssocInstancesRsp == null) {
			CIMInstance[] instA = buildInstances();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.associators_response(doc, instA);
			cAssocInstancesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cAssocInstancesRsp);
	}

	/**
	 * getEnumQauliTypesRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getEnumQauliTypesRsp() throws Exception {
		if (cEnumQualiTypesRsp == null) {
			CIMQualifierType<?>[] qtA = buildQualiTypes();
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.enumQualifierTypes_response(doc, qtA);
			cEnumQualiTypesRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cEnumQualiTypesRsp);
	}

	/**
	 * getMethodRsp
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getMethodRsp() throws Exception {
		if (cMethodRsp == null) {
			Document doc = createDoc();
			Element rspE = CIMClientXML_HelperImpl.invokeMethod_response(doc, "SampleMethod",
					Common.LOCALPATH.getBasePath(), new Integer(42), buildArguments());
			cMethodRsp = getXML(doc, rspE);
		}
		return new ByteArrayInputStream(cMethodRsp);
	}

	private static boolean bit(int pValue, int pBit) {
		return (pValue & (1 << pBit)) > 0;
	}

	private static CIMQualifierType<?>[] cQualiTypeA;

	/*
	 * CIMQualifierType( CIMObjectPath pPath, CIMDataType pType, Object pValue,
	 * int pScope, int pFlavor )
	 */

	/**
	 * buildQualiTypes
	 * 
	 * @return CIMQualifierType[]
	 */
	public static CIMQualifierType<?>[] buildQualiTypes() {
		if (cQualiTypeA != null) return cQualiTypeA;
		// 2^7=128
		cQualiTypeA = new CIMQualifierType[128];
		// no Refs
		TVPair[] pairs = TVPair.getAll(TVPair.ALL & ~(TVPair.REF | TVPair.NULL | TVPair.EMBOBJ));
		for (int i = 0; i < 128; i++) {
			TVPair pair = pairs[i % pairs.length];
			cQualiTypeA[i] = new CIMQualifierType<Object>(new CIMObjectPath(null, null, null, null,
					"QualiType" + i, null), pair.getType(), pair.getValue(), i, i % 8);
		}
		return cQualiTypeA;
	}

	private static CIMObjectPath[] cClassPathA;

	/**
	 * buildClassObjPathes
	 * 
	 * @return CIMObjectPath[]
	 */
	public static CIMObjectPath[] buildClassObjPathes() {
		if (cClassPathA != null) return cClassPathA;
		cClassPathA = new CIMObjectPath[] { Common.LOCALPATH.build("CIMClass0", null),
				Common.LOCALPATH.build("CIMClass1", "root/cimv2") };
		return cClassPathA;
	}

	private static CIMObjectPath[] cInstPathA;

	/**
	 * buildInstObjPathes
	 * 
	 * @return CIMObjectPath[]
	 */
	public static CIMObjectPath[] buildInstObjPathes() {
		if (cInstPathA != null) return cInstPathA;
		cInstPathA = new CIMObjectPath[] {
				Common.LOCALPATH.build("CIMInstance0", null, getKeyProps(false)),
				Common.LOCALPATH.build("CIMInstance1", "ibm/v2", getKeyProps(false)), };
		return cInstPathA;
	}

	/*
	 * CIMClass( String name, String superclass, CIMQualifier[] qualifiers,
	 * CIMClassProperty[] props, CIMMethod[] methods );
	 * 
	 * CIMClass( CIMObjectPath path, String superclass, CIMQualifier[]
	 * qualifiers, CIMClassProperty[] props, CIMMethod[] pMethods, boolean
	 * pIsAssociation, boolean pIsKeyed )
	 */

	/**
	 * buildClass
	 * 
	 * @param pName
	 * @param pHasSuper
	 * @param pHasQualis
	 * @param pHasProps
	 * @param pHasMethods
	 * @return CIMClass
	 */
	public static CIMClass buildClass(String pName, boolean pHasSuper, boolean pHasQualis,
			boolean pHasProps, boolean pHasMethods) {
		CIMQualifier<?>[] quals = pHasQualis ? buildQualis() : null;
		boolean isAssoc = pHasQualis ? hasAssoc(quals) : false;
		CIMClassProperty<?>[] props = pHasProps ? buildClassProperties() : null;
		boolean isKey = pHasProps ? hasKey(props) : false;
		return new CIMClass(Common.LOCALPATH.build(pName, null),
				pHasSuper ? pName + "Super" : null, quals, props, pHasMethods ? buildMethods()
						: null, isAssoc, isKey);
	}

	private static CIMClass[] cClassA;

	/**
	 * buildClasses
	 * 
	 * @return CIMClass[]
	 */
	public static CIMClass[] buildClasses() {
		if (cClassA != null) return cClassA;
		cClassA = new CIMClass[] { buildClass("CIMClass0", true, false, false, false),
				buildClass("CIMClass1", false, true, false, false),
				buildClass("CIMClass2", false, false, true, false),
				buildClass("CIMClass3", false, false, false, true),
				buildClass("CIMClass4", true, true, true, true) };
		return cClassA;
	}

	private static CIMQualifier<?>[] cQualiA;

	/*
	 * CIMQualifier(String pName, CIMDataType pType, Object pValue, int pFlavor)
	 */
	/**
	 * buildQualis
	 * 
	 * @return CIMQualifier[]
	 */
	public static CIMQualifier<?>[] buildQualis() {
		if (cQualiA != null) return cQualiA;
		// no ref, no null
		TVPair[] pairs = TVPair.getAll(TVPair.VALUE | TVPair.SCALAR | TVPair.ARRAY);
		int tvLen = pairs.length;
		int flLen = 8;
		int size = Math.max(tvLen, flLen);
		cQualiA = new CIMQualifier[size];
		for (int i = 0; i < cQualiA.length; i++) {
			TVPair pair = pairs[i % tvLen];
			cQualiA[i] = new CIMQualifier<Object>("Qualifier" + i, pair.getType(), pair.getValue(),
					i % flLen, (i & 1) > 0);
		}
		return cQualiA;
	}

	private static CIMClassProperty<?>[] cClassPropA;

	/*
	 * CIMClassProperty( String pName, CIMDataType pType, Object pValue,
	 * CIMQualifier[] pQualifiers, boolean pKey, boolean propagated, String
	 * originClass )
	 */
	/**
	 * buildClassProperties
	 * 
	 * @return CIMClassProperty[]
	 */
	public static CIMClassProperty<?>[] buildClassProperties() {
		if (cClassPropA != null) return cClassPropA;
		// ref, with nulls
		TVPair[] pairs = TVPair.getAll(TVPair.ALL);
		cClassPropA = new CIMClassProperty[pairs.length];
		for (int i = 0; i < cClassPropA.length; i++) {
			TVPair pair = pairs[i];
			cClassPropA[i] = new CIMClassProperty<Object>("ClassProp" + i, pair.getType(), pair
					.getValue(), i == 0 ? buildQualis() : null, bit(i, 0), bit(i, 1),
					bit(i, 2) ? "Origin" + i : null);
		}
		return cClassPropA;
	}

	private static CIMMethod<?>[] cMethodA;

	/*
	 * CIMMethod( String name, CIMDataType type, CIMQualifier[] qualifiers,
	 * CIMParameter[] parameters, boolean propagated, String originClass )
	 */
	/**
	 * buildMethods
	 * 
	 * @return CIMMethod[]
	 */
	public static CIMMethod<?>[] buildMethods() {
		if (cMethodA != null) return cMethodA;
		// no ref, no array, no value
		TVPair[] pairs = TVPair.getAll(TVPair.SCALAR);
		cMethodA = new CIMMethod[pairs.length];
		for (int i = 0; i < cMethodA.length; i++) {
			CIMDataType type = pairs[i].getType();
			cMethodA[i] = new CIMMethod<Object>("Method" + i, type, i == 0 ? buildQualis() : null,
					i == 1 ? buildParams() : null, bit(i, 0), bit(i, 1) ? "Origin" + i : null);
		}
		return cMethodA;
	}

	private static CIMParameter<?>[] cParamA;

	/*
	 * CIMParameter( String name, CIMDataType type, CIMQualifier[] qualifiers )
	 */
	/**
	 * buildParams
	 * 
	 * @return CIMParameter[]
	 */
	public static CIMParameter<?>[] buildParams() {
		if (cParamA != null) return cParamA;
		// any type
		TVPair[] pairs = TVPair.getAll();
		cParamA = new CIMParameter[pairs.length];
		for (int i = 0; i < cParamA.length; i++) {
			cParamA[i] = new CIMParameter<Object>("Parameter" + i, pairs[i].getType(),
					i == 0 ? buildQualis() : null);
		}
		return cParamA;
	}

	private static CIMInstance[] cInstA;

	/*
	 * CIMInstance(CIMObjectPath name, CIMProperty[] props)
	 * 
	 * enumerateInstances() response contains VALUE.NAMEDINSTANCEs and it
	 * doesn't contain ObjectPath CIM-XML element, so CIMInstance()'s
	 * CIMObjectPath, have to be minimal.
	 */

	/**
	 * buildInstances
	 * 
	 * @return CIMInstance[]
	 */
	public static CIMInstance[] buildInstances() {
		if (cInstA != null) return cInstA;
		cInstA = new CIMInstance[] {
				new CIMInstance(Common.LOCALPATH.build("CIMAnythingClass0", null, null),
						getKeyProps(false)),
				new CIMInstance(Common.LOCALPATH.build("CIMAnythingClass1", null, null),
						getKeyProps(true)),
				new CIMInstance(Common.LOCALPATH.build("CIMAnythingClass0", null, null),
						getAllProps(false)),
				new CIMInstance(Common.LOCALPATH.build("CIMAnythingClass1", null, null),
						getAllProps(true)) };
		return cInstA;
	}

	/*
	 * According to DSP0201, the following CIM data types cannot have null
	 * values (i.e. uint32 MUST contain at least one digit)
	 */
	private static int cInvalidNullTypes[] = { CIMDataType.BOOLEAN, CIMDataType.CHAR16,
			CIMDataType.DATETIME, CIMDataType.REAL32, CIMDataType.REAL64, CIMDataType.SINT16,
			CIMDataType.SINT32, CIMDataType.SINT64, CIMDataType.SINT8, CIMDataType.UINT16,
			CIMDataType.UINT32, CIMDataType.UINT64, CIMDataType.UINT8 };

	private static boolean isInvalidNullType(int pType) {
		for (int i = cInvalidNullTypes.length - 1; i >= 0; i--)
			if (cInvalidNullTypes[i] == pType) return true;
		return false;
	}

	private static CIMProperty<?>[] cKeyPropA;

	private static CIMProperty<?>[] cAllPropA;

	/*
	 * Contains the same properties like cKeyPropA and cAllPropA, but some
	 * property values are null. It is introduced to check: [ sblim-Bugs-1666336
	 * ] EnumInstance has attributes filled from others
	 */
	private static CIMProperty<?>[] cKeyPropNullA;

	private static CIMProperty<?>[] cAllPropNullA;

	/*
	 * key types: ELEMENT KEYBINDING (KEYVALUE | VALUE.REFERENCE) ELEMENT
	 * KEYVALUE (#PCDATA) ATTLIST KEYVALUE VALUETYPE (string | boolean |
	 * numeric) "string" %CIMType; #IMPLIED -> no Ref Summary: any scalar types!
	 * 
	 * CIMProperty( String name, CIMDataType type, Object value, boolean key,
	 * boolean propagated, String originClass )
	 */

	private static void buildProps() {
		if (cAllPropA != null) return;
		TVPair[] keyPairs = TVPair.getAll(TVPair.REF | TVPair.SCALAR | TVPair.VALUE);
		TVPair[] nonKeyPairs = TVPair.getAll(TVPair.REF | TVPair.ARRAY | TVPair.VALUE);
		cKeyPropA = new CIMProperty[keyPairs.length];
		cKeyPropNullA = new CIMProperty[keyPairs.length];
		cAllPropA = new CIMProperty[keyPairs.length + nonKeyPairs.length];
		cAllPropNullA = new CIMProperty[cAllPropA.length];
		// make key properties
		for (int i = 0; i < cKeyPropA.length; i++) {
			TVPair pair = keyPairs[i];
			CIMProperty<Object> prop = new CIMProperty<Object>("KeyProp" + i, pair.getType(), pair
					.getValue(), true, bit(i, 0), bit(i, 1) ? "KeyPropOrigin" + i : null);
			cKeyPropA[i] = cAllPropA[i] = prop;
			if (i >= cKeyPropA.length / 2 && pair.getType().getType() != CIMDataType.REFERENCE
					&& !isInvalidNullType(pair.getType().getType())) {
				CIMProperty<Object> propNull = new CIMProperty<Object>("KeyProp" + i, pair
						.getType(), null, true, bit(i, 0), bit(i, 1) ? "KeyPropOrigin" + i : null);
				cKeyPropNullA[i] = cAllPropNullA[i] = propNull;
			} else {
				cKeyPropNullA[i] = cAllPropNullA[i] = prop;
			}
		}
		for (int sIdx = 0, dIdx = cKeyPropA.length; sIdx < nonKeyPairs.length; sIdx++, dIdx++) {
			TVPair pair = nonKeyPairs[sIdx];
			CIMProperty<Object> prop = new CIMProperty<Object>("Prop" + sIdx, pair.getType(), pair
					.getValue(), false, bit(sIdx, 0), bit(sIdx, 1) ? "PropOrigin" + sIdx : null);
			cAllPropA[dIdx] = cAllPropNullA[dIdx] = prop;
		}
	}

	/**
	 * getKeyProps<br>
	 * Note: key properties with null values are not allowed
	 * 
	 * @param pWithNulls
	 * @return CIMProperty[]
	 */
	public static CIMProperty<?>[] getKeyProps(boolean pWithNulls) {
		buildProps();
		return pWithNulls ? cKeyPropNullA : cKeyPropA;
	}

	/**
	 * getAllProps
	 * 
	 * @param pWithNulls
	 * @return CIMProperty[]
	 */
	public static CIMProperty<?>[] getAllProps(boolean pWithNulls) {
		buildProps();
		return pWithNulls ? cAllPropNullA : cAllPropA;
	}

	private static CIMArgument<?>[] cArgA;

	/**
	 * buildArguments
	 * 
	 * @return CIMArgument[]
	 */
	public static CIMArgument<?>[] buildArguments() {
		if (cArgA != null) return cArgA;
		TVPair[] pairA = TVPair.getAll(TVPair.ALL & ~TVPair.NULL);
		cArgA = new CIMArgument[pairA.length];
		for (int i = 0; i < pairA.length; i++)
			cArgA[i] = new CIMArgument<Object>("Arg" + i, pairA[i].getType(), pairA[i].getValue());
		return (CIMArgument[]) CIMElementSorter.sort(cArgA);
	}

	private static boolean hasAssoc(CIMQualifier<?>[] pQuals) {
		if (pQuals == null) return false;
		for (int i = 0; i < pQuals.length; i++)
			if ("ASSOCIATION".equalsIgnoreCase(pQuals[i].getName())
					&& Boolean.valueOf(true).equals(pQuals[i].getValue())) return true;
		return false;
	}

	private static boolean hasKey(CIMClassProperty<?>[] pProps) {
		if (pProps == null) return false;
		for (int i = 0; i < pProps.length; i++)
			if (pProps[i].isKey()) return true;
		return false;
	}

}
