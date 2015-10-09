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
 * 1712656    2007-05-04  ebak         Correct type identification for SVC CIMOM
 * 1827728    2007-11-20  ebak         rework: embeddedInstances: attribute EmbeddedObject not set
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2763216    2009-04-14  blaschke-oss Code cleanup: visible spelling/grammar errors
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 */

package org.sblim.cimclient.unittest.wbem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.sblim.cimclient.internal.cimxml.CIMMessage;
import org.sblim.cimclient.internal.cimxml.CIMResponse;
import org.sblim.cimclient.internal.cimxml.CIMXMLParseException;
import org.sblim.cimclient.internal.cimxml.CIMXMLParserImpl;
import org.sblim.cimclient.internal.cimxml.LocalPathBuilder;
import org.sblim.cimclient.internal.cimxml.sax.XMLDefaultHandlerImpl;
import org.sblim.cimclient.internal.cimxml.sax.node.CIMNode;
import org.sblim.cimclient.internal.cimxml.sax.node.IMethodCallNode;
import org.sblim.cimclient.internal.cimxml.sax.node.MessageNode;
import org.sblim.cimclient.internal.cimxml.sax.node.MethodCallNode;
import org.sblim.cimclient.internal.cimxml.sax.node.SimpleReqNode;
import org.sblim.cimclient.internal.logging.LogAndTraceBroker;
import org.sblim.cimclient.internal.wbem.CloseableIteratorDOM;
import org.sblim.cimclient.internal.wbem.CloseableIteratorPULL;
import org.sblim.cimclient.internal.wbem.CloseableIteratorSAX;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Class Common stores commonly used methods and constants.
 */
public class Common {

	/**
	 * LOCALPATH
	 */
	public static final LocalPathBuilder LOCALPATH = new LocalPathBuilder(new CIMObjectPath(null,
			"LocalHost", null, "root/cimv2", null, null));

	/**
	 * parseWithDOM
	 * 
	 * @param pIs
	 * @return CloseableIterator
	 * @throws Exception
	 */
	public static CloseableIterator<Object> parseWithDOM(InputStream pIs) throws Exception {
		return new CloseableIteratorDOM(new InputStreamReader(pIs), LOCALPATH.getBasePath());
	}

	/**
	 * parseWithSAX
	 * 
	 * @param pIs
	 * @return CloseableIterator
	 * @throws Exception
	 */
	public static CloseableIterator<Object> parseWithSAX(InputStream pIs) throws Exception {
		return new CloseableIteratorSAX(new InputStreamReader(pIs), LOCALPATH.getBasePath());
	}

	/**
	 * parseWithPULL
	 * 
	 * @param pIs
	 * @return CloseableIterator
	 * @throws Exception
	 */
	public static CloseableIterator<Object> parseWithPULL(InputStream pIs) throws Exception {
		return new CloseableIteratorPULL(new InputStreamReader(pIs), LOCALPATH.getBasePath());
	}

	/**
	 * getSingleResponse
	 * 
	 * @param pStream
	 * @param pLocalPath
	 * @return CIMResponse
	 * @throws WBEMException
	 */
	public static CIMResponse getSingleResponse(InputStreamReader pStream, CIMObjectPath pLocalPath)
			throws WBEMException {
		final LogAndTraceBroker logger = LogAndTraceBroker.getBroker();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom;
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(new InputSource(pStream));
		} catch (Exception e) {
			String msg = "Exception occurred during DOM parsing!";
			logger.trace(Level.SEVERE, msg, e);
			throw new WBEMException(WBEMException.CIM_ERR_FAILED, msg, null, e);
		}
		CIMXMLParserImpl.setLocalObjectPath(pLocalPath);
		CIMMessage cimMsg;
		try {
			cimMsg = CIMXMLParserImpl.parseCIM(dom.getDocumentElement());
		} catch (CIMXMLParseException e) {
			String msg = "Exception occurred during parseCIM!";
			logger.trace(Level.SEVERE, msg, e);
			throw new WBEMException(WBEMException.CIM_ERR_FAILED, msg, null, e);
		}
		if (!(cimMsg instanceof CIMResponse)) {
			String msg = "CIM message must be response!";
			logger.trace(Level.SEVERE, msg);
			throw new WBEMException(msg);
		}
		return (CIMResponse) cimMsg;
	}

	/**
	 * getIMethodCallNode
	 * 
	 * @param pIS
	 * @return IMethodCallNode
	 * @throws Exception
	 */
	public static IMethodCallNode getIMethodCallNode(InputStream pIS) throws Exception {
		SimpleReqNode reqNode = getSimpleReqNode(pIS);
		return (IMethodCallNode) reqNode.getAbstractMethodCallNode();
	}

	/**
	 * getMethodCallNode
	 * 
	 * @param pIS
	 * @return MethodCallNode
	 * @throws Exception
	 */
	public static MethodCallNode getMethodCallNode(InputStream pIS) throws Exception {

		SimpleReqNode reqNode = getSimpleReqNode(pIS);
		return (MethodCallNode) reqNode.getAbstractMethodCallNode();
	}

	private static SimpleReqNode getSimpleReqNode(InputStream pIS) throws Exception {
		XMLDefaultHandlerImpl handler = new XMLDefaultHandlerImpl(LOCALPATH.getBasePath());
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(new InputSource(pIS), handler);
		CIMNode cimNode = handler.getCIMNode();
		MessageNode msgNode = cimNode.getMessageNode();
		return (SimpleReqNode) msgNode.getAbstractMessageNode();
	}

}
