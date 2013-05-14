README for PILOTS ver 0.2
===============================================

1. Software Requirements
----------------------------------------------------------------------------------------------
* Java JDK 1.6 or newer
* [JFreeChart](http://www.jfree.org/jfreechart/download.html) 1.0.14 or newer to visualize outputs from PILOTS applications.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify PILOTS grammar.


2. Downloading PILOTS library and JFreeChart
----------------------------------------------------------------------------------------------
* Download the zipped [PILOTS](https://github.com/RPI-WCL/pilots/archive/v0.2.zip) library source tree.
  The directory in which the zipped PILOTS library is extracted to will be referred to as *$PILOTS_HOME*.

* Download *jfreechart-1.0.14.jar* and *jcommon-1.0.17.jar* (or newer) from [JFreeChart](http://www.jfree.org/jfreechart/download.html) and place them under $PILOTS\_HOME/lib directory.


3. Building PILOTS library
----------------------------------------------------------------------------------------------
1. Run the following shell script from $PILOTS_HOME to setup CLASSPATH. This script also configures several alias commands for the PILOTS compiler.

  $ source setenv

2. From $PILOTS_HOME run the build script and $PILOTS_HOME/lib/pilots.jar will be created.


3. Compiling a PILOTS program
----------------------------------------------------------------------------------------------

1. Compile your PILOTS program (YourProgram.plt) into Java source code by the following:

	$ java pilots.compiler.PilotsCompiler YourProgram.plt
	
	OR
	
	$ plc YourProgram (`plc` stands for PILOTS compiler and is an alias for the above java command)

   The command line options for the PILOTS compiler are:
      - -Dstdout  : sends generated code to standard output 
      - -Dsim     : compilation switch for simulation mode
      - -Dpackage : changes the generated code's target package

3. Compile the generated java code.  The result is the PILOTS application and can be run using Java.
     
     $ javac YourProgram.java


4. Executing a PILOTS application
----------------------------------------------------------------------------------------------
PILOTS applications assume all input and output are communicated over TCP/IP sockets.  
Thus most PILOTS applications will require you to run external programs that act as data input producers or output handlers.
The order of execution must be: 

    1. output handler
    2. PILOTS application
    3. data input producers

The IP addresses that will be used for socket communication between the PILOTS application and the external programs must be specified.
The PILOTS application takes -input, -outputs as arguments as follows:

     $ java <your PILOTS application> -input=<port> -outputs=<ipaddr:port>*
     (* means one or more)

For example, the following SimpleApp application listens to the port 8888, and sends the outputs to 127.0.0.1:9998 and 127.0.0.2:9999.

     $ java pilots.tests.SimpleApp -input=8888 -outputs=127.0.0.1:9999,127.0.0.2:9999

!!!Input/Output file format (slide as well)

5. Running examples
----------------------------------------------------------------------------------------------

There are several test scripts under *$PILOTS_HOME/pilots/examples directory* to simulate experiments shown in the [QUeST 2012 workshop paper](http://wcl.cs.rpi.edu/papers/quest2012.pdf).
Please note that there have been syntax changes in the PILOTS language since the QUeST paper, so the *.plt files look different.

* Twice

Go to *$PILOTS_HOME/pilots/examples/twicesim* directory and run the following scripts in different terminals.

     $./outputHandler
     $./twicesim
     $./twiceProducer

* SpeedCheck

Go to *$PILOTS_HOME/pilots/examples/speedcheck* directory and run the following script in different terminals.

     $./outputHandler
     $./speedCheck 
     $./speedCheckProducer

You can simulate some sensor failure situations by replacing *speedCheckProducer* with one of *speedCheckProducer_PitotTubeFail*, *speedCheckProducer_GpsFail*, and *speedCheckProducer_BothFail*.


6. Limitations/Known bugs
----------------------------------------------------------------------------------------------
* PILOTS is still in a proof-of-concept state

* default external software components are not particularly customizable or user friendly.

* The error clause only allows a single error function.  

* Language only supports error signatures that are a constant, or a linear function. Examples are:
  - S: e = 100, <constraints>
  - S(K): e = 2*t + K, <constraints>
  - The presence of (K) is currently being used to differentiate these two cases.


7. Future Work
----------------------------------------------------------------------------------------------
* types of error signatures we would like to support eventually:
    -   S(K): e = K
    -   S(K): e = K*t^2
    -   S:    e = t
    -   S(K1,K2): e = K1*t + K2, K1 != 0, K2 > thresh

* Support for multiple error functions (will require additional logic in the error analysis phase)

* External software components: simulation and visualization
    - more defined and customizable

* Enhanced visualization (using JFreeChart)
    - mode likelihood vector
    - corrected output
    - error function

* Simulation mode with support for dynamic simulation inputs
    - multiple files, each corresponding to a particular input
    - different directories for the different times
    - forecast data

* Enhanced error analysis accuracy
    - different interpolation methods
    - more types of error signatures

  
