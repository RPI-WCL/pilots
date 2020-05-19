# Twice (simulated mode)

To run this example, we use three separate terminals.

In **Terminal 1**, compile [Twice.plt](./Twice.plt) using the commands:
```
$ plcsim Twice.plt
$ javac Twice.java
```

In **Terminal 2**, launch [outputHandler](./outputHandler):
```
$ ./outputHandler
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./runsim
```

In **Terminal 3**, to produce the execute [producer](./producer):
```
$ ./producer
```

Finally, in **Terminal 1**, press **enter** to start the simulation.


Note: that failures are simulated by the input data themselves. The files in $PILOTS_HOME/data/11-May-2013_Twice were generated programmaticaly to exhibit certain errors at certain times (see the [publication](https://wcl.cs.rpi.edu/papers/dddas2013.pdf) regarding error detection and correction for details).

To run the program with error detection and recovery:
In Terminal 1
```
$ plc TwiceCorrect.plt
$ javac TwiceCorrect.java
```
then instead of calling runsim
```
$ ./runCorrect
```