# Take-off Weight Estimator


### Training of weight estimation model

To train this model, we use two seperate terminals.

In **Terminal 1**, compile [weight_model.trn](./weight_model.trn) using the commands:
```
$ plc -t Weight_model.trn
$ javac Weight_model.java
```

In **Terminal 2**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ pltserver
```

In **Terminal 1**, run the training program:
```
$ java Weight_model
```


### Running of program

To run this example, we use four separate terminals.

In **Terminal 1**, compile [weight_estimator.plt](./weight_estimator.plt) using the commands:
```
$ plcsim Weight_estimator.plt
$ javac Weight_estimator.java
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
The available trials can be found in the [data/TakeoffWeightEst](../../data/TakeoffWeightEst/) folder. 


Finally, in **Terminal 1**, press **enter** to start the simulation.

