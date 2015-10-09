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
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1660743    2007-02-15  lupusalex    SSLContext is static
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 3197627    2011-03-02  blaschke-oss testBasicConnect unit test fails on Windows
 * 3536399    2012-08-25  hellerda     Add client/listener peer authentication properties
 */
package org.sblim.cimclient.unittest.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocket;
import javax.security.auth.Subject;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.client.WBEMClientFactory;
import javax.wbem.listener.WBEMListenerFactory;

import org.sblim.cimclient.IndicationListenerSBLIM;
import org.sblim.cimclient.WBEMClientSBLIM;
import org.sblim.cimclient.WBEMConfigurationProperties;
import org.sblim.cimclient.WBEMListenerSBLIM;
import org.sblim.cimclient.internal.http.HttpSocketFactory;
import org.sblim.cimclient.internal.util.WBEMConfiguration;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class SSLConfigurationTest is responsible for testing the HTTP SSL
 * configuration. If keystore / truststore settings are not applied correctly
 * test from this class will fail.
 * 
 */
public class SSLConfigurationTest extends TestCase {

	private static final String HELLO_CLIENT = "Hello Client!";

	private static final String HELLO_SERVER = "Hello Server!";

	/**
	 * Tests basic SSL connection
	 * 
	 * @throws Exception
	 */
	public void testBasicConnect() throws Exception {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1000");
		URL keystore = getClass().getResource("keystore.pks");
		System.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore.getFile());
		System.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		System.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		System.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "");
		System.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
		System.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "ignore");

		// PEER VERIFICATION disabled globally (should PASS)
		connect(WBEMConfiguration.getGlobalConfiguration(), WBEMConfiguration
				.getGlobalConfiguration());

		// PEER VERIFICATION disabled but truststore configured (should PASS and
		// give WARNING)
		System.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH,
				"/any/path/even/if/not/validated");
		connect(WBEMConfiguration.getGlobalConfiguration(), WBEMConfiguration
				.getGlobalConfiguration());

		// CLIENT_PEER_VERIFICATION enabled globally but no truststore
		// configured (should FAIL)
		System.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
		System.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "ignore");
		System.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		try {
			connect(WBEMConfiguration.getGlobalConfiguration(), WBEMConfiguration
					.getGlobalConfiguration());
			fail("Connection established with client peer verification enabled but no truststore configured");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION enabled globally but no truststore
		// configured (should FAIL)
		System.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
		System.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "require");
		try {
			connect(WBEMConfiguration.getGlobalConfiguration(), WBEMConfiguration
					.getGlobalConfiguration());
			fail("Connection established with listener peer verification enabled but no truststore configured (n)");
		} catch (Exception e) {
			// expected to fail
		}
		// CLIENT_PEER_VERIFICATION enabled globally but truststore
		// misconfigured (should FAIL)
		System.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
		System.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "ignore");
		System.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "somebadpath");
		try {
			connect(WBEMConfiguration.getGlobalConfiguration(), WBEMConfiguration
					.getGlobalConfiguration());
			fail("Connection established with client peer verification enabled but truststore misconfigured");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION enabled globally but truststore
		// misconfigured (should FAIL)
		System.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
		System.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "require");
		System.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "somebadpath");
		try {
			connect(WBEMConfiguration.getGlobalConfiguration(), WBEMConfiguration
					.getGlobalConfiguration());
			fail("Connection established with listener peer verification enabled but truststore misconfigured");
		} catch (Exception e) {
			// expected to fail
		}
	}

	/**
	 * Test if the client correctly evaluates the authentication of the server.<br />
	 * Uses four keystores:
	 * 
	 * <em>keystore</em> and <em>stranger</em> contain a full private/public key
	 * each; <em>truststore</em> contains the public key from keystore;
	 * <em>notrust</em> is empty<br />
	 * 
	 * @throws Exception
	 */
	public void testClientTrust() throws Exception {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1000");
		URL stranger = getClass().getResource("stranger.pks");
		URL keystore = getClass().getResource("keystore.pks");
		URL truststore = getClass().getResource("truststore.pks");
		URL notrust = getClass().getResource("notrust.pks");

		Properties clientConfiguration = new Properties();
		Properties serverConfiguration = new Properties();

		serverConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore
				.getFile());
		serverConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		serverConfiguration
				.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "");
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		clientConfiguration
				.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
		clientConfiguration.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION,
				"false");
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"ignore");

		// PEER VERIFICATION disabled at domain-level (should PASS)
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// PEER VERIFICATION disabled but truststore configured (should PASS and
		// give WARNING)
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH,
				"/any/path/even/if/not/validated");
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// CLIENT_PEER_VERIFICATION enabled at domain-level but no truststore
		// configured (should FAIL)
		clientConfiguration.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION,
				"true");
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"ignore");
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with client peer verification enabled but no truststore configured");
		} catch (Exception e) {
			// expected to fail
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level but truststore
		// misconfigured (should FAIL)
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "somebadpath");
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with client peer verification enabled but truststore misconfigured");
		} catch (Exception e) {
			// expected to fail
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level with valid
		// truststore (should PASS)
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// CLIENT_PEER_VERIFICATION enabled at domain-level with notrust
		// truststore (should FAIL)
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, notrust
				.getFile());
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with client peer verification enabled with notrust truststore");
		} catch (Exception e) {
			// expected to fail
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level with untrusted
		// server keystore (should FAIL)
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		serverConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, stranger
				.getFile());
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with client peer verification enabled with untrusted server keystore");
		} catch (Exception e) {
			// expected to fail
		}
	}

	/**
	 * Test if the server correctly evaluates the authentication of the client.<br />
	 * Uses four keystores:
	 * 
	 * <em>keystore</em> and <em>stranger</em> contain a full private/public key
	 * each; <em>truststore</em> contains the public key from keystore;
	 * <em>notrust</em> is empty<br />
	 * 
	 * @throws Exception
	 */
	public void testServerTrust() throws Exception {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1000");
		URL stranger = getClass().getResource("stranger.pks");
		URL keystore = getClass().getResource("keystore.pks");
		URL truststore = getClass().getResource("truststore.pks");
		URL notrust = getClass().getResource("notrust.pks");

		Properties clientConfiguration = new Properties();
		Properties serverConfiguration = new Properties();

		serverConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore
				.getFile());
		serverConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		serverConfiguration
				.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore
				.getFile());
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		clientConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		clientConfiguration
				.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
		clientConfiguration.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION,
				"false");
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"ignore");

		// PEER VERIFICATION disabled at domain-level (should PASS)
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// PEER VERIFICATION disabled but truststore configured (should PASS and
		// give WARNING)
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH,
				"/any/path/even/if/not/validated");
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// LISTENER_PEER_VERIFICATION enabled at domain-level but no truststore
		// configured (should FAIL)
		clientConfiguration.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION,
				"false");
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"require");
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with listener peer verification enabled but no truststore configured");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level but truststore
		// misconfigured (should FAIL)
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "somebadpath");
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with listener peer verification enabled but truststore misconfigured");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level with valid
		// truststore (should PASS)
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// LISTENER_PEER_VERIFICATION enabled at domain-level with notrust
		// truststore (should FAIL)
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, notrust
				.getFile());
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with listener peer verification enabled with notrust truststore");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level with untrusted
		// client keystore (should FAIL)
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, stranger
				.getFile());
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with listener peer verification enabled with untrusted client keystore");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level but no client
		// keystore (should FAIL)
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "");
		try {
			connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
					serverConfiguration));
			fail("Connection established with listener peer verification enabled but no client keystore");
		} catch (Exception e) {
			// expected to fail
		}
		// LISTENER_PEER_VERIFICATION set to accept with valid client keystore
		// (should PASS)
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"accept");
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore
				.getFile());
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// LISTENER_PEER_VERIFICATION set to accept with untrusted client
		// keystore (should PASS)
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"accept");
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, stranger
				.getFile());
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));

		// LISTENER_PEER_VERIFICATION set to accept with no client keystore
		// (should PASS)
		serverConfiguration.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION,
				"accept");
		serverConfiguration.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore
				.getFile());
		clientConfiguration.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "");
		connect(new WBEMConfiguration(clientConfiguration), new WBEMConfiguration(
				serverConfiguration));
	}

	/**
	 * Test if the WBEMClient correctly evaluates the authentication of the
	 * listener.<br />
	 * Uses four keystores:
	 * 
	 * <em>keystore</em> and <em>stranger</em> contain a full private/public key
	 * each; <em>truststore</em> contains the public key from keystore;
	 * <em>notrust</em> is empty<br />
	 * 
	 * @throws Exception
	 */
	public void testWBEMClientTrust() throws Exception {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1000");
		URL stranger = getClass().getResource("stranger.pks");
		URL keystore = getClass().getResource("keystore.pks");
		URL truststore = getClass().getResource("truststore.pks");
		URL notrust = getClass().getResource("notrust.pks");

		WBEMListenerSBLIM listener = (WBEMListenerSBLIM) WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);

		IndicationListenerSBLIM ilsBrief = new IndicationListenerSBLIM() {

			public void indicationOccured(String pIndicationURL, CIMInstance pIndication,
					InetAddress pSenderAddress) {
				System.out.println("Indication received on: " + pIndicationURL + ": from IP: "
						+ pSenderAddress.getHostAddress() + ": classname: "
						+ pIndication.getClassName());
			}
		};

		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore.getFile());
		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
		listener.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "ignore");

		CIMObjectPath dummyCop = new CIMObjectPath(null, null, null, "root/interop",
				"CIM_Namespace", null);

		// PEER VERIFICATION disabled at domain-level (should PASS)
		WBEMClientSBLIM client;
		int port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) fail("Connection failed with SSLException: "
					+ e.getCause().getMessage());
		} finally {
			listener.removeListener(port);
		}
		// PEER VERIFICATION disabled but truststore configured (should PASS and
		// give WARNING)
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH,
					"/any/path/even/if/not/validated");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) fail("Connection failed with SSLException: "
					+ e.getCause().getMessage());
		} finally {
			listener.removeListener(port);
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level but no truststore
		// configured (should FAIL)
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) {
				// expected SSLException
			} else fail("Connection established with client peer verification enabled but no truststore configured");
		} finally {
			listener.removeListener(port);
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level but truststore
		// misconfigured (should FAIL)
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "somebadpath");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) {
				// expected SSLException
			} else fail("Connection established with client peer verification enabled but truststore misconfigured");
		} finally {
			listener.removeListener(port);
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level with valid
		// truststore (should PASS)
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore.getFile());
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) fail("Connection failed with SSLException: "
					+ e.getCause().getMessage());
		} finally {
			listener.removeListener(port);
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level with notrust
		// truststore (should FAIL)
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, notrust.getFile());
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) {
				// expected SSLException
			} else fail("Connection established with client peer verification enabled with notrust truststore");
		} finally {
			listener.removeListener(port);
		}
		// CLIENT_PEER_VERIFICATION enabled at domain-level with untrusted
		// server keystore (should FAIL)
		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, stranger.getFile());
		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "true");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore.getFile());
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) {
				// expected SSLException
			} else fail("Connection established with client peer verification enabled with untrusted server keystore");
		} finally {
			listener.removeListener(port);
		}
	}

	/**
	 * Test if the WBEMListener correctly evaluates the authentication of the
	 * client.<br />
	 * Uses four keystores:
	 * 
	 * <em>keystore</em> and <em>stranger</em> contain a full private/public key
	 * each; <em>truststore</em> contains the public key from keystore;
	 * <em>notrust</em> is empty<br />
	 * 
	 * @throws Exception
	 */
	public void testWBEMListenerTrust() throws Exception {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1000");
		URL stranger = getClass().getResource("stranger.pks");
		URL keystore = getClass().getResource("keystore.pks");
		URL truststore = getClass().getResource("truststore.pks");
		URL notrust = getClass().getResource("notrust.pks");

		WBEMListenerSBLIM listener = (WBEMListenerSBLIM) WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);

		IndicationListenerSBLIM ilsBrief = new IndicationListenerSBLIM() {

			public void indicationOccured(String pIndicationURL, CIMInstance pIndication,
					InetAddress pSenderAddress) {
				System.out.println("Indication received on: " + pIndicationURL + ": from IP: "
						+ pSenderAddress.getHostAddress() + ": classname: "
						+ pIndication.getClassName());
			}
		};

		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore.getFile());
		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PASSWORD, "password");
		listener.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "ignore");

		CIMObjectPath dummyCop = new CIMObjectPath(null, null, null, "root/interop",
				"CIM_Namespace", null);

		// PEER VERIFICATION disabled at domain-level (should PASS)
		WBEMClientSBLIM client;
		int port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.enumerateInstanceNames(dummyCop);
			client.close();
			listener.removeListener(port);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) fail("Connection failed with SSLException: "
					+ e.getCause().getMessage());
		} finally {
			listener.removeListener(port);
		}
		// PEER VERIFICATION disabled but truststore configured (should PASS and
		// give WARNING)
		listener.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "ignore");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH,
				"/any/path/even/if/not/validated");
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) fail("Connection failed with SSLException: "
					+ e.getCause().getMessage());
		} finally {
			listener.removeListener(port);
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level but no truststore
		// configured (should FAIL)
		listener.setProperty(WBEMConfigurationProperties.SSL_LISTENER_PEER_VERIFICATION, "require");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) {
				// expected SSLException
			} else fail("Connection established with listener peer verification enabled but no truststore configured");
		} finally {
			listener.removeListener(port);
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level but truststore
		// misconfigured (should FAIL)
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "somebadpath");
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) {
				// expected SSLException
			} else fail("Connection established with listener peer verification enabled but truststore misconfigured");
		} finally {
			listener.removeListener(port);
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level with valid
		// truststore (should PASS)
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore.getFile());
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore.getFile());
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class) || e.getCause()
							.getClass().equals(SSLHandshakeException.class))) fail("Connection failed with SSLException: "
					+ e.getCause().getMessage());
		} finally {
			listener.removeListener(port);
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level with notrust
		// truststore (should FAIL)
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, notrust.getFile());
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, keystore.getFile());
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class)
							|| e.getCause().getClass().equals(SSLHandshakeException.class) || e
							.getCause().getClass().equals(SocketException.class))) {
				// expected SSLException or SocketException
			} else fail("Connection established with listener peer verification enabled with notrust truststore");
		} finally {
			listener.removeListener(port);
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level with untrusted
		// client keystore (should FAIL)
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore.getFile());
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, stranger.getFile());
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class)
							|| e.getCause().getClass().equals(SSLHandshakeException.class) || e
							.getCause().getClass().equals(SocketException.class))) {
				// expected SSLException or SocketException
			} else fail("Connection established with listener peer verification enabled with untrusted client keystore");
		} finally {
			listener.removeListener(port);
		}
		// LISTENER_PEER_VERIFICATION enabled at domain-level but no client
		// keystore (should FAIL)
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, truststore.getFile());
		port = listener.addListener(ilsBrief, 0, "https");
		try {
			client = (WBEMClientSBLIM) initClient(new URL("https://localhost:" + port), null, null);
			client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PEER_VERIFICATION, "false");
			client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "");
			client.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "password");
			client.enumerateInstanceNames(dummyCop);
		} catch (WBEMException e) {
			if (e.getCause() != null
					&& (e.getCause().getClass().equals(SSLException.class)
							|| e.getCause().getClass().equals(SSLHandshakeException.class) || e
							.getCause().getClass().equals(SocketException.class))) {
				// expected SSLException or SocketException
			} else fail("Connection established with listener peer verification enabled but no client keystore");
		} finally {
			listener.removeListener(port);
		}
	}

	/**
	 * Initializes a CIM client connection to the given server socket.
	 * 
	 * @param pWbemUrl
	 *            The URL of the WBEM service (e.g.
	 *            <code>https://myhost.mydomain.com:5989</code>)
	 * @param pUser
	 *            The user name for authenticating with the WBEM service
	 * @param pPassword
	 *            The corresponding password
	 * @return A <code>WBEMClient</code> instance if connect was successful,
	 *         <code>null</code> otherwise
	 */
	private static WBEMClient initClient(final URL pWbemUrl, final String pUser,
			final String pPassword) {
		try {
			final WBEMClient client = WBEMClientFactory
					.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
			final CIMObjectPath path = new CIMObjectPath(pWbemUrl.getProtocol(),
					pWbemUrl.getHost(), String.valueOf(pWbemUrl.getPort()), null, null, null);
			final Subject subject = new Subject();
			if (pUser != null && pPassword != null) {
				subject.getPrincipals().add(new UserPrincipal(pUser));
				subject.getPrivateCredentials().add(new PasswordCredential(pPassword));
			}
			client.initialize(path, subject, new Locale[] { Locale.US });
			return client;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Starts a server, creates a client connection and exchange greetings.
	 * 
	 * @param pClientConfiguration
	 *            The client configuration
	 * @param pServerConfiguration
	 *            The server configuration
	 * @throws Exception
	 */
	private void connect(WBEMConfiguration pClientConfiguration,
			WBEMConfiguration pServerConfiguration) throws Exception {

		int port = startServer(pServerConfiguration);

		Socket socket = HttpSocketFactory.getInstance().getClientSSLContext(pClientConfiguration)
				.getSocketFactory().createSocket("127.0.0.1", port);
		try {
			socket.setSoTimeout(5000);
			socket.getOutputStream().write(HELLO_SERVER.getBytes());
			socket.getOutputStream().flush();

			int read = 0;
			byte[] buffer = new byte[32];
			while (read <= 0) {
				read = socket.getInputStream().read(buffer);
			}
			verify("Message from server", EQUAL, new String(buffer, 0, read), HELLO_CLIENT);
		} finally {
			socket.close();
		}

	}

	/**
	 * Creates a server socket and starts a thread that listens on the socket.
	 * When a connection comes in, greeting are exchanged and the socket is
	 * closed.
	 * 
	 * @param pConfiguration
	 * @return The port of the server socket
	 * @throws IOException
	 */
	private int startServer(final WBEMConfiguration pConfiguration) throws IOException,
			InterruptedException {
		final ServerSocket socket = HttpSocketFactory.getInstance().getServerSSLContext(
				pConfiguration).getServerSocketFactory().createServerSocket(0);

		if (pConfiguration.getSslListenerPeerVerification().equalsIgnoreCase("ignore")) {
			((SSLServerSocket) socket).setNeedClientAuth(false);
		} else if (pConfiguration.getSslListenerPeerVerification().equalsIgnoreCase("accept")) {
			((SSLServerSocket) socket).setWantClientAuth(true);
		} else {
			((SSLServerSocket) socket).setNeedClientAuth(true);
		}
		Thread server = new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						socket.setSoTimeout(5000);
						Socket connection = socket.accept();
						try {
							if (connection != null) {
								try {
									// System.out.println(((SSLSocket)
									// connection).getSession());
									int read = 0;
									byte[] buffer = new byte[32];
									while (read <= 0) {
										read = connection.getInputStream().read(buffer);
									}
									if (HELLO_SERVER.equals(new String(buffer, 0, read))) {
										connection.getOutputStream().write(HELLO_CLIENT.getBytes());
										connection.getOutputStream().flush();
									}
								} finally {
									connection.close();
								}
							}
							return;
						} finally {
							socket.close();
						}
					} catch (IOException e) {
						return;
					}
				}
			}
		});
		server.setDaemon(true);
		server.start();
		Thread.sleep(1000); // Windows checkAccept() a bit slower than Linux
		return socket.getLocalPort();
	}
}
