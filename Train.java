import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class Train {
	private int id;
	private Line line;
	private ArrayList<Passenger> passengers;
	private LinkedList<Passenger> passengersList;
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
	
	
	private static int LOADING_WAITS 	= 10;
	private static int ACROSS 			= 2;				// Max passengers across
	private static int DOWN 			= 3;				// Max passengers down
	private static int MAX_SIZE 		= ACROSS * DOWN; 	// Max passengers
	private static int P_SIZE 			= 8;				// Height and width of passengers on train
	
	private int DISTANCE = 5;
	
	
	public Train(Line line) {
		this.id = line.getTrains().size();
		this.line = line;
		this.passengers = new ArrayList<Passenger>();
		this.passengersList = new LinkedList<Passenger>();
		
		this.currentStation = line.getStations().getFirst();
		this.x = currentStation.getCenter().x;
		this.y = currentStation.getCenter().y;
		setNextStation(line.getStations().get(1));
		status = Status.MOVING;
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
			double xcoor = x + ((pCount % (ACROSS - 1)) * P_SIZE);
			double ycoor = y + ((pCount % (DOWN - 1)) * P_SIZE);
			
			switch(p.getType()) {
				case SQUARE:
					g.fill(new Rectangle2D.Double(xcoor, ycoor, P_SIZE, P_SIZE));
				case CIRCLE:
					g.fill(new Ellipse2D.Double(xcoor, ycoor, P_SIZE, P_SIZE));
				case TRIANGLE:
//					g.fill(new Polygon(new double[]{xcoor, xcoor+(P_SIZE/2), xcoor+P_SIZE}, 
//            				new double[]{ycoor+P_SIZE, ycoor, ycoor+P_SIZE}, 3));
			}
			ycoor += 5;
			pCount++;
		}
	}
	
	private Station getNextStation() {
		LinkedList<Station> stations = line.getStations();
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
		
		setValidPassengers();
		status = Status.LOADING;
	}
	
	/* Fills passengersList with the passengers to unload */
	private void setPassengersToUnload() {
		for (Passenger p : passengers) {
			if (p.getType() == currentStation.getType()) {
				passengersList.add(p);
			}
		}
	}
	
	/* Fills passengersList with the passengers to load */
	private void setValidPassengers() {
		for (Passenger p : currentStation.getPassengers()) {
			if (isValidPassenger(p)) {
				passengersList.add(p);
			}
		}
	}
	
	private void handleLoading() {
		// Done loading
		if (isFull() || currentStation.getPassengers().isEmpty() || passengersList.isEmpty()) {
			status = Status.UNLOADING;
			setPassengersToUnload();
			return;
		}
		
		// Add passenger
		Passenger nextPassenger = passengersList.pop();
		currentStation.getPassengers().remove(nextPassenger);
		passengers.add(nextPassenger);
		
		waits += LOADING_WAITS;
	}
	
	private void handleUnloading() {
		// Done unloading
		if (passengersList.isEmpty()) {
			status = Status.MOVING;
			setNextStation(getNextStation());
			return;
		}
		
		// Unload passenger
		Passenger nextPassenger = passengersList.pop();
		passengers.remove(nextPassenger);
		Game.increaseScore();
		waits += LOADING_WAITS;
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
		}
	
	}

	
	public boolean isFull() {
		return passengers.size() == MAX_SIZE;
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

}
