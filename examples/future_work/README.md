# Future Work
 
These files show examples of code that use the proposed extensions to PILOTS
for federated learning and methods for creating simple ensemble models.
As proposed in **Learning Models from Avionics Data Streams**.

## Federated Learning

[Federated_trainer.trn](./Federated_trainer.trn) shows an example of a PILOTS program which trains a neural network
across 10 different computation nodes over 20 rounds using the Federated Averaging algorithm.

## Ensemble Models

[Ensemble_methods.plt](./Ensemble_methods.plt) shows the proposed ensemble methods: __average__, __majority__, __weighted__, and __decision__.
The program combines the results of three different models which calculate density altitude.
Currently, this functionality can be performed using the EnsembleMethods model.
However, the **EnsembleMethods** models lacks the clarity and simplicity shown in **Ensemble_methods.plt**.
