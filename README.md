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

* PILOTS applications handle spatio-temporal input/output data using a specific format.  All input/output in a PILOTS application looks like:

	var0, var1, ... varn \r\n   [first line of file]

	space:time:v0,...,vn \r\n   [all other lines]

* Space and time are specified as points (ex: x,y, or t), or ranges (ex: x0~x1, t0~t1). 

* Simulation mode, as opposed to real-time mode, relies on a different algorithm that uses simulated time.  When running a PILOTS application in simulation mode, the -Dtimespan=tb~te (where tb and te are both timestamps) argument must be specified. 

* See $PILOTS_HOME/pilots/data for example input files.

* As a convenient example, we will show how to run the twice example found in $PILOTS_HOME/pilots/examples/twice.  In this example, the PILOTS application listens to the port 8888, and sends the output to 127.0.0.1:9999.  We have provided the external software components that handle the input (pilots.util.SimpleInputProducer) and the output (pilots.util.ChartServer).  These external components are invoked using the scripts in the $PILOTS_HOME/pilots/examples/twice directory.  
    1. Compile the Twice.plt example program using:

       $ java -Dpackage=pilots.examples.twice pilots.compiler.PilotsCompiler Twice.plt

    2. Now compile the resulting Twice.java file using:
    
       $ javac
    
    3. Finally we must rebuild the pilots.jar file.  This can be done in one line by executing the following command from the $PILOTS_HOME/classes directory:

       $ jar cf ../lib/pilots.jar `find pilots/compiler/` `find pilots/util/` `find pilots/runtime/` `find pilots/examples/`

* Now $PILOTS_HOME/classes/pilots/twice/Twice.class exists, and the application can be run by opening four separate terminals and executing the following steps (from the $PILOTS_HOME/pilots/examples/twice directory):
    1. execute $./outputHandler (external software component that listens for output on port 9999 and graphs it)

    2. On another terminal, execute 
        $java pilots.examples.twice.Twice -input=8888 -outputs=127.0.0.1:9999 -tau=0.6 -omega=10 (or just run the twice script)

    3a. On the 3rd terminal, execute $./twiceProducerA jitter   (where jitter is a number from 0-100 corresponding to the percentage of timing jitter)
    3b. On the 4th terminal, execute $./twiceProducerB jitter

* Try to execute steps 3a and 3b immediately after one another for the best results.

* After running ./outputHandler a window should pop up to begin listening to the socket and graphing the output.

* Simple errors (failure to generate data) can be simulated by simply killing one of the Producer processes.

5. Running examples
----------------------------------------------------------------------------------------------

* There are several test scripts under *$PILOTS_HOME/pilots/examples directory* to simulate experiments shown in the publications.
Please note that there have been syntax changes in the PILOTS language since the earlier papers, so the *.plt files look different.

* For convenience, the examples come with scripts to make compiling and running the applications much easier.  Each example has an associated build script that can be used for quick compilation, along with scripts to invoke the application and its external components.

* Each example is initially compiled when the PILOTS language is build, so recompiling the examples is unnecessary unless changes are made to the originals.

* twice -- pilots.examples.twice (real-time version of twice example)

     $./outputHandler
     $./twice
     $./twiceProducerA jitter
     $./twiceProducerB jitter

* twicesim -- pilots.examples.twicesim (simulated-time version of twice example)
     $./outputHandler
     $./twicesim
     $./twiceProducer
     
     -Note that after ./twiceProducer has been executed, you must activate the window running ./twicesim and press any key to initiate the simulation.

* speedcheck -- pilots.examples.speedcheck (simulated-time example using real flight data)

Go to the *$PILOTS_HOME/pilots/examples/speedcheck* directory and run the following script in different terminals.

     $./outputHandler
     $./speedCheck 
     $./speedCheckProducer

     -Note that after ./speedCheckProducer (or equivalent) has been executed, you must activate the window running ./speedCheck and press any key to initiate the simulation.

* You can simulate specific errors by replacing *speedCheckProducer* with one of *speedCheckProducer_PitotTubeFail*, *speedCheckProducer_GpsFail*, and *speedCheckProducer_BothFail*.


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

* Better syntax support for intervals (in signature constraints)
    - i.e., K in (0,10) or K not in (-10,10)

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

  
