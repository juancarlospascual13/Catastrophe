package main.java.catastrophe.middleware;

import lombok.Data;
import main.java.catastrophe.Exceptions.CleanerOperationException;
import main.java.catastrophe.Exceptions.CommandExecutionException;
import main.java.catastrophe.Exceptions.DroneOperationException;
import main.java.catastrophe.Exceptions.NotFoundInMapException;
import main.java.catastrophe.model.Cleaner;
import main.java.catastrophe.model.Drone;
import main.java.catastrophe.model.Pickable;
import main.java.catastrophe.model.Rubble;
import main.java.catastrophe.model.Waypoint;
import main.java.catastrophe.singletons.Configuration;
import main.java.catastrophe.singletons.Logger;
import main.java.catastrophe.singletons.PointMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by victorperez on 18/04/17.
 */
@Data
public class Executioner implements Runnable {
    private Thread thread;
    private boolean waiting;
    private boolean shutdown;
    private String id;
    private ArrayList<String> commands;
    private String next;
    private int currentPlan;
    private Configuration conf = Configuration.getInstance();
    private Logger log = Logger.getInstance();

    public Executioner(String id) {
        this.id = id;
        this.waiting = false;
        this.commands = runPlanner();
        this.next = commands.get(0);
        this.currentPlan = 0;
    }

    public ArrayList<String> runPlanner(){
        TroubleMaker.make();

        try {
            Process p=Runtime.getRuntime().exec("./run-map.sh -d " + conf.getProperty("output") + "/domain.pddl -p " + conf.getProperty("output") + "/p01.pddl -o " + id + " -A cmap -s mingoals -P private -M nil  -a lama-unit-cost -r lama-unit-cost -g subsets -y nil -Y lama-second -t 1800 -C t",
                    null, new File(conf.getProperty("cmap")));
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> c = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(conf.getProperty("cmap") + "/" + id));
            String line = br.readLine();

            while (line != null) {
                if(!line.startsWith(";;")){
                    c.add(line);
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }

    //returns current position
    public int runNextCommand(){
        try {
            //StringBuilder sb = new StringBuilder();

            if(!next.startsWith(";;")) {
                //sb.append(line);
                //sb.append(System.lineSeparator());
                int execResult = executeCommand(next.split(":? +\\(?|\\)"));
                if (( execResult > 0) || (currentPlan < Integer.parseInt(conf.getProperties().getProperty("nextPlan")))) {
                    this.commands = runPlanner();
                    this.next = commands.get(0);
                    if (execResult > 0) {
                        synchronized (conf) {
                            conf.getProperties().setProperty("nextPlan", String.valueOf(Integer.parseInt(conf.getProperties().getProperty("nextPlan"))+1));
                        }
                    }
                    this.currentPlan = Integer.parseInt(conf.getProperties().getProperty("nextPlan"));
                    return 0;
                }
                else if (commands.indexOf(next)+1 < commands.size()){
                    next = commands.get(commands.indexOf(next)+1);
                    return commands.indexOf(next)+1;
                }
                else if (commands.indexOf(next)+1 == commands.size()){
                    next = "";
                    return 0;
                }
            }
        }  catch (CommandExecutionException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int executeCommand(String[] command) throws CommandExecutionException {
        PointMap map = PointMap.getInstance();
        synchronized (map) {
            switch (command[3].split("\\d")[0]) {//agent

                case "DRONE":
                    try {
                        Drone cls;
                        String name;
                        name = command[3].toLowerCase();
                        if (name.equals(id)) {
                            try {
                                cls = map.getParticipant(Drone.class, new Drone(name));
                            } catch (NotFoundInMapException e) {
                                throw new CommandExecutionException("Could not find drone: " + name);
                            }
                            Waypoint y;
                            Rubble r;
                            switch (command[2]) {
                                case "FLY":
                                    name = command[5].toLowerCase();
                                    try {
                                        y = map.getWaypoint(name);
                                    } catch (NotFoundInMapException e) {
                                        throw new CommandExecutionException("Could not find waypoint: " + name);
                                    }
                                    cls.fly(y);
                                    return 0;
                                case "ASSESS":
                                    name = command[4].toLowerCase();
                                    try {
                                        r = map.getParticipant(Rubble.class, new Rubble(name));
                                    } catch (NotFoundInMapException e) {
                                        throw new CommandExecutionException("Could not find rubble: " + name);
                                    }
                                    synchronized (r) {
                                        cls.assess(r);
                                        r.notifyAll();
                                    }
                                    return 1;
                            }
                        }
                    } catch (DroneOperationException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    return -1;
                case "CLEANER":
                    try {
                        Cleaner cls;
                        String name = command[3].toLowerCase();
                        if (name.equals(id)) {
                            try {
                                cls = map.getParticipant(Cleaner.class, new Cleaner(name));
                            } catch (NotFoundInMapException e) {
                                throw new CommandExecutionException("Could not find cleaner: " + name);
                            }
                            Waypoint y;
                            Pickable r;
                            switch (command[2]) {
                                case "WALK":
                                    name = command[5].toLowerCase();
                                    try {
                                        y = map.getWaypoint(name);
                                    } catch (NotFoundInMapException e) {
                                        throw new CommandExecutionException("Could not find waypoint: " + name);
                                    }
                                    cls.walk(y);
                                    return 0;
                                case "PICKUP_RUBBLE":
                                    name = command[4].toLowerCase();
                                    try {
                                        r = map.getParticipant(Rubble.class, new Rubble(name));
                                    } catch (NotFoundInMapException e) {
                                        throw new CommandExecutionException("Could not find rubble: " + name);
                                    }
                                    synchronized (r) {
                                        Rubble aux = (Rubble) r;
                                        while (!(aux.isAssessed() && aux.isRadioactive())) {
                                            r.wait();
                                        }
                                        cls.pickUp(r);
                                    }
                                    return 0;
                                case "PICKUP_MACHINE":
                                    name = command[4].toLowerCase();
                                    switch (command[4].split("\\d")[0]) {
                                        case "DRONE":
                                            try {
                                                r = map.getParticipant(Drone.class, new Drone(name));
                                            } catch (NotFoundInMapException e) {
                                                throw new CommandExecutionException("Could not find drone: " + name);
                                            }
                                            break;
                                        case "CLEANER":
                                            try {
                                                r = map.getParticipant(Cleaner.class, new Cleaner(name));
                                            } catch (NotFoundInMapException e) {
                                                throw new CommandExecutionException("Could not find cleaner: " + name);
                                            }
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
                        }
                    } catch (CleanerOperationException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return -1;
            }
            return -1;
        }
    }

    public void start () {
        log.println("Starting: " +  id );
        if (thread == null) {
            thread = new Thread (this, id);
            thread.start ();
        }
    }

    public void run(){
        while (!shutdown) {
            int aux = 0;
            synchronized (this) {
                if ((currentPlan == Integer.parseInt(conf.getProperties().getProperty("nextPlan"))) && !shutdown) {
                    while (next.equals("") && !shutdown) {
                        try {
                            log.println(id + ": waiting...");
                            waiting = true;
                            this.wait();
                            if (shutdown)
                                log.println(id + ": Shutting down");
                            else
                                log.println(id + ": waking...");
                            if (!next.equals("")) {
                                String name;
                                name = next.split(":? +\\(?|\\)")[3].toLowerCase();
                                if (name.equals(id)) {
                                    log.println(id + ": " + next);
                                }
                                aux = runNextCommand();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            while (aux != -1 && !next.equals("") && !shutdown) {
                String name;
                name = next.split(":? +\\(?|\\)")[3].toLowerCase();
                if (name.equals(id)) {
                    log.println(id + ": " + next);
                }
                aux = runNextCommand();
            }
        }

    }
}
