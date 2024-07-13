import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Substitution {
    public static void main(String[] args) {
        String cipherText = readCipherTextFromFile();
        int[] alphaVals = getAlphaVals(cipherText);
        System.out.println("STRIPPED CIPHERTEXT: " + cipherText);
        String decrypted = decrypt(alphaVals);
        System.out.println("STRIPPED PLAINTEXT: " + decrypted);
        // Write decryptedText to a file
        writeDecryptedTextToFile(decrypted.toLowerCase(), "output.txt");
    }

    private static String readCipherTextFromFile() {
        StringBuilder ciphertext = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("cipher3.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove spaces and periods from each line
                String cleanedLine = line.replaceAll("[\\s.,'()-]", "");
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

    // FREQUENCY ANALYSIS OF CIPHERTEXT
    private static Map<Character, Integer> analyzeFrequencies(String ciphertext) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        // Count the occurrences of each letter
        for (int i = 0; i < ciphertext.length(); i++) {
            char letter = Character.toUpperCase(ciphertext.charAt(i));
            if (Character.isLetter(letter)) {
                frequencyMap.put(letter, frequencyMap.getOrDefault(letter, 0) + 1);
            }
        }

        // Sort the frequency map in descending order
        Map<Character, Integer> sortedFrequencyMap = frequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Display the frequencies
        for (Map.Entry<Character, Integer> entry : sortedFrequencyMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        return sortedFrequencyMap;
    }

    // GUESSES PLAINTEXT LETTER OF A CIPHERTEXT LETTER
    private static char guessLetter(Map<Character, Integer> frequencyMap, Map<Character, Double> probabilityTable) {
        char guessedLetter = ' ';
        double maxProbability = 0.0;

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            char letter = entry.getKey();
            double probability = (double) entry.getValue() / frequencyMap.size();

            if (probability > maxProbability) {
                maxProbability = probability;
                guessedLetter = letter;
            }
        }

        // If the guessed letter is not in the probability table, return a default value
        return probabilityTable.containsKey(guessedLetter) ? guessedLetter : '?';
    }

    // CREATES AND SORTS THE ENGLISH LETTER PROBABILITY TABLE
    private static Map<Character, Double> createProbabilityTable() {
        Map<Character, Double> probabilityTable = new HashMap<>();
        // Populate the probability table with the given values
        probabilityTable.put('a', 0.082);
        probabilityTable.put('b', 0.015);
        probabilityTable.put('c', 0.028);
        probabilityTable.put('d', 0.043);
        probabilityTable.put('e', 0.127);
        probabilityTable.put('f', 0.022);
        probabilityTable.put('g', 0.02);
        probabilityTable.put('h', 0.061);
        probabilityTable.put('i', 0.07);
        probabilityTable.put('j', 0.002);
        probabilityTable.put('k', 0.008);
        probabilityTable.put('l', 0.04);
        probabilityTable.put('m', 0.024);
        probabilityTable.put('n', 0.067);
        probabilityTable.put('o', 0.075);
        probabilityTable.put('p', 0.019);
        probabilityTable.put('q', 0.001);
        probabilityTable.put('r', 0.06);
        probabilityTable.put('s', 0.063);
        probabilityTable.put('t', 0.091);
        probabilityTable.put('u', 0.028);
        probabilityTable.put('v', 0.01);
        probabilityTable.put('w', 0.023);
        probabilityTable.put('x', 0.001);
        probabilityTable.put('y', 0.02);
        probabilityTable.put('z', 0.001);

        // Sort the probability table in descending order
        Map<Character, Double> sortedProbabilityTable = probabilityTable.entrySet()
                .stream()
                .sorted(Map.Entry.<Character, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return sortedProbabilityTable;
    }

    // BUILD SUBSTITUTION
    public static int[] buildSubsitution() {
        // SUBSTITUTE VALUES DETERMINED BY FREQUENCY ANALYSIS
        //int[] substituteValues = {21, 9, 16, 12, 2, 13, 14, 15, 7, 25, 17, 18, 23, 24, 8, 10, 0, 3, 11, 4, 6, 22, 1, 20, 5, 19};
        int[] substituteValues = {10, 23, 21, 12, 2, 13, 14, 15, 7, 16, 17, 18, 25, 24, 8, 9, 0, 3, 11, 4, 6, 22, 1, 20, 5, 19};
        return substituteValues;
    }

    // DECRYPT SUBSTITUTION CIPHER
    public static String decrypt(int[] alphaValues) {
        String plaintext = "";
        int[] subVals = buildSubsitution();
        for(int i = 0; i < alphaValues.length; i++) {
            int curr = subVals[alphaValues[i]] + 97;
            plaintext += Character.toString((char) curr);
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