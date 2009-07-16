/**
 * Start time:12:13:39 2009-07-16<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.media.server.testsuite.cli;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import jain.protocol.ip.mgcp.CreateProviderException;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.TooManyListenersException;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sdp.Attribute;

import javax.sdp.SdpFactory;
import org.mobicents.media.server.testsuite.general.AbstractTestCase;
import org.mobicents.media.server.testsuite.general.CallDisplayInterface;
import org.mobicents.media.server.testsuite.general.TestState;
import org.mobicents.media.server.testsuite.general.ann.AnnouncementTest;

/**
 * Start time:12:13:39 2009-07-16<br>
 * Project: mobicents-media-server-test-suite<br>
 * 
 * This is class which enables running test tool in cli mode.
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CLIRunner implements CallDisplayInterface {

    private String localAddress = "127.0.0.1",  remoteAddress = "127.0.0.1";
    private int localPort = 2428,  remotePort = 2427;
    private int cps = 1;
    private int callDuration = 2500;
    private long maxCalls = -1;
    private int maxConcurrentCalls = -1;
    private File dataDumpDir = new File("datadump");
    private String audioFileURL = new File("target/audio/ulaw_13s.wav").toURI().toString();
    private Vector codec = new Vector();
    private AbstractTestCase testCase;
    private TestTypeEnum testType = TestTypeEnum.AnnTest;
    private static final LongOpt[] _LONG_OPTS = new LongOpt[12];
    private static final String _GETOPT_PARAMS_STRING = "h:q:w:e:r:t:y:u:i:o:p:a";
    private static final Logger log = Logger.getLogger(CLIRunner.class.getName());

    static {



        _LONG_OPTS[0] = new LongOpt("usage", LongOpt.NO_ARGUMENT, null, 'h');
        _LONG_OPTS[1] = new LongOpt("localaddr", LongOpt.OPTIONAL_ARGUMENT, null, 'q');
        _LONG_OPTS[2] = new LongOpt("remoteaddr", LongOpt.OPTIONAL_ARGUMENT, null, 'w');
        _LONG_OPTS[3] = new LongOpt("remoteport", LongOpt.OPTIONAL_ARGUMENT, null, 'e');
        _LONG_OPTS[4] = new LongOpt("localport", LongOpt.OPTIONAL_ARGUMENT, null, 'r');
        _LONG_OPTS[5] = new LongOpt("concurrentcalls", LongOpt.OPTIONAL_ARGUMENT, null, 't');
        _LONG_OPTS[6] = new LongOpt("maxcalls", LongOpt.OPTIONAL_ARGUMENT, null, 'y');
        _LONG_OPTS[7] = new LongOpt("datadir", LongOpt.OPTIONAL_ARGUMENT, null, 'u');
        _LONG_OPTS[8] = new LongOpt("audiofile", LongOpt.OPTIONAL_ARGUMENT, null, 'i');
        _LONG_OPTS[9] = new LongOpt("audiocodec", LongOpt.OPTIONAL_ARGUMENT, null, 'o');
        _LONG_OPTS[10] = new LongOpt("testtype", LongOpt.OPTIONAL_ARGUMENT, null, 'p');
        _LONG_OPTS[11] = new LongOpt("cps", LongOpt.OPTIONAL_ARGUMENT, null, 'a');


    }

    /**
     * 	
     */
    public CLIRunner() {
        // TODO Auto-generated constructor stub
        convertCodec("0 pcmu/8000");
    }

    //////////////////
    // Usage method //
    //////////////////
    public static void usage() {
        StringBuffer sb = new StringBuffer();

        sb.append("java " + CLIRunner.class.getName() + " [OPTIONS] --testtype TestType \n");
        sb.append("Where options can be:\n");
        sb.append("--localaddr : local address, default is 127.0.0.1\n");
        sb.append("--remoteaddr : remote address, default is 127.0.0.1\n");
        sb.append("--localpport : local port, default is 2428\n");
        sb.append("--remoteport : remote port, default is 2427\n");
        sb.append("--concurrentcalls : concurrent calls, default is -1, which means unbound\n");
        sb.append("--maxcalls : max calls, default is -1, which means unbound\n");
        sb.append("--datadir : data dump directory, default is ./datadump\n");
        sb.append("--audiofile : audio file url, if requried, default is target/audio/ulaw_13s.wav\n");
        sb.append("--audiocodec : audio codec to be used if requried, default is \'0 pcmu/8000\', value should be specifiedd in \'\'\n");
        sb.append("--testtype : test type, currently there is only one available: AnnTest\n");
        sb.append("--usage : print this message\n");
        sb.append("example options part: --localaddress=127.0.0.1 --localport=2499 --concurentcalls=12 --audiocodec=\'8 pcma/8000\' -testtype=AnnTest\n");
        log.severe("Usage: \n" + sb);

    }

    /////////////////////////
    // Some helper methods //
    /////////////////////////
    private void convertCodec(String s) {
        SdpFactory sdpFactory = SdpFactory.getInstance();
        codec.clear();
      
        codec.add(sdpFactory.createAttribute("rtpmap", s.replaceAll("'", "")));
    }

    private void convertTest(String v) {
        testType = TestTypeEnum.fromString(v);

    }
    //////////////////////////
    // Methods used by test //
    //////////////////////////
	/* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getCPS()
     */
    public int getCPS() {
        return this.cps;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getCallDuration()
     */
    public int getCallDuration() {
        return this.callDuration;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getCodec()
     */
    public Vector<Attribute> getCodec() {
        return this.codec;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getDefaultDataDumpDirectory()
     */
    public File getDefaultDataDumpDirectory() {
        return this.dataDumpDir;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getFileURL()
     */
    public String getFileURL() {
        return this.audioFileURL;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getLocalAddress()
     */
    public String getLocalAddress() {
        return this.localAddress;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getLocalPort()
     */
    public int getLocalPort() {
        return this.localPort;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getRemoteAddress()
     */
    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#getRemotePort()
     */
    public int getRemotePort() {
        return this.remotePort;
    }

    /* (non-Javadoc)
     * @see org.mobicents.media.server.testsuite.general.CallDisplayInterface#updateCallView()
     */
    public void updateCallView() {

        StringBuffer sb = new StringBuffer();
        sb.append("=============================================================\n");
        sb.append("Press 'q' + Enter to stop test\n");
        sb.append("Current calls: " + this.testCase.getOngoingCallNumber() + "\n");
        sb.append("Success calls: " + this.testCase.getCompletedCallNumber() + "\n");
        sb.append("Failed calls : " + this.testCase.getErrorCallNumber() + "\n");
        sb.append("Total calls  : " + this.testCase.getTotalCallNumber() + "\n");
        if (log.isLoggable(Level.INFO)) {
            log.info(sb.toString());
        }
    }

    public int getMaxConcurrentCalls() {
        return this.maxConcurrentCalls;
    }

    public long getMaxCalls() {
        return this.maxCalls;
    }

    public static void main(String[] args) {

       
        CLIRunner cli = new CLIRunner();
        cli.parseArgs(args);
        cli.runTest();

    }

    private void runTest() {


        try {
            this.testCase = testType.getTestCaseForType(this);
            log.info("Starting test case, prest 'q' to exit test");
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.testCase.start();
            while (this.testCase.getTestState() != TestState.Stoped) {
                try {
                    Thread.sleep(1000);

                    if (System.in.available() > 0) {

                        int r = System.in.read();
                        if (r == 'q') {

                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        } catch (CreateProviderException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (TooManyListenersException ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            if (testCase != null) {
                try {

                    testCase.stop();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {

                    testCase.stop();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void parseArgs(String[] args) {


        Getopt getOpt = new Getopt("CLIRunner", args, _GETOPT_PARAMS_STRING, _LONG_OPTS);
        getOpt.setOpterr(true);

        int c = -1;
        String v = null;

        while ((c = getOpt.getopt()) != -1) {


            // _GETOPT_PARAMS_STRING ="h:q:w:e:r:t:y:u:i:o:p";
            switch (c) {
                case 'h':
                    usage();
                    System.exit(0);
                case 'q':
                    //local address
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Local Address must have value");
                    } else {
                        try {
                            InetAddress.getByName(v);
                            this.localAddress = v;
                        } catch (UnknownHostException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'w':
                    //remote address
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Remote Address must have value");
                    } else {
                        try {
                            InetAddress.getByName(v);
                            this.remoteAddress = v;
                        } catch (UnknownHostException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'e':
                    //remote port
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Remote Port must have value");
                    } else {
                        try {

                            this.remotePort = Integer.valueOf(v);
                        } catch (NumberFormatException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'r':
                    //local port
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Local Port must have value");
                    } else {
                        try {

                            this.localPort = Integer.valueOf(v);
                        } catch (NumberFormatException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 't':
                    //concurrent calls
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Concurrent Calls must have value");
                    } else {
                        try {

                            this.maxConcurrentCalls = Integer.valueOf(v);
                        } catch (NumberFormatException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'y':
                    //max calls
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Max Calls must have value");
                    } else {
                        try {

                            this.maxCalls = Integer.valueOf(v);
                        } catch (NumberFormatException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'u':
                    //data dir
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Data Dir must have value");
                    } else {
                        this.dataDumpDir = new File(v);
                    }
                    break;
                case 'i':
                    //audio file
                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Audio File URL must have value");
                    } else {
                        try {
                            new URL(v);
                            this.audioFileURL = v;
                        } catch (Exception ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'o':

                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Audio Codec must have value");
                    } else {
                        try {
                            this.convertCodec(v);
                        } catch (Exception ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'p':

                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Test Type must have value");
                    } else {
                        try {
                            this.convertTest(v);
                        } catch (Exception ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 'a':

                    v = getOpt.getOptarg();
                    if (v == null) {
                        log.severe("Test Type must have value");
                    } else {
                        try {
                           this.cps = Integer.valueOf(v);
                        } catch (Exception ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                    break;

                default:
                    log.severe("Wrong parameter!! ---> "+ Character.toString((char)c));

                }
        }




    }
}

enum TestTypeEnum {

    AnnTest;

    public static TestTypeEnum fromString(String v) {
        if (v.equals(AnnTest.toString())) {
            return AnnTest;
        }


        throw new RuntimeException("There is no such test type: " + v);
    }

    public AbstractTestCase getTestCaseForType(CallDisplayInterface cdi) throws UnknownHostException {
        if (this.toString().equals(AnnTest.toString())) {

            AnnouncementTest at = new AnnouncementTest();
            at.setCallDisplay(cdi);
            return at;
        }


        throw new RuntimeException("There is no such test type: " + this);
    }
    }

