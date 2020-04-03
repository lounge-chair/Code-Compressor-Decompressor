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
        // Prepare comparison strings for all instructions, and place them in an array
        String[][] compareArray = comparison(inst, dict);
        String[][] compressed = new String[inst.size()][4];
        // TODO: Implement Run-Time Encoding counter

        // COMPRESSION PRIORITY:
        // 1. RLE - Refer to RLE counter
        // 2. Direct Matching - matches dictionary entry exactly
        // 3. 1-bit mismatch - single '1' found in whole string
        // 4. 2-bit consecutive mismatch - single '11' found in whole string
        // 5. Bitmask-based compression - '1's found within four bits of each other
        // 6. 2-bit anywhere mismatch - non-consecutive '1.....1' found in whole string
        // 7. Original binary - none of the above

        String rleCheck = "";
        String current = "";
        int rleCount = -1;
        boolean rlePossible = false;
        boolean isCompressed;
        // Loop through each instruction
        for (int i = 0; i < inst.size(); i++) {
            isCompressed = false;
            // 1. RLE //

            current = inst.get(i);
            if (!current.equals(rleCheck)) {
                if (rlePossible) {
                    compressed[i - 1][0] = "000";
                    compressed[i - 1][1] = String.format("%2s", Integer.toBinaryString(rleCount)).replace(' ', '0');
                    // isCompressed = true;
                    rlePossible = false;
                    rleCount = -1;
                }
                rleCheck = current;
            } else if (current.equals(rleCheck)) {
                rlePossible = true;
                isCompressed = true;
                if (++rleCount >= 3) {
                    compressed[i][0] = "000";
                    compressed[i][1] = String.format("%2s", Integer.toBinaryString(rleCount)).replace(' ', '0');
                    isCompressed = true;
                    rlePossible = false;
                    rleCount = -1;
                    rleCheck = "";
                }
            }

            // 2. Direct Matching - matches dictionary entry exactly
            if (!isCompressed) {
                for (int j = 0; j < 8; j++) {
                    if (inst.get(i).equals(dict[j])) {
                        compressed[i][0] = "101";
                        compressed[i][1] = String.format("%3s", Integer.toBinaryString(j)).replace(' ', '0');
                        isCompressed = true;
                    }
                }
            }
            // 3. 1-bit mismatch - single '1' found in whole string (010)
            if (!isCompressed) {
                // Check against each dictionary entry
                for (int j = 0; j < 8; j++) {
                    // Initialize local vars
                    int oneCount = 0;
                    String mismatchLocation = "ERROR";
                    String dictIndex = "ERROR";
                    // Check each char in comparison array for a '1' and count total # of '1's
                    for (int k = 0; k < 32; k++) {
                        if (compareArray[i][j].charAt(k) == '1') {
                            oneCount++;
                            mismatchLocation = String.format("%5s", Integer.toBinaryString(k)).replace(' ', '0');
                            dictIndex = String.format("%3s", Integer.toBinaryString(j)).replace(' ', '0');
                        }
                    }
                    if (oneCount == 1) {
                        compressed[i][0] = "010";
                        compressed[i][1] = mismatchLocation;
                        compressed[i][2] = dictIndex;
                        isCompressed = true;
                        break;
                    }
                }
            }
            // 4. 2-bit consecutive mismatch - single '11' found in whole string
            if (!isCompressed) {
                // Check against each dictionary entry
                for (int j = 0; j < 8; j++) {
                    // Initialize local vars
                    int oneCount = 0;
                    boolean oneoneFound = false;
                    String mismatchLocation = "ERROR";
                    String dictIndex = "ERROR";

                    // Check each char in comparison array for a '11' and count total # of '1's
                    for (int k = 0; k < 32; k++) {
                        if (compareArray[i][j].charAt(k) == '1') {
                            oneCount++;
                        }
                        if (k + 1 != 32 && compareArray[i][j].substring(k, k + 2).equals("11")) {
                            oneoneFound = true;
                            mismatchLocation = String.format("%5s", Integer.toBinaryString(k)).replace(' ', '0');
                            dictIndex = String.format("%3s", Integer.toBinaryString(j)).replace(' ', '0');
                        }
                    }
                    if (oneoneFound && oneCount == 2) {
                        compressed[i][0] = "011";
                        compressed[i][1] = mismatchLocation;
                        compressed[i][2] = dictIndex;
                        isCompressed = true;
                        break;
                    }
                }
            }
            // 5. Bitmask-based compression - '1's found within four bits of each other
            if (!isCompressed) {
                for (int j = 0; j < 8; j++) {
                    // Initialize local vars
                    int oneCount = 0;
                    int subCount = 0;
                    String startLocation = "ERROR";
                    String dictIndex = "ERROR";
                    Vector<String> possible = new Vector<String>();
                    Vector<String> possibleLoc = new Vector<String>();

                    // Count total # of '1's
                    for (int k = 0; k < 32; k++) {
                        if (compareArray[i][j].charAt(k) == '1') {
                            oneCount++;
                        }
                    }
                    //
                    for (int k = 0; k < 28; k++) {
                        subCount = 0;
                        for (int m = 0; m < 4; m++) {
                            if (compareArray[i][j].substring(k, k + 4).charAt(m) == '1') {
                                subCount++;
                            }
                        }
                        if (subCount == oneCount) {
                            possible.add(compareArray[i][j].substring(k, k + 4));
                            possibleLoc.add(String.format("%5s", Integer.toBinaryString(k)).replace(' ', '0'));
                            dictIndex = String.format("%3s", Integer.toBinaryString(j)).replace(' ', '0');
                        }
                    }

                    for (int n = 0; n < possible.size(); n++) {
                        if (possible.get(n).charAt(0) == '1') {
                            compressed[i][0] = "001";
                            compressed[i][1] = possibleLoc.get(n);
                            compressed[i][2] = possible.get(n);
                            compressed[i][3] = dictIndex;
                            isCompressed = true;
                            break;
                        }
                    }

                    // if (oneoneFound && oneCount == 2) {
                    // compressed[i][0] = "011";
                    // compressed[i][1] = mismatchLocation;
                    // compressed[i][2] = dictIndex;
                    // isCompressed = true;
                    // break;
                    // }
                }
            }
            // 6. 2-bit anywhere mismatch - non-consecutive '1.....1' found in whole string
            if (!isCompressed) {
                // Check against each dictionary entry
                for (int j = 0; j < 8; j++) {
                    // Initialize local vars
                    int oneCount = 0;
                    String mismatchLoc1 = "ERROR";
                    String mismatchLoc2 = "ERROR";
                    String dictIndex = "ERROR";
                    boolean located1 = false;
                    boolean located2 = false;

                    // Check each char in comparison array for a '11' and count total # of '1's
                    for (int k = 0; k < 32; k++) {
                        if (!located1 && compareArray[i][j].charAt(k) == '1') {
                            oneCount++;
                            mismatchLoc1 = String.format("%5s", Integer.toBinaryString(k)).replace(' ', '0');
                            located1 = true;
                        } else if (located1 && !located2 && compareArray[i][j].charAt(k) == '1') {
                            oneCount++;
                            mismatchLoc2 = String.format("%5s", Integer.toBinaryString(k)).replace(' ', '0');
                            dictIndex = String.format("%3s", Integer.toBinaryString(j)).replace(' ', '0');
                            located2 = true;
                        } else if (located1 && located2 && compareArray[i][j].charAt(k) == '1') {
                            oneCount++;
                            break;
                        }
                    }
                    if (located1 && located2 && oneCount == 2) {
                        compressed[i][0] = "100";
                        compressed[i][1] = mismatchLoc1;
                        compressed[i][2] = mismatchLoc2;
                        compressed[i][3] = dictIndex;
                        isCompressed = true;
                        break;
                    }
                }
            }
            // 7. Original binary - none of the above
            if (!isCompressed) {
                compressed[i][0] = "110";
                compressed[i][1] = inst.get(i);
            }
        }

        // Print compressed code
        printCompression(compressed);
        // Print dictionary
        System.out.println("\nxxxx");
        for (int i = 0; i < 8; i++) {
            System.out.print(dict[i]);
            if (i != 7) {
                System.out.println();
            }
        }
    }

    public static void printCompression(String[][] compressed) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < compressed.length; i++) {
            for (int j = 0; j < 4; j++) {
                if (compressed[i][j] != null) {
                    builder.append(compressed[i][j]);
                } else {
                    break;
                }
            }
        }

        String compString = builder.toString();

        int compLength = compString.length();
        int compOffset = 32 - compLength % 32;

        for (int k = 0; k < compLength; k++) {
            if (k != 0 && k % 32 == 0) {
                System.out.println();
            }
            System.out.print(compString.charAt(k));
        }
        for (int c = 0; c < compOffset; c++) {
            System.out.print("1");
        }
    }

    public static String[][] comparison(Vector<String> instruction, String[] dictionary) {
        String[][] comparisonArray = new String[instruction.size()][8];

        for (int i = 0; i < instruction.size(); i++) {
            for (int j = 0; j < 8; j++) {
                StringBuilder str = new StringBuilder();
                for (int k = 0; k < 32; k++) {
                    char xor = Character.forDigit(Character.getNumericValue(instruction.get(i).charAt(k))
                            ^ Character.getNumericValue(dictionary[j].charAt(k)), 10);
                    str.append(xor);
                }
                comparisonArray[i][j] = str.toString();
            }
        }
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
            dictionary[i] = fullDictionary.get(i).getKey();
        }
        return dictionary;
    }
}