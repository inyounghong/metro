import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class End {
	
	private Status status;
	private Line line;
	private Station station;
	private double x;
	private double y;
	
	private int WIDTH = 20;
	private int HEIGHT = 20;
	
	public enum Status {
		NORMAL,
		HOVER,
		CLICKED
	}
	
	public End(Line line, Station s) {
		this.line = line;
		this.status = Status.NORMAL;
		this.station = s;
		setLocation(s);
	}
	
	public Station getStation() {
		return station;
	}
	
	public void setStation(Station s) {
		station = s;
		setLocation(s);
	}
	
	public void paint (Graphics2D g) {
		Color c = line.getColor();
		switch (status) {
			case NORMAL:
				g.setStroke(new BasicStroke(5));
				g.setColor(c);
			case HOVER:
				g.setStroke(new BasicStroke(6));
				g.setColor(c);
			case CLICKED:
				g.setStroke(new BasicStroke(6));
				g.setColor(c);
		}
		
		g.fill(new Rectangle2D.Double(x, y, WIDTH, HEIGHT));
	}
	
	private void setLocation(Station s) {
		x = s.getCenter().x + 10;
		y = s.getCenter().y + 10;
	}
}
