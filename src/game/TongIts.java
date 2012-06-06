package game;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * The application class for the game Tong-Its.
 * @author Rico Zuniga
 *
 */

public class TongIts extends MIDlet {
    private static int s=0;
    public static final int GAME_SCREEN = s++;
    public static final int MAIN_MENU = s++;
	public int currentMenu = GAME_SCREEN;

	private MainMenu menu;
    private GameRules rules;
    private GameScreen gs;
	
    private boolean gameStarted = false;
    
    public TongIts() {
    	gs = new GameScreen(this);
    	menu = new MainMenu(this);
    }
    
    public void gameOver() {
        Alert alert = new Alert("", "Splat! Game Over"
                                , null, AlertType.INFO);
        Display.getDisplay(this).setCurrent(alert, menu);
    }
    
    public void close() {
        try {
            destroyApp(true);
            notifyDestroyed();
        } catch (MIDletStateChangeException e) {}
    }
    
    public void startApp() throws MIDletStateChangeException {
        if (!gameStarted) {
            activateMainMenu();
            gameStarted = true;
        } else {
            activateGameScreen();
        }        
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional)
        throws MIDletStateChangeException {
    }
    
    public void activateGameScreen() {
    	Display.getDisplay(this).setCurrent(gs);
    	currentMenu = GAME_SCREEN;
    }
    
    public void activateMainMenu() {
    	Display.getDisplay(this).setCurrent(menu);
    	currentMenu = MAIN_MENU;
    }
    
    public void activateGameRules() {
    	rules = new GameRules(this);
    	Display.getDisplay(this).setCurrent(rules);
    }
    
    public void showAlert(String msg) {
        Alert alert = new Alert("a", msg, null, AlertType.INFO);
//        Command back = new Command("Back", Command.BACK, 0);
//        alert.addCommand(back);
//        alert.setTimeout(Alert.FOREVER);
        Display.getDisplay(this).setCurrent(alert, gs);
    }
}
