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
 * 1565892    2006-11-23  lupusalex    Make SBLIM client JSR48 compliant
 * 1698278    2006-04-11  lupusalex    Unit tests fail on Hungarian locale
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 3484014    2012-02-03  blaschke-oss Add LogAndTraceBroker.isLoggable for message/trace
 * 3554738    2012-08-16  blaschke-oss dump CIM xml by LogAndTraceBroker.trace()
 * 3562235    2012-08-27  blaschke-oss LogAndTraceBrokerTest breaks unit test tracing
 */

package org.sblim.cimclient.unittest.logging;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import org.sblim.cimclient.CIMXMLTraceListener;
import org.sblim.cimclient.LogListener;
import org.sblim.cimclient.TraceListener;
import org.sblim.cimclient.WBEMConfigurationProperties;
import org.sblim.cimclient.internal.logging.LogAndTraceBroker;
import org.sblim.cimclient.internal.logging.Messages;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class LogAndTraceBrokerTest is responsible to test the LogAndTraceBroker
 * 
 * NOTE: Because this test changes log and trace properties, handlers, etc. in
 * order to test LogAndTraceBroker, it must be the last test executed in order
 * for log and trace to work correctly during unit test
 */
public class LogAndTraceBrokerTest extends TestCase {

	private static final String TEST = "TEST";

	/**
	 * SUPPORTED_LANGUAGES
	 * 
	 * @invariant The array must be sorted in ascending order
	 */
	protected static final String[] SUPPORTED_LANGUAGES = new String[] { "de", "en" };

	private int iLogMessages = 0;

	private int iTraceMessages = 0;

	private int iCIMXMLTraceMessages = 0;

	// message, message, trace, trace, register internal listener

	/**
	 * Returns logMessages
	 * 
	 * @return The value of logMessages.
	 */
	public int getLogMessages() {
		return this.iLogMessages;
	}

	/**
	 * Sets logMessages
	 * 
	 * @param pLogMessages
	 *            The new value of logMessages.
	 */
	public void setLogMessages(int pLogMessages) {
		this.iLogMessages = pLogMessages;
	}

	/**
	 * Returns traceMessages
	 * 
	 * @return The value of traceMessages.
	 */
	public int getTraceMessages() {
		return this.iTraceMessages;
	}

	/**
	 * Sets traceMessages
	 * 
	 * @param pTraceMessages
	 *            The new value of traceMessages.
	 */
	public void setTraceMessages(int pTraceMessages) {
		this.iTraceMessages = pTraceMessages;
	}

	/**
	 * Returns CIMXMLTraceMessages
	 * 
	 * @return The value of CIMXMLTraceMessages.
	 */
	public int getCIMXMLTraceMessages() {
		return this.iCIMXMLTraceMessages;
	}

	/**
	 * Sets CIMXMLTraceMessages
	 * 
	 * @param pCIMXMLTraceMessages
	 *            The new value of CIMXMLTraceMessages.
	 */
	public void setCIMXMLTraceMessages(int pCIMXMLTraceMessages) {
		this.iCIMXMLTraceMessages = pCIMXMLTraceMessages;
	}

	/**
	 * Tests if all three internal listeners come up correctly
	 */
	public void testInternalListeners() {

		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearLogListeners();
		broker.clearTraceListeners();

		System.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "WARNING");
		System.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "WARNING");
		System.setProperty(WBEMConfigurationProperties.TRACE_FILE_LEVEL, "WARNING");
		broker.registerInternalListeners();

		List<?> listeners = broker.getLogListeners();
		verify("Count of internal log listeners", EQUAL, new Integer(listeners.size()),
				new Integer(2));
		listeners = broker.getTraceListeners();
		verify("Count of internal trace listeners", EQUAL, new Integer(listeners.size()),
				new Integer(1));

		// Make sure reregistration doesn't register two copies
		broker.registerInternalListeners();

		listeners = broker.getLogListeners();
		verify("Count of internal log listeners after reregister", EQUAL, new Integer(listeners
				.size()), new Integer(2));
		listeners = broker.getTraceListeners();
		verify("Count of internal trace listeners after reregister", EQUAL, new Integer(listeners
				.size()), new Integer(1));

		verify("testInternalListeners: isLoggableMessage(OFF) != false", broker
				.isLoggableMessage(Level.OFF) == false);
		verify("testInternalListeners: isLoggableTrace(OFF) != false", broker
				.isLoggableTrace(Level.OFF) == false);
		verify("testInternalListeners: isLoggableMessage(+1) != true", broker
				.isLoggableMessage(Level.SEVERE) == true);
		verify("testInternalListeners: isLoggableTrace(+1) != true", broker
				.isLoggableTrace(Level.SEVERE) == true);
		verify("testInternalListeners: isLoggableMessage(==) != true", broker
				.isLoggableMessage(Level.WARNING) == true);
		verify("testInternalListeners: isLoggableTrace(==) != true", broker
				.isLoggableTrace(Level.WARNING) == true);
		verify("testInternalListeners: isLoggableMessage(-1) != false", broker
				.isLoggableMessage(Level.INFO) == false);
		verify("testInternalListeners: isLoggableTrace(-1) != false", broker
				.isLoggableTrace(Level.INFO) == false);

		System.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "OFF");
		System.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "OFF");
		System.setProperty(WBEMConfigurationProperties.TRACE_FILE_LEVEL, "OFF");
		broker.clearLogListeners();
		broker.clearTraceListeners();
	}

	/**
	 * Tests if external listeners come up correctly
	 */
	public void testExternalListeners() {

		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearLogListeners();
		broker.clearTraceListeners();

		System.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "WARNING");
		System.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "WARNING");
		System.setProperty(WBEMConfigurationProperties.TRACE_FILE_LEVEL, "WARNING");

		broker.addLogListener(new LogListener() {

			public void log(Level pLevel, String pMessageKey, String pMessage, Object[] pParameters) {
				String s = "log(" + pLevel.intValue() + "," + pMessageKey + "," + pMessage + ","
						+ pParameters.length + ")";
				verify("log(4)", NOT_EQUAL, s, null);
			}
		});

		broker.addTraceListener(new TraceListener() {

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage,
					Throwable pThrown) {
				String s = "trace(" + pLevel.intValue() + "," + pOrigin.toString() + "," + pMessage
						+ "," + pThrown.getMessage() + ")";
				verify("trace(4)", NOT_EQUAL, s, null);
			}

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage) {
				String s = "trace(" + pLevel.intValue() + "," + pOrigin.toString() + "," + pMessage
						+ ")";
				verify("trace(3)", NOT_EQUAL, s, null);
			}
		});

		List<?> listeners = broker.getLogListeners();
		verify("Count of external log listeners", EQUAL, new Integer(listeners.size()),
				new Integer(1));
		listeners = broker.getTraceListeners();
		verify("Count of external trace listeners", EQUAL, new Integer(listeners.size()),
				new Integer(1));

		verify("testExternalListeners: isLoggableMessage(OFF) != false", broker
				.isLoggableMessage(Level.OFF) == false);
		verify("testExternalListeners: isLoggableTrace(OFF) != false", broker
				.isLoggableTrace(Level.OFF) == false);
		verify("testExternalListeners: isLoggableMessage(+1) != true", broker
				.isLoggableMessage(Level.SEVERE) == true);
		verify("testExternalListeners: isLoggableTrace(+1) != true", broker
				.isLoggableTrace(Level.SEVERE) == true);
		verify("testExternalListeners: isLoggableMessage(==) != true", broker
				.isLoggableMessage(Level.WARNING) == true);
		verify("testExternalListeners: isLoggableTrace(==) != true", broker
				.isLoggableTrace(Level.WARNING) == true);
		verify("testExternalListeners: isLoggableMessage(-1) != true", broker
				.isLoggableMessage(Level.INFO) == true);
		verify("testExternalListeners: isLoggableTrace(-1) != true", broker
				.isLoggableTrace(Level.INFO) == true);

		System.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "OFF");
		System.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "OFF");
		System.setProperty(WBEMConfigurationProperties.TRACE_FILE_LEVEL, "OFF");
		broker.clearLogListeners();
		broker.clearTraceListeners();
	}

	/**
	 * Tests if all three internal listeners and external listeners come up
	 * correctly and can be removed individually
	 */
	public void testListeners() {

		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearLogListeners();
		broker.clearTraceListeners();

		System.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "WARNING");
		System.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "WARNING");
		System.setProperty(WBEMConfigurationProperties.TRACE_FILE_LEVEL, "WARNING");
		broker.registerInternalListeners();

		LogListener logL = new LogListener() {

			public void log(Level pLevel, String pMessageKey, String pMessage, Object[] pParameters) {
				String s = "log(" + pLevel.intValue() + "," + pMessageKey + "," + pMessage + ","
						+ pParameters.length + ")";
				verify("log(4)", NOT_EQUAL, s, null);
			}
		};
		broker.addLogListener(logL);

		TraceListener traceL = new TraceListener() {

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage,
					Throwable pThrown) {
				String s = "trace(" + pLevel.intValue() + "," + pOrigin.toString() + "," + pMessage
						+ "," + pThrown.getMessage() + ")";
				verify("trace(4)", NOT_EQUAL, s, null);
			}

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage) {
				String s = "trace(" + pLevel.intValue() + "," + pOrigin.toString() + "," + pMessage
						+ ")";
				verify("trace(3)", NOT_EQUAL, s, null);
			}
		};
		broker.addTraceListener(traceL);

		// At this point, should have the three internal and two external
		// listeners
		List<?> listeners = broker.getLogListeners();
		verify("Count of log listeners", EQUAL, new Integer(listeners.size()), new Integer(3));
		listeners = broker.getTraceListeners();
		verify("Count of trace listeners", EQUAL, new Integer(listeners.size()), new Integer(2));

		LogListener logDummy = new LogListener() {

			public void log(Level pLevel, String pMessageKey, String pMessage, Object[] pParameters) {
				String s = "dummy log(" + pLevel.intValue() + "," + pMessageKey + "," + pMessage
						+ "," + pParameters.length + ")";
				verify("log(4)", NOT_EQUAL, s, null);
			}
		};

		broker.removeLogListener(logDummy);

		// At this point, should still have the three internal and two external
		// listeners (previous removeLogListener did nothing)
		listeners = broker.getLogListeners();
		verify("Count of log listeners", EQUAL, new Integer(listeners.size()), new Integer(3));
		listeners = broker.getTraceListeners();
		verify("Count of trace listeners", EQUAL, new Integer(listeners.size()), new Integer(2));

		verify("testListeners1: isLoggableMessage(OFF) != false", broker
				.isLoggableMessage(Level.OFF) == false);
		verify("testListeners1: isLoggableTrace(OFF) != false",
				broker.isLoggableTrace(Level.OFF) == false);
		verify("testListeners1: isLoggableMessage(+1) != true", broker
				.isLoggableMessage(Level.SEVERE) == true);
		verify("testListeners1: isLoggableTrace(+1) != true",
				broker.isLoggableTrace(Level.SEVERE) == true);
		verify("testListeners1: isLoggableMessage(==) != true", broker
				.isLoggableMessage(Level.WARNING) == true);
		verify("testListeners1: isLoggableTrace(==) != true",
				broker.isLoggableTrace(Level.WARNING) == true);
		verify("testListeners1: isLoggableMessage(-1) != true", broker
				.isLoggableMessage(Level.INFO) == true);
		verify("testListeners1: isLoggableTrace(-1) != true",
				broker.isLoggableTrace(Level.INFO) == true);

		broker.removeLogListener(logL);
		broker.removeTraceListener(traceL);

		// At this point, should have the three internal listeners
		listeners = broker.getLogListeners();
		verify("Count of log listeners - external", EQUAL, new Integer(listeners.size()),
				new Integer(2));
		listeners = broker.getTraceListeners();
		verify("Count of trace listeners - external", EQUAL, new Integer(listeners.size()),
				new Integer(1));

		verify("testListeners2: isLoggableMessage(+1) != true", broker
				.isLoggableMessage(Level.SEVERE) == true);
		verify("testListeners2: isLoggableTrace(+1) != true",
				broker.isLoggableTrace(Level.SEVERE) == true);
		verify("testListeners2: isLoggableMessage(==) != true", broker
				.isLoggableMessage(Level.WARNING) == true);
		verify("testListeners2: isLoggableTrace(==) != true",
				broker.isLoggableTrace(Level.WARNING) == true);
		verify("testListeners2: isLoggableMessage(-1) != false", broker
				.isLoggableMessage(Level.INFO) == false);
		verify("testListeners2: isLoggableTrace(-1) != false",
				broker.isLoggableTrace(Level.INFO) == false);

		List<LogListener> logListeners = broker.getLogListeners();
		Iterator<LogListener> iterL = logListeners.iterator();
		while (iterL.hasNext()) {
			broker.removeLogListener(iterL.next());
		}

		List<TraceListener> traceListeners = broker.getTraceListeners();
		Iterator<TraceListener> iterT = traceListeners.iterator();
		while (iterT.hasNext()) {
			broker.removeTraceListener(iterT.next());
		}

		// At this point, should have no listeners
		listeners = broker.getLogListeners();
		verify("Count of log listeners - all", EQUAL, new Integer(listeners.size()), new Integer(0));
		listeners = broker.getTraceListeners();
		verify("Count of trace listeners - all", EQUAL, new Integer(listeners.size()), new Integer(
				0));

		verify("testListeners3: isLoggableMessage(+1) != false", broker
				.isLoggableMessage(Level.SEVERE) == false);
		verify("testListeners3: isLoggableTrace(+1) != false",
				broker.isLoggableTrace(Level.SEVERE) == false);
		verify("testListeners3: isLoggableMessage(==) != false", broker
				.isLoggableMessage(Level.WARNING) == false);
		verify("testListeners3: isLoggableTrace(==) != false", broker
				.isLoggableTrace(Level.WARNING) == false);
		verify("testListeners3: isLoggableMessage(-1) != false", broker
				.isLoggableMessage(Level.INFO) == false);
		verify("testListeners3: isLoggableTrace(-1) != false",
				broker.isLoggableTrace(Level.INFO) == false);

		System.setProperty(WBEMConfigurationProperties.LOG_CONSOLE_LEVEL, "OFF");
		System.setProperty(WBEMConfigurationProperties.LOG_FILE_LEVEL, "OFF");
		System.setProperty(WBEMConfigurationProperties.TRACE_FILE_LEVEL, "OFF");
		broker.clearLogListeners();
		broker.clearTraceListeners();
	}

	/**
	 * Tests if a log message is forwarded correctly to the log&trace listeners
	 * This test checks the message level, locale, parameter.
	 */
	public void testMessage1() {
		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearLogListeners();
		broker.clearTraceListeners();

		broker.addLogListener(new LogListener() {

			/**
			 * @param pParameters
			 */
			public void log(Level pLevel, String pMessageKey, String pMessage, Object[] pParameters) {
				setLogMessages(getLogMessages() + 1);
				if (pMessageKey.equals(Messages.TEST_MESSAGE)) {
					verify("Message level", EQUAL, pLevel, Level.SEVERE);
					verify("Message param", EQUAL, pParameters[0], TEST);
					final String messageLanguage = pMessage.substring(0, 2);
					verifyLanguage(messageLanguage);
				}
			}
		});

		broker.addTraceListener(new TraceListener() {

			/**
			 * @param pLevel
			 * @param pOrigin
			 * @param pMessage
			 * @param pThrown
			 */
			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage,
					Throwable pThrown) {
				setTraceMessages(getTraceMessages() + 1);
			}

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage) {
				setTraceMessages(getTraceMessages() + 1);
				if (pMessage.startsWith(Messages.TEST_MESSAGE)) {
					verify("Message level", EQUAL, pLevel, Level.SEVERE);
					verify("Message locale", EQUAL, pMessage.substring(Messages.TEST_MESSAGE
							.length() + 1, Messages.TEST_MESSAGE.length() + 3), Locale.ENGLISH
							.getLanguage());
					verify("Message param", EQUAL, pMessage.substring(Messages.TEST_MESSAGE
							.length() + 4), TEST);
					verify("Origin", EQUAL, pOrigin.getMethodName(), "testMessage1");
				}
			}
		});

		setLogMessages(0);
		setTraceMessages(0);
		broker.message(Messages.TEST_MESSAGE, TEST);
		verify("Message count logged", EQUAL, new Integer(getLogMessages()), new Integer(1));
		verify("Message count traced", EQUAL, new Integer(getTraceMessages()), new Integer(1));

		broker.clearLogListeners();
		broker.clearTraceListeners();
	}

	/**
	 * Tests if a log message is forwarded correctly to the log&trace listeners
	 * This test checks the message level, locale, exception parameter.
	 */
	public void testMessage2() {
		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearLogListeners();
		broker.clearTraceListeners();

		broker.addLogListener(new LogListener() {

			/**
			 * @param pParameters
			 */
			public void log(Level pLevel, String pMessageKey, String pMessage, Object[] pParameters) {
				setLogMessages(getLogMessages() + 1);
				if (pMessageKey.equals(Messages.TEST_MESSAGE)) {
					verify("Message level", EQUAL, pLevel, Level.SEVERE);
					final String messageLanguage = pMessage.substring(0, 2);
					verifyLanguage(messageLanguage);
				}
			}
		});

		broker.addTraceListener(new TraceListener() {

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage,
					Throwable pThrown) {
				setTraceMessages(getTraceMessages() + 1);
				if (pMessage.startsWith(Messages.TEST_MESSAGE)) {
					verify("Message level", EQUAL, pLevel, Level.SEVERE);
					verify("Message locale", EQUAL, pMessage.substring(Messages.TEST_MESSAGE
							.length() + 1, Messages.TEST_MESSAGE.length() + 3), Locale.ENGLISH
							.getLanguage());
					verify("Message param", EQUAL, pThrown.getMessage(), TEST);
					verify("Origin", EQUAL, pOrigin.getMethodName(), "testMessage2");
				}
			}

			/**
			 * @param pLevel
			 * @param pOrigin
			 * @param pMessage
			 */
			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage) {
				setTraceMessages(getTraceMessages() + 1);
			}
		});

		setLogMessages(0);
		setTraceMessages(0);
		broker.message(Messages.TEST_MESSAGE, new RuntimeException(TEST));
		verify("Message count logged", EQUAL, new Integer(getLogMessages()), new Integer(1));
		verify("Message count traced", EQUAL, new Integer(getTraceMessages()), new Integer(1));

		broker.clearLogListeners();
		broker.clearTraceListeners();
	}

	/**
	 * Tests if a log message is forwarded correctly to the log&trace listeners
	 * This test checks the message level, locale, exception parameter.
	 */
	public void testTrace() {
		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearLogListeners();
		broker.clearTraceListeners();

		broker.addLogListener(new LogListener() {

			/**
			 * @param pLevel
			 * @param pMessageKey
			 * @param pMessage
			 * @param pParameters
			 */
			public void log(Level pLevel, String pMessageKey, String pMessage, Object[] pParameters) {
				setLogMessages(getLogMessages() + 1);
			}
		});

		broker.addTraceListener(new TraceListener() {

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage,
					Throwable pThrown) {
				setTraceMessages(getTraceMessages() + 1);
				if (pMessage.startsWith(TEST)) {
					verify("Message level", EQUAL, pLevel, Level.FINE);
					verify("Message text", EQUAL, pMessage, TEST);
					verify("Message param", EQUAL, pThrown.getMessage(), TEST);
					verify("Origin", EQUAL, pOrigin.getMethodName(), "testTrace");
				}
			}

			public void trace(Level pLevel, StackTraceElement pOrigin, String pMessage) {
				setTraceMessages(getTraceMessages() + 1);
				if (pMessage.startsWith(TEST)) {
					verify("Message level", EQUAL, pLevel, Level.FINEST);
					verify("Message text", EQUAL, pMessage, TEST);
					verify("Origin", EQUAL, pOrigin.getMethodName(), "testTrace");
				}
			}
		});

		setLogMessages(0);
		setTraceMessages(0);
		broker.trace(Level.FINEST, TEST);
		broker.trace(Level.FINE, TEST, new RuntimeException(TEST));
		verify("Message count logged", EQUAL, new Integer(getLogMessages()), new Integer(0));
		verify("Message count traced", EQUAL, new Integer(getTraceMessages()), new Integer(2));

		broker.clearLogListeners();
		broker.clearTraceListeners();
	}

	/**
	 * Tests if a log message is forwarded correctly to the log&trace listeners
	 * This test checks the message level, locale, exception parameter.
	 */
	public void testCIMXMLTrace() {
		LogAndTraceBroker broker = LogAndTraceBroker.getBroker();
		broker.clearCIMXMLTraceListeners();

		verify("before: isLoggableCIMXMLTrace(OFF) != false", broker
				.isLoggableCIMXMLTrace(Level.OFF) == false);
		verify("before: isLoggableCIMXMLMessage(+1) != false", broker
				.isLoggableCIMXMLTrace(Level.SEVERE) == false);

		broker.addCIMXMLTraceListener(new CIMXMLTraceListener() {

			public void traceCIMXML(Level pLevel, String pMessage, boolean pOutgoing) {
				setCIMXMLTraceMessages(getCIMXMLTraceMessages() + 1);
				verify("Message level", EQUAL, pLevel, Level.FINEST);
				verify("Message text", EQUAL, pMessage, TEST);
				verify("Outgoing flag", pOutgoing == true);
			}
		});

		verify("after: isLoggableCIMXMLTrace(OFF) != false", broker
				.isLoggableCIMXMLTrace(Level.OFF) == false);
		verify("after: isLoggableCIMXMLMessage(+1) != true", broker
				.isLoggableCIMXMLTrace(Level.SEVERE) == true);

		setCIMXMLTraceMessages(0);
		broker.traceCIMXML(Level.FINEST, TEST, true);
		verify("Message count logged", EQUAL, new Integer(getCIMXMLTraceMessages()), new Integer(1));

		broker.clearCIMXMLTraceListeners();
	}

	protected void verifyLanguage(final String messageLanguage) {
		final String jvmLanguage = Locale.getDefault().getLanguage();
		if (Arrays.binarySearch(SUPPORTED_LANGUAGES, jvmLanguage) >= 0) {
			verify("Message locale", EQUAL, messageLanguage, jvmLanguage);
		} else {
			warning("Unsupported language: " + jvmLanguage);
			verify("Message locale", EQUAL, messageLanguage, Locale.ENGLISH.getLanguage());
		}
	}
}
