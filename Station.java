import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class Station {
	private int id;
	private Type type;
	private Shape shape;
	private Color color;
	private ArrayList<Passenger> passengers;
//	private ArrayList<Station> stations;
	private ArrayList<Line> lines;
	
	private boolean hover = false;
	private boolean clicked = false;

	
	public enum Type {
		SQUARE,
		CIRCLE,
		TRIANGLE
	}
	
	public Station(Type type, int limit) {
		this.id = Game.stations.size() - 1;
		
		Random rand = new Random(); 
		
		int x = Board.getCenter().x + rand.nextInt(limit) * (rand.nextBoolean() ? -1 : 1); 
		int y = Board.getCenter().y + rand.nextInt(limit) * (rand.nextBoolean() ? -1 : 1); 
		
		this.type = type;
		switch(type) {
			case SQUARE:
				shape = new Rectangle2D.Double(x, y, 15, 15);
				break;
			case CIRCLE:
				shape = new Ellipse2D.Double(x, y, 15, 15);
				break;
			case TRIANGLE:
				shape = new Polygon(new int[]{x, x+8, x+16}, new int[]{y+15, y, y+15}, 3);
				break;
		}
		
		// Init
		passengers = new ArrayList<Passenger>();
		lines = new ArrayList<Line>();
		passengers.add(new Passenger(Type.SQUARE));
	}
	
	public boolean getHover() {
		return hover;
	}
	
	public void setHover(boolean hover) {
		this.hover = hover;
	}
	
	public boolean getClicked() {
		return clicked;
	}
	
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public ArrayList<Line> getLines() {
		return lines;
	}
	
	/* Paints station and its passengers */
	public void paint(Graphics2D g) {
		// Draw shape
		if (clicked) {
			g.setColor(Color.red);
		} else if (hover) {
			g.setColor(Color.blue);
		} else {
			g.setColor(Color.black);
		}
		
        g.draw(getShape());
        
        // Draw passengers
        g.setColor(Color.BLACK);
        int xcoor = getShape().getBounds().x + 20;
        int ycoor = getShape().getBounds().y;
        for (Passenger p: passengers) {
        	if (p.getType() == Type.SQUARE) {
        		g.fill(new Rectangle2D.Double(xcoor, ycoor, 10, 10));
        		xcoor += 20;
        	}
        }
	}
	
//	public void connectToStation(Station s) {
//		if (s == this) {
//			System.out.println("Can't connect to self");
//			return;
//		}
//		if (stations.contains(s)) {
//			System.out.println("Already connected to station " + s);
//			return;
//		}
//		stations.add(s);
//		System.out.println(this + " and " + s + " are connected.");
//	}
	
	public Point getCenter() {
		return new Point((int) shape.getBounds2D().getCenterX(), (int) shape.getBounds2D().getCenterY());
	}
	
	public void addLine(Line line) {
		if (lines.contains(line)) {
			System.out.println(this + " already contains " + line);
		}
		lines.add(line);
	}
	
	public String toString() {
		return this.type.toString() + "-" + this.id;
	}
	
	
}
