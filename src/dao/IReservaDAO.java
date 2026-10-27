package dao;

import modelo.Reserva;
import java.sql.SQLException;
import java.util.List;

public interface IReservaDAO {

    int insertar(Reserva reserva) throws SQLException;

    List<Reserva> listar() throws SQLException;

    boolean actualizar(Reserva reserva) throws SQLException;

    boolean cancelar(int idReserva) throws SQLException;
}
