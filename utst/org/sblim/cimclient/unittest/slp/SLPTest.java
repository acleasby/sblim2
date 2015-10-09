/**
 * (C) Copyright IBM Corp. 2007, 2010
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Endre Bak, IBM, ebak@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1804402    2007-09-28  ebak         IPv6 ready SLP
 * 1892103    2008-02-15  ebak         SLP improvements
 * 1949918    2008-04-08  raman_arora  Malformed service URL crashes SLP discovery
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 * 3019214    2010-06-21  blaschke-oss SLP equals methods assume too much
 */

package org.sblim.cimclient.unittest.slp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;

import org.sblim.cimclient.unittest.GenericUTestExts;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.Advertiser;
import org.sblim.slp.Locator;
import org.sblim.slp.ServiceLocationAttribute;
import org.sblim.slp.ServiceLocationException;
import org.sblim.slp.ServiceLocationManager;
import org.sblim.slp.ServiceType;
import org.sblim.slp.ServiceURL;
import org.sblim.slp.internal.SLPConfig;
import org.sblim.slp.internal.TRC;
import org.sblim.slp.internal.sa.ServiceAgent;
import org.sblim.slp.internal.ua.SLEnumerationImpl;

/**
 * SLPTest
 * 
 */
public class SLPTest extends TestCase {

	/*
	 * Note: advertiser doesn't provide facility for scoped registrations so
	 * "default" is used always.
	 */

	private static class SrvEntry {

		private ServiceURL iSrvURL;

		private List<ServiceLocationAttribute> iAttribs;

		/**
		 * Ctor.
		 * 
		 * @param pSrvURL
		 * @param pAttribs
		 */
		public SrvEntry(String pSrvURL, ServiceLocationAttribute[] pAttribs) {
			this(new ServiceURL(pSrvURL, ServiceURL.LIFETIME_DEFAULT), pAttribs == null ? null
					: Arrays.asList(pAttribs));
		}

		/**
		 * Ctor.
		 * 
		 * @param pSrvURL
		 * @param pAttribs
		 */
		public SrvEntry(ServiceURL pSrvURL, List<ServiceLocationAttribute> pAttribs) {
			this.iSrvURL = pSrvURL;
			this.iAttribs = pAttribs;
		}

		/**
		 * getSrvURL
		 * 
		 * @return ServiceURL
		 */
		public ServiceURL getSrvURL() {
			return this.iSrvURL;
		}

		/**
		 * getAttribs
		 * 
		 * @return List
		 */
		public List<ServiceLocationAttribute> getAttribs() {
			return this.iAttribs;
		}

		/**
		 * register
		 * 
		 * @param pAdv
		 * @throws ServiceLocationException
		 */
		public void register(Advertiser pAdv) throws ServiceLocationException {
			pAdv.register(this.iSrvURL, this.iAttribs == null ? null
					: new Vector<ServiceLocationAttribute>(this.iAttribs));
		}

		/**
		 * deregister
		 * 
		 * @param pAdv
		 * @throws ServiceLocationException
		 */
		public void deregister(Advertiser pAdv) throws ServiceLocationException {
			pAdv.deregister(this.iSrvURL);
		}

		@Override
		public boolean equals(Object pObj) {
			if (pObj == null || !(pObj instanceof SrvEntry)) return false;
			SrvEntry that = (SrvEntry) pObj;
			return this.iSrvURL.equals(that.iSrvURL) && equalLists(this.iAttribs, that.iAttribs);
		}

	}

	private static final ServiceLocationAttribute[] ATTRIBS = new ServiceLocationAttribute[] {
			mkAttr("Description", new Object[] { "Lofasz", new byte[] { 0, 1, 2, 3 } }),
			mkAttr("Description", new Object[] { "ssh server", new Integer(10), Boolean.FALSE }),
			mkAttr("Description", new Object[] { "Nem lofasz" }) };

	private static final SrvEntry[] WBEM_SRVS = new SrvEntry[] {
			new SrvEntry("service:wbem://thishost.com:345",
					new ServiceLocationAttribute[] { ATTRIBS[2] }),
			new SrvEntry("service:wbem://thathost.com:3395",
					new ServiceLocationAttribute[] { ATTRIBS[0] }) };

	private static final SrvEntry[] SSH_SRVS = new SrvEntry[] {
			new SrvEntry("service:ssh://host.a.org:22", null),
			new SrvEntry("service:ssh://host.b.org:22",
					new ServiceLocationAttribute[] { ATTRIBS[1] }) };

	private static final ServiceType[] SRV_TYPES = new ServiceType[] {
			new ServiceType("service:wbem"), new ServiceType("service:ssh") };

	static boolean equalLists(List<?> pList0, List<?> pList1) {
		if (pList0 == null) return pList1 == null;
		if (pList1 == null) return false;
		if (pList0.size() != pList1.size()) return false;
		Iterator<?> itr0 = pList0.iterator();
		while (itr0.hasNext())
			if (!pList1.contains(itr0.next())) return false;
		return true;
	}

	private static ServiceLocationAttribute mkAttr(String pID, Object[] pValues) {
		return new ServiceLocationAttribute(pID, GenericUTestExts.mkVec(pValues));
	}

	private static List<ServiceURL> getURLs(SrvEntry[] pEntries) {
		List<ServiceURL> list = new ArrayList<ServiceURL>();
		for (int i = 0; i < pEntries.length; i++)
			list.add(pEntries[i].getSrvURL());
		return list;
	}

	private void checkEnum(Enumeration<?> pEnum, Object[] pRefObjs) {
		checkEnum(pEnum, Arrays.asList(pRefObjs));
	}

	private void checkEnum(Enumeration<?> pEnum, List<?> pRefList) {
		List<Object> resList = new ArrayList<Object>();
		while (pEnum.hasMoreElements())
			resList.add(pEnum.nextElement());
		verify("result count:" + resList.size() + " != reference count:" + pRefList.size()
				+ "\nresultList:" + resList, resList.size() == pRefList.size());
		Iterator<Object> resItr = resList.iterator();
		Iterator<?> refItr = pRefList.iterator();
		while (resItr.hasNext()) {
			Object resObj = resItr.next();
			verify("pRefList doesn't contain " + resObj, pRefList.contains(resObj));
		}
		while (refItr.hasNext()) {
			Object refObj = refItr.next();
			verify("resList doesn't contain " + refObj, resList.contains(refObj));
		}
	}

	private void checkSrvRequest(String pSrvType, SrvEntry[] pRefEntries)
			throws ServiceLocationException {
		debug("srvType:" + pSrvType);
		List<ServiceURL> srvList = getURLs(pRefEntries);
		Locator locator = ServiceLocationManager.getLocator(Locale.US);
		Enumeration<?> srvEnum = locator.findServices(new ServiceType(pSrvType), null, null);
		checkEnum(srvEnum, srvList);
	}

	private void checkSrvTypeRequest() throws ServiceLocationException {
		Locator locator = ServiceLocationManager.getLocator(Locale.US);
		Enumeration<?> srvTypeEnum = locator.findServiceTypes("", null);
		checkEnum(srvTypeEnum, SRV_TYPES);
	}

	private Vector<String> mkAttrIDVec(ServiceLocationAttribute[] pAttribs) {
		if (pAttribs == null) return null;
		Vector<String> vec = new Vector<String>();
		for (int i = 0; i < pAttribs.length; i++)
			vec.add(pAttribs[i].getId());
		return vec;
	}

	private void checkAttribRequest(String pURLStr, ServiceLocationAttribute[] pAttribs)
			throws ServiceLocationException {
		Locator locator = ServiceLocationManager.getLocator(Locale.US);
		Enumeration<?> attrEnum = locator.findAttributes(new ServiceURL(pURLStr,
				ServiceURL.LIFETIME_DEFAULT), null, mkAttrIDVec(pAttribs));
		checkEnum(attrEnum, pAttribs);
	}

	private void checkAttribRequest(ServiceType pSrvType, ServiceLocationAttribute[] pAttribs)
			throws ServiceLocationException {
		Locator locator = ServiceLocationManager.getLocator(Locale.US);
		Enumeration<?> attrEnum = locator.findAttributes(pSrvType, null, mkAttrIDVec(pAttribs));
		checkEnum(attrEnum, pAttribs);
	}

	private ServiceAgent iAgent;

	private static final int PORT = 11223;

	private void init() throws IOException {
		if (this.iAgent != null) return;
		TRC.setLevel(Level.SEVERE);
		TRC.setPatterns(new String[] {
		// "^.+$"
				}, new String[] { "^.*\\.SLPTest\\..+$", "^.*\\.MsgHeader\\..+$"
				// "^.*\\.Requester\\..+$",
				});
		TRC.debug("test");
		SLPConfig.getGlobalCfg().setPort(PORT);
		// SLPConfig.getGlobalCfg().setUseIPv4(false); // Windows won't like it
		// SLPConfig.getGlobalCfg().setUseIPv6(false);
		this.iAgent = new ServiceAgent();
		this.iAgent.start();
	}

	/**
	 * testReqistration
	 * 
	 * @throws Exception
	 */
	public void testReqistration() throws Exception {
		try {
			init();
			this.iAgent.setSkipFirstRequest(true);
			Advertiser adv = ServiceLocationManager.getAdvertiser(Locale.US);
			// registration
			for (int i = 0; i < WBEM_SRVS.length; i++)
				WBEM_SRVS[i].register(adv);
			for (int i = 0; i < SSH_SRVS.length; i++)
				SSH_SRVS[i].register(adv);
		} catch (Exception e) {
			this.iAgent.stop();
			throw e;
		} finally {
			this.iAgent.setSkipFirstRequest(false);
		}
	}

	/**
	 * testSrvRequest
	 * 
	 * @throws Exception
	 */
	public void testSrvRequest() throws Exception {
		try {
			init();
			checkSrvRequest("service:wbem", WBEM_SRVS);
			checkSrvRequest("service:ssh", SSH_SRVS);
		} catch (Exception e) {
			this.iAgent.stop();
			throw e;
		}
	}

	/**
	 * testDeregistration
	 * 
	 * @throws Exception
	 */
	public void testDeregistration() throws Exception {
		try {
			init();
			Advertiser adv = ServiceLocationManager.getAdvertiser(Locale.US);
			SSH_SRVS[0].deregister(adv);
			checkSrvRequest("service:ssh", new SrvEntry[] { SSH_SRVS[1] });
		} catch (Exception e) {
			this.iAgent.stop();
			throw e;
		}
	}

	/**
	 * testSrvTypeRequest
	 * 
	 * @throws Exception
	 */
	public void testSrvTypeRequest() throws Exception {
		try {
			init();
			checkSrvTypeRequest();
		} catch (Exception e) {
			this.iAgent.stop();
			throw e;
		}
	}

	/**
	 * testAttrRequest
	 * 
	 * @throws Exception
	 */
	public void testAttrRequest() throws Exception {
		try {
			init();
			checkAttribRequest("service:wbem://thathost.com:3395",
					new ServiceLocationAttribute[] { ATTRIBS[0] });
			checkAttribRequest(new ServiceType("service:wbem"), new ServiceLocationAttribute[] {
					ATTRIBS[0], ATTRIBS[2] });
		} catch (Exception e) {
			this.iAgent.stop();
			throw e;
		}
	}

	/**
	 * testOverflowHandling
	 * 
	 * @throws Exception
	 */
	public void testOverflowHandling() throws Exception {
		try {
			init();
			Advertiser adv = ServiceLocationManager.getAdvertiser(Locale.US);
			SrvEntry[] entries = new SrvEntry[128];
			for (int i = 0; i < entries.length; i++) {
				entries[i] = new SrvEntry("service:https://dummy." + i + ".org:443",
						new ServiceLocationAttribute[] { mkAttr("ID",
								new Object[] { new Integer(i) }) });
				entries[i].register(adv);
			}
			checkSrvRequest("service:https", entries);
		} catch (Exception e) {
			this.iAgent.stop();
			throw e;
		}
	}

	/**
	 * testExceptionHandling
	 * 
	 * @throws Exception
	 */
	public void testExceptionHandling() throws Exception {
		Locator locator = ServiceLocationManager.getLocator(Locale.US);
		Enumeration<?> srvEnum = locator.findServices(new ServiceType("service:wbem"), null, null);
		// WBEM_SRVS.length number of services should be read
		for (int i = 0; i < WBEM_SRVS.length; i++) {
			verify("One more element is expected", srvEnum.hasMoreElements());
			Object obj = srvEnum.nextElement();
			String typeStr = obj == null ? "null" : obj.getClass().getName();
			verify("obj is " + typeStr, obj instanceof ServiceURL);
		}
		/*
		 * Now let's send a garbage packet to the UA, as a result nextElement()
		 * should not throw a RuntimeException with a parsing error cause.
		 * Parsing error saves exception into ExceptionTable
		 */
		DatagramSocket socket = new DatagramSocket();
		byte[] reqBytes = new byte[] { (byte) 0xff, 0, 0 };
		int port = ((SLEnumerationImpl) srvEnum).getPort();
		DatagramPacket outPacket = new DatagramPacket(reqBytes, reqBytes.length, InetAddress
				.getByName("127.0.0.1"), port);
		socket.send(outPacket);

		verify("One more element is expected", !srvEnum.hasMoreElements());
		try {
			srvEnum.nextElement();
			verify("Exception was not thrown!", false);
		} catch (Exception e) {
			String cause = e.getCause() == null ? "null" : e.getCause().toString();
			verify("Wrong cause: " + cause, e instanceof NoSuchElementException);
		}
		verify("Unexpected element.", !srvEnum.hasMoreElements());
	}

	/**
	 * testStop
	 */
	public void testStop() {
		if (this.iAgent != null) this.iAgent.stop();
	}

	/**
	 * @param pMsg
	 */
	private static void debug(String pMsg) {
	// System.out.println("DEBUG:"+pMsg);
	}

}
