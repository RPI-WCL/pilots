# README for PILOTS ver 0.5

PILOTS (**P**rogramm**I**ng **L**anguage for spati**O**-**T**emporal data **S**treaming applications) is a highly declarative programming language for spatio-temporal streaming applications.
It is capable of detecting and correcting data errors through user-defined *error signatures*.

PILOTS has been successfully applied to avionics applications. Most notably, we have shown that PILOTS can fix data errors due to pitot tube sensor failures which occurred in [Air France Flight 447 accident](http://wcl.cs.rpi.edu/papers/bdse2013.pdf). For more information, visit [the PILOTS web site](http://wcl.cs.rpi.edu/pilots/) and look at [related papers](https://wcl.cs.rpi.edu/bib/Keyword/DATA-STREAMING.html).

In version 0.5, we added the following new features to the PILOTS grammar:
* Support for multiple models of analytical redundancy (for details, see our [DASC2019 paper](http://wcl.cs.rpi.edu/papers/DASC2019_imai.pdf). Example programs to support this feature are available under [examples/multi_redundancy](./examples/multi_rendundancy) directory.
  - Support for multiple error values under the `errors` section.
  - Support for multiple `estimate` clauses under the `signatures` section (as a side effect of this new
  feature, the `when` clause is now deprecated).
  - Enhancement of the `modes` section to support general boolean expressions.
  - Scoped naming for output variables from child programs (e.g., variable `x` from program `A`
    can be referred to as `A.x` in the parent program).
  - Reserved `mode` variable to store the estimated mode.
* Introduction of constants` section to declare constant values.
* Support for x^n expression to denote the n-th power of x.

In version 0.5, there are some new features to the PILOTS runtime system and its utilities:
* Arguments to PILOTS programs are handled by argparse4j.
* Log messages are managed by Java Logging APIs. Logging levels are configurable through
  [`logging.properties`](logging.properties).
* ['MultipleChartServer`](./pilots/util/MultipleChartServer.java) to show multiple plots in one windows. Settings for each plot can be configured through a yaml file ([example](./examples/airfrancesim/charts_conf.yaml)). Due to this functionality, JFreeChart and JCommon must be updated to v1.0.19 and v1.0.23 respectively.

*Note: The following commands shown for the command line are assumed to be implemented in the bash shell* 


## 1. Software Requirements

* Java JDK 1.8 or newer.
* (Optional) [JFreeChart](http://www.jfree.org/jfreechart/download.html) 1.0.19 or newer to visualize outputs from PILOTS applications.
* (Optional) [JavaCC](http://javacc.java.net/) if you want to modify the PILOTS grammar.
  
## 2. Downloading PILOTS library and its dependencies

* Download a PILOTS release either from [the PILOTS web site](http:/wcl.cs.rpi.edu/pilots/) or [the PILOTS github page](https://github.com/RPI-WCL/pilots).

* Note that the directory containing this README is referred to as `$PILOTS_HOME`.

* Dependencies of the PILOTS library are included in `$PILOTS_HOME/lib`:

 - JFreeChart requires `jfreechart-1.0.19.jar` and `jcommon-1.0.23.jar` (GNU LGPL, see `$PILOTS_HOME/lib/lgpl.html`)
 - Json operations require `json-java.jar` ( The Json License, see `$PILOTS_HOME/lib/The JSON License.html` )


## 3. Getting Started with PILOTS

* **Building a PILOTS jar file**

  In `$PILOTS_HOME`, run
  ~~~
  $ ./build.sh    // (for Windows, use build.bat instead)
  ~~~
  pilots.jar will be created under the `$PILOTS_HOME/lib` directory.

* **Configure aliases**

  In order to make sure that aliases for the `plc` and `plcsimM` compiler commands are correctly figured as well as the definition of $PILOTS_HOME, make sure that you are in the [*root directory*](https://github.com/RPI-WCL/pilots) of the project and then use the following command:

  ~~~
  source setenv
  ~~~

* **(Optional) Predictive Function setup**

	In PILOTS, the predictive function is a function which uses a learning model to generate predicted output based on given input. If you decide to use the predictive function in PILOTS, please see [pilots/util/learningmodel/](https://github.com/RPI-WCL/pilots/tree/learn_dev/pilots/util/learningmodel) for instruction.

* **Running example PILOTS programs**: See [PILOTS tutorial](https://wcl.cs.rpi.edu/pilots/tutorial/index.html).

* **Running examples with learning model**: Refer to the [Detailed Overview of Implementing Learning Model](https://github.com/RPI-WCL/pilots/wiki/Detailed-Overview-of-Implementing-Learning-Model)


## 4. Limitations

* Language only supports error signatures that are a constant, or a linear function. Examples are:
  - S: e = 100;
  - S(K): e = K, abs(K) > 25;
  - S(K): e = 2*t + K, K < 0, k > -100;

* The `when` clause introduced in our [Cluster Computing article](http://wcl.cs.rpi.edu/papers/pilots-cluster.pdf) was deprecated in v0.5 because the previous implementation assumed only one `estimate` clause
per error signature.


## 5. Future Work

* Types of error signatures we would like to support eventually:
    -   S(K): e = K
    -   S(K): e = K*t^2
    -   S:    e = t
    -   S(K1,K2): e = K1*t + K2, K1 != 0, K2 > threshold

* Better syntax support for intervals (in signature constraints)
    - i.e., K in (0,10) or K not in (-10,10)

* Simulation mode with support for dynamic simulation inputs
    - Multiple files, each corresponding to a particular input
    - Different directories for the different times
    - Forecast data

* Enhanced error analysis accuracy
    - Different interpolation methods
    - More types of error signatures

* Resolve the limitation for the `when` clause described in [Limitations](#limitations)
