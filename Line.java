import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

public class Line {
	private int id;
	private Color color;
//	private Path2D path;
	private ArrayList<ConnectingLine> lines;
	private ArrayList<Point> pathPoints;

	private LinkedList<Station> stations 	= new LinkedList<Station>();
	private ArrayList<Train> trains			= new ArrayList<Train>();		
	private Set<Game.Type> types			= new HashSet<Game.Type>();
	
	private End head;
	private End tail;
	
	private static Color[] lineColors = new Color[] {Color.RED, Color.BLUE, Color.YELLOW};

	private Status status;
	
	public enum Status {
		NORMAL,
		HOVER,
		CLICKED,
		EDITING
	}
	
	public Line(Station s1, Station s2) {
		id = Game.lines.size();
		color = lineColors[id];
		status = Line.Status.NORMAL;
		
		initStations(s1, s2);
		
		// Add trains
		trains.add(new Train(this));
		
		// Init path
		setNewPath();
	}
	
	/*
	 * Initializes line with the first two stations, s1 and s2
	 */
	private void initStations(Station s1, Station s2) {
		// Add stations to line
		stations.add(s1);
		stations.add(s2);
		s1.addLine(this);
		s2.addLine(this);
		
		// Add head and tail
		head = new End(this, s1);
		tail = new End(this, s2);
		
		// Add types
		types.add(s1.getType());
		types.add(s2.getType());
	}
	
//	private void calculatePathPoints() {
//		FlatteningPathIterator iter = new FlatteningPathIterator(path.getPathIterator(new AffineTransform()), 1);
//        pathPoints = new ArrayList<Point>();
//        
//        float[] coords = new float[6];
//        while (!iter.isDone()) {
//            iter.currentSegment(coords);
//            int x = (int)coords[0];
//            int y = (int)coords[1];
//            pathPoints.add(new Point(x,y));
//            iter.next();
//        }
//        
//        System.out.println(pathPoints);
//	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public ArrayList<ConnectingLine> getLines() {
		return lines;
	}
	
	public End getHead() {
		return head;
	}
	
	public End getTail() {
		return tail;
	}
	
	public LinkedList<Station> getStations() {
		return stations;
	}
	
	public ArrayList<Train> getTrains() {
		return trains;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Set<Game.Type> getTypes() {
		return types;
	}
	
	public boolean isHead(Station s) {
		return (stations.getFirst() == s);
	}
	
	public boolean isLast(Station s) {
		return (stations.getLast() == s);
	}
	
	/*
	 * Appends s2 to s1, which is either the head or tail of the path
	 */
	public void append(Station s1, Station s2) {
		if (stations.contains(s2)) {
			System.out.println("This line already contains " + s2);
			return;
		}
		// Station added to head
		if (s1 == stations.getFirst()) {
			stations.addFirst(s2);
			head.setStation(s2);
		} else if (s1 == stations.getLast()) {
			// Station added to the tail
			stations.addLast(s2);
			tail.setStation(s2);
		}
		types.add(s2.getType());
		setNewPath();
	}
	
	/*
	 * @return index of the given ConnectingLine in lines ArrayList, or -1 if not found
	 */
	public int getIndex(ConnectingLine cl) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).getStations()[0] == cl.getStations()[0]) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * Inserts cl2 after cl1 in the lines ArrayList
	 */
	public void insertConnectingLine(ConnectingLine cl1, ConnectingLine cl2) {
		int i = getIndex(cl1);
		lines.add(i+1, cl2);
		printLines();
		stations.add(i+1, cl1.getStations()[1]);
	}
	
	
	/*
	 * Sets lines as an array of the current stations
	 */
	private void setNewPath() {
		lines = new ArrayList<ConnectingLine>();
		
		Iterator<Station> iter = stations.iterator();
		Station s1 = iter.next();
		
		while (iter.hasNext()) {
			Station s2 = iter.next();
			lines.add(new ConnectingLine(this, s1, s2));
			s1 = s2;
		}
		System.out.println("new path set");
	}
	

	private Rectangle2D getEnd(Station s) {
		return new Rectangle2D.Double(s.getCenter().x, s.getCenter().y-10, 15, 15);
	}
	
	/*
	 * Paint line
	 */
	public void paint(Graphics2D g) {
		// Don't paint if no stations
		if (stations.size() < 2) {
			return;
		}
		
		// Paint line
		paintLines(g);
		
		// Paint trains
		for (Train train : trains) {
			train.paint(g);
		}
	}
	
	private void paintLines(Graphics2D g) {
		g.setColor(color);
		g.setStroke(new BasicStroke(5));
		
//		switch(status) {
//			case NORMAL:
//				g.setStroke(new BasicStroke(5));
//				break;
//			case HOVER:
//				g.setStroke(new BasicStroke(7));
//				break;
//			case CLICKED:
//				break;
//		}
		for (ConnectingLine line : lines) {
			line.paint(g);
		}
		// Paint ends
		head.paint(g);
		tail.paint(g);
	}
	
	public String toString() {
		return "LINE " + this.id;
	}
	
	public void printLines() {
		for (ConnectingLine cl : lines) {
			System.out.println(cl);
		}
	}
	
}
