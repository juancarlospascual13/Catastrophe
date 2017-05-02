package main.java.catastrophe.middleware;

import main.java.catastrophe.model.Cleaner;
import main.java.catastrophe.model.Drone;
import main.java.catastrophe.model.Rubble;
import main.java.catastrophe.model.Waypoint;
import main.java.catastrophe.singletons.Configuration;
import main.java.catastrophe.singletons.PointMap;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by victorperez on 16/04/17.
 */
public class TroubleMaker {
    static Configuration conf = Configuration.getInstance();

    public static void make() {
        PointMap map = PointMap.getInstance();
        List<Cleaner> cleaners = map.getListfromParticipants(Cleaner.class);
        List<Drone> drones = map.getListfromParticipants(Drone.class);
        List<Rubble> rubbles = map.getListfromParticipants(Rubble.class);
        List<Cleaner> cleanersEnd = map.getListfromFinal(Cleaner.class);
        List<Drone> dronesEnd = map.getListfromFinal(Drone.class);
        List<Rubble> rubblesEnd = map.getListfromFinal(Rubble.class);
        String out = "(define (problem pickup1234) (:domain Nuclear)\n" +
                "(:objects\n";
        for (Cleaner c : cleaners){
            out += "        (:private " + c.getId() + "\n" +
                    "            " + c.getId() + " - cleaner\n" +
                    "        )\n";
        }
        for (Drone d : drones) {
            out += "        (:private " + d.getId() + "\n" +
                    "            " + d.getId() + " - drone\n" +
                    "        )\n";
        }
        for (Waypoint w : map.getWaypoints()) {
            if (w.isDump())
                out += "        " + w.getId() + " - dump\n";
            else
                out += "        " + w.getId() + " - waypoint\n";
        }
        for (Rubble r : rubbles) {
            out += "        " + r.getId() + " - rubble\n";
        }
        out += ")\n" +
                "(:init\n";
        for (Drone d : drones) {
            out += "        (is_active " + d.getId() + ")\n" +
                    "        (at " + d.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (d.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
            if (d.isBroken()){
                out += "        (is_broken " + d.getId() + ")\n";
            }
            else {
                out += "        (is_active " + d.getId() + ")\n";
            }
        }
        for (Cleaner c : cleaners) {
            if (c.getCargo() == null){
                out += "        (empty " + c.getId() + ")\n" ;
            }
            else {
                out += "        (full " + c.getCargo().getId() + " " + c.getId() + ")\n" ;
            }
            out += "        (is_active " + c.getId() + ")\n" +
                    "        (at " + c.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (c.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
            if (c.isBroken()){
                out += "        (is_broken " + c.getId() + ")\n";
            }
            else {
                out += "        (is_active " + c.getId() + ")\n";
            }
        }
        for (Rubble r : rubbles) {
            out += "        (at " + r.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (r.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
            if (r.isAssessed() && r.isRadioactive()) {
                out += "        (is_radioactive " + r.getId() + ")\n";
            }
        }
        for (Waypoint x : map.getWaypoints()) {
            for (Waypoint y : map.getWaypoints()) {
                if (x.getConnectedWaypoints().contains(y))
                    out += "        (visible " + x.getId() + " " + y.getId() + ")\n";
                if (x.getConnectedWaypointsByFlight().contains(y))
                    out += "        (traversable_flight " + x.getId() + " " + y.getId() + ")\n";
                if (x.getConnectedWaypointsByLand().contains(y))
                    out += "        (traversable_land " + x.getId() + " " + y.getId() + ")\n";
            }
        }
        out += ")\n" +
                "(:goal (and\n";
        for (Cleaner c : cleanersEnd) {
            out += "            (at " + c.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (c.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
        }
        for (Drone d : dronesEnd) {
            out += "            (at " + d.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (d.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
        }
        for (Rubble r : rubbles) {
            if (!r.isAssessed()){
                out += "            (assessed " + r.getId() + ")\n";
            }
            else if (r.isRadioactive()){
                out += "            (is_clean " + r.getId() + ")\n";
            }
            else { //Is assessed but not radioactive
                out += "            (at " + r.getId();
                for (Waypoint w : map.getWaypoints()) {
                    if (rubblesEnd.get(rubbles.indexOf(r)).getPosition().equals(w))
                        out += " " + w.getId() + ")\n";
                }
            }
        }
        out += "       )\n" +
                ")\n" +
                ")\n";
        try {
            PrintWriter printer = new PrintWriter(conf.getProperty("output") + "/p01.pddl");
            printer.print(out);
            printer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
