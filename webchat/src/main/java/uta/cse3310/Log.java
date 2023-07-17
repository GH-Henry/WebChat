package uta.cse3310;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.*;

public class Log {
    private File file = new File("Log.txt");

    //creates file
    public void createLog() {
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            }
            else { 
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occured.");
            e.printStackTrace();
        }    
    }

    //writing text to file
    public void writeToLog(String text) {
        Gson textGson = new GsonBuilder().setPrettyPrinting().create();
        String textJson = textGson.toJson("{" + java.time.LocalDate.now() + "," + java.time.LocalTime.now() + "," + text +"}");
        
        try {
            FileWriter writer = new FileWriter("Log.txt", true);
            writer.write( textJson+ "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }
} 