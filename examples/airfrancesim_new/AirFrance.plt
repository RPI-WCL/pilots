program AirFrance;
	inputs
		true_air_speed, ground_speed, wind_speed, wind_speed_estimate (t) using closest(t);
		air_angle, ground_angle, wind_angle, wind_angle_estimate (t) using closest(t);
	outputs
		true_air_speed_out: true_air_speed at every 1 sec;
		/*error_out: ground_speed - sqrt(true_air_speed*true_air_speed + wind_speed*wind_speed +
		  		2*true_air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle))) at every 1 sec;*/
    errors
		e: ground_speed - sqrt(true_air_speed*true_air_speed + wind_speed*wind_speed +
		  		2*true_air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle)));
    signatures
    /* v_a=470, a=0.1, c=0.2...0.33 */
        s0(K): e = K, -47 < K, K < 47           "No error";
        s1(K): e = K, 220.9 < K, K < 517       "Pitot tube failure"
		estimate true_air_speed = sqrt(ground_speed*ground_speed + wind_speed_estimate*wind_speed_estimate -
			  	2*ground_speed*wind_speed_estimate*cos((PI/180)*(ground_angle-wind_angle_estimate)));
        s2(K): e = K, -517 < K, K < -423        "GPS failure"
		estimate ground_speed = sqrt(true_air_speed*true_air_speed + wind_speed_estimate*wind_speed_estimate +
                2*true_air_speed*wind_speed_estimate*cos((PI/180)*(wind_angle_estimate-air_angle)));
        s3(K): e = K, -203.66 < K, K < -47      "Pitot tube + GPS failure";
end
