package fr.ubx.poo.ubomb.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import static fr.ubx.poo.ubomb.game.EntityCode.*;

public class GridRepoFile extends GridRepo {

    public GridRepoFile(Game game) {
        super(game);
    }

    @Override
    public final Grid load(int level, String name) throws IOException {
        int height = 0;
        int width = 0;
            // Créer l'objet File Reader
            FileReader fr = new FileReader("/Users/yoann/Downloads/UBomb21-student/src/main/resources/sample/" + name + ".txt");
            // Créer l'objet BufferedReader
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;
            while((line = br.readLine()) != null)
            {
                // ajoute la ligne au buffer
                sb.append(line);
                height = height + 1;
            }
            fr.close();
            width = sb.length()/height;
            Grid grid = new Grid(width, height);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Position position = new Position(j, i);
                    EntityCode entityCode = fromCode(sb.charAt(i*width + j));
                    grid.set(position, processEntityCode(entityCode, position));
                }
            }
        return grid;
    }

    private final EntityCode[][] getEntities(String name) {
        try {
            Field field = this.getClass().getDeclaredField(name);
            return (EntityCode[][]) field.get(this);
        } catch (IllegalAccessException e) {
            return null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
