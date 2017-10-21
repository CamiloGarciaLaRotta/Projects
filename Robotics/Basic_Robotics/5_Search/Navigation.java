package ev3Search;

/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	public static int ACCELERATION = 2000;
	public static double DEG_ERR = 1.5, CM_ERR = 2;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public Navigation(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if(lSpd==0)
			leftMotor.stop(true);
		if(rSpd==0)
			rightMotor.stop(true);
		
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0)
			minAng += 360.0;

		this.turnTo(minAng, true);
		while (Math.abs(x - odometer.getX()) > CM_ERR
				|| Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
					* (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;

			this.setSpeeds(Lab5.MOTOR_HIGH, Lab5.MOTOR_HIGH);
		}
		this.turnTo(minAng, true);
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-Lab5.MOTOR_LOW, Lab5.MOTOR_LOW);
			} else if (error < 0.0) {
				this.setSpeeds(Lab5.MOTOR_LOW, -Lab5.MOTOR_LOW);
			} else if (error > 180.0) {
				this.setSpeeds(Lab5.MOTOR_LOW, -Lab5.MOTOR_LOW);
			} else {
				this.setSpeeds(-Lab5.MOTOR_LOW, Lab5.MOTOR_LOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}

	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng()))
				* distance, Math.cos(Math.toRadians(this.odometer.getAng()))
				* distance);

	}
}
