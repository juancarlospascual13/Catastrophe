package main.java.catastrophe.model;

import main.java.catastrophe.Exceptions.DroneOperationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by victorperez on 14/04/17.
 */
@NoArgsConstructor
@Getter
@Setter
public class Drone extends Machine {

    public Drone (String id) {
        super(id);
    }

    public boolean assess (Rubble item) throws DroneOperationException {
        if (this.isBroken())
            throw new DroneOperationException("This drone is broken");
        if (item.getPosition().equals(this.getPosition())){
            super.setDeathCounter(super.getDeathCounter() - item.getRadioactivity());
            if (super.getDeathCounter() <= 0)
                this.setBroken(true);
            item.setAssessed(true);
            return item.getRadioactivity() > 0;
        }
        else
            throw new DroneOperationException("This drone and the pickable object are not in the same position");
    }

    public void fly (Waypoint waypoint) throws DroneOperationException {
        if (this.isBroken())
            throw new DroneOperationException("This drone is broken");
        if(this.getPosition().getConnectedWaypointsByFlight().contains(waypoint)){
            move(waypoint);
        }
    }
}
