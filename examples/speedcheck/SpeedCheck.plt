program SpeedCheck;
	inputs
		wind_speed, wind_angle (x,y,z) using euclidean(x,y), interpolate(z,2);
		air_speed, air_angle (x,y,t) using euclidean(x,y), closest(t);
		ground_speed, ground_angle (x,y,t) using euclidean(x,y), closest(t);
	outputs
		o: ground_speed - sqrt(air_speed*air_speed + wind_speed*wind_speed + 
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle))) at every 1 min;
	errors
		e: ground_speed - sqrt(air_speed*air_speed + wind_speed*wind_speed +
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));
	signatures
		s0(K): e = 0     "No error";
		s1(K): e = 100   "Pitot tube failure";
		s2(K): e = -150  "GPS failure";
		s3(K): e = -50   "Pitot tube + GPS failure";
		
	correct
		s1: air_speed = sqrt( ground_speed*ground_speed + wind_speed*wind_speed -
								2*ground_speed*wind_speed*cos((PI/180)*(ground_angle-wind_angle)));
								
		s2: ground_speed = sqrt( air_speed*air_speed + wind_speed*wind_speed +
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));

end;
