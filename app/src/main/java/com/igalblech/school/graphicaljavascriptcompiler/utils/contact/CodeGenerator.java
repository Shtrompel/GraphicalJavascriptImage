package com.igalblech.school.graphicaljavascriptcompiler.utils.contact;

/*
 * Used to create user codes
 */
public class CodeGenerator {

    // --Commented out by Inspection (28/10/2020 14:07):private int seed;
    private final java.util.Random random;
    private final char[] NUMBERS = "0123456789".toCharArray();

// --Commented out by Inspection START (28/10/2020 14:07):
//    public CodeGenerator(int seed) {
//        random = new java.util.Random(seed);
//    }
// --Commented out by Inspection STOP (28/10/2020 14:07)

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
