package main.java.catastrophe.singletons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import main.java.catastrophe.Exceptions.NotFoundInMapException;
import main.java.catastrophe.model.Pickable;
import main.java.catastrophe.model.Waypoint;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victorperez on 14/04/17.
 */
@Data
@JsonDeserialize(using = PointMapDeserializer.class)
public class PointMap {
    private static Configuration conf = Configuration.getInstance();
    private static PointMap instance = null;
    private Waypoint[] waypoints;
    private Pickable[] participants;
    private Pickable[] finalState;

    private PointMap() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            PointMap aux = mapper.readValue(new File(conf.getProperties().getProperty("map")), PointMap.class);
            this.setWaypoints(aux.getWaypoints());
            this.setParticipants(aux.getParticipants());
            this.setFinalState(aux.getFinalState());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public PointMap(Waypoint[] waypoints, Pickable[] participants, Pickable[] finalState) {
        this.waypoints = waypoints;
        this.participants = participants;
        this.finalState = finalState;
    }

    public static PointMap getInstance(){
        if (instance == null){
            instance = new PointMap();
        }
        return instance;
    }

    public <T extends Pickable> List<T> getListOf (Class<T> cls, Pickable[] objs){
        ArrayList<T> x = new ArrayList<>();
        for (Pickable p : objs){
            if (cls.isAssignableFrom(p.getClass()))
                x.add((T) p);
        }
        return x;
    }

    public <T extends Pickable> List<T> getListfromParticipants (Class<T> cls){
        return getListOf(cls, participants);
    }

    public <T extends Pickable> List<T> getListfromFinal (Class<T> cls){
        return getListOf(cls, finalState);
    }

    public <T extends Pickable> T getPickable (Class<T> cls, T pick, Pickable[] objs) throws NotFoundInMapException {
        if (this.getListOf(cls, objs).contains(pick))
            return getListOf(cls, this.getParticipants()).get(this.getListOf(cls, this.getParticipants()).indexOf(pick));
        else
            throw new NotFoundInMapException("Could not find the object.");
    }

    public <T extends Pickable> T getParticipant (Class<T> cls, T pick) throws NotFoundInMapException {
        return getPickable(cls, pick, participants);
    }

    public <T extends Pickable> T getFinalState (Class<T> cls, T pick) throws NotFoundInMapException {
        return getPickable(cls, pick, finalState);
    }

    public Waypoint getWaypoint (String name) throws NotFoundInMapException {
        Waypoint pick = new Waypoint(name);
        for (Waypoint p : waypoints){
            if (p.equals(pick))
                return p;
        }
        throw new NotFoundInMapException("Could not find the waypoint.");
    }


}
