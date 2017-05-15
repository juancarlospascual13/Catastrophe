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

    public static void make(String name) {
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
            out += "        (= (pick_machine " + d.getId() + ") 20)\n" +
                    "        (at " + d.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (d.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
            if (!d.isBroken()){
                out += "        (is_active " + d.getId() + ")\n";
            }
            if (d.getDeathCounter() > dronesEnd.get(dronesEnd.indexOf(d)).getDeathCounter()) {
                out += "        (not_damaged " + d.getId() + ")\n";
            }
        }
        for (Cleaner c : cleaners) {
            out += "        (= (pick_machine " + c.getId() + ") 20)\n";
            if (c.getCargo() == null){
                out += "        (empty " + c.getId() + ")\n" ;
            }
            else {
                out += "        (full " + c.getCargo().getId() + " " + c.getId() + ")\n" ;
            }
            out += "        (at " + c.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (c.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
            if (!c.isBroken()){
                out += "        (is_active " + c.getId() + ")\n";
            }
            if (c.getDeathCounter() > cleanersEnd.get(cleanersEnd.indexOf(c)).getDeathCounter()) {
                out += "        (not_damaged " + c.getId() + ")\n";
            }
        }
        for (Rubble r : rubbles) {
            out += "        (= (pick_rubble " + r.getId() + ") 200)\n";
            out += "        (at " + r.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (r.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
            if (r.isAssessed() && (r.getRadioactivity() > 0)) {
                out += "        (is_radioactive " + r.getId() + ")\n";
            }
        }
        for (Waypoint x : map.getWaypoints()) {
            for (Waypoint y : map.getWaypoints()) {
                if (x.getConnectedWaypointsByFlight().contains(y))
                    out += "        (traversable_flight " + x.getId() + " " + y.getId() + ")\n";
                if (x.getConnectedWaypointsByLand().contains(y))
                    out += "        (traversable_land " + x.getId() + " " + y.getId() + ")\n";
                if (x.getConnectedWaypoints().contains(y))
                    out += "        (= (distance " + x.getId() + " " + y.getId() + ") 1)\n";
            }
        }
        out += "        (= (total-cost) 0)\n";
        out += ")\n" +
                "(:goal (and\n";
        int damagedCleanersCounter = 0;
        for (Cleaner c : cleanersEnd) {
            //We count hoe many cleaners are damaged or dead
            if (cleaners.get(cleaners.indexOf(c)).isBroken() || (cleaners.get(cleaners.indexOf(c)).getDeathCounter() <= c.getDeathCounter()))
                damagedCleanersCounter++;
            out += "            (at " + c.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (c.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
        }
        int damagedDronesCounter = 0;
        for (Drone d : dronesEnd) {
            //We count hoe many drones are damaged or dead
            if (drones.get(drones.indexOf(d)).isBroken() || (drones.get(drones.indexOf(d)).getDeathCounter() <= d.getDeathCounter()))
                damagedDronesCounter++;
            out += "            (at " + d.getId();
            for (Waypoint w : map.getWaypoints()) {
                if (d.getPosition().equals(w))
                    out += " " + w.getId() + ")\n";
            }
        }
        for (Rubble r : rubbles) {
            if (!r.isAssessed()){
                //Can't assess without healthy drones, and so on
                if (damagedDronesCounter < drones.size())
                    out += "            (assessed " + r.getId() + ")\n";
            }
            else if (r.getRadioactivity() > 0){
                if (damagedCleanersCounter < cleaners.size())
                    out += "            (is_clean " + r.getId() + ")\n";
            }
            else { //Is assessed but not radioactive
                for (Waypoint w : map.getWaypoints()) {
                    if ((damagedCleanersCounter < cleaners.size()) && rubblesEnd.contains(r) && rubblesEnd.get(rubblesEnd.indexOf(r)).getPosition().equals(w))
                        out += "            (at " + r.getId() + " " + w.getId() + ")\n";
                }
            }
        }
        out += "       )\n" +
                ")\n" +
                "(:metric minimize (total-cost))\n" +
                ")\n";
        try {
            PrintWriter printer = new PrintWriter(conf.getProperty("output") + "/" + name + ".pddl");
            printer.print(out);
            printer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
