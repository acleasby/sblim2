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
 * 1734936    2007-06-11  lupusalex    DiscovererSLPTest fails in some environments
 * 1804402    2007-09-28  ebak         IPv6 ready SLP
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2) 
 */

package org.sblim.cimclient.unittest.discovery;

import java.io.IOException;
import java.net.BindException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.sblim.cimclient.discovery.Discoverer;
import org.sblim.cimclient.discovery.DiscovererFactory;
import org.sblim.cimclient.discovery.WBEMServiceAdvertisement;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.Advertiser;
import org.sblim.slp.ServiceLocationAttribute;
import org.sblim.slp.ServiceLocationException;
import org.sblim.slp.ServiceLocationManager;
import org.sblim.slp.ServiceURL;
import org.sblim.slp.internal.SLPConfig;
import org.sblim.slp.internal.sa.ServiceAgent;

/**
 * Class DiscovererSLPTest is responsible for testing the DiscovererSLP class
 * 
 */
public class DiscovererSLPTest extends TestCase {

	// ports under 1024 are not bindable for non-root users on Linux
	static private final int PORT = 11223;

	static private List<String[]> cTestData = new LinkedList<String[]>();

	static {
		{
			String[] data = new String[] { "http://192.168.1.1:5988", "IBM4711", "cim-XML", null,
					"root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "https://192.168.1.1:5989", "IBM4711", "cim-XML", null,
					"root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "http://192.168.1.1:8888", "IBM4711", "other", "ws-man",
					"root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "https://192.168.1.2:5989", "IBM0815", "cim-XML", null,
					"root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "https://192.168.1.3:5989", "XXX", "cim-XML", null,
					"interop" };
			cTestData.add(data);
		}

	}

	private static final String SERVICE_WBEM = "service:wbem:";

	private ServiceAgent iSrvAgent;

	/**
	 * Sets up a SLP SA, registers fake services and checks afterwards if these
	 * are correctly discovered by the findeWBEMServices() method.
	 * 
	 * @throws Exception
	 */
	public void testFindWbemServices() throws Exception {
		if (!startSlpSa()) return;

		try {
			if (!registerFakeData()) { return; }
			Discoverer discoverer = DiscovererFactory.getDiscoverer(DiscovererFactory.SLP);
			WBEMServiceAdvertisement[] advertisements = discoverer
					.findWbemServices(new String[] { SLPConfig.getLoopbackV4().getHostAddress() });
			verify("Number of advertisements", EQUAL, new Integer(advertisements.length),
					new Integer(cTestData.size()));
			outer: for (int i = 0; i < advertisements.length; ++i) {
				WBEMServiceAdvertisement advertisement = advertisements[i];
				for (int k = 0; k < cTestData.size(); ++k) {
					String[] data = cTestData.get(k);
					if (advertisement.getServiceUrl().equals(data[0])) {
						verify(WBEMServiceAdvertisement.SERVICE_ID, EQUAL, advertisement
								.getAttribute(WBEMServiceAdvertisement.SERVICE_ID), data[1]);
						verify(WBEMServiceAdvertisement.COMM_MECHANISM, EQUAL, advertisement
								.getAttribute(WBEMServiceAdvertisement.COMM_MECHANISM), data[2]);
						verify(WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC, EQUAL, advertisement
								.getAttribute(WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC),
								data[3]);
						verify(WBEMServiceAdvertisement.INTEROP_NS, EQUAL, advertisement
								.getAttribute(WBEMServiceAdvertisement.INTEROP_NS), data[4]);
						continue outer;
					}
				}
				fail("Invalid service url: " + advertisement.getServiceUrl());
			}
		} finally {
			shutdownSlpSa();
		}
	}

	private boolean registerFakeData() throws ServiceLocationException {
		Advertiser advertiser = ServiceLocationManager.getAdvertiser(Locale.US);
		Iterator<String[]> iter = cTestData.iterator();
		while (iter.hasNext()) {
			String[] data = iter.next();
			Vector<ServiceLocationAttribute> attributes = new Vector<ServiceLocationAttribute>();
			attributes.add(new ServiceLocationAttribute("(" + WBEMServiceAdvertisement.SERVICE_ID
					+ "=" + data[1] + ")"));
			attributes.add(new ServiceLocationAttribute("("
					+ WBEMServiceAdvertisement.COMM_MECHANISM + "=" + data[2] + ")"));
			if (data[3] != null) {
				attributes.add(new ServiceLocationAttribute("("
						+ WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC + "=" + data[3] + ")"));
			}
			attributes.add(new ServiceLocationAttribute("(" + WBEMServiceAdvertisement.INTEROP_NS
					+ "=" + data[4] + ")"));
			try {
				advertiser.register(new ServiceURL(SERVICE_WBEM + data[0], 60), attributes);
			} catch (ServiceLocationException e) {
				if (e.getCause() instanceof IOException) {
					// no slpd on system
					warning("Couldn't connect to SLP SA ... skipping test");
					return false;
				}
				throw e;
			}
		}
		return true;
	}

	/**
	 * Starts a SLP Service Agent (SA) on the loopback address (127.0.0.1)
	 */
	private boolean startSlpSa() throws IOException {
		try {

			SLPConfig.getGlobalCfg().setPort(PORT);
			this.iSrvAgent = new ServiceAgent();
			this.iSrvAgent.start();
		} catch (IOException e) {
			if (e.getCause() instanceof BindException) {
				warning("Couldn't bind to SLP port " + PORT + " ... skipping SLP tests");
				return false;
			}
			throw e;
		}
		return true;
	}

	/**
	 * Shuts down the SLP SA
	 */
	private void shutdownSlpSa() {
		if (this.iSrvAgent != null) this.iSrvAgent.stop();
	}
}
