package eu.atanasio.middleware;

import eu.atanasio.BuildingMap;
import eu.atanasio.Cleaner;
import eu.atanasio.Drone;
import eu.atanasio.Rubble;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by victorperez on 16/04/17.
 */
public class TroubleMaker {
    public static void make(BuildingMap map, Cleaner[] cleaners, Drone[] drones, Rubble[] rubbles, Cleaner[] cleanersEnd, Drone[] dronesEnd, Rubble[] rubblesEnd) {
        String out = "(define (problem pickup1234) (:domain Nuclear)\n" +
                "(:objects\n";
        for (int i = 0; i < cleaners.length; i++) {
            out += "        (:private cleaner" + i + "\n" +
                    "            cleaner" + i + " - cleaner\n" +
                    "        )\n";
        }
        for (int i = 0; i < drones.length; i++) {
            out += "        (:private drone" + i + "\n" +
                    "            drone" + i + " - drone\n" +
                    "        )\n";
        }
        for (int i = 0; i < map.getWaypoints().length; i++) {
            if (map.getWaypoints()[i].isDump())
                out += "        waypoint" + i + " - dump\n";
            else
                out += "        waypoint" + i + " - waypoint\n";
        }
        for (int i = 0; i < rubbles.length; i++) {
            out += "        rubble" + i + " - rubble\n";
        }
        out += ")\n" +
                "(:init\n";
        for (int i = 0; i < drones.length; i++) {
            out += "        (is_active drone" + i + ")\n" +
                    "        (at drone" + i;
            for (int z = 0; z < map.getWaypoints().length; z++) {
                if (drones[i].getPosition().equals(map.getWaypoints()[z]))
                    out += " waypoint" + z + ")\n";
            }
            if (drones[i].isBroken()){
                out += "        (is_broken drone"+i+")\n";
            }
            else {
                out += "        (is_active drone"+i+")\n";
            }
        }
        for (int i = 0; i < cleaners.length; i++) {
            if (cleaners[i].getCargo() == null){
                out += "        (empty cleaner" + i + ")\n" ;
            }
            else {
                out += "        (full " + cleaners[i].getCargo().getName() + " cleaner" + i + ")\n" ;
            }
            out += "        (is_active cleaner" + i + ")\n" +
                    "        (at cleaner" + i;
            for (int z = 0; z < map.getWaypoints().length; z++) {
                if (cleaners[i].getPosition().equals(map.getWaypoints()[z]))
                    out += " waypoint" + z + ")\n";
            }
            if (cleaners[i].isBroken()){
                out += "        (is_broken cleaner"+i+")\n";
            }
            else {
                out += "        (is_active cleaner"+i+")\n";
            }
        }
        for (int i = 0; i < rubbles.length; i++) {
            out += "        (at rubble" + i;
            for (int z = 0; z < map.getWaypoints().length; z++) {
                if (rubbles[i].getPosition().equals(map.getWaypoints()[z]))
                    out += " waypoint" + z + ")\n";
            }
            if (rubbles[i].isAssessed() && rubbles[i].isRadioactive()) {
                out += "        (is_radioactive rubble" + i + ")\n";
            }
        }
        for (int i = 0; i < map.getWaypoints().length; i++) {
            for (int z = 0; z < map.getWaypoints().length; z++) {
                if (map.getWaypoints()[i].getConnectedWaypoints().contains(map.getWaypoints()[z]))
                    out += "        (visible waypoint"+i+" waypoint"+z+")\n";
                if (map.getWaypoints()[i].getConnectedWaypointsByFlight().contains(map.getWaypoints()[z]))
                    out += "        (traversable_flight waypoint"+i+" waypoint"+z+")\n";
                if (map.getWaypoints()[i].getConnectedWaypointsByLand().contains(map.getWaypoints()[z]))
                    out += "        (traversable_land waypoint"+i+" waypoint"+z+")\n";
            }
        }
        out += ")\n" +
                "(:goal (and\n";
        for (int i = 0; i < cleanersEnd.length; i++) {
            out += "            (at cleaner" + i;
            for (int z = 0; z < map.getWaypoints().length; z++) {
                if (cleanersEnd[i].getPosition().equals(map.getWaypoints()[z]))
                    out += " waypoint" + z + ")\n";
            }
        }
        for (int i = 0; i < dronesEnd.length; i++) {
            out += "            (at drone" + i;
            for (int z = 0; z < map.getWaypoints().length; z++) {
                if (dronesEnd[i].getPosition().equals(map.getWaypoints()[z]))
                    out += " waypoint" + z + ")\n";
            }
        }
        for (int i = 0; i < rubblesEnd.length; i++) {
            if (!rubbles[i].isAssessed()){
                out += "            (assessed rubble" + i + ")\n";
            }
            else if (rubbles[i].isRadioactive()){
                out += "            (is_clean rubble" + i + ")\n";
            }
            else { //Is assessed but not radioactive
                out += "            (at rubble" + i;
                for (int z = 0; z < map.getWaypoints().length; z++) {
                    if (rubblesEnd[i].getPosition().equals(map.getWaypoints()[z]))
                        out += " waypoint" + z + ")\n";
                }
            }
        }
        out += "       )\n" +
                ")\n" +
                ")\n";
        try {
            PrintWriter printer = new PrintWriter("/home/victorperez/ATA/output/p01.pddl");
            printer.print(out);
            printer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
