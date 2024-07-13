import java.io.*;

public class TargetSearch {

    public static void main(String[] args) {
        // Specify the words to search for
        String[] targetWords = {"QU", "THE"};

        // Input file name
        String inputFile = "permutations9.txt";

        // Output file name for targeted permutations
        String outputFile = "permTargeted9.txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // Read lines from the file
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the target words
                if (containsWords(line, targetWords)) {
                    // Write the line to the output file
                    writer.write("\n" + line + "\n");
                }
            }

            reader.close();
            writer.close();

            System.out.println("Targeted permutations written to " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean containsWords(String text, String[] targetWords) {
        text = text.toUpperCase(); // Convert to uppercase for case-insensitive comparison

        for (String word : targetWords) {
            if (!text.contains(word)) {
                return false;
            }
        }
        return true;
    }
}
