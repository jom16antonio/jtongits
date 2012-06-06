package game;

import game.Config;

public class Card {
    public static final int ACE = 1;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;

    public static final int SPADES = 0;
    public static final int HEARTS = 1;
    public static final int CLUBS = 2;
    public static final int DIAMONDS = 3;
    
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    
	public int rank = ACE;	// ace to king, or 1 to 13
	public int suit = SPADES;	// spades, hearts, clubs, diamonds, or 0 to 3
	public int x = 0;
	public int y = 0;
    
	private boolean selected = false;
	private boolean marked = false;
	private boolean facedDown = false;
    private boolean visible = true;
	
	public Card(int rank, int suit) {
        if (rank > KING) rank = KING;
        else if (rank < ACE) rank = ACE;
        if (suit > DIAMONDS) suit = DIAMONDS;
        else if (suit < SPADES) suit = SPADES;
        
		this.rank = rank;
		this.suit = suit;
	}
	
	public void select() {
		selected = true;
	}
	
	public void deselect() {
		selected = false;
	}
    
    public void toggle() {
        selected = !selected;
    }
	
	public void mark() {
		marked = true;
	}
	
	public void unmark() {
		marked = false;
	}
	
	public void faceDown() {
		facedDown = true;
	}
	
	public void faceUp() {
		facedDown = false;
	}
    
    public void show() {
        visible = true;
    }
    
    public void hide() {
        visible = false;
    }
	
	public void flip() {
		facedDown = !facedDown;
	}
	
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void nudge(int direction) {
		if (direction == Card.UP) {
			// move up
			move(x, y - Config.NUDGE_OFFSET);
		} else if (direction == Card.DOWN) {
			// move down
			move(x, y + Config.NUDGE_OFFSET);
		} else if (direction == Card.LEFT) {
			// move left
			move(x - Config.NUDGE_OFFSET, y);
		} else if (direction == Card.RIGHT) {
			// move right
			move(x + Config.NUDGE_OFFSET, y);
		}
	}
    
    public boolean isSelected() {
        return selected;
    }
    
    public boolean isMarked() {
        return this.marked;
    }
    
    public boolean isFacedDown() {
        return this.facedDown;
    }
}
