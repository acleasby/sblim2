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
 * 1660756    2007-03-02  ebak         Embedded object support
 * 1689085    2007-04-10  ebak         Embedded object enhancements for Pegasus
 * 1688273    2007-04-16  ebak         Full support of HTTP trailers
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1714878    2007-05-08  ebak         Empty string property values are parsed as nulls
 * 1719991    2007-05-16  ebak         FVT: regression ClassCastException in EmbObjHandler
 * 1742873    2007-06-25  ebak         IPv6 ready cim-client
 * 1827728    2007-11-20  ebak         rework: embeddedInstances: attribute EmbeddedObject not set
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 */

package org.sblim.cimclient.unittest.wbem;

import javax.cim.CIMArgument;
import javax.cim.CIMClass;
import javax.cim.CIMDataType;
import javax.cim.CIMDateTime;
import javax.cim.CIMDateTimeAbsolute;
import javax.cim.CIMDateTimeInterval;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger32;
import javax.wbem.CloseableIterator;

import org.sblim.cimclient.unittest.TestCase;

/**
 * ParserTest
 */
public class ParserTest extends TestCase {

	private static interface Dumper {

		/**
		 * dump
		 * 
		 * @param pObj
		 * @return String
		 */
		public String dump(Object pObj);

		/**
		 * detailedCheck
		 * 
		 * @param pSmpObj
		 * @param pObj
		 * @return String
		 */
		public String detailedCheck(Object pSmpObj, Object pObj);
	}

	private void enumTest(CloseableIterator<Object> pItr, Object[] pObjA) {
		enumTest(pItr, pObjA, null, false);
	}

	private void enumTest(CloseableIterator<Object> pItr, Object[] pObjA, Dumper pDumper) {
		enumTest(pItr, pObjA, pDumper, false);
	}

	private void enumTest(CloseableIterator<Object> pItr, Object[] pObjA, Dumper pDumper,
			boolean pDebug) {
		int cnt = 0;
		while (pItr.hasNext()) {
			Object itrObj = pItr.next(), smpObj = pObjA[cnt];
			if (pDebug) {
				System.currentTimeMillis(); // breakpoint
			}
			boolean equal = smpObj.equals(itrObj);
			if (!equal) {
				String msg = "smpObj != obj\n\n";
				if (pDumper != null) {
					msg += ("\nsmpObj:\n" + pDumper.dump(smpObj) + "\nitrObj:\n"
							+ pDumper.dump(itrObj) + '\n');
					String detailed = pDumper.detailedCheck(smpObj, itrObj);
					if (detailed != null) msg += "Detailed check:" + detailed + "\n";
				}
				verify(msg, equal);
			}
			++cnt;
		}
		verify("number of sample objects (" + pObjA.length + ") != number of iterated objects ("
				+ cnt + ") !", pObjA.length == cnt);
	}

	@SuppressWarnings("null")
	static void compareList(StringBuffer pBuf, String pName, Object[] pSmpObjA, Object[] pObjA) {
		int smpCnt = pSmpObjA == null ? 0 : pSmpObjA.length;
		int cnt = pObjA == null ? 0 : pObjA.length;
		if (smpCnt == cnt) {
			for (int i = 0; i < smpCnt; i++) {
				Object smpObj = pSmpObjA[i];
				Object obj = pObjA[i];
				if (!smpObj.equals(obj)) {
					pBuf.append(pName + " at " + i + " aren't equal!\n" + "smpQuali:\n" + smpObj
							+ "\nquali:\n" + obj + "\n");
				}
			}
		} else {
			pBuf.append("smp" + pName + ".length(" + smpCnt + ")!=" + pName + ".length(" + cnt
					+ ")\n");
		}
	}

	class ClassDumper implements Dumper {

		public String dump(Object pObj) {
			CIMClass cl = (CIMClass) pObj;
			return cl + "\n" + "getObjectPath()=" + cl.getObjectPath().toString() + "\n"
					+ "getKeys().length=" + cl.getKeys().length + "\n" + "getQualifierCount()="
					+ cl.getQualifierCount() + "\n" + "getPropertyCount()=" + cl.getPropertyCount()
					+ "\n" + "getMethodCount()=" + cl.getMethodCount() + "\n" + "isAssociation()="
					+ cl.isAssociation() + "\n" + "isKeyed()=" + cl.isKeyed() + "\n";
		}

		public String detailedCheck(Object pSmpObj, Object pObj) {
			StringBuffer buf = new StringBuffer();
			CIMClass smpCl = (CIMClass) pSmpObj;
			CIMClass cl = (CIMClass) pObj;
			compareList(buf, "Qualifiers", smpCl.getQualifiers(), cl.getQualifiers());
			compareList(buf, "Properties", smpCl.getProperties(), cl.getProperties());
			compareList(buf, "Methods", smpCl.getMethods(), cl.getMethods());
			return buf.toString();
		}
	}

	private void enumClassesTest(CloseableIterator<Object> pItr) {
		enumTest(pItr, CIMBuilder.buildClasses(), new ClassDumper());
	}

	/**
	 * testEnumClassesDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumClassesDOM() throws Exception {
		enumClassesTest(Common.parseWithDOM(CIMBuilder.getEnumerateClassesRsp()));
	}

	/**
	 * testEnumClassesSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumClassesSAX() throws Exception {
		enumClassesTest(Common.parseWithSAX(CIMBuilder.getEnumerateClassesRsp()));
	}

	/**
	 * testEnumClassesPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumClassesPULL() throws Exception {
		enumClassesTest(Common.parseWithPULL(CIMBuilder.getEnumerateClassesRsp()));
	}

	class InstDumper implements Dumper {

		public String dump(Object pObj) {
			CIMInstance inst = (CIMInstance) pObj;
			return "getObjectPath():" + inst.getObjectPath() + "\n"
					+ new ObjPathDumper().dump(inst.getObjectPath());
		}

		/**
		 * @param pSmpObj
		 * @param pObj
		 * @return String detailedCheck
		 */
		public String detailedCheck(Object pSmpObj, Object pObj) {
			return null;
		}

	}

	private void enumInstancesTest(CloseableIterator<Object> pItr) {
		enumTest(pItr, CIMBuilder.buildInstances());
	}

	/**
	 * testEnumInstancesDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesDOM() throws Exception {
		enumInstancesTest(Common.parseWithDOM(CIMBuilder.getEnumerateInstancesRsp()));
	}

	/**
	 * testEnumInstancesSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesSAX() throws Exception {
		enumInstancesTest(Common.parseWithSAX(CIMBuilder.getEnumerateInstancesRsp()));
	}

	/**
	 * testEnumInstancesPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesPULL() throws Exception {
		enumInstancesTest(Common.parseWithPULL(CIMBuilder.getEnumerateInstancesRsp()));
	}

	class ObjPathDumper implements Dumper {

		public String dump(Object pObj) {
			CIMObjectPath op = (CIMObjectPath) pObj;
			CIMProperty<?>[] keys = op.getKeys();
			String keyList;
			if (keys.length > 0) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < keys.length; i++)
					buf.append("  " + keys[i]);
				keyList = buf.toString();
			} else keyList = "";
			return "getScheme():" + op.getScheme() + "\ngetHost():" + op.getHost() + "\ngetPort():"
					+ op.getPort() + "\ngetNamespace():" + op.getNamespace() + "\ngetObjectName():"
					+ op.getObjectName() + "\ngetKeys().length:" + keys.length + '\n' + keyList
					+ '\n';
		}

		/**
		 * @param pSmpObj
		 * @param pObj
		 * @return String detailedCheck
		 */
		public String detailedCheck(Object pSmpObj, Object pObj) {
			return null;
		}

	}

	private void associatorClassNamesTest(CloseableIterator<Object> pItr) {
		enumTest(pItr, CIMBuilder.buildClassObjPathes());
	}

	/**
	 * testAssociatorClassNamesDOM
	 * 
	 * @throws Exception
	 */
	public void testAssociatorClassNamesDOM() throws Exception {
		associatorClassNamesTest(Common.parseWithDOM(CIMBuilder.getClassObjPathesRsp()));
	}

	/**
	 * testAssociatorClassNamesSAX
	 * 
	 * @throws Exception
	 */
	public void testAssociatorClassNamesSAX() throws Exception {
		associatorClassNamesTest(Common.parseWithSAX(CIMBuilder.getClassObjPathesRsp()));
	}

	/**
	 * testAssociatorClassNamesPULL
	 * 
	 * @throws Exception
	 */
	public void testAssociatorClassNamesPULL() throws Exception {
		associatorClassNamesTest(Common.parseWithPULL(CIMBuilder.getClassObjPathesRsp()));
	}

	private void associatorInstanceNamesTest(CloseableIterator<Object> pItr, boolean pDebug) {
		enumTest(pItr, CIMBuilder.buildInstObjPathes(), new ObjPathDumper(), pDebug);
	}

	/**
	 * testAssociatorInstanceNamesDOM
	 * 
	 * @throws Exception
	 */
	public void testAssociatorInstanceNamesDOM() throws Exception {
		associatorInstanceNamesTest(Common.parseWithDOM(CIMBuilder.getInstObjPathesRsp()), false);
	}

	/**
	 * testAssociatorInstanceNamesSAX
	 * 
	 * @throws Exception
	 */
	public void testAssociatorInstanceNamesSAX() throws Exception {
		associatorInstanceNamesTest(Common.parseWithSAX(CIMBuilder.getInstObjPathesRsp()), false);
	}

	/**
	 * testAssociatorInstanceNamesPULL
	 * 
	 * @throws Exception
	 */
	public void testAssociatorInstanceNamesPULL() throws Exception {
		associatorInstanceNamesTest(Common.parseWithPULL(CIMBuilder.getInstObjPathesRsp()), false);
	}

	private void assocClassesTest(CloseableIterator<Object> pItr, boolean pDebug) {
		enumTest(pItr, CIMBuilder.buildClasses(), null, pDebug);
	}

	/**
	 * testAssocClassesDOM
	 * 
	 * @throws Exception
	 */
	public void testAssocClassesDOM() throws Exception {
		assocClassesTest(Common.parseWithDOM(CIMBuilder.getAssocClassesRsp()), false);
	}

	/**
	 * testAssocClassesSAX
	 * 
	 * @throws Exception
	 */
	public void testAssocClassesSAX() throws Exception {
		assocClassesTest(Common.parseWithSAX(CIMBuilder.getAssocClassesRsp()), false);
	}

	/**
	 * testAssocClassesPULL
	 * 
	 * @throws Exception
	 */
	public void testAssocClassesPULL() throws Exception {
		assocClassesTest(Common.parseWithSAX(CIMBuilder.getAssocClassesRsp()), false);
	}

	private void assocInstancesTest(CloseableIterator<Object> pItr, boolean pDebug) {
		enumTest(pItr, CIMBuilder.buildInstances(), new InstDumper(), pDebug);
	}

	/**
	 * testAssocInstancesDOM
	 * 
	 * @throws Exception
	 */
	public void testAssocInstancesDOM() throws Exception {
		assocInstancesTest(Common.parseWithDOM(CIMBuilder.getAssocInstancesRsp()), false);
	}

	/**
	 * testAssocInstancesSAX
	 * 
	 * @throws Exception
	 */
	public void testAssocInstancesSAX() throws Exception {
		assocInstancesTest(Common.parseWithSAX(CIMBuilder.getAssocInstancesRsp()), false);
	}

	/**
	 * testAssocInstancesPULL
	 * 
	 * @throws Exception
	 */
	public void testAssocInstancesPULL() throws Exception {
		assocInstancesTest(Common.parseWithSAX(CIMBuilder.getAssocInstancesRsp()), false);
	}

	private void enumQualiTypesTest(CloseableIterator<Object> pItr, boolean pDebug) {
		enumTest(pItr, CIMBuilder.buildQualiTypes(), null, pDebug);
	}

	/**
	 * testEnumQualiTypesDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumQualiTypesDOM() throws Exception {
		enumQualiTypesTest(Common.parseWithDOM(CIMBuilder.getEnumQauliTypesRsp()), false);
	}

	/**
	 * testEnumQualiTypesSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumQualiTypesSAX() throws Exception {
		enumQualiTypesTest(Common.parseWithSAX(CIMBuilder.getEnumQauliTypesRsp()), false);
	}

	/**
	 * testEnumQualiTypesPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumQualiTypesPULL() throws Exception {
		enumQualiTypesTest(Common.parseWithPULL(CIMBuilder.getEnumQauliTypesRsp()), false);
	}

	private static MethodRspChecker cMethodRsp;

	private MethodRspChecker getMethodRspChk() throws Exception {
		if (cMethodRsp == null) cMethodRsp = new MethodRspChecker(new Integer(42), CIMBuilder
				.buildArguments(), CIMBuilder.getMethodRsp());
		return cMethodRsp;
	}

	/**
	 * testMethodRspSAX
	 * 
	 * @throws Exception
	 */
	public void testMethodRspSAX() throws Exception {
		String res = getMethodRspChk().testSAX();
		verify(res, res == null);
	}

	/**
	 * testMethodRspDOM
	 * 
	 * @throws Exception
	 */
	public void testMethodRspDOM() throws Exception {
		String res = getMethodRspChk().testDOM();
		verify(res, res == null);
	}

	private static final MethodRspChecker SVC_METHOD_RSP0 = new MethodRspChecker(new Integer(42),
			new CIMArgument[] {
					new CIMArgument<Integer>("pOutInt", CIMDataType.SINT32_T, new Integer(12)),
					new CIMArgument<Boolean>("pOutBool", CIMDataType.BOOLEAN_T, Boolean.TRUE),
					new CIMArgument<CIMDateTime[]>("pOutDTA", CIMDataType.DATETIME_ARRAY_T,
							new CIMDateTime[] {
									new CIMDateTimeAbsolute("20070314160503.566012+010"),
									new CIMDateTimeInterval("00000133102418.001328:000") }) },
			"data/SVCMethodRsp.xml");

	/**
	 * testSVCMethodRsp0SAX
	 * 
	 * @throws Exception
	 */
	public void testSVCMethodRsp0SAX() throws Exception {
		String res = SVC_METHOD_RSP0.testSAX();
		verify(res, res == null);
	}

	/**
	 * testSVCMethodRsp0DOM
	 * 
	 * @throws Exception
	 */
	public void testSVCMethodRsp0DOM() throws Exception {
		String res = SVC_METHOD_RSP0.testDOM();
		verify(res, res == null);
	}

	private static final MethodRspChecker SVC_METHOD_RSP_SMPL0 = new MethodRspChecker(
			new UnsignedInteger32(0), new CIMArgument[] { new CIMArgument<CIMObjectPath>("Account",
					new CIMDataType("IBMTS_Account"),
					// String host, String namespace, String objectName,
					// CIMProperty[] keys
					new CIMObjectPath(null, "CL3E2289", null, "root/ibm", "IBMTS_Account",
							new CIMProperty[] {
									new CIMProperty<String>("SystemName", CIMDataType.STRING_T,
											"icat", true, false, null),
									new CIMProperty<String>("SystemCreationClassName",
											CIMDataType.STRING_T, "IBMTS_Account", true, false,
											null),
									new CIMProperty<String>("Name", CIMDataType.STRING_T, "dummy1",
											true, false, null),
									new CIMProperty<String>("CreationClassName",
											CIMDataType.STRING_T, "IBMTS_Account", true, false,
											null) })) }, "data/SVCMethodRsp.smpl0.xml");

	/**
	 * testSVCMethodRspSmpl0SAX
	 * 
	 * @throws Exception
	 */
	public void testSVCMethodRspSmpl0SAX() throws Exception {
		String res = SVC_METHOD_RSP_SMPL0.testSAX();
		verify(res, res == null);
	}

	/**
	 * testSVCMethodRspSmpl0DOM
	 * 
	 * @throws Exception
	 */
	public void testSVCMethodRspSmpl0DOM() throws Exception {
		String res = SVC_METHOD_RSP_SMPL0.testDOM();
		verify(res, res == null);
	}

	/**
	 * Checks the correct building of the METHODCALL CIM-XML element.
	 * 
	 * @throws Exception
	 */
	public void testMethodCallSAX() throws Exception {
		MethodCallChecker methodCallChk = new MethodCallChecker("DummyMethode", Common.LOCALPATH
				.build("Gyurcsany", null), CIMBuilder.buildArguments());
		String res = methodCallChk.testSAX();
		verify(res, res == null);
	}

}
