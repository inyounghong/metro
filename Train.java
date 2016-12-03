import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ListIterator;

public class Train {
	private int id;
	private Line line;
	private ArrayList<Passenger> passengers;
	
	private int x;
	private int y;
	
	public Train(Line line) {
		this.id = line.getTrains().size();
		this.line = line;
		this.passengers = new ArrayList<Passenger>();
		
		this.x = line.getStations().getFirst().getCenter().x;
		this.y = line.getStations().getFirst().getCenter().y;
	}

	public void paint(Graphics2D g) {
		g.setColor(line.getColor());
		
		g.fill(new Rectangle2D.Double(x, y, 10, 20));
	}
}
