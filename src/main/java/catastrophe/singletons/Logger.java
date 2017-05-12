package main.java.catastrophe.singletons;

import lombok.Data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by victorperez on 8/05/17.
 */
@Data
public class Logger {
    private static Configuration conf = Configuration.getInstance();
    private static Logger instance = null;
    private PrintWriter writer;

    private Logger() {
        try {
            writer = new PrintWriter(conf.getProperty("result"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance(){
        if (instance == null){
            instance = new Logger();
        }
        return instance;
    }

    public void print (String s){
        writer.print(s);
    }

    public void println (String s){
        writer.println(s);
    }

    public void close (){
        writer.close();
    }
}
