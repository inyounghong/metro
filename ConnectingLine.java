import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

// Line connecting two stations. Child of a parent Line.
public class ConnectingLine {
	
	private Line parent;
	private Station s1;
	private Station s2;
	private Line2D line;
	private Line2D line2;
	
	public ConnectingLine(Line parent, Station s1, Station s2) {
		this.parent = parent;
		this.s1 = s1;
		this.s2 = s2;
		
		this.line = new Line2D.Double(s1.getPoint(), s2.getPoint());
	}
	
	public Line getParent() {
		return parent;
	}
	
	public Station[] getStations() {
		return new Station[]{s1, s2};
	}
	
	public Line2D getLine() {
		return line;
	}
	
	/*
	 * 	Remove point after finishing dragging the line
	 */
	public void removePoint() {
		line = new Line2D.Double(s1.getPoint(), s2.getPoint());
		line2 = null;
	}
	
	
	/*
	 * 	Add mouse point for dragging this line
	 */
	public void addPoint(Point2D p) {
		line = new Line2D.Double(s1.getPoint(), p);
		line2 = new Line2D.Double(p, s2.getPoint());
	}
	
	/*
	 * Inserts a station between the two stations of this connecting line
	 */
	public void insertStation(Station s) {
		
		Station oldS2 = s2;
		
		// Fix current line
		s2 = s; 
		line = new Line2D.Double(s1.getPoint(), s.getPoint());
		line2 = null;
		
		System.out.println("Inserting " + s);
		ConnectingLine newLine = new ConnectingLine(parent, s, oldS2);
		parent.insertConnectingLine(this, newLine);
	}
	
	
	/*
	 * 	Paint connecting line
	 */
	public void paint(Graphics2D g) {
		g.draw(line);
		if (line2 != null) {
			g.draw(line2);
		}
	}
	
	public String toString() {
		return "CL " + s1 + "-" + s2;
	}
 
}
