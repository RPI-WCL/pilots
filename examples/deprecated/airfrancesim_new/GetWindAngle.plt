program GetWindAngle;
	inputs
		true_air_speed, ground_speed, wind_speed (t) using closest(t);
		air_angle, ground_angle, wind_angle (t) using closest(t);
	outputs
		wind_angle_out: ground_angle - (180/PI)*asin(sin((PI/180)*(air_angle - ground_angle)) * true_air_speed / sqrt(true_air_speed*true_air_speed + ground_speed*ground_speed - 2*true_air_speed*ground_speed*cos((PI/180)*(ground_angle-air_angle)))) at every 1 sec;
end
