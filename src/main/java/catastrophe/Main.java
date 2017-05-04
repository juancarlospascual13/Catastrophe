package main.java.catastrophe;

import main.java.catastrophe.middleware.Executioner;
import main.java.catastrophe.model.Machine;
import main.java.catastrophe.singletons.Configuration;
import main.java.catastrophe.singletons.PointMap;

import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        Configuration conf = Configuration.getInstance();
        conf.getProperties().setProperty("map", args[0]);
        PointMap map = PointMap.getInstance();
        ArrayList<Executioner> threads = new ArrayList<>();
        Executioner.nextPlan = 0;
        for (Machine m : map.getListfromParticipants(Machine.class)){
            Executioner aux = new Executioner(m.getId());
            aux.start();
            threads.add(aux);
        }
    }
}
