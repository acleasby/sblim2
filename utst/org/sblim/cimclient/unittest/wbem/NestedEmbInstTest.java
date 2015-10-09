/**
 * (C) Copyright IBM Corp. 2013
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Endre Bak, ebak@de.ibm.com
 *           Dave Blaschke, blaschke@us.ibm.com
 * 
 * Flag       Date        Prog         Description
 * -------------------------------------------------------------------------------
 *    2636    2013-05-08  blaschke-oss Nested embedded instances cause CIMXMLParseException
 *    2637    2013-05-09  blaschke-oss Add nested embedded instance builder test
 */
package org.sblim.cimclient.unittest.wbem;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;

import org.sblim.cimclient.internal.cimxml.CIMClientXML_HelperImpl;
import org.sblim.cimclient.internal.cimxml.CimXmlSerializer;
import org.sblim.cimclient.unittest.TestCase;
import org.w3c.dom.Document;

/**
 * 
 * Class NestedEmbInstTest is responsible for testing nested embedded instances
 * (in this case, an instance within an instance within an instance)
 * 
 */
public class NestedEmbInstTest extends TestCase {

	private InputStream getInputStream(String pName) {
		InputStream is = getClass().getResourceAsStream(pName);
		verify("Failed to load resource! : " + pName, is != null);
		return is;
	}

	private InputStream getInstIS() {
		return getInputStream("data/NestedEmbInst.xml");
	}

	@SuppressWarnings("null")
	private void checkEnumInstResult(CloseableIterator<Object> pItr) {
		verify("CIMInstance is not retrieved!", pItr.hasNext());

		try {
			Object next = pItr.next();
			verify("Top level object not instance!", next instanceof CIMInstance);
			CIMInstance inst = (CIMInstance) next;
			verify("Top level instance incorrect!", inst.getClassName().equalsIgnoreCase(
					"LMI_StorageInstModification"));
			CIMProperty<?> prop = inst.getProperty("PreviousInstance");
			verify("Expected property not present in top level instance!", prop != null);
			Object obj = prop.getValue();
			verify("Top level instance property null!", obj != null);
			verify("First nested object not instance!", obj instanceof CIMInstance);
			inst = (CIMInstance) obj;
			verify("First nested object incorrect!", inst.getClassName().equalsIgnoreCase(
					"LMI_StorageJob"));
			prop = inst.getProperty("JobInParameters");
			verify("Expected property not present in first nested instance!", prop != null);
			obj = prop.getValue();
			verify("First nested instance property null!", obj != null);
			verify("Second nested object not instance!", obj instanceof CIMInstance);
			inst = (CIMInstance) obj;
			verify("Second nested object incorrect!", inst.getClassName().equalsIgnoreCase(
					"CIM_ManagedElement"));
			prop = inst.getProperty("Size");
			verify("Expected property not present in second nested instance!", prop != null);
			obj = prop.getValue();
			verify("Second nested instance property null!", obj != null);
			verify("Second nested instance property not uint64!", obj instanceof UnsignedInteger64);
			UnsignedInteger64 uint64 = (UnsignedInteger64) obj;
			verify("Second nested instance property uint64 incorrect!",
					uint64.intValue() == 10000000);
		} catch (Exception e) {
			try {
				throw pItr.getWBEMException();
			} catch (WBEMException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * testEnumInstancesDOM
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesDOM() throws Exception {
		checkEnumInstResult(Common.parseWithDOM(getInstIS()));
	}

	/**
	 * testEnumInstancesSAX
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesSAX() throws Exception {
		checkEnumInstResult(Common.parseWithSAX(getInstIS()));
	}

	/**
	 * testEnumInstancesPULL
	 * 
	 * @throws Exception
	 */
	public void testEnumInstancesPULL() throws Exception {
		checkEnumInstResult(Common.parseWithPULL(getInstIS()));
	}

	/**
	 * testNestedEmbInstBuilder
	 * 
	 * @throws Exception
	 */
	public void testNestedEmbInstBuilder() throws Exception {
		CIMProperty<String> prop4a = new CIMProperty<String>("CreationClassName",
				CIMDataType.STRING_T, "LMI_StorageExtent", true, false, null);
		CIMProperty<String> prop4b = new CIMProperty<String>("DeviceID", CIMDataType.STRING_T,
				"/dev/disk/by-id/scsi-0QEMU_QEMU_HARDDISK_drive-scsi0-0-1", true, false, null);
		CIMProperty<String> prop4c = new CIMProperty<String>("SystemCreationClassName",
				CIMDataType.STRING_T, "Linux_ComputerSystem", true, false, null);
		CIMProperty<String> prop4d = new CIMProperty<String>("SystemName", CIMDataType.STRING_T,
				"rawhide", true, false, null);
		CIMProperty<?>[] props4 = new CIMProperty[] { prop4a, prop4b, prop4c, prop4d };

		CIMProperty<CIMObjectPath> prop3a = new CIMProperty<CIMObjectPath>("Extent",
				new CIMDataType(""), new CIMObjectPath(null, null, null, "root/cimv2",
						"CIM_StorageExtent", props4));
		CIMProperty<UnsignedInteger64> prop3b = new CIMProperty<UnsignedInteger64>("Size",
				CIMDataType.UINT64_T, new UnsignedInteger64("10000000"));
		CIMProperty<?>[] props3 = new CIMProperty[] { prop3a, prop3b };
		CIMInstance inst3 = new CIMInstance(new CIMObjectPath(null, null, null, null,
				"CIM_ManagedElement", null), props3);

		CIMProperty<CIMInstance> prop2a = new CIMProperty<CIMInstance>("JobInParameters",
				CIMDataType.OBJECT_T, inst3);
		CIMProperty<String> prop2b = new CIMProperty<String>("InstanceID", CIMDataType.STRING_T,
				"LMI:LMI_StorageJob:1");
		CIMProperty<?>[] props2 = new CIMProperty[] { prop2a, prop2b };
		CIMInstance inst2 = new CIMInstance(new CIMObjectPath(null, null, null, null,
				"LMI_StorageJob", null), props2);

		CIMProperty<CIMInstance> prop1a = new CIMProperty<CIMInstance>("SourceInstance",
				CIMDataType.OBJECT_T, inst2);
		CIMProperty<String> prop1b = new CIMProperty<String>("SourceInstanceModelPath",
				CIMDataType.STRING_T,
				"//rawhide/root/cimv2:LMI_StorageJob.InstanceID=\"LMI:LMI_StorageJob:1\"");
		CIMProperty<?>[] props1 = new CIMProperty[] { prop1a, prop1b };
		CIMInstance inst1 = new CIMInstance(new CIMObjectPath(null, null, null, null,
				"LMI_StorageInstModification", null), props1);

		CIMClientXML_HelperImpl helper = new CIMClientXML_HelperImpl();
		helper.setId(3);
		Document doc = helper.newDocument();
		helper.createCIMMessage(doc, helper.createInstance_request(doc, new CIMObjectPath(null,
				null, null, null, "CIM_ManagedElement", null), inst3));
		ByteArrayOutputStream stream3 = new ByteArrayOutputStream(4096);
		CimXmlSerializer.serialize(stream3, doc, false);

		helper.setId(2);
		doc = helper.newDocument();
		helper.createCIMMessage(doc, helper.createInstance_request(doc, new CIMObjectPath(null,
				null, null, null, "LMI_StorageJob", null), inst2));
		ByteArrayOutputStream stream2 = new ByteArrayOutputStream(4096);
		CimXmlSerializer.serialize(stream2, doc, false);

		helper.setId(1);
		doc = helper.newDocument();
		helper.createCIMMessage(doc, helper.createInstance_request(doc, new CIMObjectPath(null,
				null, null, null, "LMI_StorageInstModification", null), inst1));
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream(4096);
		CimXmlSerializer.serialize(stream1, doc, false);

		String level1 = stream3.toString();
		String level2 = stream2.toString();
		String level3 = stream1.toString();

		String levelExpectedSubstring1 = "<PROPERTY NAME=\"Size\" TYPE=\"uint64\"><VALUE>10000000</VALUE></PROPERTY>";
		// levelExpectedSubstring2 is escaped version of levelExpectedSubstring1
		String levelExpectedSubstring2 = "&lt;PROPERTY NAME=&quot;Size&quot; TYPE=&quot;uint64&quot;&gt;&lt;VALUE&gt;10000000&lt;/VALUE&gt;&lt;/PROPERTY&gt;";
		// levelExpectedSubstring3 is escaped version of levelExpectedSubstring2
		String levelExpectedSubstring3 = "&amp;lt;PROPERTY NAME=&amp;quot;Size&amp;quot; TYPE=&amp;quot;uint64&amp;quot;&amp;gt;&amp;lt;VALUE&amp;gt;10000000&amp;lt;/VALUE&amp;gt;&amp;lt;/PROPERTY&amp;gt;";

		verify("1st level nesting error!", level1.indexOf(levelExpectedSubstring1) != -1);
		verify("2nd level nesting error!", level2.indexOf(levelExpectedSubstring2) != -1);
		verify("3rd level nesting error!", level3.indexOf(levelExpectedSubstring3) != -1);
	}
}
