package com.igalblech.school.graphicaljavascriptcompiler.utils;

public class CodeGenerator {

    private int seed;
    private java.util.Random random;
    private char[] NUMBERS = "0123456789".toCharArray();

    public CodeGenerator(int seed) {
        random = new java.util.Random(seed);
    }

    public CodeGenerator() {
        random = new java.util.Random();
    }

    public String generate(int length) {
        String ret = "";
        for (int i = 0; i < NUMBERS.length; i++) {
            ret += NUMBERS[random.nextInt(NUMBERS.length)];
        }
        return ret;
    }
}
