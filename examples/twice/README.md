# Twice (real-time mode)

Twice is a very basic streaming application with two input streams a and b. At every given point in time, the value on stream b should be twice the value on stream a. We use PILOTS to easily produce this application. Furthermore, we were able to observe the PILOTS application to produce error correcting code. This is the first example that is used to exemplify error signatures.

To run this example, we use four separate terminals.

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
$ ./run
```


In the last two terminals run both producers. Try to make sure that these processes are initialized as close as possible. Starting one a long while after the other while cause an out of sync error in the PILOTS application.
In **Terminal 3**,  execute [producerA](./producerA):
```
$ ./producerA
```
In **Terminal 4**,  execute [producerB](./producerB):
```
$ ./producerB
```

In order to simulate a failure in one of the streams, kill the data producing process. For example to simulate a failure in stream b you can activate the terminal that is running producerB and kill the process (using CTRL+C). These errors produce characteristic patterns which we describe using error signatures. 

To run the program with error detection and recovery:
In Terminal 1
```
$ plc TwiceCorrect.plt
$ javac TwiceCorrect.java
```
then instead of calling run
```
$ ./runCorrect
```