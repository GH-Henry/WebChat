package uta.cse3310;

import java.io.*;
import com.google.gson.*;

public class Log {
    private File file = new File("Log.txt");

    // creates Log file
    public void createLog() {
        try {
            if (file.createNewFile()) {
                System.out.println(file.getName() + "created!");
            } else {
                System.out.println(file.getName() + "already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occured creating Log.txt!");
            e.printStackTrace();
        }
    }

    // writing text to file
    public void writeToLog(String text) {
        Gson textGson = new GsonBuilder().setPrettyPrinting().create();
        String textJson = textGson
                .toJson("{" + java.time.LocalDate.now() + "," + java.time.LocalTime.now() + "," + text + "}");

        try {
            FileWriter fw = new FileWriter("Log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(textJson + "\n");
            bw.close();
        } catch (IOException e) {
            System.out.println("An error occured writing to Log.txt!");
            e.printStackTrace();
        }
    }
}