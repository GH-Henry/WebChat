package uta.cse3310;

import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;

public class LogTest {
    File file;
    // creates a test file and writes to it. It will be deleted after
    
    @Before
    public void testCreateLog() throws IOException {
    //create LogTest file
        file = new File ("LogTest.txt");
    }

    @Test
    public void testWriteToLog() {
    //write data to the LogTest file
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("testing!");
            bw.close();
        }
        catch(IOException ioe) {
            System.err.println("Error writing to LogTest file!");
        }       
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertEquals(file.length(), 8L);
        assertTrue(file.getAbsolutePath().endsWith("LogTest.txt"));
    }

}