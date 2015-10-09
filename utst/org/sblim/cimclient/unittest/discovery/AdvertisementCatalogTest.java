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
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2) 
 */

package org.sblim.cimclient.unittest.discovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sblim.cimclient.discovery.AdvertisementCatalog;
import org.sblim.cimclient.discovery.WBEMProtocol;
import org.sblim.cimclient.discovery.WBEMServiceAdvertisement;
import org.sblim.cimclient.internal.discovery.slp.WBEMServiceAdvertisementSLP;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.ServiceURL;

/**
 * Class AdvertisementCatalogTest is responsible for testing the class
 * AdvertisementCatalog
 * 
 */
public class AdvertisementCatalogTest extends TestCase {

	static private List<String[]> cTestData = new LinkedList<String[]>();

	static {
		{
			String[] data = new String[] { "11.11.11.11", "https://55.66.77.99:5989", "IBM0815",
					"IBM Test Instrumentation 2", "CIM-XML", null, "root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "22.22.22.22", "https://55.66.77.19:5989", "IBM1111",
					"IBM Test Instrumentation 3", "CIM-XML", null, "root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:4711", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null, "root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "https://55.66.77.88:4712", "IBM4711",
					"IBM Test Instrumentation", "cim-XML", null, "root/ibm" };
			cTestData.add(data);
		}
		{
			String[] data = new String[] { "11.22.33.44", "http://55.66.77.88:8888", "IBM4711",
					"IBM Test Instrumentation", "other", "ws-man", "root/ibm" };
			cTestData.add(data);
		}
	}

	protected List<Object[]> iEvents = new LinkedList<Object[]>();

	/**
	 * Tests addAdvertisements(), getKnownIds() and getAdvertisementsById()
	 */
	public void testAddAndGet() {
		WBEMServiceAdvertisement[] advertisements = new WBEMServiceAdvertisement[cTestData.size()];
		Iterator<String[]> iterStr = cTestData.iterator();
		int i = 0;
		while (iterStr.hasNext()) {
			String[] data = iterStr.next();
			advertisements[i++] = createAdvertisement(data);
		}

		AdvertisementCatalog catalog = new AdvertisementCatalog();
		catalog.addAdvertisements(advertisements);

		String[] ids = catalog.getKnownIds();
		verify("Number of service ids", EQUAL, new Integer(ids.length), new Integer(3));
		outer: for (i = 0; i < ids.length; ++i) {
			String id = ids[i];
			for (int k = 0; k < 3; ++k) {
				if (id.equals(advertisements[k].getAttribute(WBEMServiceAdvertisement.SERVICE_ID))) {
					continue outer;
				}
			}
			fail("Invalid service-id: " + id);
		}
		Iterator<WBEMServiceAdvertisement> iter;
		List<WBEMServiceAdvertisement> allAdvertisements = new ArrayList<WBEMServiceAdvertisement>(
				Arrays.asList(advertisements));
		for (i = 0; i < ids.length; ++i) {
			String id = ids[i];
			WBEMServiceAdvertisement[] cat_advs = catalog.getAdvertisementsById(id);
			for (int m = 0; m < cat_advs.length; ++m) {
				WBEMServiceAdvertisement cat_adv = cat_advs[m];
				verify("service id", EQUAL, cat_adv
						.getAttribute(WBEMServiceAdvertisement.SERVICE_ID), id);
				verify("Valid advertisement", allAdvertisements.contains(cat_adv));
				iter = allAdvertisements.iterator();
				while (iter.hasNext()) {
					if (iter.next().equals(cat_adv)) {
						iter.remove();
					}
				}
			}
		}
		verify("All advertisement hit", allAdvertisements.size() == 0);
	}

	/**
	 * createAdvertisement
	 * 
	 * @param data
	 */
	private WBEMServiceAdvertisement createAdvertisement(String[] data) {
		List<String> attributes = new ArrayList<String>();
		attributes.add("(" + WBEMServiceAdvertisement.SERVICE_ID + "=" + data[2] + ")");
		attributes.add("(" + WBEMServiceAdvertisement.SERVICE_HI_NAME + "=" + data[3] + ")");
		attributes.add("(" + WBEMServiceAdvertisement.COMM_MECHANISM + "=" + data[4] + ")");
		if (data[5] != null) {
			attributes.add("(" + WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC + "=" + data[5]
					+ ")");
		}
		attributes.add("(" + WBEMServiceAdvertisement.INTEROP_NS + "=" + data[6] + ")");

		return new WBEMServiceAdvertisementSLP(data[0], new ServiceURL("service:wbem:" + data[1],
				ServiceURL.LIFETIME_DEFAULT), attributes);
	}

	/**
	 * Tests the selection algorithm of getAdvertisement()
	 * 
	 */
	public void testSelection() {
		WBEMServiceAdvertisement[] advertisements = new WBEMServiceAdvertisement[cTestData.size()];
		Iterator<String[]> iter = cTestData.iterator();
		int i = 0;
		while (iter.hasNext()) {
			String[] data = iter.next();
			advertisements[i++] = createAdvertisement(data);

		}

		AdvertisementCatalog catalog = new AdvertisementCatalog();
		catalog.addAdvertisements(advertisements);

		String id = cTestData.get(3)[2];
		{
			WBEMServiceAdvertisement adv = catalog.getAdvertisement(id, new WBEMProtocol[] {
					new WBEMProtocol("hTTps", "CIM-bin"), new WBEMProtocol("hTTps", "CIM-xml"),
					new WBEMProtocol("hTTp", "CIM-xml") });
			verify("service id", EQUAL, adv.getAttribute(WBEMServiceAdvertisement.SERVICE_ID), id);
			verify("transport", EQUAL, adv.getServiceUrl().split(":", 2)[0].toLowerCase(), "https");
			verify("communication", EQUAL, adv
					.getAttribute(WBEMServiceAdvertisement.COMM_MECHANISM).toLowerCase(), "cim-xml");
		}
		{
			WBEMServiceAdvertisement adv = catalog.getAdvertisement(id, new WBEMProtocol[] {
					new WBEMProtocol("hTTps", "CIM-bin"), new WBEMProtocol("hTTp", "CIM-xml"),
					new WBEMProtocol("hTTps", "CIM-xml") });
			verify("service id", EQUAL, adv.getAttribute(WBEMServiceAdvertisement.SERVICE_ID), id);
			verify("transport", EQUAL, adv.getServiceUrl().split(":", 2)[0].toLowerCase(), "http");
			verify("communication", EQUAL, adv
					.getAttribute(WBEMServiceAdvertisement.COMM_MECHANISM).toLowerCase(), "cim-xml");
		}
		{
			WBEMServiceAdvertisement adv = catalog.getAdvertisement(id, new WBEMProtocol[] {
					new WBEMProtocol("hTTp", "ws-mAN"), new WBEMProtocol("hTTp", "CIM-xml"),
					new WBEMProtocol("hTTps", "CIM-xml") });
			verify("service id", EQUAL, adv.getAttribute(WBEMServiceAdvertisement.SERVICE_ID), id);
			verify("transport", EQUAL, adv.getServiceUrl().split(":", 2)[0].toLowerCase(), "http");
			verify("communication", EQUAL, adv.getAttribute(
					WBEMServiceAdvertisement.OTHER_COMM_MECHN_DESC).toLowerCase(), "ws-man");
		}
		{
			WBEMServiceAdvertisement adv = catalog.getAdvertisement(id, new WBEMProtocol[] {
					new WBEMProtocol("hTTps", "ws-mAN"), new WBEMProtocol("hTTp", "CIM-txt"),
					new WBEMProtocol("hTTps", "CIM-bin") });
			verify("Advertisment==null", adv == null);
		}
	}

	/**
	 * Tests refreshAdvertisements()
	 * 
	 */
	@SuppressWarnings("null")
	public void testRefresh() {
		WBEMServiceAdvertisement[] advertisements = new WBEMServiceAdvertisement[cTestData.size()];
		Iterator<String[]> iter = cTestData.iterator();
		int i = 0;
		while (iter.hasNext()) {
			String[] data = iter.next();
			advertisements[i++] = createAdvertisement(data);

		}

		AdvertisementCatalog catalog = new AdvertisementCatalog();
		catalog.addAdvertisements(advertisements);

		String[] ids = catalog.getKnownIds();
		verify("Number of service ids", EQUAL, new Integer(ids.length), new Integer(3));
		outer: for (i = 0; i < ids.length; ++i) {
			String id = ids[i];
			for (int k = 0; k < 3; ++k) {
				if (id.equals(advertisements[k].getAttribute(WBEMServiceAdvertisement.SERVICE_ID))) {
					continue outer;
				}
			}
			fail("Invalid service-id: " + id);
		}

		WBEMServiceAdvertisement drop = advertisements[0];
		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] {});
		WBEMServiceAdvertisement[] cat_advs = catalog.getAdvertisementsById(drop
				.getAttribute(WBEMServiceAdvertisement.SERVICE_ID));
		verify("Advertisement found", cat_advs != null && cat_advs.length == 1);
		verify("Advertisement expired", cat_advs[0].isExpired());
		verify("Number of service ids", EQUAL, new Integer(catalog.getKnownIds().length),
				new Integer(3));

		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] { drop });
		cat_advs = catalog.getAdvertisementsById(drop
				.getAttribute(WBEMServiceAdvertisement.SERVICE_ID));
		verify("Advertisement found", cat_advs != null && cat_advs.length == 1);
		verify("Advertisement not expired", !cat_advs[0].isExpired());
		verify("Number of service ids", EQUAL, new Integer(catalog.getKnownIds().length),
				new Integer(3));

		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] { drop });
		cat_advs = catalog.getAdvertisementsById(drop
				.getAttribute(WBEMServiceAdvertisement.SERVICE_ID));
		verify("Advertisement refreshed (no duplicates)", cat_advs != null && cat_advs.length == 1);
		verify("Advertisement not expired", !cat_advs[0].isExpired());
		verify("Number of service ids", EQUAL, new Integer(catalog.getKnownIds().length),
				new Integer(3));

		catalog.refreshAdvertisements(new String[] { "47.11.8.15", drop.getDirectory() },
				new WBEMServiceAdvertisement[] { drop });
		verify("Number of service ids", EQUAL, new Integer(catalog.getKnownIds().length),
				new Integer(3));

	}

	/**
	 * Tests listener registration, deregistration and events
	 */
	public void testListeners() {
		WBEMServiceAdvertisement[] advertisements = new WBEMServiceAdvertisement[cTestData.size()];
		Iterator<String[]> iterStr = cTestData.iterator();
		int i = 0;
		while (iterStr.hasNext()) {
			String[] data = iterStr.next();
			advertisements[i++] = createAdvertisement(data);
		}

		final AdvertisementCatalog catalog = new AdvertisementCatalog();
		final AdvertisementCatalog.EventListener listener = new AdvertisementCatalog.EventListener() {

			public void advertisementEvent(int pEvent, WBEMServiceAdvertisement pAdvertisment) {
				AdvertisementCatalogTest.this.iEvents.add(new Object[] { new Integer(pEvent),
						pAdvertisment });
			}
		};
		catalog.addEventListener(listener);

		this.iEvents.clear();

		catalog.addAdvertisements(advertisements);
		verify("Number of events", EQUAL, new Integer(this.iEvents.size()), new Integer(
				advertisements.length));
		List<WBEMServiceAdvertisement> advertisementList = new LinkedList<WBEMServiceAdvertisement>(
				Arrays.asList(advertisements));
		Iterator<Object[]> iter = this.iEvents.iterator();
		while (iter.hasNext()) {
			Object[] event = iter.next();
			verify("Event type", EQUAL, event[0], new Integer(AdvertisementCatalog.EVENT_ADD));
			verify("Event advertisement", advertisementList.contains(event[1]));
			advertisementList.remove(event[1]);
		}

		this.iEvents.clear();

		WBEMServiceAdvertisement drop = advertisements[0];
		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] {});
		verify("Number of events", EQUAL, new Integer(this.iEvents.size()), new Integer(1));
		iter = this.iEvents.iterator();
		while (iter.hasNext()) {
			Object[] event = iter.next();
			verify("Event type", EQUAL, event[0], new Integer(AdvertisementCatalog.EVENT_EXPIRE));
			verify("Event advertisement", EQUAL, event[1], drop);
		}

		this.iEvents.clear();

		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] { drop });
		verify("Number of events", EQUAL, new Integer(this.iEvents.size()), new Integer(1));
		iter = this.iEvents.iterator();
		while (iter.hasNext()) {
			Object[] event = iter.next();
			verify("Event type", EQUAL, event[0], new Integer(AdvertisementCatalog.EVENT_RENEW));
			verify("Event advertisement", EQUAL, event[1], drop);
		}

		this.iEvents.clear();

		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] { drop });
		verify("Number of events", EQUAL, new Integer(this.iEvents.size()), new Integer(0));

		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] {});
		this.iEvents.clear();

		catalog.removeExpired(drop.getDirectory());
		verify("Number of events", EQUAL, new Integer(this.iEvents.size()), new Integer(1));
		iter = this.iEvents.iterator();
		while (iter.hasNext()) {
			Object[] event = iter.next();
			verify("Event type", EQUAL, event[0], new Integer(AdvertisementCatalog.EVENT_REMOVE));
			verify("Event advertisement", EQUAL, event[1], drop);
		}

		this.iEvents.clear();

		catalog.removeEventListener(listener);
		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] { drop });
		catalog.refreshAdvertisements(new String[] { drop.getDirectory() },
				new WBEMServiceAdvertisement[] {});
		verify("Number of events", EQUAL, new Integer(this.iEvents.size()), new Integer(0));
	}
}
