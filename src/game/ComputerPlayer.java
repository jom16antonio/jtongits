package game;

public class ComputerPlayer extends Player {
	/*
	 * TODO
	 * Improve handling of valid sets by storing "tago" sets into an
	 * internal Sets container. Continuously check for valid sets on
	 * each turn. Store valid sets into the Sets container. If already
	 * revealed a set or performed a chow DO NOT reveal any more sets.
	 * This is a good strategy for the computer. There should be a way
	 * to mark the valid sets that the computer stored so that the human
	 * player will be able to notice them at the end of the game when
	 * the players' hands are revealed for scoring in case nobody won by
	 * tong-its.
	 */
	
	public Sets sets = new Sets(this);
    
	public ComputerPlayer(String name, Game game) {
        super(name, game);
		sets.orient(Hand.HORIZONTAL);
	}

	// override Player's activate method
	public void activate() {
        active = true;
        done = false;
        hand.unmark();
        hand.deselect();
	}
	
    public void playTurn(Hand dumpPile, Hand newCardPile, Sets sets, Sets[] allSets) {
        sleep();
        // perform either chow, call, or bunot
        /*if (chowFromSet(dumpPile, sets)) {
            sleep();
            return;
        }*/
        boolean canChow = prepareToChow(dumpPile) && !didChow && !didGetNewCard;
        if (canChow) {
            selectMarkedCards();
            Hand selectedCards = hand.removeSelectedCards();
            selectedCards.faceUp();
            selectedCards.mark();
            hand.addHand(selectedCards);
            selectMarkedCards();
            chow(dumpPile, sets);
            sleep();
            return;
        }
        else if (canCall) {
            call();
            sleep();
            return;
        }
        else if (bunot(newCardPile)){
        	hand.faceDown();
            sleep();
            return;
        }

        // reveal valid sets only once per turn
        if (prepareToReveal()) {
            selectMarkedCards();
            reveal(sets);
            if (sets.hands.size() > 0) {
                Hand revealedSet = (Hand) sets.hands.lastElement();
                revealedSet.faceUp();
            }

            /*if (!didReveal) {
                reveal(sets);
                if (sets.hands.size() > 0) {
                    Hand revealedSet = (Hand) sets.hands.lastElement();
                    revealedSet.faceUp();
                }
            } else {
                if (reveal(this.sets)) {
                    Debug.out(name + ": performed tago");
                }
            }*/
            sleep();
            return;
        }
        
        // winner by tong-its
        if (checkWin(sets)) return;
        
        // perform sapaw on own sets
        /*Sets[] mySets = {this.sets};
        if (autoSapaw(mySets)) {
            Debug.out(name + ": performed sapaw on tago sets");
            sleep();
            return;
        }*/
        
        // winner by tong-its
        if (checkWin(sets)) return;
        
        // perform sapaw while it can
        if (autoSapaw(allSets)) {
            sleep();
            return;
        }
        
        // winner by tong-its
        if (checkWin(sets)) return;
        
        // dump a card and end the turn
        markHighestRank();
        selectMarkedCards();
        if (dump(dumpPile) == false) {
            return;
        } else {
        	Card dumpedCard = (Card) dumpPile.cards.lastElement();
        	dumpedCard.faceUp();
            // winner by tong-its
            if (checkWin(sets)) return;

            // refresh the sets' position
            Card topCard = (Card) hand.cards.lastElement();
            this.sets.move(topCard.x + Config.X_OFFSET, hand.y);
            sleep();
        }
    }
    
    private void sleep() {
//        int time = 500;
    	int time = 0;
//        Debug.out(name + ": sleeping for " + time + " milliseconds");
        try {
            Thread.sleep(time);
        } catch (java.lang.InterruptedException e) {}
    }
    
    private boolean checkWin(Sets sets) {
        // winner by tong-its
        if (hand.getSize() == 0) {
//            revealAllSets(sets);
            done = true;
//            sleep();
            return true;
        }
        return false;
    }

    private boolean prepareToChow(Hand dumpPile) {
        hand.unmark();
        hand.deselect();
        
        if (dumpPile.getSize() == 0) return false;
        
        Card card = (Card) dumpPile.cards.lastElement();
        int rank = card.rank;
        int suit = card.suit;
        
        int a = hand.findCard(rank + 1, suit);
        int b = hand.findCard(rank + 2, suit);
        int c = hand.findCard(rank - 1, suit);
        int d = hand.findCard(rank - 2, suit);
        
        // cards with same rank
        int[] e = new int[3];
        // We should start at -1 instead of 0. A bug that could have gone unnoticed.
        e[0] = hand.findCardWithRank(-1, rank);
        if (e[0] != -1) {
            e[1] = hand.findCardWithRank(e[0], rank);
            if (e[1] != -1) {
                e[2] = hand.findCardWithRank(e[1], rank);
            }
        }
        // TODO: Improve valid set computation. Consider valid sets with
        //       more than 3 cards even 6 card sets.
        
        if(a != -1 && b != -1) {
            markCard(a);
            markCard(b);
            return true;
        }

        if(a != -1 && c != -1) {
            markCard(a);
            markCard(c);
            return true;
        }

        if(c != -1 && d != -1) {
            markCard(c);
            markCard(d);
            return true;
        }

        if(e[0] != -1 && e[1] != -1) {
            markCard(e[0]);
            markCard(e[1]);
            if(e[2] != -1) markCard(e[2]);
            return true;
        }
        
        // failed all the tests
        return false;
    }
    
    private boolean chowFromSet(Hand dumpPile, Sets sets) {
    	if (didChow || didGetNewCard) return false;
        if (dumpPile.getSize() == 0) return false;
        if (this.sets.hands.size() == 0) return false;
        
        Card dumpCard = (Card) dumpPile.cards.lastElement();
        
        for (int i = 0; i < this.sets.hands.size(); i++) {
            Hand chowHand = (Hand) this.sets.hands.elementAt(i);
            chowHand.addCard(new Card(dumpCard.rank, dumpCard.suit));
            if (Sets.validateSet(chowHand)) {
            	chowHand.removeTopCard();
            	chowHand.addCard(dumpCard);
            	dumpPile.removeTopCard();
                chowHand.sort();
                chowHand.faceUp();
                sets.addSet(chowHand);
                this.sets.hands.removeElementAt(i);
                this.sets.move();
                didReveal = didChow = true;
                Debug.out(name + ": performed chow from set");
                theGame.writeToolbarMsg(name + " chowed " + r[dumpCard.rank] + " of " + s[dumpCard.suit]);
                return true;
            } else {
                chowHand.removeTopCard();
            }
        }
        return false;
    }
    
    private void revealAllSets(Sets sets) {
        for (int i = 0; i < this.sets.hands.size(); i++) {
            Debug.out(name + ": performed reveal of all tago sets");
            Hand set = (Hand) this.sets.hands.elementAt(i);
            set.faceUp();
            sets.addSet(set);
            sets.move();
            this.sets.hands.removeElementAt(i);
            didReveal = true;
        }
    }
    
//    public boolean prepareToReveal() {
//        hand.unmark();
//        hand.deselect();
//        
//        for (int i = 0; i < hand.getSize(); i++) {
//            Card card = (Card) hand.cards.elementAt(i);
//            int rank = card.rank;
//            int suit = card.suit;
//            
//            int a = hand.findCard(rank + 1, suit);
//            int b = hand.findCard(rank + 2, suit);
//            int c = hand.findCard(rank - 1, suit);
//            int d = hand.findCard(rank - 2, suit);
//            
//            // cards with same rank, try to find all 4 cards
//            int[] e = new int[4];
//            e[0] = hand.findCardWithRank(-1, rank);
//            if (e[0] != -1) {
//                e[1] = hand.findCardWithRank(e[0], rank);
//                if (e[1] != -1) {
//                    e[2] = hand.findCardWithRank(e[1], rank);
//                    if (e[2] != 1) {
//                        e[3] = hand.findCardWithRank(e[2], rank);
//                    }
//                }
//            }
//            
//            if(a != -1 && b != -1) {
//                markCard(i);
//                markCard(a);
//                markCard(b);
//                return true;
//            }
//
//            if(a != -1 && c != -1) {
//                markCard(i);
//                markCard(a);
//                markCard(c);
//                return true;
//            }
//
//            if(c != -1 && d != -1) {
//                markCard(i);
//                markCard(c);
//                markCard(d);
//                return true;
//            }
//
//            if(e[0] != -1 && e[1] != -1 && e[2] != -1) {
//                markCard(e[0]);
//                markCard(e[1]);
//                markCard(e[2]);
//                if(e[3] != -1) markCard(e[3]);
//                return true;
//            }
//        }
//        
//        return false;
//    }
    
    private boolean autoSapaw(Sets[] sets) {
        if (!didGetNewCard && !didChow) return false;
        if (sets.length == 0) return false;

        hand.unmark();
        hand.deselect();
        
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            card.mark();
            selectMarkedCards();
            
            // loop through all the available sets
            for (int j = 0; j < sets.length; j++) {
                for (int k = 0; k < sets[j].hands.size(); k++) {
                    Hand hand = (Hand) sets[j].hands.elementAt(k);
                    if (sapaw(hand) == false) continue;
                    else {
                    	hand.faceUp();
                       	sets[j].move();
                    	return true;
                    }
                }
            }
            card.unmark();
            card.deselect();
        }
        
        // not able to sapaw anymore
        return false;
    }
    
    private void markHighestRank() {
        hand.unmark();
        hand.deselect();
        hand.sort();
        Card card = (Card) hand.cards.lastElement();
        card.mark();
    }
}