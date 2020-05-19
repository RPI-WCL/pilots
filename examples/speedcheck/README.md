# SpeedCheck

This example is a more practical motivating example. Real data was recorded during an actual flight (see [Quest paper](https://wcl.cs.rpi.edu/papers/quest2012.pdf) for details). This data includes air speed (measured from plane's sensor), ground speed (measured from GPS), and wind speed (measured from weather reports). Using the principles of relative forces we are able to derive equations for each of these speeds in terms of the other two. The point of this example is to show that under certain erroneous conditions, such as a pitot tube (airspeed sensor) freezing, it is possible to recompute erroneous data from redundant counterparts. This application also uses simulated mode, the data can be found in $PILOTS_HOME/data/03-Apr-2012-KALB-KFME.

To run this example, we use three separate terminals.
These terminals need to be opened all pointing to $PILOTS_HOME/examples/speedCheck.

In **Terminal 1**, compile [SpeedCheck.plt](./SpeedCheck.plt) using the commands:
```
$ plcsim SpeedCheck.plt
$ javac SpeedCheck.java
```

In **Terminal 2**, launch [outputHandler](./outputHandler):
```
$ ./outputHandler
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, execute [producer](./producer):
```
$ ./producer
```

Finally, in **Terminal 1**, press **enter** to start the simulation.

To simulate other types of error rerun the example using the other producers
```
$ ./producer_<ErrorType>
```
The available error types are "PitotTubeFail", "GPSFail", and "BothFail".
Which represent a pitot tube failure, a GPS failure, and both failing at the same time, repspectively.
