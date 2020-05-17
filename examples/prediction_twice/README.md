# Prediction Twice


### Training of twice model

To train this model, we use two seperate terminals.

In **Terminal 1**, compile the model using the commands:
```
$ plc -t twice_trainer.trn
$ javac Twice_model.java
```
Note: The name of the resulting java file from compiling the trainer file is different. This is because the java file is named after the model being trained not the name of the trainer file. The model can be found in the [trained_models folder](../../models/trained_models).

In **Terminal 2**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ pltserver
```

In **Terminal 2**, run the training programs:
```
$ java Twice_model
```


### Running of program

To run this example, we use four separate terminals.

In **Terminal 1**, compile [prediction_twice.plt](./prediction_twice.plt) using the commands:
```
$ plcsim prediction_twice.plt
$ javac Prediction_twice.java
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
$ ./producer
```

Finally, in **Terminal 1**, press **enter** to start the simulation.

