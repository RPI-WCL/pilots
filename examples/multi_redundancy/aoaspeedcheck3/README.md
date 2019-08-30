# AoaSpeedCheck3

To run this example, we use one child PILOTS application [SpeedCheck](../speedcheck) and one parent
PILOTS application [AoaSpeedCheck3](./AoaSpeedCheck3.plt) over four terminals.

### Preparing parent PILOTS program

In **Terminal 1**, compile [AoaSpeedCheck3.plt](./AoaSpeedCheck3.plt) using the commands:
```
$ plcsim AoaSpeedCheck3.plt
$ javac AoaSpeedCheck3.java
```

In **Terminal 2**, launch [chartserver](./chartserver):
```
$ ./chartserver
```

In **Terminal 3**, in order to show the ground truth data, execute [groundtruth_producer](./groundtruth_producer) (this will input data directly to chartserver):
```
$ ./groundtruth_producer
```

In **Terminal 1**, run the compiled [AoaSpeedCheck2.plt](./AoaSpeedCheck2.plt) program using the command:
```
$ ./run_parent
```

### Preparing child PILOTS program

In **Terminal 4**, go to [speedcheck](../speedcheck) directory and compile [SpeedCheck.plt](../speedcheck/SpeedCheck.plt):
```
$ cd ../speedcheck
$ plcsim SpeedCheck.plt
$ java SpeedCheck.java
```
Come back to this directory and run the compiled [SpeedCheck.plt](../speedcheck/SpeedCheck.plt) program:
```
$ cd aoaspeedcheck2
$ ./run_speedcheck
```

### Running the demo
In **Terminal 3**, in order to produce the erroneous data, execute [test_producer](./test_producer):
```
$ ./test_producer
```

Finally, press **enter** on each terminal in the following order to start the simulation: **Terminal 4** (SpeedCheck) and then **Terminal 1** (AoaSpeedCheck3).

