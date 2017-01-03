import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game extends JPanel{
	
	public Graphics g;
	public static Board board;
	public static ArrayList<Station> stations;
	public static ArrayList<Passenger> passengers;
	public static ArrayList<Line> lines;
	public static ArrayList<End> ends;
	private static HashMap<Type, Integer> typeMap;
	
	public static int limit;
	public static int availableLines;
	public static int score;
	
    private static Random rand = new Random();
	
	public Game() {
		
		passengers = new ArrayList<Passenger>();
		typeMap = new HashMap<Type, Integer>();
		ends = new ArrayList<End>();
		
		limit = 200;
		availableLines = 2;
		
		initStations();
		initLines();
	
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.board = new Board();
        f.add(this.board);

        f.setLocationByPlatform(true);
        f.pack();
        f.setVisible(true);
	}
	
	public enum Type {
		SQUARE,
		CIRCLE,
		TRIANGLE
	}
	
	public static void main(String[] a) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game();
                runGame();
            }
        });
	}
	
	/*
	 * Create stations
	 */
	public void initStations() {
		stations = new ArrayList<Station>();
		addStation(Type.SQUARE);
		addStation(Type.TRIANGLE);
		addStation(Type.CIRCLE);
		addStation(Type.SQUARE);
		addStation(Type.TRIANGLE);
	}
	
	/* 
	 * Adds a new station of type to the game 
	 */
	private void addStation(Type type) {
		stations.add(new Station(type, limit));
		
		// Insert into typemap
		if (typeMap.containsKey(type)) {
			typeMap.put(type, typeMap.get(type) + 1);
		} else {
			typeMap.put(type, 1);
		}
	}
	
	public void initLines() {
		lines = new ArrayList<Line>();
	}
	
	public static void runGame() {
		Runnable addPassengers = new Runnable() {
		    public void run() {
		    	addPassengers();
		    }
		};
		Runnable moveTrains = new Runnable() {
		    public void run() {
		    	moveTrains();
		    }
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		executor.scheduleAtFixedRate(addPassengers, 0, 3, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(moveTrains, 0, 50, TimeUnit.MILLISECONDS);
	}
	
	
	/*
	 * Randomly adds passengers to the game
	 */
	private static void addPassengers() {
		Station s = getRandomStation();
		Passenger p = getRandomPassenger(s.getType());
		s.addPassenger(p);
	}
	
	private static void moveTrains() {
		for (Line line: lines) {
			for (Train train: line.getTrains()) {
				train.run();
			}
		}
		board.repaint();
	}
	
	public static void increaseScore() {
		score++;
		System.out.println("Score: " + score);
	}
	
	/* Returns a random station */
	private static Station getRandomStation() {
		int stationIndex = rand.nextInt(stations.size());
		return stations.get(stationIndex);
	}
	
	/* 
	 * @returns a passenger of random type, that is NOT the given type 
	 */
	private static Passenger getRandomPassenger(Type type) {
		Type[] types = new Type[typeMap.size()];
		typeMap.keySet().toArray(types);
		int typeIndex = rand.nextInt(types.length);
		Type t = types[typeIndex];
		
		if (t == type) {
			t = types[(typeIndex + 1) % (types.length - 1)];
		}
		
		return new Passenger(t);
	}
	
}
