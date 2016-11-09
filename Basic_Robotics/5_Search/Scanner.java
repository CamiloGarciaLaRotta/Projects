package ev3Search;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Scanner extends Thread {

	// bandwith for atCheckpoint() measurement
 	private final int BW = 5;
	private final int THRESHOLD = 50;
	
	private Navigation nav;
	private Odometer odo;
	private Detector det;
	private ObstacleAvoidance avoider;
	private final int[][] CHECKPOINTS = new int[][] { {60,0}, {60,60}, {0,60}, {75,75}};

	private int currCheckpoint;

	public static enum ScanState {
		SCAN, TRAVEL, OBJECT, FOUND, OBSTACLE, DONE
	};

	private static final EV3MediumRegulatedMotor armMotor = new EV3MediumRegulatedMotor(
			LocalEV3.get().getPort("B"));
	
	public static ScanState scanState;

	public static boolean inverse;

	public Scanner(Navigation nav, Odometer odo, Detector det,
			ObstacleAvoidance avoider) {
		this.nav = nav;
		this.odo = odo;
		this.det = det;
		this.avoider = avoider;
		this.currCheckpoint = 0;
		Scanner.inverse = false;
		Scanner.scanState = ScanState.SCAN;
	}

	public void run() {
		while (true) {
			switch (Scanner.scanState) {
			case SCAN: // at checkpoint, ready to rotate towards next checkpoint
				double startAngle = 0, finalAngle = 0;
				switch(currCheckpoint){
				case 0:
					startAngle = 0;
					finalAngle = 90;
					break;
				case 1:
					startAngle = 90;
					finalAngle = 180;
					break;
				case 2:
					startAngle = 180;
					finalAngle = 270;
					break;
				case 3:
					startAngle = 270;
					finalAngle = 359;
				}
				
				// rotate to start angle
				nav.turnTo(startAngle, true);
				
				// scan 
				nav.setSpeeds(-Lab5.MOTOR_LOW, Lab5.MOTOR_LOW);

				// while it hasnt done full sweep, check for objects
				while (odo.getAng() < finalAngle) {
					if(det.getFilteredData() < THRESHOLD) {
						Scanner.scanState = ScanState.OBJECT;
						break;
					}
				} 
				
				nav.setSpeeds(0, 0);
				
				// if robot didn't detect object
				if (Scanner.scanState != ScanState.OBJECT) Scanner.scanState = ScanState.TRAVEL;
				
				break;
			case TRAVEL: 	// travel to checkpoint

				nav.travelTo(CHECKPOINTS[currCheckpoint][0], CHECKPOINTS[currCheckpoint][1]);
				currCheckpoint++;
				
				// already checked all the points?
				Scanner.scanState = (currCheckpoint == 4) ? ScanState.DONE : ScanState.SCAN;
				
				break;
			case OBSTACLE: // robot detected obstacle (not styrofoam)
				
				// TODO JACKSON, do obstacle avoidance however you want,
				// my method was just for testing 
				
				nav.setSpeeds(0, 0);
				
				// to avoid falling off the map while avoiding
				double currX = odo.getX();
				double currY = odo.getY();
				
				if (currX < 0 || currX > 60 || currY > 60) this.inverse = true;

				avoider.setSafe(false);
				Sound.twoBeeps();
				while (!avoider.getSafe()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// finished avoiding
				Scanner.scanState = ScanState.TRAVEL;
				
				break;
			case OBJECT: // travel to obstacle to inspect
				nav.setSpeeds(0, 0);
				
				nav.setSpeeds(Lab5.MOTOR_HIGH, Lab5.MOTOR_HIGH);

				break;
			case FOUND: // styrofoam found
				nav.setSpeeds(0, 0);
				Sound.beep();

				// grab foam
				bringDownArms();
				
				// take block to final destination
				nav.travelTo(75, 75);

				Scanner.scanState = ScanState.DONE;
				break;
			case DONE: // finished pushing styrofoam to destination
				Sound.beepSequenceUp();
				System.exit(0);
				break;
			default:
				break;
			}
		}
	}

//	private boolean atCheckpoint(int currCheckpoint) {
//		int destX = CHECKPOINTS[currCheckpoint][0];
//		int destY = CHECKPOINTS[currCheckpoint][1];
//		int currX = (int) odo.getX();
//		int currY = (int) odo.getY();
//		return (currX > destX - BW && currX < destX + BW && currY > destY - BW && currY < destY
//				+ BW);
//	}
	
	private void bringDownArms() {
		armMotor.setSpeed(100);
		armMotor.setAcceleration(100);
		armMotor.rotate(180);

	}

}
