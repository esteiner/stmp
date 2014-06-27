#Dependency Resolution Problem with "uses"
It seems that the "uses" directive for "Export-Package" headers is not applied correctly when declarative services are used.

I have a declarative service, which is made of 3 bundles:
- shared.datamodel = containing the data types (in package dl)
- shared.serviceintf = containing the service interface (in package sl)
- shared.servicelocator = containing the declarative service component

These 3 bundles exist in two versions: 1.1.0 and 1.2.0. The datamodel bundles export the "dl" package. The serviceintf bundles export the "sl" package. The serviceintf bundles import the datamodel package with a version range = [1.0.0,2.0.0) and define a "uses" directive for its export.

There are two client bundles FrontendA and FrontendB. Each of them has an exact (e.g. FrontendA has range = [1.1.0,1.1.1) and FrontendB has range = [1.2.0,1.2.1)) package dependency to one of the datamodel and the serviceintf bundles.

So my bundles at runtime are:
- shared.datamodel 1.1.0
- shared.datamodel 1.2.0
- shared.serviceintf 1.1.0
- shared.serviceintf 1.2.0
- shared.servicelocator 1.1.0
- shared.servicelocator 1.2.0
- frontendA 1.0.0
- frontendB 1.0.0

What I see during the dependency resolution at startup is that the shared.serviceintf 1.1.0 bundle is wired to the "dl" package of the shared.datamodel 1.2.0 bundle. 

What I would expect, is that is should be wired to version 1.1.0. Why? Because of the uses directive of the export package of "sl". My understanding is that this directive makes sure, that all importers of the "sl" package must get the "dl" package from the same exporter. Because the frontendA bundle has a more strict version to the "dl" package, it must used also for all other dependend bundles.

The error that shows up is:

~~~
!ENTRY org.eclipse.osgi 2 0 2014-06-27 11:17:43.667
!MESSAGE The following is a complete list of bundles which are not resolved, see the prior log entry for the root cause if it exists:
!SUBENTRY 1 org.eclipse.osgi 2 0 2014-06-27 11:17:43.667
!MESSAGE Bundle frontendA_1.0.0.qualifier [2] was not resolved.
!SUBENTRY 2 frontendA 2 0 2014-06-27 11:17:43.667
!MESSAGE Package uses conflict: Require-Bundle: shared.serviceintf; bundle-version="[1.1.0,1.1.1)"
~~~

My example bundles are available at github: https://github.com/esteiner/stmp

Bundles at runtime:
~~~
id	State       Bundle
0	ACTIVE      org.eclipse.osgi_3.9.1.v20130814-1242
1	ACTIVE      org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119
2	INSTALLED   frontendA_1.0.0.qualifier
3	ACTIVE      frontendB_1.0.0.qualifier
4	ACTIVE      javax.servlet_3.0.0.v201112011016
5	ACTIVE      javax.xml_1.3.4.v201005080400
6	ACTIVE      org.apache.felix.gogo.command_0.10.0.v201209301215
7	ACTIVE      org.apache.felix.gogo.runtime_0.10.0.v201209301036
8	ACTIVE      org.apache.felix.gogo.shell_0.10.0.v201212101605
9	ACTIVE      org.eclipse.core.contenttype_3.4.200.v20130326-1255
10	ACTIVE      org.eclipse.core.jobs_3.5.300.v20130429-1813
11	ACTIVE      org.eclipse.core.runtime_3.9.0.v20130326-1255
12	ACTIVE      org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004
13	RESOLVED    org.eclipse.core.runtime.compatibility.registry_3.5.200.v20130514-1256
	            Master=19
14	ACTIVE      org.eclipse.equinox.app_1.3.100.v20130327-1442
15	ACTIVE      org.eclipse.equinox.common_3.6.200.v20130402-1505
16	ACTIVE      org.eclipse.equinox.console_1.0.100.v20130429-0953
17	ACTIVE      org.eclipse.equinox.ds_1.4.101.v20130813-1853
18	ACTIVE      org.eclipse.equinox.preferences_3.5.100.v20130422-1538
19	ACTIVE      org.eclipse.equinox.registry_3.5.301.v20130717-1549
	            Fragments=13
20	ACTIVE      org.eclipse.equinox.util_1.0.500.v20130404-1337
21	ACTIVE      org.eclipse.osgi.services_3.3.100.v20130513-1956
22	ACTIVE      shared.datamodel_1.2.0
23	ACTIVE      shared.datamodel_1.1.0
24	ACTIVE      shared.serviceintf_1.2.0
25	ACTIVE      shared.serviceintf_1.1.0
26	ACTIVE      shared.servicelocator_1.2.0
27	ACTIVE      shared.servicelocator_1.1.0
~~~

The wiring results:

****** Result Wirings ******
    * WIRING for [org.eclipse.osgi_3.9.1.v20130814-1242]
        (r) no requires
        (w) no imports
****** Result Wirings ******
    * WIRING for [org.eclipse.osgi_3.9.1.v20130814-1242]
        (r) no requires
        (w) no imports
****** Result Wirings ******
    * WIRING for [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]
        (r) no requires
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.osgi.service.startlevel -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.startlevel
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.eclipse.osgi.service.resolver -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.resolver
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.eclipse.osgi.service.datalocation -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.datalocation
        (w) [org.eclipse.equinox.simpleconfigurator_1.0.400.v20130327-2119]:org.eclipse.osgi.framework.console -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.console
Time to load bundles: 63
****** Result Wirings ******
    * WIRING for [org.eclipse.core.runtime_3.9.0.v20130326-1255]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.osgi_3.9.1.v20130814-1242]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.core.jobs_3.5.300.v20130429-1813]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.equinox.registry_3.5.301.v20130717-1549]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.core.contenttype_3.4.200.v20130326-1255]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]
        (r) org.eclipse.core.runtime_3.9.0.v20130326-1255 -> [org.eclipse.equinox.app_1.3.100.v20130327-1442]
        (w) [org.eclipse.core.runtime_3.9.0.v20130326-1255]:org.eclipse.core.internal.runtime.auth -> DYNAMIC
    * WIRING for [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (r) no requires
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.equinox.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.equinox.log
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.osgi.framework.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.log
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.osgi.service.datalocation -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.datalocation
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.osgi.service.localization -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.localization
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.osgi.service.urlconversion -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.urlconversion
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.osgi.service.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.log
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.osgi.service.url -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.url
        (w) [org.eclipse.equinox.common_3.6.200.v20130402-1505]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
    * WIRING for [org.eclipse.equinox.util_1.0.500.v20130404-1337]
        (r) no requires
        (w) [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.osgi.service.cm -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.cm
        (w) [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.osgi.service.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.log
        (w) [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
    * WIRING for [shared.serviceintf_1.2.0]
        (r) no requires
        (w) [shared.serviceintf_1.2.0]:shared.datamodel -> [shared.datamodel_1.2.0]:shared.datamodel
    * WIRING for [shared.serviceintf_1.1.0]
        (r) no requires
        (w) [shared.serviceintf_1.1.0]:shared.datamodel -> [shared.datamodel_1.2.0]:shared.datamodel
    * WIRING for [org.eclipse.core.contenttype_3.4.200.v20130326-1255]
        (r) org.eclipse.core.contenttype_3.4.200.v20130326-1255 -> [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]
        (r) org.eclipse.core.contenttype_3.4.200.v20130326-1255 -> [org.eclipse.equinox.registry_3.5.301.v20130717-1549]
        (r) org.eclipse.core.contenttype_3.4.200.v20130326-1255 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:javax.xml.parsers -> [org.eclipse.osgi_3.9.1.v20130814-1242]:javax.xml.parsers
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.xml.sax -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.xml.sax
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.xml.sax.ext -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.xml.sax.ext
        (w) [org.eclipse.core.contenttype_3.4.200.v20130326-1255]:org.xml.sax.helpers -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.xml.sax.helpers
    * WIRING for [javax.servlet_3.0.0.v201112011016]
        (r) no requires
        (w) no imports
    * WIRING for [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]
        (r) org.eclipse.equinox.preferences_3.5.100.v20130422-1538 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (r) org.eclipse.equinox.preferences_3.5.100.v20130422-1538 -> [org.eclipse.equinox.registry_3.5.301.v20130717-1549]
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.eclipse.osgi.framework.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.log
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.eclipse.osgi.service.datalocation -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.datalocation
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.eclipse.osgi.service.environment -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.environment
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.osgi.service.prefs -> [org.eclipse.equinox.preferences_3.5.100.v20130422-1538]:org.osgi.service.prefs
    * WIRING for [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]
        (r) org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (w) [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]:org.eclipse.osgi.framework.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.log
        (w) [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]:org.eclipse.osgi.service.datalocation -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.datalocation
        (w) [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.core.runtime.compatibility.auth_3.2.300.v20120523-2004]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
    * WIRING for [shared.servicelocator_1.2.0]
        (r) no requires
        (w) [shared.servicelocator_1.2.0]:shared.datamodel -> [shared.datamodel_1.2.0]:shared.datamodel
        (w) [shared.servicelocator_1.2.0]:shared.serviceintf -> [shared.serviceintf_1.2.0]:shared.serviceintf
    * WIRING for [shared.servicelocator_1.1.0]
        (r) no requires
        (w) [shared.servicelocator_1.1.0]:shared.datamodel -> [shared.datamodel_1.2.0]:shared.datamodel
        (w) [shared.servicelocator_1.1.0]:shared.serviceintf -> [shared.serviceintf_1.2.0]:shared.serviceintf
    * WIRING for [frontendB_1.0.0.qualifier]
        (r) frontendB_1.0.0.qualifier -> [shared.datamodel_1.2.0]
        (r) frontendB_1.0.0.qualifier -> [shared.serviceintf_1.2.0]
        (w) no imports
    * WIRING for [org.eclipse.equinox.registry_3.5.301.v20130717-1549]
        (r) org.eclipse.equinox.registry_3.5.301.v20130717-1549 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:javax.xml.parsers -> [org.eclipse.osgi_3.9.1.v20130814-1242]:javax.xml.parsers
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.service.datalocation -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.datalocation
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.service.localization -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.localization
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.storagemanager -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.storagemanager
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.xml.sax -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.xml.sax
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.xml.sax.helpers -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.xml.sax.helpers
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.service.resolver -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.resolver
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.service.environment -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.environment
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.osgi.framework.console -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.console
        (w) [org.eclipse.equinox.registry_3.5.301.v20130717-1549]:org.eclipse.core.runtime.jobs -> [org.eclipse.core.jobs_3.5.300.v20130429-1813]:org.eclipse.core.runtime.jobs
    * WIRING for [javax.xml_1.3.4.v201005080400]
        (r) javax.xml_1.3.4.v201005080400 -> [org.eclipse.osgi_3.9.1.v20130814-1242]
        (w) no imports
    * WIRING for [org.eclipse.equinox.ds_1.4.101.v20130813-1853]
        (r) no requires
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.equinox.internal.util.event -> [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.eclipse.equinox.internal.util.event
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.equinox.internal.util.hash -> [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.eclipse.equinox.internal.util.hash
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.equinox.internal.util.pool -> [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.eclipse.equinox.internal.util.pool
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.equinox.internal.util.ref -> [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.eclipse.equinox.internal.util.ref
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.equinox.internal.util.threadpool -> [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.eclipse.equinox.internal.util.threadpool
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.equinox.internal.util.timer -> [org.eclipse.equinox.util_1.0.500.v20130404-1337]:org.eclipse.equinox.internal.util.timer
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.osgi.framework.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.log
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.osgi.service.environment -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.environment
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.osgi.service.cm -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.cm
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.osgi.service.component -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.component
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.osgi.service.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.log
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.apache.felix.scr -> [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.apache.felix.scr
        (w) [org.eclipse.equinox.ds_1.4.101.v20130813-1853]:org.eclipse.osgi.framework.console -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.console
    * WIRING for [shared.datamodel_1.2.0]
        (r) no requires
        (w) no imports
    * WIRING for [shared.datamodel_1.1.0]
        (r) no requires
        (w) no imports
    * WIRING for [org.eclipse.core.jobs_3.5.300.v20130429-1813]
        (r) org.eclipse.core.jobs_3.5.300.v20130429-1813 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (w) [org.eclipse.core.jobs_3.5.300.v20130429-1813]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.core.jobs_3.5.300.v20130429-1813]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.core.jobs_3.5.300.v20130429-1813]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.core.jobs_3.5.300.v20130429-1813]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.core.jobs_3.5.300.v20130429-1813]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
    * WIRING for [org.apache.felix.gogo.shell_0.10.0.v201212101605]
        (r) no requires
        (w) [org.apache.felix.gogo.shell_0.10.0.v201212101605]:org.apache.felix.service.command -> [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.command
        (w) [org.apache.felix.gogo.shell_0.10.0.v201212101605]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.apache.felix.gogo.shell_0.10.0.v201212101605]:org.osgi.service.startlevel -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.startlevel
        (w) [org.apache.felix.gogo.shell_0.10.0.v201212101605]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
    * WIRING for [org.eclipse.core.runtime.compatibility.registry_3.5.200.v20130514-1256]
        (r) no requires
        (h) org.eclipse.core.runtime.compatibility.registry_3.5.200.v20130514-1256 -> org.eclipse.equinox.registry_3.5.301.v20130717-1549
        (w) no imports
    * WIRING for [org.eclipse.equinox.app_1.3.100.v20130327-1442]
        (r) org.eclipse.equinox.app_1.3.100.v20130327-1442 -> [org.eclipse.equinox.registry_3.5.301.v20130717-1549]
        (r) org.eclipse.equinox.app_1.3.100.v20130327-1442 -> [org.eclipse.equinox.common_3.6.200.v20130402-1505]
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.osgi.service.event -> DYNAMIC
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.framework.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.log
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.service.datalocation -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.datalocation
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.service.debug -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.debug
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.service.environment -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.environment
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.service.runnable -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.runnable
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.storagemanager -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.storagemanager
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.osgi.service.event -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.event
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.osgi.service.condpermadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.condpermadmin
        (w) [org.eclipse.equinox.app_1.3.100.v20130327-1442]:org.eclipse.osgi.framework.console -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.console
    * WIRING for [org.eclipse.osgi.services_3.3.100.v20130513-1956]
        (r) no requires
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:javax.servlet -> DYNAMIC
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:javax.servlet.http -> DYNAMIC
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:javax.microedition.io -> DYNAMIC
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.cm -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.cm
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.component -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.component
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.device -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.device
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.event -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.event
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.http -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.http
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.io -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.io
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.log
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.metatype -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.metatype
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.provisioning -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.provisioning
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.upnp -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.upnp
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.useradmin -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.useradmin
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.wireadmin -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.wireadmin
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:javax.servlet.http -> [javax.servlet_3.0.0.v201112011016]:javax.servlet.http
        (w) [org.eclipse.osgi.services_3.3.100.v20130513-1956]:javax.servlet -> [javax.servlet_3.0.0.v201112011016]:javax.servlet
    * WIRING for [org.eclipse.equinox.console_1.0.100.v20130429-0953]
        (r) no requires
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.apache.felix.service.command -> [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.command
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.eclipse.osgi.framework.console -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.framework.console
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.eclipse.osgi.service.environment -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.environment
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.eclipse.osgi.service.resolver -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.service.resolver
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.eclipse.osgi.util -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.eclipse.osgi.util
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.service.condpermadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.condpermadmin
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.service.permissionadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.permissionadmin
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.service.startlevel -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.startlevel
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.eclipse.equinox.console_1.0.100.v20130429-0953]:org.osgi.service.cm -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.cm
    * WIRING for [org.apache.felix.gogo.command_0.10.0.v201209301215]
        (r) no requires
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.apache.felix.bundlerepository -> DYNAMIC
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.apache.felix.bundlerepository.* -> DYNAMIC
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.apache.felix.service.command -> [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.command
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.osgi.service.log -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.log
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.osgi.service.packageadmin -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.packageadmin
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.osgi.service.startlevel -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.service.startlevel
        (w) [org.apache.felix.gogo.command_0.10.0.v201209301215]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
    * WIRING for [frontendA_1.0.0.qualifier]
        (r) frontendA_1.0.0.qualifier -> NULL!!!
        (r) frontendA_1.0.0.qualifier -> NULL!!!
        (w) [frontendA_1.0.0.qualifier]:org.osgi.framework -> NULL!!!
    * WIRING for [org.apache.felix.gogo.runtime_0.10.0.v201209301036]
        (r) no requires
        (w) [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.gogo.api -> [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.gogo.api
        (w) [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.command -> [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.command
        (w) [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.threadio -> [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.apache.felix.service.threadio
        (w) [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.osgi.framework -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.framework
        (w) [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.osgi.util.tracker -> [org.eclipse.osgi_3.9.1.v20130814-1242]:org.osgi.util.tracker
        (w) [org.apache.felix.gogo.runtime_0.10.0.v201209301036]:org.osgi.service.event -> [org.eclipse.osgi.services_3.3.100.v20130513-1956]:org.osgi.service.event
****** Result Wirings ******
    * WIRING for [frontendA_1.0.0.qualifier]
        (r) frontendA_1.0.0.qualifier -> NULL!!!
        (r) frontendA_1.0.0.qualifier -> NULL!!!
        (w) [frontendA_1.0.0.qualifier]:org.osgi.framework -> NULL!!!
