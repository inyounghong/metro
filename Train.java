import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Train extends Thread {
	private int id;
	private Line line;
	private ArrayList<Passenger> passengers;
	private Station currentStation;
	private Station nextStation;
	private double nextx;
	private double nexty;
	private boolean isForward = true;
	private Status status;
	private double x;
	private double y;
	private int moves;
	private int waits = 0;
	
	
	private static int LOADING_WAITS 	= 300;
	private static int ACROSS 			= 2;				// Max passengers across
	private static int DOWN 			= 3;				// Max passengers down
	private static int MAX_SIZE 		= ACROSS * DOWN; 	// Max passengers
	private static int P_SIZE 			= 8;				// Height and width of passengers on train
	
	private int DISTANCE = 5;
	
	
	public Train(Line line) {
		this.id = line.getTrains().size();
		this.line = line;
		this.passengers = new ArrayList<Passenger>();
		
		this.currentStation = line.getStations().getFirst();
		this.x = currentStation.getCenter().x;
		this.y = currentStation.getCenter().y;
		setNextStation(line.getStations().get(1));
		status = Status.MOVING;
		
		this.start();
	}
	
	public enum Status {
		MOVING,
		STOPPED,
		LOADING,
		UNLOADING
	}
	

	/* Painting trains */
	public void paint(Graphics2D g) {
		// Paint train
		g.setColor(line.getColor());
		g.fill(new Rectangle2D.Double(x, y, ACROSS * P_SIZE, DOWN * P_SIZE));
		
		// Paint passengers
		paintPassengers(g);
	}
	
	
	/* Paints passengers in the train */
	private void paintPassengers(Graphics2D g) {
		g.setColor(Color.WHITE);

		int pCount = 0;
		for (Passenger p : passengers) {
			int xcoor = (int) (x + ((pCount % (ACROSS - 1)) * P_SIZE));
			int ycoor = (int) (y + ((pCount % (DOWN - 1)) * P_SIZE));
			
			switch(p.getType()) {
				case SQUARE:
					g.fill(new Rectangle2D.Double(xcoor, ycoor, P_SIZE, P_SIZE));
				case CIRCLE:
					g.fill(new Ellipse2D.Double(xcoor, ycoor, P_SIZE, P_SIZE));
				case TRIANGLE:
					g.fill(new Polygon(new int[]{xcoor, xcoor+(P_SIZE/2), xcoor+P_SIZE}, 
            				new int[]{ycoor+P_SIZE, ycoor, ycoor+P_SIZE}, 3));
			}
			ycoor += 5;
			pCount++;
		}
	}
	
	private Station getNextStation() {
		LinkedList<Station> stations = line.getStations();
//		printStations(stations);
		if (stations.size() < 2) {
			System.out.println(line + " has fewer than 2 stations");
			return null;
		}
		// Reverse from the front
		if (line.isHead(currentStation)) {
			isForward = true;
			return stations.get(1);
		}
		
		// Reverse from the back
		if (line.isLast(currentStation)) {
			isForward = false;
			return stations.get(stations.size()-2);
		}
		
		// Keep going in the middle
		int index = stations.indexOf(currentStation);
		if (isForward) {
			return stations.get(index + 1);
		}
		return stations.get(index - 1);
	}
	
	/* Sets nextStation, nextx, nexty and number of moves */
	private void setNextStation(Station nextStation) {
		this.nextStation = nextStation;
		double xDist = nextStation.getCenter().x - currentStation.getCenter().x;
		double yDist = nextStation.getCenter().y - currentStation.getCenter().y;
		
		double ratio = xDist/yDist;
		
		this.nexty = Math.sqrt(DISTANCE/(1+Math.pow(ratio, 2)));
		if (yDist < 0) {
			this.nexty *= -1;
		}
		this.nextx = nexty * ratio;
		
		this.moves = (int) (xDist / nextx);
	}
	
	/* Change from moving to Loading */
	private void handleMoving() {
		if (moves >= 0){
			x += nextx;
			y += nexty;
			moves--;
		} else { // Match up with nextStation and calculate new nextStation
			setUpLoading();
		}
	}
	
	private void setUpLoading() {
		// Move to next station
		x = nextStation.getCenter().getX();
		y = nextStation.getCenter().getY();
		currentStation = nextStation;
		
//		setValidPassengers();
		status = Status.LOADING;
	}
	
	
	/*
	 * Picks up passenger from station and adds to train
	 */
	private void pickUpPassenger(Passenger p) {
		System.out.println("Picking up passenger " + p + " " + passengers.size());
		passengers.add(p);
		currentStation.removePassenger(p);
	}
	
	
	/*
	 * Handles LOADING state of train
	 */
	private void handleLoading() {
		int i = 0;
		while (!(isFull() || currentStation.getPassengers().size() == i)) {
			Passenger p = currentStation.getPassengers().get(i);
			if (isValidPassenger(p)) {
				pickUpPassenger(p);
				try {
					sleep(LOADING_WAITS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				i++;
			}
		}
		
		status = Status.UNLOADING;
	}
	
	/*
	 * Drops off passenger p at the current station
	 */
	private void dropOffPassenger(Passenger p){
		passengers.remove(p);
		currentStation.addPassenger(p);
		System.out.println("dropped off " + p);
		Game.increaseScore();
	}
	
	
	/*
	 * Handles UNLOADING state of train
	 */
	private void handleUnloading() {
		int i = 0;
		
		while (!(passengers.size() == i)) {
			Passenger p = passengers.get(i);
			if (isValidStationFor(p)) {
				dropOffPassenger(p);
				try {
					sleep(LOADING_WAITS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				i++;
			}
		}

//		// Done unloading
//		if (passengersList.isEmpty()) {
//			status = Status.MOVING;
//			setNextStation(getNextStation());
//			return;
//		}
//		
//		// Unload passenger
//		Passenger nextPassenger = passengersList.pop();
//		passengers.remove(nextPassenger);
		
		setNextStation(getNextStation());
		status = Status.MOVING;
		
	}
	
	public void run() {
		// Kill time
		if (waits > 0) {
			waits--;
			return;
		}
		
		switch (status) {
			case MOVING:
				handleMoving();
				break;
			case LOADING:
				handleLoading();
				break;
			case UNLOADING:
				handleUnloading();
				break;
		default:
			System.out.println("defaulted");
			break;
		}
	
	}

	
	public boolean isFull() {
		return passengers.size() == MAX_SIZE;
	}
	
	/*
	 * @return if passenger p can be dropped off at the current station
	 */
	private boolean isValidStationFor(Passenger p) {
		return (p.getType() == currentStation.getType());
	}
	
	/* Returns whether Passenger p is a valid passenger for this train */
	private boolean isValidPassenger(Passenger p) {
		boolean isValid = false;
		for (Game.Type type : line.getTypes()) {
			if (p.getType() == type) {
				isValid = true;
				break;
			}
		}
		return isValid;
	}

	public void printStations(LinkedList<Station> stations) {
		for (Station s : stations) {
			System.out.println(s);
		}
	}
}
