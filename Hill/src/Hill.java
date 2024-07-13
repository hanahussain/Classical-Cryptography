import java.io.*;

public class Hill {
    public static void main(String[] args) {
        String str = readCipherTextFromFile();
        System.out.println("STRIPPED CIPHERTEXT: " + str);
        int[] vals = getAlphaVals(str);
        int[] key = {4,7,3,8};
        int[] invKey = findInverseKey(key, 26);
        String decrypted = decrypt(vals, invKey);
        System.out.println("STRIPPED PLAINTEXT: " + decrypted);
        writeDecryptedTextToFile(decrypted.toLowerCase(), "output.txt");
    }

    // READS CIPHERTEXT FILE INTO STRING AND STRIPS PUNCTUATION/SPACES
    private static String readCipherTextFromFile() {
        StringBuilder ciphertext = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("cipher5.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove spaces and periods from each line
                String cleanedLine = line.replaceAll("[\\s.,']", "");
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

    // FINDS THE INVERSE OF THE KEY FOUND BY HAND
    public static int[] findInverseKey(int[] key, int keySpace) {
        // Step 1: Calculate the determinant
        int determinant = (key[0] * key[3] - key[1] * key[2] + keySpace) % keySpace;

        // Step 2: Calculate the modular multiplicative inverse of the determinant
        int determinantInverse = modInverse(determinant, keySpace);

        // Step 3: Swap the elements in the main diagonal and negate the others
        int[] inverseKey = {key[3], -key[1], -key[2], key[0]}; // Fix assignment ?

        // Step 4: Change the sign of the elements in the off-diagonal
        for (int i = 0; i < inverseKey.length; i++) {
            inverseKey[i] = (inverseKey[i] * determinantInverse) % keySpace;
            if (inverseKey[i] < 0) {
                inverseKey[i] += keySpace;
            }
        }
        return inverseKey;
    }


    // Extended Euclidean Algorithm to find modular multiplicative inverse
    private static int modInverse(int a, int m) {
        int m0 = m;
        int y = 0, x = 1;

        if (m == 1) {
            return 0;
        }

        while (a > 1) {
            int q = a / m;
            int t = m;

            m = a % m;
            a = t;
            t = y;

            y = x - q * y;
            x = t;
        }

        if (x < 0) {
            x += m0;
        }
        return x;
    }

    // DECRYPTS CIPHER MESSAGE USING CALCULATED INVERSE KEY
    public static String decrypt(int[] alphaVals, int[] inverseKey){
        String decryptedText = "";
        int a = inverseKey[0];
        int b = inverseKey[2];
        int c = inverseKey[1];
        int d = inverseKey[3];
        // Take it 2 characters at a time using key
        for(int i = 0; i < alphaVals.length; i += 2) {
            // Perform decryption operation. Add 97 to make it the corresponding lowercase ascii value
            int cipherOne = (a*alphaVals[i] + c*alphaVals[i+1]) % 26 + 97;
            int cipherTwo = (b*alphaVals[i] + d*alphaVals[i+1]) % 26 + 97;

            // Convert integer to corresponding ascii character
            String strCipherOne = Character.toString((char) cipherOne);
            String strCipherTwo = Character.toString((char) cipherTwo);
            decryptedText += strCipherOne + strCipherTwo;
        }
        return decryptedText;
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