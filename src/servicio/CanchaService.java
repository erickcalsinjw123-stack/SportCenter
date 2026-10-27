package servicio;

import dao.CanchaDAO;
import dao.ICanchaDAO;
import modelo.Cancha;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CanchaService {

    // Límite de DECIMAL(10,2) en la BD
    private static final BigDecimal PRECIO_MAX = new BigDecimal("99999999.99");

    private final ICanchaDAO dao = new CanchaDAO();

    public List<Cancha> listar() throws CanchaException {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new CanchaException("No se pudo cargar la lista de canchas.");
        }
    }

    public void registrar(Cancha c) throws CanchaException {
        validar(c);
        try {
            if (dao.existeNombre(c.getNombre(), 0)) {
                throw new CanchaException("Ya existe una cancha con el nombre \"" + c.getNombre() + "\".");
            }
            dao.insertar(c);
        } catch (SQLException e) {
            throw new CanchaException("Error al registrar la cancha: " + e.getMessage());
        }
    }

    public void modificar(Cancha c) throws CanchaException {
        if (c.getIdCancha() <= 0) {
            throw new CanchaException("Seleccione una cancha para modificar.");
        }
        validar(c);
        try {
            if (dao.existeNombre(c.getNombre(), c.getIdCancha())) {
                throw new CanchaException("Ya existe otra cancha con el nombre \"" + c.getNombre() + "\".");
            }
            dao.actualizar(c);
        } catch (SQLException e) {
            throw new CanchaException("Error al modificar la cancha: " + e.getMessage());
        }
    }

    public void eliminar(int idCancha) throws CanchaException {
        if (idCancha <= 0) {
            throw new CanchaException("Seleccione una cancha para eliminar.");
        }
        try {
            dao.eliminar(idCancha);
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // La cancha ya tiene reservas asociadas (FK desde reserva)
            throw new CanchaException("No se puede eliminar: la cancha tiene reservas registradas.\n"
                    + "Cambie su estado a MANTENIMIENTO en lugar de eliminarla.");
        } catch (SQLException e) {
            throw new CanchaException("Error al eliminar la cancha: " + e.getMessage());
        }
    }

    private void validar(Cancha c) throws CanchaException {
        if (c.getNombre() == null || c.getNombre().isBlank()) {
            throw new CanchaException("Ingrese el nombre de la cancha.");
        }

        if (c.getNombre().length() > 30) {
            throw new CanchaException("El nombre no puede superar los 30 caracteres.");
        }

        if (c.getPrecioHora() == null || c.getPrecioHora().signum() <= 0) {
            throw new CanchaException("El precio por hora debe ser mayor a 0.");
        }

        if (c.getPrecioHora().compareTo(PRECIO_MAX) > 0) {
            throw new CanchaException(
                    "El precio por hora no puede superar S/ " + PRECIO_MAX + "."
            );
        }

        if (!Cancha.DISPONIBLE.equals(c.getEstado())
                && !Cancha.MANTENIMIENTO.equals(c.getEstado())) {
            throw new CanchaException("Estado de cancha no válido.");
        }
    }

}
