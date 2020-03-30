// On my honor, I have neither given nor received unauthorized aid on this assignment.

import java.util.*;
import java.io.*;
import java.util.Map.Entry;

public class SIM {
    public static void main(String[] args) throws FileNotFoundException {
        // Compression
        // if (args[0].equals("1")) {
        compression();
        // }
        // Decompression
        // else if (args[0].equals("2")) {
        // decompression();
        // } else {
        // System.out.println("Error! Invalid command entered. Please try again!");
        // }
    }

    public static void compression() throws FileNotFoundException {
        // Output setup
        PrintStream cout = new PrintStream("cout.txt");
        System.setOut(cout);
        // Input Setup
        Vector<String> inst = new Vector<String>();
        String[] dict = new String[8];

        // Take input
        inst = instReader();
        dict = dictionary(inst);

        String[][] compArray = comparison(inst, dict);

    }
    public static String[][]  comparison(Vector<String> instruction, String[] dictionary) {
        String[][] comparisonArray = new String[instruction.size()][8];
        

        for(int i = 0; i < instruction.size(); i++) {
            for(int j = 0; j < 8; j++){
                StringBuilder str = new StringBuilder();
                for(int k = 0; k < 32; k++) {
                        char xor = Character.forDigit(Character.getNumericValue(instruction.get(i).charAt(k))^Character.getNumericValue(dictionary[j].charAt(k)), 10);
                        str.append(xor);      
                }
                comparisonArray[i][j] = str.toString();
            }
        }

        //DEBUG: 
        for(int a = 0; a < instruction.size(); a++) {
            //DEBUG:
            System.out.println("Comparison Entry #" + a + " " + instruction.get(a));
            //
            for(int b = 0; b < 8; b++){

                System.out.println("Dict " + b + ": " + comparisonArray[a][b]);
                
            }
        }
        //

        return comparisonArray;
    }
    public static Vector<String> instReader() {
        Vector<String> instructions = new Vector<String>();
        try {
            // Input file setup
            File original = new File("original.txt");
            Scanner scan = new Scanner(original);

            // Add raw binaries to instruction vector
            while (scan.hasNextLine()) {
                instructions.add(scan.nextLine());
            }
            scan.close();

            // DEBUG: print out instruction vector
            for (int i = 0; i < instructions.size(); i++) {
                System.out.println(instructions.get(i));
            }
            System.out.println("xxxx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return instructions;
    }

    public static String[] dictionary(Vector<String> instructions) {
        String[] dictionary = new String[8];
        Map<String, Integer> countMap = new LinkedHashMap<>();
        // Put instructions into map
        for (int i = 0; i < instructions.size(); i++) {
            if (countMap.containsKey(instructions.get(i))) {
                countMap.put(instructions.get(i), countMap.get(instructions.get(i)) + 1);
            } else {
                countMap.put(instructions.get(i), 1);
            }
        }

        ArrayList<Entry<String, Integer>> fullDictionary = new ArrayList<>(countMap.entrySet());

        Collections.sort(fullDictionary, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                if (e1.getValue() != null && e2.getValue() != null) {
                    return e2.getValue().compareTo(e1.getValue());
                } else {
                    System.out.println("Error! Null value in map.");
                    return -1;
                }
            }
        });

        for (int i = 0; i < 8; i++) {
            // DEBUG:
            System.out.println(fullDictionary.get(i).getKey());
            // System.out.println("writing to array...");
            //
            dictionary[i] = fullDictionary.get(i).getKey();
        }
        System.out.println("xxxx");
        return dictionary;
    }

}
