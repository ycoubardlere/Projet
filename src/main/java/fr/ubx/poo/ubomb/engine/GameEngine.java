/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.engine;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Grid;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.view.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;


    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.stage = stage;
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.getGrid().getHeight();
        int width = game.getGrid().getWidth();
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        // Create sprites
        for (Decor decor : game.getGrid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
        }
        sprites.add(new SpritePlayer(layer, player));
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);
                createNewBombs(now);
                checkCollision(now);
                checkExplosions();
                pushBox(now);

                // Graphic update
                cleanupSprites();
                render();
                statusBar.update(game);
            }
        };
    }

    private void checkExplosions() {

    }

    private void createNewBombs(long now) {

    }

    private void checkCollision(long now) {
    }

    public void pushBox(long now) {
        Direction direction = player.getDirection();
        Grid grid = game.getGrid();
        Position nextPos = direction.nextPosition(player.getPosition());
        Position nextNextPos = direction.nextPosition(nextPos);
        if(grid.get(nextPos) != null){
            Box box = new Box(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == box.getClass() && grid.get(nextNextPos) == null){
                grid.get(nextPos).remove();
                grid.set(nextNextPos, box);
                sprites.add(SpriteFactory.create(layer, box));
                box.setModified(true);
            }
        }
    }


    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
            input.clear();
        } else if (input.isBomb()){
            dropBombs(now);
        }
        input.clear();
    }

    public void dropBombs(long now){
        long time = System.currentTimeMillis();
        System.out.println(time - now);
        Position pos = player.getPosition();
        Grid grid = game.getGrid();
        Bomb_0 bomb_0 = new Bomb_0(pos);
        Bomb_1 bomb_1 = new Bomb_1(pos);
        Bomb_2 bomb_2 = new Bomb_2(pos);
        Bomb_3 bomb_3 = new Bomb_3(pos);
        if(grid.get(pos) == null){
            for(int i = 3; i >= 0; i-- ){
                if(i == 3){
                    grid.set(pos, bomb_3);
                    sprites.add(SpriteFactory.create(layer, bomb_3));
                }
                if(i == 2){
                    grid.set(pos, bomb_2);
                    sprites.add(SpriteFactory.create(layer, bomb_2));
                }
                if(i == 1){
                    grid.set(pos, bomb_1);
                    sprites.add(SpriteFactory.create(layer, bomb_1));
                }
                if(i == 0){
                    grid.set(pos, bomb_1);
                    sprites.add(SpriteFactory.create(layer, bomb_1));
                }
            }
        }
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);

        if (player.getLives() == 0) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }

        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                game.getGrid().remove(sprite.getPosition());
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }
}
