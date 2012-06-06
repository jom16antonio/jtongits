package game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.CommandListener;

public class GameScreen extends Canvas implements Runnable, CommandListener {
    private TongIts theMidlet;
    private boolean running = true;
    
    // calculate the CPS
    private int cps = 0;
    private int cyclesThisSecond = 0;
    private long lastCPSTime = 0;

    // limit the CPS
    private static final int MAX_CPS = 50;
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;
    
    // states
    private int state;
    private static int t=0;
    public static final int PLAYING = t++;	// game in progress
    public static final int PAUSED = t++;	// game paused
    
    // menu items
    private Command rules;
    private Command exit;
    private Command bunot;
    private Command chow;
    private Command reveal;
    private Command sapaw;
    private Command doSapaw;
    private Command dump;
    private Command start;
    
    private Image offScreenBuffer;
    private Game game = null;
    private boolean showFps = false;
    
    public GameScreen(TongIts midlet) {
        theMidlet = midlet;
        
        // create the game thread
        Thread t =new Thread(this);
        t.start();
        
        initResources();
        
        // create and add the commands
        int priority = 1;
        rules = new Command("Rules", Command.SCREEN, priority++);
        exit = new Command("Exit", Command.EXIT, priority++);
        bunot = new Command("Get New Card", Command.SCREEN, priority++);
        chow = new Command("Chow", Command.SCREEN, priority++);
        reveal = new Command("Reveal Set", Command.SCREEN, priority++);
        sapaw = new Command("Select Set for Sapaw", Command.SCREEN, priority++);
        doSapaw = new Command("Sapaw on Set", Command.SCREEN, priority++);
        dump = new Command("Dump Card", Command.SCREEN, priority++);
        start = new Command("New Game", Command.SCREEN, priority++);
        addCommand(rules);
        addCommand(exit);
        addCommand(bunot);
        addCommand(chow);
        addCommand(reveal);
        addCommand(sapaw);
        addCommand(doSapaw);
        addCommand(dump);
        addCommand(start);
        setCommandListener(this);
    }
    
    public final void setState(int newState) {
    	state = newState;
    }
    
    public void commandAction(Command command, Displayable displayable) {
    	if (command == rules) {
            theMidlet.activateGameRules();
        } if (command == exit) {
            theMidlet.close();
        } if (command == bunot) {
        	game.bunot();
        } if (command == chow) {
        	game.chow();
        } if (command == reveal) {
        	game.reveal();
        } if (command == sapaw) {
        	game.sapaw();
        } if (command == doSapaw) {
        	game.doSapaw();
        } if (command == dump) {
        	game.dump();
        } if (command == start) {
        	game.newGame();
        }        
    }
    
    public void run() {
        while (running) {
        	// remember the starting time
        	long cycleStartTime = System.currentTimeMillis();

        	if (state != PAUSED) {
            	repaint();
            	if (game != null) game.playGame();
                if (System.currentTimeMillis() - lastCPSTime > 1000) {
                    lastCPSTime = System.currentTimeMillis();
                    cps = cyclesThisSecond;
                    cyclesThisSecond = 0;
                } else {
                    cyclesThisSecond++;
                }
                
                // sleep if we've finished our work early
                long timeSinceStart = (cycleStartTime - System.currentTimeMillis());
                if (timeSinceStart < MS_PER_FRAME) {
                	try {
                		Thread.sleep(MS_PER_FRAME - timeSinceStart);
                	} catch (java.lang.InterruptedException e) {}
                }
        		
        	} else {	// PAUSED
        		// just hang around sleeping if we are paused
        		try {
        			Thread.sleep(500);
        		} catch (java.lang.InterruptedException e) {}
        	}
        }
    }
    
    protected void paint(Graphics graphics) {
    	renderWorld();
    	graphics.drawImage(offScreenBuffer, 0, 0, Graphics.LEFT | Graphics.TOP);
    }
    
    private void initResources() {
    	offScreenBuffer = Image.createImage(getWidth(), getHeight());
        game = new Game(offScreenBuffer, theMidlet);
        game.newGame();
        repaint();
    	System.out.println("width: " + getWidth());
    	System.out.println("height: " + getHeight());
    }
    
    private void renderWorld() {
    	Graphics osg = offScreenBuffer.getGraphics();
    	game.renderGame();
    	
        // draw the current fps
        if (showFps) {
        	osg.setColor(0x00ffffff);
        	osg.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        	osg.drawString(cps + " fps", 1, (getHeight()-Config.TOOLBAR_HEIGHT) + 1, Graphics.LEFT | Graphics.TOP);
        }
    }
    
    protected void keyPressed(int keyCode) {
    	switch (getGameAction(keyCode)) {
    		case UP:
    		case DOWN:
    			game.select();
    			break;
    		case LEFT:
    			game.markNext(Game.LEFT);
    			break;
    		case RIGHT:
    			game.markNext(Game.RIGHT);
    			break;
			default:
				break;
    	}
        if (keyCode == Canvas.KEY_STAR && Config.DEBUG) {
            showFps = !showFps;
        }
    }
}
