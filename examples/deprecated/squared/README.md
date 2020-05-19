# Squared: Basic example

To run this example, we use three separate terminals.

In **Terminal 1**, compile [Squared.plt](./Squared.plt) using the commands:
```
$ plc Squared.plt
$ javac Squared.java
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
