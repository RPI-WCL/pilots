# Tuninter (simulated mode)

To run this example, we use three separate terminals.

In **Terminal 1**, compile [Tuninter.plt](./Tuninter.plt) using the commands:
```
$ plcsim Tuninter.plt
$ javac Tuninter.java
```

In **Terminal 2**, launch [outputHandler](./outputHandler):
```
$ ./outputHandler
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, in order to show the ground truth data, execute [groundtruth_producer](./groundtruth_producer) (this will input data directly to outputHandler):
```
$ ./groundtruth_producer
```

In **Terminal 3**, in order to produce the erroneous data, execute [test_producer](./test_producer):
```
$ ./test_producer
```

Finally, press **enter** on **Terminal 1** to start the simulation.
