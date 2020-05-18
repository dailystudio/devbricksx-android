package com.dailystudio.devbricksx.compiler.utils;

public class NameUtils {

    public static String capitalizeName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String lowerCamelCaseName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        StringBuilder builder = new StringBuilder(name);

        for (int i = 0; i < name.length(); i++) {
            if (Character.isAlphabetic(name.charAt(i))) {
                builder.setCharAt(i, Character.toLowerCase(name.charAt(i)));
                break;
            }
        }

        return builder.toString();
    }

    public static String underscoreCaseName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        char ch;
        for (int i = 0; i < name.length(); i++) {
            ch = name.charAt(i);
            if (!Character.isAlphabetic(ch)
                    || Character.isLowerCase(ch)) {
                builder.append(ch);
            } else {
                builder.append('_');
                builder.append(Character.toLowerCase(ch));
            }
        }

        return builder.toString();
    }

}
