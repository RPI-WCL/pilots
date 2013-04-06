
README for PILOTS ver 0.1
===============================================

1. Software Requirements
----------------------------------------------------------------------------------------------
* Java JDK 1.6 or newer
* (Optional) [JFreeChart](http://www.jfree.org/jfreechart/download.html) to visualize outputs from PILOTS applications.
  Place *jfreechart-1.x.x.jar* and *jcommon-1.x.x.jar* under *lib* directory.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify PILOTS grammar.


2. Building PILOTS library
----------------------------------------------------------------------------------------------
Just run `build.sh` and *pilots.jar* will be created under the *lib* directory.

Please note that some files under *tests* directory requires *JFreeChart*.


3. Compiling a PILOTS program
----------------------------------------------------------------------------------------------
1. Make sure that CLASSPATH must include *(your pilots folder)/lib/pilots.jar*.

2. Run `PilotsCompiler` to get translated java code in the standard output:

     $ java compiler.PilotsCompiler SimpleApp.plt > SimpleApp.java

3. Compile the generated java code:
     
     $ javac SimpleApp.java


4. Executing a PILOTS application
----------------------------------------------------------------------------------------------
To run a PILOTS application, you also need to run one data input client and one ore more server program are required to run.
The order of execution must be: 1.server, 2. PILOTS application, and 3. client.

To specify client's input ports and servers' IP addresses, the PILOTS application takes -input, -errors, -outputs as arguments as follows:

     $ java <your PILOTS application> -input <port> -outputs <ipaddr:port>* -errors [ipaddr:port]*
     (* means one or more)

For example, the following SimpleApp application listens to the port 8888, and sends the error to 127.0.0.1:9999:

     $ java pilots.tests.SimpleApp -input 8888 -errors 127.0.0.1:9999


5. Running examples
----------------------------------------------------------------------------------------------

There are several test scripts under *scripts* directory to simulate experiments shown in the [QUeST 2012 workshop paper](http://wcl.cs.rpi.edu/papers/quest2012.pdf).

* Twice ()

         $./chartServer
         $./twiceApp
         $./twiceClientA [% of timing jitter (0-100)]
         $./twiceClientB [% of timing jitter (0-100)]

* FlightSim (a simple flightplan example)

         $./chartServer
         $./flightSimApp 
         $./flightSimClient

You can simulate some sensor failure situations by replacing *flighSimClient* with one of *flightSimApp_AirspeedFail*, *flightSimApp_GpsFail*, and *flightSimApp_BothFail*.


6. Limitations/Known bugs
----------------------------------------------------------------------------------------------
* Simulation mode is not completely supported yet
