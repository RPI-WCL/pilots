                      README for PILOTS ver 0.1
                                                        Oct 04, 2012
                                                        Shigeru Imai

0. Software Requirements
(1) Java JRE 1.6 or newer
(2) JFreeChart to visualize outputs from PILOTS applications (optional)
  Download jfreechart-1.x.x.jar and jcommon-1.x.x.jar from the below
  website and store them under './lib' directory.
    http://www.jfree.org/jfreechart/download.html


1. Building PILOTS library
Just run 'build.sh' and it will create pilots.jar under 'lib' directory.
  $./build.sh


2. Compiling a PILOTS program
(1) CLASSPATH must include (your pilots folder)/lib/pilots.jar.

(2) Running 'pilotsCompiler' will output translated java code into the
standard output as follows.
  $./pilotsCompiler Twice.plt > Twice.java

(3) The generated java code must be compiled by javac as follows.
  $javac Twice.java


3. Executing the PILOTS application
You need at least a client and a server program to run a PILOTS application.
Also, the order of execution must be 1)server, 2)PILOTS application, and 3)client.

To specify input ports and output host IP addresses, the PILOTS application
takes -input, -errors, -outputs as arguments as follows.
  $java <your PILOTS application> -input <port> -outputs <ipaddr:port>* -errors [ipaddr:port]*
  (* means one or more)

For example, the following SimpleApp application listens to the port 8888, and sends
the error to 127.0.0.1:9999.
  $java pilots.tests.SimpleApp -input 8888 -errors 127.0.0.1:9999


4. Running examples
There are test scripts under 'scripts' directory to simulate experiments
shown in the QUeST 2012 workshop paper ("Programming Spatio-Temporal Data
Streaming Applications with High-Level Specifications").

(1) Twice 
  $./chartServer
  $./twiceApp
  $./twiceClientA [% of timing jitter]
  $./twiceClientB [% of timing jitter]

(2) FlightSim
  $./chartServer
  $./flightSimApp 
  $./flightSimClient

Note flightSimApp can be replaced with one of 
{flightSimApp_AirspeedFail, flightSimApp_GpsFail, flightSimApp_BothFail}.


5. Limitations/Known bugs
- Simulation mode is not supported yet



