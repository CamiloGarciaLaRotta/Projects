package ev3Search;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};
	
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private final int OFFSET = -8;
	private static int WALL_DIST = 30;
	private static int WALL_GAP = 3;
	private static int FILTER_OUT = 3;
	private static int filterControl;
	private static float lastDistance;
	public static String status = "";
	public static double angleA, angleB = 0;

	private Odometer odo;
	@SuppressWarnings("unused")
	private Navigation nav;
	private static SampleProvider usSensor;
	private static float[] usData;
	private LocalizationType locType;

	@SuppressWarnings("static-access")
	public USLocalizer(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, Odometer odo, Navigation nav,
			SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	public void doLocalization() {

		double angleA, angleB;
		leftMotor.setSpeed(Lab5.MOTOR_LOW);
		rightMotor.setSpeed(Lab5.MOTOR_LOW);

		if (locType == LocalizationType.FALLING_EDGE) {

			// rotate until no wall
			while (getFilteredData() < WALL_DIST + WALL_GAP) {
				leftMotor.forward();
				rightMotor.backward();
			}
			status = "first";
			// keep rotating until the robot sees another wall
			while (getFilteredData() > WALL_DIST) {
				leftMotor.forward();
				rightMotor.backward();
			}
			status = "second";
			angleA = odo.getAng();
			// switch direction and wait until it sees no wall
			while (getFilteredData() < WALL_DIST + WALL_GAP) {
				leftMotor.backward();
				rightMotor.forward();
			}
			status = "third";
			// keep rotating until the robot sees a wall, then latch the angle
			while (getFilteredData() > WALL_DIST) {
				leftMotor.backward();
				rightMotor.forward();
			}
			rightMotor.stop(true);
			leftMotor.stop(true);
			status = "fourth";
			angleB = odo.getAng();
			// if angle A is larger than B, then make it -360
			if (angleA > angleB) {
				angleA = angleA - 360;
			}
			double averageAngle = (angleA + angleB) / 2;
			double zeroTheta = angleB - averageAngle + 45;

			leftMotor.rotate(
					convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, zeroTheta),
					true);
			rightMotor.rotate(
					-convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, zeroTheta),
					false);
			
			odo.setPosition(new double[] { OFFSET, OFFSET, 0 }, new boolean[] { true,
					true, true });
			
			Navigation.CM_ERR = 1;
			nav.travelTo(0, 0);
			Navigation.CM_ERR = 2;
			
			nav.turnTo(0, true);	
			
			odo.setPosition(new double[] { 0, 0, 0 }, new boolean[] { true,
					true, true });			
		} else {
			// rotate until a wall
			status = "efirst";
			while (getFilteredData() > WALL_DIST - WALL_GAP) {
				leftMotor.backward();
				rightMotor.forward();
			}
			// keep rotating until the robot no longer sees the wall, then latch
			// the angle
			while (getFilteredData() < WALL_DIST) {
				leftMotor.backward();
				rightMotor.forward();
			}
			status = "esecond";
			angleA = odo.getAng();

			// switch directions and rotate until the robot sees the wall.
			while (getFilteredData() > WALL_DIST - WALL_GAP) {
				leftMotor.forward();
				rightMotor.backward();
			}
			status = "ethird";
			// rotate until the robot no longer sees the wall and latch the
			// angle.
			while (getFilteredData() < WALL_DIST) {
				leftMotor.forward();
				rightMotor.backward();
			}
			status = "efourth";
			leftMotor.stop(true);
			rightMotor.stop(true);
			angleB = odo.getAng();

			// if angle A is bigger than B, subtract 360.
			if (angleA > angleB) {
				angleA = angleA - 360;
			}
			double averageAngle = (angleA + angleB) / 2;
			double zeroTheta = angleB - averageAngle + 45;

			leftMotor.rotate(
					convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, zeroTheta),
					true);
			rightMotor.rotate(
					-convertAngle(Lab5.WHEEL_RADIUS, Lab5.TRACK, zeroTheta),
					false);

			odo.setPosition(new double[] { OFFSET, OFFSET, 0 }, new boolean[] { true,
					true, true });
			
			nav.travelTo(0, 0);
			nav.turnTo(0, true);	
			
			odo.setPosition(new double[] { 0, 0, 0 }, new boolean[] { true,
					true, true });
		}
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public static float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = (int) (usData[0] * 100.0);
		float result = 0;
		if (distance > 50 && filterControl < FILTER_OUT) {
			filterControl++;
			result = lastDistance;
		} else if (distance > 50) {
			result = 50;
		} else {
			filterControl = 0;
			result = distance;
		}
		lastDistance = distance;
		return result;
	}

}
