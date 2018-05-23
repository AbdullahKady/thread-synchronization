import java.util.LinkedList;

public class Wheel extends Thread {
	int finishedPlayersCount;
	int maxWaitTime;
	LinkedList<Player> onBoardPlayers;
	boolean running;

	public Wheel() {
		this.onBoardPlayers = new LinkedList<Player>();
		this.running = false;
	}

	/*
	 * The wheel keeps sleeping until woken up for the maximum time is up, or
	 * either it was woken up prematurely by the operator as the maximum
	 * capacity of it's players has been reached
	 */

	public void run() {
		while (true) {
      System.out.println("WHEEL<sleep>: Sleeping...");
			try {
				sleep(maxWaitTime);
        System.out.println("WHEEL<starting>: Wait time is over");
				startRide();
			} catch (InterruptedException e) {
				if (this.onBoardPlayers.size() == 5) {
          System.out.println("WHEEL<starting>: Maximum capacity reached");
					startRide();
				}
			}
		}
	}

	/*
	 * Once the wheel is woken up, it starts out the ride, and prints out the
	 * IDs of the players in the current ride, and sets their corresponding
	 * flags to true (no idea what for :D)
	 */

	public void startRide() {
		this.running = true;

    System.out.println("\n=======================\nTHREADS IN RIDE ARE : \n");
		for (Player player : this.onBoardPlayers) {
      System.out.println(player.id + " ");
			player.rideCompleted = true;
			finishedPlayersCount++;
		}
		System.out.println("\n=======================\n");

    this.onBoardPlayers = new LinkedList<Player>();
		this.running = false;
	}

	/*
	 * Add players to the wheel (Boarding it) in a synchronous manner
	 */

	synchronized public void addPlayer(Player p) {
		this.onBoardPlayers.add(p);
		System.out.println("WHEEL<boarded>: Player #" + p.id + " just boarded the wheel");
	}

}