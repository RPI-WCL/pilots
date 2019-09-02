# AoaSpeedCheck1

To run this example, we use three separate terminals.

In **Terminal 1**, compile [AoaSpeedCheck1.plt](./AoaSpeedCheck1.plt) using the commands:
```
$ plcsim AoaSpeedCheck1.plt
$ javac AoaSpeedCheck1.java
```

In **Terminal 2**, launch [chartserver](./chartserver):
```
$ ./chartserver
```

In **Terminal 3**, in order to show the ground truth data, execute [groundtruth_producer](./groundtruth_producer) (this will input data directly to chartserver):
```
$ ./groundtruth_producer
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, in order to produce the erroneous data, execute [test_producer](./test_producer):
```
$ ./test_producer
```

Finally, in **Terminal 1**, press **enter** to start the simulation.


