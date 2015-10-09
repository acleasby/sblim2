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
 * 3529062    2012-05-23  blaschke-oss WBEMListenerFactory should return new instance
 * 3529065    2012-05-31  hellerda     Enable WBEMListener get/setProperty
 * 3536399    2012-08-25  hellerda     Add client/listener peer authentication properties
 *    2635    2013-05-16  blaschke-oss Slowloris DoS attack for CIM indication listener port
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.IOException;

import javax.cim.CIMInstance;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.listener.IndicationListener;
import javax.wbem.listener.WBEMListener;
import javax.wbem.listener.WBEMListenerConstants;
import javax.wbem.listener.WBEMListenerFactory;

import org.sblim.cimclient.WBEMListenerSBLIM;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class WBEMListenerTest is responsible to test the WBEMListener(SBLIM) class.
 */
public class WBEMListenerTest extends TestCase {

	/**
	 * Tests if the listener factory returns unique instances
	 */
	public void testListenerInstance() {
		String pw1 = "passw0rd", pw2 = "abc123", pwGlobal = "gl0bal";

		// Global props must be set prior to getListener call
		System.setProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD, pwGlobal);

		WBEMListener listener1 = WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);
		WBEMListener listener2 = WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);
		verify("Listener instances not different!", !listener1.equals(listener2));

		// Uninitialized props should be same (null or empty string)
		String testProperty = WBEMListenerConstants.PROP_LISTENER_KEYSTORE;
		String prop1 = listener1.getProperty(testProperty);
		String prop2 = listener2.getProperty(testProperty);
		verify("Uninitialized property \'" + testProperty + "\' not null!", prop1 == null
				&& prop2 == null);

		testProperty = WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD;
		prop1 = listener1.getProperty(testProperty);
		prop2 = listener2.getProperty(testProperty);
		verify("Uninitialized property \'" + testProperty + "\' not empty string!", prop1 == ""
				&& prop2 == "");

		testProperty = WBEMListenerConstants.PROP_LISTENER_TRUSTSTORE;
		prop1 = listener1.getProperty(testProperty);
		prop2 = listener2.getProperty(testProperty);
		verify("Uninitialized property \'" + testProperty + "\' not null!", prop1 == null
				&& prop2 == null);

		// Initialized props should not collide
		listener1.setProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD, pw1);
		listener2.setProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD, pw2);
		prop1 = listener1.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD);
		prop2 = listener2.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD);
		verify("Initialized property not as expected! " + pw1 + " vs " + prop1, pw1
				.equalsIgnoreCase(prop1));
		verify("Initialized property not as expected! " + pw2 + " vs " + prop2, pw2
				.equalsIgnoreCase(prop2));

		// Listener-specific prop should override global
		listener1.setProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD, pw1);
		prop1 = listener1.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD);
		verify("Listener-specific property does not override global! " + pw1 + " vs " + prop1, pw1
				.equalsIgnoreCase(prop1));
	}

	/**
	 * Tests getBlockedIPs and setBlockedIPs()
	 */
	public void testListenerBlockedIPs() {
		String IPs = "1.2.3.4,5.6.7.8", result;
		WBEMListenerSBLIM listener = (WBEMListenerSBLIM) WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);
		int port = 0;

		try {
			port = listener.addListener(new IndicationListener() {

				public void indicationOccured(String pIndicationURL, CIMInstance pIndication) {
					System.out.println("Indication " + pIndication + " received on "
							+ pIndicationURL + ":");
				}
			}, 5999, "http");
		} catch (IOException e) {
			verify("Adding listener caused exception!", false);
		}

		verify("Port not set up correctly!", port > 0);
		result = listener.getBlockedIPs(port);
		verify("Initial getBlockedIPs not null!", result == null);
		listener.setBlockedIPs(port, IPs);
		result = listener.getBlockedIPs(port);
		verify("Unexpected getBlockedIPs result! " + IPs + " != " + result, IPs
				.equalsIgnoreCase(result));
		listener.setBlockedIPs(port, null);
		result = listener.getBlockedIPs(port);
		verify("Unexpected getBlockedIPs result after restting! null != " + result, result == null);
		listener.setBlockedIPs(port, " ");
		result = listener.getBlockedIPs(port);
		verify("Unexpected getBlockedIPs result! null != " + result, result == null);

		try {
			listener.getBlockedIPs(port + 1);
			verify("Getting invalid port did not result in exception!", false);
		} catch (Exception e) {
			verify("Unexpected exception getting invalid port!",
					e instanceof IllegalArgumentException);
		}

		try {
			listener.setBlockedIPs(port + 1, IPs);
			verify("Setting invalid port did not result in exception!", false);
		} catch (Exception e) {
			verify("Unexpected exception setting invalid port!",
					e instanceof IllegalArgumentException);
		}
	}
}
