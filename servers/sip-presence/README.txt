---------< Mobicents SIP Presence Service >----------

This module is a full SIP Presence Service, including servers:

- XDMS (XML Document Management Server)
- PS (Presence Server)
- RLS (Resource List Server) <not available yet>

----> REQUIREMENTS:

The XDMS depends on the Mobicents Http-Servlet RA and the PS
depends on the Mobicents SIP and Persistence RAs. Of course,
you need to deploy those RAs prior to the installation of  
the servers.

Important, at the moment the XDM is not compatible with JDK 1.6!

----> CONFIGURATION:

If you're going to build the servers from source you can 
configure:

- XCAP ROOT: This is the root relative path for XCAP uris.
Default is "/mobicents" which is the servlet default name of
the Mobicents Http Servlet RA. This value is set through the 
property ${xdm.server.xcap.root} in the pom.xml on this
directory. Note that if you change this value then you need to
also change the servlet name of the Mobicents Http Servlet RA
(consult its documentation for how to do this).

- XCAP Aware User Agent: The PS server calculates pres-rules
XCAP uris of users, by default, using a non standard way, but
compatible with Counterpath Eyebeam. You can change to the 
standards recommended way in the hard coded field
 "clientUAIsEyebeam" of PresenceSubscriptionControlSbb 
(in ps-core/subscription-sbb/src/main/java/..). This is a
temporary solution due to a feature, in current jain-sip stack,
that removes invalid User-Agent headers (seems most UAs are
failing here) of SIP REQUESTS. In the future this configuration
will not be needed.

----> INSTALL:

Option 1) For both servers integrated on same Mobicents do:

"mvn -f integrated/pom.xml install" on this directory
or "mvn install" on integrated sub-directory

Option 2) For independent servers (in different Mobicents hosts):

- XDMS : do "mvn -f xdms/pom.xml install" on this directory
or "mvn install" on xdms sub-directory
- PS : <not available yet>

----> UNINSTALL:

Option 1) For both servers integrated on same Mobicents do:

"mvn -f integrated/pom.xml clean" on this directory
or "mvn clean" on integrated sub-directory

Option 2) For independent servers:

- XDMS : do "mvn -f xdms/pom.xml clean" on this directory
or "mvn clean" on xdms sub-directory
- PS : <not available yet>

----> TESTING:

Currently only the XDMS has a test framework, it's recommended
to run it before using the server. See xdms/tests/README.txt
for more info.

Author: Eduardo Martins, JBoss R&D