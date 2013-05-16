program SpeedCheck;
	inputs
		wind_speed, wind_angle (x,y,z) using euclidean(x,y), interpolate(z,2);
		air_speed, air_angle (x,y,t) using euclidean(x,y), closest(t);
		ground_speed, ground_angle (x,y,t) using euclidean(x,y), closest(t);
	outputs
		o: ground_speed - sqrt( air_speed * air_speed + 
		   				  		2 * air_speed * wind_speed * cos((2 * PI/360) * (wind_angle - air_angle)) +
								wind_speed * wind_speed ) at every 1 min;
	errors
		e: ground_speed - sqrt( air_speed * air_speed + 
		   				  		2 * air_speed * wind_speed * cos((2 * PI/360) * (wind_angle - air_angle)) +
								wind_speed * wind_speed );
	signatures
		s0(K): e = K, -40 < K, K < 25     "No error";
		s1(K): e = K, 75 < K, K < 125     "Airspeed failure";
		s2(K): e = K, -175 < K, K < -125  "GPS failure";
		s3(K): e = K, -75 < K, K < -25    "Pitot tube + GPS failure";
		
	correct
		s1: air_speed = sqrt( ground_speed * ground_speed +
								2 * ground_speed * wind_speed * cos((2 * PI/360) * (ground_angle - wind_angle)) +
								wind_speed * wind_speed);
		s2: ground_speed = sqrt( air_speed * air_speed + 
		   				  		2 * air_speed * wind_speed * cos((2 * PI/360) * (wind_angle - air_angle)) +
								wind_speed * wind_speed);
end;
