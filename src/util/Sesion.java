package util;

import modelo.Usuario;

public final class Sesion {
    private static Usuario usuarioActual;
    private Sesion() {}
    public static void iniciar(Usuario u) { usuarioActual = u; }
    public static Usuario getUsuario() { return usuarioActual; }
    public static void cerrar() { usuarioActual = null; }
}