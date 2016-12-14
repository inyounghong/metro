import java.awt.Shape;

public class Passenger {
	private Shape shape;
	private Game.Type type;
	

	public Passenger(Game.Type type) {
		this.type = type;
	}
	
	public Game.Type getType() {
		return type;
	}
	
	public String toString() {
		return "P-" + type.toString();
	}
}
