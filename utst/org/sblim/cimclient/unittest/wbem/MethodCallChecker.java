/**
 * (C) Copyright IBM Corp. 2006, 2009
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
 * 1827728    2007-11-20  ebak         rework: embeddedInstances: attribute EmbeddedObject not set
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayInputStream;

import javax.cim.CIMArgument;
import javax.cim.CIMObjectPath;

import org.sblim.cimclient.internal.cimxml.CIMClientXML_HelperImpl;
import org.sblim.cimclient.internal.cimxml.sax.node.MethodCallNode;
import org.w3c.dom.Document;

/**
 * MethodCallChecker checks METHODCALL CIM-XML element building and parsing.
 */
public class MethodCallChecker {

	private String iRefName;

	private CIMArgument<?>[] iRefArgA;

	private CIMObjectPath iRefOP;

	/**
	 * Ctor.
	 * 
	 * @param pName
	 * @param pOP
	 * @param pArgA
	 */
	public MethodCallChecker(String pName, CIMObjectPath pOP, CIMArgument<?>[] pArgA) {
		this.iRefName = pName;
		this.iRefOP = pOP;
		this.iRefArgA = pArgA;
	}

	/**
	 * testSAX
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String testSAX() throws Exception {
		// build XML
		Document doc = CIMBuilder.createDoc();
		CIMClientXML_HelperImpl xmlHelper = new CIMClientXML_HelperImpl();
		byte[] xmlBytes = CIMBuilder.getXML(doc, xmlHelper.invokeMethod_request(doc, this.iRefOP,
				this.iRefName, this.iRefArgA));
		// parse XML
		MethodCallNode methodCallNode = Common
				.getMethodCallNode(new ByteArrayInputStream(xmlBytes));
		// check
		if (!this.iRefName.equals(methodCallNode.getName())) return "refName:" + this.iRefName
				+ " != " + methodCallNode.getName();
		if (!this.iRefOP.equals(methodCallNode.getCIMObjectPath())) return "refPath:" + this.iRefOP
				+ " != " + methodCallNode.getCIMObjectPath();
		int refArgALen = this.iRefArgA == null ? 0 : this.iRefArgA.length;
		int argALen = methodCallNode.getArgumentCount();
		if (refArgALen != argALen) return "refArgALen:" + refArgALen + " != " + argALen;
		CIMArgument<?>[] argA = methodCallNode.getCIMArguments();
		for (int i = 0; i < argALen; i++) {
			CIMArgument<?> refArg = this.iRefArgA[i], arg = argA[i];
			if (!refArg.equals(arg)) return "refArg:" + refArg + " != " + arg;
		}
		return null;
	}

	/*
	 * private static void dump(CIMArgument[] pArgA) { for (int i = 0; i <
	 * pArgA.length; i++) { System.out.println(pArgA[i]); } }
	 */

}
