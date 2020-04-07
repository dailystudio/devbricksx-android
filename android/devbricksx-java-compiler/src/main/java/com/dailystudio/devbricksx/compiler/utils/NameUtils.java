package com.dailystudio.devbricksx.compiler.utils;

public class NameUtils {

    public static String lowerCamelCaseName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        if (Character.isLowerCase(name.charAt(0))) {
            return name;
        }

        StringBuilder builder = new StringBuilder(name);

        builder.setCharAt(0, Character.toLowerCase(name.charAt(0)));

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
