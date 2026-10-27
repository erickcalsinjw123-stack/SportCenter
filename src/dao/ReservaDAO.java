package dao;

import modelo.Reserva;
import util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO implements IReservaDAO {

    @Override
    public int insertar(Reserva reserva) throws SQLException {
        String sql = """
            INSERT INTO reserva
            (id_cliente, id_cancha, id_usuario, deporte,
             fecha_inicio, fecha_fin, precio_total, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reserva.getIdCliente());
            ps.setInt(2, reserva.getIdCancha());
            ps.setInt(3, reserva.getIdUsuario());
            ps.setString(4, reserva.getDeporte());
            ps.setTimestamp(5, Timestamp.valueOf(reserva.getFechaInicio()));
            ps.setTimestamp(6, Timestamp.valueOf(reserva.getFechaFin()));
            ps.setBigDecimal(7, reserva.getPrecioTotal());
            ps.setString(8, reserva.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener el ID de la reserva.");
    }

    @Override
    public List<Reserva> listar() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();

        String sql = """
            SELECT r.id_reserva,
                   r.id_cliente,
                   c.nombre,
                   c.apellido,
                   c.telefono,
                   c.email,
                   r.id_cancha,
                   ca.nombre AS nombre_cancha,
                   r.id_usuario,
                   r.deporte,
                   r.fecha_inicio,
                   r.fecha_fin,
                   r.precio_total,
                   r.estado
            FROM reserva r
            INNER JOIN cliente c ON r.id_cliente = c.id_cliente
            INNER JOIN cancha ca ON r.id_cancha = ca.id_cancha
            ORDER BY r.fecha_inicio DESC
            """;

        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reserva r = new Reserva();

                r.setIdReserva(rs.getInt("id_reserva"));
                r.setIdCliente(rs.getInt("id_cliente"));
                r.setNombreCliente(rs.getString("nombre"));
                r.setApellidoCliente(rs.getString("apellido"));
                r.setTelefonoCliente(rs.getString("telefono"));
                r.setEmailCliente(rs.getString("email"));
                r.setIdCancha(rs.getInt("id_cancha"));
                r.setNombreCancha(rs.getString("nombre_cancha"));
                r.setIdUsuario(rs.getInt("id_usuario"));
                r.setDeporte(rs.getString("deporte"));

                r.setFechaInicio(
                        rs.getTimestamp("fecha_inicio").toLocalDateTime()
                );

                r.setFechaFin(
                        rs.getTimestamp("fecha_fin").toLocalDateTime()
                );

                r.setPrecioTotal(rs.getBigDecimal("precio_total"));
                r.setEstado(rs.getString("estado"));

                reservas.add(r);
            }
        }

        return reservas;
    }

    @Override
    public boolean actualizar(Reserva reserva) throws SQLException {
        String sql = """
            UPDATE reserva
            SET id_cliente = ?,
                id_cancha = ?,
                id_usuario = ?,
                deporte = ?,
                fecha_inicio = ?,
                fecha_fin = ?,
                precio_total = ?
            WHERE id_reserva = ?
              AND estado = 'CONFIRMADA'
            """;

        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reserva.getIdCliente());
            ps.setInt(2, reserva.getIdCancha());
            ps.setInt(3, reserva.getIdUsuario());
            ps.setString(4, reserva.getDeporte());
            ps.setTimestamp(5, Timestamp.valueOf(reserva.getFechaInicio()));
            ps.setTimestamp(6, Timestamp.valueOf(reserva.getFechaFin()));
            ps.setBigDecimal(7, reserva.getPrecioTotal());
            ps.setInt(8, reserva.getIdReserva());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cancelar(int idReserva) throws SQLException {
        String sql = """
            UPDATE reserva
            SET estado = 'CANCELADA'
            WHERE id_reserva = ?
              AND estado = 'CONFIRMADA'
            """;

        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReserva);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idReserva) throws SQLException {
        String sql = "DELETE FROM reserva WHERE id_reserva = ?";

        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        }
    }

}
