package wallflower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		bangFront();
	}
	
	///////////////////////
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;

		// TODO test without beginning or finishing bangFront()
		// TODO try to use bandwith and bandcenter instead of hardcoded #
		
		// begin process with default speed
		bangFront();
		
		// apply correction 
		if( this.distance < 22) {			// about to hit the wall
	        bangAway(3);					
	    } else if ( this.distance < 24) {		
	        bangAway(2);
	    } else if ( this.distance < 28){ 
	        bangAway(1);
	    }else {	    	
	    	if( this.distance > 35) {		// no more wall wall
		        bangCloser(3);
		    } else if ( this.distance > 32) { 
	    		bangCloser(2);
	    	} else if( this.distance > 30){
	    		bangCloser(1);
	    	}
	    }
		
		// we exit processing with default speed
		bangFront();
	}

	// we split our bangs into 3 different magnitudes
	private void bangCloser(int force) {
		if (force == 3) {				 		
			rightMotor.setSpeed(motorHigh*2);
			rightMotor.forward();
			leftMotor.setSpeed(motorLow*0.5f);
			leftMotor.forward();
			try{
				Thread.sleep(20);	
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		} else if (force == 2) {				 
			rightMotor.setSpeed(motorHigh*1.5f);
			rightMotor.forward();
			leftMotor.setSpeed(motorLow);
			leftMotor.forward();
		} else {
			rightMotor.setSpeed(motorHigh);
			rightMotor.forward();
			leftMotor.setSpeed(motorLow);
			leftMotor.forward();		 
		}	
		
		try {
			Thread.sleep(40);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	// we split our bangs into 3 different magnitudes
	private void bangAway(int force) {
		
		if (force == 3) {	
			leftMotor.setSpeed(motorHigh*2);
			leftMotor.forward();
			rightMotor.setSpeed(motorLow*0.5f);
			rightMotor.forward();
			try{
				Thread.sleep(1000);	
			} catch(InterruptedException e){
				e.printStackTrace();
			}
			
		} else if (force == 2) {				 
			leftMotor.setSpeed(motorHigh*1.5f);
			leftMotor.forward();
			rightMotor.setSpeed(motorLow);
			rightMotor.forward();
		} else {
			leftMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.setSpeed(motorLow);
			rightMotor.forward();		 
		}
		
		try {
			Thread.sleep(40);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	// set default speed and move forward
	private void bangFront(){
		leftMotor.setSpeed(motorHigh);				
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	///////////////////////////

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
