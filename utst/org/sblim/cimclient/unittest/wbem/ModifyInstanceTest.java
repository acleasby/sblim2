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
 * 1686000    2007-04-20  ebak         modifyInstance() missing from WBEMClient
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.cim.CIMArgument;
import javax.cim.CIMClass;
import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;

import org.sblim.cimclient.internal.cimxml.CIMClientXML_HelperImpl;
import org.sblim.cimclient.internal.cimxml.sax.node.IMethodCallNode;
import org.sblim.cimclient.internal.util.MOF;
import org.sblim.cimclient.unittest.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ModifyInstanceTest checks that the building of ModifyInstance request is
 * good.
 */
public class ModifyInstanceTest extends TestCase {

	/**
	 * @param pMsg
	 */
	private static void debug(String pMsg) {
	// System.out.println(pMsg);
	}

	private InputStream buildRequest(CIMInstance pInst, String[] pPropNameA) throws Exception {
		Document doc = CIMBuilder.createDoc();
		CIMClientXML_HelperImpl xmlHelper = new CIMClientXML_HelperImpl();
		Element reqE = xmlHelper.setInstance_request(doc, pInst.getObjectPath(), pInst, true,
				pPropNameA);
		byte[] bA = CIMBuilder.getXML(doc, reqE);
		debug(new String(bA));
		return new ByteArrayInputStream(bA);
	}

	private static final String MODIFYINST = "ModifyInstance", MODIFIEDINST = "ModifiedInstance",
			INCLUDEQUALIS = "IncludeQualifiers", PROPLIST = "PropertyList";

	private void verifyArgs(CIMArgument<?> pRefArg, CIMArgument<?> pArg) {
		verify("refArg != arg!\n" + "refArg: " + pRefArg + "\narg: " + pArg, pRefArg.equals(pArg));
	}

	private void checkModifiedInstArg(IMethodCallNode pMethodCallNode, CIMInstance pInst) {
		verifyArgs(new CIMArgument<CIMInstance>(MODIFIEDINST, CIMDataType.OBJECT_T, pInst),
				pMethodCallNode.getArgument(MODIFIEDINST));
	}

	private void checkIncludeQualisArg(IMethodCallNode pMethodCallNode) {
		verifyArgs(
		/*
		 * IPARAMVALUE doesn't contain type information, so it's VALUE
		 * subelement becomes string instead of boolean.
		 */
		new CIMArgument<Object>(INCLUDEQUALIS, CIMDataType.STRING_T, MOF.TRUE), pMethodCallNode
				.getArgument(INCLUDEQUALIS));
	}

	private void checkPropertyListArg(IMethodCallNode pMethodCallNode, String[] pPropNameA) {
		if (pPropNameA == null) return;
		verifyArgs(new CIMArgument<Object>(PROPLIST, CIMDataType.STRING_ARRAY_T, pPropNameA),
				pMethodCallNode.getArgument(PROPLIST));
	}

	private void checkModifyInstance(CIMInstance pInst, String[] pPropNameA) throws Exception {
		IMethodCallNode methodCallNode = Common.getIMethodCallNode(buildRequest(pInst, pPropNameA));
		// debug(methodCallNode);
		verify("MethodResponse's name shoud be " + MODIFYINST + ", not "
				+ methodCallNode.getNodeName() + "!", MODIFYINST.equalsIgnoreCase(methodCallNode
				.getName()));
		String instNS = pInst.getObjectPath().getNamespace();
		String nodeNS = methodCallNode.getCIMObjectPath().getNamespace();
		verify("pInst's namespace != methodCallNode's namespace !\n" + "pInst's namespace:"
				+ instNS + "\nmethodCallNode's namespace:" + nodeNS, instNS
				.equalsIgnoreCase(nodeNS));
		/*
		 * ModifiedInstance -> CIMInstance (pInst) IncludeQualifiers ->
		 * Boolean(true) PropertyList -> String[] (pPropNameA)
		 */
		checkModifiedInstArg(methodCallNode, pInst);
		checkIncludeQualisArg(methodCallNode);
		checkPropertyListArg(methodCallNode, pPropNameA);
	}

	private static final CIMProperty<String> KEY_PROP = new CIMProperty<String>("KeyProp",
			CIMDataType.STRING_T, "_oOo_", true, false, null);

	private static CIMProperty<Object> mkProp(String pName, CIMDataType pType, Object pValue) {
		return new CIMProperty<Object>(pName, pType, pValue, false, false, null);
	}

	private static final CIMObjectPath REF_INST_OP = Common.LOCALPATH.build("TestClass",
			"root/cimv2", new CIMProperty[] { KEY_PROP });

	private static final CIMInstance REF_INST = new CIMInstance(REF_INST_OP, new CIMProperty[] {
			KEY_PROP,
			mkProp("StrAProp", CIMDataType.STRING_ARRAY_T, new String[] { "alma", "korte" }),
			mkProp("InstRefProp", new CIMDataType("TestClass"), Common.LOCALPATH.build("TestClass",
					"root/cimv2", new CIMProperty[] { KEY_PROP })),
			mkProp("ClassRefProp", new CIMDataType("TestClass"), Common.LOCALPATH.build(
					"TestClass", "root/cimv2")),
			mkProp("ClassProp", CIMDataType.CLASS_T, new CIMClass("TestClass", null, null, null,
					null)),
			mkProp("InstProp", CIMDataType.OBJECT_T, new CIMInstance(REF_INST_OP,
					new CIMProperty<?>[] { KEY_PROP })) });

	/**
	 * testWithPropList
	 * 
	 * @throws Exception
	 */
	public void testWithPropList() throws Exception {
		checkModifyInstance(REF_INST, new String[] { "StrAProp", "ClassRefProp" });
	}

	/**
	 * testWithEmptyPropList
	 * 
	 * @throws Exception
	 */
	public void testWithEmptyPropList() throws Exception {
		checkModifyInstance(REF_INST, new String[] {});
	}

	/**
	 * testWithNullPropList
	 * 
	 * @throws Exception
	 */
	public void testWithNullPropList() throws Exception {
		checkModifyInstance(REF_INST, null);
	}

}
