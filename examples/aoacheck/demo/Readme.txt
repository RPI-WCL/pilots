To start the demo, run the following commands on separate terminals in order:
$ ./outputHandler1
$ ./outputHandler2
$ ./outputHandler3
$ ./aoacheckdemo
$ ./aoaProducer

To add error on input data streams:
$ echo error | nc localhost 7777

To go back to normal input data streams:
$ echo noerror | nc localhost 7777

To terminate the input data streams:
$ echo bye | nc localhost 7777
