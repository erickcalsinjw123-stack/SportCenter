package util;

import java.util.ArrayList;
import java.util.List;

public final class ValidadorContrasena {

    private ValidadorContrasena() {
    }

    /**
     * Devuelve la lista de reglas incumplidas (vacía = contraseña válida).
     */
    public static List<String> validar(String p) {
        List<String> errores = new ArrayList<>();
        if (p == null) {
            p = "";
        }
        if (p.length() < 8) {
            errores.add("Mínimo 8 caracteres");
        }
        if (!p.matches(".*[a-z].*")) {
            errores.add("Al menos una letra minúscula");
        }
        if (!p.matches(".*[A-Z].*")) {
            errores.add("Al menos una letra mayúscula");
        }
        if (!p.matches(".*\\d.*")) {
            errores.add("Al menos un dígito");
        }
        if (!p.matches(".*[^A-Za-z0-9].*")) {
            errores.add("Al menos un símbolo especial");
        }
        return errores;
    }

    public static boolean esValida(String p) {
        return validar(p).isEmpty();
    }
}
