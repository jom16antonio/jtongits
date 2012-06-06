package game;

import java.util.Vector;

public class Player {
    /*
     * TODO
     * There should be a way for the player to be able to perform "tago" just
     * like the computer. The player can select the tago sets and can perform
     * chow if possible. The player should also be able to perform sapaw on his
     * own tago sets. Tago sets are not counted at the end of a non tong-its-won
     * game. The only sensible way of creating a tago is when the player has
     * at least 1 (one) revealed valid set either through a reveal move or a chow.
     */
    public Hand hand = new Hand();
    public boolean active = false;
    public boolean canCall = false;
    public boolean mano = false;
    public boolean done = false;
    public String name = "";
    
    // moves performed
    protected boolean didGetNewCard = false;
    protected boolean didChow = false;
    protected boolean didReveal = false;
    
    protected Game theGame;
    
    protected String[] r = {"none", "ace", "two", "three", "four", "five"
    		            , "six", "seven", "eight", "nine", "ten", "jack"
    		            , "queen", "king"};
    protected String[] s = {"spades", "hearts", "clubs", "diamonds"};
    
    public Player(String name, Game game) {
    	theGame = game;
        this.name = name;
    }
    
    public void markNext(boolean goRight) {
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            if (card.isMarked()) {
                // TODO: In the future, create a Card markedCard member variable
                //       that will hold the marked card to avoid looping every
                //       time we want to mark the next card. Could also be an
                //       integer index value.
                card.unmark();
                if (goRight) {
                    // check if we're at the rightmost card
                    if (i + 1 == hand.getSize()) {
                        // mark the first card
                        card = (Card) hand.cards.firstElement();
                        card.mark();
                        return;
                    } else {
                        // just mark the next card
                        card = (Card) hand.cards.elementAt(i+1);
                        card.mark();
                        return;
                    }
                } else {
                    // check if we're at the leftmost card
                    if (i  == 0) {
                        // mark the last card
                        card = (Card) hand.cards.lastElement();
                        card.mark();
                        return;
                    } else {
                        // just mark the previous card
                        card = (Card) hand.cards.elementAt(i-1);
                        card.mark();
                        return;
                    }
                }
            }
        }
    }
    
    public void markNextSet(Sets[] sets, boolean goRight) {
        Vector allSets = new Vector();
        
        for (int i = 0; i < sets.length; i++) {
            for (int j = 0; j < sets[i].hands.size(); j++) {
                allSets.addElement(sets[i].hands.elementAt(j));
            }
        }
        
        for (int i = 0; i < allSets.size(); i++) {
            Hand hand = (Hand) allSets.elementAt(i);
            if (hand.marked) {
                hand.unmark();
                if (goRight) {
                    if (i + 1 == allSets.size()) {
                        // mark the first set
                        hand = (Hand) allSets.firstElement();
                        hand.mark();
                        return;
                    } else {
                        hand = (Hand) allSets.elementAt(i+1);
                        hand.mark();
                        return;
                    }
                } else {    // go left
                    if (i == 0) {
                        // mark the last
                        hand = (Hand) allSets.lastElement();
                        hand.mark();
                        return;
                    } else {
                        hand = (Hand) allSets.elementAt(i-1);
                        hand.mark();
                        return;
                    }
                }
            }
        }
    }
    
    public void selectMarkedCards() {
        // select the cards that are marked
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            if (card.isMarked()) {
                // nudge the cards to indicate being selected
                if (hand.orientation == Hand.HORIZONTAL) {
                    if (!card.isSelected()) card.nudge(Card.UP);
                    else card.nudge(Card.DOWN);
                } else if (hand.orientation == Hand.VERTICAL) {
                    if (!card.isSelected()) card.nudge(Card.RIGHT);
                    else card.nudge(Card.LEFT);
                } else {    // Hand.VERTICAL_VARIANT
                    if (!card.isSelected()) card.nudge(Card.LEFT);
                    else card.nudge(Card.RIGHT);
                }
                card.toggle();
            }
        }
    }
    
    public void activate() {
        active = true;
        done = false;
        hand.unmark();
        hand.deselect();
        Card card = (Card) hand.cards.firstElement();
        card.mark();
    }
    
    public void deactivate() {
        active = false;
        hand.deselect();
        hand.unmark();
        hand.move();
        
        // reset the moves
        didGetNewCard = didChow = /*didReveal =*/ false;
        
        // can call on next turn if no one performed sapaw on this player's hand
        // also check if this player has valid sets
//        canCall = true;
    }
    
    public void makeMano() {
//        activate();
        
        // mano player automatically received the new card from the pile
        mano = didGetNewCard = true;
    }
    
    public void orient(int orientation) {
        hand.orient(orientation);
    }
    
    public boolean bunot(Hand newCardPile) {
        if (didGetNewCard || didChow) return false;
        
        // extract the card from the new card pile
        Card card = newCardPile.removeTopCard();
        card.faceUp();
        
        // then add to the player's hand
        hand.addCard(card);
        didGetNewCard = true;
        
        Debug.out(name + ": performed bunot, got " + r[card.rank] + " of " + s[card.suit]);
        theGame.writeToolbarMsg(name + " got " + r[card.rank] + " of " + s[card.suit]);
        return true;
    }
    
    public boolean chow(Hand dumpPile, Sets sets) {
        if (didGetNewCard || didChow) return false;
        
        boolean success = false;
        Hand selectedCards = hand.getSelectedCards();
        Card dumpedCard = (Card) dumpPile.cards.lastElement();
        selectedCards.addCard(new Card(dumpedCard.rank, dumpedCard.suit));
        
        if (Sets.validateSet(selectedCards)) {
        	dumpedCard = dumpPile.removeTopCard();
        	selectedCards = hand.removeSelectedCards();
        	selectedCards.addCard(dumpedCard);
        	selectedCards.deselect();
        	selectedCards.unmark();
        	selectedCards.sort();
            sets.addSet(selectedCards);
            
            // refresh the player's hand by resetting the marked cards
            hand.move();
            activate();
            
            didReveal = success = true;
            Debug.out(name + ": performed chow on "
                    + r[dumpedCard.rank] + " of " + s[dumpedCard.suit]);
            theGame.writeToolbarMsg(name + " chowed " + r[dumpedCard.rank] + " of " + s[dumpedCard.suit]);
        } else success = false;
        didChow = success;
        return success;
    }
    
    public boolean reveal(Sets sets) {
        if (!didGetNewCard && !didChow) return false;
        
        boolean success = false;
        Hand selectedCards = hand.getSelectedCards();
        
        if (Sets.validateSet(selectedCards)) {
        	selectedCards = hand.removeSelectedCards();
        	selectedCards.deselect();
        	selectedCards.unmark();
        	selectedCards.sort();
            sets.addSet(selectedCards);
            hand.move();
            activate();
            didReveal = success = true;
            Debug.out(name + ": performed reveal");
            theGame.writeToolbarMsg(name + " revealed a set.");
            
        } else success = false;
        return success;
    }
    
    public boolean tago() {
        // TODO: Implement tago just like what the computer does.
        boolean success = false;
        return success;
    }
    
    public boolean sapaw(Hand set) {
        if (!didGetNewCard && !didChow) return false;
        if (hand.getNumSelectedCards() < 1) return false;
        
        boolean success = false;
        Hand selectedCards = hand.getSelectedCards();

        for (int i = 0; i < set.getSize(); i++) {
        	Card card = (Card) set.cards.elementAt(i);
        	selectedCards.addCard(new Card(card.rank, card.suit));
        }

        if (Sets.validateSet(selectedCards)) {
        	selectedCards = hand.removeSelectedCards();
        	selectedCards.deselect();
        	selectedCards.unmark();
            set.addHand(selectedCards);
            set.sort();
            set.move();
            
            hand.move();
            activate();
            success = true;
            Debug.out(name + ": performed sapaw");
            theGame.writeToolbarMsg(name + " performed sapaw.");
        } else success = false;
        
        return success;
    }
    
    public boolean dump(Hand dumpPile) {
        if (!didGetNewCard && !didChow) {
            Debug.out(name + ": failed to dump");
            return false;
        }
        int numCards = hand.getNumSelectedCards();
        if (numCards < 1 || numCards > 1) {
            Debug.out(name + ": failed to dump");
            return false;
        }

        Hand dumpHand = hand.removeSelectedCards();
        Card dumpCard = (Card) dumpHand.cards.firstElement();
        dumpCard.unmark();
        dumpCard.deselect();
        dumpPile.addCard(dumpCard);
        
        Debug.out(name + ": performed dump with " + r[dumpCard.rank] + " of " + s[dumpCard.suit]);
        theGame.writeToolbarMsg(name + " dumped " + r[dumpCard.rank] + " of " + s[dumpCard.suit]);
        
        done = true;
        return done;
    }
    
    public int countRankTotal() {
        int total = 0;
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            total += card.rank;
        }
        return total;
    }
    
    public boolean call() {
        Debug.out(name + ": performed call");
        return false;
    }
    
    public boolean prepareToReveal() {
        hand.unmark();
        hand.deselect();
        
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            int rank = card.rank;
            int suit = card.suit;
            
            int a = hand.findCard(rank + 1, suit);
            int b = hand.findCard(rank + 2, suit);
            int c = hand.findCard(rank - 1, suit);
            int d = hand.findCard(rank - 2, suit);
            
            // cards with same rank, try to find all 4 cards
            int[] e = new int[4];
            e[0] = hand.findCardWithRank(-1, rank);
            if (e[0] != -1) {
                e[1] = hand.findCardWithRank(e[0], rank);
                if (e[1] != -1) {
                    e[2] = hand.findCardWithRank(e[1], rank);
                    if (e[2] != 1) {
                        e[3] = hand.findCardWithRank(e[2], rank);
                    }
                }
            }
            
            if(a != -1 && b != -1) {
                markCard(i);
                markCard(a);
                markCard(b);
                return true;
            }

            if(a != -1 && c != -1) {
                markCard(i);
                markCard(a);
                markCard(c);
                return true;
            }

            if(c != -1 && d != -1) {
                markCard(i);
                markCard(c);
                markCard(d);
                return true;
            }

            if(e[0] != -1 && e[1] != -1 && e[2] != -1) {
                markCard(e[0]);
                markCard(e[1]);
                markCard(e[2]);
                if(e[3] != -1) markCard(e[3]);
                return true;
            }
        }
        
        return false;
    }
    
    protected void markCard(int index) {
        Card card = (Card) hand.cards.elementAt(index);
        card.mark();
    }
}
