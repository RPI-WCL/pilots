README for PILOTS ver 0.4.2
===============================================

PILOTS (**P**rogramm**I**ng **L**anguage for spati**O**-**T**emporal data **S**treaming applications) is a highly declarative programming language for spatio-temporal streaming applications.
It is capable of detecting and correcting data errors through user-defined *error signatures*.

PILOTS has been successfully applied to avionics applications. Most notably, we have shown that PILOTS can fix data errors due to pitot tube sensor failures which occurred in [Air France Flight 447 accident](http://wcl.cs.rpi.edu/papers/bdse2013.pdf). For more information, visit [the PILOTS web site](http://wcl.cs.rpi.edu/pilots/) and look at [related papers](https://wcl.cs.rpi.edu/bib/Keyword/DATA-STREAMING.html).

In version 0.4.2, we add [the AoaCheck example](https://github.com/RPI-WCL/pilots/tree/master/examples/aoacheck) which demonstrates how PILOTS works with erroneous Angle-of-Attack sensor data such as allegedly observed in accidents involving the Boeing 737 Max 8 ([Lion Air Flight 610](https://en.wikipedia.org/wiki/Lion_Air_Flight_610), [Ethiopian Airlines Flight 302](https://en.wikipedia.org/wiki/Ethiopian_Airlines_Flight_302)).


Please refer to [Readme](https://github.com/RPI-WCL/pilots/blob/master/examples/aoacheck/Readme.txt) for how to run the AoACheck example.

Demo video is available at: http://wcl.cs.rpi.edu/pilots/

*Note: The following commands shown for the command line are assumed to be implemented in the bash shell* 


1. Software Requirements
----------------------------------------------------------------------------------------------
* Java JDK 1.8 or newer.
* (Optional) [JFreeChart](http://www.jfree.org/jfreechart/download.html) 1.0.14 or newer to visualize outputs from PILOTS applications.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify the PILOTS grammar.
  
2. Downloading PILOTS library and its dependencies
----------------------------------------------------------------------------------------------
* Download a PILOTS release either from [the PILOTS web site](http:/wcl.cs.rpi.edu/pilots/) or [the PILOTS github page](https://github.com/RPI-WCL/pilots).

* Note that the directory containing this README is referred to as `$PILOTS_HOME`.

* Dependencies of the PILOTS library are included in `$PILOTS_HOME/lib`:

 - JFreeChart requires `jfreechart-1.0.14.jar` and `jcommon-1.0.17.jar` ( GNU LGPL, see `$PILOTS_HOME/lib/lgpl.html` )
 - Json operations require `json-java.jar` ( The Json License, see `$PILOTS_HOME/lib/The JSON License.html` )


3. Getting Started with PILOTS
----------------------------------------------------------------------------------------------
* **Building a PILOTS jar file**

  In `$PILOTS_HOME`, run
  ~~~
  $ ./build.sh    // (for Windows, use build.bat instead)
  ~~~
  pilots.jar will be created under the `$PILOTS_HOME/lib` directory.

* **Configure aliases**

  In order to make sure that aliases for the PLC and PLCSIM compiler commands are correctly figured as well as the definition of $PILOTS_HOME, make sure that you are in the [*root directory*](https://github.com/RPI-WCL/pilots) of the project and then use the following command:

  ~~~
  source setenv
  ~~~

* **(Optional) Predictive Function setup**

	In PILOTS, the predictive function is a function which uses a learning model to generate predicted output based on given input. If you decide to use the predictive function in PILOTS, please see [pilots/util/learningmodel/](https://github.com/RPI-WCL/pilots/tree/learn_dev/pilots/util/learningmodel) for instruction.

* **Running example PILOTS programs**: See [PILOTS tutorial](https://wcl.cs.rpi.edu/pilots/tutorial/index.html).

* **Running examples with learning model**: Refer to the [Detailed Overview of Implementing Learning Model](https://github.com/RPI-WCL/pilots/wiki/Detailed-Overview-of-Implementing-Learning-Model)


4. Limitations
----------------------------------------------------------------------------------------------
* The error clause only allows a single error function.

* Language only supports error signatures that are a constant, or a linear function. Examples are:
  - S: e = 100;
  - S(K): e = K, abs(K) > 25;
  - S(K): e = 2*t + K, K < 0, k > -100;


5. Future Work
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
