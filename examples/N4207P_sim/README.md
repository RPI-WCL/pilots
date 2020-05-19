# N4207P Experimental Take-off Weight Estimation


### Training of weight estimation model

To train this model, we use two seperate terminals.

In **Terminal 1**, compile the weight estimation model using the commands:
```
$ plc -t Weight_exp.trn
$ javac Weight_model_exp.java
```

In **Terminal 2**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ pltserver
```

In **Terminal 2**, run the training programs:
```
$ java Weight_model_exp
```


### Running of program

To run this example, we use four separate terminals.

In **Terminal 1**, compile [N4207P.plt](./N4207P.plt) using the commands:
```
$ plcsim N4207P.plt
$ javac N4207P.java
```

In **Terminal 2**, launch [outputHandler](./outputHandler):
```
$ ./outputHandler
```

In **Terminal 4**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ pltserver
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, in order to produce the simulated data, execute [producer](./producer):
```
$ ./producer (NAME OF TRIAL)
```
The available trials can be found in the [data/TakeoffWeightExperiment](../../data/TakeoffWeightExperiment/) folder: Trial1 and Trial2.

Finally, in **Terminal 1**, press **enter** to start the simulation.

