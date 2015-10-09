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
 * 1565892    2006-11-27  lupusalex    Make SBLIM client JSR48 compliant
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2882448    2009-10-21  blaschke-oss Add WBEMClientConstants from JSR48
 * 2884718    2009-10-23  blaschke-oss Merge JSR48 and SBLIM client properties
 * 2930341    2010-01-12  blaschke-oss Sync up WBEMClientConstants with JSR48 1.0.0
 * 3496355    2012-03-02  blaschke-oss JSR48 1.0.0: add new WBEMClientConstants
 * 3514685    2012-04-03  blaschke-oss TCK: getProperty must return default values
 * 3515180    2012-04-05  blaschke-oss JSR48 log dir/file should handle UNIX/Win separators
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 * 3521157    2012-05-10  blaschke-oss JSR48 1.0.0: PROP_ENABLE_*_LOGGING is Level, not 0/1
 * 3529065    2012-05-31  hellerda     Enable WBEMListener get/setProperty
 */

package org.sblim.cimclient.unittest.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import javax.cim.CIMObjectPath;
import javax.security.auth.Subject;
import javax.wbem.WBEMException;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.client.WBEMClientFactory;
import javax.wbem.listener.WBEMListener;
import javax.wbem.listener.WBEMListenerConstants;
import javax.wbem.listener.WBEMListenerFactory;

import org.sblim.cimclient.WBEMClientSBLIM;
import org.sblim.cimclient.WBEMConfigurationProperties;
import org.sblim.cimclient.internal.util.WBEMConfiguration;
import org.sblim.cimclient.internal.util.WBEMConfigurationDefaults;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class WBEMConfigurationTest is responsible to test the WBEMConfiguration
 * class
 * 
 */
public class WBEMConfigurationTest extends TestCase {

	private static class TestThread extends Thread {

		private WBEMConfiguration iConfig;

		private int iTimeout;

		private int iTimeout2;

		protected TestThread(WBEMConfiguration pConfig) {
			this.iConfig = pConfig;
		}

		@Override
		public void run() {
			this.iTimeout = this.iConfig.getHttpTimeout();
			this.iConfig.setLocalProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "4444");
			this.iTimeout2 = this.iConfig.getHttpTimeout();
		}

		protected int getTimeout() {
			return this.iTimeout;
		}

		protected int getTimeout2() {
			return this.iTimeout2;
		}
	}

	/**
	 * Tests loadGlobalConfiguration(). First is checked if the attempt to load
	 * a non-existing file fails correctly. Second an existing file is loaded
	 * and the resulting change of a configuration property verified.
	 */
	public void testLoadGlobalConfiguration() {
		System.setProperty(WBEMConfigurationProperties.CONFIG_URL, "file:xyz.abc.propelties");
		WBEMConfiguration.getGlobalConfiguration();
		WBEMConfiguration.loadGlobalConfiguration();
		verify("No exception on non-existing file", WBEMConfiguration
				.getConfigurationLoadException() instanceof FileNotFoundException);
		verify("Non-existing file found", !WBEMConfiguration.isConfigurationLoadSuccessful());
		verify("Value of getHttpTimeout()", EQUAL, new Integer(WBEMConfiguration
				.getGlobalConfiguration().getHttpTimeout()), new Integer(
				WBEMConfigurationDefaults.HTTP_TIMEOUT));
		System.setProperty(WBEMConfigurationProperties.CONFIG_URL,
				"file:utst/org/sblim/cimclient/unittest/util/cimclient.properties");
		WBEMConfiguration.loadGlobalConfiguration();
		verify("Exception during config load",
				WBEMConfiguration.getConfigurationLoadException() == null);
		verify("Config file not found", WBEMConfiguration.isConfigurationLoadSuccessful());
		verify("Value of sblim.wbem.httpTimeout", EQUAL, System
				.getProperty(WBEMConfigurationProperties.HTTP_TIMEOUT), "1000");
		verify("Value of getHttpTimeout()", EQUAL, new Integer(WBEMConfiguration
				.getGlobalConfiguration().getHttpTimeout()), new Integer(1000));

		System.getProperties().remove(WBEMConfigurationProperties.CONFIG_URL);
	}

	/**
	 * Test fall back to defaults
	 */
	public void testDefault() {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1234");
		verify("Global value of getHttpTimeout()", EQUAL, new Integer(WBEMConfiguration
				.getGlobalConfiguration().getHttpTimeout()), new Integer(1234));
		System.getProperties().remove(WBEMConfigurationProperties.HTTP_TIMEOUT);
		verify("Default value of getHttpTimeout()", EQUAL, new Integer(WBEMConfiguration
				.getGlobalConfiguration().getHttpTimeout()), Integer
				.valueOf(WBEMConfigurationDefaults.HTTP_TIMEOUT));
	}

	/**
	 * Tests overriding of properties
	 */
	public void testOverride() {
		System.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "1111");
		Properties override = new Properties();
		override.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "2222");
		WBEMConfiguration config = new WBEMConfiguration(override);
		verify("Value of overridden getHttpTimeout()", EQUAL, new Integer(config.getHttpTimeout()),
				new Integer(2222));
		verify("Value of global getHttpTimeout()", EQUAL, new Integer(WBEMConfiguration
				.getGlobalConfiguration().getHttpTimeout()), new Integer(1111));
		config.setLocalProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "3333");
		verify("Value of locally overridden getHttpTimeout()", EQUAL, new Integer(config
				.getHttpTimeout()), new Integer(3333));
		verify("Value of global getHttpTimeout()", EQUAL, new Integer(WBEMConfiguration
				.getGlobalConfiguration().getHttpTimeout()), new Integer(1111));
		TestThread thread = new TestThread(config);
		thread.start();
		try {
			thread.join(10000);
		} catch (InterruptedException e) {
			// empty
		}
		verify("Inherited value seen from independent thread in getHttpTimeout()", EQUAL,
				new Integer(thread.getTimeout()), new Integer(3333));
		verify("Overridden value seen by independent thread in getHttpTimeout()", EQUAL,
				new Integer(thread.getTimeout2()), new Integer(4444));
		verify("Value seen in parent thread thread in getHttpTimeout()", EQUAL, new Integer(config
				.getHttpTimeout()), new Integer(3333));

		System.getProperties().remove(WBEMConfigurationProperties.HTTP_TIMEOUT);
	}

	/**
	 * Test setter and getters
	 */
	public void testSetAndGet() {

		System.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "bad_bad_bad");
		WBEMConfiguration config = new WBEMConfiguration(new Properties());

		Properties props = new Properties();
		props.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "test_1_value");
		config.setDomainProperties(props);
		verify("Domainproperties set&get", config.getDomainProperties() == props);
		verify("Domainproperty", EQUAL, config
				.getDomainProperty(WBEMConfigurationProperties.KEYSTORE_PATH), "test_1_value");
		verify("Property", EQUAL, config.getSslKeyStorePath(), "test_1_value");

		Properties localprops = new Properties();
		localprops.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "test_2_value");
		config.setLocalProperties(localprops);
		verify("Localproperties set&get", config.getLocalProperties() == localprops);
		verify("Localproperty", EQUAL, config
				.getLocalProperty(WBEMConfigurationProperties.KEYSTORE_PATH), "test_2_value");
		verify("Property", EQUAL, config.getSslKeyStorePath(), "test_2_value");

		config = new WBEMConfiguration(new Properties());

		config.setDomainProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "test_3_value");
		verify("Domainproperty", EQUAL, config
				.getDomainProperty(WBEMConfigurationProperties.KEYSTORE_PATH), "test_3_value");
		verify("Property", EQUAL, config.getSslKeyStorePath(), "test_3_value");

		config.setLocalProperty(WBEMConfigurationProperties.KEYSTORE_PATH, "test_4_value");
		verify("Localproperty", EQUAL, config
				.getLocalProperty(WBEMConfigurationProperties.KEYSTORE_PATH), "test_4_value");
		verify("Property", EQUAL, config.getSslKeyStorePath(), "test_4_value");
	}

	/**
	 * Tests if the settings are picked up by the client
	 * 
	 * @throws Exception
	 */
	public void testClient() throws Exception {
		WBEMClientSBLIM client = (WBEMClientSBLIM) WBEMClientFactory
				.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
		client.initialize(new CIMObjectPath("https", "127.0.0.1", "5989", "root", null, null),
				new Subject(), null);
		client.setLocalProperty(WBEMConfigurationProperties.SSL_SOCKET_PROVIDER,
				"no.such.package.JSSEProvider");
		try {
			client.enumerateClasses(new CIMObjectPath(null, null, null, "root",
					"CIM_ManagedElement", null), false, false, true, false);
			fail("Didn't fail even though JSSE provider was invalid");
		} catch (WBEMException e) {
			if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
				RuntimeException re = (RuntimeException) e.getCause();
				if (re.getCause() != null && re.getCause() instanceof ClassNotFoundException) { return; }
			}
			throw e;
		}

	}

	/**
	 * testJSR48Properties tests relationship between SBLIM and JSR48 properties
	 * 
	 * @throws Exception
	 */
	public void testJSR48Properties() throws Exception {
		String file1 = "filename.1";
		String file2 = "filename.2";
		String dirRoot = File.separator;
		String dirAbsolute = File.separator + "dirabs";
		String dirRelative = ".." + File.separator + ".." + File.separator + "dirrel";
		String dirAbsoluteLong = File.separator + "dirabs1" + File.separator + "dirabs2";
		String dirRelativeLong = ".." + File.separator + ".." + File.separator + "dirrel";
		String pathRoot1 = dirRoot + file1;
		String pathAbsolute1 = dirAbsolute + File.separator + file1;
		String pathRelative1 = dirRelative + File.separator + file1;
		String pathAbsoluteLong1 = dirAbsoluteLong + File.separator + file1;
		String pathRelativeLong1 = dirRelativeLong + File.separator + file1;
		String pathRoot2 = dirRoot + file2;
		String pathAbsolute2 = dirAbsolute + File.separator + file2;
		String pathRelative2 = dirRelative + File.separator + file2;
		String pathAbsoluteLong2 = dirAbsoluteLong + File.separator + file2;
		String pathRelativeLong2 = dirRelativeLong + File.separator + file2;
		String invalidProperty = "javax.wbem.client.log.name";

		System.getProperties().remove(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL);
		System.getProperties().remove(WBEMConfigurationProperties.LOG_FILE_LEVEL);
		System.getProperties().remove(WBEMConfigurationProperties.LOG_FILE_SIZE_LIMIT);
		System.getProperties().remove(WBEMConfigurationProperties.LOG_FILE_LOCATION);
		System.getProperties().remove(WBEMConfigurationProperties.LOG_FILE_COUNT);
		System.getProperties().remove(WBEMConfigurationProperties.HTTP_TIMEOUT);
		System.getProperties().remove(WBEMConfigurationProperties.HTTP_USE_CHUNKING);
		System.getProperties().remove(WBEMConfigurationProperties.KEYSTORE_PATH);
		System.getProperties().remove(WBEMConfigurationProperties.KEYSTORE_PASSWORD);
		System.getProperties().remove(WBEMConfigurationProperties.TRUSTSTORE_PATH);

		// Client tests
		WBEMClientSBLIM client = (WBEMClientSBLIM) WBEMClientFactory
				.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
		client.initialize(new CIMObjectPath("https", "127.0.0.1", "5989", "root", null, null),
				new Subject(), null);

		// All JSR48 properties should be defaults with new client
		verify("Uninit enab con", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_ENABLE_CONSOLE_LOGGING), "OFF");
		verify("Uninit enab file", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_ENABLE_FILE_LOGGING), "OFF");
		verify("Uninit byte limit", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_BYTE_LIMIT), "5242880");
		verify("Uninit log dir", EQUAL, client.getProperty(WBEMClientConstants.PROP_LOG_DIR), "%t");
		verify("Uninit log file", EQUAL, client.getProperty(WBEMClientConstants.PROP_LOG_FILENAME),
				"cimclient_log_%g.txt");
		verify("Uninit num files", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_NUM_FILES), "3");
		verify("Uninit timeout", EQUAL, client.getProperty(WBEMClientConstants.PROP_TIMEOUT), "0");
		verify("Uninit chunking", EQUAL, client
				.getProperty(WBEMClientConstants.PROPERTY_WBEM_CHUNKING), "1");
		verify("Uninit keystore", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE), null);
		verify("Uninit keystore passwd", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE_PASSWORD), "");
		verify("Uninit truststore", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_TRUSTSTORE), null);

		// Setting corresponding SBLIM properties affect JSR48 properties
		client.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "WARNING");
		client.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "ALL");
		client.setProperty(WBEMConfigurationProperties.LOG_FILE_SIZE_LIMIT, "1048576");
		client.setProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION, pathAbsoluteLong1);
		client.setProperty(WBEMConfigurationProperties.LOG_FILE_COUNT, "50");
		client.setProperty(WBEMConfigurationProperties.HTTP_TIMEOUT, "60000");
		client.setProperty(WBEMConfigurationProperties.HTTP_USE_CHUNKING, "false");
		client.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, pathAbsoluteLong1);
		client.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "passw0rd");
		client.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, pathAbsoluteLong2);

		verify("SBLIM init enab con", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_ENABLE_CONSOLE_LOGGING), "WARNING");
		verify("SBLIM init enab file", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_ENABLE_FILE_LOGGING), "ALL");
		verify("SBLIM init byte limit", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_BYTE_LIMIT), "1048576");
		verify("SBLIM init log dir", EQUAL, client.getProperty(WBEMClientConstants.PROP_LOG_DIR),
				dirAbsoluteLong);
		verify("SBLIM init log file", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_FILENAME), file1);
		verify("SBLIM init num files", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_NUM_FILES), "50");
		verify("SBLIM init timeout", EQUAL, client.getProperty(WBEMClientConstants.PROP_TIMEOUT),
				"60000");
		verify("SBLIM init chunking", EQUAL, client
				.getProperty(WBEMClientConstants.PROPERTY_WBEM_CHUNKING), "0");
		verify("SBLIM init keystore", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE), pathAbsoluteLong1);
		verify("SBLIM init keystore passwd", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE_PASSWORD), "passw0rd");
		verify("SBLIM init truststore", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_TRUSTSTORE), pathAbsoluteLong2);

		// Set JSR48 properties directly
		client.setProperty(WBEMClientConstants.PROP_ENABLE_CONSOLE_LOGGING, "FINER");
		client.setProperty(WBEMClientConstants.PROP_ENABLE_FILE_LOGGING, "CONFIG");
		client.setProperty(WBEMClientConstants.PROP_LOG_BYTE_LIMIT, "8192");
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirAbsolute);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file2);
		client.setProperty(WBEMClientConstants.PROP_LOG_NUM_FILES, "4");
		client.setProperty(WBEMClientConstants.PROP_TIMEOUT, "5000");
		client.setProperty(WBEMClientConstants.PROPERTY_WBEM_CHUNKING, "1");
		client.setProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE, pathRelativeLong1);
		client.setProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE_PASSWORD, "pa$$word");
		client.setProperty(WBEMClientConstants.PROP_CLIENT_TRUSTSTORE, pathRelativeLong2);

		verify("JSR48 init enab con", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_ENABLE_CONSOLE_LOGGING), "FINER");
		verify("JSR48 init enab file", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_ENABLE_FILE_LOGGING), "CONFIG");
		verify("JSR48 init byte limit", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_BYTE_LIMIT), "8192");
		verify("JSR48 init log dir", EQUAL, client.getProperty(WBEMClientConstants.PROP_LOG_DIR),
				dirAbsolute);
		verify("JSR48 init log file", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_FILENAME), file2);
		verify("JSR48 init num files", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_LOG_NUM_FILES), "4");
		verify("JSR48 init timeout", EQUAL, client.getProperty(WBEMClientConstants.PROP_TIMEOUT),
				"5000");
		verify("JSR48 init chunking", EQUAL, client
				.getProperty(WBEMClientConstants.PROPERTY_WBEM_CHUNKING), "1");
		verify("JSR48 init keystore", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE), pathRelativeLong1);
		verify("JSR48 init keystore passwd", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_KEYSTORE_PASSWORD), "pa$$word");
		verify("JSR48 init truststore", EQUAL, client
				.getProperty(WBEMClientConstants.PROP_CLIENT_TRUSTSTORE), pathRelativeLong2);

		// Setting corresponding JSR48 properties affect SBLIM properties
		verify("JSR48 init enab con from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL), "FINER");
		verify("JSR48 init enab file from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL), "CONFIG");
		verify("JSR48 init byte limit from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_SIZE_LIMIT), "8192");
		verify("JSR48 init file loc from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathAbsolute2);
		verify("JSR48 init num files from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_COUNT), "4");
		verify("JSR48 init timeout from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.HTTP_TIMEOUT), "5000");
		verify("JSR48 init chunking from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.HTTP_USE_CHUNKING), "true");
		verify("JSR48 init keystore from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.KEYSTORE_PATH), pathRelativeLong1);
		verify("JSR48 init keystore passwd from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD), "pa$$word");
		verify("JSR48 init truststore from SBLIM", EQUAL, client
				.getProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH), pathRelativeLong2);

		// Try various combinations of log dir and file
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, null);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, null);
		verify("dir + file 1", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), "");
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file1);
		verify("dir + file 2", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), file1);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file2);
		verify("dir + file 3", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), file2);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, ".");
		verify("dir + file 4", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), "." + File.separator
				+ file2);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file1);
		verify("dir + file 5", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), "." + File.separator
				+ file1);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirRoot);
		verify("dir + file 6", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRoot1);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file2);
		verify("dir + file 7", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRoot2);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirAbsolute);
		verify("dir + file 8", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathAbsolute2);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file1);
		verify("dir + file 9", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathAbsolute1);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirRelative);
		verify("dir + file 10", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRelative1);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file2);
		verify("dir + file 11", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRelative2);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirAbsoluteLong);
		verify("dir + file 12", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathAbsoluteLong2);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file1);
		verify("dir + file 13", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathAbsoluteLong1);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirRelativeLong);
		verify("dir + file 14", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRelativeLong1);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file2);
		verify("dir + file 15", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRelativeLong2);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, null);
		verify("dir + file 16", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), dirRelativeLong
				+ File.separator);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, file1);
		verify("dir + file 17", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathRelativeLong1);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirAbsoluteLong + File.separator);
		verify("dir + file 18", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), pathAbsoluteLong1);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, null);
		verify("dir + file 19", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), file1);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, dirRoot);
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, null);
		verify("dir + file 20", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), dirRoot);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, ".." + File.separator);
		verify("dir + file 21", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), ".." + File.separator);
		client.setProperty(WBEMClientConstants.PROP_LOG_DIR, "");
		client.setProperty(WBEMClientConstants.PROP_LOG_FILENAME, "");
		verify("dir + file 22", EQUAL, client
				.getProperty(WBEMConfigurationProperties.LOG_FILE_LOCATION), "");

		// Try invalid JSR48 properties
		verify("Invalid get", EQUAL, client.getProperty(invalidProperty), null);
		try {
			client.setProperty(invalidProperty, file2);
			fail("Didn't fail even though property was invalid");
		} catch (IllegalArgumentException e) {
			verify("Invalid set", EQUAL, e.getMessage(), invalidProperty);
		}

		// Listener tests
		WBEMListener listener = WBEMListenerFactory
				.getListener(WBEMClientConstants.PROTOCOL_CIMXML);

		// All JSR48 properties should be defaults with new listener
		verify("Uninit keystore", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE), null);
		verify("Uninit keystore passwd", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD), "");
		verify("Uninit truststore", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_TRUSTSTORE), null);

		// Setting corresponding SBLIM properties affect JSR48 properties
		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PATH, pathAbsoluteLong1);
		listener.setProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD, "passw0rd");
		listener.setProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH, pathAbsoluteLong2);

		verify("SBLIM init keystore", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE), pathAbsoluteLong1);
		verify("SBLIM init keystore passwd", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD), "passw0rd");
		verify("SBLIM init truststore", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_TRUSTSTORE), pathAbsoluteLong2);

		// Set JSR48 properties directly
		listener.setProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE, pathRelativeLong1);
		listener.setProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD, "pa$$word");
		listener.setProperty(WBEMListenerConstants.PROP_LISTENER_TRUSTSTORE, pathRelativeLong2);

		verify("JSR48 init keystore", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE), pathRelativeLong1);
		verify("JSR48 init keystore passwd", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_KEYSTORE_PASSWORD), "pa$$word");
		verify("JSR48 init truststore", EQUAL, listener
				.getProperty(WBEMListenerConstants.PROP_LISTENER_TRUSTSTORE), pathRelativeLong2);

		// Setting corresponding JSR48 properties affect SBLIM properties
		verify("JSR48 init keystore from SBLIM", EQUAL, listener
				.getProperty(WBEMConfigurationProperties.KEYSTORE_PATH), pathRelativeLong1);
		verify("JSR48 init keystore passwd from SBLIM", EQUAL, listener
				.getProperty(WBEMConfigurationProperties.KEYSTORE_PASSWORD), "pa$$word");
		verify("JSR48 init truststore from SBLIM", EQUAL, listener
				.getProperty(WBEMConfigurationProperties.TRUSTSTORE_PATH), pathRelativeLong2);

		// Try invalid JSR48 properties
		verify("Invalid get", EQUAL, listener.getProperty(invalidProperty), null);
		try {
			listener.setProperty(invalidProperty, file2);
			fail("Didn't fail even though property was invalid");
		} catch (IllegalArgumentException e) {
			verify("Invalid set", EQUAL, e.getMessage(), invalidProperty);
		}
	}
}
