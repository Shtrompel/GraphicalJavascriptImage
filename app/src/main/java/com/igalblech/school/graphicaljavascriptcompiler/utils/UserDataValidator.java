package com.igalblech.school.graphicaljavascriptcompiler.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserDataValidator {

    public static String STRING_USERNAME = "Username";
    public static String STRING_PASSWORD = "Password";
    public static String STRING_EMAIL = "email";
    public static String STRING_PHONE = "Phone";


    public static int MIN_PASSWORD = 8;
    public static int MAX_PASSWORD = 24;
    public static int MIN_USERNAME = 8;
    public static int MAX_USERNAME = 24;
    public static int MAX_EMAIL = 254;

    public static final int ERROR_NONE = 0;
    public static final int ERROR_EMPTY = 1;
    public static final int ERROR_PASSWORD_LENGTH = 2;
    public static final int ERROR_NO_LETTER = 3;
    public static final int ERROR_NO_NUMBER = 4;
    public static final int ERROR_NO_AT_SING = 5;
    public static final int ERROR_BAD_FORMATTING = 6;
    public static final int ERROR_USERNAME_LENGTH = 7;
    public static final int ERROR_PHONE_LENGTH = 8;
    public static final int ERROR_ONLY_NUMBER = 9;
    public static final int ERROR_NO_SPACES = 10;
    public static final int ERROR_CLASHES_USERNAME = 11;
    public static final int ERROR_CLASHES_PASSWORD = 12;
    public static final int ERROR_CLASHES_EMAIL = 13;
    public static final int ERROR_CLASHES_PHONE = 14;
    public static final int ERROR_USERNAME_DIF_PASSWORD = 15;
    public static final int ERROR_DIF_PASSWORDS = 16;
    public static final int ERROR_EMAIL_LENGTH = 17;


    public static final char[] ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final char[] ALPHABET_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static final char[] NUMBERS = "0123456789".toCharArray();

    // https://www.journaldev.com/641/regular-expression-phone-number-validation-in-java
    private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }

    private static int getRecurrenceStringCharArr(String str, char[] arr) {
        int c = 0;
        for (int i = 0; i < str.length(); i++) {
            for (int j = 0; j < arr.length; j++) {
                if (str.charAt(i) == arr[j])
                    c++;
            }
        }
        return c;
    }

    public static String errorToString(int v) {
        return errorToString(v, "Everything");
    }

        public static String errorToString(int v, String s) {
        switch (v) {
            case ERROR_NONE:
                return String.format("%s is okay!", s);
            case ERROR_EMPTY:
                return String.format("%s is empty!", s);
            case ERROR_PASSWORD_LENGTH:
                return String.format("%s should have %d to %d characters!", s, MIN_PASSWORD, MAX_PASSWORD);
            case ERROR_NO_LETTER:
                return String.format("%s should have at least one letter!", s);
            case ERROR_NO_NUMBER:
                return String.format("%s should have at least one number!", s);
            case ERROR_NO_AT_SING:
                return String.format("%s missing at sing (@) !", s);
            case ERROR_BAD_FORMATTING:
                return String.format("%s has invalid/bad formatting!", s);
            case ERROR_USERNAME_LENGTH:
                return  String.format("%s should have %d to %d characters!", s, MIN_USERNAME, MAX_USERNAME);
            case ERROR_PHONE_LENGTH:
                return  String.format("%s should have 10 numbers!", s);
            case ERROR_NO_SPACES:
                return  String.format("%s shouldn't have any spaces!", s);
            case ERROR_ONLY_NUMBER:
                return  String.format("%s should only have numbers!", s);
            case ERROR_CLASHES_PASSWORD:
                return "Password has been already used! Try another one.";
            case ERROR_CLASHES_USERNAME:
                return "Username has been already used! Try another one.";
            case ERROR_CLASHES_EMAIL:
                return "Email has been already used! Try another one.";
            case ERROR_CLASHES_PHONE:
                return "Phone has been already used!";
            case ERROR_USERNAME_DIF_PASSWORD:
                return "You cant make your username and password the same!";
            case ERROR_DIF_PASSWORDS:
                return "Passwords are different!";
            case ERROR_EMAIL_LENGTH:
                return String.format("%s should have less than %d characters!", s, MAX_EMAIL);
                default:
                    return String.format("%s have an unknown error!", s);
        }
    }

    public static int validatePassword(String str) {
        if (str.length() == 0)
            return ERROR_EMPTY;

        if (MIN_PASSWORD > str.length() || str.length() > MAX_PASSWORD)
            return ERROR_PASSWORD_LENGTH;

        if (getRecurrenceStringCharArr(str, ALPHABET_LOWERCASE) == 0 &&
                getRecurrenceStringCharArr(str, ALPHABET_UPPERCASE) == 0)
            return ERROR_NO_LETTER;

        if (getRecurrenceStringCharArr(str, NUMBERS) == 0)
            return ERROR_NO_NUMBER;

        if (getRecurrenceStringCharArr(str, new char[]{' '}) > 0)
            return ERROR_NO_SPACES;

        return ERROR_NONE;
    }

    public static int validateEmail(String str) {
        if (str.length() == 0)
            return ERROR_EMPTY;

        if (!str.contains("@"))
            return ERROR_NO_AT_SING;

        if (str.charAt(0) == '@' || str.charAt(str.length() - 1) == '@')
            return ERROR_BAD_FORMATTING;

        if (getRecurrenceStringCharArr(str, new char[]{' '}) > 0)
            return ERROR_NO_SPACES;

        return ERROR_NONE;
    }

    public static int validateUsername(String str) {
        if (str.length() == 0)
            return ERROR_EMPTY;

        if (MIN_USERNAME > str.length() || str.length() > MAX_USERNAME)
            return ERROR_PASSWORD_LENGTH;

        if (getRecurrenceStringCharArr(str, ALPHABET_LOWERCASE) == 0 &&
                getRecurrenceStringCharArr(str, ALPHABET_UPPERCASE) == 0)
            return ERROR_NO_LETTER;

        if (getRecurrenceStringCharArr(str, new char[]{' '}) > 0)
            return ERROR_NO_SPACES;

        return ERROR_NONE;
    }

    public static int validatePhone(String str) {
        if (str.length() == 0)
            return ERROR_EMPTY;

        //if (str.length() != 10)
        //    return ERROR_PHONE_LENGTH;

        if (getRecurrenceStringCharArr(str, ALPHABET_LOWERCASE) > 0 ||
                getRecurrenceStringCharArr(str, ALPHABET_UPPERCASE) > 0)
            return ERROR_ONLY_NUMBER;

        if (getRecurrenceStringCharArr(str, new char[]{' '}) > 0)
            return ERROR_NO_SPACES;

        if (!validatePhoneNumber(str))
            return ERROR_BAD_FORMATTING;

        return ERROR_NONE;
    }

    public static int checkForClashes(UserData main, UserData other) {

        if (!other.isVerified ())
            return ERROR_NONE;

        if (main.getUsername().equals(other.getUsername()))
            return ERROR_CLASHES_USERNAME;

        if (main.getPassword().equals(other.getPassword()))
            return ERROR_CLASHES_PASSWORD;

        if (main.getEmail().equals(other.getEmail()))
            return ERROR_CLASHES_EMAIL;

        if (main.getPhone().equals(other.getPhone()))
            return ERROR_CLASHES_PHONE;

        return ERROR_NONE;
    }

}
