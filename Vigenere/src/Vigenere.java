import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vigenere {
    public static void main(String[] args) {
        String cipherText = readCipherTextFromFile();
        int[] alphaVals = getAlphaVals(cipherText);
        System.out.println("STRIPPED CIPHERTEXT: " + cipherText);
        String decrypted = decrpyt(alphaVals);
        System.out.println("STRIPPED PLAINTEXT: " + decrypted);
        writeDecryptedTextToFile(decrypted.toLowerCase(), "output.txt");
    }

    private static String readCipherTextFromFile() {
        StringBuilder ciphertext = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("cipher1.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove spaces and periods from each line
                String cleanedLine = line.replaceAll("[\\s.,'#27193608();:\"-]", "");
                ciphertext.append(cleanedLine);
            }
        } catch (IOException e) {
            System.out.println("Error reading the ciphertext file: " + e.getMessage());
        }
        return ciphertext.toString();
    }

    // USES CIPHERLETTERS TO GET CORRESPONDING ALPHABET VALUES
    // eg A = 0, B = 1, etc
    public static int[] getAlphaVals(String ciphertext) {
        int[] alphaVals = new int[ciphertext.length()];
        for(int i = 0; i < ciphertext.length(); i++){
            // Obtain the uppercase ascii value and subtract 65 to reset it so that a = 0, b = 1, etc.
            alphaVals[i] = (int) ciphertext.charAt(i) - 65;
        }
        return alphaVals;
    }

    // CREATES A HASHMAP THAT KEEPS ALL REPEATING WORDS IN THE CIPHER AND THEIR STARTING INDEX
    static Map<String, Integer> getRepeatingWords(String cipher) {
        System.out.printf("Finding repeating words of length [%d,%d] please wait...\n",
                3, 10);

        // Key: word, Value: starting index
        Map<String, Integer> repeatingWords = new LinkedHashMap<String, Integer>();
        int totalWords = 0;

        String regex = String.format("(\\S{%d,%d})(?=.*?\\1)", 3, 10);
        Matcher m = Pattern.compile(regex).matcher(cipher);

        while (m.find())
        {
            if (!repeatingWords.containsKey(m.group()))
            {
                totalWords++;
                repeatingWords.put(m.group(), m.start());
            }
        }
        System.out.printf("Found %d repeating words.\n\n", totalWords);
        return repeatingWords;
    }

    // SORTS REPEATINGWORDS BY STARTING INDEX
    static Map<String, Integer> sortByFrequency(Map<String, Integer> repeatingWords, String cipher) {
        // Create a stream of entries and sort them by value (starting index) in increasing order
        return repeatingWords.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // CALCULATES HOW OFTEN A REPEATED WORD OCCURS IN THE CIPHER
    static int calculateFrequency(String word, String cipher) {
        int count = 0;
        int lastIndex = 0;

        while ((lastIndex = cipher.indexOf(word, lastIndex)) != -1) {
            count++;
            lastIndex += word.length();
        }

        return count;
    }

    // CALCULATES DISTANCES BETWEEN FIRST INSTANCE OF A REPEATED WORD AND ALL SUBSEQUENT INSTANCES
    public static List<Integer> calculateDistances(String cipher, String word) {
        List<Integer> distances = new ArrayList<>();

        int newFirst = 0;
        int firstIndex = cipher.indexOf(word);
        int lastIndex = cipher.indexOf(word, firstIndex + 1);

        while (lastIndex != -1) {
            int distance = lastIndex - firstIndex;
            distances.add(distance);

            newFirst = lastIndex;
            lastIndex = cipher.indexOf(word, newFirst + 1);
        }

        return distances;
    }

    public static double[] calculateIOC(int[] alphaVals) {
        int groupSize = 10; // according to Kasiski analysis described in solution document
        int numGroups = (alphaVals.length + groupSize - 1) / groupSize;

        double[] iocValues = new double[numGroups];

        for (int i = 0; i < numGroups; i++) {
            int startIndex = i * groupSize;
            int endIndex = Math.min((i + 1) * groupSize, alphaVals.length);
            int[] group = Arrays.copyOfRange(alphaVals, startIndex, endIndex);
            iocValues[i] = calculateGroupIOC(group);
        }
        return iocValues;
    }

    private static double calculateGroupIOC(int[] group) {
        int[] frequency = new int[26];

        // Count the occurrences of each letter in the group
        for (int alphaVal : group) {
            if (alphaVal >= 0 && alphaVal < 26) {
                frequency[alphaVal]++;
            }
        }

        // Calculate the Index of Coincidence for the group
        double sumIC = 0;
        for (int freq : frequency) {
            sumIC += (double) (freq * (freq - 1));
        }

        return sumIC / (group.length * (group.length - 1));
    }

    // USES INFORMATION DETERMINED TO DECODE VIGENERE CIPHER
    public static String decrpyt(int[] alphaVals) {
        String keyString = "DARKKNIGHT"; // as determined in solution file
        int[] key = getAlphaVals(keyString);
        String plaintext = "";
        int j = 0;
        // get ascii values of plaintext
        for(int i = 0; i < alphaVals.length; i++) {
            // resetting to start of key
            if(j >= key.length) {
                j = 0;
            }
            int pt = 0;
            if(alphaVals[i]-key[j] >= 0) {
                // Perform decryption operation. Add 97 to make it the corresponding lowercase ascii value
                pt = (alphaVals[i] - key[j]) % 26 + 97;
            } else {
                // Modular arithmetic doesn't work as expected when its negative, so use an adjusted calculation
                pt = 26 + (alphaVals[i] - key[j]) + 97;
            }

            // Convert integer to corresponding ascii character
            String ptString = Character.toString((char) pt);
            plaintext += ptString;
            j++;
        }
        return plaintext;
    }

    private static void writeDecryptedTextToFile(String decryptedText, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(decryptedText);
            System.out.println("Decrypted text has been written to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing decrypted text to file: " + e.getMessage());
        }
    }
}