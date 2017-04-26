package eu.atanasio;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class Machine extends Pickable {
    private boolean broken;

    public Machine(String name) {
        super(name);
        this.broken = false;
    }

    public void move (Waypoint waypoint) {
        if (this.getPosition().getConnectedWaypoints().contains(waypoint))
            this.setPosition(waypoint);
    }
}
