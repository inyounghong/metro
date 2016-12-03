import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class Line {
	private int id;
	private Color color;
	private LinkedList<Station> stations;
	private ArrayList<Train> trains;
	private boolean active;
	
	private static Color[] lineColors = new Color[] {Color.RED, Color.BLUE, Color.YELLOW};
	
	public Line(Station s1, Station s2) {
		id = Game.lines.size();
		color = lineColors[id];
		stations = new LinkedList<Station>();
		stations.add(s1);
		stations.add(s2);
		s1.addLine(this);
		s2.addLine(this);
		
		this.trains = new ArrayList<Train>();
		trains.add(new Train(this));
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
	
	public void append(Station s1, Station s2) {
		if (stations.contains(s2)) {
			System.out.println("This line already contains " + s2);
			return;
		}
		System.out.println("s1" + s1 + " first: " + stations.getFirst());
		if (s1 == stations.getFirst()) {
			stations.addFirst(s2);
			System.out.println("Added " + s2 + " to the front of " + this);
		} else if (s1 == stations.getLast()) {
			stations.addLast(s2);
			System.out.println("Added " + s2 + " to the end of " + this);
		}
		printLine();
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
		
		// Paint trains
		for (Train train : trains) {
			train.paint(g);
		}
		
	}
	
	public void printLine() {
		System.out.println("printing");
		ListIterator<Station> iter = stations.listIterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	public String toString() {
		return "LINE " + this.id;
	}
	
}
