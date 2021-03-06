package edu.virginia.engine.display;

import java.awt.AlphaComposite;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import edu.virginia.engine.controller.GamePad;
import edu.virginia.engine.controller.GamePadComponent;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import javax.swing.JFrame;
import javax.swing.Timer;


/**
 * Highest level class for creating a game in Java.
 * 
 * */
public class Game extends DisplayObjectContainer implements ActionListener, KeyListener, MouseListener {

	/* Frames per second this game runs at */
	private int FRAMES_PER_SEC = 60;

	/* The main JFrame that holds this game */
	private JFrame mainFrame;

	/* Timer that this game runs on */
	protected Timer gameTimer;
	
	/* The JPanel for this game */
	private GameScenePanel scenePanel;

	/* Connected Game Controllers */
	private ArrayList<GamePad> controllers;

	public Game(String gameId, int width, int height) {
		super(gameId);
		
		setUpMainFrame(gameId, width, height);
		
		setScenePanel(new GameScenePanel(this));
		
		/* Use an absolute layout */
		scenePanel.setLayout(null);

		/* Search for and add any controllers that are connected */
		controllers = new ArrayList<GamePad>();
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();
		for (int i = 0; i < cs.length; i++) {
			Controller controller = cs[i];
			if (
					controller.getType() == Controller.Type.STICK ||
							controller.getType() == Controller.Type.GAMEPAD ||
							controller.getType() == Controller.Type.WHEEL ||
							controller.getType() == Controller.Type.FINGERSTICK
					)
			{
				System.out.println("Found Controller: " + controller.getName() + ", " + controller.getType() );
				controllers.add(new GamePad(controller));
			}
		}
	}
	
	
	public void setFramesPerSecond(int fps){
		if(fps > 0) this.FRAMES_PER_SEC = fps;
	}

	public void setUpMainFrame(String gameId, int width, int height) {
		this.mainFrame = new JFrame();
		getMainFrame().setTitle(gameId);
		getMainFrame().setResizable(false);
		getMainFrame().setVisible(true);
		getMainFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getMainFrame().setBounds(0, 0, width, height);
		getMainFrame().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		getMainFrame().addKeyListener(this);
		getMainFrame().addMouseListener(this);
	}

	/**
	 * Starts the game
	 */
	public void start() {
		if (gameTimer == null) {
			gameTimer = new Timer(1000 / FRAMES_PER_SEC, this);
			gameTimer.start();
		} else {
			gameTimer.start();
		}
	}

	/**
	 * Stops the animation.
	 */
	public void stop() {
		pause();
		gameTimer = null;
	}

	public void pause() {
		if (gameTimer != null) {
			gameTimer.stop();
		}
	}
	
	public void exitGame(){
		stop();
		this.mainFrame.setVisible(false);
		this.mainFrame.dispose();
	}
	
	/**
	 * Close the window
	 * */
	public void closeGame(){
		this.stop();
		if(this.getMainFrame() != null){
			this.getMainFrame().setVisible(false);
			this.getMainFrame().dispose();
		}
	}


	/**
	 * Called once per frame. updates the game, redraws the screen, etc. May
	 * need to optimize this if games get too slow.
	 * */
	//@Override
	public void actionPerformed(ActionEvent e) {
		repaintGame();
	}
	
	/**
	 * Forces a repaint
	 * */
	public void repaint(){repaintGame();}
	public void repaintGame(){
		if(getScenePanel() != null){
			getScenePanel().validate();
			getScenePanel().repaint();
		}
	}

	protected void nextFrame(Graphics g) {

		try {
			/* Poll for any wireless controller buttons that are pressed down */
			pollControllers();

			/* Update all objects on the stage */
			this.update(pressedKeys,controllers);

			/* Draw everything on the screen */
			this.draw(g);
		} catch (Exception e) {
			System.out
					.println("Exception in nextFrame of game. Stopping game (no frames will be drawn anymore");
			stop();
			e.printStackTrace();
		}
	}

	private void pollControllers(){
		if(controllers != null) {
			if (controllers.size() > 0) {
				for (GamePad controller : controllers) {
					controller.update();
				}
			}
		}
	}
	
	@Override
	public void draw(Graphics g){
		/* Start with no transparency */
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1.0f));
		
		super.draw(g);
	}

	public JFrame getMainFrame() {
		return this.mainFrame;
	}
	
	public void setScenePanel(GameScenePanel scenePanel) {
		this.scenePanel = scenePanel;
		this.getMainFrame().add(this.scenePanel);
		getMainFrame().setFocusable(true);
		getMainFrame().requestFocusInWindow();
	}

	public GameScenePanel getScenePanel() {
		return scenePanel;
	}

	ArrayList<Integer> pressedKeys = new ArrayList<Integer>();
	@Override
	public void keyPressed(KeyEvent e) {
		if(!pressedKeys.contains((Integer)e.getKeyCode()))
			pressedKeys.add((Integer)e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(pressedKeys.contains((Integer)e.getKeyCode()))
			pressedKeys.remove((Integer)e.getKeyCode());

	}


	//@Override
	public void keyTyped(KeyEvent e) {
		//if(!pressedKeys.contains(KeyEvent.getKeyText(e.getKeyCode()))) {
		//	pressedKeys.add(KeyEvent.getKeyText(e.getKeyCode()));
		//	System.out.println("Key Typed");
		//	pressedKeys.remove(KeyEvent.getKeyText(e.getKeyCode()));
		//}
		
	}


	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
