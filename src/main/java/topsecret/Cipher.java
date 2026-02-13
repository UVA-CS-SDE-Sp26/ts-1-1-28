package topsecret;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// this class reads a key file and uses it to decipher encoded text
// the key has two lines, first is the real alphabet and second is the swapped version
public class Cipher {

    private HashMap<Character, Character> cipherMap;
    private String path;
    private boolean loaded;
    private String err;

    private static final String DEFAULT = "ciphers/key.txt";

    // default constructor uses the default key
    public Cipher() {
        this(DEFAULT);
    }

    // you can also pass in a different key file
    public Cipher(String keyPath) {
        this.path = keyPath;
        this.cipherMap = new HashMap<>();
        this.loaded = false;
        this.err = null;
        readKey(keyPath);
    }

    // reads the key file and sets up the hashmap
    private void readKey(String filePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line1 = reader.readLine(); // plain chars
            String line2 = reader.readLine(); // cipher chars

            String line3 = reader.readLine(); // shouldnt exist
            reader.close();

            // need both lines
            if (line1 == null || line2 == null) {
                err = "Key file needs exactly two lines";
                loaded = false;
                return;
            }

            // no extra lines allowed
            if (line3 != null && line3.trim().length() > 0) {
                err = "Key file needs exactly two lines";
                loaded = false;
                return;
            }

            if (line1.length() != line2.length()) {
                err = "Key lines have to be the same length. Got "
                    + line1.length() + " and " + line2.length();
                loaded = false;
                return;
            }

            if (line1.length() == 0) {
                err = "Key lines cant be empty";
                loaded = false;
                return;
            }

            // make sure no repeating chars in the cipher line
            HashSet<Character> check = new HashSet<>();
            for (int i = 0; i < line2.length(); i++) {
                if (check.contains(line2.charAt(i))) {
                    err = "Found duplicate '" + line2.charAt(i) + "' in cipher line";
                    loaded = false;
                    return;
                }
                check.add(line2.charAt(i));
            }

            // same thing for plain line
            check.clear();
            for (int i = 0; i < line1.length(); i++) {
                if (check.contains(line1.charAt(i))) {
                    err = "Found duplicate '" + line1.charAt(i) + "' in plain line";
                    loaded = false;
                    return;
                }
                check.add(line1.charAt(i));
            }

            // now actually build the map
            // cipher char -> plain char
            cipherMap.clear();
            for (int i = 0; i < line1.length(); i++) {
                cipherMap.put(line2.charAt(i), line1.charAt(i));
            }

            loaded = true;
            err = null;

        } catch (IOException e) {
            err = "Couldnt read key file: " + filePath;
            loaded = false;
        }
    }

    // returns true if key loaded fine
    public boolean validateKey() {
        return loaded;
    }

    // returns error msg or null if everything is fine
    public String getValidationError() {
        return err;
    }

    // deciphers the text using the map we built
    // anything not in the map just stays the same (spaces, punctuation etc)
    public String decipher(String input) {
        if (loaded == false) {
            throw new IllegalStateException("Key isnt loaded so cant decipher. "
                + (err != null ? err : ""));
        }

        if (input == null) {
            return null;
        }

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (cipherMap.containsKey(ch)) {
                out.append(cipherMap.get(ch));
            } else {
                // just keep it as is
                out.append(ch);
            }
        }

        return out.toString();
    }

    // getter for the key path
    public String getKeyFilePath() {
        return path;
    }

    // returns a copy so nothing breaks if someone messes with it
    public Map<Character, Character> getDecipherMap() {
        return new HashMap<>(cipherMap);
    }
}
