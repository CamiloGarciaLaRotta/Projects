package ev3Navigation;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread {

	private int destX;
	private int destY;
	private double absTheta; 			// absolute value w/r to X axis
	private double relTheta; 			// absolute value w/r to robot heading
	private double distanceToDest;
	protected boolean isNavigating;

	private Odometer odometer;
	private UltrasonicPoller usPoller;
	private ObstacleAvoidance avoidance;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;

	// state of the navigator
	enum State { INIT, TURNING, TRAVELLING, EMERGENCY };

	private State state;

	// for mutual exclusion
	private Object lock;

	// constructor
	public Navigator(Odometer odometer, UltrasonicPoller usPoller,
			EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.odometer = odometer;
		this.usPoller = usPoller;
		this.avoidance = new ObstacleAvoidance(this, usPoller);
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.state = State.INIT;

		lock = new Object();
	}

	public void run() {

		while (true) {

			// update position
			updateDistance();

			switch (state) {
			case INIT:
				if (this.isNavigating)
					state = State.TURNING;
				break;

			case TURNING:
				turnToDest(0, true);
				state = State.TRAVELLING;
				break;

			case TRAVELLING:

				goForward(this.distanceToDest, true);
				// we chose 14 as a limit distance to detect obstacle
				if (usPoller.getDistance() < 14) {
					Sound.beep();
					state = State.EMERGENCY;
					avoidance.setSafe(false);
					rightMotor.stop();
					leftMotor.stop();
					
					avoidance.start();
					
				} else if (arrived()) {
					leftMotor.stop();
					rightMotor.stop();

					// update coordinates to destination coordinates
					odometer.setX(this.destX);
					odometer.setY(this.destY);

					state = State.INIT;
					this.isNavigating = false;
				}
				break;

			case EMERGENCY:
				if (avoidance.getSafe())
					state = State.TURNING;
				break;
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// set destination coordinates
	public void travelTo(int destX, int destY) {
		synchronized (lock) {
			this.destX = destX;
			this.destY = destY;
		}
		state = State.TURNING;
		isNavigating = true;
	}

	// rotate heading
	// boolean true indicates turn to destination angle
	// boolean false indicates its an arbitrary angle (ex: called by obstacle avoidance)
	public void turnToDest(int angle, boolean destinationAngle) {
		
		double relativeAngle;
		
		if(destinationAngle) {
			// obtain absolute angle w/r to x axis
			this.absTheta = calculateAbsTheta();

			// obtain relative angle w/r to current heading
			this.relTheta = calculateRelTheta();

			relativeAngle = relTheta;
		} else {
			// if in movement forward, wait until done to turn
			while (leftMotor.isMoving() || rightMotor.isMoving()) {
				try {
					Thread.sleep(10); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			relativeAngle = angle;
		}

		// set in-place rotation speed
		leftMotor.setSpeed(Lab3.MOTOR_LOW);
		rightMotor.setSpeed(Lab3.MOTOR_LOW);

		// rotate the robot to the desired angle
		leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, relativeAngle), true);
		rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, relativeAngle), false);
	}

	// forward until distance to destination
	public void goForward(double distance, boolean returnToCaller) {

		// set forward speed
		leftMotor.setSpeed(Lab3.MOTOR_HIGH);
		rightMotor.setSpeed(Lab3.MOTOR_HIGH);

		// advance the robot by the desired distance
		leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), returnToCaller);
	}

	// calculate absolute destination angle w/r to X axis (degrees)
	private double calculateAbsTheta() {

		// fill array with current position
		double currPos[] = new double[2];
		odometer.getPosition(currPos, new boolean[] { true, true, false });

		// get difference of coordinates
		double diffX = destX - currPos[0];
		double diffY = destY - currPos[1];
		double absTheta;

		// different cases depending on origin and destination coordinates
		if (diffX > -2 && diffX < 2) {
			if (diffY > 0)
				absTheta = 90;
			else
				absTheta = 270;
		} else if (diffY > -2 && diffY < 2) {
			if (diffX > 0)
				absTheta = 0;
			else
				absTheta = 180;
		} else {
			absTheta = Math.toDegrees(Math.atan2(diffY, diffX));
		}

		return absTheta;
	}

	// calculate relative destination angle w/r to robot heading
	private double calculateRelTheta() {

		// obtain net angle w/r to current heading
		double relTheta = this.absTheta - Math.toDegrees(odometer.getTheta());

		// ensure shortest path is taken
		if (relTheta < -180)
			relTheta += 360;
		if (relTheta > 180)
			relTheta -= 360;

		return relTheta;
	}

	// update distance to destination
	private void updateDistance() {

		// fill array with current position
		double currPos[] = new double[2];
		odometer.getPosition(currPos, new boolean[] { true, true, false });

		// update value
		this.distanceToDest = Math.sqrt(Math.pow(destY - currPos[1], 2)
				+ Math.pow(destX - currPos[0], 2));
	}

	// verifies if distance to destination is small enough
	private boolean arrived() {
		return (this.distanceToDest < 0.8);
	}

	// convert a distance into a rotation angle for the wheels
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// convert a destination angle into a rotation angle for the wheels
	private static int convertAngle(double radius, double track, double angle) {
		return convertDistance(radius, Math.PI * track * angle / 360.0);
	}

	// ////// TO STRINGS /////////

	// robot state to print
	public String stateToString() {
		String state;

		synchronized (lock) {
			state = this.state.toString();
		}

		return state;
	}

	// destination coordinates to print
	public String destToString() {
		double x, y;

		synchronized (lock) {
			x = destX;
			y = destY;
		}

		return "(" + x + ", " + y + ")";
	}

	// destination coordinates to print
	public String thetaToString() {
		double at, rt;

		synchronized (lock) {
			at = absTheta;
			rt = relTheta;
		}

		return "abs: " + (int) at + " rel: " + (int) rt;
	}

	// distance to destination to print
	public String distanceToString() {
		double d;

		synchronized (lock) {
			d = this.distanceToDest;
		}

		return d + "";
	}
}
