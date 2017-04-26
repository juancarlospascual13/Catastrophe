package eu.atanasio.middleware;

import eu.atanasio.BuildingMap;
import eu.atanasio.Cleaner;
import eu.atanasio.CleanerOperationException;
import eu.atanasio.Drone;
import eu.atanasio.DroneOperationException;
import eu.atanasio.Machine;
import eu.atanasio.Pickable;
import eu.atanasio.Rubble;
import eu.atanasio.Waypoint;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

/**
 * Created by victorperez on 18/04/17.
 */
@Data
public class Executioner {

    public static Waypoint r0 = new Waypoint("r0");
    public static Waypoint r1 = new Waypoint("r1");
    public static Waypoint r2 = new Waypoint("r2");
    public static Waypoint r3 = new Waypoint("r3");
    public static Waypoint r4 = new Waypoint("r4");
    public static BuildingMap map = getMap();

    public static Drone drone0 = new Drone("drone0",50);
    public static Cleaner cleaner0 = new Cleaner("cleaner0",50);
    public static Rubble rubble0 = new Rubble("rubble0");
    public static Rubble rubble1 = new Rubble("rubble1");


    public static Drone drone0End = new Drone("drone0",50);
    public static Cleaner cleaner0End = new Cleaner("cleaner0",50);
    public static Rubble rubble0End = new Rubble("rubble0");
    public static Rubble rubble1End = new Rubble("rubble1");

    public static Cleaner[] cleaners = {cleaner0};
    public static Drone[] drones = {drone0};
    public static Rubble[] rubbles = {rubble0,rubble1};
    public static Cleaner[] cleanersEnd = {cleaner0End};
    public static Drone[] dronesEnd = {drone0End};
    public static Rubble[] rubblesEnd = {rubble0End,rubble1End};

    public void setUp(){
        drone0.setPosition(map.getWaypoints()[0]);
        cleaner0.setPosition(map.getWaypoints()[0]);
        rubble0.setPosition(map.getWaypoints()[4]);
        //rubble0.setAssessed(true);
        rubble0.setRadioactive(true);
        rubble1.setPosition(map.getWaypoints()[3]);
        //rubble1.setAssessed(true);
        rubble1.setRadioactive(true);

        drone0End.setPosition(map.getWaypoints()[0]);
        cleaner0End.setPosition(map.getWaypoints()[0]);
        rubble0End.setPosition(map.getWaypoints()[1]);
        rubble1End.setPosition(map.getWaypoints()[1]);
    }

    public void runPlanner(){
        TroubleMaker.make(map,cleaners,drones,rubbles,cleanersEnd,dronesEnd,rubblesEnd);

        try {
            Process p=Runtime.getRuntime().exec("./run-map.sh -d /home/victorperez/ATA/output/domain.pddl -p /home/victorperez/ATA/output/p01.pddl -o output -A cmap -s mingoals -P private -M nil  -a lama-unit-cost -r lama-unit-cost -g subsets -y nil -Y lama-second -t 1800 -C t",
                    null, new File("/home/victorperez/ATA/CMAP/"));
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init(){
        runPlanner();

        try {
            BufferedReader br = new BufferedReader(new FileReader("/home/victorperez/ATA/CMAP/output"));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                if(!line.startsWith(";;")){
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if (executeCommand(line.split(":? +\\(?|\\)"))>0){
                        br.close();
                        runPlanner();
                        br = new BufferedReader(new FileReader("/home/victorperez/ATA/CMAP/output"));
                    }
                }
                line = br.readLine();
            }
            try {
                PrintWriter printer = new PrintWriter("/home/victorperez/ATA/output/simulation_results");
                printer.print(sb.toString());
                printer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BuildingMap getMap() {
        Waypoint[] Waypoints = {r0,r1,r2,r3,r4};
        BuildingMap map = new BuildingMap(Waypoints);

        //Waypoint 0
        HashSet<Waypoint> auxWalk0 = new HashSet<>();
        HashSet<Waypoint> auxFly0 = new HashSet<>();
        auxWalk0.add(r1);
        auxFly0.add(r1);
        auxFly0.add(r4);
        r0.setConnectedWaypointsByFlight(auxFly0);
        r0.setConnectedWaypointsByLand(auxWalk0);
        r0.fixWaypoints();

        //Waypoint 1
        HashSet<Waypoint> auxWalk1 = new HashSet<>();
        HashSet<Waypoint> auxFly1 = new HashSet<>();
        auxWalk1.add(r0);
        auxWalk1.add(r2);
        auxFly1.add(r0);
        r1.setConnectedWaypointsByFlight(auxFly1);
        r1.setConnectedWaypointsByLand(auxWalk1);
        r1.setDump(true);
        r1.fixWaypoints();

        //Waypoint 2
        HashSet<Waypoint> auxWalk2 = new HashSet<>();
        HashSet<Waypoint> auxFly2 = new HashSet<>();
        auxWalk2.add(r1);
        auxWalk2.add(r3);
        auxWalk2.add(r4);
        auxFly2.add(r3);
        r2.setConnectedWaypointsByFlight(auxFly2);
        r2.setConnectedWaypointsByLand(auxWalk2);
        r2.fixWaypoints();

        //Waypoint 3
        HashSet<Waypoint> auxWalk3 = new HashSet<>();
        HashSet<Waypoint> auxFly3 = new HashSet<>();
        auxWalk3.add(r2);
        auxFly3.add(r2);
        auxFly3.add(r4);
        r3.setConnectedWaypointsByFlight(auxFly3);
        r3.setConnectedWaypointsByLand(auxWalk3);
        r3.fixWaypoints();

        //Waypoint 4
        HashSet<Waypoint> auxWalk4 = new HashSet<>();
        HashSet<Waypoint> auxFly4 = new HashSet<>();
        auxWalk4.add(r2);
        auxFly4.add(r3);
        auxFly4.add(r0);
        r4.setConnectedWaypointsByFlight(auxFly4);
        r4.setConnectedWaypointsByLand(auxWalk4);
        r4.fixWaypoints();

        return map;
    }

    public int executeCommand(String[] command){
        switch (command[3].split("\\d")[0]) {//agent

            case "DRONE":
                try {
                    Drone cls = (Drone) Executioner.class.getField("drone"+command[3].split("[a-zA-Z]+")[1]).get(this);
                    Waypoint y;
                    Rubble r;
                    switch (command[2]){
                        case "FLY":
                            y = (Waypoint) Executioner.class.getDeclaredField("r"+command[5].split("[a-zA-Z]+")[1]).get(this);
                            cls.fly(y);
                            return 0;
                        case "ASSESS":
                            r = (Rubble) Executioner.class.getDeclaredField("rubble"+command[4].split("[a-zA-Z]+")[1]).get(this);
                            r.setAssessed(true);
                            return 1;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (DroneOperationException e) {
                    e.printStackTrace();
                }
                return -1;
            case "CLEANER":
                try {
                    Cleaner cls = (Cleaner) Executioner.class.getField("cleaner"+command[3].split("[a-zA-Z]+")[1]).get(this);
                    Waypoint y;
                    Pickable r;
                    switch (command[2]){
                        case "WALK":
                            y = (Waypoint) Executioner.class.getDeclaredField("r"+command[5].split("[a-zA-Z]+")[1]).get(this);
                            cls.walk(y);
                            return 0;
                        case "PICKUP_RUBBLE":
                            r = (Rubble) Executioner.class.getDeclaredField("rubble"+command[4].split("[a-zA-Z]+")[1]).get(this);
                            cls.pickUp(r);
                            return 0;
                        case "PICKUP_MACHINE":
                            switch (command[4].split("\\d")[0]){
                                case "DRONE":
                                    r = (Machine) Executioner.class.getDeclaredField("drone"+command[4].split("[a-zA-Z]+")[1]).get(this);
                                    break;
                                case "CLEANER":
                                    r = (Machine) Executioner.class.getDeclaredField("cleaner"+command[4].split("[a-zA-Z]+")[1]).get(this);
                                    break;
                                default:
                                    return -1;
                            }
                            cls.pickUp(r);
                            return 0;
                        case "DROP":
                            cls.dump();
                            return 0;
                        case "PLACE_AT":
                            cls.dump();
                            return 0;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (CleanerOperationException e) {
                    e.printStackTrace();
                }
                return -1;
        }
        return -1;
    }
}
