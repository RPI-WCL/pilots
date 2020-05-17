# AoaCheck (real-time mode)

To run this example, we use four separate terminals.

In **Terminal 1**, compile [AoaCheck.plt](./AoaCheck.plt) using the commands:
```
$ plc AoaCheck.plt
$ javac AoaCheck.java
```

In **Terminal 2**, launch [outputHandler](./outputHandler):
```
$ ./outputHandler
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, start producing an input data stream using the command:
```
$ ./aoa_producer
```

In **Terminal 4**, to add error on input data streams:

```
$ echo error | nc localhost 7777
```

To go back to normal input data streams:

```
$ echo noerror | nc localhost 7777
```

To terminate the input data streams:

```
$ echo bye | nc localhost 7777
```

