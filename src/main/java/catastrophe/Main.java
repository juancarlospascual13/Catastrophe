package main.java.catastrophe;

import main.java.catastrophe.middleware.Executioner;
import main.java.catastrophe.singletons.Configuration;

public class Main {


    public static void main(String[] args) {
        Configuration conf = Configuration.getInstance();
        conf.getProperties().setProperty("map", args[0]);
        Executioner exe = new Executioner();
        exe.init();
    }
}
