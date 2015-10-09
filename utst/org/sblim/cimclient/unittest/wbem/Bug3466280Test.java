/**
 * (C) Copyright IBM Corp. 2012
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Endre Bak, ebak@de.ibm.com
 *           Dave Blaschke, blaschke@us.ibm.com
 * 
 * Flag       Date        Prog         Description
 * -------------------------------------------------------------------------------
 * 3466280    2012-04-23  blaschke-oss get instance failure for CIM_IndicationSubscription
 */
package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;

import org.sblim.cimclient.unittest.TestCase;

/**
 * 
 * Class Bug3466280Test is responsible for testing that the default namespace is
 * NOT included in the CLASSNAME or INSTANCENAME elements within a
 * VALUE.REFERENCE element.
 * 
 */
public class Bug3466280Test extends TestCase {

	private InputStream getInputStream(String pName) {
		InputStream is = getClass().getResourceAsStream(pName);
		verify("Failed to load resource! : " + pName, is != null);
		return is;
	}

	private InputStream getInstIS() {
		return getInputStream("data/Bug3466280.xml");
	}

	@SuppressWarnings("null")
	private void checkEnumInstResult(CloseableIterator<Object> pItr) {
		verify("CIMInstance is not retrieved!", pItr.hasNext());

		try {
			while (pItr.hasNext()) {
				Object next = pItr.next();

				if (next instanceof CIMInstance) {
					CIMObjectPath cop = ((CIMInstance) next).getObjectPath();
					verify("Subscription CIMObjectPath is null!", cop != null);
					CIMProperty<?> prop = cop.getKey("Handler");
					verify("Subscription handler is null!", prop != null);
					CIMObjectPath cop2 = (CIMObjectPath) prop.getValue();
					CIMProperty<?> prop2 = cop2.getKey("Name");
					verify("Subscription handler name is null!", prop2 != null);
					String name = (String) prop2.getValue();

					// The handler instances named SNMP, TEC, Log and Email
					// define the VALUE.REFERENCE with INSTANCENAME, so they
					// should NOT include a namespace. The other handler
					// instances define the VALUE.REFERENCE with
					// LOCALINSTANCEPATH, so they should include a namespace.
					if ("SNMP".equalsIgnoreCase(name) || "TEC".equalsIgnoreCase(name)
							|| "Log".equalsIgnoreCase(name) || "Email".equalsIgnoreCase(name)) {
						verify("Subscription handler namespace not null!",
								cop2.getNamespace() == null);
					} else {
						verify("Subscription handler namespace not correct!",
								cop2.getNamespace() != null
										&& "root/aristos".equalsIgnoreCase(cop2.getNamespace()));
					}
				}
			}
		} catch (Exception e) {
			try {
				throw pItr.getWBEMException();
			} catch (WBEMException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * testEnumInstancesDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesDOM() throws Exception {
		checkEnumInstResult(Common.parseWithDOM(getInstIS()));
	}

	/**
	 * testEnumInstancesSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesSAX() throws Exception {
		checkEnumInstResult(Common.parseWithSAX(getInstIS()));
	}

	/**
	 * testEnumInstancesPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesPULL() throws Exception {
		checkEnumInstResult(Common.parseWithPULL(getInstIS()));
	}
}
