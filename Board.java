import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class Board extends JPanel{

    private Random rand = new Random();
    public static int WIDTH = 600;
	public static int HEIGHT = 500;
	
	private Station clickedStation = null;
	private Line clickedLine = null;

    public Board() {

//        addMouseMotionListener(this);
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
		Game.availableLines--;
		System.out.println("Created line " + newLine + " between " + s1 + " " + s2);
	}
	
	private class MyListener extends MouseInputAdapter {

        public void mouseMoved(MouseEvent e) {
        	for (Station station: Game.stations) {
    			boolean mouseInShape = station.getShape().contains(e.getPoint());
    			boolean hovered = station.getHover();
    			
    			// If mouse enters shape 
    			if (station.getClicked()) {
    				continue;
    			}
    			if(mouseInShape && !hovered) {
    				station.setHover(true);
    				repaint();
    			} else if (!mouseInShape && hovered){ // Mouse leaves shape
    				station.setHover(false);
    				repaint();
    			}
    		}
        }
        
        public void mouseClicked(MouseEvent e) {

    		Station s2 = getClickedStation(e);
    		
    		// New station selected
    		if (s2 != null) {
    			System.out.println("Clicked " + s2);
    			s2.setClicked(true);
    			
    			if (clickedStation == null) { // First click of a station 
    				clickedStation = s2;
    				clickedLine = getClickedLine(e, s2);
    			}
    			else if (clickedStation == s2) { // Deselect
    				clickedStation = null;
    				clickedLine = null;
					s2.setClicked(false);
    			} else { 
    				if (clickedLine == null) { // Finishing a new line connection
    					createNewLine(clickedStation, s2);
    				} else { // Appending to existing line connection
    					addToLine(clickedLine, clickedStation, s2);
    				}
    				s2.setClicked(false);
    				clickedStation.setClicked(false);
    				clickedStation = null;
    			}
    			
    			repaint();
    			return;
    		}
    		
    		// No station selected
    		if (clickedStation != null) {
        		clickedStation.setClicked(false);
        		clickedStation.setHover(false);
        		clickedStation = null;
        		repaint();
    		}
    		
        }
	
		
        

    }

}