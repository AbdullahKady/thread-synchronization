public class Player extends Thread {

  Operator operator;
	int id;
	int waitTime;
	boolean rideCompleted;

  public Player(String id, String waitTime, Operator operator) {
		this.id = Integer.parseInt(id);
		this.waitTime = Integer.parseInt(waitTime);
		this.rideCompleted = false;
		this.operator = operator;
	}

 	/*
	 * A Player does nothing but sleeping for his specific sleep time, then wakes
	 * up to notify the operator that he wants to join the queue.
	 */
 	public void run() {
		try {
			sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Operator.appendToOutput("PLAYER: #" + this.id + " Woke Up", true);
		operator.addMe(this);
	}
}