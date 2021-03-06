# PILOTS ver 0.6

PILOTS (**P**rogramm**I**ng **L**anguage for spati**O**-**T**emporal data **S**treaming applications) is a highly declarative programming language for spatio-temporal streaming applications.
It is capable of detecting and correcting data errors through user-defined *error signatures*.

PILOTS has been successfully applied to avionics applications. Most notably, we have shown that PILOTS can fix data errors caused by pitot tube sensor failures which occurred in [Air France Flight 447 accident](http://wcl.cs.rpi.edu/papers/bdse2013.pdf). For more information, visit [the PILOTS web site](http://wcl.cs.rpi.edu/pilots/) and look at [related papers](https://wcl.cs.rpi.edu/bib/Keyword/DATA-STREAMING.html).

*Note: The following commands shown for the command line are assumed to be implemented in the bash shell* 

## 0. Prerequisite Knowledge

Basics of using a terminal
Installing programs for use on the terminal

## 1. Software Requirements

* Java JDK 1.8 or newer.
* Bash shell to run [example PILOTS programs](./examples).
* (Optional) [JavaCC](http://javacc.org/) if you want to modify the PILOTS grammar.
For machine learning component of PILOTS:
* Python 3
* Python libraries: numpy, scipy, scikit-learn, pickle, and flask
Using pip they can be installed using the following line:
```
pip3 install numpy scipy scikit-learn pickle flask
```
  
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
  alias pltserver='$PILOTS_HOME/pilots/util/model/server.sh'
  ```
  
* **Building a PILOTS jar file**

  In `$PILOTS_HOME`, run
  ~~~
  $ ./build.sh
  ~~~
  pilots.jar will be created under the `$PILOTS_HOME/lib` directory.

## 4. Running a PILOTS program

* First read about PILOTS in the [wiki](https://github.com/RPI-WCL/pilots/wiki)
* Follow along the README in a simple program: [Twice](https://github.com/RPI-WCL/pilots/tree/master/examples/twice), [Twice Sim](https://github.com/RPI-WCL/pilots/tree/master/examples/twicesim)
* Try some other programs [Aoacheck](https://github.com/RPI-WCL/pilots/tree/master/examples/aoacheck)

## 5. Running a PILOTS program with machine learning

In PILOTS, the __model__ function is a function which uses a machine learning model to generate an output based on a given input. 

* First try the [Prediction Twice](https://github.com/RPI-WCL/pilots/tree/master/examples/prediction_twice) program
* Try the [N4207P Sim](https://github.com/RPI-WCL/pilots/tree/master/examples/N4207P_sim) program
* Experiment with the [Takeoff Weight Estimation](https://github.com/RPI-WCL/pilots/tree/master/examples/takeoff_weight_est) program
* Try making your own machine learning algorithm, please see [pilots/util/model/](https://github.com/RPI-WCL/pilots/tree/master/models) for instruction



