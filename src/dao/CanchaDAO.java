package dao;

import modelo.Cancha;
import util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CanchaDAO implements ICanchaDAO {

    private static final String COLUMNAS =
            "id_cancha, nombre, precio_hora, estado";

    @Override
    public List<Cancha> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS
                + " FROM cancha ORDER BY id_cancha";

        List<Cancha> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    @Override
    public Cancha buscarPorId(int idCancha) throws SQLException {
        String sql = "SELECT " + COLUMNAS
                + " FROM cancha WHERE id_cancha = ?";

        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCancha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void insertar(Cancha c) throws SQLException {
        String sql = """
                INSERT INTO cancha (nombre, precio_hora, estado)
                VALUES (?, ?, ?)
                """;

        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setBigDecimal(2, c.getPrecioHora());
            ps.setString(3, c.getEstado());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cancha c) throws SQLException {
        String sql = """
                UPDATE cancha
                SET nombre = ?, precio_hora = ?, estado = ?
                WHERE id_cancha = ?
                """;

        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setBigDecimal(2, c.getPrecioHora());
            ps.setString(3, c.getEstado());
            ps.setInt(4, c.getIdCancha());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idCancha) throws SQLException {
        String sql = "DELETE FROM cancha WHERE id_cancha = ?";

        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCancha);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existeNombre(String nombre, int idExcluir)
            throws SQLException {

        String sql = """
                SELECT 1 FROM cancha
                WHERE nombre = ? AND id_cancha <> ?
                """;

        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Cancha mapear(ResultSet rs) throws SQLException {
        Cancha c = new Cancha();

        c.setIdCancha(rs.getInt("id_cancha"));
        c.setNombre(rs.getString("nombre"));
        c.setPrecioHora(rs.getBigDecimal("precio_hora"));
        c.setEstado(rs.getString("estado"));

        return c;
    }
}
