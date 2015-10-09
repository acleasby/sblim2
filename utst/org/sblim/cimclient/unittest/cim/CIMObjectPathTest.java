/**
 * (C) Copyright IBM Corp. 2007, 2013
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
 * 1716991    2006-05-11  lupusalex    FVT: CIMObjectPath.equals() should ignore host name
 * 1723607    2007-05-22  ebak         IPv6 support in WBEM-URI strings
 * 1736318    2007-06-13  lupusalex    Wrong object path in HTTP header
 * 1917309    2008-03-27  raman_arora  "/root:__NAMESPACE" not valid CIMObjectPath
 * 2003590    2008-06-30  blaschke-oss Change licensing from CPL to EPL
 * 2204488 	  2008-10-28  raman_arora  Fix code to remove compiler warnings
 * 2524131    2009-01-21  raman_arora  Upgrade client to JDK 1.5 (Phase 1)
 * 2531371    2009-02-10  raman_arora  Upgrade client to JDK 1.5 (Phase 2)
 * 2797550    2009-06-01  raman_arora  JSR48 compliance - add Java Generics
 * 2944824    2010-02-08  blaschke-oss Missing getXmlSchemaName() in CIMObjectPath
 * 3374012    2011-07-24  blaschke-oss Sblim client CIMObjectPath class defect for LLA format URL
 * 3496349    2012-03-02  blaschke-oss JSR48 1.0.0: add CIMObjectPath getKeyValue
 * 3510090    2012-03-23  blaschke-oss Fix CIMObjectPath.toString() inconsistencies
 * 3521119    2012-04-24  blaschke-oss JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
 * 3529151    2012-08-22  blaschke-oss TCK: CIMInstance property APIs include keys from COP
 *    2660    2013-09-04  blaschke-oss CIMObjectPath.equalsModelPath same as equals
 */

package org.sblim.cimclient.unittest.cim;

import java.util.ArrayList;

import javax.cim.CIMDataType;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger32;

import org.sblim.cimclient.internal.util.MOF;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class CIMObjectPathTest is responsible for testing the CIMObjectPath class
 * 
 */
public class CIMObjectPathTest extends TestCase {

	/**
	 * testEquals
	 */
	public void testEquals() {

		CIMObjectPath path1 = new CIMObjectPath("http", "myhost.mydomain.com", "5988",
				"root/mynamespace", "CIM_MyClass", new CIMProperty[] { createProperty1a(),
						createProperty2a(), createProperty3() });

		verify("Identity", path1.equals(path1));

		CIMObjectPath path2 = new CIMObjectPath("https", "myotherhost.mydomain.com", "5989",
				"root/mynamespace", "CIM_MyClass", new CIMProperty[] { createProperty1a(),
						createProperty2a(), createProperty3() });

		verify("Hostname ignored", path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/mynamespace",
				"CIM_MyClass", new CIMProperty[] { createProperty1b(), createProperty2a(),
						createProperty3() });

		verify("Unimportant differences", path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/mynamespace",
				"CIM_MyClass", new CIMProperty[] { createProperty1c(), createProperty2a(),
						createProperty3() });

		verify("Key value differs", !path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/mynamespace",
				"CIM_MyClass", new CIMProperty[] { createProperty1d(), createProperty2a(),
						createProperty3() });

		verify("Key name differs", !path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/mynamespace",
				"CIM_MyClass", new CIMProperty[] { createProperty1a(), createProperty2b(),
						createProperty3() });

		verify("Key array value differs", !path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/mynamespace",
				"CIM_MyClass", new CIMProperty[] { createProperty1a(), createProperty2a() });

		verify("Key count differs", !path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/othernamespace",
				"CIM_MyClass", new CIMProperty[] { createProperty1a(), createProperty2a(),
						createProperty3() });

		verify("Namespace differs", !path1.equals(path2));

		path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988", "root/mynamespace",
				"CIM_OtherClass", new CIMProperty[] { createProperty1a(), createProperty2a(),
						createProperty3() });

		verify("Classname differs", !path1.equals(path2));
	}

	/**
	 * testEqualsModelPath
	 */
	public void testEqualsModelPath() {
		CIMObjectPath path1 = new CIMObjectPath("http", "myhost.mydomain.com", "5988",
				"root/mynamespace", "CIM_MyClass", new CIMProperty[] { createProperty1a(),
						createProperty2a(), createProperty3() });

		CIMObjectPath path2 = new CIMObjectPath("http", "myhost.mydomain.com", "5988",
				"root/myothernamespace", "CIM_MyClass", new CIMProperty[] { createProperty1a(),
						createProperty2a(), createProperty3() });

		verify("Object names do not differ", !path1.equals(path2));
		verify("Model paths differ", path1.equalsModelPath(path2));
	}

	/*
	 * Negative test for CIMObjectPath constructor
	 */
	private void negativeConstructor(String pObjectName) {
		try {
			new CIMObjectPath(pObjectName);
			verify("IllegalArgumentException was not thrown for invalid char in object name : "
					+ pObjectName + " !", false);
		} catch (IllegalArgumentException e) {
			debug(e.getMessage());
		}
	}

	/**
	 * Tests the full constructor and the URI-String constructor.
	 */
	public void testConstructors() {
		ArrayList<OPChecker> opCheckerAL = buildOPCheckers();
		for (int i = 0; i < opCheckerAL.size(); i++) {
			OPChecker chk = opCheckerAL.get(i);
			String msg = chk.checkCtors();
			verify(msg, msg == null);
		}

		// Negative test for CIMObjectPath constructor
		for (int i = 0; i < negObjectName_A.length; i++)
			negativeConstructor(negObjectName_A[i]);

		// Test xmlSchemaName (currently unused)
		String xmlSchemaName = "http://www.w3.org/2001/XMLSchema-instance";
		CIMObjectPath cop = new CIMObjectPath(null, null, null, "root/cimv2", null, null,
				xmlSchemaName);
		verify("XmlSchemaName differs", xmlSchemaName.equals(cop.getXmlSchemaName()));
	}

	// These COPs are examples from DSP0004, DSP0207, etc. with slight
	// modifications to match the client's functionality, i.e. true instead of
	// True, 42 instead of 0x2A, 'A' instead of '\x41', sorted keys, etc.
	//
	// NOTE: UNTYPEDPATH_A AND TYPEDPATH_A ENTRIES MUST MATCH UP!!! In other
	// words, they must be identical except for presence/absence of typing.
	private static final String[] UNTYPEDPATH_A = {
			"http://myserver.acme.com/root/cimv2:ACME_LogicalDisk.Drive=\"C\",SystemName=\"acme\"",
			"//myserver.acme.com:5988/root/cimv2:ACME_BooleanKeyClass.KeyProp=true",
			"/root/cimv2:ACME_IntegerKeyClass.KeyProp=42",
			"ACME_CharKeyClass.KeyProp='A'",
			"http://myserver.org:5066/root/cimv2:My_ComputerSystem.CreationClassName=\"My_ComputerSystem\",Name=\"mycmp\"",
			"http://myserver.org/root/cimv2:My_ComputerSystem.CreationClassName=\"My_ComputerSystem\",Name=\"mycmp\"",
			"//myserver.org/root/cimv2:My_ComputerSystem", "/root/cimv2:My_ComputerSystem",
			"//www.acme.com/root/cimv2", "//www.acme.com/root/cimv2:CIM_RegisteredProfile",
			"https://jdd:test@acme.com:5959/cimv2:CIM_RegisteredProfile",
			"https://jdd:test@acme.com:5959/cimv2:CIM_RegisteredProfile.InstanceID=\"acme:1\"" };

	private static final String[] TYPEDPATH_A = {
			"http://myserver.acme.com/root/cimv2/(instance)ACME_LogicalDisk.Drive=(string)\"C\",SystemName=(string)\"acme\"",
			"//myserver.acme.com:5988/root/cimv2/(instance)ACME_BooleanKeyClass.KeyProp=(boolean)true",
			"/root/cimv2/(instance)ACME_IntegerKeyClass.KeyProp=(uint8)42",
			"/(instance)ACME_CharKeyClass.KeyProp=(char16)'A'",
			"http://myserver.org:5066/root/cimv2/(instance)My_ComputerSystem.CreationClassName=(string)\"My_ComputerSystem\",Name=(string)\"mycmp\"",
			"http://myserver.org/root/cimv2/(instance)My_ComputerSystem.CreationClassName=(string)\"My_ComputerSystem\",Name=(string)\"mycmp\"",
			"//myserver.org/root/cimv2/(class)My_ComputerSystem",
			"/root/cimv2/(class)My_ComputerSystem",
			"//www.acme.com/root/cimv2/(namespace)",
			"//www.acme.com/root/cimv2/(class)CIM_RegisteredProfile",
			"https://jdd:test@acme.com:5959/cimv2/(class)CIM_RegisteredProfile",
			"https://jdd:test@acme.com:5959/cimv2/(instance)CIM_RegisteredProfile.InstanceID=(string)\"acme:1\"" };

	/**
	 * Tests the toString method and MOF.objectHandle to make sure that what
	 * goes in matches what comes out.
	 */
	public void testObjectPaths() {
		String inStr, outStr;
		CIMObjectPath cop;

		verify("Untyped/typed arrays differ in size", UNTYPEDPATH_A.length == TYPEDPATH_A.length);

		for (int i = 0; i < UNTYPEDPATH_A.length; i++) {
			inStr = UNTYPEDPATH_A[i];
			cop = new CIMObjectPath(inStr);
			outStr = MOF.objectHandle(cop, false, false);
			verify("Untyped in/out differs: " + inStr + " != " + outStr, inStr.equals(outStr));
		}

		for (int i = 0; i < TYPEDPATH_A.length; i++) {
			inStr = TYPEDPATH_A[i];
			cop = new CIMObjectPath(inStr);
			outStr = MOF.objectHandle(cop, true, false);
			verify("Typed in/out differs: " + inStr + " != " + outStr, inStr.equals(outStr));
		}

		for (int i = 0; i < UNTYPEDPATH_A.length; i++) {
			inStr = UNTYPEDPATH_A[i];
			cop = new CIMObjectPath(inStr);
			outStr = MOF.objectHandle(cop, true, false);
			verify("Untyped in/typed out differs: " + inStr + " != " + outStr, TYPEDPATH_A[i]
					.equals(outStr));
		}

		for (int i = 0; i < TYPEDPATH_A.length; i++) {
			inStr = TYPEDPATH_A[i];
			cop = new CIMObjectPath(inStr);
			outStr = MOF.objectHandle(cop, false, false);
			verify("Typed in/untyped out differs: " + inStr + " != " + outStr, UNTYPEDPATH_A[i]
					.equals(outStr));
		}

		for (int i = 0; i < UNTYPEDPATH_A.length; i++) {
			inStr = UNTYPEDPATH_A[i];
			cop = new CIMObjectPath(inStr);
			outStr = cop.toString();
			verify("Untyped in/toString differs: " + inStr + " != " + outStr, inStr.equals(outStr));
		}
	}

	private CIMProperty<String> createProperty1a() {
		return new CIMProperty<String>("Name", CIMDataType.STRING_T, "A name", true, true,
				"MyClass");
	}

	private CIMProperty<String> createProperty1b() {
		return new CIMProperty<String>("nAME", CIMDataType.STRING_T, "A name", true, false,
				"AnotherClass");
	}

	private CIMProperty<String> createProperty1c() {
		return new CIMProperty<String>("Name", CIMDataType.STRING_T, "Another name", true, true,
				"MyClass");
	}

	private CIMProperty<String> createProperty1d() {
		return new CIMProperty<String>("NameX", CIMDataType.STRING_T, "A name", true, true,
				"MyClass");
	}

	private CIMProperty<UnsignedInteger32[]> createProperty2a() {
		return new CIMProperty<UnsignedInteger32[]>("States", CIMDataType.UINT32_ARRAY_T,
				new UnsignedInteger32[] { new UnsignedInteger32(1), new UnsignedInteger32(2) },
				true, true, "MyClass");
	}

	private CIMProperty<UnsignedInteger32[]> createProperty2b() {
		return new CIMProperty<UnsignedInteger32[]>("States", CIMDataType.UINT32_ARRAY_T,
				new UnsignedInteger32[] { new UnsignedInteger32(1), new UnsignedInteger32(25) },
				true, true, "MyClass");
	}

	private CIMProperty<UnsignedInteger32> createProperty3() {
		return new CIMProperty<UnsignedInteger32>("Size", CIMDataType.UINT32_T,
				new UnsignedInteger32(100), true, false, "MyClass");
	}

	/**
	 * @param pMsg
	 */
	static void debug(String pMsg) {
	// System.out.println(pMsg);
	}

	/*
	 * CIMObjectPath( String scheme, String host, String port, String namespace,
	 * String objectName, CIMProperty[] keys )
	 */

	/*
	 * WBEM-URI-TypedNamespacePath = namespacePath "/(namespace)" namespacePath
	 * = [namespaceType ":"] namespaceHandle namespaceType = ("http" ["s"]) /
	 * ("cimxml.wbem" ["s"]) namespaceHandle = ["//" authority] "/"
	 * [namespaceName] authority = [ userinfo "@" ] host [ ":" port ]
	 * namespaceName = IDENTIFIER *("/"IDENTIFIER)
	 * 
	 * empty namespace : //(namespace) //bela@hello.com/root/cimv2/(namespace)
	 * host with empty namespace //bela@hello.com//(namespace)
	 */

	private static final String[] SCHEME_A = { null, "http", "https" };

	private static final String[] HOST_A = { null, "MyHost", "me@MyHost",
			"www.YourHost.anything.org", "you@www.YourHost.anything.org", "129.42.60.212", "[::]",
			"[::55aa]", "[::129.42.60.212]", "[::1e5f:55aa]", "[::de2a:129.42.60.212]",
			"[::de2a:1e5f:55aa]", "[::031c:de2a:129.42.60.212]", "[::031c:de2a:1e5f:55aa]",
			"[::f5a3:031c:de2a:129.42.60.212]", "[::f5a3:031c:de2a:1e5f:55aa]",
			"[::0B:f5a3:031c:de2a:129.42.60.212]", "[::0B:f5a3:031c:de2a:1e5f:55aa]",
			"[::1:0B:f5a3:031c:de2a:129.42.60.212]", "[::1:0B:f5a3:de2a:1e5f:55aa]",
			"[23a:1:0B:f5a3:031c:de2a:129.42.60.212]", "[23a:1:0B:f5a3:031c:de2a:1e5f:55aa]",
			"[23a:1:0B:f5a3:031c:de2a::]", "[23a:1:0B:f5a3:031c:de2a:1e5f::]",
			"[23a:1:0B:f5a3:031c::]", "[23a:1:0B:f5a3::]", "[23a:1:0B::]", "[23a:1::]", "[23a::]",
			"[23a:1:0B:f5a3:031c::129.42.60.212]", "[23a:1:0B:f5a3:031c::1e5f:55aa]",
			"[23a:1:0B:f5a3::129.42.60.212]", "[23a:1:0B:f5a3::1e5f:55aa]",
			"[23a:1:0B::129.42.60.212]", "[23a:1:0B::1e5f:55aa]", "[23a:1::129.42.60.212]",
			"[23a:1::1e5f:55aa]", "[23a::129.42.60.212]", "[23a::1e5f:55aa]",
			"[23a::0B:f5a3:031c:de2a:129.42.60.212]", "[23a::0B:f5a3:031c:de2a:1e5f:55aa]",
			"[23a::f5a3:031c:de2a:129.42.60.212]", "[23a::f5a3:031c:de2a:1e5f:55aa]",
			"[23a::de2a:129.42.60.212]", "[23a::de2a:1e5f:55aa]", "[23a::129.42.60.212]",
			"[23a::1e5f:55aa]", "[23a::55aa]", "[23a:1::f5a3:031c:de2a:129.42.60.212]",
			"[23a:1::f5a3:031c:de2a:1e5f:55aa]", "[23a:1::f5a3:031c:de2a:129.42.60.212]",
			"[23a:1::f5a3:031c:de2a:1e5f:55aa]", "[23a:1::031c:de2a:129.42.60.212]",
			"[23a:1::031c:de2a:1e5f:55aa]", "[23a:1::de2a:129.42.60.212]",
			"[23a:1::de2a:1e5f:55aa]", "[23a:1::129.42.60.212]", "[23a:1::1e5f:55aa]",
			"[23a:1::55aa]", "[23a:1::]", "[23a:1:0B:f5a3:031c:de2a:1e5f:55aa%eth0]",
			"[23a:1:0B:f5a3:031c::1e5f:55aa%eth1]", "[23a::1e5f:55aa%2]",
			"[23a:1::1e5f:55aa%pvc1.3]", "[23a:1::55aa%interface4]" };

	private static final String[] PORT_A = { null, "5898" };

	private static final String[] NAMESPACE_A = { "root/cimv2" };

	private static final String[] OBJNAME_A = { null, "CIM_TestClass", "_CIM_TestClass",
			"CIM_TestClass2" };

	private static final String[] negObjectName_A = { "/root:1CIM_MyClass", "/root:1CIM_1MyClass",
			"/root:$CIM_MyClass", "/root:_CIM_$MyClass", "/root:CIM_$MyClass" };

	private static final CIMProperty<?>[][] KEYS_A = {
			null,
			new CIMProperty[] {
					new CIMProperty<CIMObjectPath>("RefKey", new CIMDataType("TestClass"),
							new CIMObjectPath(null, null, null, "root/cimv2", "TestClass",
									new CIMProperty[] { new CIMProperty<String>("KeyProp",
											CIMDataType.STRING_T, "OK", true, false, null) }),
							true, false, null),
					new CIMProperty<CIMObjectPath>(
							"RefKey2",
							new CIMDataType("TestClass"),
							new CIMObjectPath(
									null,
									null,
									null,
									"root/cimv2",
									"TestClass",
									new CIMProperty[] {
											new CIMProperty<CIMObjectPath>(
													"RefKey",
													new CIMDataType("SubClass"),
													new CIMObjectPath(
															null,
															"[23a:1::129.42.60.212]",
															null,
															"root/cimv2",
															"SubClass",
															new CIMProperty[] { new CIMProperty<String>(
																	"KeyProp",
																	CIMDataType.STRING_T, "OK",
																	true, false, null) }), true,
													false, null),
											new CIMProperty<String>("KeyProp",
													CIMDataType.STRING_T, "OK", true, false, null) }),
							true, false, null) } };

	/*
	 * CIMObjectPath( String scheme, String host, String port, String namespace,
	 * String objectName, CIMProperty[] keys )
	 */
	private static class OPChecker {

		private String iScheme, iHost, iPort, iNameSpace, iObjName;

		private CIMProperty<?>[] iKeys;

		private CIMObjectPath iRefPath;

		/**
		 * Ctor.
		 * 
		 * @param pScheme
		 * @param pHost
		 * @param pPort
		 * @param pNameSpace
		 * @param pObjName
		 * @param pKeys
		 */
		public OPChecker(String pScheme, String pHost, String pPort, String pNameSpace,
				String pObjName, CIMProperty<?>[] pKeys) {
			this.iScheme = pScheme;
			this.iHost = pHost;
			this.iPort = pPort;
			this.iNameSpace = pNameSpace;
			this.iObjName = pObjName;
			this.iKeys = pKeys;
			this.iRefPath = new CIMObjectPath(this.iScheme, this.iHost, this.iPort,
					this.iNameSpace, this.iObjName, this.iKeys);
		}

		private boolean equals(String pStr0, String pStr1) {
			return pStr0 == null ? pStr1 == null : pStr0.equalsIgnoreCase(pStr1);
		}

		private void equals(StringBuffer pBuf, String pOPName, String pMethodName, String pStrRef,
				String pStr) {
			if (!equals(pStrRef, pStr)) pBuf.append(pOPName + '.' + pMethodName + "() = " + pStr
					+ " != " + pStrRef + " !!!\n");
		}

		@SuppressWarnings("null")
		private void equals(StringBuffer pBuf, String pOPName, CIMObjectPath pOP) {
			CIMProperty<?>[] keys = pOP.getKeys();
			int refLen = this.iKeys == null ? 0 : this.iKeys.length;
			int len = keys == null ? 0 : keys.length;
			if (refLen != len) {
				pBuf.append(pOPName + ".getKeys().length = " + len + " != " + refLen + " !!!\n");
				return;
			}
			for (int i = 0; i < len; i++) {
				CIMProperty<?> refKey = this.iKeys[i], key = keys[i];
				if (!refKey.equals(key)) pBuf.append(pOPName + ": idx:" + i + " refKey!=key !!!\n"
						+ "refKey:" + refKey + "\n   key:" + key + "\n");
				if (!pOP.getKeyValue(refKey.getName()).equals(pOP.getKeyValue(key.getName()))) pBuf
						.append(pOPName + ": idx:" + i
								+ " getKeyValue(refKey)!=getKeyValue(key) !!!\n"
								+ "getKeyValue(refKey):" + pOP.getKeyValue(refKey.getName())
								+ "\n   getKeyValue(key):" + pOP.getKeyValue(key.getName()) + "\n");
			}
		}

		private String checkGetters(String pName, CIMObjectPath pOP) {
			StringBuffer strBuf = new StringBuffer();
			equals(strBuf, pName, "getScheme", this.iScheme, pOP.getScheme());
			equals(strBuf, pName, "getHost", this.iHost, pOP.getHost());
			equals(strBuf, pName, "getPort", this.iPort, pOP.getPort());
			equals(strBuf, pName, "getNamespace", this.iNameSpace, pOP.getNamespace());
			equals(strBuf, pName, "getObjectName", this.iObjName, pOP.getObjectName());
			equals(strBuf, pName, "getXmlSchemaName", null, pOP.getXmlSchemaName());
			equals(strBuf, pName, pOP);
			return strBuf.length() == 0 ? null : strBuf.toString();
		}

		/**
		 * checkCtors
		 * 
		 * @return String
		 */
		public String checkCtors() {
			String msg = checkGetters("iRefPath", this.iRefPath);
			if (msg != null) return msg;

			String untypedStr = MOF.objectHandle(this.iRefPath, false, false);
			debug("untypedStr:" + untypedStr);
			CIMObjectPath objPathUntypedStr = new CIMObjectPath(untypedStr);
			msg = checkGetters("objPathUntypedStr", objPathUntypedStr);
			if (msg != null) return msg;

			String typedStr = MOF.objectHandle(this.iRefPath, true, false);
			debug("typedStr:" + typedStr);
			CIMObjectPath objPathTypedStr = new CIMObjectPath(typedStr);
			msg = checkGetters("objPathTypedStr", objPathTypedStr);
			if (msg != null) return msg;

			return null;
		}

	}

	private void buildOPCheckers(ArrayList<OPChecker> pAL, String pScheme, String pHost,
			String pPort) {
		for (int nsIdx = 0; nsIdx < NAMESPACE_A.length; nsIdx++) {
			String nameSpace = NAMESPACE_A[nsIdx];
			for (int onIdx = 0; onIdx < OBJNAME_A.length; onIdx++) {
				String objName = OBJNAME_A[onIdx];
				if (objName == null) {
					pAL.add(new OPChecker(pScheme, pHost, pPort, nameSpace, objName, null));
				} else {
					for (int keysIdx = 0; keysIdx < KEYS_A.length; keysIdx++) {
						CIMProperty<?>[] keys = KEYS_A[keysIdx];
						pAL.add(new OPChecker(pScheme, pHost, pPort, nameSpace, objName, keys));
					}
				}
			}
		}
	}

	private ArrayList<OPChecker> buildOPCheckers() {
		ArrayList<OPChecker> al = new ArrayList<OPChecker>(128);
		for (int schIdx = 0; schIdx < SCHEME_A.length; schIdx++) {
			String scheme = SCHEME_A[schIdx];
			for (int hostIdx = 0; hostIdx < HOST_A.length; hostIdx++) {
				String host = HOST_A[hostIdx];
				if (host == null) {
					buildOPCheckers(al, scheme, null, null);
				} else {
					for (int portIdx = 0; portIdx < PORT_A.length; portIdx++) {
						String port = PORT_A[portIdx];
						buildOPCheckers(al, scheme, host, port);
					}
				}
			}

		}
		return al;
	}

}
