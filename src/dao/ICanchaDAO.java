package dao;
 
import modelo.Cancha;
 
import java.sql.SQLException;
import java.util.List;
 
public interface ICanchaDAO {
 
    List<Cancha> listar() throws SQLException;
 
    Cancha buscarPorId(int idCancha) throws SQLException;
 
    void insertar(Cancha c) throws SQLException;
 
    void actualizar(Cancha c) throws SQLException;
 
    void eliminar(int idCancha) throws SQLException;
 
    /** Verifica si ya existe una cancha con ese nombre, ignorando la cancha con id = idExcluir (0 para no excluir ninguna). */
    boolean existeNombre(String nombre, int idExcluir) throws SQLException;
}