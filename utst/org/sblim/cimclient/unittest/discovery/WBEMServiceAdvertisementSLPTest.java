/**
 * (C) Copyright IBM Corp. 2007, 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1678915    2007-03-27  lupusalex    Integrated WBEM service discovery via SLP
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2) 
 */

package org.sblim.cimclient.unittest.discovery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.wbem.client.WBEMClient;

import org.sblim.cimclient.discovery.WBEMServiceAdvertisement;
import org.sblim.cimclient.internal.discovery.slp.WBEMServiceAdvertisementSLP;
import org.sblim.cimclient.internal.wbem.WBEMClientCIMXML;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.ServiceURL;

/**
 * Class WBEMServiceAdvertisementTest is responsible for testing the
 * WBEMServiceAdvertisementSLP class
 * 
 */
public class WBEMServiceAdvertisementSLPTest extends TestCase {

	static private List<String[]> cTestData = new LinkedList<String[]>();

	static {
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null, "root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.11.11.11", "https://55.66.77.99:5989", "IBM0815",
					"IBM Test Instrumentation 2", "CIM-XML", null, "root/ibm2" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null, "root/ibm,root/interop,root/cimv2" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null,
					"root/ibm, root/interop, root/cimv2" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null,
					"root/ibm\nroot/interop\nroot/cimv2" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null,
					"root/ibm \r\n root/interop,\r\nroot/cimv2" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "Other", "cim-xml", "root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "Other", "ws-man", "root/ibm" };
			cTestData.add(data);
		}
	}

	/**
	 * Builds a bunch of advertisements and verifies that the getters return
	 * correct data and the createClient() method works properly.
	 * 
	 * @throws Exception
	 */
	public void testAdvertisement() throws Exception {
		{
			Iterator<String[]> iter = cTestData.iterator();
			while (iter.hasNext()) {
				String[] data = iter.next();
				List<String> attributes = new ArrayList<String>();
				attributes.add("(" + WBEMServiceAdvertisement.SERVICE_ID + "=" + data[2] + ")");
				attributes
						.add("(" + WBEMServiceAdvertisement.SERVICE_HI_NAME + "=" + data[3] + ")");
				attributes.add("(" + WBEMServiceAdvertisement.COMM_MECHANISM + "=" + data[4] + ")");
				if (data[5] != null) {
					attributes.add("(" + WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC + "="
							+ data[5] + ")");
				}
				attributes.add("(" + WBEMServiceAdvertisement.INTEROP_NS + "=" + data[6] + ")");

				WBEMServiceAdvertisementSLP adv = new WBEMServiceAdvertisementSLP(data[0],
						new ServiceURL("service:wbem:" + data[1], ServiceURL.LIFETIME_DEFAULT),
						attributes);

				verify("Service type", EQUAL, adv.getConcreteServiceType(),
						data[1].split(":", 2)[0]);
				verify("Directory", EQUAL, adv.getDirectory(), data[0]);
				verify("Service url", EQUAL, adv.getServiceUrl(), data[1]);
				verify("Service id", EQUAL, adv.getAttribute(WBEMServiceAdvertisement.SERVICE_ID),
						data[2]);
				verify("Service name", EQUAL, adv
						.getAttribute(WBEMServiceAdvertisement.SERVICE_HI_NAME), data[3]);
				verify("Communication", EQUAL, adv
						.getAttribute(WBEMServiceAdvertisement.COMM_MECHANISM), data[4]);
				verify("Other communication", EQUAL, adv
						.getAttribute(WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC), data[5]);
				verify("Interop namespace", EQUAL, adv
						.getAttribute(WBEMServiceAdvertisement.INTEROP_NS), data[6]);

				String[] interops = adv.getInteropNamespaces();
				StringTokenizer tokenz = new StringTokenizer(data[6], ",\n\r ");
				verify("Number of interop namespaces", EQUAL, new Integer(interops.length),
						new Integer(tokenz.countTokens()));
				int i = 0;
				while (tokenz.hasMoreTokens()) {
					verify("Interop namespaces[" + i + "]", EQUAL, interops[i++], tokenz
							.nextToken());
				}

				String other = adv.getAttribute(WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC);
				if (other == null || other.equalsIgnoreCase("CIM-XML")) {
					Subject subject = new Subject();
					WBEMClient client = adv.createClient(subject, Locale.getAvailableLocales());
					verify("WBEMClient not null", client != null);
					verify("WBEMClient instance of WBEMClientCIMXML",
							(client instanceof WBEMClientCIMXML));
				} else {
					Subject subject = new Subject();
					try {
						adv.createClient(subject, Locale.getAvailableLocales());
						fail("WBEMClient was created for unsupported protocol: " + other);
					} catch (IllegalArgumentException e) {
						/* good boy */
					}
				}
			}
		}
	}
}
