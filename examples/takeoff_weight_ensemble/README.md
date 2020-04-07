# Ensemble Take-off Weight Estimator


### Training of weight estimation model

To train this model, we use two seperate terminals.

In **Terminal 1**, compile the weight estimation models using the commands:
```
$ plc -t weight_kalb.trn
$ plc -t weight_krno.trn
$ javac Weight_model_kalb.java
$ javac Weight_model_krno.java
```

In **Terminal 2**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ cd ../../pilots/util/model
$ ./server.sh
```

In **Terminal 2**, run the training programs:
```
$ java Weight_model_kalb
$ java Weight_model_krno
```


### Running of program

To run this example, we use four separate terminals.

In **Terminal 1**, compile [weight_estimator_ensemble.plt](./weight_estimator_ensemble.plt) using the commands:
```
$ plcsim weight_estimator_ensemble.plt
$ javac Weight_estimator_ensemble.java
```

In **Terminal 2**, launch [outputHandler](./outputHandler):
```
$ ./outputHandler
```

In **Terminal 4**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ cd ../../pilots/util/model
$ ./server.sh
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ ./run
```

In **Terminal 3**, in order to produce the simulated data, execute [producer](./producer):
```
$ ./producer (NAME OF TRIAL)
```
The available trials can be found in the [data/TakeoffWeightEnsemble](./data/TakeoffWeightEnsemble/) folder. 


Finally, in **Terminal 1**, press **enter** to start the simulation.

