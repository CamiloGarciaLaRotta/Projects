
// fully working p controller.

package wallflower;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;

	public PController(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, int bandCenter, int bandwidth) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight); // Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}

	@Override
	public void processUSData(int distance) {
		if (distance >= 300)// filter
			distance = 100;
		if (distance >= 255 && filterControl < FILTER_OUT) {
			filterControl++;
		} else if (distance >= 255) {
			this.distance = distance;
		} else {
			filterControl = 0;
			this.distance = distance;
		}
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (P style)
		int k = -4 * (35 - distance);// calculate steering constant 
		steering(k);
	}

	private void steering(int scale) {
		// scale -120--+120
		// -120 turn right sharp
		// 120 turn left sharp
		// 0 straight
		if (scale > 120)
			scale = 120;
		else if (scale < -120)
			scale = -120;
		float leftPower, rightPower = 0;
		//leftPower = (float) (-2 * scale + 400); // came from test trails//left power greater than right power
		leftPower = (float) (-2 * scale + 400); // came from test trails//left power greater than right power
		//rightPower = (float) (1.8 * scale + 216); // -110
		rightPower = (float) (1.8 * scale + 210); // -110
		if (Math.abs(scale) <= 5) {// smoothing movement
			leftPower = rightPower;
		}
		leftMotor.setSpeed(leftPower);
		rightMotor.setSpeed(rightPower);
		leftMotor.forward();
		rightMotor.forward();
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}