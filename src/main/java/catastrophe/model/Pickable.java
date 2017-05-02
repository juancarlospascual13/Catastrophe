package main.java.catastrophe.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by victorperez on 14/04/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class Pickable extends IdClass {
    private Waypoint position;

    public Pickable(String id) {
        super(id);
    }

    public boolean atDump(){
        return this.getPosition().isDump();
    }

}
