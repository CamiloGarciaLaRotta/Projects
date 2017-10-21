package ev3Navigation;

public class ObstacleAvoidance extends Thread{
	Navigator navigator;
	UltrasonicPoller usPoller ;
	boolean safe;
	Object lock;
	
	public ObstacleAvoidance(Navigator navigator, UltrasonicPoller usPoller) {
		this.navigator = navigator;
		this.usPoller = usPoller;
		lock = new Object();
		
		setSafe(false);
	}
	
	public void run() {
		if(!getSafe()){
			do{
				navigator.turnToDest(-90, false);
				navigator.goForward(30, false);
				navigator.turnToDest(90, false);
			} while (usPoller.getDistance() < 14);
			
			navigator.goForward(25, false);
	
			setSafe(true);
		}
	}
	
	public boolean getSafe(){
		boolean state;
		
		synchronized(lock) {
			state = this.safe;
		}
		
		return state;
	}

	public void setSafe(boolean state) {
		synchronized(lock) {
			this.safe = state;
		}
	}
}
