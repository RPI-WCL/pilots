program SpeedCheck;
	inputs
		wind_speed, wind_angle (x,y,z) using euclidean(x,y), interpolate(z,2);
		air_speed, air_angle (x,y,t) using euclidean(x,y), closest(t);
		ground_speed, ground_angle (x,y,t) using euclidean(x,y), closest(t);
	outputs
		o: ground_speed - sqrt(air_speed*air_speed + wind_speed*wind_speed + 
		   				  		2*air_speed*wind_speed*cos((PI/180)*(wind_angle-air_angle))) at every 1 min;

end
