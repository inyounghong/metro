import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

public class Game extends JPanel{
	
	
	
	public Graphics g;
	public static Board board;
	public static ArrayList<Station> stations;
	public static ArrayList<Passenger> passengers;
	public static ArrayList<Line> lines;
	
	public static int limit;
	public static int availableLines;
	
	public Game() {
		
		passengers = new ArrayList<Passenger>();
		
		limit = 200;
		availableLines = 1;
		
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
	
	public static void main(String[] a) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game();
            }
        });
		
		// Add new passengers
		addNewPassengers();
	}
	
	public void initStations() {
		stations = new ArrayList<Station>();
		stations.add(new Station(Station.Type.SQUARE, limit));
		stations.add(new Station(Station.Type.TRIANGLE, limit));
		stations.add(new Station(Station.Type.CIRCLE, limit));
		stations.add(new Station(Station.Type.TRIANGLE, limit));
		stations.add(new Station(Station.Type.CIRCLE, limit));
	}
	
	public void initLines() {
		lines = new ArrayList<Line>();
	}
	
	public static void addNewPassengers() {
		Runnable helloRunnable = new Runnable() {
		    public void run() {
//		        board.addPassengers();
		    }
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);
	}
	
	
	
}
