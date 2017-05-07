package main.java.catastrophe;

import main.java.catastrophe.middleware.Executioner;
import main.java.catastrophe.model.Machine;
import main.java.catastrophe.singletons.Configuration;
import main.java.catastrophe.singletons.PointMap;

import java.util.ArrayList;

public class Main {
//Nota. Funciona de chiripa. Hay que hacer que los hilos esperen a que el resto cumpla sus objetivos.

    public static void main(String[] args) {
        Configuration conf = Configuration.getInstance();
        conf.getProperties().setProperty("map", args[0]);
        conf.getProperties().setProperty("nextPlan", "1");
        PointMap map = PointMap.getInstance();
        ArrayList<Executioner> threads = new ArrayList<>();
        for (Machine m : map.getListfromParticipants(Machine.class)){
            Executioner aux = new Executioner(m.getId());
            threads.add(aux);
        }
        for (Executioner ex : threads){
            ex.start();
        }
        int waiting = 0;
        int nextPlan = 0;
        while (waiting < threads.size()){
            waiting = 0;
            nextPlan = Integer.parseInt(conf.getProperties().getProperty("nextPlan"));
            for (Executioner ex : threads){
                synchronized (ex) {
                    if (ex.isWaiting() && (ex.getCurrentPlan() == Integer.parseInt(conf.getProperties().getProperty("nextPlan"))) && (nextPlan == Integer.parseInt(conf.getProperties().getProperty("nextPlan"))))
                        waiting++;
                    else if (ex.getCurrentPlan() < Integer.parseInt(conf.getProperties().getProperty("nextPlan"))){
                            ex.setCommands(ex.runPlanner());
                            ex.setNext(ex.getCommands().get(0));
                            ex.setCurrentPlan(Integer.parseInt(conf.getProperties().getProperty("nextPlan")));
                            ex.setShutdown(false);
                            ex.setWaiting(false);
                            ex.notify();
                    }
                }
            }
        }
        for (Executioner ex : threads){
            synchronized (ex) {
                ex.setShutdown(true);
                ex.notify();
            }
        }
    }
}
