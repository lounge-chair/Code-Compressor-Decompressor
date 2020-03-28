// On my honor, I have neither given nor received unauthorized aid on this assignment.

import java.util.*;

import javax.lang.model.util.ElementScanner6;

import java.io.*;

public class SIM {
    // Declare IO files
    // TODO: split this up for files for compression and decompression
    public static void main(String[] args) throws FileNotFoundException{
        // Compression
        if (args[0].equals("1")) {
            compression();
        }
        // Decompression
        else if (args[0].equals("2")) {
            // decompression();
        } else {
            System.out.println("Error! Invalid command entered. Please try again!");
        }
    }

    public static void compression(){
        // Set up I/O
        try{
        File original = new File("original.txt");
        PrintStream cout = new PrintStream("cout.txt");
        System.setOut(cout);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Test!");
    }
}