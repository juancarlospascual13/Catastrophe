package main.java.catastrophe.singletons;

import lombok.Data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by victorperez on 29/04/17.
 */
@Data
public class Configuration {
    private static Configuration instance = null;
    private Properties properties;

    private Configuration(){
        InputStream input = null;
        properties = new Properties();
        try {
            input = new FileInputStream("./conf/catastrophe.conf");
            properties.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Configuration getInstance(){
        if (instance == null){
            instance = new Configuration();
        }
        return instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
