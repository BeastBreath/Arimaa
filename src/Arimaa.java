import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

public class Arimaa implements ActionListener{
	
	JFrame frame = new JFrame();
	JFrame rulesFrame = new JFrame();
	Container center = new Container();
	Container west = new Container();
	Container east = new Container();
	Container south = new Container();
	Container northAfterStart = new Container();
	Container rulesNorth = new Container();
	Container rulesCenter = new Container();
	Container rulesSouth = new Container();
	
	JLabel rulesTitle = new JLabel();
	JLabel rulesWords = new JLabel();
	ScrollPane scrollRule = new ScrollPane();
	
	JButton startGame = new JButton("Start Game");
    JButton openRules = new JButton("Help");
	JButton endTurn = new JButton("End Turn");
	JButton closeRules = new JButton("Close");
	JButton reset = new JButton("Reset");
	TextField moves = new TextField("Moves: 4");
	TextField turns = new TextField("Turn: Yellow");
	final int YELLOW = 0;
	final int BLUE = 1;
	final int OPEN = 2;
	final int YELLOWTURN = 0;
	final int BLUETURN = 1;
	int turn = 0;
	int oppTurn = 1;
	int move = 0;
	final int NOCLICK = 0;
	final int FIRSTCLICK = 1;
	final int PUSHPULL = 2;
	int state = NOCLICK;
	int rowI;
	int columnJ;
	int pushPullRowI;
	int pushPullColumnJ;
	int pushPullDirection;
	final int ROWS = 0;
	final int COLUMNS = 1;
	int part;
	GridSquares[][] board = new GridSquares[9][9];
	GridSquares[][] yellowWest = new GridSquares[8][2];
	GridSquares[][] blueEast = new GridSquares[8][2];
	boolean gameStarted = false;
	boolean gameOver = false;
	final int GOAL = 0;
	final int ELIMINATION = 1;
	final int IMMOBILIZATION = 2;
	ArrayList<GridSquares[][]> pastBoardsBlue = new ArrayList<GridSquares[][]>();
	ArrayList<GridSquares[][]> pastBoardsYellow = new ArrayList<GridSquares[][]>();
	ArrayList<GridSquares[][]> pastBoards = new ArrayList<GridSquares[][]>();
	
	public boolean checkIfSame(GridSquares[][] first, GridSquares[][] second) {
		//Checks if two arrays of gridsquares are the same
		for (int i = 0; i < first.length; i++) {
			for (int j = 0; j < first[i].length; j++) {
				if(!(second[i][j].getSide() == first[i][j].getSide()) &&
						!(second[i][j].getValue() == first[i][j].getValue())) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void setAllToSame(GridSquares[][] first, GridSquares[][] second) {
		//Sets one array of gridsquares equal to another. Both are inputed
		for (int i = 0; i < first.length; i++) {
			for (int j = 0; j < first[i].length; j++) {
				second[i][j] = first[i][j];
			}
		}
	}
	
	public void setColorToSame(GridSquares[][] first, GridSquares[][] second) {
		//Records the pieces of one players (whose turn it is)position in an array of gridsquares
		for (int i = 0; i < first.length; i++) {//Goes through each column
			for (int j = 0; j < first[i].length; j++) {//Goes through each row
				GridSquares empty= new GridSquares();
				empty.setAll(OPEN, 0);//Gridsquare that is open
				if(first[i][j].getSide() == turn) {//If the first one is equal to the side whose turn it is, it makes the new gridsquare the same
					second[i][j] = first[i][j];
				}
				else{//Otherwise it makes it empty
					second[i][j] = empty;
				}
			}
		}
	}
	
	public boolean checkIfTwiceBefore() {
		//Checks if current position has been repeated twice before
		int numberOfTimesBefore = 0;
		if(turn == YELLOW) {//if turn is yellow
			for (int i = 0; i < pastBoardsYellow.size() - 1; i++) {//Goes through each previous board
				if(checkIfSame(pastBoardsYellow.get(pastBoardsYellow.size() - 1), pastBoardsYellow.get(i))){
					numberOfTimesBefore++;
				}
			}
		}
		else if(turn == BLUE) {//If turn is blue
			for (int i = 0; i < pastBoardsBlue.size() - 1; i++) {//Goes through each previous board
				if(checkIfSame(pastBoardsBlue.get(pastBoardsBlue.size() - 1), pastBoardsBlue.get(i))){
					numberOfTimesBefore++;
				}
			}
		}
		if(numberOfTimesBefore >= 2) {//Checks if the position counter is greater than or equal to two
			return true;
		}
		return false;
	}
	
	public void undoLastMove() {
		//Undo's last move: It takes out last board from ArrayLists
		pastBoards.remove(pastBoards.size() - 1);
		if(turn == YELLOW) {//Takes out move from yellow list of past boards if turn was yellow
			pastBoardsYellow.remove(pastBoardsYellow.size() - 1);
		}
		else if(turn == BLUE) {//Takes out move from blue list of boards if turn was blue
			pastBoardsBlue.remove(pastBoardsBlue.size() - 1);
		}
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {//Goes through each gridsquare on the board and sets all pieces on board to the previous position
				board[i][j].setAll(pastBoards.get(pastBoards.size() - 2)[i][j]);
			}
		}
	}
	
	public boolean freeze(int i, int j) {
		//When inputed two integers, the row and the column, this method returns whether or not the piece is frozen
		//Checks the cardinal directions and if there is a higher enemy piece, surroundingHigher is set to be true, and if a friendly piece is adjacent, sets surroundingSameColor to true
		boolean surroundingHigher = false;//Variable for checking if there is an opposite piece with a higher value adjacent
		boolean surroundingSameSide = false;//Variable for checking if friendly piece is adjacent
		GridSquares temp = new GridSquares();
		if(i < 8) {//Checks Below
			temp.setAll(board[i + 1][j]);
			if((temp.getSide() == oppTurn) && (temp.getValue() > board[i][j].getValue())) {
				surroundingHigher = true;
			}
			else if(temp.getSide() == turn) {
				surroundingSameSide = true;
			}
		}
		if(i > 0) {//Checks Above
			temp.setAll(board[i - 1][j]);
			if((temp.getSide() == oppTurn) && (temp.getValue() > board[i][j].getValue())) {
				surroundingHigher = true;
			}
			else if(temp.getSide() == turn) {
				surroundingSameSide = true;
			}
		}
		if(j < 8) {//Checks Right
			temp.setAll(board[i][j + 1]);
			if((temp.getSide() == oppTurn) && (temp.getValue() > board[i][j].getValue())) {
				surroundingHigher = true;
			}
			else if(temp.getSide() == turn) {
				surroundingSameSide = true;
			}
		}
		if(j > 0) {//Checks Left
			temp.setAll(board[i][j - 1]);
			if((temp.getSide() == oppTurn) && (temp.getValue() > board[i][j].getValue())) {
				surroundingHigher = true;
			}
			else if(temp.getSide() == turn) {
				surroundingSameSide = true;
			}
		}
		if(!surroundingSameSide && surroundingHigher) {
			return true;
		}
		else {
			return false;
		}
	}
	 
	public void onWin(int side, int methodOfWin) {
		//Takes in the method and the side that won, and produces a pop-up message 
		if(!gameOver) {//Makes it so that the message is only printed once
			String message = "";//Message that will be displayed
			if(side == BLUE) {
				message += "Blue ";
			}
			else if (side == YELLOW) {
				message += "Yellow ";
			}
			message += "wins by ";
			if(methodOfWin == GOAL) {
				message += "moving a rabbit to the goal";
			}
			else if(methodOfWin == ELIMINATION) {
				message += "capturing all the opponents rabbits";
				
			}
			else if(methodOfWin == IMMOBILIZATION) {
				message += "depriving the opponent of legal moves";
				
			}
			disableAll();	
			JOptionPane.showMessageDialog(frame, message);
			gameOver = true;//Changes the variable gameOver so that the win message will only show once
		}
	}
 	
	public int checkWin() {
		//Checks if a player has won by moving their rabbit to the goal, or by eliminating the other player�s rabbits and returns which side wo
		if(gameStarted) {//Only checks once game has started(because there are no rabbits on the board before the game starts
			int yellowRabbits = 0;
			int blueRabbits = 0;
			for(int j = 0; j < board.length; j++) {//Checks if any rabbits have made it to the goal
				if(board[0][j].getSide() == BLUE && board[0][j].getValue() == 1) {
					onWin(BLUE, GOAL);
					return BLUE;
				}
				if(board[7][j].getSide() == YELLOW && board[7][j].getValue() == 1) {
					onWin(YELLOW, GOAL);
					return YELLOW;
				}
			}
			for(int i = 0; i < board.length; i++) {//Checks if a player has eliminated all the other player's rabbits
				for (int j = 0; j < board[i].length; j++) {
					if(board[i][j].getValue() == 1) {
						if(board[i][j].getSide() == YELLOW) {
							yellowRabbits++;
						}
						else if(board[i][j].getSide() == BLUE) {
							blueRabbits++;
						}
					}
				}
			}
			if(blueRabbits == 0) {//If blue won by elimination
				onWin(YELLOW, ELIMINATION);
				return BLUE;
			}
			else if(yellowRabbits == 0) {//If yellow won by elimination
				onWin(BLUE, ELIMINATION);
				return YELLOW;
			}
		}
		return OPEN;
	}
	
	public void switchSide() {
		//Ends the turn Makes the moves zero, and switches which side�s turn it is
		state = NOCLICK;
		disableAll();
		if(turn == BLUE) {//If turn is blue, switches turn to yellow
			turn = YELLOW;
			oppTurn = BLUE;
			turns.setText("Turn: Yellow");
		}
		else if(turn == YELLOW) {//If turn is yellow, switches turn to blue
			turn = BLUE;
			oppTurn = YELLOW;
			turns.setText("Turn: Blue");
		}
		enableColor();//Enables that colors pieces
		//Sets moves to zero and changes display
		move = 0;
		String movesDisplay = "Moves: ";
		int movesLeft = 4-move;
		movesDisplay += movesLeft;
		moves.setText(movesDisplay);
	}

	public void enableSelected() {
		//Enables the selected pieces
		if(state == NOCLICK) {
			return;
		}
		else if (state == FIRSTCLICK) {
			board[rowI][columnJ].getButton().setEnabled(true);
		}
		else if (state == PUSHPULL) {
			board[rowI][columnJ].getButton().setEnabled(true);
			board[pushPullRowI][pushPullColumnJ].getButton().setEnabled(true);
			//The next few lines change the color of the piece selected to be pushed/pulled
			if(board[pushPullRowI][pushPullColumnJ].getSide() == BLUE) {
				board[pushPullRowI][pushPullColumnJ].getButton().setBackground(Color.BLUE);
			}
			else {
				board[pushPullRowI][pushPullColumnJ].getButton().setBackground(Color.ORANGE);
			}
		}
		//The next lines change the color of the first selected piece to show it is selected
		if(board[rowI][columnJ].getSide() == BLUE) {
			board[rowI][columnJ].getButton().setBackground(Color.BLUE);
		}
		else {
			board[rowI][columnJ].getButton().setBackground(Color.ORANGE);
		}
	}
	
	public void enablePushPull() {
		//If the state is push and pull, it enables the squares that can be clicked on
		disableAll();
		if((rowI - pushPullRowI) == 1) {//Checks if pushpull direction is left/right
			if(pushPullRowI > 0 && board[pushPullRowI - 1][columnJ].getSide() == OPEN) {
				board[pushPullRowI - 1][columnJ].getButton().setEnabled(true);
			}
			if(rowI < 7 && board[rowI + 1][columnJ].getSide() == OPEN) {
				board[rowI + 1][columnJ].getButton().setEnabled(true);
			}
			pushPullDirection = ROWS;
		}
		else if((pushPullRowI - rowI) == 1) {//Checks if pushpull direction is left/right
			if(rowI > 0 && board[rowI - 1][columnJ].getSide() == OPEN) {
				board[rowI - 1][columnJ].getButton().setEnabled(true);
			}
			if(pushPullRowI < 7 && board[pushPullRowI + 1][columnJ].getSide() == OPEN) {
				board[pushPullRowI + 1][columnJ].getButton().setEnabled(true);
			}
			pushPullDirection = ROWS;
		}
		else if((columnJ - pushPullColumnJ) == 1) {//Checks if pushpull direction is up/down
			if(pushPullColumnJ > 0 && board[rowI][pushPullColumnJ - 1].getSide() == OPEN) {
				board[rowI][pushPullColumnJ - 1].getButton().setEnabled(true);
			}
			if (columnJ < 7 && board[rowI][columnJ + 1].getSide() == OPEN) {
				board[rowI][columnJ + 1].getButton().setEnabled(true);
			}
			pushPullDirection = COLUMNS;
		}
		else if((pushPullColumnJ - columnJ) == 1) {//Checks if pushpull direction is up/down
			if(columnJ > 0 && board[rowI][columnJ - 1].getSide() == OPEN) {
				board[rowI][columnJ - 1].getButton().setEnabled(true);
			}
			if(pushPullColumnJ < 7 && board[rowI][pushPullColumnJ + 1].getSide() == OPEN) {
				board[rowI][pushPullColumnJ + 1].getButton().setEnabled(true);
			}
			pushPullDirection = COLUMNS;
		}
	}	
	
	public void enableSurrounding() {
		//Enables buttons surrounding the piece if those squares are legal
		disableAll();
		GridSquares temp = new GridSquares();
		temp = board[rowI][columnJ];
		board[rowI][columnJ].getButton().setEnabled(true);

		if(rowI < 8 && board[rowI + 1][columnJ].getSide() == OPEN && 
				!(board[rowI][columnJ].getSide() == BLUE &&  board[rowI][columnJ].getValue() == 1)) {
			//If down is open and piece is not a blue rabbit
			board[rowI + 1][columnJ].getButton().setEnabled(true);
		} 
		else if (rowI < 8 && board[rowI + 1][columnJ].getSide() == oppTurn && 
				(temp.getValue() > board[rowI + 1][columnJ].getValue()) && move <= 2) {
			//If down is a enemy piece of lower value(for push/pull)
			board[rowI + 1][columnJ].getButton().setEnabled(true);
		}
		if(rowI > 0 && board[rowI - 1][columnJ].getSide() == OPEN && 
				!(board[rowI][columnJ].getSide() == YELLOW &&  board[rowI][columnJ].getValue() == 1)) {
			//If up is open and is not a yellow rabbit
			board[rowI - 1][columnJ].getButton().setEnabled(true);
		} 
		else if (rowI > 0 && board[rowI - 1][columnJ].getSide() == oppTurn && 
				(temp.getValue() > board[rowI - 1][columnJ].getValue()) && move <= 2) {
			//If up is enemy piece and lower value
			board[rowI - 1][columnJ].getButton().setEnabled(true);
		}
		if(columnJ < 8 && board[rowI][columnJ + 1].getSide() == OPEN) {
			//If right is open
			board[rowI][columnJ + 1].getButton().setEnabled(true);
		} 
		else if (columnJ < 8 && board[rowI][columnJ + 1].getSide() == oppTurn && 
				(temp.getValue() > board[rowI][columnJ + 1].getValue()) && move <= 2) {
			//If right is enemy piece of lower value
			board[rowI][columnJ + 1].getButton().setEnabled(true);
		}
		if(columnJ > 0 && board[rowI][columnJ - 1].getSide() == OPEN) {
			//If left is open
			board[rowI][columnJ - 1].getButton().setEnabled(true);
		} 
		else if (columnJ > 0 && board[rowI][columnJ - 1].getSide() == oppTurn && 
				(temp.getValue() > board[rowI][columnJ - 1].getValue()) && move <= 2) {
			//if left is enemy piece of lower value
			board[rowI][columnJ - 1].getButton().setEnabled(true);
		}
		setHoles();
		setGrid();
	}	
	
	public void enableColor() {
		//Enables all the pieces of the player whose turn it is unless the piece is frozen
		disableAll();
		setGrid();
		int enabledButtons = 0;
		if(turn == BLUE) {
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					//Goes through each square and if is blue, enables it
					if(board[i][j].getSide() == BLUE){
						board[i][j].getButton().setEnabled(true);
						enabledButtons++;
						if(freeze(i, j)) {
							board[i][j].getButton().setEnabled(false);
							enabledButtons--;
						}
					}
				}
			}
		}
		else if(turn == YELLOW) {
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if(board[i][j].getSide() == YELLOW){
						//Goes through each square and if is yellow, enables it
						board[i][j].getButton().setEnabled(true);
						enabledButtons++;
						if(freeze(i, j)) {
							board[i][j].getButton().setEnabled(false);
							enabledButtons--;
						}
					}
				}
			}
		}
		if(enabledButtons == 0) {//If there are no available pieces for a side to move, the opponent moves
			onWin(oppTurn, IMMOBILIZATION);
		}
	}

	public void enableAll() {
		//Enables the whole board
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				board[i][j].getButton().setEnabled(true);
			}
		}
		setHoles();
	}
	
	public void disableAll() {
		//Disables everything besides the pieces that are selected
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				board[i][j].getButton().setEnabled(false);
			}
		}
		enableSelected();
	}

	public void enableColorStart(int side) {
		//Enables the starting positions of the side which has a selected piece(for putting pieces on the board at the sstart of the game)
		enableAll();
		if(side == BLUE) {
			for(int i = 0; i < board.length - 3; i++) {
				for(int j = 0; j < board[i].length; j++) {
					board[i][j].getButton().setEnabled(false);
				}
			}
		}
		else if (side == YELLOW) {
			for(int i = 2; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					board[i][j].getButton().setEnabled(false);
				}
			}
		}
	}

	public boolean checkIfStart(){
		//Checks if all the pieces are on the board
		for(int i = 0; i < yellowWest.length; i++) {
			for(int j = 0; j < yellowWest[i].length; j++) {
				if(!(yellowWest[i][j].getSide() == OPEN)
						|| !(blueEast[i][j].getSide() == OPEN)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void setGrid() {
		//Basically redraws the grid, but it doesn't redraw it just changes the color and image on the button. And captures pieces in traps if no friendly pieces are adjacent
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j].changeButton();
				//The next few lines check if the piece is on a trap and if it should be taken off the board
				if(board[i][j].isHole && !(board[i][j].getSide() == OPEN)) {
					GridSquares temp = new GridSquares();
					boolean surroundingSameSide = false;
					if(i < 8) {
						temp.setAll(board[i + 1][j]);
						if(temp.getSide() == board[i][j].getSide()) {
							surroundingSameSide = true;
						}
					}
					if(i > 0) {
						temp.setAll(board[i - 1][j]);
						if(temp.getSide() == board[i][j].getSide()) {
							surroundingSameSide = true;
						}
					}
					if(j < 8) {
						temp.setAll(board[i][j + 1]);
						if(temp.getSide() == board[i][j].getSide()) {
							surroundingSameSide = true;
						}
					}
					if(j > 0) {
						temp.setAll(board[i][j - 1]);
						if(temp.getSide() == board[i][j].getSide()) {
							surroundingSameSide = true;
						}
					}
					if(!surroundingSameSide) {
						board[i][j].emptyHole();
					}
				}
				else if(board[i][j].isHole && board[i][j].getSide() == OPEN) {
					board[i][j].emptyHole();
				}
			}
		}
		if(gameStarted) {
			enableSelected();
		}
	}
	
	public void setInitialGrid() {
		//This is the same as set grid, except it also �redraws� the two grids on the sides and is only used before the game starts
		setGrid();
		for (int i = 0; i < yellowWest.length; i++) {
			for(int j = 0; j < yellowWest[i].length; j++) {
				yellowWest[i][j].changeButton();
				blueEast[i][j].changeButton();
			}
		}
		setHoles();
	}

	public void setPieces() {
		//Sets up the pieces at the beginning of the game
		for (int i = 0; i < yellowWest.length; i++) {
			for(int j = 0; j < yellowWest[i].length; j++) {
				//Sets the sides of the pieces
				yellowWest[i][j].setSide(YELLOW);
				blueEast[i][j].setSide(BLUE);
			}
		}
		yellowWest[0][0].setValue(6);
		yellowWest[1][0].setValue(5);
		yellowWest[2][0].setValue(4);
		yellowWest[3][0].setValue(4);
		yellowWest[4][0].setValue(3);
		yellowWest[5][0].setValue(3);
		yellowWest[6][0].setValue(2);
		yellowWest[7][0].setValue(2);
		blueEast[0][1].setValue(6);
		blueEast[1][1].setValue(5);
		blueEast[2][1].setValue(4);
		blueEast[3][1].setValue(4);
		blueEast[4][1].setValue(3);
		blueEast[5][1].setValue(3);
		blueEast[6][1].setValue(2);
		blueEast[7][1].setValue(2);
		
		for(int i = 0; i < yellowWest.length; i++) {//Sets up rabbits
			yellowWest[i][1].setValue(1);
			blueEast[i][0].setValue(1);
		}
		
	}

	public Arimaa() {
		//Constructor
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {//Initializes all board squares
				board[i][j] = new GridSquares();
			}
		}
		for (int i = 0; i < yellowWest.length; i++) {
			for (int j = 0; j < yellowWest[i].length; j++) {//Initializes all squares on both sides
				yellowWest[i][j] = new GridSquares();
				blueEast[i][j] = new GridSquares();
			}
		}
		setInitialLayout();
		setPieces();
		setRules();
		setInitialGrid();
	}
	
	public void setRules() {
		//Sets up frame that shows the rules
		
		rulesFrame.setSize(850, 600);
		rulesFrame.setLayout(new BorderLayout());
		rulesNorth.setLayout(new GridLayout(1,1));
		rulesCenter.setLayout(new GridLayout(1,1));
		rulesSouth.setLayout(new GridLayout(1,1));

		rulesFrame.setVisible(false);
		closeRules.setEnabled(true);
		closeRules.addActionListener(this);
		rulesNorth.add(rulesTitle);
		rulesCenter.add(scrollRule);
		rulesTitle.setText("RULES");
		rulesTitle.setFont(new Font("bigBold", Font.BOLD, 20));
		rulesWords.setText("<html> Arimaa is played on an 8�8 board with four trap squares. There are six kinds of pieces, ranging from elephant (strongest) to rabbit <br/>"
				+ "(weakest). Stronger pieces can push or pull weaker pieces, and stronger pieces freeze weaker pieces. Pieces can be captured by <br/>"
				+ "dislodging them onto a trap square when they have no orthogonally adjacent friendly pieces. The two players, Yellow and Blue, each <br/>"
				+ "control sixteen pieces. These are, in order from strongest to weakest: one elephant, one camel, two horses, two dogs, two cats, and<br/>"
				+ " eight rabbits. These may be represented by the king, queen, rooks, bishops, knights, and pawns respectively when one plays using a <br/>"
				+ "chess set.<br/><br/>"
				+ "Objective<br/>"
				+ "The main object of the game is to move a rabbit of one's own color onto the home rank of the opponent, which is known as a goal. <br/>"
				+ "Thus Blue wins by moving a blue rabbit to the first rank, and Yellow wins by moving a yellow rabbit to the eigth rank. <br/>"
				+ "The game can also be won by capturing all of the opponent's rabbits (elimination) or by depriving the opponent of legal moves <br/>"
				+ "(immobilization). Compared to goal, these are uncommon.<br/><br/>"
				+ "Setup<br/>"
				+ "At the start of the game, players take the pieces from the sides of the board and place them in any configeration on the board. <br/>"
				+ "Once all the pieces are on the board, the players can push the start game button to start the game<br/><br/>"
				+ "Movement<br/>"
				+ "After the pieces are placed on the board, the players alternate turns, starting with Yellow. A turn consists of making one to four<br/>"
				+ "steps. With each step a piece may move into an unoccupied square one space left, right, forward, or backward, except that<br/>"
				+ "rabbits may not step backward. The steps of a turn may be made by a single piece or distributed among several pieces in any order.<br/>"
				+ "A player can decide to end their turn by pushing the end turn button. A player can only do this if they have used atleast one move.<br/>"
				+ "A turn must make a net change to the position. Thus one cannot, for example, take one step forward and one step back with the<br/>"
				+ "same piece. Furthermore, one's turn may not create the same position with the same player to move as has been created twice<br/>"
				+ "before. The prohibitions on passing and repetition make Arimaa a drawless game.<br/><br/>"
				+ "Pushing and Pulling<br/>"
				+ "A player may use two consecutive steps of a turn to dislodge an opposing piece with a stronger friendly piece which is adjacent<br/>"
				+ "in one of the four cardinal directions. For example, a player's dog may dislodge an opposing rabbit or cat, but not a dog, horse,<br/>"
				+ "camel, or elephant. The stronger piece may pull or push the adjacent weaker piece. When pulling, the stronger piece steps into<br/>"
				+ "an empty square, and the square it came from is occupied by the weaker piece. Friendly pieces may not be dislodged. A piece may<br/>"
				+ "not push and pull simultaneously.<br/><br/>"
				+ "Freezing<br/>"
				+ "A piece which is adjacent in any cardinal direction to a stronger opposing piece is frozen, unless it is also adjacent to a friendly<br/>"
				+ "piece. Frozen pieces may not be moved by the owner, but may be dislodged by the opponent. A frozen piece can freeze another still weaker<br/>"
				+ "piece. An elephant cannot be frozen, since there is nothing stronger, but an elephant can be blockaded.<br/><br/>"
				+ "Capturing<br/>"
				+ "A piece which enters a trap square is captured and removed from the game unless there is a friendly piece orthogonally adjacent. A piece<br/>"
				+ "on a trap square is captured when all adjacent friendly pieces move away. Note that a piece may voluntarily step into a trap square, even<br/>"
				+ "if it is thereby captured. Also, the second step of a pulling maneuver is completed even if the piece doing the pulling is captured on<br/>"
				+ "the first step."
				+ "<Html>");
		
		scrollRule.add(rulesWords);
		rulesSouth.add(closeRules);
		rulesFrame.add(rulesNorth, BorderLayout.NORTH);
		rulesFrame.add(rulesCenter, BorderLayout.CENTER);
		rulesFrame.add(rulesSouth, BorderLayout.SOUTH);
	}
	
	private void setHoles() {
		//At the beginning of the game, sets the four traps
		board[2][3].setHole(true);
		board[5][6].setHole(true);
		board[2][6].setHole(true);
		board[5][3].setHole(true);
	}

	public void setInitialLayout() {
		//Sets up the initial layout of the game including the bottom and side grids
		frame.setSize(700, 600);
		frame.setLayout(new BorderLayout());
		center. setLayout(new GridLayout(9,9));
		east.setLayout(new GridLayout(8,2));
		west.setLayout(new GridLayout(8,2));
		south.setLayout(new GridLayout(1,2));
		northAfterStart.setLayout(new GridLayout(1,4));
		
		for (int i = 0; i < blueEast.length; i++) {//Adds buttons and sets the enabled and adds a actionlistener to pieces on the side
			east.add(blueEast[i][0].getButton());
			east.add(blueEast[i][1].getButton());
			west.add(yellowWest[i][0].getButton());
			west.add(yellowWest[i][1].getButton());
			blueEast[i][0].getButton().setEnabled(true);
			blueEast[i][1].getButton().setEnabled(true);
			yellowWest[i][0].getButton().setEnabled(true);
			yellowWest[i][1].getButton().setEnabled(true);
			blueEast[i][0].getButton().addActionListener(this);
			blueEast[i][1].getButton().addActionListener(this);
			yellowWest[i][0].getButton().addActionListener(this);
			yellowWest[i][1].getButton().addActionListener(this);
		}
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {//Sets the buttons in the middle
				center.add(board[i][j].getButton());
				board[i][j].getButton().setEnabled(true);
				board[i][j].getButton().addActionListener(this);
				board[i][j].getButton().setBackground(Color.LIGHT_GRAY);
			}
		}
		for(int i = 0; i < board.length; i++) {//Sets up the letters and numbers on the sides of the board
			board[8][i].setGuide(true);
			char letter = (char) (i + 96);
			board[8][i].getButton().setText(String.valueOf(letter));
			board[i][0].setGuide(true);
			board[i][0].getButton().setText(String.valueOf(i + 1));
			board[8][0].getButton().setText("");
		}
		startGame.setEnabled(true);
		startGame.addActionListener(this);
		south.add(startGame);
		south.add(openRules);
		setHoles();
		endTurn.setEnabled(true);
		endTurn.addActionListener(this);
		northAfterStart.add(endTurn);
		northAfterStart.add(moves);
		moves.setEditable(false);
		northAfterStart.add(turns);
		turns.setEditable(false);
		openRules.setEnabled(true);
		openRules.addActionListener(this);
		
		frame.add(west, BorderLayout.WEST);
		frame.add(east, BorderLayout.EAST);
		frame.add(south, BorderLayout.SOUTH);
		frame.add(center, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		disableAll();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Arimaa();
	}

	public GridSquares getFirstClick() {
		//Returns the first click from the static variables part, rowI, and columnJ
		if (part == 0) {
			return yellowWest[rowI][columnJ];
		}
		else if(part == 1) {
			return board[rowI][columnJ];
		}
		else{
			return blueEast[rowI][columnJ];
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		//This is the method that is called when a button is clicked
		setGrid();
		if (gameStarted && e.getSource().equals(board[rowI][columnJ].getButton()) && (state == FIRSTCLICK || state == PUSHPULL)) {
			//This is for when the player pushes the selected button again, meaning to unselect the selected piece
			state = NOCLICK;
			enableColor();
			return;
		}
		else if (e.getSource().equals(board[pushPullRowI][pushPullColumnJ].getButton()) && state == PUSHPULL) {
			//When the player wants to unselect the second piece they selected (the enemy piece meant to be pushed or pulled)
			state = FIRSTCLICK;
			enableSurrounding();
			return;
		}
		for (int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				//Goes through each button and checks if that was the button that was clicked
				if (e.getSource().equals(board[i][j].getButton())) {//If that was the button clicked
					if (state == FIRSTCLICK && !gameStarted) {
						//This is before the game starts and moves the selected piece onto the board
						GridSquares temp = new GridSquares();
						temp.setAll(getFirstClick());
						getFirstClick().setAll(board[i][j]);
						board[i][j].setAll(temp);
						state = NOCLICK;
						disableAll();
					}
					else if (gameStarted) {
						if(state == NOCLICK) {
							//Selects the piece that the player wants to move
							 rowI = i;
							 columnJ = j;
							 state = FIRSTCLICK;
							 enableSurrounding();
						}
						else if(state == FIRSTCLICK) {
							//This means this is the second click(the player already selected the first piece)
							if(board[i][j].getSide() == OPEN) {
								//This is if the second click is open, and this switches the pieces
								GridSquares temp = new GridSquares();
								temp.setAll(board[i][j]);
								board[i][j].setAll(board[rowI][columnJ]);
								board[rowI][columnJ].setAll(temp);
								
								//The following lines check if the current position of the pieces have been repeated before
								GridSquares[][] current = new GridSquares[board.length][board[0].length];
								setAllToSame(board, current);
								pastBoards.add(current);
								GridSquares[][] currentColor = new GridSquares[board.length][board[0].length];
								setColorToSame(board, currentColor);
								if(turn == YELLOW) {
									pastBoardsYellow.add(currentColor);
								}
								else {
									pastBoardsBlue.add(currentColor);
								}
								if(checkIfTwiceBefore()) {//If it has been done twice before, undo the move and make a popup message saying it has been done before
									temp.setAll(board[i][j]);
									board[i][j].setAll(board[rowI][columnJ]);
									board[rowI][columnJ].setAll(temp);
									undoLastMove();
									setGrid();
									JOptionPane.showMessageDialog(frame, "This is not a valid move because this position has been created by your pieces twice before");
								}
								else {//If it hasn't been made twice before, it keeps the move and finishes by changing the number of moves left and enabling the color or switching sides
									move++;
									String movesDisplay = "Moves: ";
									int movesLeft = 4-move;
									movesDisplay += movesLeft;
									moves.setText(movesDisplay);
									state = NOCLICK;
									if(move == 4) {
										switchSide();
									}
									else {
										enableColor();
									}
								}
							}
							else if(board[i][j].getSide() == oppTurn) {
								//If the second click is the enemy piece, 
								pushPullRowI = i;
								pushPullColumnJ = j;
								state = PUSHPULL;
								enablePushPull();
							}
						}
						else if(state == PUSHPULL) {
							//If a button is selected and the button that is pushed is the third square, it moves the two pieces into open square
							if(pushPullDirection == ROWS) {//If the two selected are up/down of each other
								if(Math.abs(i - rowI) == 1) {//If the piece selected is on the on the first piece's side
									GridSquares temp = new GridSquares();
									temp.setAll(board[i][j]);
									board[i][j].setAll(board[rowI][columnJ]);
									board[rowI][columnJ].setAll(board[pushPullRowI][pushPullColumnJ]);
									board[pushPullRowI][pushPullColumnJ].setAll(temp);
									
									//The following lines add the current board to the list of past boards
									GridSquares[][] current = new GridSquares[board.length][board[0].length];
									setAllToSame(board, current);
									pastBoards.add(current);
									GridSquares[][] currentColor = new GridSquares[board.length][board[0].length];
									setColorToSame(board, currentColor);
									if(turn == YELLOW) {
										pastBoardsYellow.add(currentColor);
									}
									else {
										pastBoardsBlue.add(currentColor);
									}
									//The following lines check if the position has been repeated twice before
									if(checkIfTwiceBefore()) {//If it has been repeated twice before, it undos the move 
										temp.setAll(board[i][j]);
										board[i][j].setAll(board[pushPullRowI][pushPullColumnJ]);
										board[pushPullRowI][pushPullColumnJ].setAll(board[rowI][columnJ]);
										board[rowI][columnJ].setAll(temp);
										undoLastMove();
										setGrid();
										JOptionPane.showMessageDialog(frame, "This is not a valid move because this position has been created by your pieces twice before");
									}
									else {//If it hasn't been repeated twice before, it keeps going with the move
										move += 2;
										String movesDisplay = "Moves: ";
										int movesLeft = 4-move;
										movesDisplay += movesLeft;
										moves.setText(movesDisplay);
										state = NOCLICK;
										if(move == 4) {//If the turn is over, it switches side
											switchSide();
										}
										else {//If it isn't over, it enables the color
											enableColor();
										}
									}
								}
								else if(Math.abs(i - pushPullRowI) == 1) {//If the selected piece is on the enemy piece side
									GridSquares temp = new GridSquares();
									temp.setAll(board[i][j]);
									board[i][j].setAll(board[pushPullRowI][pushPullColumnJ]);
									board[pushPullRowI][pushPullColumnJ].setAll(board[rowI][columnJ]);
									board[rowI][columnJ].setAll(temp);
									
									GridSquares[][] current = new GridSquares[board.length][board[0].length];
									setAllToSame(board, current);
									pastBoards.add(current);
									GridSquares[][] currentColor = new GridSquares[board.length][board[0].length];
									setColorToSame(board, currentColor);
									if(turn == YELLOW) {
										pastBoardsYellow.add(currentColor);
									}
									else {
										pastBoardsBlue.add(currentColor);
									}
									//The following lines check if the position has been repeated twice before
									if(checkIfTwiceBefore()) {//If it has been repeated twice before, it undos the move 
										temp.setAll(board[i][j]);
										board[i][j].setAll(board[rowI][columnJ]);
										board[rowI][columnJ].setAll(board[pushPullRowI][pushPullColumnJ]);
										board[pushPullRowI][pushPullColumnJ].setAll(temp);
										undoLastMove();
										setGrid();
										JOptionPane.showMessageDialog(frame, "This is not a valid move because this position has been created by your pieces twice before");
									}
									else {//If the position has not been repeated twice before, continue with the turn
										move += 2;
										String movesDisplay = "Moves: ";
										int movesLeft = 4-move;
										movesDisplay += movesLeft;
										moves.setText(movesDisplay);
										state = NOCLICK;
										if(move == 4) {//If the turn is over, it switches side
											switchSide();
										}
										else {//If turn is not over, enable all the pieces of that color
											enableColor();
										}
									}
								}
							}
							else if(pushPullDirection == COLUMNS) {//If the two pieces are left/right of each other
								if(Math.abs(j - columnJ) == 1) {//If the open square is on the first piece's side
									GridSquares temp = new GridSquares();
									temp.setAll(board[i][j]);
									board[i][j].setAll(board[rowI][columnJ]);
									board[rowI][columnJ].setAll(board[pushPullRowI][pushPullColumnJ]);
									board[pushPullRowI][pushPullColumnJ].setAll(temp);
									
									GridSquares[][] current = new GridSquares[board.length][board[0].length];
									setAllToSame(board, current);
									pastBoards.add(current);
									GridSquares[][] currentColor = new GridSquares[board.length][board[0].length];
									setColorToSame(board, currentColor);
									if(turn == YELLOW) {
										pastBoardsYellow.add(currentColor);
									}
									else {
										pastBoardsBlue.add(currentColor);
									}
									//The following lines check if the position has been repeated twice before
									if(checkIfTwiceBefore()) {//If it has been repeated twice before, it undos the move 
										temp.setAll(board[i][j]);
										board[i][j].setAll(board[pushPullRowI][pushPullColumnJ]);
										board[pushPullRowI][pushPullColumnJ].setAll(board[rowI][columnJ]);
										board[rowI][columnJ].setAll(temp);
										undoLastMove();
										setGrid();
										JOptionPane.showMessageDialog(frame, "This is not a valid move because this position has been created by your pieces twice before");
									}
									else {//If the position has not been repeated twice before, continue with the turn
										move += 2;
										String movesDisplay = "Moves: ";
										int movesLeft = 4-move;
										movesDisplay += movesLeft;
										moves.setText(movesDisplay);
										state = NOCLICK;
										if(move == 4) {//If the turn is over, it switches side
											switchSide();
										}
										else {//If turn is not over, enable all the pieces of that color
											enableColor();
										}
									}
								}
								else if(Math.abs(j - pushPullColumnJ) == 1) {//If the open square is on the second piece's side
									GridSquares temp = new GridSquares();
									temp.setAll(board[i][j]);
									board[i][j].setAll(board[pushPullRowI][pushPullColumnJ]);
									board[pushPullRowI][pushPullColumnJ].setAll(board[rowI][columnJ]);
									board[rowI][columnJ].setAll(temp);
									
									GridSquares[][] current = new GridSquares[board.length][board[0].length];
									setAllToSame(board, current);
									pastBoards.add(current);
									GridSquares[][] currentColor = new GridSquares[board.length][board[0].length];
									setColorToSame(board, currentColor);
									if(turn == YELLOW) {
										pastBoardsYellow.add(currentColor);
									}
									else {
										pastBoardsBlue.add(currentColor);
									}
									//The following lines check if the position has been repeated twice before
									if(checkIfTwiceBefore()) {//If it has been repeated twice before, it undos the move 
										temp.setAll(board[i][j]);
										board[i][j].setAll(board[rowI][columnJ]);
										board[rowI][columnJ].setAll(board[pushPullRowI][pushPullColumnJ]);
										board[pushPullRowI][pushPullColumnJ].setAll(temp);
										undoLastMove();
										setGrid();
										JOptionPane.showMessageDialog(frame, "This is not a valid move because this position has been created by your pieces twice before");
									}
									else {//If the position has not been repeated twice before, continue with the turn
										move += 2;
										String movesDisplay = "Moves: ";
										int movesLeft = 4-move;
										movesDisplay += movesLeft;
										moves.setText(movesDisplay);
										state = NOCLICK;
										if(move == 4) {//If turn is over, switch side
											switchSide();
										}
										else {//If turn is not over, enable all the pieces of that color
											enableColor();
										}
									}
								}
							}
						}
					}
					setInitialGrid();
				}
			}	
		}
		if(e.getSource().equals(endTurn) && !(move == 0)) {//If the endturn button is pressed and moves have been made, switch sides
			switchSide();
		}
		else if(e.getSource().equals(endTurn) && move == 0) {//If the endturn button is pressed and no moves have been made, make a popup message
			JOptionPane.showMessageDialog(frame, "Must make atleast one move before ending your turn");
		}
		else if (e.getSource().equals(startGame) && checkIfStart()){
			//If button is start and all pieces are on the board, it takes out the bottom row, the left and right grids and starts the game
			gameStarted = true;
			frame.setSize(400,400);
			frame.remove(south);
			frame.remove(east);
			frame.remove(west);
			frame.add(northAfterStart, BorderLayout.NORTH);
			state = NOCLICK;
			turn = BLUE;
			oppTurn = YELLOW;
			northAfterStart.add(openRules);
			
			//The following lines add the initial setup to the list of past boards
			GridSquares[][] current = new GridSquares[board.length][board[0].length];
			setAllToSame(board, current);
			pastBoards.add(current);
			GridSquares[][] currentColor = new GridSquares[board.length][board[0].length];
			setColorToSame(board, currentColor);
			pastBoardsBlue.add(currentColor);
			
			turn = YELLOW;
			oppTurn = BLUE;
			GridSquares[][] otherColor = new GridSquares[board.length][board[0].length];
			setColorToSame(board, otherColor);
			pastBoardsYellow.add(otherColor);
			enableColor();
			
		}
		else if(e.getSource().equals(openRules)) {//If the button is help, it opens the rules frame
			rulesFrame.setVisible(true);
		}
		else if(e.getSource().equals(closeRules)) {//If the button is close, it closes the rules frame
			rulesFrame.setVisible(false);
		}
		for (int i = 0; i < yellowWest.length; i++) {
			for(int j = 0; j < yellowWest[i].length; j++) {
				if (e.getSource().equals(yellowWest[i][j].getButton())){
					if(getFirstClick().getButton().getBackground().equals(Color.BLUE)){
						getFirstClick().getButton().setBackground(Color.CYAN);
					}
					else if(getFirstClick().getButton().getBackground().equals(Color.ORANGE)){
						getFirstClick().getButton().setBackground(Color.YELLOW);
					}
					part = 0;
					rowI = i;
					columnJ = j;
					if(yellowWest[i][j].getButton().getBackground().equals(Color.yellow)) {
						getFirstClick().getButton().setBackground(Color.ORANGE);
						state = FIRSTCLICK;
						enableColorStart(YELLOW);
					}
				}
				else if (e.getSource().equals(blueEast[i][j].getButton())){
					if(getFirstClick().getButton().getBackground().equals(Color.BLUE)){
						getFirstClick().getButton().setBackground(Color.CYAN);
					}
					else if(getFirstClick().getButton().getBackground().equals(Color.ORANGE)){
						getFirstClick().getButton().setBackground(Color.YELLOW);
					}
					part = 2;
					rowI = i;
					columnJ = j;
					if(blueEast[i][j].getButton().getBackground().equals(Color.CYAN)) {
						getFirstClick().getButton().setBackground(Color.BLUE);
						state = FIRSTCLICK;
						enableColorStart(BLUE);
					}
				}
			}
		}
		setGrid();
		checkWin();
	}
}
