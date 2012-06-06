package game;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

/**
 * A simple menu system for Tong-Its using commands.
 * @author Rico Zuniga
 */
public class MainMenu extends Form implements CommandListener {
    private TongIts theMidlet;
    private Command start;
    private Command rules;
    private Command exit;
    
    protected MainMenu(TongIts midlet) {
        // the Form constructor
        super("");
        
        // a connection to the midlet
        theMidlet = midlet;
        
        // add the welcome text
        append("Welcome to Tong-Its!");
		append("\n\n");
        append("Copyright (C) 2006 Rico Zuniga.");
		append("\n");
        append("All rights reserved.");
        append("\n\n");
        append("Site: tong-its.blogspot.com");
        append("\n");
        append("Email: tongitz@gmail.com.");
        
        // ceate and add the commands
        int priority = 1;
        start = new Command("Start", Command.SCREEN, priority++);
        rules = new Command("Rules", Command.SCREEN, priority++);
        exit = new Command("Exit", Command.EXIT, priority++);
        addCommand(start);
        addCommand(rules);
        addCommand(exit);
        setCommandListener(this);
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if (command == start) {
            theMidlet.activateGameScreen();
        } if (command == rules) {
            theMidlet.activateGameRules();
        } if (command == exit) {
            theMidlet.close();
        }
    }
}