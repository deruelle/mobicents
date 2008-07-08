---------< Mobicents SIP Presence Service >----------

This module is a full SIP Presence Service, including servers:

- XDMS (XML Document Management Server)
- PS (Presence Server)
- RLS (Resource List Server) <not available yet>

----> REQUIREMENTS:

The XDMS and PS depends on the Mobicents Http-Servlet and SIP RAs.
Of course, you need to deploy those RAs prior to the installation of  
the servers.

IMPORTANT, at the moment the XDM is not compatible with JDK 1.6!

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

- Presence Server's Notifiers PresRules AUID : The id of the
app usage to be used, by the Presence Server, to retrieve
pres rules of a notifier, from the XDMS. You can change the
default value (OMA Pres Rules) using the property
${presence.server.notifier.presrules.auid} in the pom.xml on 
this directory, before building the server(s).

- Presence Server's Notifiers PresRules Document Name : The
name of the document to be used, by the Presence Server, to
retrieve the pres rules of a notifier, from the XDMS. You
can change the default value (pres-rules) using the
property ${presence.server.notifier.presrules.documentName} in the
pom.xml on this directory, before building the server(s).

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