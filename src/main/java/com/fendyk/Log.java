package com.fendyk;

public class Log {

    public static String Error(String str) {
        return "\uD83C\uDF39 " + str;
    }

    public static String Warning(String str) {
        return "\uD83C\uDF44 " + str;
    }

    public static String Info(String str) {
        return "\uD83C\uDF4F " + str;
    }

    public static String Success(String str) {
        return "\uD83D\uDD25 " + str;
    }

}
