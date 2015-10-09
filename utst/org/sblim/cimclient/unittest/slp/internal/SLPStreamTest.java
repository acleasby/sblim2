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
 * @author : Endre Bak, IBM, ebak@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1804402    2007-09-28  ebak         IPv6 ready SLP
 * 1892103    2008-02-12  ebak         SLP improvements
 * 1949918    2008-04-08  raman_arora  Malformed service URL crashes SLP discovery
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2797696    2009-05-27  raman_arora  Input files use unsafe operations
 * 2823494    2009-08-03  rgummada     Change Boolean constructor to static
 */

package org.sblim.cimclient.unittest.slp.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sblim.cimclient.unittest.GenericUTestExts;
import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.ServiceLocationAttribute;
import org.sblim.slp.ServiceType;
import org.sblim.slp.ServiceURL;
import org.sblim.slp.internal.AttributeHandler;
import org.sblim.slp.internal.SLPDefaults;
import org.sblim.slp.internal.msg.SLPInputStream;
import org.sblim.slp.internal.msg.SLPOutputStream;

/**
 * SLPStreamTest
 * 
 */
public class SLPStreamTest extends TestCase {

	private static final int MAX_DATAGRAM_SIZE = SLPDefaults.MTU;

	private static SLPOutputStream getFullStream(int pFreeSpace) {
		SLPOutputStream outStr = new SLPOutputStream(MAX_DATAGRAM_SIZE);
		int numOfChars = MAX_DATAGRAM_SIZE - pFreeSpace - 2;
		char[] chars = new char[numOfChars];
		Arrays.fill(chars, 'a');
		outStr.write(new String(chars));
		return outStr;
	}

	private static byte[] getHead(byte[] pBytes, int pLength) {
		byte[] res = new byte[pLength];
		System.arraycopy(pBytes, 0, res, 0, pLength);
		return res;
	}

	private static final ServiceType SRV_TYPE = new ServiceType("service:wbem:http");

	private static final List<ServiceType> SRV_TYPES = GenericUTestExts.mkList(new ServiceType[] {
			SRV_TYPE, new ServiceType("service:wbem:https"),
			new ServiceType("service:service-agent") });

	/**
	 * testServiceType
	 * 
	 * @throws Exception
	 */
	public void testServiceType() throws Exception {
		SLPOutputStream outStr = new SLPOutputStream();
		outStr.write(SRV_TYPE);
		SLPInputStream inStr = new SLPInputStream(outStr.toByteArray());
		ServiceType srvType = inStr.readServiceType();
		verify("SRV_TYPE != srvType\nSRV_TYPE: " + SRV_TYPE + "\nsrvType: " + srvType, SRV_TYPE
				.equals(srvType));
		SLPOutputStream fullOutStr = getFullStream(SRV_TYPE.toString().length() + 2);
		boolean res = fullOutStr.write(SRV_TYPE);
		verify("Failed to write SRV_TYPE into the stream!", res);
		fullOutStr = getFullStream(SRV_TYPE.toString().length());
		res = fullOutStr.write(SRV_TYPE);
		verify("SRV_TYPE is written to a stream which hasn't got enough free space!", !res);
	}

	/**
	 * testServiceTypeCut
	 * 
	 * @throws Exception
	 */
	public void testServiceTypeCut() throws Exception {
		SLPOutputStream outStr = new SLPOutputStream();
		outStr.write(SRV_TYPE);
		byte[] bytes = outStr.toByteArray();
		for (int ln = bytes.length - 1; ln >= 0; --ln) {
			SLPInputStream inStr = new SLPInputStream(getHead(bytes, ln));
			ServiceType srvType = inStr.readServiceType();
			verify("null srvType is expected", srvType == null);
		}
	}

	private static final ListHandler SRV_TYPE_LIST_HANDLER = new ListHandler() {

		public List<ServiceType> read(SLPInputStream pInStr) throws Exception {
			return pInStr.readServTypeList();
		}

		public int size(Object pObj) {
			SLPOutputStream outStr = new SLPOutputStream();
			outStr.write((ServiceType) pObj);
			return outStr.size();
		}

		public boolean write(SLPOutputStream pOutStr, List<?> pList) {
			return pOutStr.writeServTypeList(pList);
		}
	};

	/**
	 * testServiceTypeList
	 * 
	 * @throws Exception
	 */
	public void testServiceTypeList() throws Exception {
		listTest("ServiceType", SRV_TYPES, SRV_TYPE_LIST_HANDLER);
	}

	/**
	 * testServiceTypeListCut
	 * 
	 * @throws Exception
	 */
	public void testServiceTypeListCut() throws Exception {
		listCutTest("ServiceType", SRV_TYPES, SRV_TYPE_LIST_HANDLER, true);
	}

	private static final ServiceURL SRV_URL = new ServiceURL(
			"service:wbem:https://wbem4ever.org:5989", 10);

	private static final ArrayList<ServiceURL> SRV_URLS = GenericUTestExts.mkList(new ServiceURL[] {
			SRV_URL, new ServiceURL("service:ftp://bighost.org", 20),
			new ServiceURL("http://www.ibm.com", 15) });

	/**
	 * testServiceURL
	 * 
	 * @throws Exception
	 */
	public void testServiceURL() throws Exception {
		SLPOutputStream outStr = new SLPOutputStream();
		outStr.write(SRV_URL);
		SLPInputStream inStr = new SLPInputStream(outStr.toByteArray());
		ServiceURL srvURL = inStr.readURL();
		verify("srvURL != SRV_URL!\nsrvURL: " + srvURL + "\nSRV_URL:" + SRV_URL, SRV_URL
				.equals(srvURL));
		SLPOutputStream fullOutStr = getFullStream(SRV_URL.toString().length()
				+ SLPOutputStream.URL_HDR_LENGTH);
		boolean res = fullOutStr.write(SRV_URL);
		verify("Failed to write SRV_URL into the stream!", res);
		fullOutStr = getFullStream(SRV_URL.toString().length());
		res = fullOutStr.write(SRV_URL);
		verify("SRV_URL is written to a stream which hasn't got enough free space!", !res);
	}

	/**
	 * testServiceURLCut
	 * 
	 * @throws Exception
	 */
	public void testServiceURLCut() throws Exception {
		SLPOutputStream outStr = new SLPOutputStream();
		outStr.write(SRV_URL);
		byte[] bytes = outStr.toByteArray();
		for (int ln = bytes.length - 1; ln >= 0; --ln) {
			SLPInputStream inStr = new SLPInputStream(getHead(bytes, ln));
			ServiceURL srvURL = inStr.readURL();
			verify("null srvURL is expected", srvURL == null);
		}
	}

	private static final ListHandler SRV_URL_LIST_HANDLER = new ListHandler() {

		public List<ServiceURL> read(SLPInputStream pInStr) throws Exception {
			ArrayList<Exception> urlExceptions = new ArrayList<Exception>();
			return pInStr.readUrlList(urlExceptions);
		}

		public int size(Object pObj) {
			SLPOutputStream outStr = new SLPOutputStream();
			outStr.write((ServiceURL) pObj);
			return outStr.size();
		}

		public boolean write(SLPOutputStream pOutStr, List<?> pList) {
			return pOutStr.writeURLList(pList);
		}
	};

	/**
	 * testServiceURLList
	 * 
	 * @throws Exception
	 */
	public void testServiceURLList() throws Exception {
		listTest("URL", SRV_URLS, SRV_URL_LIST_HANDLER);
	}

	/**
	 * testServiceURLListCut
	 * 
	 * @throws Exception
	 */
	public void testServiceURLListCut() throws Exception {
		listCutTest("URL", SRV_URLS, SRV_URL_LIST_HANDLER, false);
	}

	private static final List<ServiceLocationAttribute> ATTRIBS = GenericUTestExts
			.mkList(new ServiceLocationAttribute[] {
					new ServiceLocationAttribute("Attrib0", GenericUTestExts.mkVec(new Object[] {
							"String (o) Value", Boolean.TRUE, Boolean.FALSE,
							new byte[] { 0x20, 0x15, 0x33, 0x5a }, new Integer(42) })),
					new ServiceLocationAttribute("Attrib1", null),
					new ServiceLocationAttribute("Attrib2", GenericUTestExts.mkVec(new Object[] {
							new byte[] { (byte) 0xff, (byte) 0xef, 0x5a }, "Hello*!" })),
					new ServiceLocationAttribute("Attrib3", GenericUTestExts
							.mkVec(new Object[] { "Just a single string.(,)" })) });

	private static final ListHandler ATTR_LIST_HANDLER = new ListHandler() {

		public List<ServiceLocationAttribute> read(SLPInputStream pInStr) throws Exception {
			return pInStr.readAttributeList();
		}

		public int size(Object pObj) {
			return AttributeHandler.buildString((ServiceLocationAttribute) pObj).length() + 1;
		}

		public boolean write(SLPOutputStream pOutStr, List<?> pList) {
			return pOutStr.writeAttributeList(pList);
		}
	};

	/**
	 * testAttributeList
	 * 
	 * @throws Exception
	 */
	public void testAttributeList() throws Exception {
		listTest("Attribute", ATTRIBS, ATTR_LIST_HANDLER);
	}

	/**
	 * testAttributeListCut
	 * 
	 * @throws Exception
	 */
	public void testAttributeListCut() throws Exception {
		listCutTest("Attribute", ATTRIBS, ATTR_LIST_HANDLER, true);
	}

	@SuppressWarnings("null")
	private static String dumpList(String pListName, List<?> pList) {
		StringBuffer buf = new StringBuffer(pListName + ":\n");
		int cnt = pList == null ? 0 : pList.size();
		for (int i = 0; i < cnt; i++)
			buf.append(pList.get(i).toString() + '\n');
		return buf.toString();
	}

	private void listTest(String pName, List<?> pRefList, ListHandler pHnd) throws Exception {
		SLPOutputStream outStr = new SLPOutputStream();
		pHnd.write(outStr, pRefList);
		SLPInputStream inStr = new SLPInputStream(outStr.toByteArray());
		List<?> list = pHnd.read(inStr);
		String listName = pName;
		String refListName = "ref " + listName;
		verify(refListName + " != " + listName + " !\n" + dumpList(refListName, pRefList) + '\n'
				+ dumpList(listName, list), pRefList.equals(list));
		int freeSpace = pHnd.size(pRefList.get(0)) + pHnd.size(pRefList.get(1)) + 2;
		SLPOutputStream fullOutStr = getFullStream(freeSpace);
		boolean res = pHnd.write(fullOutStr, pRefList);
		verify("Too much " + pName + " is written to a limited SLPOutputStream.", !res);
		inStr = new SLPInputStream(fullOutStr.toByteArray());
		inStr.readString(); // skip dummy data
		list = pHnd.read(inStr);
		verify("Expected " + pName + " cnt is 2 while " + list.size() + " was read!",
				list.size() == 2);
		for (int i = 0; i < 2; i++) {
			Object refEntry = pRefList.get(i), entry = list.get(i);
			verify(pName + "s at position " + i + " don't equal!", refEntry.equals(entry));
		}
	}

	@SuppressWarnings("null")
	private void listCutTest(String pName, List<?> pRefList, ListHandler pHnd, boolean pAllCut)
			throws Exception {
		SLPOutputStream outStr = new SLPOutputStream();
		pHnd.write(outStr, pRefList);
		byte[] bytes = outStr.toByteArray();
		int refSize = pRefList.size();
		for (int ln = bytes.length; ln >= 0; --ln) {
			SLPInputStream inStr = new SLPInputStream(getHead(bytes, ln));
			List<?> list = pHnd.read(inStr);
			int listSize = list == null ? 0 : list.size();
			int diff = refSize - listSize;
			if (diff != 0) {
				// the list size must be decreased by one
				verify("refSize: " + refSize + ", listSize: " + listSize, pAllCut ? diff == refSize
						: diff == 1);
				if (!pAllCut) refSize = listSize;
			}
			for (int i = 0; i < listSize; i++) {
				Object refEntry = pRefList.get(i), entry = list.get(i);
				verify(pName + "s at position " + i + " don't equal!", refEntry.equals(entry));
			}
		}
	}

	private static interface ListHandler {

		/**
		 * write
		 * 
		 * @param pOutStr
		 * @param pList
		 * @return boolean
		 */
		public boolean write(SLPOutputStream pOutStr, List<?> pList);

		/**
		 * read
		 * 
		 * @param pInStr
		 * @return List
		 * @throws Exception
		 */
		public List<?> read(SLPInputStream pInStr) throws Exception;

		/**
		 * size
		 * 
		 * @param pObj
		 * @return int
		 */
		public int size(Object pObj);

	}

}
