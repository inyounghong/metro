import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class Board extends JPanel{

    public static int WIDTH = 600;
	public static int HEIGHT = 500;
	private static int CLICK_BOX_WIDTH = 6;
	
	private Station clickedStation 			= null;
	private Line clickedLine			 	= null;
	private ConnectingLine draggingLine 	= null;
	private Rectangle2D clickBox	 		= null;

    public Board() {
        MyListener myListener = new MyListener();
        addMouseListener(myListener);
        addMouseMotionListener(myListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
       
        // Paint lines
        for (Line line: Game.lines) {
        	line.paint(g2);
        }
        
        // Paint stations
        g2.setStroke(new BasicStroke(3));
        for (Station station: Game.stations) {
        	station.paint(g2);
        }

        g2.dispose();
    }
    
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
    
    public static Point getCenter() {
		return new Point(WIDTH/2, HEIGHT/2);
	}
	
	private Station getClickedStation(MouseEvent e) {
    	for (Station station: Game.stations) {
    		boolean mouseInShape = station.getShape().contains(e.getPoint());
    		if (mouseInShape){
    			return station;
    		};
    	}
    	return null;
    }
	
	private Line getClickedLine(MouseEvent e, Station s) {
		if (s.getLines().size() == 0) {
			return null;
		}
		return s.getLines().get(0);
	}
	
	private void addToLine(Line l, Station s1, Station s2) {
		l.append(s1, s2);
	}
	
	
	private void createNewLine(Station s1, Station s2) {
		
		// Must have enough lines
		if (Game.availableLines < 1) {
			System.out.println("Don't have enough lines!");
			return;
		}
		
		// Create new line
		Line newLine = new Line(s1, s2);
		Game.lines.add(newLine);
		Game.ends.add(newLine.getHead());
		Game.ends.add(newLine.getTail());
		Game.availableLines--;
		
		System.out.println("Created line " + newLine + " between " + s1 + " " + s2);
	}
	
    /* 
     * @return Line that has been clicked, or null if no line was clicked
     */
    private ConnectingLine getDragLine(MouseEvent e) {
    	for (Line line: Game.lines) {
    		for (ConnectingLine l : line.getLines()) {
    			if (l.getLine().intersects(clickBox)) {
        			return l;
        		}
    		}
    		
    	}
    	return null;
    }
    
    /*
     * @return Rectangle2D square of width CLICK_BOX_WIDTH around the click event
     */
    private Rectangle2D makeClickBox(MouseEvent e) {
    	double x = e.getX() - CLICK_BOX_WIDTH/2;
    	double y = e.getY() - CLICK_BOX_WIDTH/2;
    	return new Rectangle2D.Double(x,y, CLICK_BOX_WIDTH, CLICK_BOX_WIDTH);
    }
    
    private void deselectCurrent() {
    	clickedStation.setStatus(Station.Status.NORMAL);
    	clickedStation = null;
		clickedLine = null;
    }
    
    /*
     * Handles clicking a station
     */
    private void handleClickedStation(MouseEvent e, Station s) {
    	System.out.println("Clicked " + s);
		s.setStatus(Station.Status.CLICKED);
		
		if (clickedStation == null) { // First click of a station 
			clickedStation = s;
			clickedLine = getClickedLine(e, s);
		}
		else if (clickedStation == s) {
			deselectCurrent();
		} else { 
			if (clickedLine == null) { // Finishing a new line connection
				createNewLine(clickedStation, s);
			} else { // Appending to existing line connection
				addToLine(clickedLine, clickedStation, s);
			}
			s.setStatus(Station.Status.NORMAL);
			clickedStation.setStatus(Station.Status.NORMAL);
			clickedStation = null;
		}
		
		repaint();
    }
    
    /*
     * @return Station that got dragged over by line
     */
    private Station getDraggedStation(MouseEvent e) {
    	for (Station s : Game.stations) {
			if (s.getBox().contains(e.getPoint())) {
				return s;
			}
		}
    	return null;
    }
	
	private class MyListener extends MouseInputAdapter {

        public void mouseMoved(MouseEvent e) {
        	for (Station station: Game.stations) {
    			boolean mouseInShape = station.getShape().contains(e.getPoint());
    			boolean hovered = station.getStatus() == Station.Status.HOVER;
    			
    			// If mouse enters shape 
    			if (station.getStatus() == Station.Status.CLICKED) {
    				continue;
    			}
    			if(mouseInShape && !hovered) {
    				station.setStatus(Station.Status.HOVER);
    				repaint();
    			} else if (!mouseInShape && hovered){ // Mouse leaves shape
    				station.setStatus(Station.Status.NORMAL);
    				repaint();
    			}
    		}
        	
        	// Check if line is hovered over
//        	for (Line line: Game.lines) {
//        		boolean pathHovered = line.getPath().contains(e.getPoint());
//        		if (pathHovered) {
//        			line.setStatus(Line.Status.HOVER);
//        			break;
//        		} else {
//        			line.setStatus(Line.Status.NORMAL);
//        		}
//        	}
        }
        
        
        
        public void mouseDragged(MouseEvent e) {
        	
        	// If there is a line currently being dragged
        	if (draggingLine != null) {
        		draggingLine.addPoint(e.getPoint());
        		
        		// Check if dragged line intersects a station's bounding box
        		Station s = getDraggedStation(e);
        		if (s != null) {
        			draggingLine.insertStation(s);
        			draggingLine = null;
        		}
        	}
        }
        
        public void mouseReleased(MouseEvent e) {
        	
        	// If there had been a line getting dragged that was just released
        	if (draggingLine != null) {
        		draggingLine.removePoint();
        	}
        }
        
        public void mouseClicked(MouseEvent e) {
        	
        	// Make the mouse click bounding box
        	clickBox = makeClickBox(e);

    		Station s2 = getClickedStation(e);
    		// New station selected
    		if (s2 != null) {
    			handleClickedStation(e, s2);
    			return;
    		}
    		
    		// No station selected
    		if (clickedStation != null) {
        		deselectCurrent();
    		}
    		
    		draggingLine = getDragLine(e);
        }
    }

}