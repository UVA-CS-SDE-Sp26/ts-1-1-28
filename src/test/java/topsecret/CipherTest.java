package topsecret;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// tests for cipher class
public class CipherTest {

    @TempDir
    File tempDir;

    // makes a test key file so we dont mess with the real one
    private String writeKey(String line1, String line2) throws IOException {
        File f = new File(tempDir, "testkey.txt");
        FileWriter fw = new FileWriter(f);
        fw.write(line1 + "\n");
        fw.write(line2 + "\n");
        fw.close();
        return f.getAbsolutePath();
    }

    // for edge cases where i need to control exactly whats in the file
    private String writeRaw(String stuff) throws IOException {
        File f = new File(tempDir, "testkey.txt");
        FileWriter fw = new FileWriter(f);
        fw.write(stuff);
        fw.close();
        return f.getAbsolutePath();
    }

    // shortcut to make the actual key our project uses
    private String projectKey() throws IOException {
        return writeKey(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",
            "bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890a"
        );
    }

    // --- validation stuff ---

    @Test
    public void testGoodKey() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertTrue(c.validateKey());
    }

    @Test
    public void testLengthsDontMatch() throws IOException {
        Cipher c = new Cipher(writeKey("abcdef", "zyxwv"));
        assertFalse(c.validateKey());
        assertTrue(c.getValidationError().contains("same length"));
    }

    @Test
    public void testRepeatedCharCipher() throws IOException {
        // z appears twice in cipher line
        Cipher c = new Cipher(writeKey("abcdef", "zzyxwv"));
        assertFalse(c.validateKey());
        assertTrue(c.getValidationError().contains("duplicate"));
    }

    @Test
    public void testRepeatedCharPlain() throws IOException {
        Cipher c = new Cipher(writeKey("aabcde", "zyxwvu"));
        assertFalse(c.validateKey());
        assertTrue(c.getValidationError().contains("duplicate"));
    }

    @Test
    public void testFileDoesntExist() {
        Cipher c = new Cipher("/some/fake/path.txt");
        assertFalse(c.validateKey());
        assertTrue(c.getValidationError().contains("read"));
    }

    @Test
    public void testEmptyFile() throws IOException {
        Cipher c = new Cipher(writeRaw(""));
        assertFalse(c.validateKey());
    }

    @Test
    public void testJustOneLine() throws IOException {
        Cipher c = new Cipher(writeRaw("abcdefghijklmnopqrstuvwxyz\n"));
        assertFalse(c.validateKey());
    }

    @Test
    public void testBothLinesEmpty() throws IOException {
        Cipher c = new Cipher(writeKey("", ""));
        assertFalse(c.validateKey());
    }

    @Test
    public void testExtraThirdLine() throws IOException {
        File f = new File(tempDir, "testkey.txt");
        FileWriter fw = new FileWriter(f);
        fw.write("abcdef\n");
        fw.write("zyxwvu\n");
        fw.write("this shouldnt be here\n");
        fw.close();
        Cipher c = new Cipher(f.getAbsolutePath());
        assertFalse(c.validateKey());
    }

    // --- actually deciphering stuff ---
    // reminder: our key shifts by 1
    // so b->a, c->b, d->c etc
    // "ifmmp" deciphers to "hello"

    @Test
    public void testBasicDecipher() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("hello", c.decipher("ifmmp"));
    }

    @Test
    public void testSingleChar() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("a", c.decipher("b"));
    }

    @Test
    public void testEmptyString() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("", c.decipher(""));
    }

    @Test
    public void testNullString() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertNull(c.decipher(null));
    }

    // --- spaces punctuation uppercase numbers ---

    @Test
    public void testWithSpacesAndPunctuation() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("hello, world!", c.decipher("ifmmp, xpsme!"));
    }

    @Test
    public void testCapitalLetters() throws IOException {
        Cipher c = new Cipher(projectKey());
        // H ciphers to I, so I deciphers back to H
        assertEquals("Hello", c.decipher("Ifmmp"));
    }

    @Test
    public void testAllCaps() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("HELLO", c.decipher("IFMMP"));
    }

    @Test
    public void testWithNumbers() throws IOException {
        Cipher c = new Cipher(projectKey());
        // 1 ciphers to 2, so 2 deciphers to 1
        assertEquals("1234", c.decipher("2345"));
    }

    @Test
    public void testWraparound() throws IOException {
        // the last char in cipher line is 'a' which maps to '0'
        Cipher c = new Cipher(projectKey());
        assertEquals("0", c.decipher("a"));
    }

    @Test
    public void testSpacesAreKept() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("  hello  ", c.decipher("  ifmmp  "));
    }

    @Test
    public void testJustPunctuation() throws IOException {
        // punctuation isnt in the key at all
        Cipher c = new Cipher(projectKey());
        assertEquals("!@#$%^&*()", c.decipher("!@#$%^&*()"));
    }

    @Test
    public void testWholeSentence() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("The quick brown fox", c.decipher("Uif rvjdl cspxo gpy"));
    }

    // --- using a different key ---

    @Test
    public void testAlternateKey() throws IOException {
        // reverse key instead
        Cipher c = new Cipher(writeKey("abcdef", "fedcba"));
        assertTrue(c.validateKey());
        assertEquals("abc", c.decipher("fed"));
    }

    @Test
    public void testTinyKey() throws IOException {
        Cipher c = new Cipher(writeKey("abc", "xyz"));
        assertTrue(c.validateKey());
        assertEquals("abc", c.decipher("xyz"));
    }

    // --- error handling ---

    @Test
    public void testDecipherFailsWithBadKey() {
        Cipher c = new Cipher("doesnt_exist.txt");
        assertThrows(IllegalStateException.class, () -> {
            c.decipher("hello");
        });
    }

    @Test
    public void testDecipherFailsWithMismatchedKey() throws IOException {
        Cipher c = new Cipher(writeKey("abcdef", "zyxwv"));
        assertThrows(IllegalStateException.class, () -> {
            c.decipher("hello");
        });
    }

    @Test
    public void testNoErrorOnGoodKey() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertNull(c.getValidationError());
    }

    @Test
    public void testHasErrorOnBadKey() {
        Cipher c = new Cipher("nah.txt");
        assertNotNull(c.getValidationError());
    }

    // --- getters ---

    @Test
    public void testPathGetter() throws IOException {
        String p = projectKey();
        Cipher c = new Cipher(p);
        assertEquals(p, c.getKeyFilePath());
    }

    @Test
    public void testDefaultPath() {
        Cipher c = new Cipher();
        assertEquals("ciphers/key.txt", c.getKeyFilePath());
    }

    @Test
    public void testMapContents() throws IOException {
        Cipher c = new Cipher(writeKey("abc", "xyz"));
        Map<Character, Character> m = c.getDecipherMap();
        assertEquals('a', m.get('x'));
        assertEquals('b', m.get('y'));
        assertEquals('c', m.get('z'));
    }

    @Test
    public void testMapIsCopy() throws IOException {
        Cipher c = new Cipher(projectKey());
        Map<Character, Character> m = c.getDecipherMap();
        m.put('!', '!'); // mess with the copy
        assertFalse(c.getDecipherMap().containsKey('!')); // original should be fine
    }

    // --- misc ---

    @Test
    public void testMapHas62Entries() throws IOException {
        // 26 lower + 26 upper + 10 digits
        Cipher c = new Cipher(projectKey());
        assertEquals(62, c.getDecipherMap().size());
    }

    @Test
    public void testNewlines() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals("hello\nworld", c.decipher("ifmmp\nxpsme"));
    }

    @Test
    public void testLongerText() throws IOException {
        Cipher c = new Cipher(projectKey());
        assertEquals(
            "This is a longer string that we need to test.",
            c.decipher("Uijt jt b mpohfs tusjoh uibu xf offe up uftu.")
        );
    }
}
