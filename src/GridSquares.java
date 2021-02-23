import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class GridSquares {
	
	int side = 2;
	int value;
	JButton button = new JButton();
	boolean isHole = false;
	boolean isGuide = false;
	final String imagePath = ".\\img\\";

    Icon elephant = new ImageIcon(imagePath + "elephant.jpg");
    Icon camel = new ImageIcon(imagePath + "camel.jpg");
    Icon horse = new ImageIcon(imagePath + "horse.png");
    Icon dog = new ImageIcon(imagePath + "dog.jpg");
    Icon cat = new ImageIcon(imagePath + "cat.png");
    Icon rabbit = new ImageIcon(imagePath + "rabbit.jpg");
    Icon noImage;

	/*
	 * Elephant: 6
	 * Camel: 5
	 * Horse: 4
	 * Dog: 3
	 * Cat: 2
	 * Rabbit: 1
	 * 
	 */

	public void setAll(GridSquares square) {//Sets this gridsquare equal to another one that is inputed
		side = square.getSide();
		value = square.getValue();
	}
	
	public void setAll(int side, int value) {//Sets the value and side to inputed value and side
		this.value = value;
		this.side = side;
	}
	
	public void changeButton() {
		//Sets the color and picture on the button depending on what the side and value
		if(isGuide) {//If it is a guide, it doesn't change anything
			button.setEnabled(false);
			button.setBackground(Color.WHITE);
		}
		else {
			if (side == 0) {//Sets color to yellow if side is yellow
				button.setBackground(Color.YELLOW);
			}
			else if (side == 1) {//Sets color to blue if side is blue
				button.setBackground(Color.CYAN);
			}
			else if (side == 2) {//If the square is open and enabled, makes it light gray, other wise dark gray if it is disable and open
				button.setBackground(Color.LIGHT_GRAY);
				if(button.isEnabled()) {
					button.setBackground(Color.LIGHT_GRAY);
					button.setBorderPainted(true);
				}
				else {
					button.setBackground(Color.gray);
				}
			}
			//The following lines sets the image of the piece depending on the value
			if(value == 6) {
				button.setIcon(elephant);
			}
			else if (value == 5) {
				button.setIcon(camel);
			}
			else if (value == 4) {
				button.setIcon(horse);
			}
			else if (value == 3) {
				button.setIcon(dog);
			}
			else if (value == 2) {
				button.setIcon(cat);
			}
			else if (value == 1) {
				button.setIcon(rabbit);
			}
			else if(value == 0) {
				button.setIcon(noImage);
			}
		}
	}

	public void emptyHole() {//Removes piece on the trap
		if(button.isEnabled() == false) {
			button.setBackground(Color.red);	
		}
		else {
			button.setBackground(Color.pink);
		}
		setSide(2);
		value = 0;
		button.setText("");
	}
	
	//The remaining methods are getters and setters	
	
	public boolean isGuide() {
		return isGuide;
	}

	public void setGuide(boolean isGuide) {
		this.isGuide = isGuide;
	}
    
	public boolean isHole() {
		return isHole;
	}

	public void setHole(boolean isHole) {
		this.isHole = isHole;
	}
	
	public JButton getButton() {
		return button;
	}
	public void setButton(JButton button) {
		this.button = button;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	
}
