package dao;

import util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    public int buscarPorTelefono(String telefono) throws SQLException {
        String sql = """
            SELECT id_cliente
            FROM cliente
            WHERE telefono = ?
            """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, telefono);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_cliente");
                }
            }
        }

        return 0;
    }

    public int insertar(
            String nombre,
            String apellido,
            String telefono,
            String email) throws SQLException {

        String sql = """
            INSERT INTO cliente (nombre, apellido, telefono, email)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, telefono);
            ps.setString(4, email);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener el ID del cliente.");
    }
}
