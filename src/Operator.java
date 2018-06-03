import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Operator {

  // Since the output will always be reasonably small, an accumulating string
  // would be better than a shared stream
	static String output = "";
	Wheel wheel;
	Queue<Player> queuedPlayers = new LinkedList<Player>();
	private int totalPlayerCount;
	private String inputFilePath;

	public Operator(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

  /*
   * To avoid interleaving printing to the output between the various threads.
   */
  synchronized static void appendToOutput(String s, boolean lineBreak) {
  	if (lineBreak)
  		Operator.output += s + "\n";
  	else
  		Operator.output += s;
  }

	public void startOperator() {
		initializeWheel();
		/*
		 * Keep checking on the wheel, if it's not running, we start boarding
		 * players from the queue, if the queue is empty we check if all players
		 * are done with their rides to terminate the program and write out the
		 * output file.
		*/
		while (true) {
			synchronized (this) {
				if (!wheel.running) {
					Player currentPlayer = this.queuedPlayers.poll();
					if (currentPlayer != null) {
						wheel.addPlayer(currentPlayer);
					} else if (wheel.finishedPlayersCount == totalPlayerCount) {
            Operator.appendToOutput("\nOPERATOR: All players are done with their rides", true);

            File file = new File("output.txt");
            try {
							FileWriter fileWriter = new FileWriter(file);
							fileWriter.write(Operator.output);
							fileWriter.flush();
							fileWriter.close();
						} catch (Exception e) {
							System.out.println("SOMETHING WENT WRONG WHILE WRITING THE OUTPUT!\n");
							e.printStackTrace();
						}

						System.out.println("Output has been written successfully.");
						System.out.println("Terminating");
						System.exit(0);
					}
					if (wheel.onBoardPlayers.size() == 5) {
						wheel.interrupt();
					}
				}
			}
		}
	}

	/*
	 * Just initializes the wheel, populates the players from the input, then
	 * starts the wheel thread
	 */

	public void initializeWheel() {
		wheel = new Wheel();
		initializePlayers(this.inputFilePath);
		wheel.start();
	}

	/*
	 * Initializes all players by reading the input file passed through the
	 * constructor, then it starts all the players (which means putting them to
	 * sleep for their corresponding timings)
	 */

	public void initializePlayers(String filePath) {
		try {
			File file = new File(filePath);
			Scanner sc = new Scanner(file);
			this.wheel.maxWaitTime = Integer.parseInt(sc.nextLine());
			this.totalPlayerCount = Integer.parseInt(sc.nextLine());
			LinkedList<Player> temp = new LinkedList<Player>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.length() != 0) {
					String[] values = line.split(",");
					Player player = new Player(values[0], values[1], this);
					// player.start();
					temp.add(player);
				}
			}

			// The temporary array to ensure a faster consecutive starting of
			// the players since starting them while reading is more expensive
			for (Player player : temp) {
				player.start();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	/*
	 * Adds a player who just woke up to the queue of the operator
	 */

	synchronized void addMe(Player player) {
		this.queuedPlayers.add(player);
	}

  /*
	 * Main method for testing, passing the input file path as a relevant path
	 * in case it's in the same directory as the project files
	 */

	public static void main(String[] args) {
    String inputFilePath = "inputs/input-1.txt";
		Operator op = new Operator(inputFilePath);
		op.startOperator();
	}
}