%define name                    sblim-cim-client2
%define project_folder			%{name}-%{version}-src
%define archive_folder			build
%define version                 2.2.5
%define release                 1jpp
%define section                 free

# -----------------------------------------------------------------------------

Name:           %{name}
Version:        %{version}
Release:        %{release}
Epoch:          0
License:        Eclipse Public License
Url:            http://sblim.sourceforge.net/
Group:          Development/Libraries/Java
Vendor:         IBM
Distribution:   JPackage
Summary:        Java CIM Client library

BuildRoot:      %{_tmppath}/%{name}-%{version}-buildroot
SOURCE:         %{name}-%{version}-src.tar.bz2

BuildArch:      noarch

BuildRequires:  jpackage-utils >= 0:1.5.32
BuildRequires:  ant >= 0:1.6

Requires:       jpackage-utils >= 0:1.5.32

%description
The purpose of this package is to provide a CIM Client Class Library for Java
applications. It complies to the DMTF standard CIM Operations over HTTP and 
intends to be compatible with JCP JSR48 once it becomes available. To learn
more about DMTF visit http://www.dmtf.org.
More infos about the Java Community Process and JSR48 can be found at
http://www.jcp.org and http://www.jcp.org/en/jsr/detail?id=48.

# -----------------------------------------------------------------------------

%package javadoc
Summary:        Javadoc for %{name}
Group:          Development/Documentation

%description javadoc
Javadoc for %{name}.

# -----------------------------------------------------------------------------

%package manual
Summary:        Manual and sample code for %{name}
Group:          Development/Documentation

%description manual
Manual and sample code for %{name}.

# -----------------------------------------------------------------------------

%prep
%setup -q -n %{project_folder}

dos2unixConversion() {
	fileName=$1
	%{__sed} -i 's/\r//g' "$fileName"
}

dosFiles2unix() {
	fileList=$1
	for fileName in $fileList; do
		dos2unixConversion $fileName
	done
}

dosFiles2unix 'COPYING sblim-cim-client2.properties sblim-slp-client2.properties'

# -----------------------------------------------------------------------------

%build
# export CLASSPATH=$(build-classpath)
export ANT_OPTS="-Xmx256m"
ant \
        -Dbuild.compiler=modern \
        -DManifest.version=%{version}\
        package java-doc

# -----------------------------------------------------------------------------

%install
RPM_BUILD_ROOT=`echo "$RPM_BUILD_ROOT" | sed -r 's/\/+$//'`
if [ -z $RPM_BUILD_ROOT ]; then
	echo "RPM_BUILD_ROOT must be set."
fi

dirs='/ /bin /dev /home /mnt /sbin /srv /var /boot /etc /lib /media /opt /root /selinux /usr'
for dir in $dirs; do
	if [ "$RPM_BUILD_ROOT" = "$dir" ]; then
		echo "$dir for RPM_BUILD_ROOT is evil!"
		exit 1
	fi
done

rm -rf $RPM_BUILD_ROOT

# --- documentation ---
dstDocDir=$RPM_BUILD_ROOT%{_docdir}/%{name}-%{version}
install -d $dstDocDir
install --mode=644 ChangeLog COPYING README NEWS $dstDocDir

# --- samples (also into _docdir) ---
cp -pr  smpl/org $dstDocDir

# --- config files ---
confDir=$RPM_BUILD_ROOT%{_sysconfdir}/java
install -d $confDir
install --mode=664 sblim-cim-client2.properties sblim-slp-client2.properties $confDir

# --- jar ---
install -d $RPM_BUILD_ROOT%{_javadir}
install %{archive_folder}/lib/%{name}-%{version}.jar $RPM_BUILD_ROOT%{_javadir}/%{name}-%{version}.jar
(
  cd $RPM_BUILD_ROOT%{_javadir} && 
    ln -sf %{name}-%{version}.jar %{name}.jar;
)

# --- javadoc ---
install -d $RPM_BUILD_ROOT%{_javadocdir}/%{name}-%{version}
cp -pr %{archive_folder}/doc/* $RPM_BUILD_ROOT%{_javadocdir}/%{name}-%{version}



# -----------------------------------------------------------------------------

%files
# %defattr(0644,root,root,0755)
%defattr(0644,root,root)
%config %{_sysconfdir}/java/sblim-cim-client2.properties
%config %{_sysconfdir}/java/sblim-slp-client2.properties
%doc %{_docdir}/%{name}-%{version}/COPYING
%doc %{_docdir}/%{name}-%{version}/README
%doc %{_docdir}/%{name}-%{version}/ChangeLog
%doc %{_docdir}/%{name}-%{version}/NEWS
%{_javadir}/%{name}.jar
%{_javadir}/%{name}-%{version}.jar


%files javadoc
%defattr(0644,root,root,0755)
%{_javadocdir}/%{name}-%{version}


%files manual
%defattr(0644,root,root,0755)
# %doc %{_docdir}/%{name}-%{version}/README.samples
%doc %{_docdir}/%{name}-%{version}/COPYING
%doc %{_docdir}/%{name}-%{version}/org


# -----------------------------------------------------------------------------

%changelog
* Fri Dec 13 2013 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.2.5
  o 2717 Update detailed release history HTML for 2.2.5
  o 2716 Sync up javax.* javadoc with JSR48 1.0.0 Final V
  o 2719 TCK: CIM APIs should not generate NullPointerException
  o 2594 CR28: Support CIMErrorDescription HTTP field
  o 2718 Bad CIMStatusCode generates NumberFormatException
  o 2538 CR14: Support new CORRELATOR element
  o 2715 Add VALUE.NULL support
  o 2714 Add detailed CIM-XML parser test based on DSP0201 2.4
  o 2713 Enforce loose validation of CIM-XML documents
  o 2712 SimpleReqNode allows any CIM element as child
  o 2709 Lower the level of the EOF message to FINE
  o 2711 LOCALNAMESPACEPATH allows 0 NAMESPACE children
  o 2710 parseVALUEOBJECTWITH(LOCAL)PATH ignores (LOCAL)CLASSPATH child
  o 2708 CIMNode quietly ignores DECLARATION child
  o 2707 INSTANCENAME ignores KEYVALUE and VALUE.REFERENCE children
  o 2706 Bad PARAMETER.REFARRAY ARRAYSIZE generates NumberFormatException
  o 2705 PARAMETER.ARRAY does not require TYPE attribute
  o 2704 PARAMETER does not require TYPE attribute
  o 2703 MethodNode should not require TYPE attribute
  o 2702 Bad PROPERTY.ARRAY ARRAYSIZE generates NumberFormatException
  o 2701 PROPERTY.ARRAY does not require TYPE attribute
  o 2700 PROPERTY does not require TYPE attribute
  o 2699 parseQUALIFIER does not require TYPE attribute
  o 2697 (I)MethodResponseNode allows ERROR with PARAMVALUE
  o 2696 parseIRETURNVALUE ignores VALUE and VALUE.ARRAY
  o 2695 parseMETHODCALL allows LOCALCLASSPATH and LOCALINSTANCEPATH
  o 2694 NAME attribute not required by DOM parser (part 2)
  o 2693 ReturnValueNode allows invalid PARAMTYPE attribute
  o 2691 RETURNVALUE should not require PARAMTYPE attribute
  o 2537 Add new data types for PARAMVALUE
  o 2690 Remove RESPONSEDESTINATION support
  o 2689 createMETHODCALL should not add PARAMTYPE attribute
  o 2688 parseMETHODCALL looks for CIMName attribute instead of NAME
  o 2687 ExpParamValueNode allows VALUE, (I)METHODRESPONSE children
  o 2686 parseEXPPARAMVALUE allows 2+ children, prohibits 0
  o 2685 Element.getAttribute returns empty string if no attribute
  o 2684 parseEXPMETHODRESPONSE has several issues
  o 2683 KEYVALUE VALUETYPE optional, "string" default
  o 2682 (I)MethodCallNode allows no LOCAL*PATH
  o 2681 parseQUALIFIERDECLARATION does not require TYPE attribute
  o 2680 IPARAMVALUE parsing broken on DOM/SAX
  o 2679 parseIMETHODCALL requires one IPARAMVALUE child element
  o 2678 parseMULTI___ allows one SIMPLE___ child element
  o 2677 ObjectPathNode allows all child nodes
  o 2676 parseMULTI(EXP)REQ looking for wrong child elements
  o 2675 CIMXMLParseException messages should contain element name
  o 2674 Null pointer exception in CIMDateTime(String)
  o 2673 NameSpaceNode does not need testCompletness()
  o 2672 Remove SIMPLEREQACK support
  o 2671 Potential null pointer exception in parseERROR
  o 2670 NAME attribute not required by DOM parser
  o 2669 Potential null pointer exception in parseMESSAGE
  o 2668 Potential null pointer exception in parseCIM
  o 2666 CR12: Remove ENUMERATIONCONTEXT

* Fri Sep 13 2013 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.2.4
  o 2661 Update detailed release history HTML for 2.2.4
  o 2662 Need the specific SSLHandshakeException during the cim call
  o 2660 CIMObjectPath.equalsModelPath same as equals
  o 2657 Potential null pointer exception in handleConnection
  o 2151 gzip compression not supported
  o 2655 Content-length must be ignored when Transfer-encoding present
  o 2651 IOException when tracing the cimxml
  o 2654 Check jcc idle time with CIMOM keepalive timeout to avoid EOF
  o 2653 FVT: java.lang.ExceptionInInitializerError during static init
  o 2652 LogAndTraceBroker.setXmlTraceStream should not close previous stream
  o 2650 SLP opaque value handling incorrect
  o 2604 SAXException messages should contain node name
  o 2647 Add two ssl protocol properties for http server and client
   
* Fri May 31 2013 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.2.3
  o 2643 Update detailed release history HTML for 2.2.3
  o 2642 Seperate properties needed for cim client and listener to filter out ciphers
  o 2635 Slowloris DoS attack for CIM indication listener port
  o 2640 Multiple CDATA parsing broken in DOM parser
  o 2639 CDATA parsing broken in PULL parser
  o 2638 Do not build empty REFERENCECLASS
  o 2637 Add nested embedded instance builder test
  o 2636 Nested embedded instances cause CIMXMLParseException
  o 2632 Potential Null Point Exception in CIMDataType
  o 2605 SAX parser throws wrong exception
  o 2628 Limit size of LinkedList of CIMEvents to be dispatched
  
* Fri Mar 15 2013 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.2.2
  o 2624 Update detailed release history HTML for 2.2.2
  o 2618 Need to add property to disable weak cipher suites for the secure indication
  o 2623 Reflect SourceForge upgrade in documentation
  o 2616 Add new API WBEMClientSBLIM.sendIndication()
  o 2621 Not all chunked input has trailers
  o 2620 Chunked output broken
  o 2619 Host should contain port when not 5988/5989
  o 2615 Add Allura links to detailed release history HTML
  o 2614 Remove redundant code in transmitRequest
  o 3602604 Clean up SAXException messages
  o 3601894 Enhance HTTP and CIM-XML tracing
  o 3598613 different data type in cim instance and cim object path
  o 3596303 windows http response WWW-Authenticate: Negotiate fails 

* Fri Dec 14 2012 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.2.1
  o 3584119 Update detailed release history HTML for 2.2.1
  o 3592502 Enhance CIMDataType unit test
  o 3588558 An enhancement on Java CIM Client logging
  o 3557283 Print full response when get EOF from CIMOM
  o 3576396 Improve logging of config file name
  o 3572993 parseDouble("2.2250738585072012e-308") DoS vulnerability
  o 3567433 Add links to top of detailed release history HTML

* Fri Sep 14 2012 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.2.0
  o 3567429 Update detailed release history HTML for 2.2.0
  o 3555752 Sync up javax.* javadoc with JSR48 1.0.0 Final IV
  o 3565581 TCK: remove unnecessary overriding methods
  o 3562235 LogAndTraceBrokerTest breaks unit test tracing
  o 3536399 Add client/listener peer authentication properties
  o 3529151 TCK: CIMInstance property APIs include keys from COP
  o 3536398 Update HTML to reflect secure indication support
  o 3554738 dump CIM xml by LogAndTraceBroker.trace()
  o 3553858 Append duplicate HTTP header fields instead of replace
  o 3535383 HashDoS fix 3498482
  o 3545797 Support new error code of SFCB
  o 3529066 Add Jsr48IndicationTester
  o 3524050 Improve WWW-Authenticate in HTTPClient.java
  o 3529065 Enable WBEMListener get/setProperty
  o 3529062 WBEMListenerFactory should return new instance
  o 3527580 WBEMClient should not throw IllegalArgumentException
  o 3526681 CIMError valid status codes out-of-date
  o 3526679 DOM parser ignores ERROR node CODE
  o 3526675 Unit test fails on Java 7
  o 3525914 TCK: SetPropertyTest.testSetProperty failing
  o 3521157 JSR48 1.0.0: PROP_ENABLE_*_LOGGING is Level, not 0/1
  o 3525657 Sync up javax.* javadoc with JSR48 1.0.0 Final III
  o 3525150 Remove CIMGetClassOp.getPropertyLis
  o 3525145 Remove CIMDeleteNameSpaceOp.getNamespace
  o 3525138 Remove WBEMConstants.PROTOCOL_CIMXML
  o 3525135 Remove CIMResponse.isSuccessul
  o 3525128 Remove WBEMTransportException/WBEMAuthenticationException
  o 3523918 "java.io.IOException: Unexpected EOF" returned as HTTP 401
  o 3522904 Add new API WBEMClientSBLIM.isActive()
  o 3521328 JSR48 1.0.0: remove WBEMClient associators and references
  o 3521119 JSR48 1.0.0: remove CIMObjectPath 2/3/4-parm ctors
  o 3521131 Sync up javax.* javadoc with JSR48 1.0.0 Final II
  o 3466280 get instance failure for CIM_IndicationSubscription
  o 3513228 Reliable Indications support can create lots of threads
  o 3517503 Missing parm in CIMDataType ctor javadoc
  o 3516848 enumerateNamespaces() method to WBEMClient
  o 3515180 JSR48 log dir/file should handle UNIX/Win separators
  o 3514685 TCK: getProperty must return default values
  o 3514537 TCK: execQueryInstances requires boolean, not Boolean
  o 3513357 Handle multiple CDATAs in CimXmlSerializer
  o 3513347 TCK: CIMObjectPath allows empty string
  o 3513343 TCK: CIMObjectPath must validate XML schema name
  o 3513349 TCK: CIMDataType must not accept null string
  o 3513353 TCK: CIMDataType arrays must have length >= 1
  o 3511454 SAX nodes not reinitialized properly
  o 3510090 Fix CIMObjectPath.toString() inconsistencies
  o 3510321 Handle CDATA in CimXmlSerializer
  o 3505681 Add detailed release history HTML
  o 3500619 JSR48 1.0.0: CIMClass association/key clean up

* Thu Mar 15 2012 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.12
  o 3504344 Sync Experimental/HEAD branches
  o 3504304 Rename socket timeout variables
  o 3498482 Red Hat: Possible XML Hash DoS in sblim
  o 3496355 JSR48 1.0.0: add new WBEMClientConstants
  o 3496385 JSR48 1.0.0: add WBEMListener get/setProperty
  o 3496380 JSR48 1.0.0: add new WBEMListenerConstants
  o 3496349 JSR48 1.0.0: add CIMObjectPath getKeyValue
  o 3496343 JSR48 1.0.0: deprecate WBEMClient associators and references
  o 3496301 Sync up javax.* javadoc with JSR48 1.0.0 Final
  o 3495662 Invalid HTML from HttpConnectionHandler.writeError
  o 3477298 Error compiling JSR48
  o 3489638 PERF: Bottleneck in LogAndTraceBroker.java - getCaller()
  o 3492224 Need two different timeouts for Socket connections
  o 3492214 Add a SenderIPAddress property indications
  o 3492246 Rename new indication trace property
  o 3490355 TCK: Cannot instantiate WBEMClientFactory
  o 3490032 TCK: WBEMException must validate error ID
  o 3490009 TCK: Too many WBEMListenerFactory class methods
  o 3484022 Turn reliable indication mode on and off based on SC/SN
  o 3485074 An Indication trace request
  o 3477087 Need Access to an Indication Sender's IP Address
  o 3484014 Add LogAndTraceBroker.isLoggable for message/trace
  o 3480115 Add Jsr48SfcbIndicationSample
  o 3469427 Fix broken HTML links
  o 3469210 Include reliable indications in HTML
  o 3469018 Properties not passed to CIMIndicationHandler
  
* Thu Dec 15 2011 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.11
  o 3459036 Linked list for RI queue not efficient for many LDs
  o 3444912 Client delay during SSL handshake
  o 3423064 Add UpdateExpiredPassword Header for Reqs from Java Client
  o 3411944 createJavaObject exception with hex uint
  o 3411879 TCK: CIM element value must match type
  o 3410126 TCK: CIM element name cannot be null
  
* Thu Sep 15 2011 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.10
  o 3409691 2.1.10 packaging issues: rpmbuild broken on Red Hat
  o 3406275 HEAD branch ONLY: sync with Experimental branch
  o 3400209 Highlighted Static Analysis (PMD) issues
  o 3397922 support OctetString
  o 3390724 Problem with Reliable Indication support in the Listener
  o 3374012 Sblim client CIMObjectPath class defect for LLA format URL
  o 3376657 Get reliable indication properties once
  o 3374206 NullPointerException caused by Indication
  o 3323310 Need the ability to override certain Global Properties
  
* Wed Jun 15 2011 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.9
  o 3311279 Repeated Instantiation of SAXParserFactory
  o 3304953 Indication URL mapped to lower case
  o 3304058 Use same date format in change history
  o 3288721 Need the function of indication reordering
  o 3206904 Indication listener deadlock causes JVM to run out sockets
  o 3297028 Instances contain CIMClassProperty with DOM parser
  o 3293248 Support for CIM_ERROR instances within ERROR
  o 3281781 fail to parse Embedded Instance parameter
  o 3277928 CIM-XML tracing cannot be enabled in the field
  o 3267429 Samples should close client
  o 3252669 setXmlTraceStream blindly closes previous stream
  o 3235440 NullPointerException when socket factory returns null
  
* Tue Mar 15 2011 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.8
  o 3197423 Server authentication with PegasusLocalAuthInfo failing
  o 3197627 testBasicConnect unit test fails on Windows
  o 3194700 Exception thrown on extrinsic methods
  o 3194680 Error in numeric keys
  o 3195069 Need support to disable SSL Handshake
  o 3185763 Reliable indication support - Phase 1
  o 3190335 Erroneous use of SystemName property in samples
  o 3186176 XML response for indication not traced
  o 3185833 missing newline when logging request/response
  o 3185824 Char16 definition includes whitespace
  o 3185818 indicationOccured URL incorrect
  o 3182121 Add Jsr48PegasusIndicationSample
  o 3154232 EmbeddedObject misspelled in javadoc

* Wed Dec 15 2010 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.7
  o 3111718 org.sblim.cimclient SSL Code is using the wrong SSL Property
  o 3109824 Move Java link from Sun to Oracle
  o 3078280 Fix for a null pointer exception in 1.3.9.1
  o 3062747 SblimCIMClient does not log all CIM-XML responces.
  
* Wed Sep 15 2010 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.6
  o 3046073 Performance hit due to socket conn. creation with timeout
  o 3048749 Hex digit parsing logic error in XMLPullParser
  o 3028518 Additional StringBuilder use
  o 3027618 Close files/readers in finally blocks
  o 3027615 Use CLASS_ARRAY_T instead of new CIMDataType(CLASS,0)
  o 3036683 HEAD branch ONLY: sync with Experimental branch
  o 3027479 Dead store to local variable
  o 3027392 Nullcheck of value previously dereferenced
  o 3026417 XMLAttributeValue does not use iHash field
  o 3026360 Handle unwritten fields
  o 3026316 XMLPullParser unused fields
  o 3026311 Vacuous comparison of integer value
  o 3026302 CIMDateTimeInterval uses # constructor instead of valueOf
  o 3023349 SLP uses # constructor instead of valueOf
  o 3023348 Listener uses # constructor instead of valueOf
  o 3023340 CIMObjectFactory uses # constructor instead of valueOf
  o 3023145 CharValue uses # constructor instead of valueOf
  o 3023143 CIMXMLParserImpl uses # constructor instead of valueOf
  o 3023141 CIMObjectPath uses # constructor instead of valueOf
  o 3023135 DADescriptor equals/compareTo issue
  o 3023120 RequestDescriptor equals/compareTo issue
  o 3023095 CIMQualifiedElementInterfaceImpl equals/hashCode issue
  o 3022554 Flushing socket ignores skip() return code
  o 3022541 File descriptor leak in sample/unittest
  o 3022524 iSortedValueEntries not serializable in Serializable class
  o 3022519 ServiceLocationAttribute.equals() compares same array
  o 3022501 Possible integer overflow in getTotalUSec
  o 3019252 Methods concatenate strings using + in a loop
  o 3019214 SLP equals methods assume too much
  o 3018178 CIMDateTimeInterval clean up
  o 3004779 TCK: CIMDataType not throwing IllegalArgumentException
  o 3004762 HTTPClient infinite loop for HTTP 407
  
* Tue Jun 15 2010 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.5
  o 3001333 CIMMethod class ignores propagated parameter
  o 3001680 CIMQualifierElementInterfaceImpl changes qualifiers
  o 3001359 XMLPullParser.CharString equals() method broken
  o 3001357 CIMDeleteNameSpaceOp name clash
  o 3001353 HttpHeaderParser ignores return value of toLowerCase()
  o 3001345 File handle leaks in HttpSocketFactory and LogAndTraceBroker
  o 3001243 Overview HTML out of date
  o 2997865 Infinite loop in HttpClient
  o 2994776 http 401 gives CIM_ERR_FAILED instead of CIM_ERR_ACCESS_DENIED
  o 2994252 CIMDateTimeInterval.getTotalMilliseconds() not unit tested
  o 2994249 CIMDateTimeInterval(long) calculates milliseconds
  o 2992349 CIMDateTimeInterval hr/min/sec max is 23/59/59, not 24/60/60
  o 2989424 TCK: CIMDateTimeInterval constructor
  o 2989367 CIMDateTimeInterval(long) constructor range wrong
  o 2990370 Development/unittest HTML out of date
  o 2974884 Exception when attaching 2 CDRoms with invoke method
  o 2978722 PasswordCredential(char[]) constructor is wrong
  o 2975989 TCK: CIMQualifierType constructor does not handle null
  o 2975975 TCK: CIMObjectPath(String) does not handle null
  o 2975917 TCK: CIMClass.getProperty() does not handle null property
  o 2975885 TCK: CIMXXX.hasQualifierValue(null,null) returns true
  o 2973300 TCK: CIMDateTimeXXX.compareTo() does not handle null
  o 2973230 TCK: UnsignedInteger64.equals() does not handle null
  o 2973233 TCK: UnsignedIntegerNN.hashCode() not working
  o 2972697 Fix spelling errors in HTML files

* Mon Mar 15 2010 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.4
  o 2970881 Add property to control EmbeddedObject case
  o 2942520 IPv6 link local address with scope_id including a dot not supported
  o 2964463 WBEMClient.initialize() throws wrong exception
  o 2963502 Add XML tracing to sample code
  o 2957387 EmbededObject XML attribute must not be all uppercases
  o 2956716 Jsr48IndicationSample hardcoded namespace
  o 2961592 Remove WBEMClient.setLocales() UnsupportedOperationException
  o 2959586 Sync up WBEMClient javadoc with JSR48 1.0.0
  o 2959264 Sync up javax.client.* javadoc with JSR48 1.0.0
  o 2959240 Sync up javax.listener.* javadoc with JSR48 1.0.0
  o 2959235 Update build.xml copyright year
  o 2959039 Fix WBEMException.toString() logic
  o 2958990 Remove WBEMException.CIM_ERR_TYPE_MISMATCH
  o 2958941 Sync up javax.wbem.* javadoc with JSR48 1.0.0
  o 2913938 Duplicate CIM requests with identical message ID
  o 2946113 First steps code snippet has compile errors
  o 2944830 Fix spelling of checkGranurality()
  o 2944842 Missing thrown ArrayIndexOutOfBoundsException
  o 2944839 Remove redundant toString() methods
  o 2944833 Need private setValue in UnsignedInteger8
  o 2944826 getUTCOffset() incorrect if not significant field
  o 2944824 Missing getXmlSchemaName() in CIMObjectPath
  o 2944219 Problem with pull operations using client against EMC CIMOM
  o 2935258 Sync up javax.cim.* javadoc with JSR48 1.0.0
  o 2930341 Sync up WBEMClientConstants with JSR48 1.0.0
  o 2927029 Unit test fails on Java 6
  o 2909941 RequestStateChange gives wrong exception/error id
  o 2912490 NullPointerException when invoking getInstance

* Tue Dec 15 2009 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.3
  o 2912104 Sync up javax.wbem.* with JSR48 1.0.0
  o 2907527 Fix SLP properties issues
  o 2901216 lost IndicationURL for IndcationListener.indicationOccured
  o 2903373 Java doc incorrect
  o 2900875 Javadoc should link to external Java5 Objects / APIs
  o 2886829 JSR48 new APIs: referenceClasses & referenceInstances
  o 2899859 javax.wbem.client missing JSR48 credential/principal APIs
  o 2899389 Password maximum length of 16?
  o 2888774 support POST retry on HTTP error 505
  o 2878054 Pull Enumeration Feature (PULL Parser)
  o 2884718 Merge JSR48 and SBLIM client properties
  o 2882448 Add WBEMClientConstants from JSR48
  o 2858933 JSR48 new APIs: associatorClasses & associatorInstances
  o 2870455 Missing CLASS_ARRAY_T in CIMDataType
  o 2839595 SLP discovery fails on Unix IPv6 systems
  o 2865222 enumerateQualifierTypes shouldn't require a class name
  o 2845128 CIMObjectPath.toString() misses host
  o 2846231 connection failure on CIMOM w/o user/pw
  o 2860081 Pull Enumeration Feature (DOM Parser)

* Tue Sep 15 2009 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.2
  o 2849970 createVALUEARRAY fails to create reference array
  o 2845211 Pulled Enumeration Support (SAX Parser)
  o 2834838 Add interface to retrieve version number and product name
  o 2817962 socket creation connects w/o a timeout
  o 2823494 Change Boolean constructor to static

* Mon Jun 15 2009 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.1
  o 2806362 Missing new CIMDateTimeAbsolute.getUTCOffset() method
  o 2797550 Make code compatible with JSR48 / Java Generics 
  o 2797696 Input files use unchecked or unsafe operations 
  o 2795671 Add Type to Comparable <T> 
  o 2799260 Fix left over @author tag from Java5 upgrade 
  o 2798931 Fix spelling of WBEMListenerFactory.getPROTOCOLS()
  o 2791860 Export instance to mof, wrong syntax
  o 2787464 lang exception in Chinese env with Java client 2.0.7
  o 2784078 Code cleanup: messages_XX.properties
  o 2763216 Code cleanup: visible spelling/grammar errors
  o 2750520 Code cleanup from empty statement et al
  o 2714989 Code cleanup from redundant null check et al

* Thu Mar 12 2009 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.1.0
  o 2680372 Eliminate duplicate entries in javadoc allclasses-frame.html
  o 2641758 CIM Client does not recognize HTTP extension headers
  o 2620505 EmbeddedObject qualifier is missing from CIMClass
  o 2433593 isArray returns true for method parameters of type reference
  o 2531371 Upgrade client to JDK 1.5 (Phase 2)
  o 2573575 Fix build.xml to allow file names >100 chars
  o 2524131 Upgrade client to JDK 1.5 (Phase 1)

* Fri Dec 12 2008 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.0.9
  o 2414503 SLPConfig : parseList not returning populated list
  o 2412389 Test case failure: Java5 Complier : CIMDateTimeAbsoluteTest
  o 2382763 HTTP header field Accept-Language does not include *
  o 2372030 Add property to control synchronized SSL handshaking
  o 2315151 Jsr48IndicationSample does not work 
  o 2227442 Add missing serialVersionUID
  o 2210455 Enhance javadoc, fix potential null pointers
  o 2204488 Fix / clean code to remove compiler warnings

* Fri Sep 12 2008 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.0.8
  o 2093708 HTTP 400 - Bad Request, CIMError: request-not-valid
  o 2087969 VALUE.ARRAY used in request for array of references
  o 2087975 can't set the pPropagated in WBEMClient.enumerateClasses()
  o 2038305 SAXException SBLIM Java Client V2.0.7
  o 2034342 HttpClient not closed on cimclient close
  o 2013628 SAXException when listing classes
  o 2002599 M-POST not supported in java-client

* Mon Jun 30 2008 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.0.7
  o 2003590 Change licensing from CPL to EPL
  o 1963102 NullPointerException when getting qualifiers
  
* Thu Jun 12 2008 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.0.6
  o 1992337 2.0.6 packaging issues
  o 1944875 Indications with embedded objects are not accepted
  o 1917321 CIMObjectPath("CIM_Memory","/root/ibmsd") broken
  o 1963762 connection leak in WBEMClientCIMXML
  o 1949000 setLocales() is empty
  o 1950819 SLP error: "java.io.IOException" on Linux and IPv6
  o 1949918 malformed service URL crashes SLP discovery
  o 1931621 CIMDateTimeAbsolute(Calendar) does not respect DST
  o 1931216 In HTTPClient need to get status before closing connection
  o 1917309 "/root:__NAMESPACE" not valid CIMObjectPath

* Wed Mar 12 2008 Dave Blaschke <blaschke@us.ibm.com>
- New release 2.0.5
  o 1911400 Source RPM file on SourceForge is broken
  
* Tue Feb 26 2008 Alexander Wolf-Reber <a.wolf-reber@de.ibm.com>
- New release 2.0.4
  o 1892046 Basic/digest authentication problem for Japanese users
  o 1849235 DTStringWriter.writeSigned parameter pDigits is not used
  o 1855726 CIMInstance.deriveInstance is setting wrong CIMObjectPath
  o 1892103 SLP improvements
  o 1804402 IPv6 ready SLP
  o 1832635 less strict parsing for IPv6 hostnames
  o 1848607 Strict EmbeddedObject types
  o 1827728 embeddedInstances: attribute EmbeddedObject not set
  o 1820763 Supporting the EmbeddedInstance qualifier
  o 1815707 TLS support
  o 1796339 Serializable interface missing from internal componentry
  o 1783288 CIMClass.isAssociation() not working for retrieved classes.
  o 1776114 Cannot derive instance of class CIM_IndicationSubscription
  o 1741654 Header mismatch error on ModifyInstance
  o 1769504   Type identification for VALUETYPE="numeric"

* Fri Aug 17 2007 Alexander Wolf-Reber <a.wolf-reber@de.ibm.com>
- New release 2.0.3
  o 1745282 Uniform time stamps for log files
  o 1742873 IPv6 ready cim-client
  o 1729361 Multicast discovery is broken in DiscovererSLP
  o 1737141 Sync up with JSR48 evolution
  o 1737123 Differences to JSR48 public review draft
  o 1736318 Wrong object path in HTTP header
  o 1735693 Empty VALUE.ARRAY elements are parsed as nulls
  o 1735614 Wrong ARRAYSIZE attribute handling in SAX/PULL
  o 1734936 DiscovererSLPTest fails in some environments
  o 1734888 Wrong reference building in METHODCALL request
  o 1723607 IPv6 support in WBEM-URI strings

* Fri May 25 2007 Alexander Wolf-Reber <a.wolf-reber@de.ibm.com>
- New release 2.0.2
  o 1720707 Conventional Node factory for CIM-XML SAX parser
  o 1719991 FVT: regression ClassCastException in EmbObjHandler
  o 1712656 Correct type identification for SVC CIMOM
  o 1715612 FVT: Status 0 in trailer is parsed as error
  o 1715511 FVT: Wrong HTTP header values
  o 1715027 FVT: Make message id random
  o 1714902 FVT: Threading related weak spots
  o 1714853 FVT: Inexplicit error when operation is invoked on closed client
  o 1714878 FVT: Empty string property values are parsed as nulls
  o 1715053 FVT: No forced retry on HTTP 501/510
  o 1714184 FVT: NPE on WBEMClientCIMXML.init()
  o 1711092 Some fixes/additions of log&trace messages
  o 1710066 LocalAuth fails for z/OS Pegasus
  o 1669961 CIMTypedElement.getType() =>getDataType()
  o 1669225 Ctor CIMDataType(int) shall be private
  o 1689085 Embedded object enhancements for Pegasus
  o 1678915 Integrated WBEM service discovery via SLP
  o 1698278 Unit tests fail on Hungarian locale
  o 1678807 Minor CIMDateTime suggestions
  o 1686977 Change package structure
  o 1679620 Ant build fails with javadoc error
  o 1679534 wrong ValueObjectNode.testChild()
  o 1660756 Embedded object support
  o 1671502 Remove dependency from Xerces
  o 1663270 Minor performance problems
  o 1660743 SSLContext is static
  o 1660575 Chunking broken on SUN JRE
  o 1656285 IndicationHandler does not accept non-Integer message ID

* Tue Feb 13 2007 Alexander Wolf-Reber <a.wolf-reber@de.ibm.com>
- New release 2.0.1
  o 1565892 Make SBLIM client JSR48 compliant

* Mon Feb 12 2007 Wolfgang Taphorn <taphorn@de.ibm.com>
- Initial Version
