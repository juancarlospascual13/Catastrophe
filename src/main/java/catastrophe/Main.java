package main.java.catastrophe;

import main.java.catastrophe.middleware.Executioner;
import main.java.catastrophe.model.Machine;
import main.java.catastrophe.singletons.Configuration;
import main.java.catastrophe.singletons.Logger;
import main.java.catastrophe.singletons.PointMap;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {
        Configuration conf = Configuration.getInstance();
        conf.getProperties().setProperty("map", args[0]);
        conf.getProperties().setProperty("result", args[1]);
        conf.getProperties().setProperty("nextPlan", "1");
        Logger log = Logger.getInstance();
        PointMap map = PointMap.getInstance();
        ArrayList<Executioner> threads = new ArrayList<>();
        for (Machine m : map.getListfromParticipants(Machine.class)){
            Executioner aux = new Executioner(m.getId());
            threads.add(aux);
        }
        log.println("Start");
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
                try {//We don't need to be checking this all the time
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Executioner ex : threads){
            synchronized (ex) {
                ex.setShutdown(true);
                ex.notify();
            }
        }
        log.println("Finish");
        log.close();
    }
}
