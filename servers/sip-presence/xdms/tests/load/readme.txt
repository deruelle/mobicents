This is the perfomance test for Mobicents XDMS

It tests both XCAP and SIP interfaces, using both seagull and sipp as load generators.

In order to be able to run this test you must have seagull instaled on your system.#
For that, please refer to seagull home page at http://gull.sourceforge.net/

A script is provided to download and compile sipp. You must run it once before start the test.

To adjust the call rate, there are two files that need to be edited.
	- Adjust -r parameter in sipp-cient.sh to adjust the sipp call rate
	- Call-rate at xcap-config.xml to adjust the seagull call rate

To adjust the IP and port, the same two files need to be edited.
	- Adjust the first ip:port in sipp
	- channel1 open-args parameter at xcap-config.xml in seagull

The sipp call rate must be equal or greater than the seagull one.

To analyse the results produced you can use the sipp report tool with -a flag.
You can find it in mobicents SVN under /trunk/tools/qa/sipp-report-tool

Further questions, or suggestions, can be put to mobicents-public@googlegroups.com
Don't be shy. Share your results with us.



The message diagram of the test is the following:

   seagull                             XDMS                                 sipp
      |                                 |                                    |  
      |                                 | <----------------------- SUBSCRIBE |  
      |                                 | 200 OK --------------------------> |  
      |                                 |                                    |  
      |                                 | NOTIFY --------------------------> |  
      |                                 | <-------------------------- 200 OK |  
      |                                 |                                    |  
      | PUT --------------------------> |                                    |  
      | <------------------ 201 CREATED |                                    |  
      |                                 |                                    |  
      |                                 | NOTIFY --------------------------> |  
      |                                 | <-------------------------- 200 OK |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | GET --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | GET --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | GET --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      | PUT --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      |                                 | NOTIFY --------------------------> |  
      |                                 | <-------------------------- 200 OK |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | GET --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | GET --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | GET --------------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      | 1 sec timer                     |                                    |  
      |                                 |                                    |  
      | DELETE -----------------------> |                                    |  
      | <----------------------- 200 OK |                                    |  
      |                                 |                                    |  
      |                                 | NOTIFY --------------------------> |  
      |                                 | <-------------------------- 200 OK |  
      |                                 |                                    |  
      |                                 |                       1 sec timer  |  
      |                                 |                                    |  
      |                                 | <------------------- (un)SUBSCRIBE |  
      |                                 | 200 OK --------------------------> |  
      |                                 |                                    |  
      |                                 | NOTIFY --------------------------> |  
      |                                 | <-------------------------- 200 OK |  
      |                                 |                                    |  