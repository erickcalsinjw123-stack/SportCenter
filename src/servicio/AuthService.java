package servicio;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.PasswordUtil;
import util.Sesion;

import java.sql.SQLException;

public class AuthService {
    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario login(String nombreUsuario, char[] password) throws AuthException {
        if (nombreUsuario == null || nombreUsuario.isBlank() || password.length == 0) {
            throw new AuthException("Ingrese usuario y contraseña.");
        }
        try {
            Usuario u = dao.buscarPorNombre(nombreUsuario.trim());
            // Mismo mensaje si el usuario no existe o la clave es incorrecta
            if (u == null || !PasswordUtil.verificar(new String(password), u.getContrasena())) {
                throw new AuthException("Usuario o contraseña incorrectos.");
            }
            if (!u.isActivo()) {
                throw new AuthException("El usuario está inactivo. Contacte al administrador.");
            }
            Sesion.iniciar(u);
            return u;
        } catch (SQLException e) {
            throw new AuthException("Error de conexión con la base de datos.");
        }
    }
}