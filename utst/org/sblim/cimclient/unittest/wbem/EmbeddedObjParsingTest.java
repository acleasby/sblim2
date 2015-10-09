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
 * @author : Endre Bak, ebak@de.ibm.com  
 * 
 * Flag       Date        Prog         Description
 * -------------------------------------------------------------------------------
 * 1660756    2007-02-22  ebak         Embedded object support
 * 1689085    2007-04-10  ebak         Embedded object enhancements for Pegasus
 * 1688273    2007-04-16  ebak         Full support of HTTP trailers
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1714878    2007-05-08  ebak         Empty string property values are parsed as nulls
 * 1820763    2007-10-29  ebak         Supporting the EmbeddedInstance qualifier
 * 1848607    2007-12-11  ebak         Strict EmbeddedObject types
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2620505    2009-02-26  rgummada     EmbeddedObject qualifier is missing from CIMClass
 * 2714989    2009-03-26  blaschke-oss Code cleanup from redundant null check et al
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 * 2927029    2010-01-06  blaschke-oss Unit test fails on Java 6
 * 3027615    2010-07-12  blaschke-oss Use CLASS_ARRAY_T instead of new CIMDataType(CLASS,0)
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.cim.CIMArgument;
import javax.cim.CIMClass;
import javax.cim.CIMClassProperty;
import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMMethod;
import javax.cim.CIMParameter;
import javax.cim.CIMProperty;
import javax.cim.CIMQualifier;
import javax.wbem.CloseableIterator;

import org.sblim.cimclient.internal.cim.CIMElementSorter;
import org.sblim.cimclient.internal.cimxml.CIMResponse;
import org.sblim.cimclient.internal.cimxml.sax.SAXHelper;
import org.sblim.cimclient.internal.util.WBEMConfiguration;
import org.sblim.cimclient.unittest.TestCase;

/**
 * EmbeddedObjParsingTest
 */
public class EmbeddedObjParsingTest extends TestCase {

	// for PARAMVALUE and RETURNVALUE testing METHODRESPONSE have to be parsed

	private static final boolean STRICT_EMBOBJ_TYPES = WBEMConfiguration.getGlobalConfiguration()
			.strictEmbObjTypes();

	private static CIMDataType emptyEmbClassType() {
		return STRICT_EMBOBJ_TYPES ? CIMDataType.CLASS_T : CIMDataType.STRING_T;
	}

	private static CIMQualifier<?>[] addEmbObjQuali(CIMQualifier<?>[] pQualis) {
		if (STRICT_EMBOBJ_TYPES) return pQualis;
		if (pQualis == null) { return new CIMQualifier[] { EMB_OBJ_QUALI }; }
		CIMQualifier<?>[] qualis = new CIMQualifier[pQualis.length + 1];
		System.arraycopy(pQualis, 0, qualis, 0, pQualis.length);
		qualis[pQualis.length] = EMB_OBJ_QUALI;
		return qualis;
	}

	private static final CIMInstance EMB_INST = new CIMInstance(Common.LOCALPATH.build(
			"TestCMPI_Instance", null), new CIMProperty[] { new CIMProperty<Object>("ElementName",
			CIMDataType.STRING_T, null, false, false, null) });

	private static final CIMClass EMB_CLASS = new CIMClass("MyTestClass", null, null,
			new CIMClassProperty[] { new CIMClassProperty<Object>("KeyProp", CIMDataType.STRING_T,
					null, null, false, false, null) }, null);

	private static final CIMInstance REF_INST = new CIMInstance(Common.LOCALPATH.build("TestClass",
			null, new CIMProperty[] { new CIMProperty<String>("ID", CIMDataType.STRING_T, "00",
					true, false, null) }), new CIMProperty[] {
			new CIMProperty<Object>("ClassValueArrayWithObjAttr", CIMDataType.CLASS_ARRAY_T,
					new CIMClass[] { EMB_CLASS, EMB_CLASS, null }, false, false, null),
			new CIMProperty<Object>("ClassValueWithObjAttr", CIMDataType.CLASS_T, EMB_CLASS, false,
					false, null),
			new CIMProperty<Object>("InstValueArrayWithInstAttr", CIMDataType.OBJECT_ARRAY_T,
					new CIMInstance[] { EMB_INST, EMB_INST, null }, false, false, null),
			new CIMProperty<Object>("InstValueWithInstAttr", CIMDataType.OBJECT_T, EMB_INST, false,
					false, null),
			new CIMProperty<String>("NormalStringProperty", CIMDataType.STRING_T, "Good morning!",
					false, false, null),
			new CIMProperty<Object>("UnValuedWithInstAttr", CIMDataType.OBJECT_T, null, false,
					false, null),
			new CIMProperty<Object>("UnValuedWithObjAttr", emptyEmbClassType(), null, false, false,
					null) });

	private static final CIMQualifier<Boolean> EMB_OBJ_QUALI = new CIMQualifier<Boolean>(
			"EmbeddedObject", CIMDataType.BOOLEAN_T, Boolean.TRUE, 0);

	private static final CIMQualifier<String> mkEmbInstQuali(String pClassName) {
		return new CIMQualifier<String>("EmbeddedInstance", CIMDataType.STRING_T, pClassName, 0);
	}

	/**
	 * REF_CLASS
	 */
	public static final CIMClass REF_CLASS = new CIMClass(
			Common.LOCALPATH.build("TestClass", null), (String) null, (CIMQualifier[]) null,
			new CIMClassProperty[] {
					new CIMClassProperty<CIMInstance>("EmbInstWithQualiAndValue",
							CIMDataType.OBJECT_T, EMB_INST,
							new CIMQualifier[] { mkEmbInstQuali("TestCMPI_Instance") }, false,
							false, null),
					new CIMClassProperty<Object>("EmbInstWithQuali", CIMDataType.OBJECT_T, null,
							new CIMQualifier[] { mkEmbInstQuali("TestCMPI_Instance") }, false,
							false, null),

					new CIMClassProperty<CIMClass>("EmbObjWithQualiAndValue", CIMDataType.CLASS_T,
							EMB_CLASS, addEmbObjQuali(new CIMQualifier[] { EMB_OBJ_QUALI }), false,
							false, null),
					new CIMClassProperty<Object>("EmbObjWithQuali", emptyEmbClassType(), null,
							addEmbObjQuali(new CIMQualifier[] { EMB_OBJ_QUALI }), false, false,
							null),
					new CIMClassProperty<CIMInstance[]>("EmbInstAWithQualiAndValue",
							CIMDataType.OBJECT_ARRAY_T, new CIMInstance[] { EMB_INST, EMB_INST },
							new CIMQualifier[] { mkEmbInstQuali("TestCMPI_Instance") }, false,
							false, null),
					new CIMClassProperty<CIMClass[]>("EmbObjAWithQualiAndValue",
							CIMDataType.CLASS_ARRAY_T, new CIMClass[] { EMB_CLASS, EMB_CLASS },
							addEmbObjQuali(new CIMQualifier[] { EMB_OBJ_QUALI }), false, false,
							null) }, new CIMMethod[] { new CIMMethod<Object>("MethodWithQuali",
					emptyEmbClassType(), addEmbObjQuali(new CIMQualifier[] { EMB_OBJ_QUALI }),
					new CIMParameter[] {
							new CIMParameter<Object>("ParamWithQuali", emptyEmbClassType(),
									addEmbObjQuali(new CIMQualifier[] { EMB_OBJ_QUALI })),
							new CIMParameter<Object>("ParamWithEmbInstQuali", CIMDataType.OBJECT_T,
									new CIMQualifier[] { mkEmbInstQuali("CIMLofasz") }),
							new CIMParameter<Object>("ParamAWithEmbInstQuali",
									CIMDataType.OBJECT_ARRAY_T,
									new CIMQualifier[] { mkEmbInstQuali("CIMLofasz") }) }, false,
					null) }, false, false);

	private static final CIMArgument<?>[] REF_OUT_ARGS = {
			new CIMArgument<CIMInstance>("EmbInstAttrParamWithValue", CIMDataType.OBJECT_T,
					EMB_INST),
			new CIMArgument<Object>("EmbObjAttrParam", CIMDataType.CLASS_T, null),
			new CIMArgument<CIMInstance[]>("EmbInstAttrParamArrayWithValue",
					CIMDataType.OBJECT_ARRAY_T, new CIMInstance[] { null, EMB_INST, EMB_INST }) };

	/**
	 * @param pMsg
	 */
	private static void debug(String pMsg) {
	// System.out.println(pMsg);
	}

	private InputStream getInputStream(String pName) {
		InputStream is = getClass().getResourceAsStream(pName);
		verify("Failed to load resource! : " + pName, is != null);
		return is;
	}

	/**
	 * EmbObj qualified PROPERTY and PROPERTY.ARRAY are checked with this.
	 * 
	 * @return InputStream
	 */
	private InputStream getInstIS() {
		return getInputStream("data/EmbObjGetInstance.xml");
	}

	private InputStream getNegInstIS() {
		return getInputStream("data/EmbObjGetInstNegative.xml");
	}

	private InputStream getNegInst2IS() {
		return getInputStream("data/EmbObjGetInstNegative2.xml");
	}

	/**
	 * EmbObj qualified PROPERTY, PARAMETER and METHOD are checked with this
	 * 
	 * @return InputStream
	 */
	private InputStream getClassIS() {
		return getInputStream("data/EmbObjGetClass.xml");
	}

	/*
	 * private void dumpIS(InputStream pIS) throws Exception { byte[] buf = new
	 * byte[256]; int size; while ((size=pIS.read(buf))>0)
	 * System.out.println(new String(buf,0,size)); }
	 */

	private void checkGetInstResult(CloseableIterator<Object> pItr) {
		verify("CIMInstance is not retrieved!", pItr.hasNext());
		CIMInstance inst = (CIMInstance) pItr.next();
		verify("Reference instance doesn't equal the parsed one!\nREF_INST:\n" + REF_INST
				+ "\n\ninst:\n" + inst, REF_INST.equals(inst));
	}

	/**
	 * testGetInstanceDOM
	 * 
	 * @throws Exception
	 */
	public void testGetInstanceDOM() throws Exception {
		// dumpIS(getInstIS());
		checkGetInstResult(Common.parseWithDOM(getInstIS()));
	}

	/**
	 * testGetInstanceSAX
	 * 
	 * @throws Exception
	 */
	public void testGetInstanceSAX() throws Exception {
		// dumpIS(getInstIS());
		checkGetInstResult(Common.parseWithSAX(getInstIS()));
	}

	/**
	 * testGetInstancePULL
	 * 
	 * @throws Exception
	 */
	public void testGetInstancePULL() throws Exception {
		checkGetInstResult(Common.parseWithPULL(getInstIS()));
	}

	private void checkNegativeDOM(InputStream pIS) {
		try {
			Common.parseWithDOM(pIS);
			verify("Wrong data is parsed without exception thrown!", false);
		} catch (Exception e) {
			Throwable thr = e.getCause();
			debug(thr.getMessage());
		}
	}

	/**
	 * testNegativeGetInstanceDOM
	 */
	public void testNegativeGetInstanceDOM() {
		checkNegativeDOM(getNegInstIS());
		checkNegativeDOM(getNegInst2IS());
	}

	private void checkNegativeSAX(InputStream pIS) {
		try {
			Common.parseWithSAX(pIS);
			verify("Wrong data is parsed without exception thrown!", false);
		} catch (Exception e) {
			debug(e.getMessage());
		}
	}

	/**
	 * testNegativeGetInstanceSAX
	 */
	public void testNegativeGetInstanceSAX() {
		checkNegativeSAX(getNegInstIS());
		checkNegativeSAX(getNegInst2IS());
	}

	private void checkItrNegative(CloseableIterator<Object> pItr) {
		try {
			while (pItr.hasNext())
				pItr.next();
			verify("Wrong data is parsed without exception thrown!", false);
		} catch (Exception e) {
			Throwable thr = e.getCause();
			debug(thr.getMessage());
		}
	}

	/**
	 * testNegativeGetInstancePULL
	 * 
	 * @throws Exception
	 */
	public void testNegativeGetInstancePULL() throws Exception {
		checkItrNegative(Common.parseWithPULL(getNegInstIS()));
		checkItrNegative(Common.parseWithPULL(getNegInst2IS()));
	}

	private void checkGetClassResult(CloseableIterator<Object> pItr) {
		verify("CIMClass is not retrieved!", pItr.hasNext());
		CIMClass cl = (CIMClass) pItr.next();
		verify("Reference class doesn't equal the parsed one!\nREF_CLASS:\n" + REF_CLASS
				+ "\n\ncl:\n" + cl, REF_CLASS.equals(cl));
	}

	/**
	 * testGetClassDOM
	 * 
	 * @throws Exception
	 */
	public void testGetClassDOM() throws Exception {
		checkGetClassResult(Common.parseWithDOM(getClassIS()));
	}

	/**
	 * testGetClassSAX
	 * 
	 * @throws Exception
	 */
	public void testGetClassSAX() throws Exception {
		checkGetClassResult(Common.parseWithSAX(getClassIS()));
	}

	/**
	 * testGetClassPULL
	 * 
	 * @throws Exception
	 */
	public void testGetClassPULL() throws Exception {
		checkGetClassResult(Common.parseWithPULL(getClassIS()));
		// System.out.println("REF_CLASS:\n"+REF_CLASS);
	}

	/*
	 * private static void dumpArgA(String pCaption, CIMArgument[] pArgA) {
	 * System.out.println("\n --- "+pCaption+" ---:\n"); for(int i=0;
	 * i<pArgA.length; i++) System.out.println(pArgA[i]+"\n"); }
	 */

	private void checkMethodRsp(Object pRetVal, CIMArgument<?>[] pOutArgA) {
		CIMElementSorter.sort(pOutArgA);
		verify("EMBEDDED_INST!=retVal\nEMBEDDED_INST:\n" + EMB_INST + "\nretVal:\n" + pRetVal,
				EMB_INST.equals(pRetVal));
		// dumpArgA("sorted outArgA", outArgA);
		for (int i = 0; i < REF_OUT_ARGS.length; i++) {
			CIMArgument<?> refArg = REF_OUT_ARGS[i];
			CIMArgument<?> outArg = refArg == null ? null : (CIMArgument<?>) CIMElementSorter.find(
					pOutArgA, refArg.getName());
			verify("\nrefArg=" + refArg + "\n\noutArg=" + outArg, refArg == null ? outArg == null
					: refArg.equals(outArg));
		}
	}

	/**
	 * testMethodRsp
	 * 
	 * @throws Exception
	 */
	public void testMethodRsp() throws Exception {
		CIMArgument<?>[] outArgA = new CIMArgument[3];
		Object retVal = SAXHelper.parseInvokeMethodResponse(new InputStreamReader(
				getInputStream("data/EmbObjMethodRsp.xml")), outArgA, Common.LOCALPATH
				.getBasePath());
		checkMethodRsp(retVal, outArgA);
	}

	/**
	 * testMethodRspDOM
	 * 
	 * @throws Exception
	 */
	public void testMethodRspDOM() throws Exception {
		// DOM parser
		CIMResponse response = Common.getSingleResponse(new InputStreamReader(
				getInputStream("data/EmbObjMethodRsp.xml")), Common.LOCALPATH.getBasePath());
		response.checkError();
		List<Object> resultSet = response.getFirstReturnValue();

		Object retVal = resultSet.size() > 0 ? resultSet.get(0) : null;

		List<Object> outParamValues = response.getParamValues();
		CIMArgument<?>[] outArgA = new CIMArgument[3];
		if (outParamValues != null) {
			Iterator<Object> itr = outParamValues.iterator();
			for (int i = 0; i < outArgA.length; i++)
				if (itr.hasNext()) {
					outArgA[i] = (CIMArgument<?>) itr.next();
				} else {
					break;
				}
		}
		checkMethodRsp(retVal, outArgA);
	}

}
