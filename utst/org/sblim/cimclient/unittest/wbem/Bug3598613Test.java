/**
 * (C) Copyright IBM Corp. 2013
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
 * 3598613    2013-01-11  blaschke-oss different data type in cim instance and cim object path
 */
package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;

import javax.cim.CIMInstance;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;

import org.sblim.cimclient.internal.util.WBEMConfiguration;
import org.sblim.cimclient.unittest.TestCase;

/**
 * 
 * Class Bug3598163Test is responsible for testing that the data type of the
 * <code>CIMObjectPath</code>'s numeric keys matches the corresponding key from
 * the <code>CIMProperty[]</code>.
 * 
 */
public class Bug3598613Test extends TestCase {

	private InputStream getInputStream(String pName) {
		InputStream is = getClass().getResourceAsStream(pName);
		verify("Failed to load resource! : " + pName, is != null);
		return is;
	}

	private InputStream getInstIS() {
		return getInputStream("data/Bug3598613.xml");
	}

	private void checkEnumInstResult(CloseableIterator<Object> pItr) {
		verify("CIMInstance is not retrieved!", pItr.hasNext());

		try {
			while (pItr.hasNext()) {
				Object next = pItr.next();

				if (next instanceof CIMInstance) {
					CIMInstance inst = (CIMInstance) next;
					if (WBEMConfiguration.getGlobalConfiguration().synchronizeNumericKeyDataTypes()) {
						verify("Synchronized numeric data types do not match! "
								+ inst.getProperty("TruststoreType").getDataType() + " vs "
								+ inst.getObjectPath().getKey("TruststoreType").getDataType(),
								inst.getProperty("TruststoreType").getDataType()
										.equals(
												inst.getObjectPath().getKey("TruststoreType")
														.getDataType()));
						verify("Synchronized numeric values do not match! "
								+ inst.getProperty("TruststoreType").getValue() + " vs "
								+ inst.getObjectPath().getKey("TruststoreType").getValue(), inst
								.getProperty("TruststoreType").getValue().equals(
										inst.getObjectPath().getKey("TruststoreType").getValue()));
					} else {
						verify("Unsynchronized numeric data types match! "
								+ inst.getProperty("TruststoreType").getDataType() + " vs "
								+ inst.getObjectPath().getKey("TruststoreType").getDataType(),
								!inst.getProperty("TruststoreType").getDataType()
										.equals(
												inst.getObjectPath().getKey("TruststoreType")
														.getDataType()));
						verify("Unsynchronized numeric values match! "
								+ inst.getProperty("TruststoreType").getValue() + " vs "
								+ inst.getObjectPath().getKey("TruststoreType").getValue(), !inst
								.getProperty("TruststoreType").getValue().equals(
										inst.getObjectPath().getKey("TruststoreType").getValue()));
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
