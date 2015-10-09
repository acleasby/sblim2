/**
 * CIMCreateInstanceOp.java
 *
 * (C) Copyright IBM Corp. 2005, 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Roberto Pineiro, IBM, roberto.pineiro@us.ibm.com  
 * @author : Chung-hao Tan, IBM, chungtan@us.ibm.com
 * 
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1535756    2006-08-07  lupusalex    Make code warning free
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 */

package org.sblim.cimclient.internal.wbem.operations;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;

/**
 * CIMCreateInstanceOp
 * 
 */
public class CIMCreateInstanceOp extends CIMOperation {

	protected CIMInstance iInstance;

	/**
	 * Ctor.
	 * 
	 * @param pObjectName
	 * @param pInstance
	 */
	public CIMCreateInstanceOp(CIMObjectPath pObjectName, CIMInstance pInstance) {
		this.iMethodCall = "CreateInstance";
		this.iObjectName = pObjectName;
		this.iInstance = pInstance;
	}

	/**
	 * Returns the instance
	 * 
	 * @return The instance
	 */
	public CIMInstance getInstance() {
		return this.iInstance;
	}

}
