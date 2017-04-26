package eu.atanasio;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
@EqualsAndHashCode(callSuper=true,exclude={"deathCounter","cargo"})
public class Cleaner extends Machine {
    private int deathCounter;
    private Pickable cargo;

    public Cleaner(String name, int deathCounter) {
        super(name);
        this.deathCounter = deathCounter;
    }

    public void pickUp (Pickable item) throws CleanerOperationException {
        if (this.isBroken())
            throw new CleanerOperationException("This cleaner is broken");
        if (cargo != null)
            throw new CleanerOperationException("This cleaner is full");
        if (item.getPosition().equals(this.getPosition())){
            deathCounter--;
            if (deathCounter<=0)
                this.setBroken(true);
            cargo = item;
        }
        else
            throw new CleanerOperationException("This cleaner and the pickable object are not in the same position");
    }

    public void dump () throws CleanerOperationException {
        if (this.isBroken())
            throw new CleanerOperationException("This cleaner is broken");
        if (cargo == null)
            throw new CleanerOperationException("This cleaner is already empty");
        else {
            deathCounter--;
            if (deathCounter<=0)
                this.setBroken(true);
            cargo = null;
        }
    }

    public void walk (Waypoint waypoint) throws CleanerOperationException {
        if (this.isBroken())
            throw new CleanerOperationException("This cleaner is broken");
        if (this.getPosition().getConnectedWaypointsByLand().contains(waypoint)){
            deathCounter--;
            move(waypoint);
            if (cargo != null)
                cargo.setPosition(waypoint);
        }
    }
}
