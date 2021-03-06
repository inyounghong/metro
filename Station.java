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
	private Game.Type type;
	private Shape shape;
	private Color color;
	
	private final int STATION_SIZE = 15;
	private final int BOX_SIZE = 20;
	
	private ArrayList<Passenger> passengers;
	private ArrayList<Line> lines;
	
	private Status status;
	
	public enum Status {
		NORMAL,
		HOVER,
		CLICKED
	}

	public Station(Game.Type type, int limit) {
		this.id = Game.stations.size() - 1;
		this.status = Status.NORMAL;
		
		Random rand = new Random(); 
		
		int x = Board.getCenter().x + rand.nextInt(limit) * (rand.nextBoolean() ? -1 : 1); 
		int y = Board.getCenter().y + rand.nextInt(limit) * (rand.nextBoolean() ? -1 : 1); 
		
		this.type = type;
		switch(type) {
			case SQUARE:
				shape = new Rectangle2D.Double(x, y, STATION_SIZE, STATION_SIZE);
				break;
			case CIRCLE:
				shape = new Ellipse2D.Double(x, y, STATION_SIZE, STATION_SIZE);
				break;
			case TRIANGLE:
				shape = new Polygon(new int[]{x, x+8, x+16}, new int[]{y+STATION_SIZE, y, y+STATION_SIZE}, 3);
				break;
		}
		
		// Init
		passengers = new ArrayList<Passenger>();
		lines = new ArrayList<Line>();
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
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
	
	public Point2D getPoint() {
		return new Point2D.Double(getCenter().getX(), getCenter().getY());
	}
	
	public ArrayList<Line> getLines() {
		return lines;
	}
	
	public ArrayList<Passenger> getPassengers() {
		return passengers;
	}
	
	public Game.Type getType() {
		return type;
	}
	
	/*
	 * Gets a station's large bounding box
	 * @return Rectangle2D bounding box of extra BOX_SIZE
	 */
	public Rectangle2D getBox() {
		double x = getCenter().getX() - BOX_SIZE/2;
		double y = getCenter().getY() - BOX_SIZE/2;
		return new Rectangle2D.Double(x, y, BOX_SIZE, BOX_SIZE);
	}
	
	/* Paints station and its passengers */
	public void paint(Graphics2D g) {
		// Draw shape
		switch(status) {
			case CLICKED:
				g.setColor(Color.red);
				break;
			case HOVER:
				g.setColor(Color.blue);
				break;
			default:
				g.setColor(Color.black);
				break;
		}

        g.draw(getShape());
        
        // Draw passengers
        paintPassengers(g);
        
	}
	
	/* Paints passengers around the station */
	private void paintPassengers(Graphics2D g) {
		g.setColor(Color.BLACK);
        int xcoor = getShape().getBounds().x + 20;
        int ycoor = getShape().getBounds().y;
        
        for (Passenger p: passengers) {
        	switch(p.getType()) {
        		case SQUARE:
        			g.fill(new Rectangle2D.Double(xcoor, ycoor, 10, 10));
        		case CIRCLE:
        			g.fill(new Ellipse2D.Double(xcoor, ycoor, 10, 10));
        		case TRIANGLE:
        			g.fill(new Polygon(new int[]{xcoor, xcoor+5, xcoor+10}, 
            				new int[]{ycoor+10, ycoor, ycoor+10}, 3));
        	}
        	xcoor += 12;
        }
	}
	
	public Point getCenter() {
		return new Point((int) shape.getBounds2D().getCenterX(), (int) shape.getBounds2D().getCenterY());
	}
	
	public void addLine(Line line) {
		if (lines.contains(line)) {
			System.out.println(this + " already contains " + line);
		}
		lines.add(line);
	}
	
	public void addPassenger(Passenger p) {
		passengers.add(p);
	}
	
	public void removePassenger(Passenger p) {
		passengers.remove(p);
	}
	
	public String toString() {
		return this.type.toString() + "-" + this.id;
	}
	
	
}
