# README for PILOTS ver 0.6

PILOTS (**P**rogramm**I**ng **L**anguage for spati**O**-**T**emporal data **S**treaming applications) is a highly declarative programming language for spatio-temporal streaming applications.
It is capable of detecting and correcting data errors through user-defined *error signatures*.

PILOTS has been successfully applied to avionics applications. Most notably, we have shown that PILOTS can fix data errors caused by pitot tube sensor failures which occurred in [Air France Flight 447 accident](http://wcl.cs.rpi.edu/papers/bdse2013.pdf). For more information, visit [the PILOTS web site](http://wcl.cs.rpi.edu/pilots/) and look at [related papers](https://wcl.cs.rpi.edu/bib/Keyword/DATA-STREAMING.html).

In version 0.6, we added the following new features to the PILOTS grammar:
* Support for supervised machine learning with `trainer` grammar.
* Support for adding new machine learning algorithms.
* Introduction of a new compiler flag to compile `trainer` (.trn) files.
* Take-off weight estimation examples.

In version 0.5, we added the following new features to the PILOTS grammar:
* Support for multiple models of analytical redundancy (for details, see our [DASC2019 paper](http://wcl.cs.rpi.edu/papers/DASC2019_imai.pdf)). Example programs to support this feature are available under [examples/multi_redundancy](./examples/multi_redundancy) directory.
  - Support for multiple error values under the `errors` section.
  - Support for multiple `estimate` clauses under the `signatures`and `modes` sections
  (as a side effect of this new feature, the `when` clause is now deprecated).
  - Enhancement of the `modes` section to support general boolean expressions.
  - Scoped naming for output variables using the `namespace` command line option (e.g., output variable `x`   from a child program with `--namespace A` option can be referred to as `A.x` in the parent program).
    Example is available [here](./examples/multi_redundancy/aoaspeedcheck3/run_speedcheck).
* Reserved `mode` variable to store the estimated mode.
* Introduction of `constants` section to declare constant values.
* Support for `x^n` expression to denote the n-th power of x.

In version 0.5, there are some new features to the PILOTS runtime system and its utilities:
* Arguments to PILOTS programs are now handled by argparse4j.
* Log messaging with Java Logging APIs. Logging levels are configurable through
  [`logging.properties`](logging.properties).
* [`MultiChartsServer`](./pilots/util/MultiChartsServer.java) to show multiple plots in one window. Settings for multiple plots can be configured through a single yaml file ([example yaml file](./examples/airfrancesim/charts_conf.yaml)). Due to this functionality, JFreeChart and JCommon must be updated to v1.0.19 and v1.0.23 respectively.

*Note: The following commands shown for the command line are assumed to be implemented in the bash shell* 


## 1. Software Requirements

* Java JDK 1.8 or newer.
* Bash shell to run [example PILOTS programs](./examples).
* (Optional) [JavaCC](http://javacc.org/) if you want to modify the PILOTS grammar.
For machine learning component of PILOTS:
* Python 3
* Python libraries: numpy, scipy, scikit-learnnumpy, pickle, and flask
  
## 2. Downloading PILOTS Library

* Download a PILOTS release either from [the PILOTS web site](http:/wcl.cs.rpi.edu/pilots/) or [the PILOTS github page](https://github.com/RPI-WCL/pilots).

* Dependencies of the PILOTS library are included in [`lib`](./lib) directory.


## 3. Getting Started with PILOTS

* **Setting environment variables / aliases**

  - In `.bashrc`, set the `PILOTS_HOME` environment variable to store the directory containing this README (i.e., root directory of the PILOTS installation).
  For example, if this README is installed in **/home/user/software/pilots**, an export command to set `PILOTS_HOME` looks as follows:
  ```
  export PILOTS_HOME=/home/user/software/pilots
  ```
  Note that the directory containing this README is referred to as `$PILOTS_HOME` hereafter.


  - Followed by the above export command for `PILOTS_HOME`, set the following `CLASSPATH` environment variable and compiler command aliases in `.bashrc`:
  ```
  export CLASSPATH=.:$PILOTS_HOME/lib/*
  alias plc='java pilots.compiler.PilotsCompiler'
  alias plcsim='java pilots.compiler.PilotsCompiler --sim'
  ```
  
* **Building a PILOTS jar file**

  In `$PILOTS_HOME`, run
  ~~~
  $ ./build.sh
  ~~~
  pilots.jar will be created under the `$PILOTS_HOME/lib` directory.


* **(Optional) Model setup**

	In PILOTS, the model function is a function which uses a machine learning model to generate an output based on given input. If you decide to use the models in PILOTS, please see [pilots/util/model/](https://github.com/RPI-WCL/pilots/tree/master/pilots/util/model) for instruction.

* **Running example PILOTS programs**: See [PILOTS tutorial](https://wcl.cs.rpi.edu/pilots/tutorial/index.html).

* **Running examples with learning model**: See the READMEs in examples such as [Prediction Twice](https://github.com/RPI-WCL/pilots/tree/master/examples/prediction_twice) or [N4207P Sim](https://github.com/RPI-WCL/pilots/tree/master/examples/N4207P_sim).


## 4. Limitations

* Language only supports error signatures that are a constant, or a linear function. Examples are:
  - S: e = 100;
  - S(K): e = K, abs(K) > 25;
  - S(K): e = 2*t + K, K < 0, k > -100;

* The `when` clause introduced in our [Cluster Computing journal article](http://wcl.cs.rpi.edu/papers/pilots-cluster.pdf) is deprecated due to the support for multiple `estimate` clauses under the `signatures` and `modes` sections in v0.5. The previous implementation of the `when` clause assumed that there is only one `estimate` clause per signature, but we decided to give priority to the support for multiple `estimate` clauses in v0.5. 


## 5. Future Work

* Examples of potential additions for federated learnign and methods to create ensemble models can be seen in the [future work](https://github.com/RPI-WCL/pilots/tree/master/examples/future_work) example

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

* Resolve the limitation for the `when` clause described in [Limitations](#4-limitations)
