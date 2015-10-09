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
 * 1737141    2007-06-19  ebak         Sync up with JSR48 evolution
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 */

package org.sblim.cimclient.unittest.cim;

import javax.cim.CIMDataType;
import javax.cim.CIMQualifiedElementInterface;
import javax.cim.CIMQualifier;

/**
 * Stores commonly used methods/fields.
 */
public class Common {

	/**
	 * REF_QUALIS
	 */
	public static final CIMQualifier<?>[] REF_QUALIS = new CIMQualifier[] {
			new CIMQualifier<Integer>("LocalQuali", CIMDataType.SINT32_T, new Integer(10), 0),
			new CIMQualifier<Integer>("PropagatedQuali", CIMDataType.SINT32_T, new Integer(-10), 0,
					true) };

	/**
	 * checkQualis
	 * 
	 * @param pElement
	 * @param pInclQualis
	 * @param pLocalOnly
	 * @return String describing the problem or null.
	 */
	public static String checkQualis(CIMQualifiedElementInterface pElement, boolean pInclQualis,
			boolean pLocalOnly) {
		String paramStr = "pElement :" + pElement + "\npInclQualis:" + pInclQualis
				+ "\npLocalOnly:" + pLocalOnly;
		if (pInclQualis) {
			int refQualiCnt = 0;
			for (int i = 0; i < Common.REF_QUALIS.length; i++) {
				CIMQualifier<?> refQuali = Common.REF_QUALIS[i];
				if (pLocalOnly && refQuali.isPropagated()) continue;
				++refQualiCnt;
				CIMQualifier<?> quali = pElement.getQualifier(refQuali.getName());
				if (!refQuali.equals(quali)) return "Qualifiers don't match!\nrefQuali:" + refQuali
						+ "\nquali:" + quali + "\nparamStr:\n" + paramStr;
			}
			if (refQualiCnt != pElement.getQualifierCount()) return "Qualifier counts don't match!";
		} else {
			if (pElement.getQualifierCount() != 0) return "QualifierCount!=0 !";
		}
		return null;
	}

}
