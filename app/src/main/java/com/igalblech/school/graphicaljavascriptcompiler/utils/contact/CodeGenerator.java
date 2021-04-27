package com.igalblech.school.graphicaljavascriptcompiler.utils.contact;

/**
 * Generates random code for each user when needed for logging in for
 * the first time or when a password if forgotten.
 */
public class CodeGenerator {

    private final java.util.Random random;
    private final char[] NUMBERS = "0123456789".toCharArray();

    public CodeGenerator() {
        random = new java.util.Random();
    }

    public String generate(int length) {
        StringBuilder ret = new StringBuilder ( );
        for (int i = 0; i < length; i++) {
            ret.append ( NUMBERS[random.nextInt ( NUMBERS.length )] );
        }
        return ret.toString ( );
    }
}
