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
			Operator.appendToOutput("WHEEL<sleep>: Sleeping...", true);
			try {
				sleep(maxWaitTime);
				Operator.appendToOutput("WHEEL<starting>: Wait time is over", true);
				startRide();
			} catch (InterruptedException e) {
				if (this.onBoardPlayers.size() == 5) {
					Operator.appendToOutput("WHEEL<starting>: Maximum capacity reached", true);
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
		synchronized (Operator.output) {
			Operator.appendToOutput("\n=======================\nTHREADS IN RIDE ARE : \n", true);

			for (Player player : this.onBoardPlayers) {
				Operator.appendToOutput(player.id + " ", false);
				player.rideCompleted = true;
				finishedPlayersCount++;
			}

			Operator.appendToOutput("\n=======================\n", true);
		}
		this.onBoardPlayers = new LinkedList<Player>();
		this.running = false;
	}

	/*
	 * Add players to the wheel (Boarding it) in a synchronous manner
	 */

	synchronized public void addPlayer(Player p) {
		this.onBoardPlayers.add(p);
		Operator.appendToOutput("WHEEL<boarded>: Player #" + p.id + " just boarded the wheel", true);
	}

}