package game;

import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class CardGraphics {
    private Image deckNormal = null;
    private Image deckInverted = null;
    
    private Graphics doubleBufferGraphics = null;
    
    public CardGraphics(Image doubleBuffer) {
        doubleBufferGraphics = doubleBuffer.getGraphics();
//        doubleBufferGraphics.setClip(0, 0, doubleBuffer.getWidth(), doubleBuffer.getHeight());
        
        try {
            deckNormal = Image.createImage(Config.DECK_NORMAL);
            deckInverted = Image.createImage(Config.DECK_INVERTED);
        } catch (IOException ioe) {
            System.out.println("Unable to load image.");
        }
    }
    
    private int getCardIndex(Card card) {
        int index = 0;
        
        // return back image if card is faced down
        if (card.isFacedDown()) {
            index = Config.BACK_INDEX;
        } else {
            index = (card.suit * 13) + (card.rank - 1);
        }
        return index;
    }
    
    private int getTileIndex(Card card) {
        int index = this.getCardIndex(card);
        return Config.CARD_WIDTH * index;
    }
    
    public void drawCard(Card card) {
        if (deckNormal == null) return;
        if (deckInverted == null) return;
        
        Image deck = null;
        if (card.isMarked()) {
            deck = deckInverted;
        } else {
            deck = deckNormal;
        }
        
        int offset = this.getTileIndex(card);
        doubleBufferGraphics.setClip(card.x, card.y, Config.CARD_WIDTH, Config.CARD_HEIGHT);
        doubleBufferGraphics.drawImage(deck, card.x - offset, card.y, Graphics.TOP|Graphics.LEFT);
    }
    
    public void drawHand(Hand hand) {
        for (int i = 0; i < hand.getSize(); i++) {
            Card card = (Card) hand.cards.elementAt(i);
            drawCard(card);
        }
    }
    
    public void drawSets(Sets sets) {
    	for (int i = 0; i < sets.hands.size(); i++) {
    		Hand hand = (Hand) sets.hands.elementAt(i);
    		drawHand(hand);
    	}
    }
}
