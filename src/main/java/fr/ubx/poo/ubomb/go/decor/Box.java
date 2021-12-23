package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Grid;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class Box extends Decor {
    public Box(Position position) {
        super(position);
    }

    @Override
    public boolean isWalkable(Player player) {
        Game game = player.game;
        Grid grid = game.getGrid();
        Position pos = getPosition();
        Position nextPos = player.getDirection().nextPosition(pos);
        Position nextNextPos = player.getDirection().nextPosition(nextPos);
        if(grid.get(nextNextPos)==null){
            return true;
        }
        return false;
    }
}
