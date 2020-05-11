# Multi-model training


To run this example, we use two separate terminals.

In **Terminal 1**, compile [multi_model.trn](./multi_model.trn) using the commands:
```
$ plc -t multi_model.trn
$ javac Xor_model.java
```

In **Terminal 2**, launch the machine learning server [server.sh](../../pilots/util/model/server.sh):
```
$ cd ../../pilots/util/model
$ ./server.sh
```

In **Terminal 1**, run the compiled PILOTS program using the command:
```
$ java Xor_model
```


