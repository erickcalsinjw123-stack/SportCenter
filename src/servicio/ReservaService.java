package servicio;

import dao.ReservaDAO;
import modelo.Reserva;
import util.Sesion;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaService {

    private final ReservaDAO dao = new ReservaDAO();

    public int registrar(Reserva reserva) throws SQLException {
        validar(reserva);
        comprobarDisponibilidad(reserva);

        reserva.setEstado(Reserva.CONFIRMADA);
        return dao.insertar(reserva);
    }

    public List<Reserva> listar() throws SQLException {
        return dao.listar();
    }

    public boolean modificar(Reserva reserva) throws SQLException {
        validar(reserva);
        comprobarDisponibilidad(reserva);

        return dao.actualizar(reserva);
    }

    public boolean cancelar(int idReserva) throws SQLException {
        if (idReserva <= 0) {
            throw new IllegalArgumentException(
                    "Seleccione una reserva válida.");
        }

        return dao.cancelar(idReserva);
    }

    private void validar(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "No se recibieron los datos de la reserva.");
        }

        if (reserva.getIdCliente() <= 0) {
            throw new IllegalArgumentException(
                    "Seleccione un cliente válido.");
        }

        if (reserva.getIdCancha() <= 0) {
            throw new IllegalArgumentException(
                    "Seleccione una cancha válida.");
        }

        if (reserva.getFechaInicio() == null
                || reserva.getFechaFin() == null) {
            throw new IllegalArgumentException(
                    "Ingrese la fecha y las horas de la reserva.");
        }

        if (!reserva.getFechaFin().isAfter(
                reserva.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La hora de fin debe ser posterior a la hora de inicio.");
        }

        if (reserva.getPrecioTotal() == null
                || reserva.getPrecioTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio total debe ser mayor que cero.");
        }

        if (reserva.getIdUsuario() <= 0) {
            throw new IllegalArgumentException(
                    "No se identificó al usuario que registra la reserva.");
        }
    }

    private void comprobarDisponibilidad(Reserva reserva)
            throws SQLException {

        LocalDateTime inicio = reserva.getFechaInicio();
        LocalDateTime fin = reserva.getFechaFin();

        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            return;
        }

        List<Reserva> reservas = dao.listar();

        for (Reserva existente : reservas) {

            if (existente.getIdCancha() != reserva.getIdCancha()) {
                continue;
            }

            if (Reserva.CANCELADA.equalsIgnoreCase(existente.getEstado())) {
                continue;
            }

            if (reserva.getIdReserva() == existente.getIdReserva()) {
                continue;
            }

            boolean seCruzan
                    = inicio.isBefore(existente.getFechaFin())
                    && fin.isAfter(existente.getFechaInicio());

            if (seCruzan) {
                throw new IllegalArgumentException(
                        "La cancha ya tiene una reserva en ese horario."
                );
            }
        }
    }

    public boolean eliminar(int idReserva) throws SQLException {
        if (idReserva <= 0) {
            throw new IllegalArgumentException(
                    "Seleccione una reserva válida."
            );
        }

        return dao.eliminar(idReserva);
    }
}
