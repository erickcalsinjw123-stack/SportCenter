
package com.sportcenter.sportcenter.service;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                "^(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*\\d)" +
                "(?=.*[^a-zA-Z0-9\\s])" +
                "(?=\\S{8,}).*$"
            );

    public static boolean esValida(String password) {

        if (password == null) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
