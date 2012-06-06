package game;

public class Config {
    // number of pixels to nudge a card
    public static final int NUDGE_OFFSET = 5;

    // number of pixels the cards are drawn apart
    public static final int X_OFFSET = 10;
    public static final int Y_OFFSET = 10;
    
    // number of pixels the sets are dawn apart
    public static final int SET_X_OFFSET = 15;
    public static final int SET_Y_OFFSET = 15;

    // number of tiles in the deck image
//    public static final int TILES = 57;
    public static final int TILES = 52;

    // index of back image
    public static final int BACK_INDEX = 52;

    // deck image filenames
    public static final String DECK_NORMAL = "/deck.png";
    public static final String DECK_INVERTED = "/decki.png";
    
    // card width and height
//    public static final int CARD_WIDTH = 21;
//    public static final int CARD_HEIGHT = 29;
    public static final int CARD_WIDTH = 12;
    public static final int CARD_HEIGHT = 17;
    
    // height of the toolbar
    public static final int TOOLBAR_HEIGHT = 13;
    
    // whether to print System.out.println messages or not
    public static final boolean DEBUG = false;
//    public static final boolean DEBUG = true;
}
