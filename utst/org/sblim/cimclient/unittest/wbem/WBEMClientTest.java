/**
 * (C) Copyright IBM Corp. 2012, 2013
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 *           Dave Blaschke, IBM, blaschke@us.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 3522904    2012-05-02  blaschke-oss Add new API WBEMClientSBLIM.isActive()
 *    2616    2013-02-23  blaschke-oss Add new API WBEMClientSBLIM.sendIndication()
 *    2151    2013-08-20  blaschke-oss gzip compression not supported
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Locale;

import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.security.auth.Subject;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.client.WBEMClientFactory;
import javax.wbem.listener.IndicationListener;
import javax.wbem.listener.WBEMListener;
import javax.wbem.listener.WBEMListenerFactory;

import org.sblim.cimclient.WBEMClientSBLIM;
import org.sblim.cimclient.WBEMConfigurationProperties;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class WBEMClientTest is responsible to test the WBEMClient(SBLIM) class.
 */
public class WBEMClientTest extends TestCase {

	private static final String PROTOCOL = "http";

	private static final int LISTENER_PORT = 5999;

	/**
	 * Tests if the settings are picked up by the client
	 * 
	 * @throws Exception
	 */
	public void testClientIsActive() throws Exception {
		WBEMClientSBLIM client = (WBEMClientSBLIM) WBEMClientFactory
				.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
		boolean isActive = client.isActive();
		verify("Client active before initialization!", isActive == false);
		client.initialize(new CIMObjectPath("https", "127.0.0.1", "5989", "root", null, null),
				new Subject(), null);
		isActive = client.isActive();
		verify("Client not active after initialization!", isActive == true);
		client.close();
		isActive = client.isActive();
		verify("Client active after close!", isActive == false);
	}

	/**
	 * Tests if indications are properly sent by the client for valid
	 * sendIndication invocations
	 * 
	 * @throws Exception
	 */
	public void testValidSendIndication() throws Exception {
		try {
			// Install listener
			WBEMListener listener = WBEMListenerFactory
					.getListener(WBEMClientConstants.PROTOCOL_CIMXML);
			listener.addListener(new IndicationListener() {

				public void indicationOccured(String pIndicationURL, CIMInstance pIndication) {
					if (pIndicationURL == null) {
						fail("Indication URL is null");
					} else if (pIndication == null) {
						fail("Indication is null");
					} else {
						verify("Indication unexpected", pIndication.getClassName()
								.equalsIgnoreCase("My_TestIndication"));
						verify("", pIndication.getPropertyCount() == 1);
						CIMProperty<?> prop = pIndication.getProperty("StringPropertyName");
						if (prop == null) {
							fail("Indication property \"StringPropertyName\" not found");
						} else if (prop.getDataType() == null) {
							fail("Indication property \"StringPropertyName\" type is null");
						} else if (prop.getDataType().getType() != CIMDataType.STRING) {
							fail("Indication property \"StringPropertyName\" type not string");
						} else {
							String val = (String) prop.getValue();
							verify("Indication property \"StringPropertyName\" value unexpected",
									val.equalsIgnoreCase("StringPropertyValue"));
						}
					}
				}
			}, LISTENER_PORT, PROTOCOL);

			// Initialize client
			WBEMClient client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
			final CIMObjectPath path = new CIMObjectPath(PROTOCOL, "localhost", null, null, null,
					null);
			final Subject subject = new Subject();
			subject.getPrincipals().add(new UserPrincipal("user"));
			subject.getPrivateCredentials().add(new PasswordCredential("password"));
			client.initialize(path, subject, new Locale[] { Locale.US });

			// Create/send indication
			URI uri = new URI(PROTOCOL, null, "localhost", LISTENER_PORT, null, null, null);
			CIMProperty<String> property = new CIMProperty<String>("StringPropertyName",
					CIMDataType.STRING_T, "StringPropertyValue", false, false, null);
			CIMProperty<?>[] properties = new CIMProperty[] { property };
			CIMInstance ind = new CIMInstance(new CIMObjectPath(null, null, null, "root\\interop",
					"My_TestIndication", null), properties);
			verify("Indication sent unsuccessfully", ((WBEMClientSBLIM) client).sendIndication(uri,
					ind));

			// Give it time to be delivered/processed
			Thread.sleep(1000);

			// Clean up
			listener.removeListener(LISTENER_PORT);
			client.close();

			Thread.sleep(1000); // Delay for Java 7 to release port
		} catch (IOException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}

	/**
	 * Tests if exceptions are properly thrown by the client for invalid
	 * sendIndication invocations
	 * 
	 * @throws Exception
	 */
	public void testInvalidSendIndication() throws Exception {
		// Initialize listener
		WBEMListener listener = WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);
		listener.addListener(new IndicationListener() {

			public void indicationOccured(String pIndicationURL, CIMInstance pIndication) {
				if (pIndicationURL == null) {
					fail("Indication URL is null");
				} else if (pIndication == null) {
					fail("Indication is null");
				} else {
					verify("Indication unexpected", pIndication.getClassName().equalsIgnoreCase(
							"My_TestIndication"));
					verify("", pIndication.getPropertyCount() == 1);
					CIMProperty<?> prop = pIndication.getProperty("StringPropertyName");
					if (prop == null) {
						fail("Indication property \"StringPropertyName\" not found");
					} else if (prop.getDataType() == null) {
						fail("Indication property \"StringPropertyName\" type is null");
					} else if (prop.getDataType().getType() != CIMDataType.STRING) {
						fail("Indication property \"StringPropertyName\" type not string");
					} else {
						String val = (String) prop.getValue();
						verify("Indication property \"StringPropertyName\" value unexpected", val
								.equalsIgnoreCase("StringPropertyValue"));
					}
				}
			}
		}, LISTENER_PORT, PROTOCOL);

		// Initialize client
		WBEMClient client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
		final CIMObjectPath path = new CIMObjectPath(PROTOCOL, "localhost", null, null, null, null);
		final Subject subject = new Subject();
		subject.getPrincipals().add(new UserPrincipal("user"));
		subject.getPrivateCredentials().add(new PasswordCredential("password"));
		client.initialize(path, subject, new Locale[] { Locale.US });

		// Create indication
		CIMProperty<String> property = new CIMProperty<String>("StringPropertyName",
				CIMDataType.STRING_T, "StringPropertyValue", false, false, null);
		CIMProperty<?>[] properties = new CIMProperty[] { property };
		CIMInstance ind = new CIMInstance(new CIMObjectPath(null, null, null, "root\\interop",
				"My_TestIndication", null), properties);

		try {
			((WBEMClientSBLIM) client).sendIndication(null, ind);
			fail("Null recipient did not cause exception");
		} catch (WBEMException e) {
			verify("Unexpected exception for null recipient: " + e.getMessage(),
					e.getID() == WBEMException.CIM_ERR_INVALID_PARAMETER);
		}

		try {
			URI uri = new URI(PROTOCOL, null, "localhost", LISTENER_PORT, null, null, null);
			((WBEMClientSBLIM) client).sendIndication(uri, null);
			fail("Null indication did not cause exception");
		} catch (WBEMException e) {
			verify("Unexpected exception for null indication: " + e.getMessage(),
					e.getID() == WBEMException.CIM_ERR_INVALID_PARAMETER);
		}

		try {
			URI uri = new URI(null, null, "localhost", LISTENER_PORT, null, null, null);
			((WBEMClientSBLIM) client).sendIndication(uri, ind);
			fail("Null protocol did not cause exception");
		} catch (WBEMException e) {
			verify("Unexpected exception for null protocol: " + e.getMessage(),
					e.getID() == WBEMException.CIM_ERR_INVALID_PARAMETER);
		}

		try {
			URI uri = new URI("ftp", null, "localhost", LISTENER_PORT, null, null, null);
			((WBEMClientSBLIM) client).sendIndication(uri, ind);
			fail("Invalid protocol did not cause exception");
		} catch (WBEMException e) {
			verify("Unexpected exception for invalid protocol: " + e.getMessage(),
					e.getID() == WBEMException.CIM_ERR_INVALID_PARAMETER);
		}

		try {
			URI uri = new URI(PROTOCOL, null, "localhost", -1, null, null, null);
			((WBEMClientSBLIM) client).sendIndication(uri, ind);
			fail("Invalid port did not cause exception");
		} catch (WBEMException e) {
			verify("Unexpected exception for invalid port: " + e.getMessage(),
					e.getID() == WBEMException.CIM_ERR_INVALID_PARAMETER);
		}

		// Clean up
		listener.removeListener(LISTENER_PORT);
		client.close();
	}

	private static final String nameInstance = "CIM_DummyInstance";

	private static final String nameNamespace = "root/interop";

	private static final String nameBooleanProp = "BooleanProperty";

	private static final String nameIntegerProp = "IntegerProperty";

	private static final String nameStringProp = "StringProperty";

	private static final String httpHeadersCommon = "HTTP/1.1 200 OK\n"
			+ "Content-Type: application/xml;charset=\"utf-8\"\n"
			+ "Man: http://www.dmtf.org/cim/mapping/http/v1.0;ns=97\n"
			+ "97-CIMOperation: MethodResponse\n";

	private byte[] readFileBytes(String filename) throws IOException {
		DataInputStream is = new DataInputStream(getClass().getResourceAsStream(filename));
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		return buffer;
	}

	/**
	 * testClientGzipEncoding tests the return from WBEMClient.createInstance()
	 * using both identity (unzipped) and gzip'd CIM-XML in response
	 * 
	 * @throws Exception
	 */
	public void testClientGzipEncoding() throws Exception {
		try {
			Subject subject = new Subject();
			subject.getPrincipals().add(new UserPrincipal("user"));
			subject.getPrivateCredentials().add(new PasswordCredential("password"));

			CIMProperty<?>[] propInst = new CIMProperty[] {
					new CIMProperty<String>(nameStringProp, CIMDataType.STRING_T, "String"),
					new CIMProperty<Integer>(nameIntegerProp, CIMDataType.SINT32_T, Integer
							.valueOf(42)),
					new CIMProperty<Boolean>(nameBooleanProp, CIMDataType.BOOLEAN_T, Boolean.TRUE) };
			CIMObjectPath copInst = new CIMObjectPath(null, null, null, nameNamespace,
					nameInstance, null);

			byte[] xmlUnzipped = readFileBytes("data/createInstance.xml");
			byte[] xmlZipped = readFileBytes("data/createInstance.xml.gz");

			verify("gzip XML larger than indentity XML! ", xmlUnzipped.length > xmlZipped.length);

			StringBuilder httpHeaders = new StringBuilder(httpHeadersCommon);
			httpHeaders.append("Content-Length: ");
			httpHeaders.append(xmlUnzipped.length);
			httpHeaders.append("\n\n");

			int port = startServer(httpHeaders.toString().getBytes(), xmlUnzipped);
			WBEMClient client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
			CIMObjectPath copServer = new CIMObjectPath("http", "localhost", String.valueOf(port),
					null, null, null);
			client.initialize(copServer, subject, new Locale[] { Locale.US });
			CIMInstance inst = new CIMInstance(copInst, propInst);
			CIMObjectPath copUnzipped = client.createInstance(inst);
			client.close();

			httpHeaders = new StringBuilder(httpHeadersCommon);
			httpHeaders.append("Content-Encoding: gzip\nContent-Length: ");
			httpHeaders.append(xmlZipped.length);
			httpHeaders.append("\n\n");

			port = startServer(httpHeaders.toString().getBytes(), xmlZipped);
			client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
			copServer = new CIMObjectPath("http", "localhost", String.valueOf(port), null, null,
					null);
			System.setProperty(WBEMConfigurationProperties.ENABLE_GZIP_ENCODING, "true");
			client.initialize(copServer, subject, new Locale[] { Locale.US });
			inst = new CIMInstance(copInst, propInst);
			CIMObjectPath copZipped = client.createInstance(inst);
			client.close();

			System.setProperty(WBEMConfigurationProperties.ENABLE_GZIP_ENCODING, "false");
			verify("CIMObjectPaths not equal! " + copUnzipped.toString() + " != "
					+ copZipped.toString(), copUnzipped.equals(copZipped));
		} catch (Exception e) {
			fail("Unexpected exception in testClientGzip: " + e);
		}
	}

	private int startServer(final byte HttpData[], final byte XmlData[]) throws IOException,
			InterruptedException {
		final ServerSocket socket = new ServerSocket(0);

		Thread server = new Thread(new Runnable() {

			public void run() {
				try {
					socket.setSoTimeout(5000);
					Socket connection = socket.accept();
					try {
						if (connection != null) {
							try {
								// wait for client to write entire request
								Thread.sleep(1000);

								byte[] buffer = new byte[4096];
								int read = connection.getInputStream().read(buffer);
								if (read > 0) {
									String inStr = new String(buffer, 0, read);
									verify("Client request does not contain instance "
											+ nameInstance, inStr.contains(nameInstance));
									verify("Client request does not contain namespace "
											+ nameNamespace, inStr.contains(nameNamespace));
									verify("Client request does not contain boolean property "
											+ nameBooleanProp, inStr.contains(nameBooleanProp));
									verify("Client request does not contain integer property "
											+ nameIntegerProp, inStr.contains(nameIntegerProp));
									verify("Client request does not contain string property "
											+ nameStringProp, inStr.contains(nameStringProp));
								} else {
									fail("Server thread unable to read from socket!");
								}

								connection.getOutputStream().write(HttpData);
								connection.getOutputStream().write(XmlData);
								connection.getOutputStream().flush();
							} finally {
								// wait for client to read entire response
								Thread.sleep(1000);

								connection.close();
							}
						}
					} finally {
						socket.close();
					}
				} catch (IOException e) {
					fail("Server thread encountered unexpected IOException: " + e);
				} catch (InterruptedException e) {
					fail("Server thread interrupted!");
				}
			}
		});
		server.setDaemon(true);
		server.start();
		Thread.sleep(1000); // Windows checkAccept() a bit slower than Linux
		return socket.getLocalPort();
	}
}
