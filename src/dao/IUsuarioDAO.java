package dao;
import modelo.Usuario;
import java.sql.SQLException;

public interface IUsuarioDAO {

    Usuario buscarPorNombre(String nombreUsuario) throws SQLException;
    boolean existe(String nombreUsuario) throws SQLException;
    void insertar(Usuario u) throws SQLException;
}