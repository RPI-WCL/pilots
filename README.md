README for PILOTS ver 0.31
===============================================

PILOTS (**P**rogramm**I**ng **L**anguage for spati**O**-**T**emporal data **S**treaming applications) is a highly declarative programming language for spatio-temporal streaming applications.
It is capable of detecting and correcting data errors through user-defined *error signatures*.

PILOTS has been successfully applied to avionics applications. Most notably, we have shown that PILOTS can fix data errors due to pitot tube sensor failures which occurred in [Air France Flight 447 accident](http://wcl.cs.rpi.edu/papers/bdse2013.pdf). For more information, visit [the PILOTS web site](http://wcl.cs.rpi.edu/pilots/) and look at [related papers](https://wcl.cs.rpi.edu/bib/Keyword/DATA-STREAMING.html).


1. Software Requirements
----------------------------------------------------------------------------------------------
* Java JDK 1.6 or newer.
* (Optional) [JFreeChart](http://www.jfree.org/jfreechart/download.html) 1.0.14 or newer to visualize outputs from PILOTS applications.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify the PILOTS grammar.
  
2. Downloading PILOTS library and JFreeChart
----------------------------------------------------------------------------------------------
* Download a PILOTS release either from [the PILOTS web site](http:/wcl.cs.rpi.edu/pilots/) or [the PILOTS github page](https://github.com/RPI-WCL/pilots).

* Note that the directory containing this README is referred to as `$PILOTS_HOME`.

* The jar files required for JFreeChart (jfreechart-1.0.14.jar and jcommon-1.0.17.jar) are included in the `$PILOTS_HOME/lib` directory.
  These libraries are under the GNU LGPL (see `$PILOTS_HOME/lib/lgpl.html` for details).


3. Getting Started with PILOTS
----------------------------------------------------------------------------------------------
* **Building a PILOTS jar file**

  In `$PILOTS_HOME`, run
  ~~~
  $ ./build.sh    // (for Windows, use build.bat instead)
  ~~~
  pilots.jar will be created under the `$PILOTS_HOME/lib` directory.

* **Configuring the CLASSPATH environment variable**

  When using the PILOTS libraries, be sure that your java CLASSPATH includes the three .jar files found in `$PILOTS_HOME/lib` (i.e., jfreechar-1.0.14.jar, jcommon-1.0.17.jar, and pilots.jar). 
  We have provided an example of setting the CLASSPATH along with some recommended aliases in the `$PILOTS_HOME/setenv` script. To use it, run
  ~~~
  $ source setenv
  ~~~

* **Running example PILOTS programs**: See [PILOTS tutorial](https://wcl.cs.rpi.edu/pilots/tutorial/index.html).

4. What's New in This Version
----------------------------------------------------------------------------------------------
* **Connectivity to the X-Plane flight simulator**: PILOTS can now directly ingest data streams from [X-Plane](http://www.x-plane.com). Go to [pilots/util/xplane/](https://github.com/RPI-WCL/pilots/tree/master/pilots/util/xplane) directory for details.

* **Alternative wind data computation method for the Air France 447 example**: Instead of using potentially inaccurate wind forecast data, we estimate wind data from airspeed and ground speed for better accuracy and use it to correct data in case of errors. Go to [pilots/examples/airfrancesim_new](https://github.com/RPI-WCL/pilots/tree/master/examples/airfrancesim_new) directory for details.

* **Proof-of-concept model learning utility**: Toward future integration of a model learning capability to PILOTS, we developed a utility program to learn model parameters from input data streams. 
Go to [pilots/util/learningmodel/](https://github.com/RPI-WCL/pilots/tree/master/pilots/util/learningmodel) for details.



5. Limitations
----------------------------------------------------------------------------------------------
* The error clause only allows a single error function.  

* Language only supports error signatures that are a constant, or a linear function. Examples are:
  - S: e = 100;
  - S(K): e = K, abs(K) > 25;
  - S(K): e = 2*t + K, K < 0, k > -100;


6. Future Work
----------------------------------------------------------------------------------------------
* types of error signatures we would like to support eventually:
    -   S(K): e = K
    -   S(K): e = K*t^2
    -   S:    e = t
    -   S(K1,K2): e = K1*t + K2, K1 != 0, K2 > threshold

* Support for multiple error functions (will require additional logic in the error analysis phase)

* Better syntax support for intervals (in signature constraints)
    - i.e., K in (0,10) or K not in (-10,10)

* External software components: simulation and visualization
    - More defined and customizable

* Enhanced visualization (using JFreeChart)
    - Mode likelihood vector
    - Corrected output
    - Error function

* Simulation mode with support for dynamic simulation inputs
    - Multiple files, each corresponding to a particular input
    - Different directories for the different times
    - Forecast data

* Enhanced error analysis accuracy
    - Different interpolation methods
    - More types of error signatures
