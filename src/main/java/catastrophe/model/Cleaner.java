package main.java.catastrophe.model;

import main.java.catastrophe.Exceptions.CleanerOperationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by victorperez on 14/04/17.
 */
@NoArgsConstructor
@Getter
@Setter
public class Cleaner extends Machine {
    private Pickable cargo;

    public Cleaner(String id) {
        super(id);
    }

    public void pickUp (Pickable item) throws CleanerOperationException {
        if (this.isBroken())
            throw new CleanerOperationException("This cleaner is broken");
        if (cargo != null)
            throw new CleanerOperationException("This cleaner is full");
        if (item.getPosition().equals(this.getPosition())){
            if (item instanceof Rubble) {
                Rubble r = (Rubble) item;
                super.setDeathCounter(super.getDeathCounter() - r.getRadioactivity());
            }
            if (super.getDeathCounter() <= 0)
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
            if (super.getDeathCounter() <= 0)
                this.setBroken(true);
            if ((cargo instanceof Rubble) && getPosition().isDump()){
                Rubble r = (Rubble) cargo;
                r.setRadioactivity(0);
            }
            cargo = null;
        }
    }

    public void walk (Waypoint waypoint) throws CleanerOperationException {
        if (this.isBroken())
            throw new CleanerOperationException("This cleaner is broken");
        if (this.getPosition().getConnectedWaypointsByLand().contains(waypoint)){
            move(waypoint);
            if (cargo != null)
                cargo.setPosition(waypoint);
        }
    }
}
