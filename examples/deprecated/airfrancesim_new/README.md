AirFrance example with adjusted wind speed
==========================================
This example shows how to calculate wind speed and angle in the normal mode, and use those data in the error modes for correction in the Air France 447 accident. Currently PILOTS does not support this strategy, so we simulate it by calculating wind speed and angle first, delete the data after the error happens and keep the data in normal mode. Weather forecast wind speed (`wind_speed.txt` and `wind_angle.txt`) is still used for error detection. When errors are detected, calculated wind speed and angle in normal mode (in `wind_speed_estimate.txt` and `wind_angle_estimate.txt`) are used for data correction.

1. Calculate wind speed
---------------------
Wind speed are calculated by air speed and ground speed.
To calculate wind speed:
~~~
plcsim GetWindSpeed.plt
javac GetWindSpeed.java
~~~

Open three terminals,
~~~
./outputHandler
./windSpeed
./airfranceProducer
~~~

Hit the enter key in the `./windSpeed` terminal after all the input data are read. Finally, copy results from the `./outputHandler` terminal, and save the output results in `wind_speed_estimate.txt`.


2. Calculate wind angle
----------------------
Similar to calculating wind speed.
~~~
plcsim GetWindAngle.plt
javac GetWindAngle.java
~~~

Open three terminals,
~~~
./outputHandler
./windAngle
./airfranceProducer
~~~

Hit enter key in the `./windAngle` terminal after all the input data are read. Finally, copy results from the `./outputHandler` terminal, and save the output results in `wind_angle_estimate.txt`.


3. Keep wind speed and angle in normal mode
-------------------------------------------
Run AirFrance.plt to see that the pitot tube failure began at `:2009-06-01 031005000-0400:`. 

We keep the calculated wind speed till 8 seconds before the error occurred and delete the rest of data from `wind_speed_estimate.txt` and `wind_angle_estimate.txt` respectively. So data after `:2009-06-01 030957000-0400:` is deleted from both files. Finallly, `wind_speed_estimate.txt` and `wind_angle_estimate.txt` look as follows:

* `wind_speed_estimate.txt`
  ~~~
  #wind_speed_estimate
  :2009-06-01 030900000-0400:73.83209018773378
   ...
  :2009-06-01 030956000-0400:67.60861674205357
  :2009-06-01 030957000-0400:66.34955669599627
  ~~~

* `wind_angle_estimate.txt`
  ~~~
  #wind_angle_estimate
  :2009-06-01 030900000-0400:-56.40196553928595
   ...
  :2009-06-01 030956000-0400:-53.79876637536205
  :2009-06-01 030957000-0400:-59.301172180057335
  ~~~


4. Run AirFrance.plt and AirFranceDemo.java
-------------------------------------------
Run both AirFrance.plt and AirFranceDemo.java to see that they produce more accurate corrected data in the pitot tube failure mode than before (AirFrance.plt and AirFranceDemo.java work the same as in example "airfrancesim"). 

