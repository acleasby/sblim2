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
 * @author : Alexander Wolf-Reber, IBM, a.wolf-reber@de.ibm.com
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1671502    2007-02-08  lupusalex    Remove dependency from Xerces
 * 1715027    2007-05-08  lupusalex    Make message id random
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 */

package org.sblim.cimclient.unittest.cimxml;

import java.io.ByteArrayOutputStream;

import javax.cim.CIMDataType;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;

import org.sblim.cimclient.internal.cimxml.CIMClientXML_HelperImpl;
import org.sblim.cimclient.internal.cimxml.CimXmlSerializer;
import org.sblim.cimclient.unittest.TestCase;
import org.w3c.dom.Document;

/**
 * Class CimXmlSerializerTest is responsible for testing the CimXmlSerializer
 * 
 */
public class CimXmlSerializerTest extends TestCase {

	/**
	 * Tests the CimXmlSerializer with a DOM document contains all kind of white
	 * space and special characters.
	 * 
	 * @throws Exception
	 */
	public void testSerializer() throws Exception {
		CIMClientXML_HelperImpl helper = new CIMClientXML_HelperImpl();
		helper.setId(0);
		Document doc = helper.newDocument();
		helper.createCIMMessage(doc, helper.associators_request(doc,
				new CIMObjectPath("http", "127.0.0.1", "5988", "root/cimv2", "CIM_ComputerSystem",
						new CIMProperty[] {
								new CIMProperty<String>("Name", CIMDataType.STRING_T,
										" the Name:<>&\"': ", true, false, null),
								new CIMProperty<String>("SystemName", CIMDataType.STRING_T,
										"  the  SystemName:\r\n\t\u001E:  ", true, false, null),
								new CIMProperty<String>("CreationClassName", CIMDataType.STRING_T,
										"   the   CCName:äöüßµ€©êéÉ:   ", true, false,
										null),
								new CIMProperty<String>("SystemCreationClassName",
										CIMDataType.STRING_T,
										"    the    SCCName:\u4EB0:\uD834\uDD1E:    ", true, false,
										null) }), "CIM_SystemDevice", "CIM_LogicalDevice",
				"System", "Device", false, false, new String[] { "OperationalState", "Capacity" }));
		ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
		CimXmlSerializer.serialize(stream, doc, false);
		String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<CIM CIMVERSION=\"2.0\" DTDVERSION=\"2.0\"><MESSAGE ID=\"0\" PROTOCOLVERSION=\"1.0\"><SIMPLEREQ><IMETHODCALL NAME=\"Associators\"><LOCALNAMESPACEPATH><NAMESPACE NAME=\"root\"/><NAMESPACE NAME=\"cimv2\"/></LOCALNAMESPACEPATH><IPARAMVALUE NAME=\"ObjectName\"><INSTANCENAME CLASSNAME=\"CIM_ComputerSystem\"><KEYBINDING NAME=\"CreationClassName\"><KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20; &#x20;the &#x20; CCName:äöüßµ€©êéÉ: &#x20;&#x20;</KEYVALUE></KEYBINDING><KEYBINDING NAME=\"Name\"><KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20;the Name:&lt;&gt;&amp;&quot;&apos;:&#x20;</KEYVALUE></KEYBINDING><KEYBINDING NAME=\"SystemCreationClassName\"><KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20; &#x20; the &#x20; &#x20;SCCName:\u4EB0:\uD834\uDD1E: &#x20; &#x20;</KEYVALUE></KEYBINDING><KEYBINDING NAME=\"SystemName\"><KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20; the &#x20;SystemName:&#xd;&#xa;&#x9;&#x1e;: &#x20;</KEYVALUE></KEYBINDING></INSTANCENAME></IPARAMVALUE><IPARAMVALUE NAME=\"AssocClass\"><CLASSNAME NAME=\"CIM_SystemDevice\"/></IPARAMVALUE><IPARAMVALUE NAME=\"ResultClass\"><CLASSNAME NAME=\"CIM_LogicalDevice\"/></IPARAMVALUE><IPARAMVALUE NAME=\"Role\"><VALUE>System</VALUE></IPARAMVALUE><IPARAMVALUE NAME=\"ResultRole\"><VALUE>Device</VALUE></IPARAMVALUE><IPARAMVALUE NAME=\"IncludeQualifiers\"><VALUE>false</VALUE></IPARAMVALUE><IPARAMVALUE NAME=\"IncludeClassOrigin\"><VALUE>false</VALUE></IPARAMVALUE><IPARAMVALUE NAME=\"PropertyList\"><VALUE.ARRAY><VALUE>OperationalState</VALUE><VALUE>Capacity</VALUE></VALUE.ARRAY></IPARAMVALUE></IMETHODCALL></SIMPLEREQ></MESSAGE></CIM>";
		verify("Plain XML output doesn't match expectation", EQUAL, stream.toString("UTF-8"),
				expectedResult);

		stream.reset();
		CimXmlSerializer.serialize(stream, doc, true);
		expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<CIM CIMVERSION=\"2.0\" DTDVERSION=\"2.0\">\n <MESSAGE ID=\"0\" PROTOCOLVERSION=\"1.0\">\n  <SIMPLEREQ>\n   <IMETHODCALL NAME=\"Associators\">\n    <LOCALNAMESPACEPATH>\n     <NAMESPACE NAME=\"root\"/>\n     <NAMESPACE NAME=\"cimv2\"/>\n    </LOCALNAMESPACEPATH>\n    <IPARAMVALUE NAME=\"ObjectName\">\n     <INSTANCENAME CLASSNAME=\"CIM_ComputerSystem\">\n      <KEYBINDING NAME=\"CreationClassName\">\n       <KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20; &#x20;the &#x20; CCName:äöüßµ€©êéÉ: &#x20;&#x20;</KEYVALUE>\n      </KEYBINDING>\n      <KEYBINDING NAME=\"Name\">\n       <KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20;the Name:&lt;&gt;&amp;&quot;&apos;:&#x20;</KEYVALUE>\n      </KEYBINDING>\n      <KEYBINDING NAME=\"SystemCreationClassName\">\n       <KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20; &#x20; the &#x20; &#x20;SCCName:\u4EB0:\uD834\uDD1E: &#x20; &#x20;</KEYVALUE>\n      </KEYBINDING>\n      <KEYBINDING NAME=\"SystemName\">\n       <KEYVALUE TYPE=\"string\" VALUETYPE=\"string\">&#x20; the &#x20;SystemName:&#xd;&#xa;&#x9;&#x1e;: &#x20;</KEYVALUE>\n      </KEYBINDING>\n     </INSTANCENAME>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"AssocClass\">\n     <CLASSNAME NAME=\"CIM_SystemDevice\"/>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"ResultClass\">\n     <CLASSNAME NAME=\"CIM_LogicalDevice\"/>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"Role\">\n     <VALUE>System</VALUE>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"ResultRole\">\n     <VALUE>Device</VALUE>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"IncludeQualifiers\">\n     <VALUE>false</VALUE>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"IncludeClassOrigin\">\n     <VALUE>false</VALUE>\n    </IPARAMVALUE>\n    <IPARAMVALUE NAME=\"PropertyList\">\n     <VALUE.ARRAY>\n      <VALUE>OperationalState</VALUE>\n      <VALUE>Capacity</VALUE>\n     </VALUE.ARRAY>\n    </IPARAMVALUE>\n   </IMETHODCALL>\n  </SIMPLEREQ>\n </MESSAGE>\n</CIM>";
		verify("Pretty XML output doesn't match expectation", EQUAL, stream.toString("UTF-8"),
				expectedResult);
	}

	/**
	 * Main for quick&dirty standalone test
	 * 
	 * @param args
	 *            Ignored
	 */
	public static void main(String[] args) {
		try {
			new CimXmlSerializerTest().testSerializer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
