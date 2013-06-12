README for PILOTS ver 0.2.2
===============================================

1. Software Requirements
----------------------------------------------------------------------------------------------
* Java JDK 1.6 or newer
* (Optional) [JFreeChart](http://www.jfree.org/jfreechart/download.html) 1.0.14 or newer to visualize outputs from PILOTS applications.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify PILOTS grammar.
  
2. Downloading PILOTS library and JFreeChart
----------------------------------------------------------------------------------------------
* Download the the pilots tarball from wcl.cs.rpi.edu/pilots.
  The directory to which this tarball is extracted to will be referred to as *$PILOTS_HOME*.

* The jar files required for JFreeChart (jfreechart-1.0.14.jar and jcommon-1.0.17.jar) are included in the *$PILOTS_HOME/lib* directory.
  These libraries are under the GNU LGPL (see *$PILOTS_HOME/lib/lgpl.html*) for details.

* When using the PILOTS libraries be sure that your java CLASSPATH includes the three .jar files found in *$PILOTS_HOME/lib*.  These include jfreechar-1.0.14.jar, jcommon-1.0.17.jar and pilots.jar.  We have provided an example of setting the CLASSPATH along with some recommended aliases in the *$PILOTS_HOME/setenv* script.

3. Getting Started With PILOTS - Running Pre-compiled Examples
----------------------------------------------------------------------------------------------
See PILOTS tutorial at https://wcl.cs.rpi.edu/pilots/tutorial/index.html

4. Limitations/Known bugs
----------------------------------------------------------------------------------------------
* PILOTS is still in a proof-of-concept state

* default external software components are not particularly customizable or user friendly.

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
