package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	private int newRightTacho, newLeftTacho, oldRightTacho, oldLeftTacho;
	private double leftDistance, rightDistance, tachoDistance,thetaDistance, posDistance;
	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static final long ODOMETER_PERIOD = 25;
	
	// lock object for mutual exclusion
	private Object lock;
	
	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		// set coordinates
		x = 0.0; y = 0.0; theta = 0.0;
		
		// set tachometers
		oldRightTacho = 0; oldLeftTacho = 0; 
		newRightTacho = 0; newLeftTacho = 0;
		
		// set motors
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		rightMotor.resetTachoCount();
		leftMotor.resetTachoCount();
		
		lock = new Object();
	}

	
	public void run() {
		
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			
			// get latest tachometer count
			newLeftTacho = leftMotor.getTachoCount();	
			newRightTacho = rightMotor.getTachoCount();
			
			// calculate distance
			tachoDistance = newRightTacho - oldRightTacho;
			rightDistance = Lab3.WHEEL_RADIUS * Math.PI * tachoDistance / 180;

			tachoDistance = newLeftTacho - oldLeftTacho;
			leftDistance = Lab3.WHEEL_RADIUS * Math.PI * tachoDistance / 180;
			
			thetaDistance = (rightDistance - leftDistance) / Lab3.TRACK;
			posDistance = (leftDistance + rightDistance) / 2;
			
			// keep old values for next calculation
			oldRightTacho = newRightTacho;
			oldLeftTacho = newLeftTacho;			

			synchronized (lock) {
				// mod theta to wrap around
				theta = (theta + thetaDistance) % (2 * Math.PI);
				// avoid negatives
				if(theta < 0) theta += (2 * Math.PI);
				
				y += posDistance*Math.sin(theta);
				x += posDistance*Math.cos(theta);
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}