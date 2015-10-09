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
 * 1804402    2007-11-10  ebak         IPv6 ready SLP - revision 4
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 */

package org.sblim.cimclient.unittest.slp;

import org.sblim.cimclient.unittest.TestCase;
import org.sblim.slp.ServiceURL;
import org.sblim.slp.internal.SLPDefaults;

/**
 * Class ServiceURLTest is for testing the correctness of ServiceURL string
 * parsing.
 * 
 */
public class ServiceURLTest extends TestCase {

	private static final String[] SRV_TYPES = { "http", "service:wbem", "service:wbem:cimom",
			SLPDefaults.SA_SERVICE_TYPE.toString(), SLPDefaults.DA_SERVICE_TYPE.toString() };

	private static final String[] HOSTS = { "blue.com", "192.34.3.12", "[12ab:34bc::12]" };

	private static final String[] PORTS = { null, "5988" };

	private void check(String pTypeStr, String pHostStr, String pPortStr) {
		StringBuffer buf = new StringBuffer(pHostStr);
		if (pPortStr != null) buf.append(':' + pPortStr);
		String urlPath = buf.toString();
		String srvURLStr = pTypeStr + "://" + urlPath;
		String msg = "\n[typeStr=" + pTypeStr + ", hostStr=" + pHostStr + ", portStr=" + pPortStr
				+ ", urlStr=" + srvURLStr + "]";
		ServiceURL srvURL = new ServiceURL(srvURLStr, ServiceURL.LIFETIME_DEFAULT);
		String toStr = srvURL.toString();
		verify("Wrong toString()=" + toStr + msg, srvURLStr.equals(srvURL.toString()));
		String typeStr = srvURL.getServiceType().toString();
		verify("Wrong getServiceType()=" + typeStr + msg, pTypeStr.equals(typeStr));
		String hostStr = srvURL.getHost();
		verify("Wrong getHost()=" + hostStr + msg, pHostStr.equals(hostStr));
		int port = srvURL.getPort();
		int refPort = pPortStr == null ? 0 : Integer.parseInt(pPortStr);
		verify("Wrong getPort()=" + port + msg, refPort == port);
		String urlPathStr = srvURL.getURLPath();
		verify("Wrong getURLPath()=" + urlPathStr + msg, urlPath.equals(urlPathStr));
	}

	/**
	 * test
	 */
	public void test() {
		for (int typeIdx = 0; typeIdx < SRV_TYPES.length; typeIdx++) {
			String typeStr = SRV_TYPES[typeIdx];
			for (int hostIdx = 0; hostIdx < HOSTS.length; hostIdx++) {
				String hostStr = HOSTS[hostIdx];
				for (int portIdx = 0; portIdx < PORTS.length; portIdx++) {
					String portStr = PORTS[portIdx];
					check(typeStr, hostStr, portStr);
				}
			}
		}
	}

}
