import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

public class Line {
	private int id;
	private Color color;

	private LinkedList<Station> stations;
	private ArrayList<Train> trains;
	private Set<Game.Type> types;
	
	private End head;
	private End tail;
	
	private static Color[] lineColors = new Color[] {Color.RED, Color.BLUE, Color.YELLOW};

	
	public Line(Station s1, Station s2) {
		id = Game.lines.size();
		color = lineColors[id];
		stations = new LinkedList<Station>();
		stations.add(s1);
		stations.add(s2);
		s1.addLine(this);
		s2.addLine(this);
		
		// Add head and tail
		head = new End(this, s1);
		tail = new End(this, s2);
		
		// Add trains
		trains = new ArrayList<Train>();
		trains.add(new Train(this));
		
		// Add types
		types = new HashSet<Game.Type>();
		types.add(s1.getType());
		types.add(s2.getType());
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
	
	public void append(Station s1, Station s2) {
		if (stations.contains(s2)) {
			System.out.println("This line already contains " + s2);
			return;
		}
		if (s1 == stations.getFirst()) {
			stations.addFirst(s2);
			head.setStation(s2);
		} else if (s1 == stations.getLast()) {
			stations.addLast(s2);
			tail.setStation(s2);
		}
		types.add(s2.getType());
	}
	
	private Rectangle2D getEnd(Station s) {
		return new Rectangle2D.Double(s.getCenter().x, s.getCenter().y-10, 15, 15);
	}
	
	public void paint(Graphics2D g) {
		if (stations.size() < 2) {
			return;
		}
		
		g.setStroke(new BasicStroke(5));
		g.setColor(color);
	
		
		ListIterator<Station> iter = stations.listIterator();
		Station s1 = iter.next();
		while (iter.hasNext()) {
			Station s2 = iter.next();
			Line2D lin = new Line2D.Float(s1.getCenter().x, s1.getCenter().y, s2.getCenter().x, s2.getCenter().y);
			g.draw(lin);
			s1 = s2;
		}
		
		// Paint ends
		head.paint(g);
		tail.paint(g);
		
		// Paint trains
		for (Train train : trains) {
			train.paint(g);
		}
		
		
	}
	
	public void printLine() {
		ListIterator<Station> iter = stations.listIterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	public String toString() {
		return "LINE " + this.id;
	}
	
}
