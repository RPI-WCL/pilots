program SpeedCheck;
	inputs
		wind_speed, wind_angle (x,y,z) using euclidean(x,y), interpolate(z,2);
		air_speed, air_angle (x,y,t) using closest(t);
		ground_speed, ground_angle (x,y,t) using closest(t);
	outputs
		o: ground_speed - sqrt(air_speed*air_speed + wind_speed*wind_speed + 
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle))) at every 1 sec;
	errors
		e: ground_speed - sqrt(air_speed*air_speed + wind_speed*wind_speed +
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));
	signatures
        /* v_a=162 */
        S0(k): e = k,  -16.2 <= k, k <=   16.2  "No error";
        S1(k): e = k,   91.8 <= k, k <=  145.8  "Pitot tube failure"
		estimate air_speed = sqrt( ground_speed*ground_speed + wind_speed*wind_speed -
                                2*ground_speed*wind_speed*cos((PI/180)*(ground_angle-wind_angle)));
        S2(k): e = k, -178.2 <= k, k <= -145.8  "GPS failure"
		estimate ground_speed = sqrt( air_speed*air_speed + wind_speed*wind_speed +
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));
        S3(k): e = k,  -70.2 <= k, k <=  -16.2  "Pitot tube + GPS failure";
end;
