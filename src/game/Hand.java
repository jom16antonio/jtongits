package game;

import java.util.Vector;
import java.util.Random;

public class Hand {
    // the orientations of a hand
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int PILE = 2;   // stacked 3D look
    public static final int VERTICAL_VARIANT = 3;   // rightmost column

    public int orientation = Hand.HORIZONTAL;
    
    public int x = 0;
    public int y = 0;
    public int id = 0;
    
    public Vector cards = new Vector();
    
    /*
     * We need to differentiate between a selected card and a marked card.
     * Cards can be marked but not selected and vice versa although cards
     * are automatically marked when selected. A marked card is drawn with
     * the negative card images. The player can select card (and thus "marking"
     * the card) and then move on to (or select) the next/previous card.
     */
    public boolean selected = false;
    public boolean marked = false;     // whether card is highlighted or not

    public Hand() {
    }

    public void createStandardDeck() {
        // create a standard 52-card deck
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = Card.ACE; rank < Card.KING + 1; rank++) {
                cards.addElement(new Card(rank, suit));
            }
        }
    }
    
    public int getWidth() {
        int size = this.cards.size();
        if (size == 0) {
            return 0;
        } else if (size == 1) {
            return Config.CARD_WIDTH;
        } else {
            return Config.CARD_WIDTH + (size - 1) * Config.X_OFFSET;
        }
    }
    
    public int getHeight() {
        int size = this.cards.size();
        if (size == 0) {
            return 0;
        } else if (this.orientation == Hand.HORIZONTAL) {
            return Config.CARD_HEIGHT;
        } else if (size == 1) {
            return Config.CARD_HEIGHT;
        } else {
            return Config.CARD_HEIGHT + (size - 1) * Config.Y_OFFSET;
        }
    }
    
    public void orient(int orientation) {
        this.orientation = orientation;
    }
    
    public void arrangeIn3d() {
        int cardX = x;
        int cardY = y;
        int pos = 0;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.x = cardX;
            card.y = cardY;
            if ((i % 6) == 0) pos++;
            cardX = pos + x;
            cardY = pos + y;
        }
    }
    
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
        deselect();
        
        if (orientation == Hand.HORIZONTAL) {
            for (int i = 0; i < getSize(); i++) {
                Card card = (Card) cards.elementAt(i);
                card.x = x;
                card.y = y;
                x += Config.X_OFFSET;
            }
        } else if (orientation == Hand.PILE) {
                arrangeIn3d();
        } else { // VERTICAL
            for (int i = 0; i < getSize(); i++) {
                Card card = (Card) cards.elementAt(i);
                card.x = x;
                card.y = y;
                y += Config.Y_OFFSET;
            }
        }
    }
    
    public void move() {
        // "refresh" the cards' positions using current owned coordinates
        move(x, y);
    }
    
    public int getSize() {
        return this.cards.size();
    }
    
    public void clear() {
        this.cards.removeAllElements();
    }
    
    public void addCard(Card card) {
        this.cards.addElement(card);
        move();
    }
    
    public void addHand(Hand hand) {
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            addCard(card);
        }
    }
    
    public Card removeCard(int index) {
        if (cards.isEmpty()) return null; 
        if (index > getSize() - 1) index = getSize() - 1;
        if (index < 0) index = 0;
        
        Card card = (Card)cards.elementAt(index);
        cards.removeElementAt(index);
        move();
        return card;
    }
    
    public Card removeTopCard() {
        if (cards.isEmpty()) return null;
        int index = getSize() - 1;
        
        Card card = (Card)cards.elementAt(index);
        cards.removeElementAt(index);
        return card;
    }
    
    public Hand removeSelectedCards() {
        Hand selectedCards = new Hand();
        for (int i = getSize()-1; i > -1; i--) {
            Card card = (Card) cards.elementAt(i);
            if (card.isSelected()) {
                selectedCards.addCard(card);
                cards.removeElementAt(i);                
            }
        }
        return selectedCards; 
    }
    
    public Hand getSelectedCards() {
        Hand selectedCards = new Hand();
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            if (card.isSelected()) selectedCards.addCard(new Card(card.rank, card.suit));
        }
        return selectedCards;
    }
    
    public int getNumSelectedCards() {
        int selectedCardCounter = 0;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            if (card.isSelected()) selectedCardCounter++;
        }
        return selectedCardCounter;
    }
    
    public int findCard(int rank, int suit) {
        if (cards.isEmpty()) return -1;
        
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            if (card.rank == rank && card.suit == suit) {
                return i;
            }
        }
        
        // failed to find the card
        return -1;
    }
    
    public int findCardWithRank(int start, int rank) {
        if (cards.isEmpty()) return -1;
        if (start < -1) return -1;
        if (start > getSize() - 2) return -1;
        
        /*
         * Look for a card with the specified rank. The function starts
         * looking for the card at index start+1. start+1 is usually the
         * index of the previous card with the desired rank.
         */
        for (int i = start + 1; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            if (card.rank == rank) {
                return i;
            }
        }
        
        // failed to find the card
        return -1;
    }
    
    public void faceUp() {
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.faceUp();
        }
    }
    
    public void faceDown() {
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.faceDown();
        }        
    }
    
    public void select() {
        selected = true;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.select();
        }
    }
    
    public void deselect() {
        selected = false;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.deselect();
        }
    }
    
    public void toggle() {
        if (selected) deselect();
        else select();
    }
    
    public void mark() {
        marked = true;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.mark();
        }
    }
    
    public void unmark() {
        marked = false;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.unmark();
        }
    }
    
    public static int getRand(int min, int max) {
        Random randomizer = new Random();
        int r = Math.abs(randomizer.nextInt());
        return (r % (max - min)) + min;
    }
    
    private void swap(Vector a, int i, int j) {
        Object temp = a.elementAt(i);
        a.setElementAt(a.elementAt(j), i);
        a.setElementAt(temp, j);        
    }
    
    public void shuffle() {
        for (int i = cards.size(); i > 1; i--) {
            swap(cards, i-1, getRand(0, i));
        }
    }
    
    public void sort() {
        // algorithm taken from http://en.wikipedia.org/wiki/Insertion_sort
        int indPost;
        int indAnt;
        Card nextElement = null;
        
        for (indPost = 1; indPost < cards.size(); indPost++) {
            nextElement = (Card) cards.elementAt(indPost);
            // compare the ranks
            for (indAnt = indPost  - 1; indAnt  >= 0 && ((Card)cards.elementAt(indAnt)).rank > nextElement.rank; --indAnt) {
                cards.setElementAt(cards.elementAt(indAnt), indAnt+1);
            }
            cards.setElementAt(nextElement, indAnt+1);
        }
        
        // refresh the cards' positions
        move();
    }
    
    public void show() {
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.show();
        }
    }
    
    public void hide() {
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            card.hide();
        }
    }
    
    public int getRankTotal() {
        int total = 0;
        for (int i = 0; i < getSize(); i++) {
            Card card = (Card) cards.elementAt(i);
            if (card.rank > 10) total += 10;
            else total+= card.rank;
        }
        return total;
    }
}

