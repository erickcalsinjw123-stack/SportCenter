package dao;

import modelo.Usuario;
import util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario buscarPorNombre(String nombreUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nombre_usuario, contrasena, rol, estado, fecha_creacion "
                + "FROM usuario WHERE nombre_usuario = ?";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombreUsuario(rs.getString("nombre_usuario"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                    u.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    return u;
                }
            }
        }
        return null;
    }

    public boolean existe(String nombreUsuario) throws SQLException {
        return buscarPorNombre(nombreUsuario) != null;
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena, rol, estado) VALUES (?,?,?,?)";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol());
            ps.setString(4, u.getEstado());
            ps.executeUpdate();
        }
    }
}
