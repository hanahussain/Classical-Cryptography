# Permuation
A program that decrypts a permutation cipher.

### Purpose 
This program generates all plaintext permutations for a given ciphertext and then filters them to find the correct one. 

### Running Instructions
Download the .zip file and extract all the files. The code was created using the IntelliJ IDE, but any method of running java will suffice. Open the folder
and run the Permutation.java class. 

The program will output the stripped ciphertext followed by the stripped plaintext. It will also write out the decrypted solution to output.txt

#### Files: 

- cipher2.txt: input ciphertext

- Permutation.java: driver class that handles decryption for this ciphertext

- WritePermutations.java: writes to a file all possible permutations and their corresponding plaintexts for any keylength

- TargetSearch.java: reads the permutation file created by WritePermutations.java and filters based on target words that are required in plaintexts, and writes the plaintexts that pass the search to a new file

- output.txt: output file that contains the decrypted plaintext (stripped).
