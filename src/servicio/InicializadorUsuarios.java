package servicio;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.PasswordUtil;
import util.ValidadorContrasena;

import java.sql.SQLException;

public class InicializadorUsuarios {

    public static void crearUsuariosPorDefecto() throws SQLException {
        UsuarioDAO dao = new UsuarioDAO();
        crearSiNoExiste(dao, "admin",     "Admin@2026", Usuario.ADMIN);
        crearSiNoExiste(dao, "recepcion", "Recep@2026", Usuario.RECEPCIONISTA);
    }

    private static void crearSiNoExiste(UsuarioDAO dao, String user, String pass, String rol)
            throws SQLException {
        if (dao.existe(user)) return;
        if (!ValidadorContrasena.esValida(pass)) {
            throw new IllegalStateException(
                "La contraseña por defecto de " + user + " no cumple la política.");
        }
        dao.insertar(new Usuario(user, PasswordUtil.hash(pass), rol));
    }
}