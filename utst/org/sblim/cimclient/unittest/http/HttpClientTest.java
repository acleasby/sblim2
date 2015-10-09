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
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1702832    2007-04-18  lupusalex    WBEMClientCIMXL.setCustomSocketFactory() not implemented
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2817962    2009-08-05  blaschke-oss socket creation connects w/o a timeout
 */
package org.sblim.cimclient.unittest.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.net.SocketFactory;

import org.sblim.cimclient.internal.http.AuthorizationHandler;
import org.sblim.cimclient.internal.http.HttpClient;
import org.sblim.cimclient.internal.http.HttpClientPool;
import org.sblim.cimclient.internal.util.WBEMConfiguration;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class HttpClientTest tests the HttpClient class
 * 
 */
public class HttpClientTest extends TestCase {

	protected static class TestException extends RuntimeException {

		private static final long serialVersionUID = -7570396311377511390L;
	}

	/**
	 * Tests custom socket factory setting
	 * 
	 * @throws Exception
	 */
	public void testCustomSocketFactory() throws Exception {
		WBEMConfiguration configuration = new WBEMConfiguration(new Properties());
		configuration.setCustomSocketFactory(new SocketFactory() {

			/**
			 */
			@Override
			public Socket createSocket() {
				throw new TestException();
			}

			/**
			 * @param pHost
			 * @param port
			 */
			@Override
			public Socket createSocket(String pHost, int port) {
				throw new TestException();
			}

			/**
			 * @param pHost
			 * @param port
			 * @param pLocalHost
			 * @param pLocalPort
			 */
			@Override
			public Socket createSocket(String pHost, int port, InetAddress pLocalHost,
					int pLocalPort) {
				throw new TestException();
			}

			/**
			 * @param pHost
			 * @param port
			 */
			@Override
			public Socket createSocket(InetAddress pHost, int port) {
				throw new TestException();
			}

			/**
			 * @param pAddress
			 * @param port
			 * @param pLocalAddress
			 * @param pLocalPort
			 */
			@Override
			public Socket createSocket(InetAddress pAddress, int port, InetAddress pLocalAddress,
					int pLocalPort) {
				throw new TestException();
			}
		});
		HttpClientPool pool = new HttpClientPool(configuration);
		HttpClient client = pool.retrieveAvailableConnectionFromPool(new URI(
				"http://www.sblim.org:55555"), new AuthorizationHandler());
		try {
			client.connect();
			fail("HttpClient didn't use the given custom socket factory");
		} catch (TestException e) {
			// good boy !
		} catch (UnknownHostException e) {
			fail("HttpClient didn't use the given custom socket factory");
		} catch (IOException e) {
			fail("HttpClient didn't use the given custom socket factory");
		}
		configuration.setCustomSocketFactory(null);
		try {
			client.connect();
		} catch (TestException e) {
			fail("HttpClient used custom socket factory when not set");
		} catch (UnknownHostException e) {
			// good boy !
		} catch (IOException e) {
			// good boy !
		}

	}
}
