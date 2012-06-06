package game;

import java.util.Vector;

public class Sets {
    public Vector hands = new Vector();
    public int x = 0;
    public int y = 0;
    public Player owner = null;
    
    private int orientation = Hand.HORIZONTAL;
    
    public Sets(Player owner) {
        this.owner = owner;
    }
    
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
        
        if (orientation == Hand.HORIZONTAL) {
            for (int i = 0; i < hands.size(); i++) {
                Hand hand = (Hand) hands.elementAt(i);
                hand.move(x, y);
                int width = hand.getWidth() - Config.CARD_WIDTH;
                x += width;
                x += Config.SET_X_OFFSET;
            }
        } else {
            for (int i = 0; i < hands.size(); i++) {
                Hand hand = (Hand) hands.elementAt(i);
                hand.move(x, y);
                int height = hand.getHeight() - Config.CARD_HEIGHT;
                y += height;
                y += Config.SET_Y_OFFSET;
            }
        }
    }
    
    public void move() {
        move(x, y);
    }
    
    public void addSet(Hand hand) {
        hand.id = Hand.getRand(999, 9999);
        hands.addElement(hand);
        orient(orientation);
        move();
    }
    
    public void clear() {
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = (Hand) hands.elementAt(i);
            hand.clear();
        }
        hands.removeAllElements();
    }
    
    public static boolean validateSet(Hand hand) {
        hand.sort();
        final int size = hand.getSize();
        
        // must be at least 3 cards
        if (size < 3) return false;
        
        // check for same ranks, note that having different ranks doesn't mean invalid (e.g. straight flush)
        int sameRankCounter = 0;
        for (int i = 0; i < size; i++) {
            Card card = (Card) hand.cards.elementAt(i);
            Card lastCard = (Card) hand.cards.lastElement();
            if (card.rank != lastCard.rank) break;
            sameRankCounter++;
        }
        
        // compared all the cards, all have same rank, it is a valid set
        if (sameRankCounter == size) return true;
        
        // check for same suits, must be same suit in order to be a valid set
        for (int i = 0; i < size; i++) {
            Card card = (Card) hand.cards.elementAt(i);
            Card lastCard = (Card) hand.cards.lastElement();
            if (card.suit != lastCard.suit) return false;
        }
        
        // yes they have same suits, continue processing
        
        // check for straight flush
        for (int i = 0; i < size - 1; i++) {
            Card card = (Card) hand.cards.elementAt(i);
            Card nextCard = (Card) hand.cards.elementAt(i+1);
            int difference = nextCard.rank - card.rank;
            if (difference != 1) return false;
        }
        
        // survived all the checks, must be a valid set
        return true;
    }
    
    public void orient(int orientation) {
        this.orientation = orientation;
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = (Hand) hands.elementAt(i);
            hand.orient(orientation);
        }
    }
    
    public void unmark() {
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = (Hand) hands.elementAt(i);
            hand.unmark();
        }
    }
}
