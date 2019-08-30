# AoaSpeedCheck2

To run this example, we use two child PILOTS applications, [SpeedCheck](../speedcheck) and [AoaCheck](../AoaCheck), and one parent PILOTS application [AoaSpeedCheck2](./AoaSpeedCheck2.plt) over five terminals.

### Preparing parent PILOTS program

In **Terminal 1**, compile [AoaSpeedCheck2.plt](./AoaSpeedCheck2.plt) using the commands:
```
$ plcsim AoaSpeedCheck2.plt
$ javac AoaSpeedCheck2.java
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

### Preparing child PILOTS programs

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

In **Terminal 5**, go to [aoacheck](../aoacheck) directory and compile [AoaCheck.plt](../aoacheck/AoaCheck.plt):
```
$ cd ../aoacheck
$ plcsim AoaCheck.plt
$ java AoaCheck.java
```
Come back to this directory and run the compiled [AoaCheck.plt](../aoacheck/AoaCheck.plt) program:
```
$ cd aoaspeedcheck2
$ ./run_aoacheck
```

### Running the demo
In **Terminal 3**, in order to produce the erroneous data, execute [test_producer](./test_producer):
```
$ ./test_producer
```

Finally, press **enter** on each terminal in the following order to start the simulation: **Terminal 4** (SpeedCheck), **Terminal 5** (AoaCheck), and **Terminal 1** (AoaSpeedCheck2).

