program GetWindSpeed;
	inputs
		true_air_speed, ground_speed, wind_speed (t) using closest(t);
		air_angle, ground_angle, wind_angle (t) using closest(t);
	outputs
		wind_speed_out: sqrt(true_air_speed*true_air_speed + ground_speed*ground_speed - 2*true_air_speed*ground_speed*cos((PI/180)*(air_angle-ground_angle))) at every 1 sec;
end
