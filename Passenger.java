import java.awt.Shape;

public class Passenger {
	private Shape shape;
	private Station.Type type;
	

	public Passenger(Station.Type type) {
		this.type = type;
	}
	
	public Station.Type getType() {
		return type;
	}
	

}
