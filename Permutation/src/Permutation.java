import java.io.*;

public class Permutation {

    public static void main(String[] args) {
        String ciphertext = readCipherTextFromFile();
        System.out.println("STRIPPED CIPHERTEXT: " + ciphertext);

        // Define the fixed permutation - FOUND VIA OTHER FILES
        int[] permutation = {3, 4, 0, 8, 6, 1, 7, 5, 2};

        // Apply the permutation to the ciphertext
        String decryptedText = decryptWithPermutation(ciphertext, 9, permutation);

        // Print the result
        System.out.println("STRIPPED PLAINTEXT: " + decryptedText.toLowerCase());

        // Write decryptedText to a file
        writeDecryptedTextToFile(decryptedText.toLowerCase(), "output.txt");
    }

    private static String readCipherTextFromFile() {
        StringBuilder ciphertext = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("cipher2.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove spaces and periods from each line
                String cleanedLine = line.replaceAll("[\\s.70]", "");
                ciphertext.append(cleanedLine);
            }
        } catch (IOException e) {
            System.out.println("Error reading the ciphertext file: " + e.getMessage());
        }
        return ciphertext.toString();
    }

    private static String decryptWithPermutation(String ciphertext, int keyLength, int[] permutation) {
        char[] chars = ciphertext.toCharArray();
        int length = chars.length;

        StringBuilder decryptedText = new StringBuilder();

        for (int i = 0; i < length; i += keyLength) {
            for (int j = 0; j < keyLength; j++) {
                int index = i + permutation[j];
                if (index < length) {
                    decryptedText.append(chars[index]);
                }
            }
        }

        return decryptedText.toString();
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
