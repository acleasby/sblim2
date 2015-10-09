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
 * 1832635    2007-11-15  ebak         less strict parsing for IPv6 hostnames
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 */

package org.sblim.cimclient.unittest.util;

import org.sblim.cimclient.internal.util.XMLHostStr;
import org.sblim.cimclient.unittest.TestCase;

/**
 * XMLHostStrTest
 */
public class XMLHostStrTest extends TestCase {

	private static final String[] PROTOCOLS = { null, "https" };

	private static final String[] HOSTS = { "wbem.org", "172.20.8.1", "[5f9a:124a::0001]", "[::]",
			"[::5f9a]", "[::5f9a:124a]", "[35e8::5f9a:124a]", "[35e8:0032::5f9a:124a]",
			"[5f9a:124a::]", "[5f9a::]", "[35e8:0032:5f9a:124a:35e8:0032:5f9a:124a]" };

	private static final String[] PORTS = { null, "5988" };

	private static final String[] IPV6_LITERALS = { "::", "::5f9a", "::5f9a:124a",
			"35e8::5f9a:124a", "35e8:0032::5f9a:124a", "5f9a:124a::", "5f9a::",
			"35e8:0032:5f9a:124a:35e8:0032:5f9a:124a" };

	private XMLHostStr xmlHostStr = new XMLHostStr();

	private static boolean equals(String pS0, String pS1) {
		return pS0 == null ? pS1 == null : pS0.equals(pS1);
	}

	private void checkURL(String pProtocol, String pHost, String pPort) {
		StringBuffer buf = new StringBuffer();
		if (pProtocol != null) buf.append(pProtocol + "://");
		buf.append(pHost);
		if (pPort != null) buf.append(":" + pPort);
		String hostStr = buf.toString();
		this.xmlHostStr.set(hostStr);
		String msg = "Expected: protocol:" + pProtocol + ", host:" + pHost + ", port:" + pPort
				+ "Produced: " + this.xmlHostStr.toString();
		verify(msg, equals(pProtocol, this.xmlHostStr.getProtocol()));
		verify(msg, equals(pHost, this.xmlHostStr.getHost()));
		verify(msg, equals(pPort, this.xmlHostStr.getPort()));

	}

	/**
	 * testURLs
	 */
	public void testURLs() {
		for (int prIdx = 0; prIdx < PROTOCOLS.length; prIdx++) {
			String protocol = PROTOCOLS[prIdx];
			for (int hostIdx = 0; hostIdx < HOSTS.length; hostIdx++) {
				String host = HOSTS[hostIdx];
				for (int portIdx = 0; portIdx < PORTS.length; portIdx++) {
					String port = PORTS[portIdx];
					checkURL(protocol, host, port);
				}
			}
		}
	}

	/**
	 * testIPv6Literals
	 */
	public void testIPv6Literals() {
		for (int i = 0; i < IPV6_LITERALS.length; i++)
			checkURL(null, IPV6_LITERALS[i], null);
	}

}
