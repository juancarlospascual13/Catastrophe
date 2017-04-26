package eu.atanasio;

import lombok.Data;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
public class Pickable {
    private Waypoint position;
    private String name;

    public Pickable(String name) {
        this.name = name;
    }

    public boolean atDump(){
        return this.getPosition().isDump();
    }
}
