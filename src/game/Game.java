package game;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class Game {
    public static final int HUMAN = 0;
    public static final int COMPUTER_A = 1;
    public static final int COMPUTER_B = 2;
    public static final int NUM_PLAYERS = 3;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    public static final int SELECT_HAND = 0;
    public static final int SELECT_SET = 1;
    
    private Graphics doubleBufferGraphics = null;
    private int screenWidth = 0;
    private int screenHeight = 0;
    
    private CardGraphics cardGraphics = null;
    
    private Hand deck = new Hand(); // not rendered
    private Hand newCardPile = new Hand();
    private Hand dumpPile = new Hand();
    
    private Player[] players;
    private Sets[] sets;
    
    private int winner = Game.HUMAN;
    private int activePlayer = Game.HUMAN;
    
    private int selectionMode = Game.SELECT_HAND;
    
    // if the game's state changes set dirty to true
    private boolean dirty = false;
    private boolean gameHasEnded = false;
    
    private TongIts theMidlet;
    
    private int playerAreaHeight;
    
    public Game(Image doubleBuffer, TongIts midlet) {
    	Player[] p = { new Player("You", this)
                , new ComputerPlayer("Rey", this), new ComputerPlayer("Ian", this) };
    	players = p;
    	Sets[] s =  { new Sets(players[HUMAN])
                , new Sets(players[COMPUTER_A])
                , new Sets(players[COMPUTER_B])};
    	sets = s;
    	theMidlet = midlet;
        doubleBufferGraphics = doubleBuffer.getGraphics();
        doubleBufferGraphics.setClip(0, 0, screenWidth, screenHeight);
        cardGraphics = new CardGraphics(doubleBuffer);
        screenWidth = doubleBuffer.getWidth();
        screenHeight = doubleBuffer.getHeight();
        playerAreaHeight = (screenHeight - Config.TOOLBAR_HEIGHT) / 3;
        
        // create the permanent deck
        deck.createStandardDeck();

        // set the positions of all the hands
        initPositions();
    }
    
    public void newGame() {
    	selectionMode = Game.SELECT_HAND;
    	
    	// initialize the deck
    	deck.unmark();
    	deck.deselect();
    	deck.faceUp();
    	
        // empty all hands
        newCardPile.clear();
        dumpPile.clear();
        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            sets[i].clear();
            players[i].hand.clear();
            players[i].didReveal = players[i].didChow = players[i].didGetNewCard = false;
        }
        
        // clear the computer players' hidden sets
        ComputerPlayer computer = (ComputerPlayer) players[Game.COMPUTER_A];
        computer.sets.clear();
        computer = (ComputerPlayer) players[Game.COMPUTER_B];
        computer.sets.clear();
        
        // create the new card pile
        for (int i = 0; i < deck.getSize(); i++) {
            Card card = (Card) deck.cards.elementAt(i);
            newCardPile.addCard(card);
        }
        newCardPile.shuffle();
        newCardPile.move();
        
        // the winner in the previous round becomes the mano
        players[winner].makeMano();
        
        // deal the cards
        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            // deal extra card to the mano
            // note that there should ONLY be ONE mano
            if (players[i].mano) {
                Card card = newCardPile.removeTopCard();
                players[i].hand.addCard(card);
                players[i].mano = false;
            }
            
            // deal 12 cards each
            for (int j = 0; j < 12; j++) {
                Card card = newCardPile.removeTopCard();
                players[i].hand.addCard(card);
            }
        }
        
        // refresh the positions of the players' hands and sets
        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            sets[i].move();
            players[i].hand.move();
        }
        // set the hands that are faced down
        newCardPile.faceDown();
        players[Game.COMPUTER_A].hand.faceDown();
        players[Game.COMPUTER_B].hand.faceDown();
        players[Game.HUMAN].hand.sort();
        players[Game.COMPUTER_A].hand.sort();
        players[Game.COMPUTER_B].hand.sort();
        players[winner].activate();
        activePlayer = winner;
        dirty = true;
        gameHasEnded = false;
    }
    
    public void renderGame() {
        if (!dirty) return;
        
        // if dirty
        dirty = false;
            
        // draw the background
        doubleBufferGraphics.setClip(0, 0, screenWidth, screenHeight);
        doubleBufferGraphics.setClip(0, 0, screenWidth, screenHeight);
        doubleBufferGraphics.setColor(0, 128, 128);
        doubleBufferGraphics.fillRect(0, 0, screenWidth, playerAreaHeight*2);
        doubleBufferGraphics.setColor(0, 128, 0);
        doubleBufferGraphics.fillRect(0, playerAreaHeight*2, screenWidth, playerAreaHeight+Config.TOOLBAR_HEIGHT);
        doubleBufferGraphics.setColor(255, 255, 255);
        doubleBufferGraphics.fillRect(0, playerAreaHeight-1, screenWidth, 1);
        doubleBufferGraphics.fillRect(0, playerAreaHeight*2-1, screenWidth, 1);
        doubleBufferGraphics.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        doubleBufferGraphics.setColor(255, 255, 255);
        doubleBufferGraphics.drawString("("+players[Game.COMPUTER_B].name+")", screenWidth-1, 2, Graphics.RIGHT | Graphics.TOP);
        doubleBufferGraphics.drawString("("+players[Game.COMPUTER_A].name+")", screenWidth-1, playerAreaHeight+2, Graphics.RIGHT | Graphics.TOP);
        
        cardGraphics.drawHand(newCardPile);
        cardGraphics.drawHand(dumpPile);
        
        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            for (int j = 0; j < sets[i].hands.size(); j++) {
                Hand hand = (Hand) sets[i].hands.elementAt(j);
                cardGraphics.drawHand(hand);
            }
            cardGraphics.drawHand(players[i].hand);
        }

        
        // draw the computer players' hidden sets (tago)
        ComputerPlayer computer = (ComputerPlayer) players[Game.COMPUTER_A];
        cardGraphics.drawSets(computer.sets);
        computer = (ComputerPlayer) players[Game.COMPUTER_B];
        cardGraphics.drawSets(computer.sets);
        
//        doubleBufferGraphics.setClip(0, 0, screenWidth, screenHeight);
//        writeToolbarMsg("test");
        
        if (gameHasEnded) renderWinInfo();
    }
    
    public void writeToolbarMsg(String msg) {
//    	dirty = true;
//        doubleBufferGraphics.setColor(0x00666666);
//        doubleBufferGraphics.fillRect(0, screenHeight-Config.TOOLBAR_HEIGHT, screenWidth, Config.TOOLBAR_HEIGHT);
//    	doubleBufferGraphics.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
//    	doubleBufferGraphics.setColor(0x00ffffff);
//    	doubleBufferGraphics.drawString(msg, 1, (screenHeight-Config.TOOLBAR_HEIGHT) + 1, Graphics.LEFT | Graphics.TOP);
    }
    
    public void playGame() {
       	startComputer();
    }
    
    public void markNext(int direction) {
        if (gameHasEnded) return;
        if (players[Game.HUMAN].active) {
        	if (selectionMode == Game.SELECT_HAND) {
        		players[Game.HUMAN].markNext(direction == Game.RIGHT);
        	} else {
        		players[Game.HUMAN].markNextSet(sets, direction == Game.RIGHT);
        	}
            dirty = true;
        }
    }
    
    public void select() {
        if (gameHasEnded) return;
        if (players[Game.HUMAN].active) {
            players[Game.HUMAN].selectMarkedCards();
            dirty = true;
        }   
    }
    
    public void bunot() {
        if (gameHasEnded) return;
        if (players[Game.HUMAN].active) {
        	if (players[Game.HUMAN].didGetNewCard || players[Game.HUMAN].didChow) {
        		theMidlet.showAlert("You already got a new card or performed a chow.");
        		return;
        	}
            if (players[Game.HUMAN].bunot(newCardPile)) {
            	dirty = true;
            }
        }
    }
    
    public void reveal() {
        if (gameHasEnded) return;
        if (players[Game.HUMAN].active && selectionMode == Game.SELECT_HAND) {
            int numCards =  players[Game.HUMAN].hand.getNumSelectedCards();
            if (numCards < 3) {
                return;
            }
        	if (!players[Game.HUMAN].didGetNewCard && !players[Game.HUMAN].didChow) {
        		theMidlet.showAlert("You must get a new card first or perform a chow.");
        		return;
        	}
            if (players[Game.HUMAN].reveal(sets[Game.HUMAN])) {
            	dirty = true;
            } else {
            	theMidlet.showAlert("Not a valid set to reveal.");
            }
        }
    }
    
    public void chow() {
        if (gameHasEnded) return;
        if (players[Game.HUMAN].active) {
            if (players[Game.HUMAN].hand.getNumSelectedCards() == 0) return;
            if (players[Game.HUMAN].didGetNewCard || players[Game.HUMAN].didChow) {
            	theMidlet.showAlert("You already got a new card or performed a chow.");
            	return;
            }
            if (players[Game.HUMAN].chow(dumpPile, sets[Game.HUMAN])) {
            	dirty = true;
            } else {
            	theMidlet.showAlert("Wrong card for chow.");
            }
        }
    }
    
    public void dump() {
        if (gameHasEnded) return;
        if (players[Game.HUMAN].active && selectionMode == Game.SELECT_HAND) {
            int numCards =  players[Game.HUMAN].hand.getNumSelectedCards();
            if (numCards < 1 || numCards > 1) {
                return;
            }
            if (!players[Game.HUMAN].didGetNewCard && !players[Game.HUMAN].didChow) {
            	theMidlet.showAlert("You must get a new card first or perform a chow.");
            	return;
            }
            if (players[Game.HUMAN].dump(dumpPile)) {
                dirty = true;
                switchPlayer();
            }
        }
    }
    
    public void sapaw() {
        if (gameHasEnded) return;
    	if (!players[Game.HUMAN].active) return;
    	if (players[Game.HUMAN].hand.getNumSelectedCards() == 0) return;
    	if (!players[Game.HUMAN].didChow && !players[Game.HUMAN].didGetNewCard) return;
    	boolean hasSets = false;
    	for (int i = 0; i < Game.NUM_PLAYERS; i++) {
    		if (sets[i].hands.size() > 0) hasSets = true;
    	}
    	if (!hasSets) {
    		theMidlet.showAlert("There are no valid sets available.");
    		return;
    	}
    	
    	if (selectionMode == Game.SELECT_SET) {
    		selectionMode = Game.SELECT_HAND;
    		players[Game.HUMAN].hand.move();
    		players[Game.HUMAN].activate();
    		for (int i = 0; i < Game.NUM_PLAYERS; i++) {
        		sets[i].unmark();
        	}
    	} else if (selectionMode == Game.SELECT_HAND) {
    		theMidlet.showAlert("Select the set then press \"Sapaw on Set\" from the menu.");
    		selectionMode = Game.SELECT_SET;
    		players[Game.HUMAN].hand.unmark();
    		for (int i = 0; i < Game.NUM_PLAYERS; i++) {
    			if (sets[i].hands.size() > 0) {
    				Hand hand = (Hand) sets[i].hands.firstElement();
    				hand.mark();
    				break;
    			}
    		}
    	}
    	dirty = true;
    }
    
    public void doSapaw() {
        if (gameHasEnded) return;
    	if (selectionMode == Game.SELECT_HAND) return;
    	Vector allSets = new Vector();
    	for (int i = 0; i < sets.length; i++) {
    		for (int j = 0; j < sets[i].hands.size(); j++) {
    			allSets.addElement(sets[i].hands.elementAt(j));
    		}
    	}
    	for (int i = 0; i < allSets.size(); i++) {
    		Hand hand = (Hand) allSets.elementAt(i);
    		if (hand.marked) {
    			if (players[Game.HUMAN].sapaw(hand) == false) {
    				Debug.out("Sapaw failed :(");
    				theMidlet.showAlert("Wrong card for sapaw.");
    			} else {
    				Debug.out("Sapaw success! :)");
                    for (int j = 0; j < sets.length; j++) {
                    	sets[j].move();
                    }
    			}
    			break;
    		}
    	}    	
    	// cancel the sapaw attempt
    	endSapaw();
    	dirty = true;
    }
    
    private void endSapaw() {
    	if (selectionMode == Game.SELECT_SET) {
    		selectionMode = Game.SELECT_HAND;
    		players[Game.HUMAN].hand.move();
    		players[Game.HUMAN].activate();
    		for (int i = 0; i < Game.NUM_PLAYERS; i++) {
        		sets[i].unmark();
        	}
    	}
    }
    
    private void startComputer() {
        if (gameHasEnded) return;
    	if (activePlayer == Game.HUMAN) return;
//    	Debug.out("Computer " + activePlayer);
    	((ComputerPlayer)players[activePlayer]).playTurn(dumpPile, newCardPile, sets[activePlayer], sets);
    	if (players[activePlayer].done) switchPlayer();
    	dirty = true;
    }

    private void switchPlayer() {
        // determine if there is a winner
        if (testWinner()) return;
        
    	players[activePlayer].deactivate();
    	if (activePlayer == Game.NUM_PLAYERS - 1) {
    		activePlayer = Game.HUMAN;
    	} else {
    		activePlayer++;
    	}
    	players[activePlayer].activate();
    }
    
    private boolean testWinner() {
    	Debug.out("in testWinner()");
        if (players[activePlayer].hand.getSize() == 0) {
        	for (int i = 0; i < NUM_PLAYERS; i++) {
        		players[i].hand.faceUp();
        	}
        	gameHasEnded = true;
        } else if(newCardPile.getSize() == 0) {
        	for (int i = 0; i < NUM_PLAYERS; i++) {
        		players[i].hand.faceUp();
        	}
            gameHasEnded = true;
        }
        dirty = gameHasEnded;
        return gameHasEnded;
    }
    
    private void renderWinInfo() {
        if (players[activePlayer].hand.getSize() == 0) {
            Debug.out("A player discarded all his cards.");
            gameHasEnded = true;
            winner = activePlayer;
            String msg = players[winner].name
                       + " have no cards left. "
            		   + players[winner].name
                       + " won the game! You can start a new game from the menu.";
            theMidlet.showAlert(msg);
        } else if(newCardPile.getSize() == 0) {
            Debug.out("New card pile ran out of cards.");
            gameHasEnded = true;

            // determine the player with the lowest valued card
            winner = getLowestPlayer();

            String msg = "No more cards left in the pile. "
                       + players[winner].name
                       + " won the game! You can start a new game from the menu.";
            theMidlet.showAlert(msg);
        }
    }
    
    private int getLowestPlayer() {
    	// remove the "tago" cards from the human player
//    	while (players[Game.HUMAN].prepareToReveal()) {
//    		players[Game.HUMAN].reveal(sets[Game.HUMAN]);
//    	}
    	
        int loval = 1000;
        int val = 0;
        int lowestPlayer = 0;
        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
        	if (!players[i].didReveal) continue;
            val = players[i].hand.getRankTotal();
            if (val < loval) {
                loval = val;
                lowestPlayer = i;
            }
        }
        return lowestPlayer;
    }
    
    private void initPositions() {
    	// the new card pile
        newCardPile.orient(Hand.PILE);  // 3D stacked look
        newCardPile.move(screenWidth - Config.CARD_WIDTH - 7, playerAreaHeight*2 + 1);  
        
        // the dump pile
        dumpPile.orient(Hand.PILE);
        dumpPile.move(screenWidth - (Config.CARD_WIDTH*2) - 14, playerAreaHeight*2 + 1);

        // position and orientation of human player's hand and sets
        players[Game.HUMAN].orient(Hand.HORIZONTAL);
        players[Game.HUMAN].hand.move(1, playerAreaHeight*2 + 2);
        sets[Game.HUMAN].orient(Hand.HORIZONTAL);
        sets[Game.HUMAN].move(1, (playerAreaHeight*2) + Config.CARD_HEIGHT + 4);
                
        // position and orientation of computer players' hands and sets
        players[Game.COMPUTER_B].orient(Hand.HORIZONTAL);
        sets[Game.COMPUTER_B].orient(Hand.HORIZONTAL);
        players[Game.COMPUTER_B].hand.move(1, 2);
        sets[Game.COMPUTER_B].move(1, Config.CARD_HEIGHT +4);
        
        players[Game.COMPUTER_A].orient(Hand.HORIZONTAL);
        sets[Game.COMPUTER_A].orient(Hand.HORIZONTAL);
        players[Game.COMPUTER_A].hand.move(1, playerAreaHeight + 2);
        sets[Game.COMPUTER_A].move(1, playerAreaHeight + Config.CARD_HEIGHT + 4);
    }
}
