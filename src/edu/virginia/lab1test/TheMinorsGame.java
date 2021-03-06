package edu.virginia.lab1test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import edu.virginia.engine.controller.GamePad;
import edu.virginia.engine.event.Event;
import edu.virginia.engine.tween.Tween;
import edu.virginia.engine.tween.TweenJuggler;
import edu.virginia.engine.display.*;
import edu.virginia.engine.event.*;
import edu.virginia.engine.tween.TweenTransition;
import edu.virginia.engine.tween.TweenableParam;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.RNJesus;
import edu.virginia.engine.util.SoundEffect;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class TheMinorsGame extends Game {

    // GLOBAL CONSTANTS
    public enum GameMode {
        ITEM_SELECTION, ITEM_PLACEMENT, GAMEPLAY, MAIN_MENU, ROUND_COMPLETE, LEVEL_SELECTION, GAME_COMPLETE, START_SCREEN, FEEDBACK, INSTRUCTIONS;
    }

    private GameMode gameMode = GameMode.LEVEL_SELECTION;
    private final static int GAME_WIDTH = 1900;
    private final static int GAME_HEIGHT = 1000;
    private final static String INPUT_KEYBOARD = "keyboard";
    private final static String INPUT_GAMEPADS = "gamepads";
    // keys
    private final static int KEY_DELAY = 200;
    private final static int KEY_UP = 38;
    private final static int KEY_DOWN = 40;
    private final static int KEY_LEFT = 37;
    private final static int KEY_RIGHT = 39;
    private final static int KEY_SPACE = 32;
    private final static int KEY_R = 82;
    private final static int KEY_ESC = 27;

    // speeds etc
    private final static int CURSOR_SPEED = 10;
    private final static int PLAYER_SPEED = 5;
    private final static int BEAM_SPEED = 15;
    private final static int GRAVITY = 1;
    private final static int JUMP_SPEED = 16;
    private final static int ROUND_COUNT = 10;
    private final static int SLIDING_PLATFORM_SPEED = 2;
    private final static int FLOATTIME = 500;
    private final static int SAW_SPEED = 1;
    private final static int WIN_SCORE = 400; //TODO



	// GLOBAL VARIABLES
    private int frameCounter = 0;
    private boolean itemSelectionInitialized = false;
    private int placedItemCounter = 0;
    private boolean debugHitboxes = false;
    private String inputMode = "";
    private int numberOfPlayers = 0;
    private int numberOfSelectedItems = 0;
    private int numberOfPlacedItems = 0;
    private int playersDead = 0;
    private int playersCompleted = 0;
    private Player firstCompleted = null;
    private boolean scoresCalculated = false;
    private int gameWinner = 5;
    private boolean gameWon = false;
    private boolean gameOver = false;
    private int roundsCompleted = 1;
    private int roundsSinceLevelCompleted = 0;


//
	// SET UP SPRITE ASSETS
    // Characters
    private ArrayList<Player> players = new ArrayList<>(0);
	private Player player1 = new Player("player1", "player1","cursor-orange.png",0);
	private Player player2 = new Player("player2", "player2","cursor-blue.png",1);
	private Player player3 = new Player("player3", "player3","cursor-green.png",2);
	private Player player4 = new Player("player4", "player4","cursor-pink.png",3);
	// Level starting items
	private Sprite platform1 = new Sprite("platform1", "3x1platform.png");
	private Sprite platform2 = new Sprite("platform2", "3x1platform.png");
	private Sprite portal = new Sprite("portal","portal.png");
	// Placeholder Sprites for randomly selected placeable items - their images are what will be set later, and their ids updated
    private Sprite item1 = new Sprite("item1");
    private Sprite item2 = new Sprite("item2");
    private Sprite item3 = new Sprite("item3");
    private Sprite item4 = new Sprite("item4");
    private Sprite item5 = new Sprite("item5");
    private RNJesus rnJesus = new RNJesus();
	// Backgrounds
    private Sprite selectionBackground = new Sprite("selectionbackground","big-item-selection-screen.png");
    private Sprite scoreboardBackground = new Sprite("scoreboardbackground","bigger-item-selection-screen.png");
    private Sprite gameOverBackground = new Sprite("gameoverbackground","item-selection-screen.png");
    private Sprite levelSelectionBackground = new Sprite("levelselectionbackground","Background4.png");
    private Sprite startBackground = new Sprite("levelselectionbackground","goalareas.png");
    private Sprite finishBackground = new Sprite("levelselectionbackground","goalareas.png");
    private Sprite controlsBackground = new Sprite("controlsbackground","goalareas.png");

    // Titles
    private Sprite gameTitle = new Sprite("game title","gametitle.png");
    private Sprite scoreTitle = new Sprite("score title", "scoreboardtitle.png");
    private Sprite noPointsTitle = new Sprite("no points", "nopoints.png");
    private Sprite noPointsWide = new Sprite("no points wide", "nopointswide.png");
    private Sprite startText = new Sprite("score title", "starttext.png");
    private Sprite finishText = new Sprite("score title", "finishtext.png");
    private Sprite scoreboardHeader = null;
    private Sprite lastRound = new Sprite("last round", "lastround.png");
    private Sprite penultimateRound = new Sprite("penultimate", "2roundsleft.png");
    private Sprite thirdToLast = new Sprite("third to last", "3roundsleft.png");
    private Sprite controlsText = new Sprite("controlsText", "controls.png");
    private Sprite instructions = new Sprite("instructions", "instructions.png");
    // Item Lists
    private ArrayList<Sprite> placeableItemList = new ArrayList<>(0);
    private ArrayList<Sprite> laserGunList = new ArrayList<>(0);
    private ArrayList<LaserBeam> laserBeams = new ArrayList<>(0);
    private ArrayList<Sprite> selectableSlidingPlatforms = new ArrayList<>(0);
    private ArrayList<Sprite> gameplaySlidingPlatforms = new ArrayList<>(0);
    private ArrayList<Sprite> selectableSawbladeList = new ArrayList<>(0);
    private ArrayList<Sprite> gameplaySawbladeList = new ArrayList<>(0);
    // Display Object Containers
    private DisplayObjectContainer levelContainer = new DisplayObjectContainer("level container");
    private DisplayObjectContainer overlay = new DisplayObjectContainer("start and finish overlay");
    private DisplayObjectContainer levelImages = new DisplayObjectContainer("level images");        // contains level background images for level selection
    // Levels
    private ArrayList<Level> levelList= new ArrayList<>(0);

    private Level level1 = new Level("level1",new Sprite("level1_background","Background1.png"));
    private Level level2 = new Level("level2",new Sprite("level2_background","Background2.jpg"));
    private Level level3 = new Level("level3",new Sprite("level3_background","Background3.jpg"));
    private Level currentLevel = null;

	// AUDIO ASSETS
    private SoundEffect theme1 = new SoundEffect("theme1.wav");
    private SoundEffect theme2 = new SoundEffect("theme2.wav");
    private SoundEffect theme3 = new SoundEffect("theme3.wav");
    private SoundEffect theme4 = new SoundEffect("theme4.wav");
    private SoundEffect laserSound = new SoundEffect("laser.wav");
    private SoundEffect jumpSound = new SoundEffect("jump.wav");
    private SoundEffect DynamiteSound = new SoundEffect("rocket.wav");


	// EVENT MANAGERS
	private EventManager eventManager = new EventManager();
	//this is just a sanity check to make sure I remember how managers/events work
	//xCoinTween.addEventListener(eventManager, Event.TWEEN_COMPLETE_EVENT);
    // the quest manager listens for events from the xCoinTween

	// TWEENS

    // GAME CLOCKS
    //item selection, item placement, play time
    private GameClock rKeyClock = new GameClock();
    private GameClock spaceKeyClock = new GameClock();
    private GameClock roundCompleteClock = new GameClock();
    private GameClock gameCompleteClock = new GameClock();
    private GameClock feedbackClock = new GameClock();



	
    /**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	private TheMinorsGame() {
		super("The Minors Game", GAME_WIDTH, GAME_HEIGHT);

        // POPULATE ITEM LISTS
        placeableItemList.add(item1);
        placeableItemList.add(item2);
        placeableItemList.add(item3);
        placeableItemList.add(item4);
        placeableItemList.add(item5);

        levelList.add(level1);
        levelList.add(level2);
        levelList.add(level3);

        //SET BACKGROUNDS UP FOR EACH SCREEN
        selectionBackground.setPosition(700,250);
        selectionBackground.setScale(1,1);

        scoreboardBackground.setPosition(633,250);
        scoreboardBackground.setScale(1,1);

        instructions.setPosition(0,0);
        instructions.setScaledSize(GAME_WIDTH,GAME_HEIGHT);

//        scoreboardHeader.setyScale(.5);
//        scoreboardHeader.setxScale(.5);
//        scoreboardHeader.alignCenterHorizontal(scoreboardBackground);

        gameOverBackground.setPosition(675,250);
        gameOverBackground.setScale(1,1);

        levelSelectionBackground.setPosition(0,0);
        levelSelectionBackground.setScaledSize(GAME_WIDTH,GAME_HEIGHT);

        theme1.play(true);

        // BUILD DISPLAY TREES

        for(Level level : levelList) {
            levelImages.addChild(level.getBackground());
        }
        for(int i = 0; i < levelImages.getChildren().size(); i++) {
            levelImages.getByIndex(i).setScale(0.2,0.2);
            levelImages.getByIndex(i).setPosition((i+1)*GAME_WIDTH/4,GAME_HEIGHT/2);
        }

        levelContainer.addChild(platform1);
        levelContainer.addChild(platform2);
        levelContainer.addChild(portal);

        overlay.addChild(startBackground);
        overlay.addChild(finishBackground);
        overlay.addChild(startText);
        overlay.addChild(finishText);
        startBackground.setAlpha((float)0.5);
        finishBackground.setAlpha((float)0.5);
        controlsBackground.setAlpha((float)0.5);
        startBackground.setScale(1.75, 1.5);
        finishBackground.setScale(1.75, 1.5);
        controlsBackground.setScale(1.5, 1.25);
        startText.setScale(0.5,0.5);
        finishText.setScale(0.5,0.5);

        platform1.setxPosition(0);
        platform1.setyPosition(GAME_HEIGHT/2);
        platform1.setxScale(.8);
        platform1.setyScale(.8);

        platform2.setxScale(.8);
        platform2.setyScale(.8);
        platform2.setxPosition(GAME_WIDTH - platform2.getScaledWidth());
        platform2.setyPosition(GAME_HEIGHT/2);

        portal.setScale(0.2,0.2);
        portal.setPosition(GAME_WIDTH-portal.getScaledWidth()-20,GAME_HEIGHT/2-60);


        // PLAY MUSIC


        gameMode = GameMode.LEVEL_SELECTION;

	}

	private void initializePlayers(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        if(numberOfPlayers==0) {
            switch (gamePads.size()) {
                case 0:
                    inputMode = INPUT_KEYBOARD;
                    players.add(player1);
                    System.out.println("One player on keyboard");
                    break;
                case 1:
                    inputMode = INPUT_GAMEPADS;
                    players.add(player1);
                    System.out.println("One player on controller");
                    break;
                case 2:
                    inputMode = INPUT_GAMEPADS;
                    players.add(player1);
                    players.add(player2);
                    System.out.println("Two players on controllers");
                    break;
                case 3:
                    inputMode = INPUT_GAMEPADS;
                    players.add(player1);
                    players.add(player2);
                    players.add(player3);
                    System.out.println("Three players on controllers");
                    break;
                case 4:
                    inputMode = INPUT_GAMEPADS;
                    players.add(player1);
                    players.add(player2);
                    players.add(player3);
                    players.add(player4);
                    System.out.println("Four players on controllers");
                    break;
            }
            numberOfPlayers = players.size();
            scoreTitle.alignFractionVertical(scoreboardBackground,numberOfPlayers+2,1);
            resetPlayers(pressedKeys,gamePads);
            for(Player player : players) {
                player.addEventListener(eventManager, Event.SAFE_COLLISION);
                player.addEventListener(eventManager, Event.UNSAFE_COLLISION);
                player.addEventListener(eventManager, Event.DEATH);
                player.addEventListener(eventManager, Event.GOAL);
            }
        }
    }

    private void resetPlayers(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        for(Player player : players) {
            player.setCourseCompleted(false);
            player.setRotation(0);
            player.setVisible(true);
            player.setAlive(true);
            player.setAirborne(true);
            player.setAlpha(1);
            player.setPivotCenter();
            player.setScale(.8, .8);
            if(currentLevel != null) {
                if (currentLevel.getBackground().getFileName().contains("1")) {
                    player.setPosition(10 + players.indexOf(player) * 10, GAME_HEIGHT - 200);   //space out players
                } else if (currentLevel.getBackground().getFileName().contains("2")) {
                    player.setPosition(10 + players.indexOf(player) * 10, 400);   //space out players
                } else if (currentLevel.getBackground().getFileName().contains("3")) {
                    player.setPosition(GAME_WIDTH / 2 + players.indexOf(player) * 10, GAME_HEIGHT - 200);   //space out players
                }
            }
            player.setyAcceleration(GRAVITY);
            player.setyVelocity(0);
            player.setxVelocity(0);
            player.cursor.setScale(0.25, 0.25);
            player.cursor.setyPosition(350);
            player.cursor.alignCenterHorizontal(levelContainer);
            player.cursor.setVisible(true);
            player.item = null;
            //player.cursor.alignFractionHorizontal(levelContainer,players.size()+1,players.indexOf(player)+1);      //space out cursors
            //TODO make the cursors spread nicely
            player.cursor.setxPosition(GAME_WIDTH / 2);
            player.update(pressedKeys,gamePads);
        }
        for(Sprite platform: gameplaySlidingPlatforms) {
            platform.setxPosition(platform.getStartX());
            platform.setyPosition(platform.getStartY());
        }
        playersDead = 0;
        playersCompleted = 0;
        firstCompleted = null;
        scoresCalculated = false;
    }

    private void resetGame(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        gameWon = false;
        gameOver = false;
        gameWinner = 5;
        roundsCompleted = 1;
        gameOverBackground.removeAll();
        levelContainer.removeAll();
        levelContainer.addChild(platform1);
        levelContainer.addChild(platform2);
        levelContainer.addChild(portal);
        laserGunList.clear();
        resetPlayers(pressedKeys, gamePads);
        roundCompleteClock.resetGameClock();
        for(Player player : players) {
            player.setScore(1);
        }
        theme2.stop();
        theme3.stop();
        theme4.stop();
        theme1.play(true);
    }

	private void initializeItemSelection() {
	    selectionBackground.removeAll();
        selectionBackground.addChild(item1);
        selectionBackground.addChild(item2);
        selectionBackground.addChild(item3);
        selectionBackground.addChild(item4);
        selectionBackground.addChild(item5);
        for(int i = 0; i < 4 - numberOfPlayers; i++) {
            selectionBackground.removeByIndex(selectionBackground.getChildren().size()-1);
        }
        int itemCount = selectionBackground.getChildren().size();
        placeableItemList.clear();
        selectableSawbladeList.clear();
        selectableSlidingPlatforms.clear();

        for(DisplayObjectContainer item : selectionBackground.getChildren()) {
            String filename = rnJesus.intelligentlyRandomize(roundsSinceLevelCompleted,laserGunList.size(),roundsCompleted);
            if(filename.equals("slidingplatform.png")) {
                item.setImage("slidingplatform.png");
                item.setId("sliding1x1");
                item.setScale(0.8,0.8);
                if(frameCounter % 3 == 0) {
                    item.setLeftRight(true);
                    item.setUpDown(false);
                } else if (frameCounter % 3 == 1) {
                    item.setLeftRight(false);
                    item.setUpDown(true);
                } else if(frameCounter % 3 == 2) {
                    item.setLeftRight(true);
                    item.setUpDown(true);
                }
                selectableSlidingPlatforms.add((Sprite)item);
            } else item.setImage(filename);
            switch(filename){
                case "3x1platform.png":
                    item.setScale(.8,.8);
                    break;
                case "spikerow.png":
                    item.setScale(.8,.8);
                    break;
                case "LaserGun.png":
                    item.setScale(1.5,1.5);
                    break;
                case "1x1platform.png":
                    item.setScale(.8,.8);
                    break;
                case "box.png":
                    item.setScale(.8,.8);
                    break;
                case "sawblade.png":
                    item.setScale(.75,.75);
                    selectableSawbladeList.add((Sprite) item);
                    break;
                case "Dynamite.png":
                    item.setScale(.2,.2);
                    break;
            }
            item.setVisible(true);
            item.setRotation(0);
            placeableItemList.add((Sprite)item);
            item.alignCenterHorizontal(selectionBackground);
            item.alignFractionVertical(selectionBackground,
                    itemCount+1,
                    selectionBackground.getChildren().indexOf(item)+1);
            ((Sprite) item).setStartX(item.getxAbsolutePosition());
            ((Sprite) item).setStartY(item.getyAbsolutePosition());
        }

        // GIVE ITEMS IMAGES - will be randomized later TODO

//        item1.setScale(0.7,0.3);
//        item1.alignCenterHorizontal(selectionBackground);
//        item1.alignFractionVertical(selectionBackground,100,30);
//
//        item2.setScale(0.3,0.3);
//        item2.alignCenterHorizontal(selectionBackground);
//        item2.alignFractionVertical(selectionBackground,100,52);
//
//        item3.setScale(.5,.5);
//        item3.alignCenterHorizontal(selectionBackground);
//        item3.alignFractionVertical(selectionBackground,100,75);
//
//        for(Sprite item : placeableItemList) {
//            item.setVisible(true);
//        }

        itemSelectionInitialized = true;
    }

    private void initializeLevels() {
	    //TODO give levels their sprites and locations
    }

	@Override
	public void update(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads){
		super.update(pressedKeys,gamePads);
        frameCounter++;
        if (frameCounter > 3) {
            TweenJuggler.getInstance().nextFrame();
            initializePlayers(pressedKeys,gamePads); //only happens once
            if(gameMode != null) {
                switch (gameMode) {
                    case START_SCREEN:
                        startScreenUpdate(pressedKeys,gamePads);
                        break;
                    case LEVEL_SELECTION:
                        levelSelectionUpdate(pressedKeys,gamePads);
                        break;
                    case ITEM_SELECTION:
                        itemSelectionUpdate(pressedKeys,gamePads);
                        break;
                    case ITEM_PLACEMENT:
                        itemPlacementUpdate(pressedKeys,gamePads);
                        break;
                    case GAMEPLAY:
                        gameplayUpdate(pressedKeys,gamePads);
                        break;
                    case MAIN_MENU:
                        break;
                    case ROUND_COMPLETE:
                        roundCompleteUpdate(pressedKeys,gamePads);
                        break;
                    case GAME_COMPLETE:
                        gameCompleteUpdate(pressedKeys, gamePads);
                        break;
                    case FEEDBACK:
                        feedbackUpdate(pressedKeys, gamePads);
                        break;
                    case INSTRUCTIONS:
                        instructionsUpdate(pressedKeys,gamePads);
                        break;
                }
            }
        }
	}

	// UPDATE METHODS FOR MODES

    private void startScreenUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads){
        //TODO make a start screen, idk what we want
    }

    private void levelSelectionUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads){
        if(players.size() > 0) {
            for(Player player : players) {
                player.cursor.setVisible(true);
                player.cursor.update(pressedKeys, gamePads);
                // MOVE CURSOR BASED ON USER INPUT
                if (inputMode.equals(INPUT_GAMEPADS))
                    handleGamepadCursorMoveInput(player.cursor, CURSOR_SPEED, gamePads, player.playerNumber);
                else handleCursorMoveInput(player.cursor, CURSOR_SPEED, pressedKeys);
                levelImages.update(pressedKeys,gamePads);
                gameTitle.alignCenterHorizontal(GAME_WIDTH);
                gameTitle.setyPosition(GAME_HEIGHT/2 - 275);
                controlsText.setPivotCenter();
                controlsText.setScale(0.5,0.5);
                controlsText.alignCenterHorizontal(GAME_WIDTH);
                controlsText.setPosition(controlsBackground.getxPosition()+controlsBackground.getScaledWidth()/2- controlsText.getScaledWidth()/2,
                        controlsBackground.getyPosition()+controlsBackground.getScaledHeight()/2- controlsText.getScaledHeight()/2);
                controlsBackground.alignCenterHorizontal(GAME_WIDTH);
                controlsBackground.setyPosition(GAME_HEIGHT-300);
                controlsBackground.update(pressedKeys,gamePads);
                for(DisplayObjectContainer background : levelImages.getChildren()) {
                    background.setScaledSize(300,175);
                    background.alignCenterVertical(GAME_HEIGHT);
                    background.alignFractionHorizontal(GAME_WIDTH,4,levelImages.getChildren().indexOf(background)+1);
                    if (player.cursor.collidesWith(background)) {
                        if (inputMode.equals(INPUT_GAMEPADS)) {
                            if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_A) && gamePads.get(player.playerNumber).aButtonClock.getElapsedTime() > KEY_DELAY) {
                                for(Level level : levelList) {                              // potentially not the best way to do this
                                    if(background.getFileName().equals(level.getBackground().getFileName())){
                                        theme1.stop();
                                        currentLevel = level;
                                        currentLevel.getBackground().setPosition(0,0);
                                        currentLevel.setPosition(0,0);
                                        currentLevel.getBackground().setScaledSize(GAME_WIDTH, GAME_HEIGHT);
                                        if(currentLevel.getBackground().getFileName().contains("1")) {
                                            theme4.play(true);
                                            platform1.setPosition(0,GAME_HEIGHT*7/8);
                                            platform2.setxPosition(GAME_WIDTH - platform2.getScaledWidth());
                                            platform2.setyPosition(GAME_HEIGHT/4);
                                            portal.setPosition(GAME_WIDTH-portal.getScaledWidth()-20,GAME_HEIGHT/4-60);
                                        } else if(currentLevel.getBackground().getFileName().contains("2")) {
                                            theme2.play(true);
                                            platform1.setxPosition(0);
                                            platform1.setyPosition(GAME_HEIGHT/2);
                                            platform2.setxPosition(GAME_WIDTH - platform2.getScaledWidth());
                                            platform2.setyPosition(GAME_HEIGHT/2);
                                            portal.setPosition(GAME_WIDTH-portal.getScaledWidth()-20,GAME_HEIGHT/2-60);
                                        } else if(currentLevel.getBackground().getFileName().contains("3")) {
                                            theme3.play(true);
                                            platform1.setxPosition(GAME_WIDTH/2 - platform1.getScaledWidth()/2);
                                            platform1.setyPosition(GAME_HEIGHT*7/8);
                                            platform2.setxPosition(GAME_WIDTH/2 - platform2.getScaledWidth()/2);
                                            platform2.setyPosition(GAME_HEIGHT/4);
                                            portal.setPosition(GAME_WIDTH/2-portal.getScaledWidth()/2,GAME_HEIGHT/4-60);
                                        }
                                        startBackground.setPosition(platform1.getxPosition()+platform1.getScaledWidth()/2 - startBackground.getScaledWidth()/2,platform1.getyPosition()-startBackground.getScaledHeight());
                                        startText.setPosition(startBackground.getxPosition()+startBackground.getScaledWidth()/2-startText.getScaledWidth()/2,
                                                startBackground.getyPosition()+startBackground.getScaledHeight()/2-startText.getScaledHeight()/2);
                                        finishBackground.setPosition(platform2.getxPosition() + platform2.getScaledWidth()/2 - finishBackground.getScaledWidth()/2,platform2.getyPosition()-finishBackground.getScaledHeight());
                                        finishText.setPosition(finishBackground.getxPosition()+finishBackground.getScaledWidth()/2-finishText.getScaledWidth()/2,
                                                finishBackground.getyPosition()+finishBackground.getScaledHeight()/2-finishText.getScaledHeight()/2);
                                        break;
                                    }
                                }
                                resetPlayers(pressedKeys,gamePads);
                                gameMode = GameMode.ITEM_SELECTION;
                                gamePads.get(player.playerNumber).aButtonClock.resetGameClock();
                                return;
                            }
                        } else if (pressedKeys.contains(KEY_SPACE) && spaceKeyClock.getElapsedTime() > KEY_DELAY) {
                            for(Level level : levelList) {                              // potentially not the best way to do this
                                if(background.getFileName().equals(level.getBackground().getFileName())){
                                    theme1.stop();
                                    currentLevel = level;
                                    currentLevel.getBackground().setPosition(0,0);
                                    currentLevel.setPosition(0,0);
                                    currentLevel.getBackground().setScaledSize(GAME_WIDTH, GAME_HEIGHT);
                                    if(currentLevel.getBackground().getFileName().contains("1")) {
                                        theme4.play(true);
                                        platform1.setxPosition(0);
                                        platform1.setyPosition(GAME_HEIGHT*7/8);
                                        platform2.setxPosition(GAME_WIDTH - platform2.getScaledWidth());
                                        platform2.setyPosition(GAME_HEIGHT/4);
                                        portal.setPosition(GAME_WIDTH-portal.getScaledWidth()-20,GAME_HEIGHT/4-60);
                                    } else if(currentLevel.getBackground().getFileName().contains("2")) {
                                        theme2.play(true);
                                        platform1.setxPosition(0);
                                        platform1.setyPosition(GAME_HEIGHT/2);
                                        platform2.setxPosition(GAME_WIDTH - platform2.getScaledWidth());
                                        platform2.setyPosition(GAME_HEIGHT/2);
                                        portal.setPosition(GAME_WIDTH-portal.getScaledWidth()-20,GAME_HEIGHT/2-60);
                                    } else if(currentLevel.getBackground().getFileName().contains("3")) {
                                        theme3.play(true);
                                        platform1.setxPosition(GAME_WIDTH/2 - platform1.getScaledWidth()/2);
                                        platform1.setyPosition(GAME_HEIGHT*7/8);
                                        platform2.setxPosition(GAME_WIDTH/2 - platform2.getScaledWidth()/2);
                                        platform2.setyPosition(GAME_HEIGHT/4);
                                        portal.setPosition(GAME_WIDTH/2-portal.getScaledWidth()/2,GAME_HEIGHT/4-60);
                                    }
                                    startBackground.setPosition(platform1.getxPosition(),platform1.getyPosition()-startBackground.getScaledHeight());
                                    startText.setPosition(startBackground.getxPosition()+startBackground.getScaledWidth()/2-startText.getScaledWidth()/2,
                                            startBackground.getyPosition()+startBackground.getScaledHeight()/2-startText.getScaledHeight()/2);
                                    finishBackground.setPosition(platform2.getxPosition(),platform2.getyPosition()-finishBackground.getScaledHeight());
                                    finishText.setPosition(finishBackground.getxPosition()+finishBackground.getScaledWidth()/2-finishText.getScaledWidth()/2,
                                            finishBackground.getyPosition()+finishBackground.getScaledHeight()/2-finishText.getScaledHeight()/2);
                                    //align center/vertical didnt work here
                                    break;
                                }
                            }
                            resetPlayers(pressedKeys,gamePads);
                            gameMode = GameMode.ITEM_SELECTION;
                            spaceKeyClock.resetGameClock();
                            return;
                        }
                    }
                }
                if (player.cursor.collidesWith(controlsBackground)){
                    if (inputMode.equals(INPUT_GAMEPADS)) {
                        if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_A)) {
                            gameMode = GameMode.INSTRUCTIONS;
                        }
                    } else if (pressedKeys.contains(KEY_SPACE)) {
                        gameMode = GameMode.INSTRUCTIONS;
                    }
                }
            }
        }
    }

	private void itemSelectionUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
	    if(! itemSelectionInitialized && frameCounter > 4 && numberOfPlayers > 0) {
	        initializeItemSelection();
        }
        movePlatforms(30,pressedKeys,gamePads,false);
	    spinSawblades(false);
	    overlay.update(pressedKeys,gamePads);
        for(Player player : players) {
            if(!levelContainer.getChildren().contains(player.item)) {
                player.cursor.update(pressedKeys, gamePads);
                selectionBackground.update(pressedKeys, gamePads);
                // MOVE CURSOR BASED ON USER INPUT
                if (inputMode.equals(INPUT_GAMEPADS))
                    handleGamepadCursorMoveInput(player.cursor, CURSOR_SPEED, gamePads, player.playerNumber);
                else handleCursorMoveInput(player.cursor, CURSOR_SPEED, pressedKeys);
                // CHECK FOR OVERLAP BETWEEN CURSORS & SELECTABLE ITEMS
                for (Iterator<Sprite> iterator = placeableItemList.iterator(); iterator.hasNext(); ) {
                    Sprite s = iterator.next();
                    s.update(pressedKeys, gamePads);
                    // if the cursor overlaps with a selectable items
                    if (player.cursor.collidesWith(s)) {
                        // add tween stuff here for polish if desired
                        // and the player presses the select button over it
                        if (inputMode.equals(INPUT_GAMEPADS)) {
                            if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_A) && gamePads.get(player.playerNumber).aButtonClock.getElapsedTime() > KEY_DELAY) {
                                player.item = selectItem(iterator, s);
                                numberOfSelectedItems++;
                                player.cursor.setVisible(false);
                                if (numberOfSelectedItems >= numberOfPlayers) {
                                    gameMode = GameMode.ITEM_PLACEMENT;
                                    numberOfSelectedItems = 0;
                                    wait(200);
                                }
                                gamePads.get(player.playerNumber).aButtonClock.resetGameClock();
                                break;
                            }
                        } else if (pressedKeys.contains(KEY_SPACE) && spaceKeyClock.getElapsedTime() > KEY_DELAY) {
                            player.item = selectItem(iterator, s);
                            numberOfSelectedItems++;
                            player.cursor.setVisible(false);
                            if (numberOfSelectedItems >= numberOfPlayers) {
                                gameMode = GameMode.ITEM_PLACEMENT;
                                numberOfSelectedItems = 0;
                            }
                            spaceKeyClock.resetGameClock();         // make sure it doesn't get placed immediately after selection
                            break;
                        }
                    }
                }
                // BASED ON OVERLAPS, HANDLE USER INPUT (SELECTION OF AN ITEM)
                //if colliding and a is pressed
                //create new sprite based on selection
                //give item to player
                //remove from selectable items
                //add item to display tree

                // CHECK IF SELECTION IS DONE OR TIMED OUT
                //end selection phase and move into item phase
            }
        }
        if (numberOfSelectedItems >= numberOfPlayers) {
            for(Sprite sprite : placeableItemList) {
                if(selectableSlidingPlatforms.contains(sprite)) {
                    selectableSlidingPlatforms.remove(sprite);
                }
            }
            gameMode = GameMode.ITEM_PLACEMENT;
            GameClock gameClock = new GameClock();
            gameClock.resetGameClock();
            while (gameClock.getElapsedTime() < 200) {
                continue;
            }      //wait 200ms to prevent placement
            numberOfSelectedItems = 0;
        }
//        if(pressedKeys.contains(KEY_ESC) && escKeyClock.getElapsedTime() > KEY_DELAY){
//            gameMode = GameMode.GAMEPLAY;
//        }
//        if( gamePads.size()>=1) {
//            if (gamePads.get(0).isButtonPressed(GamePad.BUTTON_START) && escKeyClock.getElapsedTime() > KEY_DELAY) {
//                gameMode = GameMode.GAMEPLAY;
//            }
//        }
    }

    private Sprite selectItem(Iterator<Sprite> spriteIterator, Sprite sprite) {
        String spriteId = "item" + Integer.toString(placedItemCounter);   // we need to make a unique spriteId to make sure that --
        placedItemCounter++;                                              // --> new sprites don't have the same id for checks later
        Sprite newSprite = new Sprite(spriteId, sprite.getFileName());          //duplicate the sprite and add it to our level
        newSprite.setScale(sprite.getxAbsoluteScale(), sprite.getyAbsoluteScale());
        newSprite.setPosition(sprite.getxAbsolutePosition(), sprite.getyAbsolutePosition());
        levelContainer.addChild(newSprite);                                 // the level container will hold everything in the level
        if (sprite.getFileName().contains("Laser")) {
            laserGunList.add(newSprite);
        }
        if(selectableSlidingPlatforms.contains(sprite)) {
            newSprite.setLeftRight(sprite.isLeftRight());
            newSprite.setUpDown(sprite.isUpDown());
            newSprite.setId("sliding1x1");
            selectableSlidingPlatforms.remove(sprite);
            gameplaySlidingPlatforms.add(newSprite);
        }
        if(sprite.getFileName().contains("saw")) {
            selectableSawbladeList.remove(sprite);
            gameplaySawbladeList.add(newSprite);
        }
        newSprite.setPivotCenter();                                         // we only want rotation about the center of the sprite
        newSprite.dangerous = sprite.getFileName().contains("spike") ||            // if its spiky, it kills us
            sprite.getFileName().contains("sawblade");            // if its spiky, it kills us
        sprite.setVisible(false);
        spriteIterator.remove();                                                 // the item can no longer be selected
        return newSprite;
    }

    private void itemPlacementUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        if (levelContainer != null) {
            levelContainer.update(pressedKeys, gamePads);
            overlay.update(pressedKeys,gamePads);
            for(Sprite saw: gameplaySawbladeList) {
                saw.setRotation(0);
            }
            //spinSawblades(true);
            }
            for(Player player : players) {
                // Move sprite based on user input
                if (!player.item.isPlaced()) {
                    player.cursor.setVisible(true);
                    handleCursorMoveInput(player.item, CURSOR_SPEED, pressedKeys);
                    handleCursorMoveInput(player.cursor, CURSOR_SPEED, pressedKeys);
                    handleGamepadCursorMoveInput(player.item, CURSOR_SPEED, gamePads, player.playerNumber);
                    handleGamepadCursorMoveInput(player.cursor, CURSOR_SPEED, gamePads, player.playerNumber);
                    player.cursor.update(pressedKeys,gamePads);
                    // Allow user to rotate image
                    if (inputMode.equals(INPUT_GAMEPADS)) {
                        if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.RIGHT_TRIGGER) && gamePads.get(player.playerNumber).triggerButtonClock.getElapsedTime() > KEY_DELAY) {
                            if (player.item.getRotation() >= 3 * Math.PI / 2) {
                                player.item.setRotation(0);    // prevent rotations past 2 PI
                            } else
                                player.item.setRotation(player.item.getRotation() + Math.PI / 2);
                            gamePads.get(player.playerNumber).triggerButtonClock.resetGameClock();
                            player.item.update(pressedKeys,gamePads);
                        }
                    } else if (pressedKeys.contains(KEY_R) && rKeyClock.getElapsedTime() > KEY_DELAY) {
                        if (player.item.getRotation() >= 3 * Math.PI / 2)
                            player.item.setRotation(0);    // prevent rotations past 2 PI
                        else
                            player.item.setRotation(player.item.getRotation() + Math.PI / 2);
                        rKeyClock.resetGameClock();
                        player.item.update(pressedKeys,gamePads);
                    }
                    // Preventing overlaps - image changes to imageName + "-error.png"
                    for (DisplayObjectContainer levelItem : levelContainer.getChildren()) {              // iterate over the sprites
                        DisplayObjectContainer DOCbeingPlaced = player.item;
                        if (!levelItem.getId().equals(DOCbeingPlaced.getId())) {                                  // if it's not itself
                            if (DOCbeingPlaced.getFileName().contains("-error") && levelItem.getFileName().contains("-error")) {
                                if (!levelItem.collidesWith(DOCbeingPlaced)) {                                     //if there NOT a collision
                                    DOCbeingPlaced.setImageNormal();
                                    levelItem.setImageNormal();
                                    break;
                                }
                            } else {
                                if (levelItem.collidesWith(DOCbeingPlaced)
                                        && !levelItem.getFileName().contains("beam")) {                             //if there IS a collision
                                    if (!DOCbeingPlaced.getFileName().contains("Dynamite")) {
                                        DOCbeingPlaced.setImageError();
                                        levelItem.setImageError();
                                        break;
                                    } else if (levelItem.getId().contains("platform1") || levelItem.getId().contains("platform2") || levelItem.getId().contains("portal")) {
                                        DOCbeingPlaced.setImageError();
                                        levelItem.setImageError();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    for (DisplayObjectContainer levelItem : overlay.getChildren()) {              // iterate over the sprites
                        DisplayObjectContainer DOCbeingPlaced = player.item;
                        if (!levelItem.getId().equals(DOCbeingPlaced.getId())) {                                  // if it's not itself
                            if (DOCbeingPlaced.getFileName().contains("-error") && levelItem.getFileName().contains("-error")) {
                                if (!levelItem.collidesWith(DOCbeingPlaced)) {                                     //if there NOT a collision
                                    DOCbeingPlaced.setImageNormal();
                                    levelItem.setImageNormal();
                                    break;
                                }
                            } else {
                                if (levelItem.collidesWith(DOCbeingPlaced)
                                        && !levelItem.getFileName().contains("beam")) {                             //if there IS a collision
                                    if (!DOCbeingPlaced.getFileName().contains("Dynamite")) {
                                        DOCbeingPlaced.setImageError();
                                        levelItem.setImageError();
                                        break;
                                    } else if (levelItem.getId().contains("platform1") || levelItem.getId().contains("platform2") || levelItem.getId().contains("portal")) {
                                        DOCbeingPlaced.setImageError();
                                        levelItem.setImageError();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (inputMode.equals(INPUT_GAMEPADS)) {
                        if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_A)) {     //if space is pressed
                            if (!player.item.getFileName().contains("-error")) {                // and placement is allowed
                                if(player.item.getFileName().contains("Dynamite")) {
                                    for(Iterator<DisplayObjectContainer> iterator = levelContainer.getChildren().iterator(); iterator.hasNext();) {
                                        DisplayObjectContainer levelItem = iterator.next();
                                        if(player.item.collidesWith(levelItem)) {
                                            if(levelItem.getFileName().contains("Gun")) {
                                                laserGunList.remove(levelItem);
                                            }
                                            if(levelItem.getFileName().contains("saw")) {
                                                gameplaySawbladeList.remove(levelItem);
                                            }
                                            iterator.remove();
                                            DynamiteSound.play(false);
                                            player.item.setPlaced(true);
                                        }
                                    }
                                }
                                player.item.setStartX(player.item.getxAbsolutePosition());
                                player.item.setStartY(player.item.getyAbsolutePosition());
                                player.item.setPlaced(true);
                                player.cursor.setVisible(false);
                                numberOfPlacedItems++;
                                if(numberOfPlacedItems >= numberOfPlayers) {
                                    gameMode = GameMode.GAMEPLAY;
                                    itemSelectionInitialized = false;
                                    GameClock gameClock = new GameClock();
                                    gameClock.resetGameClock();
                                    numberOfPlacedItems=0;
                                }
                            }
                        }
                    } else if (pressedKeys.contains(KEY_SPACE) && spaceKeyClock.getElapsedTime() > KEY_DELAY) {     //if space is pressed
                        if (!player.item.getFileName().contains("-error")) {// and placement is allowed
                            if(player.item.getFileName().contains("Dynamite")) {
                                for(Iterator<DisplayObjectContainer> iterator = levelContainer.getChildren().iterator(); iterator.hasNext();) {
                                    DisplayObjectContainer levelItem = iterator.next();
                                    if(player.item.collidesWith(levelItem)) {
                                        if(levelItem.getFileName().contains("Gun")) {
                                            laserGunList.remove(levelItem);
                                        }
                                        if(levelItem.getFileName().contains("saw")) {
                                            gameplaySawbladeList.remove(levelItem);
                                        }
                                        iterator.remove();
                                        DynamiteSound.play(false);
                                        player.item.setPlaced(true);
                                    }
                                }
                            }
                            player.item.setStartX(player.item.getxAbsolutePosition());
                            player.item.setStartY(player.item.getyAbsolutePosition());
                            player.item.setPlaced(true);
                            numberOfPlacedItems++;
                            if(numberOfPlacedItems >= numberOfPlayers) {
                                gameMode = GameMode.GAMEPLAY;
                                itemSelectionInitialized = false;
//                                GameClock gameClock = new GameClock();
//                                gameClock.resetGameClock();
//                                while(gameClock.getElapsedTime() < 200){continue;}      //wait 200ms to prevent placement
                                numberOfPlacedItems=0;
                                levelContainer.setAllChildrenImagesNormal();
                            }
                            spaceKeyClock.resetGameClock();
                        }
                    }
                }
            }
        }

    private void gameplayUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads){
        if(levelContainer != null){
            levelContainer.update(pressedKeys, gamePads);
            shootGuns(pressedKeys,gamePads);
            movePlatforms(100,pressedKeys,gamePads,true);
            spinSawblades(true);
        }
        for(Player player : players) {
            if(player.isAlive()){
                handleAnimation(player,pressedKeys,gamePads);
                player.animate();
            }
            player.update(pressedKeys,gamePads);
            if(player.isAlive() && !player.isCourseCompleted()) handlePlayerMoveInput(player, pressedKeys, gamePads);
            player.constrainToLevel(GAME_WIDTH,GAME_HEIGHT);
            player.fallOffPlatforms(player.platformPlayerIsOn);
            for (DisplayObjectContainer object : levelContainer.getChildren()) {
                if(player.collidesWith(object)) {
                    if(object.getId().equals("portal")){
                        player.dispatchEvent(new Event(Event.GOAL, player));
                        if(firstCompleted == null) {
                            firstCompleted = player;
                        }
                    }
                }
            }
            if(inputMode.equals(INPUT_GAMEPADS)) {
                // hold B to commit suicide
                if (player.isAlive() && !player.isCourseCompleted()) {
                    if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_B)){
                        if(gamePads.get(player.playerNumber).bButtonClock.getElapsedTime() > 1500) {
                            player.dispatchEvent(new Event(player,null,Event.UNSAFE_COLLISION));
                        }
                    } else {
                        gamePads.get(player.playerNumber).bButtonClock.resetGameClock();
                    }
                }
            } else if(pressedKeys.contains(KEY_ESC)) player.dispatchEvent(new Event(player,null,Event.UNSAFE_COLLISION));
        }
        int dead = 0;
        int done = 0;
        for(Player player : players) {
            if (!player.isAlive()) dead++;
            if (player.isCourseCompleted()) done++;
        }
        playersDead = dead;
        playersCompleted = done;
        if((playersDead + playersCompleted) >= numberOfPlayers && TweenJuggler.getInstance().tweensComplete()) {
            gameMode = GameMode.ROUND_COMPLETE;
            for(Sprite beam : laserBeams) {
                beam.setVisible(false);
                beam.setHitbox(new Rectangle(0,0,0,0));
                beam.setPosition(-100,-100);    //TODO maybe figure out the right way to do this
            }
            laserBeams.clear();
            roundCompleteClock.resetGameClock();
            if(playersCompleted == 0) {
                roundsSinceLevelCompleted++;
            }
        }
    }

    private void instructionsUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads){
        for(Player player : players) {
            if(inputMode.equals(INPUT_GAMEPADS)) {
                if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_B)) gameMode = GameMode.LEVEL_SELECTION;
            } else {
                if(pressedKeys.contains(KEY_SPACE)) gameMode = GameMode.LEVEL_SELECTION;
            }
        }
    }

    // METHODIZED UPDATE SEGMENTS

    private void handleCursorMoveInput(DisplayObject displayObject, int speed, ArrayList<Integer> pressedKeys) {
        Rectangle hitbox = displayObject.getHitbox();
        if (pressedKeys.contains(KEY_UP) && !(hitbox.y - speed < 0)) {
            displayObject.setyPosition(displayObject.getyPosition() - speed);
        } else if (pressedKeys.contains(KEY_DOWN) && !(hitbox.y + hitbox.height + speed + 50 > GAME_HEIGHT)) {
            displayObject.setyPosition(displayObject.getyPosition() + speed);
        }
        if (pressedKeys.contains(KEY_LEFT) && !(hitbox.x - speed < 0)) {
            displayObject.setxPosition(displayObject.getxPosition() - speed);
        } else if (pressedKeys.contains(KEY_RIGHT) && !(hitbox.x + hitbox.width + speed > GAME_WIDTH)) {
            displayObject.setxPosition(displayObject.getxPosition() + speed);
        }
    }

    private void handleGamepadCursorMoveInput(DisplayObject displayObject, int speed,ArrayList<GamePad> gamePads, int playerNumber) {
        Rectangle hitbox = displayObject.getHitbox();
        if(inputMode.equals(INPUT_GAMEPADS)) {
            double leftStickYAxis = gamePads.get(playerNumber).getLeftStickYAxis();
            double leftStickXAxis = gamePads.get(playerNumber).getLeftStickXAxis();
            if (leftStickYAxis < 0  && !(hitbox.y - speed < 0)) {
                displayObject.setyPosition(displayObject.getyPosition() - Math.abs((int)(speed*leftStickYAxis)));
            } else if (leftStickYAxis > 0  && !(hitbox.y + hitbox.height + speed + 2> GAME_HEIGHT)) {
                displayObject.setyPosition(displayObject.getyPosition() + Math.abs((int)(speed*leftStickYAxis)));
            }
            if (leftStickXAxis < 0  && !(hitbox.x - speed < 0)) { //Left
                displayObject.setxPosition(displayObject.getxPosition() - Math.abs((int)(speed*leftStickXAxis)));
            } else if (leftStickXAxis > 0  && !(hitbox.x + hitbox.width + speed > GAME_WIDTH)) { //Right
                displayObject.setxPosition(displayObject.getxPosition() + Math.abs((int)(speed*leftStickXAxis)));
            }
        }
    }

    private void handlePlayerMoveInput(Player player, ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        if(inputMode.equals(INPUT_GAMEPADS)) {
            if (gamePads.get(player.playerNumber).getLeftStickXAxis() < 0) { //Left
                player.setxPosition(player.getxPosition() - PLAYER_SPEED);
            } else if (gamePads.get(player.playerNumber).getLeftStickXAxis() > 0) { //Right
                player.setxPosition(player.getxPosition() + PLAYER_SPEED);
            }
            if (gamePads.get(player.playerNumber).isButtonPressed(GamePad.BUTTON_A)) {
                if(!player.isAirborne()) {
                    player.setAirborne(true);
                    player.setyVelocity(-JUMP_SPEED);
                    jumpSound.play(false);
                    player.hoverClock.resetGameClock();
                } else if(player.getyVelocity() > 0 && player.hoverClock.getElapsedTime() < FLOATTIME) {
                    player.setyVelocity(player.getyVelocity()-1);
                }
            }
        } else if(pressedKeys.contains(KEY_LEFT)){
            player.setxPosition(player.getxPosition()-PLAYER_SPEED);
        }
        else if(pressedKeys.contains(KEY_RIGHT)){
            player.setxPosition(player.getxPosition()+PLAYER_SPEED);
        }
        if(pressedKeys.contains(KEY_UP)) {
            if(!player.isAirborne()) {
                player.setAirborne(true);
                player.setyVelocity(-JUMP_SPEED);
                jumpSound.play(false);
                player.hoverClock.resetGameClock(); //This line keeps the amount of time hovering equivalent, removes random aspect, do we want this?
            } else if(player.getyVelocity() > 0 && player.hoverClock.getElapsedTime() < FLOATTIME) {
                player.setyVelocity(player.getyVelocity()-1);
            }
        }
    }

    private void handleAnimation(Player player, ArrayList<Integer> pressedKeys, ArrayList<GamePad> gamePads) {
        if (player.isCourseCompleted()) {
            player.setAnimation(AnimatedSprite.DANCING_ANIMATION);
        } else if (inputMode.equals(INPUT_GAMEPADS)) {
            if (gamePads.get(player.playerNumber).getLeftStickXAxis() < 0) {
                player.setRight(false);
                if (player.isAirborne()) {
                    if (player.getyVelocity() > 0) {
                        player.setAnimation(AnimatedSprite.FALLING_ANIMATION);
                    } else {
                        player.setAnimation(AnimatedSprite.JUMP_ANIMATION);
                    }
                } else {
                    player.setAnimation(AnimatedSprite.WALK_ANIMATION);
                }
            } else if (gamePads.get(player.playerNumber).getLeftStickXAxis() > 0) {
                player.setRight(true);
                if (player.isAirborne()) {
                    if (player.getyVelocity() > 0) {
                        player.setAnimation(AnimatedSprite.FALLING_ANIMATION);
                    } else {
                        player.setAnimation(AnimatedSprite.JUMP_ANIMATION);
                    }
                } else {
                    player.setAnimation(AnimatedSprite.WALK_ANIMATION);
                }
            } else if (player.isAirborne()) {
                if (player.getyVelocity() > 0) {
                    player.setAnimation(AnimatedSprite.FALLING_ANIMATION);
                } else {
                    player.setAnimation(AnimatedSprite.JUMP_ANIMATION);
                }
            } else {
                player.setAnimation(AnimatedSprite.IDLE_ANIMATION);
            }

        } else if (pressedKeys.contains(KEY_RIGHT)) {
            player.setRight(true);
            if (player.isAirborne()) {
                if (player.getyVelocity() > 0) {
                    player.setAnimation(AnimatedSprite.FALLING_ANIMATION);
                } else {
                    player.setAnimation(AnimatedSprite.JUMP_ANIMATION);
                }
            } else {
                player.setAnimation(AnimatedSprite.WALK_ANIMATION);
            }

        } else if (pressedKeys.contains(KEY_LEFT)) {
            player.setRight(false);
            if (player.isAirborne()) {
                if (player.getyVelocity() > 0) {
                    player.setAnimation(AnimatedSprite.FALLING_ANIMATION);
                } else {
                    player.setAnimation(AnimatedSprite.JUMP_ANIMATION);
                }
            } else {
                player.setAnimation(AnimatedSprite.WALK_ANIMATION);
            }

        } else if (player.isAirborne()) {
            if (player.getyVelocity() > 0) {
                player.setAnimation(AnimatedSprite.FALLING_ANIMATION);
            } else {
                player.setAnimation(AnimatedSprite.JUMP_ANIMATION);
            }
        } else {
            player.setAnimation(AnimatedSprite.IDLE_ANIMATION);
        }
    }


    private void shootGuns(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        if(frameCounter % 100 == 0) {
            for(Sprite gun : laserGunList) {
                LaserBeam beam = new LaserBeam("laserbeam" + gun.getId(),gun.getRotation());
                beam.dangerous = true;
                beam.setScale(.5,.5);
                //beam.setPosition((int) (gun.getUnscaledWidth()*.35/2 - beam.getScaledWidth()/2 + gun.getxAbsolutePosition()), (int) (gun.getUnscaledHeight()*.35/2 - beam.getScaledHeight()/2 + gun.getyAbsolutePosition()));
                beam.alignCenterVertical(gun);
                beam.alignCenterHorizontal(gun);
                beam.setPivotCenter();
                laserBeams.add(beam);
                levelContainer.addChild(beam);
                levelContainer.removeChild(gun);
                levelContainer.addChild(gun);
                laserSound.play(false);
            }
        }
        for(Iterator<LaserBeam> iterator = laserBeams.iterator(); iterator.hasNext();) {
            LaserBeam beam = iterator.next();
            beam.update(pressedKeys,gamePads);
            if(beam.direction == 0) beam.setxPosition(beam.getxPosition() - BEAM_SPEED);
            else if(beam.direction == Math.PI/2) beam.setyPosition(beam.getyPosition() - BEAM_SPEED);
            else if(beam.direction == Math.PI) beam.setxPosition(beam.getxPosition() + BEAM_SPEED);
            else if(beam.direction == 3*Math.PI/2) beam.setyPosition(beam.getyPosition() + BEAM_SPEED);

            if(beam.direction % Math.PI < 1) {
                if(beam.getRight() < 0) iterator.remove();
                else if(beam.getLeft() > GAME_WIDTH) iterator.remove();
            } else if(beam.direction % Math.PI/2 < 1) {
                if(beam.getBottom() < 0) iterator.remove();
                else if(beam.getTop() > GAME_HEIGHT) iterator.remove();
            }

        }
    }

    private void movePlatforms(int travelDistance, ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads, boolean gameplay){

        if(!gameplay) {
            for (Sprite platform : selectableSlidingPlatforms) {
                if (platform.isLeftRight()) {
                    if (platform.isSlidingPlatformDirection()) {
                        if(platform.getxAbsolutePosition() < platform.getStartX() + travelDistance) {
                            platform.setxPosition(platform.getxPosition() + SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                    } else {
                        if(platform.getxAbsolutePosition() > platform.getStartX() - travelDistance) {
                            platform.setxPosition(platform.getxPosition() - SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                    }
                    platform.update(pressedKeys, gamePads);
                }
                if(platform.isUpDown()) {
                    if (platform.isSlidingPlatformDirection()) {
                        if (platform.getyAbsolutePosition() > platform.getStartY() - travelDistance) {
                            platform.setyPosition(platform.getyPosition() - SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                    } else {
                        if (platform.getyAbsolutePosition() < platform.getStartY() + travelDistance) {
                            platform.setyPosition(platform.getyPosition() + SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                    }
                }
                    platform.update(pressedKeys, gamePads);
            }
        } else {
            for (Sprite platform : gameplaySlidingPlatforms) {
                if(platform.isLeftRight()) {
                    if (platform.isSlidingPlatformDirection()) {
                        if(platform.getxAbsolutePosition() < platform.getStartX() + travelDistance) {
                            platform.setxPosition(platform.getxPosition() + SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                        for (Player player : players) {
                            if (player.platformPlayerIsOn != null && player.isAlive()) {
                                if (player.platformPlayerIsOn.equals(platform)) {
                                    player.setxPosition(player.getxPosition() + SLIDING_PLATFORM_SPEED);
                                }
                            }
                        }
                    } else {
                        if(platform.getxAbsolutePosition() > platform.getStartX() - travelDistance) {
                            platform.setxPosition(platform.getxPosition() - SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                        for (Player player : players) {
                            if (player.platformPlayerIsOn != null && player.isAlive()) {
                                if (player.platformPlayerIsOn.equals(platform))
                                    player.setxPosition(player.getxPosition() - SLIDING_PLATFORM_SPEED);
                            }
                        }
                    }
                } if(platform.isUpDown()) {
                    if (platform.isSlidingPlatformDirection()) {
                        if (platform.getyAbsolutePosition() > platform.getStartY() - travelDistance) {
                            platform.setyPosition(platform.getyPosition() - SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                        for (Player player : players) {
                            if (player.platformPlayerIsOn != null && player.isAlive()) {
                                if (player.platformPlayerIsOn.equals(platform))
                                    player.setyPosition(player.getyPosition() - SLIDING_PLATFORM_SPEED);
                            }
                        }
                    } else {
                        if (platform.getyAbsolutePosition() < platform.getStartY() + travelDistance) {
                            platform.setyPosition(platform.getyPosition() + SLIDING_PLATFORM_SPEED);
                        } else {
                            platform.setSlidingPlatformDirection(!platform.isSlidingPlatformDirection());
                        }
                        for (Player player : players) {
                            if (player.platformPlayerIsOn != null && player.isAlive()) {
                                if (player.platformPlayerIsOn.equals(platform))
                                    player.setyPosition(player.getyPosition() + SLIDING_PLATFORM_SPEED);
                            }
                        }
                    }
                }


                platform.update(pressedKeys, gamePads);
            }
        }
    }

    private void spinSawblades(boolean gameplay) {
        if(!gameplay) {
            for (Sprite saw : selectableSawbladeList) {
                if (frameCounter % SAW_SPEED == 0) {
                    saw.setPivotCenter();
                    saw.setRotation(saw.getRotation() + .03);
                }
            }
        } else {
            for (Sprite saw : gameplaySawbladeList) {
                if (frameCounter % SAW_SPEED == 0) {
                    saw.setPivotCenter();
                    saw.setRotation(saw.getRotation() + .03);
                }
            }
        }
    }

    private void roundCompleteUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads){
        levelContainer.update(pressedKeys,gamePads);
        scoreboardBackground.removeChild(scoreboardHeader);
        for(Player player: players) {
            if(player.isAlive()){
                handleAnimation(player,pressedKeys,gamePads);
                player.animate();
            }
        }
        if(playersCompleted == numberOfPlayers && numberOfPlayers != 1) {
            scoreboardHeader = noPointsWide;
            scoreboardBackground.addChild(scoreboardHeader);
            scoreboardHeader.setxScale(.5);
            scoreboardHeader.setyScale(.5);
            scoreboardHeader.alignCenterHorizontal(scoreboardBackground);
        } else {
            scoreboardHeader = scoreTitle;
            scoreboardBackground.addChild(scoreboardHeader);
            scoreboardHeader.setxScale(.5);
            scoreboardHeader.setyScale(.5);
            scoreboardHeader.alignCenterHorizontal(scoreboardBackground);
        }

        if(!scoresCalculated) {
            for (Player player : players) {
                if (playersDead != numberOfPlayers && playersCompleted != numberOfPlayers) {
                    if (player.isCourseCompleted()) {
                        player.incrementScore(100);
                    }
                    if (firstCompleted.equals(player)) {
                        player.incrementScore(20);
                    }
                }
                if(numberOfPlayers == 1 && playersCompleted == 1) {
                    player.incrementScore(100);
                }
                scoreboardBackground.addChild(player.getHead());
                player.getScoreBar().setxPosition(150);
                player.getHead().setxPosition(100);
                player.getHead().setScale(.8,.8);
                player.getScoreBar().setyScale(1);
                player.getScoreBar().setxScale(0);
                player.getScoreBar().alignFractionVertical(scoreboardBackground, numberOfPlayers + 2, player.playerNumber + 2);
                player.getHead().alignFractionVertical(scoreboardBackground, numberOfPlayers + 2, player.playerNumber + 2);
                scoreboardHeader.alignFractionVertical(scoreboardBackground, numberOfPlayers + 2, 1);
                scoreboardBackground.update(pressedKeys,gamePads);
                if (!scoreboardBackground.getChildren().contains(player.getScoreBar()))
                    scoreboardBackground.addChild(player.getScoreBar());
//                if(player.getScore() > 1) {
                    if (player.playerNumber == 0) {
                        Tween score1 = new Tween(player1.getScoreBar(), new TweenTransition(TweenTransition.TransitionType.LINEAR));
                        score1.animate(TweenableParam.SCALE_X, 0, (double) player.getScore() / (double) WIN_SCORE, 100);
                        TweenJuggler.getInstance().add(score1);
                        player.getScoreBar().setVisible(true);
                    } else if (player.playerNumber == 1) {
                        Tween score2 = new Tween(player2.getScoreBar(), new TweenTransition(TweenTransition.TransitionType.LINEAR));
                        score2.animate(TweenableParam.SCALE_X, 0, (double) player.getScore() / (double) WIN_SCORE, 100);
                        TweenJuggler.getInstance().add(score2);
                        player.getScoreBar().setVisible(true);
                    } else if (player.playerNumber == 2) {
                        Tween score3 = new Tween(player3.getScoreBar(), new TweenTransition(TweenTransition.TransitionType.LINEAR));
                        score3.animate(TweenableParam.SCALE_X, 0, (double) player.getScore() / (double) WIN_SCORE, 100);
                        TweenJuggler.getInstance().add(score3);
                        player.getScoreBar().setVisible(true);
                    } else if (player.playerNumber == 3) {
                        Tween score4 = new Tween(player4.getScoreBar(), new TweenTransition(TweenTransition.TransitionType.LINEAR));
                        score4.animate(TweenableParam.SCALE_X, 0, (double) player.getScore() / (double) WIN_SCORE, 100);
                        TweenJuggler.getInstance().add(score4);
                        player.getScoreBar().setVisible(true);
                    }
//                }
                //player.sizeScoreBar(0);

                if(player.getScore() >= WIN_SCORE) {
                    gameWon = true;
                    gameOver = true;
                }
            }
            if(roundsCompleted < ROUND_COUNT) {
                roundsCompleted++;
            } else {
                gameOver = true;
            }
            if(gameOver) {
                int maxScore = 0;
                for(Player player : players) {
                    if(player.getScore() > maxScore) {
                        maxScore = player.getScore();
                        gameWinner = player.playerNumber;
                    }
                }
                gameWinner++;
                if(numberOfPlayers == 1 && maxScore < WIN_SCORE) {
                    gameWon = false;
                } else if(numberOfPlayers != 1 && maxScore == 1) {
                    gameWon = false;
                } else {
                    gameWon = true;
                }
            }
            scoresCalculated = true;
        }


        if(roundCompleteClock.getElapsedTime() > 5000) {
            for(Player player: players) {
                player.getScoreBar().setVisible(false);
            }
            if(!gameOver) {
                if(roundsCompleted == ROUND_COUNT || roundsCompleted == ROUND_COUNT - 1 || roundsCompleted == ROUND_COUNT - 2) {
                    gameMode = GameMode.FEEDBACK;
                    feedbackClock.resetGameClock();
                } else {
                    gameMode = GameMode.ITEM_SELECTION;
                    resetPlayers(pressedKeys, gamePads);
                    levelContainer.update(pressedKeys, gamePads);
                }
            } else {
                gameMode = GameMode.GAME_COMPLETE;
                gameCompleteClock.resetGameClock();
            }
        }
    }

    private void gameCompleteUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        if(gameWon) {
            Sprite winnerTitle = new Sprite("winner", "wins" + gameWinner + ".png");
            gameOverBackground.addChild(winnerTitle);
            //winnerTitle.setPosition(100,100);
            winnerTitle.setScale(0.6,0.6);
            winnerTitle.alignCenterHorizontal(gameOverBackground);
            winnerTitle.alignCenterVertical(gameOverBackground);
        } else {
            Sprite noWinnerTitle = new Sprite("noWinner", "noonewins.png");
            gameOverBackground.addChild(noWinnerTitle);
            noWinnerTitle.setScale(0.6,0.6);
            noWinnerTitle.alignCenterHorizontal(gameOverBackground);
            noWinnerTitle.alignCenterVertical(gameOverBackground);
        }
        if(gameCompleteClock.getElapsedTime() > 5000) {
            resetGame(pressedKeys, gamePads);

            gameMode = GameMode.LEVEL_SELECTION;
        }

    }

    private void feedbackUpdate(ArrayList<Integer> pressedKeys,ArrayList<GamePad> gamePads) {
        if(roundsCompleted == ROUND_COUNT) {
            gameOverBackground.addChild(lastRound);
            lastRound.setScale(0.6,0.6);
            lastRound.alignCenterHorizontal(gameOverBackground);
            lastRound.alignCenterVertical(gameOverBackground);
        } else if (roundsCompleted == ROUND_COUNT - 1) {
            gameOverBackground.addChild(penultimateRound);
            penultimateRound.setScale(0.6,0.6);
            penultimateRound.alignCenterHorizontal(gameOverBackground);
            penultimateRound.alignCenterVertical(gameOverBackground);
        } else if (roundsCompleted == ROUND_COUNT - 2) {
            gameOverBackground.addChild(thirdToLast);
            thirdToLast.setScale(0.6,0.6);
            thirdToLast.alignCenterHorizontal(gameOverBackground);
            thirdToLast.alignCenterVertical(gameOverBackground);
        }
        if(feedbackClock.getElapsedTime() > 5000) {
            gameMode = GameMode.ITEM_SELECTION;
            resetPlayers(pressedKeys, gamePads);
            levelContainer.update(pressedKeys, gamePads);
            gameOverBackground.removeAll();
        }
    }

	@Override
	public void draw(Graphics g){
		super.draw(g);
		if(currentLevel != null) currentLevel.getBackground().draw(g);
        if(gameMode != null) {
            switch(gameMode) {
                case LEVEL_SELECTION:
                    levelSelectionDraw(g);
                    break;
                case ITEM_SELECTION:
                    itemSelectionDraw(g);
                    break;
                case ITEM_PLACEMENT:
                    itemPlacementDraw(g);
                    break;
                case GAMEPLAY:
                    gameplayDraw(g);
                    break;
                case MAIN_MENU:
                    break;
                case ROUND_COMPLETE:
                    roundCompleteDraw(g);
                    break;
                case GAME_COMPLETE:
                    gameCompleteDraw(g);
                    break;
                case FEEDBACK:
                    feedbackDraw(g);
                    break;
                case INSTRUCTIONS:
                    instructionsDraw(g);
                    break;
            }
        }
	}

	private void levelSelectionDraw(Graphics g) {
        if(frameCounter > 3) {
            levelSelectionBackground.draw(g);
            levelImages.draw(g);
            gameTitle.draw(g);
            controlsBackground.draw(g);
            controlsText.draw(g);
            for(Player player : players) {
                player.cursor.draw(g);
            }

        }
    }

    private void itemSelectionDraw(Graphics g) {
        if(levelContainer != null) {
            Rectangle test;
            levelContainer.draw(g);
            overlay.draw(g);
            selectionBackground.draw(g);
            for(Player player : players) {
                player.cursor.draw(g);
                if(debugHitboxes) {
                    test = player.cursor.getHitbox();
                    g.fillRect(test.x, test.y, test.width, test.height);
                    for(DisplayObject displayObject : selectionBackground.getChildren()){
                        test = displayObject.getHitbox();
                        g.fillRect(test.x, test.y, test.width, test.height);
                    }
                    for(DisplayObjectContainer c : levelContainer.getChildren()) {
                        test = c.getHitbox();
                        g.fillRect(test.x, test.y, test.width, test.height);
                    }
                }
            }
        }
    }

    private void itemPlacementDraw(Graphics g) {
	    if(levelContainer != null) {
	        levelContainer.draw(g);
	        overlay.draw(g);
            for(Player player : players) {
                player.cursor.draw(g);
            }
        }


        if(debugHitboxes) {
            for(DisplayObjectContainer c : levelContainer.getChildren()) {
                Rectangle test = c.getHitbox();
                g.fillRect(test.x, test.y, test.width, test.height);
            }
        }
    }

    private void gameplayDraw(Graphics g) {
        if(levelContainer != null) {
            levelContainer.draw(g);
            for(Player player : players) {
                if(player.isVisible()) player.draw(g);
            }
        }

        if(debugHitboxes) {
            for(DisplayObjectContainer c : levelContainer.getChildren()) {
                Rectangle test = c.getHitbox();
                g.fillRect(test.x, test.y, test.width, test.height);
            }
            for(Player p : players) {
                Rectangle test = p.getHitbox();
                g.fillRect(test.x, test.y, test.width, test.height);
            }
            for(LaserBeam b : laserBeams) {
                Rectangle test = b.getHitbox();
                g.fillRect(test.x, test.y, test.width, test.height);
            }
        }
    }

    private void roundCompleteDraw(Graphics g) {
        if(levelContainer != null) {
            levelContainer.draw(g);
            for(Player player : players) {
                if(player.isVisible()) player.draw(g);
            }
            scoreboardBackground.draw(g);
        }
    }

    private void gameCompleteDraw(Graphics g) {
        if(levelContainer != null) {
            levelContainer.draw(g);
        }
        gameOverBackground.draw(g);
    }

    private void feedbackDraw(Graphics g) {
        if(levelContainer != null) {
            levelContainer.draw(g);
        }
        gameOverBackground.draw(g);
    }

    private void instructionsDraw(Graphics g) {
        if(instructions != null) instructions.draw(g);
    }

    private void wait(int milliseconds) {
	    GameClock clock = new GameClock();
	    clock.resetGameClock();
	    while(clock.getElapsedTime() < milliseconds) {
	        //do nothing
        }
    }

	public static void main(String[] args) {
		TheMinorsGame game = new TheMinorsGame();
		game.start();

	}
}
