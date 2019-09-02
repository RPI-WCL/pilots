# AirFrance (simulated mode)

To run this example, we use three separate terminals.

In **Terminal 1**, compile [AirFrance.plt](./AirFrance.plt) using the commands:
```
$ plcsim AirFrance.plt
$ javac AirFrance.java
```

In **Terminal 2**, launch [chartserver](./chartserver):
```
$ ./chartserver
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, in order to produce the simulated data, execute [producer](./producer):
```
$ ./producer
```

Finally, in **Terminal 1**, press **enter** to start the simulation.


