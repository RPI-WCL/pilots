program AirFrance;
	inputs
		true_air_speed, ground_speed, wind_speed (t) using closest(t);
		air_angle, ground_angle, wind_angle (t) using closest(t);
	outputs
		true_air_speed_out: true_air_speed at every 1 sec;
    errors
		e: ground_speed - sqrt(true_air_speed*true_air_speed + wind_speed*wind_speed + 
		  		2*true_air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));
    signatures
    /* v_a=470, c=0.2...0.33 */
        s0(K): e = K, -47 < K, K < 47           "No error";
        s1(K): e = K, 266.33 < K, K < 423       "Pitot tube failure";
        s2(K): e = K, -517 < K, K < -423        "GPS failure";
        s3(K): e = K, -203.66 < K, K < -47      "Pitot tube + GPS failure";
    correct
        s1: true_air_speed = sqrt(ground_speed*ground_speed + wind_speed*wind_speed - 
			  	2*ground_speed*wind_speed*cos((PI/180)*(ground_angle-wind_angle)));
        s2: ground_speed = sqrt(true_air_speed*true_air_speed + wind_speed*wind_speed +
                2*true_air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));
end
