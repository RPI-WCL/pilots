/*-----------------------------------------*				
 * Squared.plt                             *
 * Written for PILOTS v0.2.1               *
 * By Richard Klockowski                   *
 * 5/22/2013                               *
 *                                         *
 * Simple PILOTS application designed to   *
 *  introduce basic syntax                 *
 *-----------------------------------------*/

program Squared;
	inputs
		x(t) using closest(t);
	outputs
		o: x*x at every 1 sec;
end