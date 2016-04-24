AirFrance example with adjusted wind speed
==========================================

1.Calcuate wind speed
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

Hit enter key in the `./windSpeed` terminal after all the input data are read. Finally, copy results from the `./outputHandler` terminal, and save the output results in `wind_speed_estimate.txt`.


2.Calculate wind angle
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


3.Keep wind speed and angle in normal mode
-------------------------------------------
Run AirFrance.plt to see that the pitot tube failure began at `:2009-06-01 031002000-0400:`. We keep the calculated wind speed till 5 seconds before the error occurred. So data after `:2009-06-01 030957000-0400:` is deleted from `wind_speed_estimate.txt` and `wind_angle_estimate.txt`.


4.Use normal mode wind speed to correct data
---------------------------------------------
Weather forecast wind speed (`wind_speed.txt` and `wind_angle.txt`) is still used for error detection. When errors are detected, calculated wind speed and angle in normal mode (in `wind_speed_estimate.txt` and `wind_angle_estimate.txt` are used to correct data). Input data of `AirFrance.plt` and `airfranceProducer` are modified accordingly.


5.AirFrance.plt and AirFranceDemo.java
---------------------------------------
AirFrance.plt and AirFranceDemo.java works the same as in example "airfrancesim". The correction in pitot tube failure mode are more accurate than before.
