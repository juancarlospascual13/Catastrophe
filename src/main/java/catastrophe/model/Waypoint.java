package main.java.catastrophe.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by victorperez on 14/04/17.
 */
@Getter
@Setter
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Waypoint extends IdClass {

    private Set<Waypoint> connectedWaypoints;
    private Set<Waypoint> connectedWaypointsByLand;
    private Set<Waypoint> connectedWaypointsByFlight;
    private boolean dump;

    public Waypoint(Set<Waypoint> connectedWaypointsByLand, Set<Waypoint> connectedWaypointsByFlight) {
        this.connectedWaypointsByLand = connectedWaypointsByLand;
        this.connectedWaypointsByFlight = connectedWaypointsByFlight;
        this.connectedWaypoints.addAll(connectedWaypointsByLand);
        this.connectedWaypoints.addAll(connectedWaypointsByFlight);
    }

    public Waypoint(String id) {
        super(id);
        connectedWaypoints = new HashSet<>();
        dump = false;
    }

    public void fixWaypoints(){
        this.connectedWaypoints.addAll(connectedWaypointsByLand);
        this.connectedWaypoints.addAll(connectedWaypointsByFlight);
    }

}
