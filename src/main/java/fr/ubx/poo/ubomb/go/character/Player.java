/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.engine.GameEngine;
import fr.ubx.poo.ubomb.game.*;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.view.SpriteFactory;

import java.io.IOException;


public class Player extends GameObject implements Movable {

    private Direction direction;
    private boolean moveRequested = false;
    private int levels = 1;
    private int lives;
    private int keys = game.key;
    private int bombs = game.bombBagCapacity;
    private int bombRange = 1;
    Grid grid = game.getGrid();


    public Player(Game game, Position position, int lives) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = lives;
    }

    public int getLives() {
        return this.lives;
    }

    public int getKeys() {
        return this.keys;
    }

    public int getBombs() {
        return this.bombs;
    }

    public int getBombRange(){
        return this.bombRange;
    }

    public int getLevels(){
        return this.levels;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if(nextPos.getX() < 0 || nextPos.getY() < 0 || nextPos.getY() > grid.getHeight() - 1 || nextPos.getX() > grid.getWidth() - 1) {
            return false;
        }
        for (Decor decor : grid.values()) {
            if(grid.get(nextPos) == decor && !decor.isWalkable(game.getPlayer())){
                return false;
            }
        }
        return true;

    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public void doMove(Direction direction) {
        // Check if we need to pick something up
        Position nextPos = direction.nextPosition(getPosition());
        takeKey();
        takeDamage();
        takeHeart();
        takeBombs();
        setPosition(nextPos);
    }

    @Override
    public boolean isWalkable(Player player) {
        return true;
    }

    @Override
    public void explode() {

    }

    // Example of methods to define by the player


    public void takeDoor(int gotoLevel) throws IOException {
        Position nextPos = direction.nextPosition(getPosition());
        if(grid.get(nextPos) != null){
            DoorPrevOpened doorPrevOpened = new DoorPrevOpened(nextPos);
            DoorNextOpened doorNextOpened = new DoorNextOpened(nextPos);
            DoorNextClosed doorNextClosed = new DoorNextClosed(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == doorPrevOpened.getClass()){
                GridRepo gridRepo = new GridRepoFile(game);
                this.grid = gridRepo.load(gotoLevel, "level" + gotoLevel);
            }
        }
    }

    public void takeDamage(){
        Position nextPos = direction.nextPosition(getPosition());
        if(grid.get(nextPos) != null){
            Monster monster = new Monster(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == monster.getClass()){
                lives = lives -1;
            }
        }
    }

    public void takeKey() {
        Position nextPos = direction.nextPosition(getPosition());
        if(grid.get(nextPos) != null){
            Key key = new Key(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == key.getClass()){
                keys = keys + 1;
                grid.get(nextPos).remove();
            }
        }
    }

    public void takeHeart() {
        Position nextPos = direction.nextPosition(getPosition());
        if(grid.get(nextPos) != null){
            Heart heart = new Heart(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == heart.getClass()){
                lives = lives + 1;
                grid.get(nextPos).remove();
            }
        }
    }

    public void takeBombs() {
        Position nextPos = direction.nextPosition(getPosition());
        if(grid.get(nextPos) != null){
            BombNumberInc bombNumberInc = new BombNumberInc(nextPos);
            BombNumberDec bombNumberDec = new BombNumberDec(nextPos);
            BombRangeInc bombRangeInc = new BombRangeInc(nextPos);
            BombRangeDec bombRangeDec = new BombRangeDec(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == bombNumberInc.getClass()){
                bombs = bombs + 1;
                grid.get(nextPos).remove();
            }
            if(n == bombNumberDec.getClass()){
                bombs = bombs - 1;
                grid.get(nextPos).remove();
            }
            if(n == bombRangeInc.getClass()){
                bombRange = bombRange + 1;
                grid.get(nextPos).remove();
            }
            if(n == bombRangeDec.getClass()){
                bombRange = bombRange - 1;
                grid.get(nextPos).remove();
            }
        }
    }

    public boolean isWinner() {
        Position nextPos = direction.nextPosition(getPosition());
        if(grid.get(nextPos) != null){
            Princess princess = new Princess(nextPos);
            Class<?>n = grid.get(nextPos).getClass();
            if(n == princess.getClass()){
                return true;
            }
        }
        return false;
    }
}
