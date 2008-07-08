To run XDM tests (after install of XDM SERVER):

mvn test

Important: JBoss AS does not comes with HSQLDB in server mode by default,
which is required for testing, so before starting the server go to
"hsqldb-scripts" and run "ant install" to change the HSQLDB config.
To restore the previous config run "ant uninstall".