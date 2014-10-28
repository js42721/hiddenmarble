package com.mygdx.hiddenmarble.ui;

import static com.mygdx.hiddenmarble.ui.HiddenMarble.ACCEL_MULTIPLIER;
import static com.mygdx.hiddenmarble.ui.HiddenMarble.BOX2D_SCALE;
import static com.mygdx.hiddenmarble.ui.HiddenMarble.HEIGHT;
import static com.mygdx.hiddenmarble.ui.HiddenMarble.SAVE;
import static com.mygdx.hiddenmarble.ui.HiddenMarble.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.hiddenmarble.utils.Assets;
import com.mygdx.hiddenmarble.utils.SaveState;
import com.mygdx.hiddenmarble.world.GameWorld;
import com.mygdx.hiddenmarble.world.GameWorldListener;
import com.mygdx.hiddenmarble.world.GameWorldAdapter;

/** Displays the game and adds a UI overlay. */
public class GameScreen extends ScreenAdapter implements Screen {   
    /** The duration of the cover fade animation in seconds. */
    private static final float FADE_TIME = 2.0f;
    
    private final HiddenMarble game;
    private final SaveState save;
    private final Camera camera;
    private final Viewport viewport;
    private final Stage stage;
    private final StateMachine<GameScreen> stateMachine;
    private final GameWorldListener worldListener;
    private final Vector2 gravity;
    private final float scaledWidth;
    private final float scaledHeight;
    
    private GameWorld world;
    private WorldRenderer renderer;
    
    public GameScreen(HiddenMarble game) {
        this.game = game;
        
        scaledWidth = WIDTH * BOX2D_SCALE;
        scaledHeight = HEIGHT * BOX2D_SCALE;
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(scaledWidth, scaledHeight, camera);
        
        stage = new Stage(new FitViewport(WIDTH, HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);
        
        gravity = new Vector2();
        
        stateMachine = new DefaultStateMachine<GameScreen>(this, UIState.PLAY);
        
        worldListener = new GameWorldAdapter() {
            @Override
            public void mazeSolved() {
                if (stateMachine.getCurrentState() != UIState.GAVE_UP) {
                    stateMachine.changeState(UIState.WIN);
                }
            }
        };
        
        save = new SaveState(SAVE);
        if (!load(save)) {
            startNewGame();
        }
    }

    @Override
    public void render(float delta) {        
        /* Gets the accelerometer readings. */
        float accelX = Gdx.input.getAccelerometerX();
        float accelY = Gdx.input.getAccelerometerY();
        gravity.set(accelX, accelY).scl(ACCEL_MULTIPLIER);
        
        /* Updates the physics. */
        world.update(delta, gravity);
        
        /* Draws the game. */
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        renderer.render(delta, game.batch);
        game.batch.end();

        /* Draws the UI overlay. */
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        game.batch.getProjectionMatrix().setToOrtho2D(0.0f, 0.0f, width, height);
        viewport.update(width, height);
        stage.getViewport().update(width, height);
    }
    
    @Override
    public void pause() {
        Assets.stopSound();
        save(save);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        world.dispose();
    }
    
    private void startNewGame() {
        Assets.stopSound();
        save.clear();
        save.erase();
        world = new GameWorld(scaledWidth, scaledHeight);
        renderer = new WorldRenderer(world);
        world.addListener(renderer);
        world.addListener(worldListener);
        stateMachine.changeState(UIState.PLAY);
    }

    private void save(SaveState save) {
        save.setWorld(world);
        UIState state = (UIState)stateMachine.getCurrentState();
        String stateName = state.name();
        save.setUIState(stateName);
        save.save();
    }
    
    private boolean load(SaveState save) {
        if (!save.load()) {
            return false;
        }

        GameWorld savedWorld = save.getWorld();
        if (savedWorld == null) {
            return false;
        }
        
        String stateString = save.getUIState();
        UIState state;
        try {
            state = UIState.valueOf(stateString);
        } catch (Exception e) {
            return false;
        }

        world = savedWorld;
        world.resize(scaledWidth, scaledHeight);
        renderer = new WorldRenderer(world);
        world.addListener(renderer);
        world.addListener(worldListener);

        switch (state) {
        case CONFIRM_GIVE_UP:
            state = UIState.PLAY; // Clears the dialog.
            break;
        case GAVE_UP:
        case WIN:
            revealMaze(0.0f); // Skips the fade animation.
            break;
        default:
            break;
        }
        
        stateMachine.changeState(state);
        
        Assets.stopSound();
        
        return true;
    }
    
    private void revealMaze(float fadeDuration) {
        renderer.setRevealed(true, fadeDuration);
    }
    
    private void showNextButton() {
        stage.addActor(UIButton.NEXT.get(this));
    }
    
    private void showGiveUpButton() {
        stage.addActor(UIButton.GIVE_UP.get(this));
    }
    
    private void showGiveUpConfirmation() {
        float pad = 8.0f;
        Table table = new Table(Assets.uiSkin);
        
        String text = "Are you sure you want to give up?";
        Label label = new Label(text, Assets.uiSkin);
        table.add(label)
        .pad(pad)
        .row();
        
        table.add(UIButton.YES.get(this))
        .size(UIButton.YES.width, UIButton.YES.height)
        .pad(pad)
        .row();

        table.add(UIButton.NO.get(this))
        .size(UIButton.NO.width, UIButton.NO.height)
        .pad(pad)
        .row();
        
        table.setFillParent(true);
        stage.addActor(table);
    }
    
    private void showWinMessage() {
        String text = "Congratulations! You recovered the hidden marble!";
        Label label = new Label(text, Assets.uiSkin);
        label.setCenterPosition(WIDTH / 2.0f, HEIGHT / 2.0f);
        stage.addActor(label);
    }
    
    private void showStartIndicator() {
        Table table = new Table(Assets.uiSkin);
        
        String text = "<- START";
        Label label = new Label(text, Assets.uiSkin);
        label.setWrap(true);
        label.setColor(0.5f, 1.0f, 0.0f, 1.0f);
        
        Vector2 start = world.getMarbleStart();
        start.scl(1.0f / BOX2D_SCALE);
        table.add(label);
        table.setPosition(start.x, start.y);
        
        table.setFillParent(true);
        stage.addActor(table);
    }
    
    private void playWinSound() {
        Assets.magic.play(0.5f);
    }
    
    private void clearUI() {
        stage.clear();
    }
    
    private enum UIState implements State<GameScreen> {
        PLAY {
            @Override
            public void enter(GameScreen screen) {
                screen.showGiveUpButton();
            }
        },
        
        CONFIRM_GIVE_UP {
            @Override
            public void enter(GameScreen screen) {
                screen.showGiveUpConfirmation();
            }
        },
        
        WIN {
            @Override
            public void enter(GameScreen screen) {
                screen.revealMaze(FADE_TIME);
                screen.showNextButton();
                screen.showStartIndicator();
                screen.showWinMessage();
                screen.playWinSound();
            }
        },
        
        GAVE_UP {
            @Override
            public void enter(GameScreen screen) {
                screen.revealMaze(FADE_TIME);
                screen.showNextButton();
                screen.showStartIndicator();
            }
        };

        @Override
        public void exit(GameScreen screen) {
            screen.clearUI();
        }
        
        @Override
        public void update(GameScreen screen) {
        }

        @Override
        public boolean onMessage(Telegram telegram) {
            return false;
        }
    }
    
    private interface ButtonListener {
        void clicked(GameScreen screen);
    }
    
    private enum UIButton implements ButtonListener {
        NEXT("Next", 100.0f, 42.0f, WIDTH - 120.0f, 20.0f) {
            @Override
            public void clicked(GameScreen screen) {
                screen.startNewGame();
                screen.stateMachine.changeState(UIState.PLAY);
            }
        },
        
        GIVE_UP("I give up!", 100.0f, 42.0f, WIDTH / 2.0f - 50.0f, 20.0f) {
            @Override
            public void clicked(GameScreen screen) {
                screen.stateMachine.changeState(UIState.CONFIRM_GIVE_UP);
            }
        },
        
        YES("Yes", 100.0f, 42.0f) {
            @Override
            public void clicked(GameScreen screen) {
                screen.stateMachine.changeState(UIState.GAVE_UP);
            }
        },
        
        NO("No", 100.0f, 42.0f) {
            @Override
            public void clicked(GameScreen screen) {
                screen.stateMachine.revertToPreviousState();
            }
        };
        
        final String text;
        final float width;
        final float height;
        final float x;
        final float y;
       
        UIButton(String text, float width, float height) {
            this(text, width, height, 0.0f, 0.0f);
        }
        
        UIButton(String text, float width, float height, float x, float y) {
            this.text = text;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }
        
        Button get(final GameScreen screen) {
            Button button = new TextButton(text, Assets.uiSkin);
            button.setSize(width, height);
            button.setPosition(x, y);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    UIButton.this.clicked(screen);
                }
            });
            return button;
        }
    }
}
