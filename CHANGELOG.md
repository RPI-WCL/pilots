# Detailed PILOTS README (for PILOTS users)

## Changelog

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


## Limitations

* Language only supports error signatures that are a constant, or a linear function. Examples are:
  - S: e = 100;
  - S(K): e = K, abs(K) > 25;
  - S(K): e = 2*t + K, K < 0, k > -100;

* The `when` clause introduced in our [Cluster Computing journal article](http://wcl.cs.rpi.edu/papers/pilots-cluster.pdf) is deprecated due to the support for multiple `estimate` clauses under the `signatures` and `modes` sections in v0.5. The previous implementation of the `when` clause assumed that there is only one `estimate` clause per signature, but we decided to give priority to the support for multiple `estimate` clauses in v0.5.


## Future Work

* Examples of potential additions for federated learning and methods to create ensemble models can be seen in the [future work](https://github.com/RPI-WCL/pilots/tree/master/examples/future_work) example

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

* Resolve the limitation for the `when` clause described above
