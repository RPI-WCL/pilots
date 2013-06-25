program AirFrance;
	inputs
		air_speed, ground_speed, wind_speed (t) using closest(t);
		air_angle, ground_angle, wind_angle (t) using closest(t);
	outputs
/*		wind_speed_o: wind_speed at every 5 sec;*/
/*		wind_angle_o: wind_angle at every 5 sec;*/
		air_angle_o: air_angle at every 5 sec;
/*		o: ground_speed - sqrt(air_speed*air_speed + wind_speed*wind_speed + 
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle))) at every 5 sec;*/
							

end
