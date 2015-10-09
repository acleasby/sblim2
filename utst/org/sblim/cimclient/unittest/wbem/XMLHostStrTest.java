/**
 * (C) Copyright IBM Corp. 2006, 2012
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
 * 1742873    2007-06-25  ebak         IPv6 ready cim-client
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 */

package org.sblim.cimclient.unittest.wbem;

import javax.cim.CIMArgument;
import javax.cim.CIMDataType;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;

import org.sblim.cimclient.internal.util.XMLHostStr;
import org.sblim.cimclient.unittest.TestCase;

/**
 * XMLHostStrTest tests XMLHostStr.
 */
public class XMLHostStrTest extends TestCase {

	private static final String[] PROTOCOLS = { null, "http", "https", "cimxml.wbem" };

	private static final String[] HOSTS = { "geza@de.ibm.com", "172.20.8.1",
			"[23a:1:0B:f5a3:031c:de2a:129.42.60.212]", "[23a:1:0B:f5a3:031c:de2a:1e5f:55aa]",
			"[23a:1::129.42.60.212]", "[23a:1::1e5f:55aa]" };

	private static final String[] PORTS = { null, "5898" };

	private void check(String pProtocol, String pHost, String pPort) {
		StringBuffer buf = new StringBuffer();
		if (pProtocol != null) buf.append(pProtocol + "://");
		buf.append(pHost);
		if (pPort != null) buf.append(':' + pPort);
		String hostStr = buf.toString();
		XMLHostStr xmlHostStr = new XMLHostStr(hostStr);
		String protocol = xmlHostStr.getProtocol();
		verify("getProtocol() failed!", pProtocol == null ? protocol == null : pProtocol
				.equals(protocol));
		String host = xmlHostStr.getHost();
		verify("getHost() failed!", pHost.equals(host));
		String port = xmlHostStr.getPort();
		verify("getPort() failed!", pPort == null ? port == null : pPort.equals(port));
	}

	/**
	 * test
	 */
	public void test() {
		for (int i = 0; i < PROTOCOLS.length; i++) {
			String protocol = PROTOCOLS[i];
			for (int j = 0; j < HOSTS.length; j++) {
				String host = HOSTS[j];
				for (int k = 0; k < PORTS.length; k++) {
					String port = PORTS[k];
					check(protocol, host, port);
				}
			}
		}
	}

	private static final String PROTOCOL = "https", HOST = "[::1]", PORT = "5999",
			NAMESPACE = "root/cimv2", CLASSNAME = "CIM_SampleClass";

	private static final CIMObjectPath INSTANCEPATH = new CIMObjectPath(PROTOCOL, HOST, PORT,
			NAMESPACE, CLASSNAME, new CIMProperty[] { new CIMProperty<String>("KeyProp",
					CIMDataType.STRING_T, "Hello", true, false, null) });

	private static final MethodRspChecker METHOD_RSP_CHKR = new MethodRspChecker(new CIMObjectPath(
			PROTOCOL, HOST, PORT, NAMESPACE, CLASSNAME,
			new CIMProperty[] { new CIMProperty<String>("KeyProp", CIMDataType.STRING_T, "Hello",
					true, false, null) }), new CIMArgument[] { new CIMArgument<CIMObjectPath>(
			"outParam", new CIMDataType(CLASSNAME), INSTANCEPATH) },
			"data/MethodRspWithReference.xml");

	@SuppressWarnings("null")
	private void checkClassPath(CIMObjectPath pClassPath) {
		verify("pRef shouldn't be null!", pClassPath != null);
		verify("Protocols don't match! " + PROTOCOL + "!=" + pClassPath.getScheme() + "\npRef="
				+ pClassPath, PROTOCOL.equals(pClassPath.getScheme()));
		verify("Hosts don't match!", HOST.equals(pClassPath.getHost()));
		verify("Ports don't match!", PORT.equals(pClassPath.getPort()));
		verify("Namespaces don't match!", NAMESPACE.equals(pClassPath.getNamespace()));
		verify("Class names don't match!", CLASSNAME.equals(pClassPath.getObjectName()));
	}

	private void checkPathes(Object pRetVal, CIMArgument<?>[] pArgs) {
		checkClassPath((CIMObjectPath) pRetVal);
		CIMObjectPath instPath = (CIMObjectPath) pArgs[0].getValue();
		checkClassPath(instPath);
		CIMProperty<?>[] keys = instPath.getKeys();
		int len = keys == null ? 0 : keys.length;
		verify("Unexpected key property count!", len == 1);
	}

	/**
	 * Tests that XMLHostStr is integrated well into the SAX/PULL parser.
	 * 
	 * @throws Exception
	 */
	public void testSAX() throws Exception {
		CIMArgument<?>[] args = new CIMArgument[1];
		checkPathes(METHOD_RSP_CHKR.parseSAX(args), args);

	}

	/**
	 * Tests that XMLHostStr is integrated well into the DOM parser.
	 * 
	 * @throws Exception
	 */
	public void testDOM() throws Exception {
		CIMArgument<?>[] args = new CIMArgument[1];
		checkPathes(METHOD_RSP_CHKR.parseDOM(args), args);
	}

}
