package ev3Search;

public class ObstacleAvoidance extends Thread{
	private Navigation nav;
	private Detector det;
	private boolean safe;
	Object lock;
	
	public ObstacleAvoidance(Navigation nav, Detector det) {
		this.nav = nav;
		this.det = det;
		lock = new Object();
		
		setSafe(true);
	}
	
	public void run() {
		// obstacle avoidance starts
		if(!getSafe()){
			// avoid CW by default, CCW if inversed
			int rotationSense = (Scanner.inverse) ? -1 : 1;
		
			do{
				nav.turnTo(-(rotationSense) * 90, false);
				nav.goForward(30);
				nav.turnTo((rotationSense) * 90, false);
			} while (det.getFilteredData() < 14);
		
			nav.goForward(25);
		}
		
		setSafe(true);
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
