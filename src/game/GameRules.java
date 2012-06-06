package game;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

/**
 * A simple menu system for RoadRun using commands.
 * @author Rico Zuniga
 */
public class GameRules extends Form implements CommandListener {
    private TongIts theMidlet;
    private Command back;
    
    /**
     * Creates a new menu object (while retaining a reference to
     * its parent midlet).
     * @param midlet The MIDlet this menu belongs to (used to
     * later call back to activate the game screen as well
     * as exit).
     */
    protected GameRules(TongIts midlet) {
        // the Form constructor
        super("");
        
        // a connection to the midlet
        theMidlet = midlet;
        
        // add the rules text
        append("How To Play Tong Its\n\nTong-Its is similar to other rummy games such as Gin Rummy. Those familiar with it can easily learn to play Tong-Its.\n\n* The Set up\nTong its is a three player game. The mano (winner of previous round) is dealt 13 cards while the other two players are dealt 12. The remaining cards are placed in the center of the table.\n\n* The Play\nThe mano always starts the play. He can open by [Revealing a Set] if he has any, or [Dumping a Card]. Once he dumps a card his turn ends.The play moves to the next player to his right.\n\nThe next player can either pick up the dumped card which is called a [Chow] or [Get a New Card] from the deck. If the player chooses to perform Chow, he has to Reveal a Set with the dumped card he just picked up. If he gets a new card from the deck (instead of Chow) and forms a valid set he is not obligated to reveal this set.\n\nDuring his turn, the player also has the option to connect to or perform [Sapaw] on any revealed Set, either his or the other players\'. He can connect by extending a straight lush by one or more or by adding a card to a trio making a 4 of a kind. When he is done either Revealing Sets or performing Sapaw, he must dump one card to end his turn.\n\nThe play continues as above until the deck runs out of cards or a player discards all his cards thus performing a Tong-Its.\n\n* Navigation and Selecting Cards\nYou can move the highlighter using the left and right keys. You can select or deselect a highlighted card using the up or down keys. Selected cards appear nudged.\n\n* To Get a New Card\nPress \"Get New Card\" from the menu. This can only be done at the start of your turn. You can perform a Chow instead of getting a new card from the deck.\n\n* To Perform Chow\nSelect 2 or more cards in your hand that can form a valid set with the dumped card then press \"Chow\" from the menu. You can Chow only at the start of your turn.\n\n* To Reveal a Set\nA valid set is either at least a three-card straight flush (3 cards of the same suit) or at least a three of a kind (a.k.a. trio). You can drop a straight flush with more than three cards or a four of a kind instead of a trio. Note taht Aces can only be used in low straights.\nExamples of valid sets: 2C 3C 4C or 7C 7S 7H\n\n\n* To Perform Sapaw\nSelect 1 or more cards from your hand that can extend a Revealed Set. Revealed Sets are located just below a player\'s hand. Press \"Select Set for Sapaw\" from the menu, this brings you to set selection mode. Choose the correct set that you want to perform Sapaw on then press \"Sapaw on Set\" from the menu.\n\n* To Dump\nSelect the card to dump then press \"Dump Card\" from the menu. You can only dump if you have performed Chow or got a new card from the deck. This will end your turn.\n\n* To Start a New Game\nPress \"New Game\" from the menu.");
        
        // ceate and add the commands
        int priority = 1;
        back = new Command("Back", Command.BACK, priority++);
        addCommand(back);
        setCommandListener(this);
    }
    
    /**
     * Handles the start (activate game screen) and exit commands.
     * All the work is done by the RoadRun class.
     * @param command the command that was triggered
     * @param displayable the displayable on which the event occured
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command == back) {
        	if (theMidlet.currentMenu == TongIts.MAIN_MENU) {
        		theMidlet.activateMainMenu();
        	} else if (theMidlet.currentMenu == TongIts.GAME_SCREEN) {
        		theMidlet.activateGameScreen();
        	}
            
        }
    }
}
