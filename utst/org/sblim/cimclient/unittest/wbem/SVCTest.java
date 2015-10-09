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
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1719991    2007-05-16  ebak         FVT: regression ClassCastException in EmbObjHandler
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;
import java.util.ArrayList;

import javax.cim.CIMClass;
import javax.cim.CIMClassProperty;
import javax.cim.CIMDataType;
import javax.cim.CIMObjectPath;
import javax.cim.CIMQualifier;
import javax.cim.CIMQualifierType;
import javax.wbem.CloseableIterator;

import org.sblim.cimclient.unittest.TestCase;

/**
 * SVC CIMOM sends typed CIM-XML elements in a non-standard way. The TYPE
 * attribute is included in the VALUE or VALUE.ARRAY element not in the
 * enclosing element as the standard says.
 */
public class SVCTest extends TestCase {

	private final CIMClass REF_CLASS = new CIMClass(
			Common.LOCALPATH.build("TestClass", null),
			null,
			new CIMQualifier[] {
					new CIMQualifier<String>("StringQuali", CIMDataType.STRING_T, "Hello world!", 0),
					new CIMQualifier<Integer[]>("IntAQuali", CIMDataType.SINT32_ARRAY_T,
							new Integer[] { new Integer(12), new Integer(24) }, 0) },
			new CIMClassProperty[] {
					new CIMClassProperty<Integer>("IntProp", CIMDataType.SINT32_T, new Integer(38),
							null, false, false, null),
					new CIMClassProperty<Boolean[]>("BoolPropArray", CIMDataType.BOOLEAN_ARRAY_T,
							new Boolean[] { Boolean.TRUE, Boolean.FALSE }, null, false, false, null) },
			null, false, false);

	private InputStream getIS(String pName) {
		InputStream is = getClass().getResourceAsStream(pName);
		verify("Failed to load resource! : " + pName, is != null);
		return is;
	}

	private static final String GETCLASS = "data/SVCGetClass.xml";

	/**
	 * PROPERTY, PROPERTY.ARRAY and QUALIFIER elements are tested here.
	 */
	private void checkGetClass(CloseableIterator<Object> pItr) {
		verify("Iterator is empty!", pItr.hasNext());
		CIMClass cl = (CIMClass) pItr.next();
		verify("REF_CLASS != cl\nREF_CLASS: " + this.REF_CLASS + "\ncl: " + cl, this.REF_CLASS
				.equals(cl));
	}

	/**
	 * testGetClassSAX
	 * 
	 * @throws Exception
	 */
	public void testGetClassSAX() throws Exception {
		checkGetClass(Common.parseWithSAX(getIS(GETCLASS)));
	}

	/**
	 * testGetClassPULL
	 * 
	 * @throws Exception
	 */
	public void testGetClassPULL() throws Exception {
		checkGetClass(Common.parseWithPULL(getIS(GETCLASS)));
	}

	/**
	 * testGetInstanceDOM
	 * 
	 * @throws Exception
	 */
	public void testGetClassDOM() throws Exception {
		checkGetClass(Common.parseWithDOM(getIS(GETCLASS)));
	}

	private static final CIMQualifierType<?>[] REF_QUALI_TYPE_A = {
			new CIMQualifierType<Boolean>(new CIMObjectPath(null, null, null, null, "BoolQuali",
					null), CIMDataType.BOOLEAN_T, Boolean.FALSE, 0, 0),
			new CIMQualifierType<Integer[]>(new CIMObjectPath(null, null, null, null, "IntAQuali",
					null), CIMDataType.SINT32_ARRAY_T, new Integer[] { new Integer(-5),
					new Integer(15) }, 0, 0) };

	private static final String ENUMQUALIS = "data/SVCEnumQualiTypes.xml";

	/**
	 * checks QUALIFIER.DECLARATION
	 * 
	 * @param pQualiA
	 */
	@SuppressWarnings("null")
	private void checkQualis(CloseableIterator<Object> pItr) {
		ArrayList<Object> qAL = new ArrayList<Object>();
		while (pItr.hasNext())
			qAL.add(pItr.next());
		CIMQualifierType<?>[] qualiTypeA = qAL.toArray(new CIMQualifierType[0]);
		int qualiTypeALen = qualiTypeA == null ? 0 : qualiTypeA.length;
		verify("REF_QUALI_TYPE_A.length(" + REF_QUALI_TYPE_A.length + ") != qualiTypeALen("
				+ qualiTypeALen + ") !", REF_QUALI_TYPE_A.length == qualiTypeALen);
		for (int i = 0; i < REF_QUALI_TYPE_A.length; i++) {
			CIMQualifierType<?> refQualiT = REF_QUALI_TYPE_A[i];
			CIMQualifierType<?> qualiT = qualiTypeA[i];
			verify("At idx:" + i + " refQualiT!=qualiT !" + "\nrefQualiT: " + refQualiT
					+ "\nqualiT: " + qualiT, refQualiT.equals(qualiT));
		}
	}

	/**
	 * testEnumQualisSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumQualisSAX() throws Exception {
		checkQualis(Common.parseWithSAX(getIS(ENUMQUALIS)));
	}

	/**
	 * testEnumQualisPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumQualisPULL() throws Exception {
		checkQualis(Common.parseWithPULL(getIS(ENUMQUALIS)));
	}

	/**
	 * testEnumQualisDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumQualisDOM() throws Exception {
		checkQualis(Common.parseWithDOM(getIS(ENUMQUALIS)));
	}

}
