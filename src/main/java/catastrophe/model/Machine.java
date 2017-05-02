package main.java.catastrophe.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by victorperez on 14/04/17.
 */
@NoArgsConstructor
@Getter
@Setter
public class Machine extends Pickable {
    private boolean broken;

    public Machine(String id) {
        super(id);
        this.broken = false;
    }

    public void move (Waypoint waypoint) {
        if (this.getPosition().getConnectedWaypoints().contains(waypoint))
            this.setPosition(waypoint);
    }
}
