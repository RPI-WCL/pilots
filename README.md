README for PILOTS ver 0.2
===============================================

1. Software Requirements
----------------------------------------------------------------------------------------------
* Java JDK 1.6 or newer
* [JFreeChart](http://www.jfree.org/jfreechart/download.html) 1.0.14 or newer to visualize outputs from PILOTS applications.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify PILOTS grammar.

* Note: the following instructions assume a Unix based system that uses the Bash shell.  Users of different operating systems and shells may have to adjust the following instructions slightly.

2. Downloading PILOTS library and JFreeChart
----------------------------------------------------------------------------------------------
* Download the zipped [PILOTS](https://github.com/RPI-WCL/pilots/archive/v0.2.zip) library source tree.
  The directory in which the zipped PILOTS library is extracted to will be referred to as *$PILOTS_HOME*.

* Download *jfreechart-1.0.14.jar* and *jcommon-1.0.17.jar* (or newer) from [JFreeChart](http://www.jfree.org/jfreechart/download.html) and place them under $PILOTS_HOME/lib directory.


3. Building PILOTS library
----------------------------------------------------------------------------------------------
* Run the following shell script from $PILOTS_HOME to setup CLASSPATH. This script also configures several alias commands for the PILOTS compiler.

	$ source setenv

* From $PILOTS_HOME run `build` and $PILOTS_DIR/lib/pilots.jar will be created.


3. Compiling a PILOTS program
----------------------------------------------------------------------------------------------

* Compile your PILOTS program (YourProgram.plt) into Java source code by the following:

	$ java pilots.compiler.PilotsCompiler YourProgram.plt
	
	OR
	
	$ plc YourProgram (`plc` is an alias for the above java command)

   The command line options for the PILOTS compiler are:
      - -Dstdout  : sends generated code to standard output instead
      - -Dsim     : compilation switch for simulation mode
      - -Dpackage : changes the generated code's target package

* Compile the generated java code.  The result is the PILOTS application and can be run using Java.
     
     $ javac YourProgram.java


4. Executing a PILOTS application
----------------------------------------------------------------------------------------------
* PILOTS applications assume all input and output are communicated over TCP/IP sockets.  
Thus most PILOTS applications will require you to run external programs that act as data input producers or output handlers.
The order of execution must be: 

    1. output handler
    2. PILOTS application
    3. data input producers

* The IP addresses that will be used for socket communication between the PILOTS application and the external programs must be specified.
The PILOTS application takes -input and -outputs arguments along with error detection parameters -tau and -omega as follows:

     $ java <your PILOTS application> -input=<port> -outputs=<ipaddr:port>* -tau=<t> -omega=<w>
     (* means one or more)

* As an example, we will show how to run the twice example found in $PILOTS_HOME/examples.  In this example, the PILOTS application listens to the port 8888, and sends the output to 127.0.0.1:9998 and error to 127.0.0.2:9999.

     $ java pilots.tests.SimpleApp -input=8888 -outputs=127.0.0.1:9999,127.0.0.2:9999 -tau=0.6 -omega=10

* PILOTS applications handle spatio-temporal input/output data using a specific format.  All input/output in a PILOTS application looks like:
	var0, var1, ... varn \r\n   [first line of file]
	space:time:v0,...,vn \r\n   [all other lines]

* space and time are specified as points (ex: x,y, or t), or ranges (ex: x0~x1, t0~t1). 

* Simulation mode, as opposed to real-time mode, relies on a different algorithm that uses simulated time.  When running a PILOTS application in simulation mode, the -Dtimespan=<tb~te> (where tb and te are both timestamps) argument must be specified. 

* See $PILOTS_HOME/pilots/data for example input files.

5. Running examples
----------------------------------------------------------------------------------------------

* There are several test scripts under *$PILOTS_HOME/pilots/examples directory* to simulate experiments shown in the publications.
Please note that there have been syntax changes in the PILOTS language since the earlier papers, so the *.plt files look different.

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
  - The presence of '(K)' is currently being used to differentiate these two cases.


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

  
