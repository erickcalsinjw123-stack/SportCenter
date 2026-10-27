package modelo;

import java.math.BigDecimal;

public class Cancha {

    // Estados permitidos (coinciden con el CHECK de la BD)
    public static final String DISPONIBLE = "DISPONIBLE";
    public static final String MANTENIMIENTO = "MANTENIMIENTO";

    private int idCancha;
    private String nombre;
    private String deporte;
    private BigDecimal precioHora;
    private String estado;

    public Cancha() {
    }

    public Cancha(String nombre, String deporte, BigDecimal precioHora, String estado) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.precioHora = precioHora;
        this.estado = estado;
    }

    public int getIdCancha() {
        return idCancha;
    }

    public void setIdCancha(int idCancha) {
        this.idCancha = idCancha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(BigDecimal precioHora) {
        this.precioHora = precioHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre + " (" + deporte + ")";
    }
}